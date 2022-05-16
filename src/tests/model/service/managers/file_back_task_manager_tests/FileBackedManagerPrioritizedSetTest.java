package model.service.managers.file_back_task_manager_tests;

import model.service.TaskManagerPrioritizedSetTest;
import service.managers.FileBackedTaskManager;
import service.managers.InMemoryHistoryManager;
import util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

// Сергей, попробовал с клонировать с гитхаба проект, добавил библиотеки - у меня норм все тесты компилируются :\
// Может от версии идеи зависит? Но я всё равно добавил на всякий TaskManagerTests. где наследование есть в тестах
public class FileBackedManagerPrioritizedSetTest extends TaskManagerPrioritizedSetTest<FileBackedTaskManager> {

    private final String fileBackedPath = Util.getBackedPath();

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
