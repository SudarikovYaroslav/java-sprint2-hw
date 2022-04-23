package service;

import model.Status;
import model.exceptions.TaskCreateException;
import model.exceptions.TaskLoadException;
import model.exceptions.TaskSaveException;
import model.exceptions.TaskUpdateException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static util.Util.getIdFromString;
import static util.Util.getStatusFromString;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    public static final String LINE_DELIMITER = "\n";
    public static final String EMPTY_LINE_DELIMITER = " \n";
    public static final String META_LINE_DELIMITER = "#";
    public static final String TASK_IN_LINE_DELIMITER = ",";
    public static final String IDS_DELIMITER = "\\.";
    public static final int ALL_TASKS_IN_LINE_INDEX = 0;
    public static final int HISTORY_IN_LINE_INDEX = 1;
    public static final int META_LINE_INDEX = 0;
    public static final int CURRENT_ID_INDEX = 1;
    public static final int TYPE_COLUMN_INDEX = 0;
    public static final int ID_COLUMN_INDEX = 1;
    public static final int NAME_COLUMN_INDEX = 2;
    public static final int DESCRIPTION_COLUMN_INDEX = 3;
    public static final int STATUS_COLUMN_INDEX = 4;
    public static final int IDS_COLUMN_INDEX = 5;

    private final Path fileBacked;
    private final IdGenerator idGenerator;

    public FileBackedTaskManager(HistoryManager historyManager, Path fileBacked, IdGenerator idGenerator) {
        super(historyManager);
        this.fileBacked = fileBacked;
        this.idGenerator = idGenerator;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task getTaskById(long id) {
        Task result = super.getTaskById(id);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic result = super.getEpicById(id);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask result = super.getSubTaskById(id);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void createTask(Task task) throws TaskCreateException {
        super.createTask(task);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createEpic(Epic epic) throws TaskCreateException {
        super.createEpic(epic);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createSubTask(SubTask subTask) throws TaskCreateException {
        super.createSubTask(subTask);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTask(Task task) throws TaskUpdateException {
        super.updateTask(task);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEpic(Epic epic) throws TaskUpdateException {
        super.updateEpic(epic);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) throws TaskUpdateException {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (TaskSaveException e) {
            e.printStackTrace();
        }
    }

    public void save() throws TaskSaveException {
        if (!Files.exists(fileBacked)) throw new TaskSaveException("Указанный файл для записи не существует");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileBacked.toFile()))) {
            writer.write("type,id,name,description,status,id...,#" +
                    idGenerator.peekCurrentIdValue() + LINE_DELIMITER);

            for (Task task : tasks.values()) {
                writer.write(task.toString() + LINE_DELIMITER);
            }

            for (Epic epic : epics.values()) {
                writer.write(epic.toString() + LINE_DELIMITER);
            }

            for (SubTask subTask : subTasks.values()) {
                writer.write(subTask.toString() + LINE_DELIMITER);
            }

            writer.write(EMPTY_LINE_DELIMITER);
            writer.write(InMemoryHistoryManager.toString(historyManager));

        } catch (IOException e) {
            throw new TaskSaveException("Ошибка при сохранении данных");
        }
    }

    public Task convertStringToTask(String taskInLine) {
        String[] taskFields = taskInLine.split(TASK_IN_LINE_DELIMITER);
        long id = getIdFromString(taskFields[ID_COLUMN_INDEX], "Неверный формат id при загрузке Task");
        String name = taskFields[NAME_COLUMN_INDEX];
        String description = taskFields[DESCRIPTION_COLUMN_INDEX];
        Status status = getStatusFromString(taskFields[STATUS_COLUMN_INDEX]);
        long currentIdValue = idGenerator.peekCurrentIdValue();

        Task task = new Task(name, description, idGenerator);
        task.setId(id);
        task.setStatus(status);
        idGenerator.setStartIdValue(currentIdValue);
        return task;
    }

    /**
     * Первый этап загрузки эпиков: загружает эпик без подзадач, но со всеми Id своих SubTask-ов.
     * Выполняется ДО ЗАГРУЗКИ SubTask-ов
     */
    public Epic convertStringToEpic(String epicInLine) {
        String[] epicFields = epicInLine.split(TASK_IN_LINE_DELIMITER);
        long id = getIdFromString(epicFields[ID_COLUMN_INDEX], "Неверный формат id при загрузке Epic");
        String name = epicFields[NAME_COLUMN_INDEX];
        String description = epicFields[DESCRIPTION_COLUMN_INDEX];
        Status status = getStatusFromString(epicFields[STATUS_COLUMN_INDEX]);
        Long[] subTasksId = new Long[0]; // default empty arr

        if (epicFields.length > 5) {
            String[] ids = epicFields[IDS_COLUMN_INDEX].split(IDS_DELIMITER);
            subTasksId = new Long[ids.length];

            for (int i = 0; i < ids.length; i++) {
                subTasksId[i] = getIdFromString(ids[i], "Неверный формат id при загрузке Epic.SubTasks");
            }
        }
        long currentIdValue = idGenerator.peekCurrentIdValue();
        Epic epic = new Epic(name, description, idGenerator);
        epic.setId(id);
        epic.setStatus(status);
        epic.addSubTasksId(subTasksId);
        idGenerator.setStartIdValue(currentIdValue);
        return epic;
    }

    /**
     * Загрузка SubTask-ов должна выполняется ПОСЛЕ первого этапа загрузки эпиков epicFromString(String data)
     */
    public SubTask convertStringToSubTask(String subTaskInLine) throws TaskLoadException {
        String[] subTaskFields = subTaskInLine.split(TASK_IN_LINE_DELIMITER);
        long id = getIdFromString(subTaskFields[ID_COLUMN_INDEX], "Неверный формат id при загрузке SubTask");
        String name = subTaskFields[NAME_COLUMN_INDEX];
        String description = subTaskFields[DESCRIPTION_COLUMN_INDEX];
        Status status = getStatusFromString(subTaskFields[STATUS_COLUMN_INDEX]);
        Long epicId = getIdFromString(subTaskFields[IDS_COLUMN_INDEX], "Неверный формат EpicId при " +
                "загрузке SubTask");
        Epic epic;

        if (epics.containsKey(epicId)) {
            epic = epics.get(epicId);
        } else {
            throw new TaskLoadException("null epic при загрузке SubTask id: " + id);
        }

        long currentIdValue = idGenerator.peekCurrentIdValue();
        SubTask subTask = new SubTask(name, description, idGenerator);
        subTask.setId(id);
        subTask.setStatus(status);
        subTask.setEpic(epic);
        idGenerator.setStartIdValue(currentIdValue);
        return subTask;
    }

    /**
     * Второй этап загрузки эпиков: добавляет в эпики их подзадачи по ID хранящимся в Set<Long> subTasksId
     * внутри каждого эпика. Выполняется ПОСЛЕ ЗАГРУЗКИ SubTask-ов
     */
    public void fillEpicWithSubTasks(Epic epic) throws TaskLoadException {
        if (epic.getSubTasksId().size() == 0) return;

        for (Long id : epic.getSubTasksId()) {
            if (subTasks.containsKey(id)) {
                epic.addSubTask(subTasks.get(id));
            } else {
                throw new TaskLoadException("Не выполнена загрузка SubTask-ов");
            }
        }
    }

    /**
     * Загружает историю просмотров из файла-хранилища
     */
    public void loadHistory(String data) throws TaskLoadException {
        List<Long> ids = InMemoryHistoryManager.fromString(data);

        for (Long id : ids) {

            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
                continue;
            }

            if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
                continue;
            }

            if (subTasks.containsKey(id)) {
                historyManager.add(subTasks.get(id));
                continue;
            }

            throw new TaskLoadException("Ошибка загрузки истории просмотров + id: " + id);
        }
    }
}
