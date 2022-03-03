package model.tasks;

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
}
