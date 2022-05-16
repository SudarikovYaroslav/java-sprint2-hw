package TaskManagerTests;

import service.managers.InMemoryHistoryManager;
import service.managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerCreateTest extends TaskManagerTests
        .TaskManagerCreateTest<InMemoryTaskManager> {
    @BeforeEach
    public void preparation() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        Assertions.assertTrue(historyManager.getLastViewedTasks().isEmpty());
        Assertions.assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        Assertions.assertTrue(taskManager.getTasksList().isEmpty());
        Assertions.assertTrue(taskManager.getEpicsList().isEmpty());
        Assertions.assertTrue(taskManager.getSubTasksList().isEmpty());
    }
}
