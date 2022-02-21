package main;

import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;
import main.util.EpicStatusService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {
    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, Epic> epics;
    private final HashMap<Long, SubTask> subTasks;
    private final EpicStatusService epicStatusService;
    private long id;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        id = 1;
        epicStatusService = new EpicStatusService();
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
    }

    public void deleteSubTasks() {
        subTasks.clear();
    }

    public Task getTaskById(long id) {
        return tasks.get(id);
    }

    public Epic getEpicById(long id) {
        return epics.get(id);
    }

    public SubTask getSubTaskById(long id) {
        return subTasks.get(id);
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    /**
     * Method gets new version of task with right id as parameter
     */
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Method gets new version of epic with right id as parameter
     */
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
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    public void deleteTaskById(long id) {
        tasks.remove(id);
    }

    public void deleteEpicById(long id) {
        List<SubTask> innerSubTasks = epics.get(id).getSubTasks();
        epics.remove(id);

        for (SubTask subTask : innerSubTasks) {
            subTasks.remove(subTask.getId());
        }
    }

    public void deleteSubTaskById(long id) {
        Epic epic = subTasks.get(id).getEpic();
        epic.deleteSubTaskById(id);
        subTasks.remove(id);
        epic.setStatus(epicStatusService.calculateStatus(epic));
    }

    public List<SubTask> getSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    public long generatedId() {
        return id++;
    }
}
