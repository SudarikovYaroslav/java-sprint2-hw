package main.tasks;

import main.Status;

public class Task {
    private String name;
    private String description;
    private long id;
    private Status status;

    public Task(String taskName, String description) {
        this.name = taskName;
        this.description = description;
        status = Status.NEW;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
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
