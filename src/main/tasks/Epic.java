package main.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Task> subTasks = new ArrayList<>();

    public Epic(String taskName, String description, ArrayList<SubTask> subTasks) {
        super(taskName, description);
        addSubTasks(subTasks);
    }

    public Epic(String taskName, String description) {
        super(taskName, description);
    }

    public ArrayList<Task> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTask.setEpic(this);
        subTasks.add(subTask);
    }

    public void deleteSubTaskById(long id) {
        subTasks.removeIf(subTask -> subTask.getId() == id);
    }

    public void addSubTasks(ArrayList<SubTask> subTasks) {
        linkWithSubTasks(subTasks);
        this.subTasks.addAll(subTasks);
    }

    private void linkWithSubTasks(ArrayList<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            subTask.setEpic(this);
        }
    }
}
