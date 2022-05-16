package model.service;

import model.Status;
import model.exceptions.TaskCreateException;
import model.exceptions.TimeIntersectionException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.InMemoryHistoryManager;
import service.managers.TaskManager;
import org.junit.jupiter.api.Test;

import static model.service.TaskForTestsGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerFieldsInitRightTest<T extends TaskManager> {

    protected InMemoryHistoryManager historyManager;
    protected T taskManager;

    protected abstract void preparation();

    @Test
    public void checkEmptyTasksListNotNull() {
        assertNotNull(taskManager.getTasksList());
    }

    @Test
    public void checkStandardTasksListAddedRight() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTasksList().size());
    }

    @Test
    public void checkTasksIdValidatesAndTasksListIsEmpty() {
        Task task = testTaskTemplateGen();
        task.setId(-1);

        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );

        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkEmptyEpicsListNotNull() {
        assertNotNull(taskManager.getEpicsList());
    }

    @Test
    public void checkStandardEpicsListAddedRight() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicsList().size());
    }

    @Test
    public void checkEpicIdValidatesAndEpicsListIsEmpty() {
        Epic epic = testEpicTemplateGen();
        epic.setId(-1);

        assertThrows(TaskCreateException.class, () -> taskManager.createTask(epic));

        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkEmptySubTasksListNotNull() {
        assertNotNull(taskManager.getSubTasksList());
    }

    @Test
    public void checkStandardSubTasksListAddedRight() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        assertEquals(1, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkSubTaskIdValidatesAndSubTasksListIsEmpty() {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        subTask.setId(-1);
        subTask.setEpic(epic);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    private void linkEpicWithSubTask(Epic epic, SubTask subTask) throws TaskCreateException, TimeIntersectionException {
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
    }
}
