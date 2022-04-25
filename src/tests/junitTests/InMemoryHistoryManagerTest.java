import model.exceptions.TaskCreateException;
import model.exceptions.TaskDeleteException;
import model.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private IdGenerator idGenerator;
    private HistoryManager historyManager;

    @BeforeEach
    private void preparation() {
        idGenerator = IdGenerator.getInstance();
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    public void addTaskIntoHistoryTest() throws TaskCreateException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        assertEquals(1, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void addOneTaskTwiceInHistoryTest() throws TaskCreateException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        taskManager.getTaskById(id);
        assertEquals(1, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void getLastViewedTasksTest() throws TaskCreateException {
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
    public void removeRemoveTest() throws TaskCreateException, TaskDeleteException {
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

    private Task testTaskTemplateGen() {
        return new Task("TestTask", "TestDescription", idGenerator);
    }
}
