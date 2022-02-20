package main.tasks;

import main.Manager;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, Manager manager) {
        super(name, description, manager);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
