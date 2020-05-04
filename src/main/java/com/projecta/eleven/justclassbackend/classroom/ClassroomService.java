package com.projecta.eleven.justclassbackend.classroom;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.invitation.Invitation;
import com.projecta.eleven.justclassbackend.invitation.InvitationService;
import com.projecta.eleven.justclassbackend.notification.InviteNotification;
import com.projecta.eleven.justclassbackend.notification.NotificationService;
import com.projecta.eleven.justclassbackend.notification.NotificationType;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;

@Service
public class ClassroomService implements IClassroomOperationsService {

    private final IClassroomRepository repository;

    private final IUserOperations userService;

    private final NotificationService notificationService;

    private final InvitationService invitationService;

    @Autowired
    public ClassroomService(IClassroomRepository repository,
                            NotificationService notificationService,
                            InvitationService invitationService,
                            @Qualifier("defaultUserService") IUserOperations userService) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.invitationService = invitationService;
        this.userService = userService;
    }

    @Override
    public Stream<MinifiedClassroom> get(String hostId, MemberRoles role, Timestamp lastRequest)
            throws InvalidUserInformationException, ExecutionException, InterruptedException {
        if (Objects.isNull(hostId) || hostId.trim().length() == 0) {
            throw new InvalidUserInformationException(
                    "LocalId of current logged in user is required to retrieve classrooms",
                    new NullPointerException("LocalId is null."));
        }

        var members = repository.getMembersByUser(hostId, role)
                .map(Member::new)
                .collect(Collectors.toList());

        if (members.isEmpty()) {
            return Stream.empty();
        }

        var classrooms = ApiFutures.allAsList(
                members
                        .parallelStream()
                        .map(Member::getClassroomReference)
                        .map(DocumentReference::get)
                        .collect(Collectors.toList()))
                .get()
                .parallelStream()
                .map(MinifiedClassroom::new)
                .collect(Collectors.toList());

        if (lastRequest != null) {
            var index = 0;

            while (index < classrooms.size()) {
                if (classrooms.get(index).getLastEdit().compareTo(lastRequest) < 0) {
                    classrooms.remove(index);
                    members.remove(index);
                    continue;
                }
                index++;
            }
        }

        // TODO what if you don't find any owner of a classroom?
        var ownersReferences = ApiFutures.allAsList(
                members
                        .parallelStream()
                        .map(Member::getClassroomId)
                        .map(c -> repository.getMembers(c, MemberRoles.OWNER))
                        .collect(Collectors.toList()))
                .get()
                .parallelStream()
                .map(QuerySnapshot::getDocuments)
                .map(c -> c.get(0))
                .map(Member::new)
                .map(Member::getUserReference)
                .map(DocumentReference::get)
                .collect(Collectors.toList());

        var owners = ApiFutures.allAsList(ownersReferences)
                .get()
                .parallelStream()
                .map(MinifiedUser::new)
                .collect(Collectors.toList());

        var studentsCount = getMembersCount(
                members.parallelStream().map(Member::getClassroomId), MemberRoles.STUDENT);

        var collaboratorsCount = getMembersCount(
                members.parallelStream().map(Member::getClassroomId), MemberRoles.COLLABORATOR);

        for (int index = 0; index < classrooms.size(); index++) {
            var currentClassroom = classrooms.get(index);
            currentClassroom.setRole(members.get(index).getRole());
            currentClassroom.setLastAccess(members.get(index).getLastAccess());
            currentClassroom.setOwner(owners.get(index));
            currentClassroom.setCollaboratorsCount(collaboratorsCount.get(index));
            currentClassroom.setStudentsCount(studentsCount.get(index));
            classrooms.set(index, currentClassroom);
        }
        return classrooms.stream();
    }

    private List<Integer> getMembersCount(Stream<String> classroomIdStream, MemberRoles role)
            throws ExecutionException, InterruptedException {
        return ApiFutures.allAsList(
                classroomIdStream
                        .map(id -> repository.getMembers(id, role))
                        .collect(Collectors.toList()))
                .get()
                .parallelStream()
                .map(QuerySnapshot::size)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Classroom> get(String localId, String classroomId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateRetrieveOrDeleteRequestInput(localId, classroomId);
        DocumentSnapshot memberSnapshot = repository.getMember(classroomId, localId)
                .get()
                .get();
        if (!memberSnapshot.exists()) {
            throw new InvalidClassroomInformationException("Classroom does not exist, or user does not have permission to perform `GET` task.");
        }
        var now = Timestamp.now();
        var member = new Member(memberSnapshot);
        var classroom = new Classroom(member
                .getClassroomReference()
                .get()
                .get());
        getMembersMetadataForClassroom(classroom, member);
        classroom.setLastAccess(now);
        classroom.setRole(member.getRole());

        // Update collaborators info.
        var memberUpdateMap = new HashMap<String, Object>();
        memberUpdateMap.put("lastAccess", now);
        memberSnapshot.getReference()
                .update(memberUpdateMap);

        return Optional.of(classroom);
    }

    @Override
    public Optional<Classroom> create(ClassroomRequestBody classroomRequestBody, String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateCreateRequestInput(classroomRequestBody, localId);
        DocumentReference userReference = verifyUserExistenceOrElseThrow(localId);

        var now = Timestamp.now();
        var classroom = classroomRequestBody.toClassroom(
                now, NotePermissions.VIEW_COMMENT_POST, generateNewPublicCode());
        classroom.setLastEdit(now);

        DocumentReference classroomReference = createClassroomDocument(classroom);
        createMemberDocument(classroomReference, userReference, now);

        classroom.setClassroomId(classroomReference.getId());
        classroom.setRole(MemberRoles.OWNER);
        classroom.setLastAccess(now);
        return Optional.of(classroom);
    }

    private String generateNewPublicCode() throws ExecutionException, InterruptedException {
        var isCorrect = false;
        var crc = new CRC32();
        String newPublicCode = "";
        long currentMillis;

        while (!isCorrect) {
            currentMillis = System.currentTimeMillis();
            crc.update(longToBytes(currentMillis));
            newPublicCode = Long.toHexString(crc.getValue());
            isCorrect = !repository.isPublicCodeAlreadyExist(newPublicCode);
        }

        return newPublicCode;
    }

    private byte[] longToBytes(long x) {
        var buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private DocumentReference verifyUserExistenceOrElseThrow(String localId) throws InvalidUserInformationException, ExecutionException, InterruptedException {
        DocumentReference ref = userService.getUserReference(localId);

        if (!ref.get().get().exists()) {
            throw new InvalidUserInformationException("User with localID [" + localId + "] does not exist.");
        }
        return ref;
    }

    private void createMemberDocument(DocumentReference classroomReference, DocumentReference userReference, Timestamp createdTimestamp) throws ExecutionException, InterruptedException {
        var member = new Member(
                classroomReference.getId() + "_" + userReference.getId(),
                classroomReference,
                userReference,
                createdTimestamp,
                createdTimestamp,
                MemberRoles.OWNER);

        repository.createMember(member);
    }

    private DocumentReference createClassroomDocument(Classroom classroom) throws InterruptedException, ExecutionException, InvalidClassroomInformationException {
        // Since ClassroomRequestBody constructor also include these four fields,
        // we must exclude them to prevent side effects.
        classroom.setClassroomId(null);
        classroom.setRole(null);
        classroom.setLastAccess(null);

        return repository.createClassroom(classroom);
    }

    private void validateCreateRequestInput(ClassroomRequestBody classroomRequestBody, String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException {
        if (Objects.isNull(localId)) {
            throw new InvalidUserInformationException(
                    "LocalId of current logged in user is required to create new classroomRequestBody",
                    new NullPointerException("LocalId is null."));
        }
        if (Objects.isNull(classroomRequestBody)) {
            throw new InvalidClassroomInformationException(
                    "Classroom should have at least theme or title field.",
                    new NullPointerException("classroomRequestBody title or theme is missing."));
        }
        var title = classroomRequestBody.getTitle();
        if (Objects.isNull(title) || title.trim().length() == 0) {
            throw new InvalidClassroomInformationException("Classroom title must not null or empty.");
        }
        if (Objects.isNull(classroomRequestBody.getTheme())) {
            throw new InvalidClassroomInformationException(
                    "Classroom theme for " + title + " must not null.",
                    new NullPointerException("Theme for classroomRequestBody " + title + " is null."));
        }
    }

    @Override
    public Optional<Classroom> update(Classroom classroom, String localId, Boolean requestNewPublicCode)
            throws InvalidClassroomInformationException, InvalidUserInformationException, ExecutionException, InterruptedException {
        validateClassroomUpdateRequestInput(classroom, localId);
        var classroomId = classroom.getClassroomId();
        DocumentSnapshot memberSnapshot = repository
                .getMember(classroomId, localId)
                .get()
                .get();

        if (!memberSnapshot.exists()) {
            throw new IllegalArgumentException("User " + localId + " is not part of classroom [" + classroomId + "]. Or this classroom does not exist.");
        }
        var member = new Member(memberSnapshot);

        // Only OWNER have permissions to edit class.
        if (Objects.isNull(member.getRole()) || member.getRole() != MemberRoles.OWNER) {
            throw new IllegalArgumentException("User " + localId + " does not have permission to edit classroom [" + classroomId + "] info.");
        }

        var originalClassroomSnapshot = repository.getClassroom(classroomId)
                .get()
                .get();
        if (!originalClassroomSnapshot.exists()) {
            throw new InvalidClassroomInformationException("Classroom [" + classroomId + "] does not exist. Use POST method if you want to create new classroom.");
        }

        var originalClassroom = new Classroom(originalClassroomSnapshot);
        boolean shouldUpdateLastEdit = shouldUpdateLastAccess(classroom, originalClassroom);

        if (containChangesAfterCompareAndApplyUpdates(originalClassroom, classroom, requestNewPublicCode)) {
            var classroomMap = classroom.toMap();
            classroomMap.remove("classroomId");
            if (requestNewPublicCode != null && !requestNewPublicCode) {
                classroomMap.put("publicCode", null);
            }
            // No need to exclude `publicCode` and `createdTimestamp` from `classroomMap`, since these fields are marked as @JsonIgnore.
            repository.updateClassroom(classroomMap, classroomId);
        }

        var now = Timestamp.now();
        // Update `lastEdit` of collaborators.
        if (shouldUpdateLastEdit) {
            var classroomUpdateMap = new HashMap<String, Object>();
            classroomUpdateMap.put("lastEdit", now);
            member.getClassroomReference().update(classroomUpdateMap);
        }
        // Update `lastAccess` of owner.
        var memberUpdateMap = new HashMap<String, Object>();
        memberUpdateMap.put("lastAccess", now);
        memberSnapshot.getReference()
                .update(memberUpdateMap);

        originalClassroom.setRole(member.getRole());
        originalClassroom.setLastAccess(now);
        originalClassroom.setLastEdit(now);
        return Optional.of(originalClassroom);
    }

    private void validateClassroomUpdateRequestInput(Classroom classroom, String localId) throws InvalidClassroomInformationException, InvalidUserInformationException {
        if (Objects.isNull(classroom) || Objects.isNull(classroom.getClassroomId()) || classroom.getClassroomId().trim().length() == 0) {
            throw new InvalidClassroomInformationException("ClassroomId must be included to execute the edit task.",
                    new NullPointerException("Classroom or classroomId is null."));
        }
        if (classroom.getTitle() != null && classroom.getTitle().trim().length() == 0) {
            throw new InvalidClassroomInformationException("Cannot update classroom title of empty.");
        }
        if (Objects.isNull(localId) || localId.trim().length() == 0) {
            throw new InvalidUserInformationException("LocalId of current logged in user must include to execute the edit task.",
                    new NullPointerException("LocalId is null or empty."));
        }
    }

    private boolean shouldUpdateLastAccess(Classroom oldVersion, Classroom newVersion) {
        return newVersion.getTitle() != null && newVersion.getTitle().trim().length() != 0 && !newVersion.getTitle().equals(oldVersion.getTitle()) ||
                newVersion.getSubject() != null && !newVersion.getSubject().equals(oldVersion.getSubject()) ||
                newVersion.getTheme() != null && !newVersion.getTheme().equals(oldVersion.getTheme());
    }

    private boolean containChangesAfterCompareAndApplyUpdates(Classroom oldVersion, Classroom newVersion, Boolean requireNewPublicCode) throws ExecutionException, InterruptedException {
        var containChanges = false;

        if (newVersion.getTitle() != null && newVersion.getTitle().trim().length() != 0 && !newVersion.getTitle().equals(oldVersion.getTitle())) {
            oldVersion.setTitle(newVersion.getTitle());
            containChanges = true;
        }
        if (newVersion.getStudentsNotePermission() != null && newVersion.getStudentsNotePermission() != oldVersion.getStudentsNotePermission()) {
            oldVersion.setStudentsNotePermission(newVersion.getStudentsNotePermission());
            containChanges = true;
        }
        if (newVersion.getDescription() != null && !newVersion.getDescription().equals(oldVersion.getDescription())) {
            oldVersion.setDescription(newVersion.getDescription());
            containChanges = true;
        }
        if (newVersion.getRoom() != null && !newVersion.getRoom().equals(oldVersion.getRoom())) {
            oldVersion.setRoom(newVersion.getRoom());
            containChanges = true;
        }
        if (newVersion.getSection() != null && !newVersion.getSection().equals(oldVersion.getSection())) {
            oldVersion.setSection(newVersion.getSection());
            containChanges = true;
        }
        if (newVersion.getSubject() != null && !newVersion.getSubject().equals(oldVersion.getSubject())) {
            oldVersion.setSubject(newVersion.getSubject());
            containChanges = true;
        }
        if (newVersion.getTheme() != null && !newVersion.getTheme().equals(oldVersion.getTheme())) {
            oldVersion.setTheme(newVersion.getTheme());
            containChanges = true;
        }
        if (requireNewPublicCode != null) {
            if (requireNewPublicCode) {
                var newPublicCode = generateNewPublicCode();
                newVersion.setPublicCode(newPublicCode);
                oldVersion.setPublicCode(newPublicCode);
            } else {
                newVersion.setPublicCode(null);
                oldVersion.setPublicCode(null);
            }
            containChanges = true;
        }
        return containChanges;
    }

    @Override
    public Optional<Boolean> delete(String localId, String classroomId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateRetrieveOrDeleteRequestInput(localId, classroomId);
        // Check if user has permission to perform delete task.
        var memberSnapshot = repository.getMember(classroomId, localId)
                .get()
                .get();
        if (!memberSnapshot.exists()) {
            throw new InvalidClassroomInformationException("Classroom does not exist, or user does not have permission to perform `DELETE` task.");
        }
        var member = new Member(memberSnapshot);
        if (member.getRole() != MemberRoles.OWNER) {
            throw new InvalidUserInformationException("User with Id [" + localId + "] does not have permission to delete classroom [" + classroomId + "].");
        }
        // No need to check classroomReference for
        var collaboratorsByClassroom = repository.getMembersByClassroom(classroomId)
                .map(DocumentReference::delete)
                .collect(Collectors.toList());

        collaboratorsByClassroom.add(
                Objects.requireNonNull(memberSnapshot
                        .get("classroomReference", DocumentReference.class))
                        .delete());
        ApiFutures.allAsList(collaboratorsByClassroom);
        return Optional.of(true);
    }

    @Override
    public Stream<MinifiedMember> invite(
            String localId,
            String classroomId,
            List<Invitation> invitations)
            throws ExecutionException, InterruptedException, InvalidClassroomInformationException, InvalidUserInformationException {
        if (invitations.size() == 0) {
            return Stream.empty();
        }
        // check if those invitations are valid.
        // check if those invitations are from users that already in the class.
        // check if invitations are sent.
        DocumentReference classroomReference = repository.getClassroom(classroomId);
        if (!classroomReference.get().get().exists()) {
            throw new InvalidClassroomInformationException("Classroom with ID " + classroomId + " does not exist.");
        }
        Member memberInstance = verifyMember(classroomId, localId);
        DocumentSnapshot currentOwnerSnapshot = memberInstance.getUserReference().get().get();

        var now = Timestamp.now();

        // PROMOTE OWNER
        List<Invitation> validInvitations = invitations
                .stream()
                .filter(this::isValidInvitation)
                .filter(i -> i.getLocalId() == null || !i.getLocalId().equals(localId))
                .filter(i -> i.getEmail() == null || !i.getEmail().equals(currentOwnerSnapshot.getString("email")))
                .collect(Collectors.toList());
        Optional<Invitation> ownerMember = validInvitations
                .stream()
                .filter(i -> i.getRole() == MemberRoles.OWNER)
                .findFirst();
        var newMembers = new ArrayList<MinifiedMember>();

        if (ownerMember.isPresent() && memberInstance.getRole() == MemberRoles.OWNER) {
            var newOwner = promoteOwner(
                    memberInstance,
                    ownerMember.get().getLocalId(), classroomId);
            if (newOwner != null) {
                newMembers.add(newOwner);
            }
        }

        // SENT OTHERS INVITATIONS
        validInvitations = validInvitations
                .stream()
                .filter(invitation -> invitation.getRole() != MemberRoles.OWNER)
                .peek(invitation -> {
                    invitation.setClassroomId(classroomId);
                    invitation.setClassroomReference(classroomReference);
                    invitation.setInvitorLocalId(localId);
                    invitation.setInvitorReference(memberInstance.getUserReference());
                    invitation.setInvokeTime(now);
                })
                .collect(Collectors.toList());
        if (validInvitations.size() == 0) {
            return newMembers.stream();
        }

        List<InvitationWrapper> inviteesAlreadyInClass = new ArrayList<>();
        List<InvitationWrapper> inviteesNotInClass = new ArrayList<>();

        // Invite using ID
        List<Invitation> inviteesById = validInvitations
                .stream()
                .filter(i -> i.getLocalId() != null)
                .collect(Collectors.toList());

        if (inviteesById.size() > 0) {
            List<DocumentSnapshot> inviteeByIdSnapshots = ApiFutures
                    .allAsList(
                            inviteesById
                                    .stream()
                                    .map(i -> repository.getMember(i.getClassroomId(), i.getLocalId()))
                                    .map(DocumentReference::get)
                                    .collect(Collectors.toList())
                    )
                    .get();

            List<DocumentSnapshot> usersByIdSnapshot = ApiFutures
                    .allAsList(
                            inviteesById
                                    .stream()
                                    .map(Invitation::getLocalId)
                                    .map(userService::getUserReference)
                                    .map(DocumentReference::get)
                                    .collect(Collectors.toList())
                    )
                    .get();

            for (var index = 0; index < inviteeByIdSnapshots.size(); index++) {
                InvitationWrapper wrapper;
                DocumentSnapshot memberSnapshot = inviteeByIdSnapshots.get(index);
                Invitation invitation = inviteesById.get(index);
                DocumentSnapshot userSnapshot = usersByIdSnapshot.get(index);

                if (memberSnapshot.exists()) {
                    var role = MemberRoles.fromText(memberSnapshot.getString("role"));
                    wrapper = new InvitationWrapper(memberSnapshot, userSnapshot, invitation);
                    if (role != invitation.getRole()) {
                        inviteesAlreadyInClass.add(wrapper);
                    }
                } else if (userSnapshot.exists()) {
                    wrapper = new InvitationWrapper(null, userSnapshot, invitation);
                    inviteesNotInClass.add(wrapper);
                }
            }
        }

        // Invite using email.
        var inviteesByEmail = validInvitations
                .stream()
                .filter(i -> i.getLocalId() == null)
                .collect(Collectors.toList());

        if (inviteesByEmail.size() > 0) {
            List<QueryDocumentSnapshot> inviteeByEmailSnapshots = userService
                    .getUsersByEmail(
                            inviteesByEmail
                                    .stream()
                                    .map(Invitation::getEmail)
                                    .collect(Collectors.toList())
                    )
                    .collect(Collectors.toList());

            List<DocumentSnapshot> inviteeMemberSnapshots = ApiFutures.allAsList(
                    inviteeByEmailSnapshots
                            .stream()
                            .map(DocumentSnapshot::getId)
                            .map(id -> repository.getMember(classroomId, id))
                            .map(DocumentReference::get)
                            .collect(Collectors.toList())
            )
                    .get();

            for (var index = 0; index < inviteeByEmailSnapshots.size(); index++) {
                InvitationWrapper wrapper;
                // Member snapshot may not exist.
                DocumentSnapshot memberSnapshot = inviteeMemberSnapshots.get(index);
                // Assume this snapshot is exist.
                DocumentSnapshot userSnapshot = inviteeByEmailSnapshots.get(index);
                Invitation associateInvitation = inviteesByEmail
                        .stream()
                        .filter(i -> {
                            assert i.getEmail() != null;
                            return i.getEmail().equals(userSnapshot.getString("email"));
                        })
                        .findAny()
                        .get();

                if (userSnapshot.getId().equals(localId)) {
                    continue;
                }
                if (memberSnapshot.exists()) {
                    wrapper = new InvitationWrapper(
                            memberSnapshot,
                            userSnapshot,
                            associateInvitation
                    );
                    var role = MemberRoles.fromText(memberSnapshot.getString("role"));
                    if (role != associateInvitation.getRole()
                            && isInvitationNotYetExisted(wrapper, inviteesAlreadyInClass)) {
                        inviteesAlreadyInClass.add(wrapper);
                    }
                } else {
                    wrapper = new InvitationWrapper(
                            null,
                            userSnapshot,
                            associateInvitation
                    );
                    if (isInvitationNotYetExisted(wrapper, inviteesNotInClass)) {
                        inviteesNotInClass.add(wrapper);
                    }
                }
            }
        }

        // TODO considering whether COLLABORATORS have permission to change users'roles.
        for (var invitation : inviteesAlreadyInClass) {
            Invitation finalInvitation = invitation.invitation;
            MinifiedMember user = MinifiedMember.toMinifiedMember(
                    invitation.user,
                    invitation.invitation.getRole(),
                    invitation.member.getCreatedTimestamp());

            // Save notifications.
            var notification = new InviteNotification(
                    null,
                    now,
                    localId,
                    memberInstance.getUserReference(),
                    finalInvitation.getLocalId(),
                    invitation.userSnapshot.getReference(),
                    finalInvitation.getClassroomId(),
                    finalInvitation.getClassroomReference(),
                    NotificationType.ROLE_CHANGE,
                    finalInvitation.getRole()
            );
            notificationService.add(notification);
            newMembers.add(user);
        }

        for (var invitation : inviteesNotInClass) {
            Invitation finalInvitation = invitation.invitation;
            MinifiedMember user = MinifiedMember.toMinifiedMember(
                    invitation.user,
                    invitation.invitation.getRole(),
                    now);

            // Save final invitations.
            if (finalInvitation.getEmail() == null) {
                finalInvitation.setEmail(invitation.userSnapshot.getString("email"));
            }
            finalInvitation.setInvitationId(classroomId + "_" + finalInvitation.getLocalId());
            invitationService.addInvitation(finalInvitation);

            // Save notifications.
            var notification = new InviteNotification(
                    null,
                    now,
                    localId,
                    memberInstance.getUserReference(),
                    finalInvitation.getLocalId(),
                    invitation.userSnapshot.getReference(),
                    finalInvitation.getClassroomId(),
                    finalInvitation.getClassroomReference(),
                    NotificationType.INVITATION,
                    finalInvitation.getRole()
            );
            notificationService.add(notification);
            newMembers.add(user);
        }
        invitationService.send();
        notificationService.send();
        return newMembers.stream();
    }

    private boolean isInvitationNotYetExisted(InvitationWrapper currentInvitation, List<InvitationWrapper> invitations) {
        boolean alreadyExist = false;
        for (var invite : invitations) {
            if (invite.user.getLocalId().equals(currentInvitation.user.getLocalId())) {
                alreadyExist = true;
                break;
            }
        }
        return !alreadyExist;
    }

    private static class InvitationWrapper {
        DocumentSnapshot memberSnapshot;

        DocumentSnapshot userSnapshot;

        MinifiedUser user;

        Invitation invitation;

        Member member;

        public InvitationWrapper(
                DocumentSnapshot memberSnapshot,
                DocumentSnapshot userSnapshot,
                Invitation invitation) {
            this.userSnapshot = userSnapshot;
            this.memberSnapshot = memberSnapshot;
            this.invitation = invitation;
            this.member = memberSnapshot != null && memberSnapshot.exists() ?
                    new Member(memberSnapshot) :
                    null;
            this.user = userSnapshot != null && userSnapshot.exists() ?
                    new MinifiedUser(userSnapshot) :
                    null;
        }
    }

    private Member verifyMember(String classroomId, String localId) throws ExecutionException, InterruptedException, InvalidUserInformationException {
        DocumentReference memberReference = repository.getMember(classroomId, localId);
        DocumentSnapshot memberSnapshot = memberReference.get().get();

        if (!memberSnapshot.exists()) {
            throw new InvalidUserInformationException("User with ID " + localId + " does not exist, or not part of classroom with ID " + classroomId + ".");
        }
        var memberInstance = new Member(memberSnapshot);

        if (memberInstance.getRole() == MemberRoles.STUDENT) {
            throw new InvalidUserInformationException("User with ID " + localId + " does not have permission to invite.");
        }
        return memberInstance;
    }

    private MinifiedMember promoteOwner(Member currentOwner, String newOwnerId, String classroomId) throws ExecutionException, InterruptedException, InvalidClassroomInformationException, InvalidUserInformationException {
        var newOwnerSnapshot = repository.getMember(classroomId, newOwnerId)
                .get()
                .get();

        if (!newOwnerSnapshot.exists()) {
            // or throw exception?
            return null;
        }
        var newOwnerInstance = new Member(newOwnerSnapshot);
        return repository.promoteOwner(currentOwner, newOwnerInstance, Timestamp.now());
    }

    private ApiFuture<WriteResult> updateLastEdit(DocumentReference classroomReference, Timestamp now) {
        var map = new HashMap<String, Object>();
        map.put("lastEdit", now);
        return classroomReference.update(map);
    }

    private boolean isValidInvitation(Invitation invitation) {
        var localId = invitation.getLocalId();
        var email = invitation.getEmail();
        var role = invitation.getRole();

        var checkRoleNotNull = role != null;
        var checkValidNormalInvitation = localId != null && localId.trim().length() != 0 || email != null && email.trim().length() != 0;
        var checkValidInfoForOwner = role != MemberRoles.OWNER || localId != null && localId.trim().length() != 0;

        return checkRoleNotNull && checkValidNormalInvitation && checkValidInfoForOwner;
    }

    @Override
    public Optional<Classroom> join(String localId, String publicCode) throws ExecutionException, InterruptedException, InvalidUserInformationException {
//        if (localId == null || localId.trim().length() == 0 || publicCode == null || publicCode.trim().length() == 0) {
//            throw new IllegalArgumentException("LocalId is null or empty; Or public code is null or empty.");
//        }
        DocumentReference userRef = userService.getUserReference(localId);
        if (!userRef.get().get().exists()) {
            throw new InvalidUserInformationException("UserId does not exist");
        }
        var now = Timestamp.now();
        var optionalClassroom = repository.getClassroomByPublicCode(publicCode);
        if (optionalClassroom.isEmpty()) {
            return Optional.empty();
        }
        var classroomRef = optionalClassroom.get();

        DocumentSnapshot oldMemberSnapshot = repository.getMember(classroomRef.getId(), userRef.getId())
                .get()
                .get();

        if (oldMemberSnapshot.exists()) {
            var oldMemberInstance = new Member(oldMemberSnapshot);
            var classroomInstance = new Classroom(classroomRef.get().get());
            var updateMap = new HashMap<String, Object>();

            classroomInstance.setRole(oldMemberInstance.getRole());
            classroomInstance.setLastAccess(oldMemberInstance.getLastAccess());

            updateMap.put("lastAccess", now);
            oldMemberSnapshot.getReference()
                    .update(updateMap);
            getMembersMetadataForClassroom(classroomInstance, oldMemberInstance);
            return Optional.of(classroomInstance);
        }

        // Found no new classroom, attempt to create new Member.
        var member = new Member(
                classroomRef.getId() + "_" + userRef.getId(),
                classroomRef,
                userRef,
                now,
                now,
                MemberRoles.STUDENT
        );
        repository.createMember(member);

        var classroomUpdateMap = new HashMap<String, Object>();
        classroomUpdateMap.put("lastEdit", now);
        classroomRef.update(classroomUpdateMap);

        var classroomInstance = new Classroom(classroomRef.get().get());
        classroomInstance.setRole(MemberRoles.STUDENT);
        classroomInstance.setLastAccess(now);
        classroomInstance.setLastEdit(now);
        getMembersMetadataForClassroom(classroomInstance, member);

        return Optional.of(classroomInstance);
    }

    private void getMembersMetadataForClassroom(Classroom classroom, Member member) throws ExecutionException, InterruptedException {
        var queries = Lists.newArrayList(
                repository.getMembers(classroom.getClassroomId(), MemberRoles.COLLABORATOR),
                repository.getMembers(classroom.getClassroomId(), MemberRoles.STUDENT)
        );
        if (member.getRole() == MemberRoles.OWNER) {
            var owner = new MinifiedUser(member.getUserReference().get().get());
            classroom.setOwner(owner);
        } else {
            queries.add(repository.getMembers(classroom.getClassroomId(), MemberRoles.OWNER));
        }
        var querySnapshots = ApiFutures.allAsList(queries)
                .get();

        // Parse queries'result.
        classroom.setCollaboratorsCount(querySnapshots.get(0).size());
        classroom.setStudentsCount(querySnapshots.get(1).size());
        if (querySnapshots.size() == 3) {
            var ownerInstance = new Member(querySnapshots.get(2).getDocuments().get(0));
            var owner = new MinifiedUser(ownerInstance.getUserReference().get().get());
            classroom.setOwner(owner);
        }
    }

    private void validateRetrieveOrDeleteRequestInput(String localId, String classroomId)
            throws InvalidUserInformationException, InvalidClassroomInformationException {
        if (Objects.isNull(localId) ||
                localId.trim().length() == 0) {
            throw new InvalidUserInformationException("LocalId must be included to perform this task.");
        }
        if (Objects.isNull(classroomId) ||
                classroomId.trim().length() == 0) {
            throw new InvalidClassroomInformationException("ClassroomId must be included to perform this task.");
        }
    }
}
