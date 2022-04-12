package model.tasks;

import model.Status;
import model.TaskTypes;
import service.IdGenerator;

import java.util.*;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();
    //Множество id используется для корректной загрузки Epic-ов
    private Set<Long> subTasksId = new HashSet<>();

    public Epic(String name, String description, List<SubTask> subTasks, IdGenerator idGenerator) {
        super(name, description, idGenerator);
        addSubTasks(subTasks);
    }

    public Epic(String name, String description, IdGenerator idGenerator) {
        super(name, description, idGenerator);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public Set<Long> getSubTasksId() {
        return subTasksId;
    }

    public void addSubTask(SubTask subTask) {
        subTask.setEpic(this);
        subTasks.add(subTask);
    }

    public void deleteSubTaskById(long id) {
        subTasks.removeIf(subTask -> subTask.getId() == id);
        subTasksId.remove(id);
    }

    public void addSubTasks(List<SubTask> subTasks) {
        linkWithSubTasks(subTasks);
        this.subTasks.addAll(subTasks);

        for (SubTask item : subTasks) {
            subTasksId.add(item.getId());
        }
    }

    public void addSubTasksId (Long[] ids) {
        subTasksId.addAll(Arrays.asList(ids));
    }

    private void linkWithSubTasks(List<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            subTask.setEpic(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder(TaskTypes.EPIC + "," + id + "," + name + "," + description +
                "," + status);

        if (!subTasks.isEmpty()) {
            resultBuilder.append(",");

            for (SubTask item : subTasks) {
                resultBuilder.append(item.getId()).append(".");
            }
        }

        return resultBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks) &&
                Objects.equals(subTasksId, epic.subTasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks, subTasksId);
    }
}
