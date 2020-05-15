package com.projecta.eleven.justclassbackend.note;

import com.projecta.eleven.justclassbackend.classroom.*;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Service
public class NoteService {

    private final NoteRepository repository;

    private final IClassroomOperationsService classroomService;

    @Autowired
    public NoteService(NoteRepository repository, IClassroomOperationsService classroomService) {
        this.repository = repository;
        this.classroomService = classroomService;
    }

    public Stream<BasicNote> get(String localId, String classroomId, int pageSize, int pageNumber) {
        return Stream.empty();
    }

    public Optional<BasicNote> create(String localId, String classroomId, String content, List<MultipartFile> attachments) throws ExecutionException, InterruptedException, InvalidUserInformationException {
        if (localId == null || localId.trim().length() == 0 || classroomId == null || classroomId.trim().length() == 0) {
            throw new IllegalArgumentException("LocalId and ClassroomId must not null or empty.");
        }
        Member member = classroomService
                .getMember(localId, classroomId)
                .orElseThrow(InvalidUserInformationException::new);
        // TODO If user is STUDENT. Check for whether studentsPermission is VCP
        // If user is COLLABORATOR. Let them create the note.

        if (member.getRole() == MemberRoles.STUDENT) {
            var classroom = new Classroom(member.getClassroomReference().get().get());

            if (classroom.getStudentsNotePermission() != NotePermissions.VIEW_COMMENT_POST) {
                throw new IllegalArgumentException("Student with Id [" + localId + "] does not have permission to create a note.");
            }
        }

        return Optional.empty();
    }

    // TODO comment post, Check if student have permission to post Comment.
}
