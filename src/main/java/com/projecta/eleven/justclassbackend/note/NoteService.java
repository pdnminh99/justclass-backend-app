package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.classroom.*;
import com.projecta.eleven.justclassbackend.file.FileService;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
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

    public Stream<BasicNote> get(String localId, String classroomId, int pageSize, int pageNumber) {
        return Stream.empty();
    }

    public Optional<BasicNote> create(
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

        var author = new MinifiedMember(member.getUserReference().get().get());
        author.setJoinDatetime(member.getCreatedTimestamp());
        author.setRole(member.getRole());

        var now = Timestamp.now();
        var note = new MaterialNote(
                repository.getNextId(),
                author,
                member.getUserId(),
                member.getUserReference(),
                content,
                now,
                0,
                classroomId,
                member.getClassroomReference(),
                null,
                null,
                null);
        if (attachments != null && attachments.size() > 0) {
            fileService.storeAll(attachments, note.getId());

            note.setAttachments(fileService.getFiles());
            note.setAttachmentReferences(fileService.getFilesReferences());

            fileService.flush();
        }
        repository.createNote(note);
        repository.commit();

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
