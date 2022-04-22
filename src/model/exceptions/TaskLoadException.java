package model.exceptions;

public class TaskLoadException extends Exception {

    public TaskLoadException(String message) {
        super(message);
    }

    public TaskLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
