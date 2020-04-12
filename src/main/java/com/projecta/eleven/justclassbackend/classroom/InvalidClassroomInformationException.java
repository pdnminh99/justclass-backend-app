package com.projecta.eleven.justclassbackend.classroom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidClassroomInformationException extends Exception {

    public InvalidClassroomInformationException() {
        super();
    }

    public InvalidClassroomInformationException(String message) {
        super(message);
    }

    public InvalidClassroomInformationException(Throwable cause) {
        super(cause);
    }

    public InvalidClassroomInformationException(String message, Throwable cause) {
        super(message, cause);
    }

}
