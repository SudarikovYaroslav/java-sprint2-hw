package main.service;

import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskDeleteException;
import main.model.exceptions.TaskUpdateException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;

import java.util.List;
import java.util.Set;

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

    void createTask(Task task) throws TaskCreateException, TimeIntersectionException;

    void createEpic(Epic epic) throws TaskCreateException, TimeIntersectionException;

    void createSubTask(SubTask subTask) throws TaskCreateException, TimeIntersectionException;

    /**
     * Method gets new version of task with right id as parameter
     */
    void updateTask(Task task) throws TaskUpdateException, TimeIntersectionException;

    /**
     * Method gets new version of epic with right id as parameter
     */
    void updateEpic(Epic epic) throws TaskUpdateException, TimeIntersectionException;

    /**
     * Method gets new version of SubTask with right id as parameter
     */
    void updateSubTask(SubTask subTask) throws TaskUpdateException, TimeIntersectionException;

    void deleteTaskById(long id) throws TaskDeleteException;

    void deleteEpicById(long id) throws TaskDeleteException;

    void deleteSubTaskById(long id) throws TaskDeleteException;

    List<SubTask> getSubTasks(Epic epic);

    Set<Task> getPrioritizedTasks();

    // Сергей, привет)
    // В спринте, где нужно было историю просмотров добавить, в тз просили сделать тут метод history(),
    // но ты мне тогда написал, что лучше чтобы в менеджере не было методов о другом интерфейсе, и мы малость по-другому
    // сделали. А в этом ТЗ Просят от интерфейса TaskManager историю через эндпоинты получить.
    // По этому решил все-так обратно его прикрутить
    List<Task> history();
}
