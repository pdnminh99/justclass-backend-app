package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.classroom.*;
import com.projecta.eleven.justclassbackend.file.BasicFile;
import com.projecta.eleven.justclassbackend.file.FileService;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NoteService {

    private final NoteRepository repository;

    private final IClassroomOperationsService classroomService;

    private final FileService fileService;

    @Autowired
    public NoteService(NoteRepository repository, IClassroomOperationsService classroomService, FileService fileService) {
        this.repository = repository;
        this.classroomService = classroomService;
        this.fileService = fileService;
    }

    public Stream<Note> get(String classroomId, int pageSize, int pageNumber, Timestamp lastRefresh) throws ExecutionException, InterruptedException {
        lastRefresh = Objects.requireNonNullElse(lastRefresh, Timestamp.now());
        List<Note> notes = repository.get(classroomId, pageSize, pageNumber, lastRefresh);
        return notes.stream();
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
            fileService.storeAll(attachments, authorSnapshot.getId());
            files = fileService.getFiles()
                    .stream()
                    .peek(f -> f.setOwnerId(null))
                    .collect(Collectors.toList());
        }

        MinifiedMember author = MinifiedMember.newBuilder()
                .fromSnapshot(authorSnapshot)
                .setJoinDatetime(member.getCreatedTimestamp())
                .setRole(member.getRole())
                .build();

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

    // TODO comment post, Check if student have permission to post Comment.
}
