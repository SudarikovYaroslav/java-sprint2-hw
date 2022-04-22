import org.junit.jupiter.api.BeforeEach;
import service.FileBackedTaskManager;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import util.Util;

import java.nio.file.Path;

public class FileBackedTaskManagerTest extends TaskManagerTest {

    private final Path fileBacked = Util.getBacked();

    @BeforeEach
    protected void preparation() {
        historyManager = new InMemoryHistoryManager();
        idGenerator = IdGenerator.getInstance();
        taskManager = new FileBackedTaskManager(historyManager, fileBacked, idGenerator);
    }
}
