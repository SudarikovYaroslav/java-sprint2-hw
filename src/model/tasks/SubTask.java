package model.tasks;

import service.IdGenerator;

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
}
