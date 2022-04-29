package service;

import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskLoadException;
import main.model.exceptions.TaskSaveException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import main.service.FileBackedTaskManager;
import main.service.IdGenerator;
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
    private static final int EMPTY_BACKED_LINES = 2;
    private static final int EPIC_POSITION = 1;

    private final Path fileBackedPath = Util.getBackedPath();
    private FileBackedTaskManager backedTaskManager;

    @BeforeEach
    protected void preparation() {
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
    public void saveWithEmptyEpicTest() throws TaskCreateException, TaskSaveException, IOException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createTask(epic);
        taskManager.save();

        String[] savedFileInLines = readSavedFileInLinesArr();
        assertEquals(EMPTY_BACKED_LINES + 1, savedFileInLines.length);
    }

    @Test
    public void saveTestWithOneTaskAndNotEmptyHistory() throws TaskCreateException, TaskSaveException, IOException,
            TimeIntersectionException {
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
    public void saveWithInvalidPathTest() {
        Path dangerousPath = Paths.get("Some doesn't exists path");
        FileBackedTaskManager dangerousTaskManager = new FileBackedTaskManager(
                historyManager,
                dangerousPath,
                idGenerator
        );
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
