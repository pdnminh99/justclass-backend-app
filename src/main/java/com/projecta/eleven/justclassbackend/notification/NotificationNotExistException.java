package com.projecta.eleven.justclassbackend.notification;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class NotificationNotExistException extends Exception {

    public NotificationNotExistException() {
        super();
    }

    public NotificationNotExistException(String message) {
        super(message);
    }

    public NotificationNotExistException(Throwable cause) {
        super(cause);
    }

    public NotificationNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

}
