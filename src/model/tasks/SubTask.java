package model.tasks;

import model.Status;
import model.TaskTypes;
import service.IdGenerator;

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
        return TaskTypes.SUB_TASK + "," + id + "," + name + "," + description + "," + status + "," + epic.getId();
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
}
