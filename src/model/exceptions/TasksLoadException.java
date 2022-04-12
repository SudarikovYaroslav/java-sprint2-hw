package model.exceptions;

public class TasksLoadException extends Exception {
    public TasksLoadException(String message) {
        super(message);
    }

    public TasksLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
