package service;

import model.Status;
import model.TaskTypes;
import model.exceptions.TasksLoadException;
import model.exceptions.TasksSaveException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import util.Managers;
import util.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static util.Util.getIdFromString;
import static util.Util.getStatusFromString;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final Path fileBacked;
    private final IdGenerator idGenerator;

    private static final String LINE_DELIMITER = "\n";
    private static final String EMPTY_LINE_DELIMITER = " \n";
    private static final String META_LINE_DELIMITER = "#";
    private static final String ENTRY_DELIMITER = ",";
    private static final String IDS_DELIMITER = "\\.";
    private static final int ALL_TASKS_IN_LINE_INDEX = 0;
    private static final int HISTORY_IN_LINE_INDEX = 1;
    private static final int META_LINE_INDEX = 0;
    private static final int CURRENT_ID_INDEX = 1;
    private static final int TYPE_COLUMN_INDEX = 0;
    private static final int ID_COLUMN_INDEX = 1;
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int DESCRIPTION_COLUMN_INDEX = 3;
    private static final int STATUS_COLUMN_INDEX = 4;
    private static final int IDS_COLUMN_INDEX = 5;

    public FileBackedTaskManager(HistoryManager historyManager, Path fileBacked) {
        super(historyManager);
        this.fileBacked = fileBacked;
        idGenerator = IdGenerator.getInstance();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task getTaskById(long id) {
        Task result = super.getTaskById(id);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic result = super.getEpicById(id);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask result = super.getSubTaskById(id);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (TasksSaveException e) {
            e.printStackTrace();
        }
    }

    /*
     По поводу избежания коллизий id при загрузке: решил при сохранении, просто записать в файл текущий id из
     генератора, а потом при загрузке установить его как стартовый, а чтобы id в генераторе при сборке тасков вперёд
     не уходил во время загрузки, немного модифицировал методы task/epic/subTask FromString()
    */
    public static FileBackedTaskManager loadFromFile(Path tasksFilePath) throws TasksLoadException {
        if (tasksFilePath == null) throw new TasksLoadException("Не указан файл для загрузки");
        if (!Files.exists(tasksFilePath)) throw new TasksLoadException("Указанный файл для загрузки не существует");

        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(historyManager, tasksFilePath);

        try {
            String mixedLine = new String(Files.readAllBytes(tasksFilePath));
            String[] arr = mixedLine.split(EMPTY_LINE_DELIMITER);
            String allTasksInLine = arr[ALL_TASKS_IN_LINE_INDEX];
            String historyInLine = arr[HISTORY_IN_LINE_INDEX];

            String[] entries = allTasksInLine.split(LINE_DELIMITER);

            String metaLine = entries[META_LINE_INDEX];
            String[] metaData = metaLine.split(META_LINE_DELIMITER);
            long currentIdValue = Util.getIdFromString(metaData[CURRENT_ID_INDEX], "неверный формат id " +
                    "при загрузке " + "текущего значения id");
            IdGenerator.setStartIdValue(currentIdValue);

            for (int i = 1; i < entries.length; i++) {
                String entry = entries[i];
                String[] fields = entry.split(ENTRY_DELIMITER);
                String taskType = fields[TYPE_COLUMN_INDEX];

                //первичная загрузка задач
                switch (TaskTypes.valueOf(taskType)) {
                    case TASK:
                        Task task = taskManager.taskFromString(entry);
                        taskManager.createTask(task);
                        break;
                    case EPIC:
                        Epic epic = taskManager.epicFromString(entry);
                        taskManager.createEpic(epic);
                        break;
                    case SUB_TASK:
                        SubTask subTask = taskManager.subTaskFromString(entry);
                        taskManager.createSubTask(subTask);
                        break;
                }
            }

            //догружаем все epic-и до валидного состояния
            for (Epic epic : taskManager.getEpicsList()) {
                try {
                    taskManager.fillEpicWithSubTasks(epic);
                } catch (TasksLoadException e) {
                    e.printStackTrace();
                }
            }

            //заполняем историю просмотров
            taskManager.loadHistory(historyInLine);

        } catch (IOException e) {
            throw new TasksLoadException("Ошибка при загрузке резервной копии", e);
        }

        return taskManager;
    }

    public void save() throws TasksSaveException {
        if (!Files.exists(fileBacked)) throw new TasksSaveException("Указанный файл для записи не существует");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileBacked.toFile()))) {
            writer.write("type,id,name,description,status,id...,#" +
                    IdGenerator.peekCurrentIdValue() + LINE_DELIMITER);

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
            throw new TasksSaveException("Ошибка при сохранении данных");
        }
    }

    private Task taskFromString(String data) {
        String[] arr = data.split(ENTRY_DELIMITER);
        long id = getIdFromString(arr[ID_COLUMN_INDEX], "Неверный формат id при загрузке Task");
        String name = arr[NAME_COLUMN_INDEX];
        String description = arr[DESCRIPTION_COLUMN_INDEX];
        Status status = getStatusFromString(arr[STATUS_COLUMN_INDEX]);
        long currentIdValue = IdGenerator.peekCurrentIdValue();

        Task task = new Task(name, description, idGenerator);
        task.setId(id);
        task.setStatus(status);
        IdGenerator.setStartIdValue(currentIdValue);
        return task;
    }

    /**
     * Первый этап загрузки эпиков: загружает эпик без подзадач, но со всеми Id своих SubTask-ов.
     * Выполняется ДО ЗАГРУЗКИ SubTask-ов
     */
    private Epic epicFromString(String data) {
        String[] arr = data.split(ENTRY_DELIMITER);
        long id = getIdFromString(arr[ID_COLUMN_INDEX], "Неверный формат id при загрузке Epic");
        String name = arr[NAME_COLUMN_INDEX];
        String description = arr[DESCRIPTION_COLUMN_INDEX];
        Status status = getStatusFromString(arr[STATUS_COLUMN_INDEX]);
        Long[] subTasksId = new Long[0]; // default empty arr

        if (arr.length > 5) {
            String[] ids = arr[IDS_COLUMN_INDEX].split(IDS_DELIMITER);
            subTasksId = new Long[ids.length];

            for (int i = 0; i < ids.length; i++) {
                subTasksId[i] = getIdFromString(ids[i], "Неверный формат id при загрузке Epic.SubTasks");
            }
        }
        long currentIdValue = IdGenerator.peekCurrentIdValue();
        Epic epic = new Epic(name, description, idGenerator);
        epic.setId(id);
        epic.setStatus(status);
        epic.addSubTasksId(subTasksId);
        IdGenerator.setStartIdValue(currentIdValue);
        return epic;
    }

    /**
     * Загрузка SubTask-ов должна выполняется ПОСЛЕ первого этапа загрузки эпиков epicFromString(String data)
     */
    private SubTask subTaskFromString(String data) throws TasksLoadException {
        String[] arr = data.split(ENTRY_DELIMITER);
        long id = getIdFromString(arr[ID_COLUMN_INDEX], "Неверный формат id при загрузке SubTask");
        String name = arr[NAME_COLUMN_INDEX];
        String description = arr[DESCRIPTION_COLUMN_INDEX];
        Status status = getStatusFromString(arr[STATUS_COLUMN_INDEX]);
        Long epicId = getIdFromString(arr[IDS_COLUMN_INDEX], "Неверный формат EpicId при загрузке SubTask");
        Epic epic;

        if (epics.containsKey(epicId)) {
            epic = epics.get(epicId);
        } else {
            throw new TasksLoadException("null epic при загрузке SubTask id:" + id);
        }

        long currentIdValue = IdGenerator.peekCurrentIdValue();
        SubTask subTask = new SubTask(name, description, idGenerator);
        subTask.setId(id);
        subTask.setStatus(status);
        subTask.setEpic(epic);
        IdGenerator.setStartIdValue(currentIdValue);
        return subTask;
    }

    /**
     * Второй этап загрузки эпиков: добавляет в эпики их подзадачи по ID хранящимся в Set<Long> subTasksId
     * внутри каждого эпика. Выполняется ПОСЛЕ ЗАГРУЗКИ SubTask-ов
     */
    private void fillEpicWithSubTasks(Epic epic) throws TasksLoadException {
        if (epic.getSubTasksId().size() == 0) return;

        for (Long id : epic.getSubTasksId()) {
            if (subTasks.containsKey(id)) {
                epic.addSubTask(subTasks.get(id));
            } else {
                throw new TasksLoadException("Не выполнена загрузка SubTask-ов");
            }
        }
    }

    /**
     * Загружает историю просмотров из файла-хранилища
     */
    private void loadHistory(String data) throws TasksLoadException {
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

            throw new TasksLoadException("Ошибка загрузки истории просмотров + id:" + id);
        }
    }
}
