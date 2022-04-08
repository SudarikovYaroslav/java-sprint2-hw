package model.tasks;

import model.Status;
import model.TaskTypes;
import service.IdGenerator;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, IdGenerator idGenerator) {
        super(name, description, idGenerator);
    }

    /**
     * WARNING!
     * This constructor MUST BE used only when SubTask loaded from the file storage
     */
    public SubTask(long id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
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
}
