import model.exceptions.*;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.FileBackedTaskManager;
import service.IdGenerator;
import service.managers.InMemoryHistoryManager;
import util.Managers;
import util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    private static final int EPIC_POSITION = 1;

    private final String fileBackedPath = Util.getBackedPath();
    private final Path fileBacked = Paths.get(fileBackedPath);
    private InMemoryHistoryManager historyManager;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    private void preparation() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath);
        assertTrue(historyManager.getLastViewedTasks().isEmpty());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        assertTrue(taskManager.getTasksList().isEmpty());
        assertTrue(taskManager.getEpicsList().isEmpty());
        assertTrue(taskManager.getSubTasksList().isEmpty());
    }

    @Test
    public void fillEpicWithSubTaskTest()
            throws TaskCreateException, TaskSaveException, IOException, TaskLoadException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        epic.addSubTask(subTask);
        subTask.setEpic(epic);

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.save();

        String[] savedFileInLines = readSavedFileInLinesArr();
        Epic loadedEpic = taskManager.convertStringToEpic(savedFileInLines[EPIC_POSITION]);

        taskManager.fillEpicWithSubTasks(loadedEpic);
        assertNotEquals(0, loadedEpic.getSubTasks().size());
    }

    @Test
    public void fillEpicWithSubTasksBeforeSubTasksLoad() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        epic.addSubTask(subTask);
        subTask.setEpic(epic);

        taskManager.createEpic(epic);
        TaskLoadException ex = assertThrows(
                TaskLoadException.class,
                () -> taskManager.fillEpicWithSubTasks(epic)
        );
        assertEquals("Не выполнена загрузка SubTask-ов", ex.getMessage());
    }

    @Test
    public void loadHistoryTest() throws TaskCreateException, TaskSaveException, IOException, TaskLoadException,
            TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        taskManager.save();
        String[] savedFileLines = readSavedFileInLinesArr();
        final int HISTORY_POSITION = savedFileLines.length - 1;

        // перезагружаем менеджеры
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath);
        //имитация загрузки задачи
        taskManager.createTask(task);

        String historyInLine = savedFileLines[HISTORY_POSITION];
        taskManager.loadHistory(historyInLine);
        assertNotEquals(0, historyManager.getLastViewedTasks().size());
    }

    @Test
    public void loadHistoryTestWhenLoadingTaskDoesnTExists() throws TaskCreateException, TaskDeleteException,
            TimeIntersectionException {
        Task task = testTaskTemplateGen();
        long id = task.getId();
        taskManager.createTask(task);
        taskManager.getTaskById(id);
        String historyInLine = InMemoryHistoryManager.toString(historyManager);
        taskManager.deleteTaskById(id);

        TaskLoadException ex = assertThrows(
                TaskLoadException.class,
                () -> taskManager.loadHistory(historyInLine)
        );
        assertEquals("Ошибка загрузки истории просмотров id: " + id, ex.getMessage());
    }

    @Test
    public void loadFromFileTest() throws TaskCreateException, TaskSaveException, TaskLoadException,
            TimeIntersectionException {
        Task task = testTaskTemplateGen();
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        subTask.setEpic(epic);
        epic.addSubTask(subTask);
        long taskId = task.getId();
        long epicId = epic.getId();
        long subTaskId = subTask.getId();

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        //добавляем просмотр в историю
        taskManager.getTaskById(taskId);

        taskManager.save();
        taskManager = Managers.loadFromFile(fileBackedPath);

        boolean b = epic.equals(taskManager.getEpicById(epicId));
        assertEquals(task, taskManager.getTaskById(taskId));
        assertEquals(epic, taskManager.getEpicById(epicId));
        assertEquals(subTask, taskManager.getSubTaskById(subTaskId));
        assertNotEquals(0, historyManager.getLastViewedTasks().size());
    }

    private String[] readSavedFileInLinesArr() throws IOException {
        String savedFileInLine = new String(Files.readAllBytes(fileBacked));
        return savedFileInLine.split(FileBackedTaskManager.LINE_DELIMITER);
    }

    private Task testTaskTemplateGen() {
        return new Task("TestTask", "TestDescription",
                IdGenerator.generate());
    }

    private Epic testEpicTemplateGen() {
        return new Epic("TestEpic", "TestEpic description",
                IdGenerator.generate());
    }

    private SubTask testSubTaskTemplateGen() {
        return new SubTask("TestSubTask", "TestSubTask description", IdGenerator.generate());
    }
}
