import org.junit.jupiter.api.BeforeEach;
import main.service.IdGenerator;
import main.service.InMemoryHistoryManager;
import main.service.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    protected void preparation() {
        historyManager = new InMemoryHistoryManager();
        idGenerator = IdGenerator.getInstance();
        taskManager = new InMemoryTaskManager(historyManager);
    }

}
