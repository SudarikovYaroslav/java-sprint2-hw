package main.service;

import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskDeleteException;
import main.model.exceptions.TaskUpdateException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<SubTask> getSubTasksList();

    void deleteTasks();

    void deleteEpics();

    void deleteSubTasks();

    Task getTaskById(long id);

    Epic getEpicById(long id);

    SubTask getSubTaskById(long id);

    void createTask(Task task) throws TaskCreateException;

    void createEpic(Epic epic) throws TaskCreateException;

    void createSubTask(SubTask subTask) throws TaskCreateException;

    /**
     * Method gets new version of task with right id as parameter
     */
    void updateTask(Task task) throws TaskUpdateException;

    /**
     * Method gets new version of epic with right id as parameter
     */
    void updateEpic(Epic epic) throws TaskUpdateException;

    /**
     * Method gets new version of SubTask with right id as parameter
     */
    void updateSubTask(SubTask subTask) throws TaskUpdateException;

    void deleteTaskById(long id) throws TaskDeleteException;

    void deleteEpicById(long id) throws TaskDeleteException;

    void deleteSubTaskById(long id) throws TaskDeleteException;

    List<SubTask> getSubTasks(Epic epic);
}
