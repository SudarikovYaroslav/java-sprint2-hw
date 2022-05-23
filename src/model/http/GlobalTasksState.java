package model.http;

import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.TaskManager;

import java.util.List;
import java.util.Objects;

/**
 * GlobalTasksState - класс-контейнер. Он может сохранить состояние объекта TaskManager, и может быть
 * сереализован в Json для сохранения состояния менеджера задач. А также может быть использован при работе с
 * HttpTaskManager чтобы задать нужное состояние при загрузке, c помощью метода
 * setManagerCondition(HttpTaskManagerCondition condition)
 */
public class GlobalTasksState {
    protected final List<Task> tasks;
    protected final List<Epic> epics;
    protected final List<SubTask> subTasks;
    private final List<Task> lastViewedTasks;

    public GlobalTasksState(TaskManager taskManager) {
        tasks = taskManager.getTasksList();
        epics = taskManager.getEpicsList();
        subTasks = taskManager.getSubTasksList();
        lastViewedTasks = taskManager.history();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Epic> getEpics() {
        return epics;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public List<Task> getLastViewedTasks() {
        return lastViewedTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalTasksState that = (GlobalTasksState) o;
        return Objects.equals(tasks, that.tasks)
                && Objects.equals(epics, that.epics)
                && Objects.equals(subTasks, that.subTasks)
                && Objects.equals(lastViewedTasks, that.lastViewedTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, epics, subTasks, lastViewedTasks);
    }
}
