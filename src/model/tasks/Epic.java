package model.tasks;

import model.TaskTypes;
import model.exceptions.TaskTimeException;
import service.EpicStatusService;
import service.TimeParameterConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();
    //Множество id используется для корректной загрузки Epic-ов
    private final Set<Long> subTasksIds = new HashSet<>();
    private final EpicStatusService epicStatusService = new EpicStatusService();

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

    public Set<Long> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTask(SubTask subTask) {
        subTask.setEpic(this);
        subTasks.add(subTask);
        subTasksIds.add(subTask.getId());
        calculateStatus();
        duration = countSubTasksDuration();
    }

    public void deleteSubTaskById(long id) {
        subTasks.removeIf(subTask -> subTask.getId() == id);
        subTasksIds.remove(id);
        calculateStatus();
        duration = countSubTasksDuration();
    }

    public void addSubTasks(List<SubTask> subTasks) {
        linkWithSubTasks(subTasks);
        this.subTasks.addAll(subTasks);

        for (SubTask item : subTasks) {
            subTasksIds.add(item.getId());
        }

        calculateStatus();
        duration = countSubTasksDuration();
    }

    public void addSubTasksId(Long[] ids) {
        subTasksIds.addAll(Arrays.asList(ids));
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
        StringBuilder resultBuilder = new StringBuilder(TaskTypes.EPIC + "," + id + "," + name + "," + description
                + "," + status + "," + TimeParameterConverter.convertStartTimeToString(startTime) + ","
                + TimeParameterConverter.convertDurationToString(duration));

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

        for (int i = 0; i < epic.getSubTasks().size(); i++) {
            if (this.getSubTasks().get(i).getId() != epic.getSubTasks().get(i).getId()) return false;
        }

        return id == epic.id &&
                Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) &&
                status == epic.status &&
                isStartTimeEquals(epic) &&
                isDurationEquals(epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    /**
     * startTime эпика равно самому раннему startTime из его подзадач. Если у эпика нет подзадач, или у них
     * не установлены параметры времени, эпику установится startTime, переданное аргументом. При добавлении
     * эпику подзадач, значение startTime будет пересчитано
     * */
    @Override
    public void setStartTime(LocalDateTime startTime) {
        if (subTasks.isEmpty()) {
            this.startTime = startTime;
            return;
        }

        LocalDateTime earliestStartTime = LocalDateTime.MAX;

        for (SubTask subTask : subTasks) {
            LocalDateTime subTaskStartTime = subTask.getStartTime();
            if (subTaskStartTime == null) continue;
            if (subTaskStartTime.isBefore(earliestStartTime)) earliestStartTime = subTaskStartTime;
        }

        // если стартовое время не изменилось - у подзадач не установлены параметры времени
        // устанавливаем эпику собственное startTime
        if (earliestStartTime.equals(LocalDateTime.MAX)) {
            this.startTime = startTime;
            return;
        }
        this.startTime = earliestStartTime;
    }

    /**
     * Возвращает самое раннее startTime из подзадач эпика. Если у эпика нет подзадач, или у подзадач не установлены
     * параметры времени, вернётся собственное startTime эпика или null, если оно не было установлено
     * */
    @Override
    public LocalDateTime getStartTime() {
        if (subTasks.isEmpty()) return startTime;

        LocalDateTime resultStartTime = LocalDateTime.MAX;

        for (SubTask subTask : subTasks) {
            LocalDateTime subTaskStartTime = subTask.getStartTime();
            if (subTaskStartTime == null) continue;
            if (subTaskStartTime.isBefore(resultStartTime)) resultStartTime = subTaskStartTime;
        }

        // если стартовое время не изменилось - у подзадач не установлены параметры времени
        if (resultStartTime.equals(LocalDateTime.MAX)) return startTime;
        return resultStartTime;
    }

    /**
     * duration эпика всегда равна сумме duration его подзадач. Только в случае, если у эпика нет подзадач или в
     * подзадачах не установлены параметры времени, эпику установится duration, переданное аргументом. При добавлении
     * новых подзадач, значение duration эпика будет пересчитано
     */
    @Override
    public void setDuration(Duration duration) {
        if (subTasks.isEmpty()) {
            this.duration = duration;
            return;
        }
        Duration subTasksDuration = countSubTasksDuration();
        // если  subTasksDuration == null, значит у подзадач не установлены параметры времени
        // устанавливаем эпику собственную продолжительность.
        if (subTasksDuration == null) {
            this.duration = duration;
            return;
        }
        this.duration = subTasksDuration;
    }

    /**
     * duration эпика равна сумме duration его подзадач. Если у подзадач не установлены параметры времени,
     * вернётся собственное duration эпика или null, если оно не установлено
     */
    @Override
    public Duration getDuration() {
        Duration subTasksDuration = countSubTasksDuration();
        if (subTasksDuration != null) return subTasksDuration;
        return duration;
    }

    /**
     * endTime эпика равно самому позднему endTime из его подзадач. Если у эпика нет подзадач или у них не установлены
     * параметры времени, вернётся endTime, рассчитанное из собственных значений startTime и duration текущего эпика
     * или null, если они не установлены
     * */
    @Override
    public LocalDateTime getEndTime() throws TaskTimeException {
        if (subTasks.isEmpty() && duration != null && startTime != null) return startTime.plus(duration);
        if (subTasks.isEmpty()) throw new TaskTimeException(
                "В Epic id: " + getId() + "; startTime = " + getStartTime() + " duration = " + getDuration()
                        + " рассчитать EndTime невозможно!"
        );

        LocalDateTime resultEndTime = LocalDateTime.MIN;

        for (SubTask subTask : subTasks) {
            LocalDateTime subTaskEndTime = subTask.getEndTime();
            if (subTaskEndTime == null) continue;
            if (subTaskEndTime.isAfter(resultEndTime)) {
                resultEndTime = subTaskEndTime;
            }
        }

        //если true значит у подзадач не установлены параметры начала и продолжительности
        if (resultEndTime.equals(LocalDateTime.MIN)) {
            //проверяем задавались ли самому Epic параметры времени - нет возвращаем null
            if (duration != null && startTime != null) return startTime.plus(duration);
            return null;
        }
        return resultEndTime;
    }

    /**
     * рассчитывает суммарную duration всех подзадач эпика. Если у эпика нет подзадач или у них не установлены
     * параметры времени, возвращает null
     */
    private Duration countSubTasksDuration() {
        if (subTasks.isEmpty()) return null;
        Duration resultDuration = Duration.ofSeconds(0);

        for (SubTask subTask : subTasks) {
            if (subTask.getDuration() != null) resultDuration = resultDuration.plus(subTask.duration);
        }

        //если resultDuration не изменилось, у подзадач не установлены параметры времени
        if (resultDuration.equals(Duration.ofSeconds(0))) {
            return null;
        }
        return resultDuration;
    }
}
