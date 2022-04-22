import org.junit.jupiter.api.BeforeEach;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    protected void preparation() {
        historyManager = new InMemoryHistoryManager();
        idGenerator = IdGenerator.getInstance();
        taskManager = new InMemoryTaskManager(historyManager);
    }

}
