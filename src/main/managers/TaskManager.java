package main.managers;

import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

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

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    /**
     * Method gets new version of task with right id as parameter
     */
    void updateTask(Task task);

    /**
     * Method gets new version of epic with right id as parameter
     */
    void updateEpic(Epic epic);

    /**
     * Method gets new version of SubTask with right id as parameter
     */
    void updateSubTask(SubTask subTask);

    void deleteTaskById(long id);

    void deleteEpicById(long id);

    void deleteSubTaskById(long id);

    List<SubTask> getSubTasks(Epic epic);

    long generatedId();

    List<Task> history();
}
