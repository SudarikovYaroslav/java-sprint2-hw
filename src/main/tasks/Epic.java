package main.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, String description, List<SubTask> subTasks, long id) {
        super(name, description, id);
        addSubTasks(subTasks);
    }

    public Epic(String name, String description, long id) {
        super(name, description, id);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTask.setEpic(this);
        subTasks.add(subTask);
    }

    public void deleteSubTaskById(long id) {
        subTasks.removeIf(subTask -> subTask.getId() == id);
    }

    public void addSubTasks(List<SubTask> subTasks) {
        linkWithSubTasks(subTasks);
        this.subTasks.addAll(subTasks);
    }

    private void linkWithSubTasks(List<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            subTask.setEpic(this);
        }
    }
}
