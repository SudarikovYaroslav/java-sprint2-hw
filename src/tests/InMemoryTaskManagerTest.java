import org.junit.jupiter.api.BeforeEach;
import main.service.InMemoryHistoryManager;
import main.service.InMemoryTaskManager;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    protected void preparation() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        assertTrue(historyManager.getLastViewedTasks().isEmpty());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        assertTrue(taskManager.getTasksList().isEmpty());
        assertTrue(taskManager.getEpicsList().isEmpty());
        assertTrue(taskManager.getSubTasksList().isEmpty());
    }
}
