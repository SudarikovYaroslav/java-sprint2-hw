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

import java.util.List;

import static model.service.TaskForTestsGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerGetTaskTest <T extends TaskManager> {

    protected InMemoryHistoryManager historyManager;
    protected T taskManager;

    protected abstract void preparation();

    @Test
    public void checkGetTaskByIdStandard() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long controlId = task.getId();
        assertEquals(task, taskManager.getTaskById(controlId));
    }

    @Test
    public void checkGetTaskByIdWhenTasksEmpty() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetTaskByIdWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long testId = 2L;
        assertNotEquals(task.getId(), testId);
        assertEquals(1, taskManager.getTasksList().size());
        assertNull(taskManager.getTaskById(testId));
    }

    @Test
    public void checkGetEpicByIdStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();
        assertEquals(epic, taskManager.getEpicById(id));
    }

    @Test
    public void checkGetEpicByIdWhenEpicsEmpty() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetEpicByIdWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long testId = 2L;
        assertNotEquals(testId, epic.getId());
        assertEquals(1, taskManager.getEpicsList().size());
        assertNull(taskManager.getEpicById(testId));
    }

    @Test
    public void checkGetSubTaskByIdStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        long id  = subTask.getId();
        assertEquals(subTask, taskManager.getSubTaskById(id));
    }

    @Test
    public void checkGetSubTaskByIdWhenSubTasksEmpty() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetSubTaskByIdWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic,subTask);
        long testId = 2L;
        assertNotEquals(testId, subTask.getId());
        assertNull(taskManager.getSubTaskById(testId));
    }

    @Test
    public void checkGetSubTasks() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);

        List<SubTask> epicsSubTasks = taskManager.getSubTasks(epic);
        assertEquals(subTask.getId(), epicsSubTasks.get(0).getId());

        epic = testEpicTemplateGen();
        assertEquals(0, taskManager.getSubTasks(epic).size());
    }

    @Test
    public void checkGetSubTasksFromNullEpic() {
        Epic epic = null;

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.getSubTasks(epic)
        );

        assertEquals("Epic = null! при попытке getSubTasks()", ex.getMessage());
    }

    private void linkEpicWithSubTask(Epic epic, SubTask subTask) throws TaskCreateException, TimeIntersectionException {
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
    }
}
