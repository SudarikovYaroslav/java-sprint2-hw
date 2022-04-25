import model.exceptions.TaskCreateException;
import model.exceptions.TaskLoadException;
import model.exceptions.TaskSaveException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoadTests {

    /**
     * При пустой истории и отсутствии задач в файл записывается мета-строка и строка разделитель
     */
    private static final int EMPTY_BACKED_LINES = 2;
    private static final int EPIC_POSITION = 1;

    private final Path fileBackedPath = Util.getBacked();
    private InMemoryHistoryManager historyManager;
    private FileBackedTaskManager taskManager;
    private IdGenerator idGenerator;

    @BeforeEach
    private void preparation() {
        historyManager = new InMemoryHistoryManager();
        idGenerator = IdGenerator.getInstance();
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath, idGenerator);
    }

    @Test
    public void saveNoTasksAndEmptyHistoryTest() throws TaskSaveException, IOException {
        taskManager.save();
        String[] savedFileInLines = readSavedFileInLinesArr();
        assertEquals(EMPTY_BACKED_LINES, savedFileInLines.length);
    }

    @Test
    public void saveWithEmptyEpicTest() throws TaskCreateException, TaskSaveException, IOException {
        Epic epic = testEpicTemplateGen();
        taskManager.createTask(epic);
        taskManager.save();

        String[] savedFileInLines = readSavedFileInLinesArr();
        assertEquals(EMPTY_BACKED_LINES + 1, savedFileInLines.length);
    }

    @Test
    public void saveTestWithOneTaskAndNotEmptyHistory() throws TaskCreateException, TaskSaveException, IOException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        taskManager.save();
        int TASK_AND_NOT_EMPTY_HISTORY_SIZE = 2;
        String[] savedFileInLines = readSavedFileInLinesArr();
        assertEquals(EMPTY_BACKED_LINES + TASK_AND_NOT_EMPTY_HISTORY_SIZE, savedFileInLines.length);
    }

    @Test
    public void convertStringToTaskTest() {
        Task task = testTaskTemplateGen();
        String taskLine = task.toString();
        Task convertedTask = taskManager.convertStringToTask(taskLine);
        assertEquals(task, convertedTask);
    }

    @Test
    public void convertStringToEpicTest() {
        Epic epic = testEpicTemplateGen();
        String epicLine = epic.toString();
        Epic convertedEpic = taskManager.convertStringToEpic(epicLine);
        assertEquals(epic, convertedEpic);
    }

    @Test
    public void convertStringToSubTaskTest() throws TaskLoadException, TaskCreateException {
        SubTask subTask = testSubTaskTemplateGen();
        Epic epic = testEpicTemplateGen();
        epic.addSubTask(subTask);
        subTask.setEpic(epic);
        String subTaskLine = subTask.toString();
        taskManager.createEpic(epic);
        SubTask convertedSubTask = taskManager.convertStringToSubTask(subTaskLine);
        assertEquals(subTask, convertedSubTask);
    }

    @Test
    public void fillEpicWithSubTaskTest()
            throws TaskCreateException, TaskSaveException, IOException, TaskLoadException {
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
    public void loadHistoryTest() throws TaskCreateException, TaskSaveException, IOException, TaskLoadException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        taskManager.save();
        String[] savedFileLines = readSavedFileInLinesArr();
        final int HISTORY_POSITION = savedFileLines.length - 1;

        // перезагружаем менеджеры
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath, idGenerator);
        //имитация загрузки задачи
        taskManager.createTask(task);

        String historyInLine = savedFileLines[HISTORY_POSITION];
        taskManager.loadHistory(historyInLine);
        assertNotEquals(0, historyManager.getLastViewedTasks().size());
    }

    private String[] readSavedFileInLinesArr() throws IOException {
        String savedFileInLine = new String(Files.readAllBytes(fileBackedPath));
        return savedFileInLine.split(FileBackedTaskManager.LINE_DELIMITER);
    }

    private Task testTaskTemplateGen() {
        return new Task("TestTask", "TestDescription", idGenerator);
    }

    private Epic testEpicTemplateGen() {
        return new Epic("TestEpic", "TestEpic description", idGenerator);
    }

    private SubTask testSubTaskTemplateGen() {
        return new SubTask("TestSubTask", "TestSubTask description", idGenerator);
    }
}
