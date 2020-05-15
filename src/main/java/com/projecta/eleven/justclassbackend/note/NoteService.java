package com.projecta.eleven.justclassbackend.note;

import com.projecta.eleven.justclassbackend.classroom.IClassroomOperationsService;
import com.projecta.eleven.justclassbackend.classroom.MinifiedMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Optional<BasicNote> create(String localId, String classroomId, String content) throws ExecutionException, InterruptedException {
        if (localId == null || localId.trim().length() == 0 || classroomId == null || classroomId.trim().length() == 0) {
            throw new IllegalArgumentException("LocalId and ClassroomId must not null or empty.");
        }
        Optional<MinifiedMember> member = classroomService.getMember(localId, classroomId);
        // TODO If user is STUDENT. Check for whether studentsPermission is VCP
        // If user is COLLABORATOR. Let them create the note.

        return Optional.empty();
    }

    // TODO comment post, Check if student have permission to post Comment.
}
