package com.projecta.eleven.justclassbackend.note;

import com.google.api.client.util.Maps;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.classroom.*;
import com.projecta.eleven.justclassbackend.file.BasicFile;
import com.projecta.eleven.justclassbackend.file.FileService;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
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

    private final FileService fileService;

    private List<Note> notes;

    @Autowired
    public NoteService(NoteRepository repository, IClassroomOperationsService classroomService, FileService fileService) {
        this.repository = repository;
        this.classroomService = classroomService;
        this.fileService = fileService;
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

    // TODO comment post, Check if student have permission to post Comment.
}
