package com.projecta.eleven.justclassbackend.classroom;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Repository
class ClassroomRepository implements IClassroomRepository {

    private final Firestore firestore;

    private WriteBatch writeBatch;

    private final CollectionReference classroomsCollection;

    private final CollectionReference membersCollection;

    @Autowired
    ClassroomRepository(
            Firestore firestore,
            @Qualifier("classroomsCollection") CollectionReference classroomsCollection,
            @Qualifier("membersCollection") CollectionReference membersCollection) {
        this.firestore = firestore;
        writeBatch = firestore.batch();
        this.classroomsCollection = classroomsCollection;
        this.membersCollection = membersCollection;
    }

    @Override
    public DocumentReference getClassroom(String classroomId) {
        return classroomsCollection.document(classroomId);
    }

    @Override
    public Optional<DocumentReference> getClassroomByPublicCode(String publicCode) throws ExecutionException, InterruptedException {
        return classroomsCollection.whereEqualTo("publicCode", publicCode)
                .get()
                .get()
                .getDocuments()
                .stream()
                .findFirst()
                .map(DocumentSnapshot::getReference);
    }

    @Override
    public DocumentReference createClassroom(Classroom classroom)
            throws ExecutionException, InterruptedException {
        return classroomsCollection
                .add(classroom.toMap())
                .get();
    }

    @Override
    public DocumentReference updateClassroom(Map<String, Object> classroom, String classroomId) {
        var classroomReference = classroomsCollection.document(classroomId);
        classroomReference.update(classroom);
        return classroomReference;
    }

    @Override
    public DocumentReference createMember(Member member) {
        var reference = membersCollection
                .document(member.getMemberId());
        member.setMemberId(null);
        reference.set(member.toMap());
        return reference;
    }

    @Override
    public DocumentReference getMember(String classroomId, String localId) {
        return membersCollection.document(classroomId + "_" + localId);
    }

    @Override
    public ApiFuture<QuerySnapshot> getMembers(String classroomId, MemberRoles role) {
        if (classroomId == null) {
            return null;
        }
        return role == null ?
                membersCollection.whereEqualTo("classroomId", classroomId)
                        .get() :
                membersCollection.whereEqualTo("classroomId", classroomId)
                        .whereEqualTo("role", role.toString())
                        .get();
    }

    @Override
    public Stream<DocumentReference> getMembersByClassroom(String classroomId)
            throws ExecutionException, InterruptedException {
        return membersCollection
                .whereEqualTo("classroomId", classroomId)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference);
    }

    @Override
    public Stream<QueryDocumentSnapshot> getMembersByUser(String hostId, MemberRoles role)
            throws ExecutionException, InterruptedException {
        if (Objects.isNull(hostId) || hostId.trim().length() == 0) {
            return Stream.empty();
        }
        if (Objects.isNull(role)) {
            return membersCollection.whereEqualTo("userId", hostId)
                    .orderBy("lastAccess", Query.Direction.DESCENDING)
                    .get()
                    .get()
                    .getDocuments()
                    .stream();
        }
        return membersCollection.whereEqualTo("userId", hostId)
                .whereEqualTo("role", role.toString())
                .orderBy("lastAccess", Query.Direction.DESCENDING)
                .get()
                .get()
                .getDocuments()
                .stream();
    }

    @Override
    public boolean isPublicCodeAlreadyExist(String publicCode) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshots = classroomsCollection.whereEqualTo("publicCode", publicCode)
                .get()
                .get();
        if (snapshots.size() == 0) {
            return false;
        }
        // TODO save logs here
        System.err.println("Found classrooms with duplicate public code: " + publicCode);
        snapshots.getDocuments()
                .stream()
                .map(Classroom::new)
                .forEach(System.out::println);
        return true;
    }

    public MinifiedMember promoteOwner(Member originalOwner, Member newOwner, Timestamp now) throws ExecutionException, InterruptedException {
        return firestore.runTransaction(transaction -> {
            var originalOwnerRef = getMember(originalOwner.getClassroomId(), originalOwner.getUserId());
            var newOwnerRef = getMember(newOwner.getClassroomId(), newOwner.getUserId());
            var updateMap = new HashMap<String, Object>();

            updateMap.put("lastAccess", now);
            updateMap.put("role", MemberRoles.COLLABORATOR.toString());
            transaction.update(originalOwnerRef, updateMap);

            updateMap.put("role", MemberRoles.OWNER.toString());
            transaction.update(newOwnerRef, updateMap);

            var newOwnerMember = new MinifiedMember(
                    newOwner.getUserReference().get().get()
            );
            newOwnerMember.setJoinDatetime(newOwner.getCreatedTimestamp());
            newOwnerMember.setRole(MemberRoles.OWNER);
            return newOwnerMember;
        })
                .get();
    }

    // NEW CODE HERE
    public boolean isBatchActive() {
        return writeBatch != null;
    }

    private boolean verifyMemberInput(Member member) {
        if (member == null) {
            return false;
        }
        if (!isBatchActive()) {
            writeBatch = firestore.batch();
        }
        String memberId = member.getMemberId();
        return memberId != null && memberId.trim().length() != 0;
    }

    @Override
    public void createMemberAsync(Member member) {
        verifyMemberInput(member);
        String memberId = member.getMemberId();
        HashMap<String, Object> map = member.toMap();
        map.remove("memberId");

        writeBatch.set(
                membersCollection.document(memberId),
                map);
    }

    @Override
    public void updateMember(Member member) {
        verifyMemberInput(member);
        String memberId = member.getMemberId();
        var memberMap = member.toMap();
        memberMap.remove("memberId");

        writeBatch.update(
                membersCollection.document(memberId),
                member.toMap()
        );
    }

    @Override
    public void commitAsync() {
        if (isBatchActive()) {
            writeBatch.commit();
            writeBatch = null;
        }
    }

    @Override
    public void commitSync() throws ExecutionException, InterruptedException {
        if (isBatchActive()) {
            writeBatch.commit()
                    .get();
            writeBatch = null;
        }
    }
}
