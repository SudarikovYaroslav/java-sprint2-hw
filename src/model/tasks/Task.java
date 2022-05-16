package model.tasks;

import model.Status;
import model.TaskTypes;
import model.exceptions.TaskTimeException;
import service.TimeParameterConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Basic "task" type
 */
public class Task implements Comparable<Task> {
    protected String name;
    protected String description;
    protected long id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, long id) {
        this.name = name;
        this.description = description;
        this.id = id;
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
                + "," + TimeParameterConverter.convertStartTimeToString(startTime) + ","
                + TimeParameterConverter.convertDurationToString(duration);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                isStartTimeEquals(task) &&
                isDurationEquals(task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public int compareTo(Task o) {
        if (id == o.id) return 0;
        if (startTime == null && o.startTime == null) return 1;
        if (startTime != null && o.startTime == null) return -1;
        if (startTime == null && o.startTime != null) return 1;
        if (startTime.isBefore(o.startTime)) return -1;
        return 1;
    }

    protected boolean isStartTimeEquals(Task task) {
        if (startTime == null && task.startTime != null) return false;
        if (startTime != null && task.startTime == null) return false;
        if (startTime == null && task.startTime == null) return true;
        if (startTime.equals(task.startTime)) return true;
        return false;
    }

    protected boolean isDurationEquals(Task task) {
        if (duration == null && task.duration != null) return false;
        if (duration != null && task.duration == null) return false;
        if (duration == null && task.duration == null) return true;
        if (duration.equals(task.duration)) return true;
        return false;
    }
}
