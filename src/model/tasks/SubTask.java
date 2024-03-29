package model.tasks;

import model.Status;
import model.TaskTypes;
import model.exceptions.TaskTimeException;
import service.TimeParameterConverter;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private Epic epic;


    public SubTask(String name, String description, long id) {
        super(name, description, id);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return TaskTypes.SUB_TASK + "," + id + "," + name + "," + description + "," + status
                + "," + TimeParameterConverter.convertStartTimeToString(startTime) + ","
                + TimeParameterConverter.convertDurationToString(duration) + ","
                + epic.getId();
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        if (epic != null) {
            epic.calculateStatus();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return id == subTask.id &&
                Objects.equals(name, subTask.name) &&
                Objects.equals(description, subTask.description) &&
                status == subTask.status &&
                startTime == subTask.startTime &&
                duration == subTask.duration &&
                epic.id == subTask.epic.id &&
                isStartTimeEquals(subTask) &&
                isDurationEquals(subTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }

    @Override
    public LocalDateTime getEndTime() throws TaskTimeException {
        if (startTime == null || duration == null) throw new TaskTimeException(
                "В SubTask id: " + getId() + "; startTime = " + getStartTime() + " duration = " + getDuration()
                        + " рассчитать EndTime невозможно!"
        );
        return startTime.plus(duration);
    }
}
