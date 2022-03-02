package main.managers;

import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;
import main.util.EpicStatusService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, Epic> epics;
    private final HashMap<Long, SubTask> subTasks;
    private final EpicStatusService epicStatusService;
    private final List<Task> historyList;
    private long id;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyList = new ArrayList<>();
        id = 1;
        epicStatusService = new EpicStatusService();
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
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
    }

    @Override
    public Task getTaskById(long id) {
        Task task = tasks.get(id);
        if (task != null) addToTheHistoryList(task);
        return task;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = epics.get(id);
        if (epic != null) addToTheHistoryList(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) addToTheHistoryList(subTask);
        return subTask;
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    /**
     * Method gets new version of task with right id as parameter
     */
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Method gets new version of epic with right id as parameter
     */
    @Override
    public void updateEpic(Epic epic) {
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
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public void deleteTaskById(long id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(long id) {
        List<SubTask> innerSubTasks = epics.get(id).getSubTasks();
        epics.remove(id);

        for (SubTask subTask : innerSubTasks) {
            subTasks.remove(subTask.getId());
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        Epic epic = subTasks.get(id).getEpic();
        epic.deleteSubTaskById(id);
        subTasks.remove(id);
        epic.setStatus(epicStatusService.calculateStatus(epic));
    }

    @Override
    public List<SubTask> getSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public long generatedId() {
        return id++;
    }

    /**
     * As a result returns last 10 requests different types of tasks by id
     */
    @Override
    public List<Task> history() {
        return historyList;
    }

    private void addToTheHistoryList(Task task) {
        byte maxHistorySize = 10;
        boolean full = historyList.size() >= maxHistorySize;
        if (full) historyList.remove(0);
        historyList.add(task);
    }
}
