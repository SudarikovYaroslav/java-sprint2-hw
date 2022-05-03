package TaskManagerTests;

import main.service.FileBackedTaskManager;
import main.service.InMemoryHistoryManager;
import main.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;

public class FileBackedTaskManagerCreateTest extends TaskManagerCreateTest<FileBackedTaskManager> {
    private final Path fileBackedPath = Util.getBackedPath();

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
