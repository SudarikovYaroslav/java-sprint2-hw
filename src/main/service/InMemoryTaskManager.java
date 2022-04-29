package main.service;

import main.model.IntersectionAlerts;
import main.model.exceptions.*;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, Epic> epics;
    protected final HashMap<Long, SubTask> subTasks;
    protected final Set<Task> prioritizedTasks;
    protected final EpicStatusService epicStatusService;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>();
        epicStatusService = new EpicStatusService();
        this.historyManager = historyManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteTasks() {
        for (Long id : tasks.keySet()) {
            historyManager.remove(id);
        }
        deleteTasksFromPrioritizedSet(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (long id : epics.keySet()) {
            historyManager.remove(id);
        }
        deleteEpicsFromPrioritizedSet(epics.values());
        epics.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (long id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        deleteSubTasksFromPrioritizedSet(subTasks.values());
        subTasks.clear();
    }

    @Override
    public Task getTaskById(long id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void createTask(Task task) throws TaskCreateException, TimeIntersectionException {
        if (task == null) throw new TaskCreateException("При создании task == null");
        if (task.getId() <= 0) throw new TaskCreateException(
                "При создании Task id должен быть больше 0. Actual: " + task.getId());
        if (tasks.containsKey(task.getId())) throw new TaskCreateException(
                "Task с id: " + task.getId() + " уже существует"
        );

        IntersectionAlerts check = checkTimeIntersections(task);
        if(check.isIntersection()) {
            throw new TimeIntersectionException(check.getAlerts().toString());
        }

        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) throws TaskCreateException, TimeIntersectionException {
        if (epic == null) throw new TaskCreateException("При создании Epic == null");
        if (epic.getId() <= 0) throw new TaskCreateException(
                "При создании Epic id должен быть больше 0. Actual: " + epic.getId());
        if (tasks.containsKey(epic.getId())) throw new TaskCreateException(
                "Epic с id: " + epic.getId() + " уже существует"
        );

        IntersectionAlerts check = checkTimeIntersections(epic);
        if(check.isIntersection()) {
            throw new TimeIntersectionException(check.getAlerts().toString());
        }

        epics.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void createSubTask(SubTask subTask) throws TaskCreateException, TimeIntersectionException {
        if (subTask == null) throw new TaskCreateException("При создании SubTask == null");
        if (subTask.getId() <= 0) throw new TaskCreateException(
                "При создании SubTask id должен быть больше 0. Actual: " + subTask.getId());
        if (tasks.containsKey(subTask.getId())) throw new TaskCreateException(
                "SubTask с id: " + subTask.getId() + " уже существует"
        );

        IntersectionAlerts check = checkTimeIntersections(subTask);
        if(check.isIntersection()) {
            throw new TimeIntersectionException(check.getAlerts().toString());
        }

        subTasks.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
    }

    /**
     * Method gets new version of task with right id as parameter
     */
    @Override
    public void updateTask(Task task) throws TaskUpdateException, TimeIntersectionException {
        if (task == null) throw new TaskUpdateException("Обновляемая Task = null");
        if (task.getId() <= 0) throw new TaskUpdateException(
                "id обновляемой Task должен быть больше 0! Actual: " + task.getId()
        );
        if (!tasks.containsKey(task.getId())) throw new TaskUpdateException(
                "Task с id: " + task.getId() + " не существует. Обновление невозможно!"
        );

        IntersectionAlerts check = checkTimeIntersections(task);
        if(check.isIntersection()) {
            throw new TimeIntersectionException(check.getAlerts().toString());
        }

        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    /**
     * Method gets new version of epic with right id as parameter
     */
    @Override
    public void updateEpic(Epic epic) throws TaskUpdateException, TimeIntersectionException {
        if (epic == null) throw new TaskUpdateException("Обновляемый Epic = null");
        if (epic.getId() <= 0) throw new TaskUpdateException(
                "id обновляемого Epic должен быть больше 0! Actual: " + epic.getId()
        );
        if (!epics.containsKey(epic.getId())) throw new TaskUpdateException(
                "Epic с id: " + epic.getId() + " не существует. Обновление невозможно!"
        );

        IntersectionAlerts check = checkTimeIntersections(epic);
        if(check.isIntersection()) {
            throw new TimeIntersectionException(check.getAlerts().toString());
        }

        epics.put(epic.getId(), epic);

        for (SubTask subTask : epic.getSubTasks()) {
            updateSubTask(subTask);
        }
        prioritizedTasks.add(epic);
    }

    /**
     * Method gets new version of SubTask with right id as parameter
     */
    @Override
    public void updateSubTask(SubTask subTask) throws TaskUpdateException, TimeIntersectionException {
        if (subTask == null) throw new TaskUpdateException("Обновляемая SubTask = null");
        if (subTask.getId() <= 0) throw new TaskUpdateException(
                "id обновляемой SubTask должен быть больше 0! Actual: " + subTask.getId()
        );
        if (!subTasks.containsKey(subTask.getId())) throw new TaskUpdateException(
                "SubTask с id: " + subTask.getId() + " не существует. Обновление невозможно!"
        );

        IntersectionAlerts check = checkTimeIntersections(subTask);
        if(check.isIntersection()) {
            throw new TimeIntersectionException(check.getAlerts().toString());
        }

        subTasks.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
    }

    @Override
    public void deleteTaskById(long id) throws TaskDeleteException {
        if (!tasks.containsKey(id)) throw new TaskDeleteException(
                "Task с id: " + id + " не существует. Удаление не возможно!"
        );

        historyManager.remove(id);
        deleteTaskFromPrioritizedSet(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(long id) throws TaskDeleteException {
        if (!epics.containsKey(id)) throw new TaskDeleteException(
                "Epic с id: " + id + " не существует. Удаление не возможно!"
        );

        List<SubTask> innerSubTasks = epics.get(id).getSubTasks();
        historyManager.remove(id);
        deleteEpicFromPrioritizedSet(id); // также удалит из приоритетного множества подзадачи эпика
        epics.remove(id);

        for (SubTask subTask : innerSubTasks) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
    }

    @Override
    public void deleteSubTaskById(long id) throws TaskDeleteException {
        if (!subTasks.containsKey(id)) throw new TaskDeleteException(
                "SubTask с id: " + id + " не существует. Удаление не возможно!"
        );

        Epic epic = subTasks.get(id).getEpic();
        epic.deleteSubTaskById(id);
        historyManager.remove(id);
        deleteSubTaskFromPrioritizedSet(id);
        subTasks.remove(id);
        epic.setStatus(epicStatusService.calculateStatus(epic));
    }

    @Override
    public List<SubTask> getSubTasks(Epic epic) {
        if (epic == null) throw  new NullPointerException("Epic = null! при попытке getSubTasks()");
        return epic.getSubTasks();
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    protected void deleteTaskFromPrioritizedSet(long id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
        }
    }

    protected void deleteEpicFromPrioritizedSet(long id) {
        if (epics.containsKey(id)) {
            List<SubTask> subTasks = epics.get(id).getSubTasks();

            prioritizedTasks.remove(epics.get(id));

            for (SubTask subTask : subTasks) {
                prioritizedTasks.remove(subTask);
            }
        }
    }

    protected void deleteSubTaskFromPrioritizedSet(long id) {
        if (subTasks.containsKey(id)) {
            prioritizedTasks.remove(subTasks.get(id));
        }
    }

    protected void deleteTasksFromPrioritizedSet(Collection<Task> tasks) {
        for (Task task : tasks) {
            prioritizedTasks.remove(task);
        }
    }

    protected void deleteEpicsFromPrioritizedSet(Collection<Epic> epics) {
        for (Epic epic : epics) {

            for (SubTask subTask : epic.getSubTasks()) {
                prioritizedTasks.remove(subTask);
            }
            prioritizedTasks.remove(epic);
        }
    }

    protected void deleteSubTasksFromPrioritizedSet(Collection<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            prioritizedTasks.remove(subTask);
        }
    }

    /**
     * Возвращает IntersectionAlerts со значениями boolean true и Optional<String> "id startTime: value endTime: value"
     * или boolean true и Optional<String> "id startTime: value"
     * если задача имеет пересечение по времени с существующей задачей c id, началом startTime и завершением endTime.
     * Или boolean false и null, если пересечений не выявлено
     */
    protected IntersectionAlerts checkTimeIntersections(Task task) {
        if (task == null) throw new NullPointerException();
        if (task.getStartTime() == null) return new IntersectionAlerts(false, Optional.empty());

        // у task установлено только startTime
        if (task.getStartTime() != null && task.getDuration() == null) {
            return searchIfTaskHaveStartTimeOnly(task);
        }

        // У task установлены startTime и duration
        return searchIfTaskHaveStartTimeAndDuration(task);
    }

    protected IntersectionAlerts searchIfTaskHaveStartTimeOnly(Task checkedTask) {
        boolean intersection;
        boolean specialCase = false; // startTime эпика и его подзадачи могут быть равны

        for (Task existsTask : getPrioritizedTasks()) {
            if (existsTask.getStartTime() == null) continue;

            if (existsTask.getStartTime() != null && existsTask.getDuration() == null) {
                if (checkedTask.getStartTime().equals(existsTask.getStartTime())) {
                    specialCase = controlIfEpicAndSubTaskPair(checkedTask, existsTask);
                    if (specialCase) continue;
                    return new IntersectionAlerts(true, Optional.of(createAlertMessage(existsTask)));
                }
            }

            if (existsTask.getStartTime() != null && existsTask.getDuration() != null ) {
                intersection = checkIfTaskPeriodIncludesTimePoint(existsTask, checkedTask.getStartTime());
                specialCase = controlIfEpicAndSubTaskPair(checkedTask, existsTask);
                if (specialCase) continue;
                if (intersection)
                    return new IntersectionAlerts(true, Optional.of(createAlertMessage(existsTask)));
            }
        }
        return new IntersectionAlerts(false, Optional.empty());
    }

    protected IntersectionAlerts searchIfTaskHaveStartTimeAndDuration(Task checkedTask) {
        boolean intersection;
        boolean startIntersection;
        boolean endIntersection;
        boolean specialCase = false; // startTime эпика и его подзадачи могут быть равны
        try {
            for (Task existsTask : getPrioritizedTasks()) {
                if (existsTask.getStartTime() == null) continue;

                // если у существующей задачи установлено только startTime без duration,
                // проверяем пересечение нашей задачи со startTime уже существующей
                if (existsTask.getStartTime() != null && existsTask.getDuration() == null) {
                    intersection = checkIfTaskPeriodIncludesTimePoint(checkedTask, existsTask.getStartTime());
                    if (intersection)
                        specialCase = controlIfEpicAndSubTaskPair(checkedTask, existsTask);
                        if (specialCase) continue;
                    return new IntersectionAlerts(true, Optional.of(createAlertMessage(existsTask)));
                }

                // проверяем пересечение времени исполнения нашей задачи и существующей
                if (existsTask.getStartTime() != null && existsTask.getDuration() != null) {
                    startIntersection = checkIfTaskPeriodIncludesTimePoint(existsTask, checkedTask.getStartTime());
                    endIntersection = checkIfTaskPeriodIncludesTimePoint(existsTask, checkedTask.getEndTime());
                    if (startIntersection || endIntersection)
                        return new IntersectionAlerts(true, Optional.of(createAlertMessage(existsTask)));
                }
            }
        } catch (TaskTimeException e) {
            e.printStackTrace();
        }
        return new IntersectionAlerts(false, Optional.empty());
    }

    /**
     * Проверяет, попала ли переданная точка времени во интервал между task.startTime и task.EndTime
     */
    protected boolean checkIfTaskPeriodIncludesTimePoint(Task task, LocalDateTime timePoint) {
        try {
            LocalDateTime start = task.getStartTime();
            LocalDateTime end = task.getEndTime();

            if (timePoint.isAfter(start) && timePoint.isBefore(end)
            || timePoint.equals(start)
            || timePoint.equals(end)) return true;

        } catch (TaskTimeException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected String createAlertMessage(Task task) {
        String alert = "Пересечение по времени с " + task.getId() + " startTime: " + task.getStartTime();

        if (task.getDuration() == null) {
            return alert;
        }

        try {
            alert += " endTime: " + task.getEndTime();
        } catch (TaskTimeException e) {
            e.printStackTrace();
        }
        return alert;
    }

    /**
     * В случае, если проверяемые задачи - это Epic с его SubTask, из-за особенностей расчёта стартового времени
     * класса Epic - у них может быть одинаковое startTime. В этом случае возвращается true, в противном false
     */
    protected boolean controlIfEpicAndSubTaskPair(Task testedTask, Task existsTask) {
        //т.к. один из тасков должен быть эпик, а второй подзадача - проверяем оба возможных варианта
        try {
            Epic epic = (Epic) testedTask;
            SubTask subTask = (SubTask) existsTask;
            if (epic.getSubTasks().contains(subTask) && subTask.getEpic().getId() == epic.getId()) return true;
        } catch (ClassCastException e1) {
            try {
                SubTask subTask = (SubTask) testedTask;
                Epic epic = (Epic) existsTask;
                if (epic.getSubTasks().contains(subTask) && subTask.getEpic().getId() == epic.getId()) return true;
            } catch (ClassCastException e2) {
                return false;
            }
        }
        return false;
    }
}
