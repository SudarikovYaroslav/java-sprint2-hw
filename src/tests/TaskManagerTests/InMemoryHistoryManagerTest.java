package TaskManagerTests;

import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskDeleteException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    private void preparation() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    public void addTaskIntoHistoryTest() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        assertEquals(1, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void addTaskWhenNull() {
        Task task = null;
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> historyManager.add(task)
        );
        assertEquals("Нельзя добавить в историю просмотров Task = null !", ex.getMessage());
    }

    @Test
    public void maxHistorySizeTest() throws TaskCreateException, TimeIntersectionException {
        final int maxHistorySize = 10;

        for (int i = 0; i < (maxHistorySize + 1); i++) {
            Task task = testTaskTemplateGen();
            taskManager.createTask(task);
            long id = task.getId();
            taskManager.getTaskById(id);
        }

        assertEquals(maxHistorySize, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void addOneTaskTwiceInHistoryTest() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        taskManager.getTaskById(id);
        assertEquals(1, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void getLastViewedTasksTest() throws TaskCreateException, TimeIntersectionException {
        assertEquals(0, historyManager.getLastViewedTasks().size());
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        assertNotEquals(0, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void getLastViewedTasksWhenHistoryEmptyTest() {
        assertEquals(0, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void removeRemoveTest() throws TaskCreateException, TaskDeleteException, TimeIntersectionException {
        Task task1 = testTaskTemplateGen();
        Task task2 = testTaskTemplateGen();
        Task task3 = testTaskTemplateGen();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        long id1 = task1.getId();
        long id2 = task2.getId();
        long id3 = task3.getId();
        taskManager.getTaskById(id1);
        taskManager.getTaskById(id2);
        taskManager.getTaskById(id3);

        //remove from the middle
        taskManager.deleteTaskById(id2);
        assertFalse(historyManager.getLastViewedTasks().contains(task2));

        //remove from the end
        taskManager.deleteTaskById(id1);
        assertFalse(historyManager.getLastViewedTasks().contains(task1));

        //remove from from the top
        taskManager.deleteTaskById(id3);
        assertFalse(historyManager.getLastViewedTasks().contains(task3));
        assertEquals(0, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void checkHistoryEmptyWhenDeleteEpicContainedSubTasks() throws TaskDeleteException, TaskCreateException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask1 = testSubTaskTemplateGen();
        SubTask subTask2 = testSubTaskTemplateGen();
        subTask1.setEpic(epic);
        subTask2.setEpic(epic);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        long epicId = epic.getId();
        long sub1Id = subTask1.getId();
        long sub2Id = subTask2.getId();
        taskManager.getEpicById(epicId);
        taskManager.getSubTaskById(sub1Id);
        taskManager.getSubTaskById(sub2Id);

        taskManager.deleteEpicById(epicId);
        assertEquals(0, historyManager.getLastViewedTasks().size());
    }

    private Task testTaskTemplateGen() {
        return new Task("TestTask", "TestDescription",
                IdGenerator.generate());
    }

    private Epic testEpicTemplateGen() {
        return new Epic("TestEpic", "TestEpic description",
                IdGenerator.generate());
    }

    private SubTask testSubTaskTemplateGen() {
        return new SubTask("TestSubTask", "TestSubTask description", IdGenerator.generate());
    }
}
