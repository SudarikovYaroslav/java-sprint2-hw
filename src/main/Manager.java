package main;

import main.Util.Util;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        id = 0;
    }

    public void assignId(Task task) {
        task.setId(id++);
    }

    public ArrayList<Task> getTasksList() {
        return (ArrayList<Task>) tasks.values();
    }

    public ArrayList<Epic> getEpicsList() {
        return (ArrayList<Epic>) epics.values();
    }

    public ArrayList<SubTask> getSubTasksList() {
        return (ArrayList<SubTask>) subTasks.values();
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

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskById(int id) {
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
     * Method get new version of task with right id
     */
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Method get new version of epic with right id
     */
    public void updateEpic(Epic epic) {
        Util.checkEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    /**
     * Method get new version of SubTask with right id
     */
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    public ArrayList<SubTask> getSubTasks(Epic epic) {
        return epic.getSubTasks();
    }
}
