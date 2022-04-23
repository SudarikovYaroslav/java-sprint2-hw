package service;

import model.exceptions.TaskCreateException;
import model.exceptions.TaskUpdateException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, Epic> epics;
    protected final HashMap<Long, SubTask> subTasks;
    protected final EpicStatusService epicStatusService;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
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
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (long id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (long id : subTasks.keySet()) {
            historyManager.remove(id);
        }
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
    public void createTask(Task task) throws TaskCreateException{
        if (task == null) throw new TaskCreateException("При создании task == null");
        if (task.getId() <= 0) throw new TaskCreateException(
                "При создании Task id должен быть больше 0. Actual: " + task.getId());
        if (tasks.containsKey(task.getId())) throw new TaskCreateException(
                "Task с id: " + task.getId() + " уже существует"
        );

        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) throws TaskCreateException{
        if (epic == null) throw new TaskCreateException("При создании Epic == null");
        if (epic.getId() <= 0) throw new TaskCreateException(
                "При создании Epic id должен быть больше 0. Actual: " + epic.getId());
        if (tasks.containsKey(epic.getId())) throw new TaskCreateException(
                "Epic с id: " + epic.getId() + " уже существует"
        );

        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) throws TaskCreateException{
        if (subTask == null) throw new TaskCreateException("При создании SubTask == null");
        if (subTask.getId() <= 0) throw new TaskCreateException(
                "При создании SubTask id должен быть больше 0. Actual: " + subTask.getId());
        if (tasks.containsKey(subTask.getId())) throw new TaskCreateException(
                "SubTask с id: " + subTask.getId() + " уже существует"
        );

        subTasks.put(subTask.getId(), subTask);
    }

    /**
     * Method gets new version of task with right id as parameter
     */
    @Override
    public void updateTask(Task task) throws TaskUpdateException {
        if (task == null) throw new TaskUpdateException("Обновляемая Task = null");
        if (task.getId() <= 0) throw new TaskUpdateException(
                "id обновляемой Task должен быть больше 0! Actual: " + task.getId()
        );
        if (!tasks.containsKey(task.getId())) throw new TaskUpdateException(
                "Task с id: " + task.getId() + " не существует. Обновление невозможно!"
        );

        tasks.put(task.getId(), task);
    }

    /**
     * Method gets new version of epic with right id as parameter
     */
    @Override
    public void updateEpic(Epic epic) throws TaskUpdateException {
        if (epic == null) throw new TaskUpdateException("Обновляемый Epic = null");
        if (epic.getId() <= 0) throw new TaskUpdateException(
                "id обновляемого Epic должен быть больше 0! Actual: " + epic.getId()
        );
        if (!epics.containsKey(epic.getId())) throw new TaskUpdateException(
                "Epic с id: " + epic.getId() + " не существует. Обновление невозможно!"
        );

        epic.setStatus(epicStatusService.calculateStatus(epic));
        epics.put(epic.getId(), epic);

        for (SubTask subTask : epic.getSubTasks()) {
            updateSubTask(subTask);
        }
    }

    /**
     * Method gets new version of SubTask with right id as parameter
     */
    @Override
    public void updateSubTask(SubTask subTask) throws TaskUpdateException {
        if (subTask == null) throw new TaskUpdateException("Обновляемый SubTask = null");
        if (subTask.getId() <= 0) throw new TaskUpdateException(
                "id обновляемой SubTask должен быть больше 0! Actual: " + subTask.getId()
        );
        if (!subTasks.containsKey(subTask.getId())) throw new TaskUpdateException(
                "SubTask с id: " + subTask.getId() + " не существует. Обновление невозможно!"
        );

        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public void deleteTaskById(long id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(long id) {
        List<SubTask> innerSubTasks = epics.get(id).getSubTasks();
        historyManager.remove(id);
        epics.remove(id);

        for (SubTask subTask : innerSubTasks) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        Epic epic = subTasks.get(id).getEpic();
        epic.deleteSubTaskById(id);
        historyManager.remove(id);
        subTasks.remove(id);
        epic.setStatus(epicStatusService.calculateStatus(epic));
    }

    @Override
    public List<SubTask> getSubTasks(Epic epic) {
        return epic.getSubTasks();
    }
}
