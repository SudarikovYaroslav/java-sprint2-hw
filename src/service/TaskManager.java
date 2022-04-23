package service;

import model.exceptions.TaskCreateException;
import model.exceptions.TaskUpdateException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;

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

    void deleteTaskById(long id);

    void deleteEpicById(long id);

    void deleteSubTaskById(long id);

    List<SubTask> getSubTasks(Epic epic);
}
