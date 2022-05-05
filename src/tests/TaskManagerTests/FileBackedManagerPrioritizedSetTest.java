package TaskManagerTests;

import main.service.FileBackedTaskManager;
import main.service.InMemoryHistoryManager;
import main.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;

/*
Сергей, привет)
- Очень лестный комментарий по поводу реализации тестирования) Но так вроде ж в ТЗ написали сделать, вот я и написал)

    "Чтобы избежать дублирования кода, необходим базовый класс с тестами на каждый метод из интерфейса
    abstract class TaskManagerTest<T extends TaskManager>."

- По поводу T extends TaskManager: так написали в ТЗ делать. Если убрать параметр, получается
  TaskManager taskManager = new FileBackedTaskManager(historyManager, fileBackedPath);
  не может  работать, т.к. там приходится инициализировать и передавать ему в конструктор InMemoryHistoryManager, ну и
  обращаться к нему в последствие, а TaskManager с хистори не может работать. Такой конструктор тоже согласно ТЗ ранее
  делал. Вот и получается, что я для тестов в методе preparation() по разному к работе эти два менеджера подготавливаю
  перед каждым тестом. Я так понял именно из-за этой штуки просят решение с параметром использовать. Хотя я
  вроде чую, куда ты клонишь) - реализации интерфейса без таких допов должны по разному работать, так ведь выходит
  как раз полиморфизм реализуется. Просто нужную реализацию в интерфейс поставили, и должно работать.

  Может есть смысл предложить как то переделать требования ТЗ в предыдущих спринтах, чтобы не возникало этой проблемы?
  А то выходит, делаешь вроде как требуют, а потом такое в итоге((
*/
public class FileBackedManagerPrioritizedSetTest extends TaskManagerPrioritizedSetTest<FileBackedTaskManager> {

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
