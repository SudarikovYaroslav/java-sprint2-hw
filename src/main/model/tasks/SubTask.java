package main.model.tasks;

import main.model.Status;
import main.model.TaskTypes;
import main.model.exceptions.TaskTimeException;
import main.service.IdGenerator;
import main.util.Util;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private Epic epic;


    public SubTask(String name, String description, IdGenerator idGenerator) {
        super(name, description, idGenerator);
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
                + "," + timeParametersManager.convertStartTimeToString(startTime) + ","
                + timeParametersManager.convertDurationInToString(duration) + ","
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
        return Objects.equals(epic, subTask.epic);
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
