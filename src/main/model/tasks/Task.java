package main.model.tasks;

import main.model.Status;
import main.model.TaskTypes;
import main.model.exceptions.TaskTimeException;
import main.service.IdGenerator;
import main.util.Util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Basic "task" type
 */
public class Task {
    protected String name;
    protected String description;
    protected long id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, IdGenerator idGenerator) {
        this.name = name;
        this.description = description;
        this.id = idGenerator.generate();
        status = Status.NEW;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }


    public LocalDateTime getEndTime() throws TaskTimeException {
        if (startTime == null || duration == null) throw new TaskTimeException(
                "В Task id: " + getId() + "; startTime = " + getStartTime() + " duration = " + getDuration()
                        + " рассчитать EndTime невозможно!"
        );
        return startTime.plus(duration);
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
        return TaskTypes.TASK + "," + id + "," + name + "," + description + "," + status
                + "," + Util.convertStartTimeToString(startTime) + "," + Util.convertDurationInToString(duration);
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
