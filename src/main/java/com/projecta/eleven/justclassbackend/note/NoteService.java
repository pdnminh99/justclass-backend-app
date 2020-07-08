package com.projecta.eleven.justclassbackend.note;

import com.google.api.client.util.Maps;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.classroom.*;
import com.projecta.eleven.justclassbackend.file.BasicFile;
import com.projecta.eleven.justclassbackend.file.FileService;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NoteService {

    private final NoteRepository repository;

    private final IClassroomOperationsService classroomService;

    private final IUserOperations userService;

    private final FileService fileService;

    private List<Note> notes;

    private Note note;

    private Member member;

    @Autowired
    public NoteService(NoteRepository repository,
                       IClassroomOperationsService classroomService,
                       IUserOperations userService,
                       FileService fileService) {
        this.repository = repository;
        this.classroomService = classroomService;
        this.userService = userService;
        this.fileService = fileService;
    }

    public void cleanDeletedNotes() throws ExecutionException, InterruptedException {
        var calendar = Calendar.getInstance();
        calendar.setTime(Timestamp.now().toDate());
        calendar.add(Calendar.DATE, -1);

        var oneDayBefore = Timestamp.of(calendar.getTime());
        System.err.println("> Clean up deleted notes before: " + oneDayBefore.toString());

        repository.removeDeletedNotesBefore(oneDayBefore);
    }

    public Stream<Note> get(String classroomId, int pageSize, int pageNumber, Timestamp lastRefresh, boolean excludeDeleted) throws ExecutionException, InterruptedException {
        lastRefresh = Objects.requireNonNullElse(lastRefresh, Timestamp.now());
        if (pageNumber < 0) {
            return Stream.empty();
        }
        notes = repository.get(classroomId, pageSize, pageNumber, lastRefresh, excludeDeleted);
        if (notes.isEmpty()) {
            return Stream.empty();
        }
        // Get attachments.
        for (var note : notes) {
            if (note.getAttachmentReferences() != null) {
                note.getAttachmentReferences()
                        .stream()
                        .map(DocumentReference::getId)
                        .forEach(fileService::addFileQuery);
            }
        }
        fileService.commit();
        notes = notes.stream().peek(note -> {
            if (note.getAttachmentReferences() != null) {
                note.setAttachments(
                        fileService.getFiles()
                                .stream()
                                .peek(f -> {
                                    f.setClassroomId(null);
                                    f.setOwnerId(null);
                                })
                                .filter(f -> note.getAttachmentReferences()
                                        .stream()
                                        .map(DocumentReference::getId)
                                        .anyMatch(m -> f.getFileId().equals(m)))
                                .collect(Collectors.toList()));
            }
        }).collect(Collectors.toList());
        fileService.flush();

        // Get authors.
        Map<String, DocumentReference> memberReferencesMap = Maps.newHashMap();
        for (var note : notes) {
            memberReferencesMap.put(note.getAuthorId(), note.getAuthorReference());
        }

        Map<String, MinifiedMember> memberMap = Maps.newHashMap();
        ApiFutures.allAsList(
                memberReferencesMap
                        .values()
                        .stream()
                        .map(DocumentReference::get)
                        .collect(Collectors.toList()))
                .get()
                .stream()
                .map(MinifiedMember::new)
                .forEach(m -> memberMap.put(m.getLocalId(), m));

        notes = notes.stream()
                .peek(note -> note.setAuthor(memberMap.get(note.getAuthorId())))
                .collect(Collectors.toList());

        return notes.stream()
                .peek(note -> {
                    note.setAttachmentReferences(null);
                    note.setClassroomReference(null);
                    note.setAuthorReference(null);
                    note.setAuthorId(null);
                    note.setClassroomId(null);
                });
    }

    public Optional<Note> create(
            String localId,
            String classroomId,
            String content,
            List<MultipartFile> attachments) throws ExecutionException, InterruptedException, InvalidUserInformationException, IOException {
        if (localId == null || localId.trim().length() == 0 || classroomId == null || classroomId.trim().length() == 0) {
            throw new IllegalArgumentException("LocalId and ClassroomId must not null or empty.");
        }
        if ((content == null || content.trim().length() == 0) && (attachments == null || attachments.size() == 0)) {
            throw new IllegalArgumentException("Invalid note.");
        }
        Member member = classroomService
                .getMember(localId, classroomId)
                .orElseThrow(InvalidUserInformationException::new);
        if (member.getRole() == MemberRoles.STUDENT) {
            var classroom = new Classroom(member.getClassroomReference().get().get());

            if (classroom.getStudentsNotePermission() != NotePermissions.VIEW_COMMENT_POST) {
                throw new IllegalArgumentException("Student with Id [" + localId + "] does not have permission to create a note.");
            }
        }

        DocumentSnapshot authorSnapshot = member.getUserReference().get().get();
        List<BasicFile> files = null;

        if (attachments != null && attachments.size() > 0) {
            fileService.storeAll(attachments, authorSnapshot.getId(), classroomId);
            files = fileService.getFiles()
                    .stream()
                    .peek(f -> {
                        f.setOwnerId(null);
                        f.setClassroomId(null);
                    })
                    .collect(Collectors.toList());
        }

        MinifiedMember author = MinifiedMemberBuilder.newBuilder()
                .fromSnapshot(authorSnapshot)
                .build();

        // TODO only unlock this block when `getNotes` API returns `joinDatetime` and `role` of author.
//                .setJoinDatetime(member.getCreatedTimestamp())
//                .setRole(member.getRole())
//                .build();

        Note note = Note.newBuilder()
                .setNoteId(repository.getNextId())
                .setAuthor(author)
                .setAuthorReference(member.getUserReference())
                .setContent(content)
                .setCreatedAt(Timestamp.now())
                .setCommentsCount(0)
                .setClassroomReference(member.getClassroomReference())
                .setAttachments(files)
                .setAttachmentReferences(fileService.getFilesReferences())
                .build();

        repository.createNote(note);
        repository.commit();
        fileService.flush();

        // Set DocumentReference fields to nulls. Or else exception will throw.
        note.setAttachmentReferences(null);
        note.setAuthorReference(null);
        note.setClassroomReference(null);
        note.setClassroomId(null);

        // No need to send authorId to client. Since `author` field already has this field.
        note.setAuthorId(null);
        return Optional.of(note);
    }

    public void delete(String localId, String noteId) throws ExecutionException, InterruptedException {
        if (localId == null || localId.trim().length() == 0 || noteId == null || noteId.trim().length() == 0) {
            throw new IllegalArgumentException("`localId` or `noteId` is not valid.");
        }
        Note note = repository.get(noteId);
        if (note == null || note.getDeletedAt() != null) {
            repository.flush();
            return;
        }
        Optional<Member> member = classroomService.getMember(localId, note.getClassroomId());
        if (member.isEmpty() || member.get().getRole() != MemberRoles.OWNER && !member.get().getUserId().equals(localId)) {
            repository.flush();
            throw new IllegalAccessError("User with id [" + localId + "] does not have permission to modify note with id [" + noteId + "].");
        }
        // Delete files that are attached to the note.
        if (note.getAttachmentReferences() != null && note.getAttachmentReferences().size() > 0) {
            note.getAttachmentReferences().forEach(a -> fileService.delete(a.getId()));
            fileService.commit();
        }
        // Delete this note.
        repository.delete(note);
        repository.flush();
    }

    public HashMap<String, Object> edit(String localId, String noteId, String content, List<String> deletedAttachments, List<MultipartFile> attachments) throws ExecutionException, InterruptedException, InvalidUserInformationException, IOException {
        Note note = repository.get(noteId);
        if (note == null) {
            throw new IllegalArgumentException("Not with id [" + noteId + "] not found.");
        }
        if (!localId.equals(note.getAuthorId())) {
            throw new InvalidUserInformationException("User with id [" + localId + "] does not have permission to edit this note.");
        }

        // TODO: What if this user is kicked, or drop out of this class. Check whether or not this user is still class.

        if ((content == null || content.equals(note.getContent())) && (deletedAttachments == null || deletedAttachments.size() == 0) && (attachments == null || attachments.size() == 0)) {
            throw new IllegalArgumentException("No changes found.");
        }
        List<DocumentReference> finalAttachments = note.getAttachmentReferences();
        List<BasicFile> finalFiles = null;

        if (finalAttachments != null) {
            finalAttachments.forEach(f -> fileService.addFileQuery(f.getId()));
            fileService.commit();

            finalFiles = fileService.getFiles();
            fileService.flush();

            if (deletedAttachments != null && deletedAttachments.size() > 0) {
                List<String> finalDeletedAttachments = deletedAttachments.stream()
                        .filter(del -> note
                                .getAttachmentReferences()
                                .stream()
                                .map(DocumentReference::getId)
                                .anyMatch(m -> m.equals(del)))
                        .collect(Collectors.toList());
                finalAttachments = note.getAttachmentReferences().stream()
                        .filter(a -> finalDeletedAttachments.stream().noneMatch(d -> a.getId().equals(d)))
                        .collect(Collectors.toList());
                finalFiles = finalFiles.stream()
                        .filter(f -> finalDeletedAttachments.stream().noneMatch(a -> a.equals(f.getFileId())))
                        .collect(Collectors.toList());

                finalDeletedAttachments.forEach(fileService::delete);
            }
        }
        finalFiles = Objects.requireNonNullElse(finalFiles, Lists.newArrayList());
        finalAttachments = Objects.requireNonNullElse(finalAttachments, Lists.newArrayList());

        if (attachments != null && attachments.size() > 0) {
            fileService.storeAll(attachments, localId, note.getClassroomId());
            finalAttachments.addAll(fileService.getFilesReferences());
            finalFiles.addAll(fileService.getFiles());
        } else fileService.commit();
        if (content != null && !content.equals(note.getContent())) {
            note.setContent(content);
        }
        note.setAttachmentReferences(finalAttachments);
        // Update note.
        repository.update(note);
        fileService.flush();

        finalFiles = finalFiles.stream()
                .peek(f -> {
                    f.setClassroomId(null);
                    f.setOwnerId(null);
                })
                .collect(Collectors.toList());
        note.setAttachments(finalFiles);
        note.setAttachmentReferences(null);
        note.setAuthorReference(null);
        note.setClassroomReference(null);
        note.setClassroomId(null);

        // No need to send authorId to client. Since `author` field already has this field.
        note.setAuthorId(null);

        return note.toMap(true);
    }

    public void getNote(String localId, String noteId) throws ExecutionException, InterruptedException {
        note = repository.get(noteId);
        if (note == null) {
            repository.flush();
            throw new IllegalArgumentException("Note with id [" + noteId + "] not found.");
        }
        member = classroomService.getMember(localId, note.getClassroomId())
                .orElseThrow(() -> {
                    String classroomId = note.getClassroomId();
                    note = null;
                    repository.flush();
                    return new IllegalArgumentException("User with id [" + localId + "] not found. Or not part of classroom with id [" + classroomId + "].");
                });
    }

    public List<Comment> getComments(String localId, String noteId) throws ExecutionException, InterruptedException {
        getNote(localId, noteId);
        List<Comment> comments = repository.getComments(noteId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAtByEpoch))
                .collect(Collectors.toList());

        Map<String, MinifiedUser> userMaps = Maps.newHashMap();
        comments.forEach(c -> userMaps.put(c.getAuthorId(), c.getAuthor()));

        List<String> users = new ArrayList<>(userMaps.keySet());
        List<ApiFuture<DocumentSnapshot>> snaps = userService.getUsersReferences(users)
                .map(DocumentReference::get)
                .collect(Collectors.toList());

        ApiFutures.allAsList(snaps)
                .get()
                .stream()
                .map(MinifiedUser::new)
                .forEach(u -> userMaps.put(u.getLocalId(), u));

        for (Comment c : comments) {
            String authorId = c.getAuthorId();
            c.setAuthor(userMaps.get(authorId));
        }

        note = null;
        repository.flush();
        return comments;
    }

    public Comment comment(String localId, String noteId, String content) throws ExecutionException, InterruptedException, InvalidUserInformationException {
        var now = Timestamp.now();
        var comment = new Comment(null,
                noteId,
                null,
                content,
                null,
                now);
        note = repository.get(noteId);

        getNote(localId, noteId);
        assert note != null;

        if (note.getDeletedAt() != null) {
            note = null;
            repository.flush();
            throw new IllegalArgumentException("Note with id [" + noteId + "] is deleted.");
        }

        if (member.getRole() == MemberRoles.STUDENT) {
            var classroom = new Classroom(member.getClassroomReference().get().get());

            if (classroom.getStudentsNotePermission() == NotePermissions.VIEW) {
                note = null;
                repository.flush();
                throw new InvalidUserInformationException("Student with id [" + localId + "] does not have permission to post comment.");
            }
        }
        comment.setAuthor(new MinifiedUser(member.getUserReference().get().get()));
        comment.setClassroomId(note.getClassroomId());

        Integer currentCommentsCount = note.getCommentsCount();
        if (currentCommentsCount != null) {
            currentCommentsCount += 1;
            note.setCommentsCount(currentCommentsCount);
        }
        repository.update(note);
        repository.createComment(comment);
        repository.flush();
        note = null;
        return comment;
    }

    public void deleteComment(String localId, String commentId) throws ExecutionException, InterruptedException, InvalidUserInformationException {
        Comment comment = repository.getComment(commentId);
        if (comment == null) {
            repository.flush();
            throw new IllegalArgumentException("Comment with id [" + commentId + "] not found.");
        }
        note = repository.get(comment.getNoteId());
        assert note != null;
        if (note.getDeletedAt() != null) {
            note = null;
            repository.flush();
            throw new IllegalArgumentException("Comment with id [" + commentId + "] belongs to a deleted note.");
        }
        Member member = classroomService.getMember(localId, comment.getClassroomId())
                .orElseThrow(() -> {
                    String classroomId = note.getClassroomId();
                    note = null;
                    repository.flush();
                    return new IllegalArgumentException("User with id [" + localId + "] not found. Or not part of classroom with id [" + classroomId + "].");
                });
        if (member.getRole() != MemberRoles.OWNER && !localId.equals(comment.getAuthorId())) {
            note = null;
            repository.flush();
            throw new InvalidUserInformationException("User with id [" + localId + "] does not have permission to delete comment with id [" + commentId + "].");
        }
        note = null;
        repository.deleteComment(commentId);
        repository.flush();
    }
}