package TaskManagerTests;

import service.managers.FileBackedTaskManager;
import service.managers.InMemoryHistoryManager;
import util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class FileBackedTaskManagerGetTaskTest extends TaskManagerTests
        .TaskManagerGetTaskTest<FileBackedTaskManager> {
    private final String fileBackedPath = Util.getBackedPath();

    @BeforeEach
    public void preparation() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath);
        Assertions.assertTrue(historyManager.getLastViewedTasks().isEmpty());
        Assertions.assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        Assertions.assertTrue(taskManager.getTasksList().isEmpty());
        Assertions.assertTrue(taskManager.getEpicsList().isEmpty());
        Assertions.assertTrue(taskManager.getSubTasksList().isEmpty());
    }
}
