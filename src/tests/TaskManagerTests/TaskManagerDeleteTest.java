package TaskManagerTests;

import model.Status;
import model.exceptions.TaskCreateException;
import model.exceptions.TaskDeleteException;
import model.exceptions.TimeIntersectionException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.InMemoryHistoryManager;
import service.managers.TaskManager;
import org.junit.jupiter.api.Test;

import static TaskManagerTests.TaskForTestsGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerDeleteTest<T extends TaskManager> {
    protected InMemoryHistoryManager historyManager;
    protected T taskManager;

    public abstract void preparation();

    @Test
    public void checkDeleteTaskById() throws TaskCreateException, TaskDeleteException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.deleteTaskById(id);
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkDeleteTaskByIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = 123;
        assertNotEquals(id, task.getId());

        TaskDeleteException ex = assertThrows(
                TaskDeleteException.class,
                () -> taskManager.deleteTaskById(id)
        );
        assertEquals("Task с id: " + id + " не существует. Удаление не возможно!", ex.getMessage());
    }

    @Test
    public void checkDeleteEpicById() throws TaskCreateException, TaskDeleteException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();
        taskManager.deleteEpicById(id);
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkDeleteEpicByIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = 123;
        assertNotEquals(id, epic.getId());

        TaskDeleteException ex = assertThrows(
                TaskDeleteException.class,
                () -> taskManager.deleteEpicById(id)
        );
        assertEquals("Epic с id: " + id + " не существует. Удаление не возможно!", ex.getMessage());
    }

    @Test
    public void checkDeleteSubTaskById() throws TaskCreateException, TaskDeleteException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        long id = subTask.getId();
        taskManager.deleteSubTaskById(id);
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteSubTaskByIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        long id = 123;
        assertNotEquals(id, subTask.getId());

        TaskDeleteException ex = assertThrows(
                TaskDeleteException.class,
                () -> taskManager.deleteSubTaskById(id)
        );
        assertEquals("SubTask с id: " + id + " не существует. Удаление не возможно!", ex.getMessage());
    }

    @Test
    public void checkDeleteTaskFunctionDeleteTaskFromTasksList() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void  checkDeleteTaskFunctionWorkWithEmptyTasksList() {
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkDeleteEpicFunctionDeleteEpicFromEpicsList() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkDeleteEpicFunctionWorkWithEmptyTasksList() {
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkDeleteSubTaskFunctionDeleteSubTaskFromEpicsList() throws TaskCreateException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.deleteSubTasks();
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteSubTaskFunctionWorkWithEmptySubTaskList() {
        taskManager.deleteSubTasks();
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
