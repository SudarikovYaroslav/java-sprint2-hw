package service;

import org.junit.jupiter.api.BeforeEach;
import main.service.FileBackedTaskManager;
import main.service.IdGenerator;
import main.service.InMemoryHistoryManager;
import main.util.Util;

import java.nio.file.Path;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private final Path fileBacked = Util.getBackedPath();

    @BeforeEach
    protected void preparation() {
        historyManager = new InMemoryHistoryManager();
        idGenerator = IdGenerator.getInstance();
        taskManager = new FileBackedTaskManager(historyManager, fileBacked, idGenerator);
    }
}