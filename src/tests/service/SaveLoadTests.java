package service;

import main.model.exceptions.*;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.service.FileBackedTaskManager;
import main.service.IdGenerator;
import main.service.InMemoryHistoryManager;
import main.util.Managers;
import main.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/*
 Методы этого класса должны были находиться в классе FileInBackedTaskManagerTest!,
 но из за Error java can not find symbol class: test.service.TaskManagerTest, возникающей по непонятной причине при малейших
 изменениях в классе FileInBackedTaskManagerTest, ДАЖЕ ЕСЛИ ПРОСТО УБРАТЬ ЛИШНЮЮ ПУСТУЮ СТРОКУ,
 пришлось вынести эти тесты в отдельный класс. Причину возникновения ошибки установить не удалось. При её возникновении
 переставала компилироваться вся программа, по этой же причине в дженерик классах менеджеров не указан тип-параметр так
 как это тоже приводит к этой ошибке. Причём она не всегда исправляется даже если сделать откат через гит с помощью
 git reset --hard на коммит, где только что всё исправно работало.
 */
public class SaveLoadTests {

    /**
     * При пустой истории и отсутствии задач в файл записывается мета-строка и строка разделитель
     */
    private static final int EMPTY_BACKED_LINES = 2;
    private static final int EPIC_POSITION = 1;

    private Path fileBackedPath = Util.getBackedPath();
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
        taskManager = new FileBackedTaskManager(historyManager, fileBackedPath, idGenerator);
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
