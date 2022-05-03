import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskLoadException;
import main.model.exceptions.TaskSaveException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import main.service.FileBackedTaskManager;
import main.service.InMemoryHistoryManager;
import main.util.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    /**
     * При пустой истории и отсутствии задач в файл записываются 2 строки: мета-строка и строка разделитель
     */
    private static final int EXPECTED_LINES_FROM_FILE_COUNT = 2;

    private final Path fileBackedPath = Util.getBackedPath();

    @BeforeEach
    protected void preparation() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath);
    }

    @Test
    public void saveNoTasksAndEmptyHistoryTest() throws TaskSaveException, IOException {
        taskManager.save();
        String[] savedFileInLines = readSavedFileInLinesArr();
        assertEquals(EXPECTED_LINES_FROM_FILE_COUNT, savedFileInLines.length);
    }

    @Test
    public void saveWithEmptyEpicTest() throws TaskCreateException, TaskSaveException, IOException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createTask(epic);
        taskManager.save();

        String[] savedFileInLines = readSavedFileInLinesArr();
        assertEquals(EXPECTED_LINES_FROM_FILE_COUNT + 1, savedFileInLines.length);
    }

    @Test
    public void saveTestWithOneTaskAndNotEmptyHistory() throws TaskCreateException, TaskSaveException, IOException,
            TimeIntersectionException {
        final int expectedHistoryLineCount = 1;
        final int expectedNewTaskLineCount = 1;
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.getTaskById(id);
        taskManager.save();
        String[] savedFileInLines = readSavedFileInLinesArr();
        assertEquals(EXPECTED_LINES_FROM_FILE_COUNT
                + expectedHistoryLineCount + expectedNewTaskLineCount, savedFileInLines.length);
    }

    @Test
    public void saveWithInvalidPathTest() {
        Path dangerousPath = Paths.get("Some doesn't exists path");
        FileBackedTaskManager dangerousTaskManager = new FileBackedTaskManager(historyManager, dangerousPath);
        TaskSaveException ex = assertThrows(
                TaskSaveException.class,
                dangerousTaskManager::save
        );
        assertEquals("Указанный файл для записи не существует", ex.getMessage());
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
    public void convertStringToSubTaskTest() throws TaskLoadException, TaskCreateException, TimeIntersectionException {
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
    public void convertStringToSubTaskWithNullEpicTest() {
        String subTaskLine = "SUB_TASK,1,TestSubTask,TestSubTask description,NEW,null,null,2";
        long id = 1L;
        TaskLoadException ex = assertThrows(
                TaskLoadException.class,
                () -> taskManager.convertStringToSubTask(subTaskLine)
        );
        assertEquals("null epic при загрузке SubTask id: " + id, ex.getMessage());
    }

    private String[] readSavedFileInLinesArr() throws IOException {
        String savedFileInLine = new String(Files.readAllBytes(fileBackedPath));
        return savedFileInLine.split(FileBackedTaskManager.LINE_DELIMITER);
    }
}
