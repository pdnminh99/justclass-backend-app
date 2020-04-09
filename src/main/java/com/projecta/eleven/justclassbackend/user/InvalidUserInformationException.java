package com.projecta.eleven.justclassbackend.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidUserInformationException extends Exception {

    public InvalidUserInformationException() {
        super();
    }

    public InvalidUserInformationException(String message) {
        super(message);
    }

    public InvalidUserInformationException(Throwable cause) {
        super(cause);
    }

    public InvalidUserInformationException(String message, Throwable cause) {
        super(message, cause);
    }

}
