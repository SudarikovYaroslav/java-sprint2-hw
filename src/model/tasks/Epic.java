package model.tasks;

import model.Status;
import model.TaskTypes;
import model.exceptions.TaskTimeException;
import service.EpicStatusService;
import service.IdGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();
    //Множество id используется для корректной загрузки Epic-ов
    private final Set<Long> subTasksId = new HashSet<>();
    private final EpicStatusService epicStatusService = new EpicStatusService();

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
        subTasksId.add(subTask.getId());
        calculateStatus();
    }

    public void deleteSubTaskById(long id) {
        subTasks.removeIf(subTask -> subTask.getId() == id);
        subTasksId.remove(id);
        calculateStatus();
    }

    public void addSubTasks(List<SubTask> subTasks) {
        linkWithSubTasks(subTasks);
        this.subTasks.addAll(subTasks);

        for (SubTask item : subTasks) {
            subTasksId.add(item.getId());
        }

        calculateStatus();
    }

    public void addSubTasksId (Long[] ids) {
        subTasksId.addAll(Arrays.asList(ids));
    }

    private void linkWithSubTasks(List<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            subTask.setEpic(this);
        }
    }

    public void calculateStatus() {
        status = epicStatusService.calculateStatus(this);
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

        if (this.getSubTasks().size() != epic.getSubTasks().size()) return false;

        boolean subTasksEquals = true;

        for (int i = 0; i < epic.getSubTasks().size(); i++) {
            if (this.getSubTasks().get(i).getId() != epic.getSubTasks().get(i).getId()) return false;
        }

        return subTasksEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    @Override
    public void setDuration(Duration duration) {
        if (subTasks.isEmpty()) {
            this.duration = duration;
            return;
        }
        this.duration = countDuration();
    }

    @Override
    public LocalDateTime getEndTime() throws TaskTimeException {
        if (subTasks.isEmpty() && duration != null && startTime != null) return startTime.plus(duration);
        if (subTasks.isEmpty() && (duration == null || startTime == null)) throw new TaskTimeException(
                "В Epic id: " + getId() + "; startTime = " + getStartTime() + " duration = " + getDuration()
                + " рассчитать EndTime невозможно!"
        );

        LocalDateTime resultEndTime = LocalDateTime.of(0, 0,0,0,0);

        for (SubTask subTask : subTasks) {
            if (subTask.getEndTime().isAfter(resultEndTime)) {
                resultEndTime = subTask.getEndTime();
            }
        }
        return resultEndTime;
    }

    private Duration countDuration() {
        Duration resultDuration = Duration.ofSeconds(0);

        for (SubTask subTask : subTasks) {
            if (subTask.getDuration() != null) resultDuration = resultDuration.plus(subTask.duration);
        }

        return duration;
    }
}
