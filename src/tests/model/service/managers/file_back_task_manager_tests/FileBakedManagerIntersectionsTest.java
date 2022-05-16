package model.service.managers.file_back_task_manager_tests;

import model.service.TaskManagerIntersectionsTest;
import service.managers.FileBackedTaskManager;
import service.managers.InMemoryHistoryManager;
import util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class FileBakedManagerIntersectionsTest extends TaskManagerIntersectionsTest<FileBackedTaskManager> {

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
