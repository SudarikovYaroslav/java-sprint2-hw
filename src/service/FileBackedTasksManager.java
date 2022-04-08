package service;

import model.Status;
import model.TaskLoadingException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;

import java.io.File;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTasksManager(InMemoryHistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public Task getTaskById(long id) {
        save();
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(long id) {
        save();
        return super.getEpicById(id);
    }

    @Override
    public SubTask getSubTaskById(long id) {
        save();
        return super.getSubTaskById(id);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(long id) {
        super.deleteSubTaskById(id);
        save();
    }

    //todo
    // сохранять: Все задачи, подзадачи, эпики и историю просмотра любых задач
    // Инициализация всех типов задач при загрузке реализована с помощью специальных конструкторов, которые должны
    // использоваться исключительно для загрузки задач из файла - хранилища, во избежании фатальных ошибок связанных
    // с некорректной инициализацией задач и коллизий ID, которые могут возникать при использовании этих конструкторов
    // напрямую при попытке создания объектов задач вне процесса загрузки
    public void save() {
    }

    private Task taskFromString(String data) throws TaskLoadingException {
        String[] arr = data.split(",");
        long id = getIdFromString(arr[1], "Неверный формат id при загрузке Task");
        String name = arr[2];
        String description = arr[3];
        Status status = getStatusFromString(arr[4]);
        return new Task(id, name, description, status);
    }

    /**
     * Первый этап загрузки эпиков: загружает эпик без подзадач, но со всеми Id своих SubTask-ов.
     * Выполняется ДО ЗАГРУЗКИ SubTask-ов
     */
    private Epic epicFromString(String data) {
        String[] arr = data.split(",");
        long id = getIdFromString(arr[1], "Неверный формат id при загрузке Epic");
        String name = arr[2];
        String description = arr[3];
        Status status = getStatusFromString(arr[4]);
        Long[] subTasksId = new Long[0]; // default empty arr

        if (arr.length > 5) {
            String[] iDs = arr[5].split("\\.");
            subTasksId = new Long[iDs.length];

            for (int i = 0; i < iDs.length; i++) {
                subTasksId[i] = getIdFromString(iDs[i], "Неверный формат id при загрузке Epic.SubTasks");
            }
        }

        return new Epic(id, name, description, status, subTasksId);
    }

    /**
     * Загрузка SubTask-ов должна выполняется ПОСЛЕ первого этапа загрузки эпиков epicFromString(String data)
     */
    private SubTask subTaskFromString(String data) throws TaskLoadingException {
        String[] arr = data.split(",");
        long id = getIdFromString(arr[1], "Неверный формат id при загрузке SubTask");
        String name = arr[2];
        String description = arr[3];
        Status status = getStatusFromString(arr[4]);
        Long epicId = getIdFromString(arr[5], "Неверный формат EpicId при загрузке SubTask");
        Epic epic;

        if (epics.containsKey(epicId)) {
            epic = epics.get(epicId);
        } else {
            throw new TaskLoadingException("null epic при загрузке SubTask");
        }
        return new SubTask(id, name, description, status, epic);
    }

    /**
     * Второй этап загрузки эпиков: добавляет в эпики их подзадачи по ID хранящимся в Set<Long> subTasksId
     * внутри каждого эпика. Выполняется ПОСЛЕ ЗАГРУЗКИ SubTask-ов
     */
    private Epic fillEpicWithSubTasks(Epic epic) throws TaskLoadingException {
        if (epic.getSubTasksId().size() == 0) return epic;

        for (Long id : epic.getSubTasksId()) {
            if (subTasks.containsKey(id)) {
                epic.addSubTask(subTasks.get(id));
            } else {
                throw new TaskLoadingException("Не выполнена загрузка SubTask-ов");
            }
        }
        return epic;
    }

    private Status getStatusFromString(String data) {
        Status status;

        switch (data) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                status = Status.IN_PROGRESS;
                break;
            case "DONE":
                status = Status.DONE;
            default:
                status = Status.IN_PROGRESS;
        }
        return status;
    }

    private long getIdFromString(String data, String expMessage) {
        long id = 0;
        try {
            id = Long.parseLong(data);
        } catch (NumberFormatException e) {
            try {
                throw new TaskLoadingException(expMessage);
            } catch (TaskLoadingException exception) {
                exception.printStackTrace();
            }
        }
        return id;
    }
}
