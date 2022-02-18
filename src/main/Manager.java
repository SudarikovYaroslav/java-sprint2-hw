package main;

import main.util.EpicStatusService;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int id;
    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, Epic> epics;
    private final HashMap<Long, SubTask> subTasks;
    private final EpicStatusService epicStatusService;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        id = 0;
        epicStatusService = new EpicStatusService();
    }

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasksList() {
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
        assignId(task);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        assignId(epic);
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        assignId(subTask);
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
        ArrayList<Task> innerSubTasks = epics.get(id).getSubTasks();
        epics.remove(id);

        for (Task task : innerSubTasks) {
            subTasks.remove(task.getId());
        }
    }

    public void deleteSubTaskById(long id) {
        Epic epic = subTasks.get(id).getEpic();
        epic.deleteSubTaskById(id);
        subTasks.remove(id);
        epic.setStatus(epicStatusService.calculateStatus(epic));
    }

    public ArrayList<Task> getSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    private void assignId(Task task) {
        task.setId(++id);
    }
}
