package TaskManagerTests;

import model.exceptions.TaskCreateException;
import model.exceptions.TaskLoadException;
import model.exceptions.TaskSaveException;
import model.exceptions.TimeIntersectionException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.FileBackedTaskManager;
import service.managers.InMemoryHistoryManager;
import util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static TaskManagerTests.TaskForTestsGenerator.*;

public class FileBackedTaskManagerFieldsInitRightTest extends TaskManagerTests
        .TaskManagerFieldsInitRightTest<FileBackedTaskManager> {

    /**
     * При пустой истории и отсутствии задач в файл записываются 2 строки: мета-строка и строка разделитель
     */
    private static final int EXPECTED_LINES_FROM_FILE_COUNT = 2;

    private final String fileBackedPath = Util.getBackedPath();

    @BeforeEach
    protected void preparation() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath);
        Assertions.assertTrue(historyManager.getLastViewedTasks().isEmpty());
        Assertions.assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        Assertions.assertTrue(taskManager.getTasksList().isEmpty());
        Assertions.assertTrue(taskManager.getEpicsList().isEmpty());
        Assertions.assertTrue(taskManager.getSubTasksList().isEmpty());
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
        String dangerousPath = "Some doesn't exists path";
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
        Path fileBaked = Paths.get(fileBackedPath);
        String savedFileInLine = new String(Files.readAllBytes(fileBaked));
        return savedFileInLine.split(FileBackedTaskManager.LINE_DELIMITER);
    }
}
