package TaskManagerTests;

import model.Status;
import model.exceptions.TaskCreateException;
import model.exceptions.TaskUpdateException;
import model.exceptions.TimeIntersectionException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.InMemoryHistoryManager;
import service.managers.TaskManager;
import org.junit.jupiter.api.Test;

import static TaskManagerTests.TaskForTestsGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerUpdateTest<T extends TaskManager> {
    protected InMemoryHistoryManager historyManager;
    protected T taskManager;

    protected abstract void preparation();

    @Test
    public void checkUpdateTaskStandard() throws TaskCreateException, TaskUpdateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();

        Task updatedTask = testInProgressTaskTemplateGen();
        updatedTask.setId(id);
        taskManager.updateTask(updatedTask);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(id).getStatus());
    }

    @Test
    public void checkUpdateTaskNull() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);

        Task updatedTask = null;
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () -> taskManager.updateTask(updatedTask)
        );

        assertEquals("Обновляемая Task = null", ex.getMessage());
    }

    @Test
    public void checkUpdateTaskWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);

        Task updatedTask = testInProgressTaskTemplateGen();
        updatedTask.setId(0);

        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateTask(updatedTask)
        );
        assertEquals("id обновляемой Task должен быть больше 0! Actual: " + updatedTask.getId(),
                ex.getMessage()
        );

        updatedTask.setId(-1);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateTask(updatedTask)
        );
        assertEquals("id обновляемой Task должен быть больше 0! Actual: " + updatedTask.getId(),
                ex.getMessage()
        );

        long id = 123;
        assertNotEquals(task.getId(), id);
        updatedTask.setId(id);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateTask(updatedTask)
        );
        assertEquals("Task с id: " + updatedTask.getId() + " не существует. Обновление невозможно!",
                ex.getMessage()
        );
    }

    @Test
    public void checkUpdateEpicStandard() throws TaskCreateException, TaskUpdateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();

        Epic updatedEpic = testInProgressEpicTemplateGen();
        updatedEpic.setId(id);
        taskManager.updateEpic(updatedEpic);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(id).getStatus());
    }

    @Test
    public void checkUpdateEpicNull() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);

        Epic updatedEpic = null;
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () -> taskManager.updateEpic(updatedEpic)
        );

        assertEquals("Обновляемый Epic = null", ex.getMessage());
    }

    @Test
    public void checkUpdateEpicWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);

        Epic updatedEpic = testInProgressEpicTemplateGen();
        updatedEpic.setId(0);

        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateEpic(updatedEpic)
        );
        assertEquals("id обновляемого Epic должен быть больше 0! Actual: " + updatedEpic.getId(),
                ex.getMessage()
        );

        updatedEpic.setId(-1);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateEpic(updatedEpic)
        );
        assertEquals("id обновляемого Epic должен быть больше 0! Actual: " + updatedEpic.getId(),
                ex.getMessage()
        );

        long id = 123;
        assertNotEquals(epic.getId(), id);
        updatedEpic.setId(id);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateEpic(updatedEpic)
        );
        assertEquals("Epic с id: " + updatedEpic.getId() + " не существует. Обновление невозможно!",
                ex.getMessage()
        );
    }

    @Test
    public void checkUpdateSubTaskStandard() throws TaskCreateException, TaskUpdateException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask  = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        long id = subTask.getId();

        SubTask updatedSubTask = testInProgressSubTaskTemplateGen();
        epic.addSubTask(updatedSubTask);
        updatedSubTask.setEpic(epic);
        updatedSubTask.setId(id);
        taskManager.updateSubTask(updatedSubTask);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubTaskById(id).getStatus());
    }

    @Test
    public void checkUpdateSubTaskNull() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        SubTask updatedSubTask = null;
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () -> taskManager.updateSubTask(updatedSubTask)
        );

        assertEquals("Обновляемая SubTask = null", ex.getMessage());
    }

    @Test
    public void checkUpdateSubTaskWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        SubTask updatedSubTask = testInProgressSubTaskTemplateGen();
        updatedSubTask.setEpic(epic);

        updatedSubTask.setId(0);
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateSubTask(updatedSubTask)
        );
        assertEquals("id обновляемой SubTask должен быть больше 0! Actual: " + updatedSubTask.getId(),
                ex.getMessage()
        );

        updatedSubTask.setId(-1);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateSubTask(updatedSubTask)
        );
        assertEquals("id обновляемой SubTask должен быть больше 0! Actual: " + updatedSubTask.getId(),
                ex.getMessage()
        );

        long id = 123;
        assertNotEquals(subTask.getId(), id);
        updatedSubTask.setId(id);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateSubTask(updatedSubTask)
        );
        assertEquals("SubTask с id: " + updatedSubTask.getId() + " не существует. Обновление невозможно!",
                ex.getMessage()
        );
    }

    private void linkEpicWithSubTask(Epic epic, SubTask subTask) throws TaskCreateException, TimeIntersectionException {
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
    }
}
