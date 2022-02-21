package main.tasks;

import main.Manager;
import main.Status;

/**
 * Basic "task" type
 */
public class Task {
    private String name;
    private String description;
    private long id;
    private Status status;

    public Task(String name, String description, long id) {
        this.name = name;
        this.description = description;
        this.id = id;
        status = Status.NEW;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
