package TaskManagerTests;

import main.model.Status;
import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.InMemoryHistoryManager;
import main.service.TaskManager;
import org.junit.jupiter.api.Test;

import static main.service.TaskForTestsGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerCreateTest<T extends TaskManager> {
    protected InMemoryHistoryManager historyManager;
    protected T taskManager;

    public abstract void preparation();

    @Test
    public void checkCreateTaskStandard() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTasksList().size());
        assertEquals(task, taskManager.getTasksList().get(0));
    }

    @Test
    public void checkCreateTaskWithNullValue() {
        Task task = null;
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );
        assertEquals("При создании task == null", ex.getMessage());
    }

    @Test
    public void checkCreateTaskWithIncorrectId() {
        Task task = testTaskTemplateGen();
        task.setId(0);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );
        assertEquals("При создании Task id должен быть больше 0. Actual: " + task.getId(), ex.getMessage());

        task.setId(-1);
        ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );
        assertEquals("При создании Task id должен быть больше 0. Actual: " + task.getId(), ex.getMessage());
    }

    @Test
    public void checkCreateEpicStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicsList().size());
        assertEquals(epic, taskManager.getEpicsList().get(0));
    }

    @Test
    public void checkCreateEpicWithNullValue() {
        Epic epic = null;
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createEpic(epic)
        );
        assertEquals("При создании Epic == null", ex.getMessage());
    }

    @Test
    public void checkCreateEpicWithIncorrectId() {
        Epic epic = testEpicTemplateGen();
        epic.setId(0);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createEpic(epic)
        );
        assertEquals("При создании Epic id должен быть больше 0. Actual: " + epic.getId(), ex.getMessage());

        epic.setId(-1);
        ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createEpic(epic)
        );
        assertEquals("При создании Epic id должен быть больше 0. Actual: " + epic.getId(), ex.getMessage());
    }

    @Test
    public void checkCreateSubTaskStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        assertEquals(1, taskManager.getSubTasksList().size());
        assertEquals(subTask, taskManager.getSubTasksList().get(0));
    }

    @Test
    public void checkCreateSubTaskWithNullValue() {
        SubTask subTask = null;
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals("При создании SubTask == null", ex.getMessage());
    }

    @Test
    public void checkCreateSubTaskWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);

        subTask.setId(0);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals("При создании SubTask id должен быть больше 0. Actual: "
                + subTask.getId(), ex.getMessage()
        );

        subTask.setId(-1);
        ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals("При создании SubTask id должен быть больше 0. Actual: "
                + subTask.getId(), ex.getMessage()
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
