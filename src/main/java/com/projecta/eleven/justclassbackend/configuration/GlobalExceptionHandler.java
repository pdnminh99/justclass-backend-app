package com.projecta.eleven.justclassbackend.configuration;

import com.projecta.eleven.justclassbackend.classroom.InvalidClassroomInformationException;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.NestedServletException;

import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ExecutionException.class, InterruptedException.class, NestedServletException.class})
    public ResponseEntity<String> handleFirestoreException(Exception e) {
        System.err.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body("Fail to connect to Database. Please try again.");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleHttpMessageNotReadable() {
        return ResponseEntity.badRequest().body("Invalid JSON string.");
    }

    @ExceptionHandler({InvalidUserInformationException.class, InvalidClassroomInformationException.class})
    public String handleInvalidUserInfo(InvalidUserInformationException e) {
        return e.getMessage();
    }
}
