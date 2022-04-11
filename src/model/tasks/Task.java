package model.tasks;

import model.Status;
import model.TaskTypes;
import service.IdGenerator;

import java.util.Objects;

/**
 * Basic "task" type
 */
public class Task {
    protected String name;
    protected String description;
    protected long id;
    protected Status status;

    public Task(String name, String description, IdGenerator idGenerator) {
        this.name = name;
        this.description = description;
        this.id = idGenerator.generate();
        status = Status.NEW;
    }

    /**
     * WARNING!!!
     * This constructor MUST BE used only when Task loaded from the file storage
     */
    public Task(long id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
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

    @Override
    public String toString() {
        return TaskTypes.TASK + "," + id + "," + name + "," + description + "," + status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
