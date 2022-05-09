package main.util;

import main.model.TaskTypes;
import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskLoadException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {

    private Managers(){};

    private static final InMemoryHistoryManager historyManger = new InMemoryHistoryManager();
    private static final InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManger);

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return new FileBackedTaskManager(historyManger, Util.getBackedPath());
    }

    public static HistoryManager getDefaultHistory() {
        return historyManger;
    }

    public static FileBackedTaskManager loadFromFile(String tasksFilePath) throws TaskLoadException {
        if (tasksFilePath == null) throw new TaskLoadException("Не указан файл для загрузки");
        Path tasksFile = Paths.get(tasksFilePath);
        if (!Files.exists(tasksFile)) throw new TaskLoadException("Указанный файл для загрузки не существует");

        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(historyManager, tasksFilePath);

        try {
            String mixedLine = new String(Files.readAllBytes(tasksFile));
            String[] tasksAndHistoryLines = mixedLine.split(FileBackedTaskManager.EMPTY_LINE_DELIMITER);
            String allTasksInLine = tasksAndHistoryLines[FileBackedTaskManager.ALL_TASKS_IN_LINE_INDEX];

            String historyInLine = null;
            if (isHistoryPresent(tasksAndHistoryLines)) {
                historyInLine = tasksAndHistoryLines[FileBackedTaskManager.HISTORY_IN_LINE_INDEX];
            }

            String[] tasksLines = allTasksInLine.split(FileBackedTaskManager.LINE_DELIMITER);

            String metaLine = tasksLines[FileBackedTaskManager.META_LINE_INDEX];
            String[] metaData = metaLine.split(FileBackedTaskManager.META_LINE_DELIMITER);
            long currentIdValue = Util.getIdFromString(metaData[FileBackedTaskManager.CURRENT_ID_INDEX],
                    "неверный формат id при загрузке текущего значения id");
            IdGenerator.setStartIdValue(currentIdValue);

            for (int i = 1; i < tasksLines.length; i++) {
                String taskInLine = tasksLines[i];
                String[] fields = taskInLine.split(FileBackedTaskManager.TASK_IN_LINE_DELIMITER);
                String taskType = fields[FileBackedTaskManager.TYPE_COLUMN_INDEX];

                //первичная загрузка задач
                try {
                    switch (TaskTypes.valueOf(taskType)) {
                        case TASK:
                            Task task = taskManager.convertStringToTask(taskInLine);
                            taskManager.createTask(task);
                            break;
                        case EPIC:
                            Epic epic = taskManager.convertStringToEpic(taskInLine);
                            taskManager.createEpic(epic);
                            break;
                        case SUB_TASK:
                            SubTask subTask = taskManager.convertStringToSubTask(taskInLine);
                            taskManager.createSubTask(subTask);
                            break;
                    }
                } catch (TaskCreateException | TimeIntersectionException e) {
                    e.printStackTrace();
                }
            }

            //догружаем все epic-и до валидного состояния
            for (Epic epic : taskManager.getEpicsList()) {
                try {
                    taskManager.fillEpicWithSubTasks(epic);
                } catch (TaskLoadException e) {
                    e.printStackTrace();
                }
            }

            //заполняем историю просмотров
            if (historyInLine != null) {
                taskManager.loadHistory(historyInLine);
            }

        } catch (IOException e) {
            throw new TaskLoadException("Ошибка при загрузке резервной копии", e);
        }

        return taskManager;
    }

    private static boolean isHistoryPresent(String[] tasksAndHistoryLines) {
        return tasksAndHistoryLines.length == 2;
    }
}
