package service;

import model.Status;
import model.TaskTypes;
import model.exceptions.ManagerLoadException;
import model.exceptions.ManagerSaveException;
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

    private final Path fileBacked;

    public FileBackedTaskManager(HistoryManager historyManager, Path fileBacked) {
        super(historyManager);
        this.fileBacked = fileBacked;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task getTaskById(long id) {
        Task result = super.getTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic result = super.getEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask result = super.getSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    public void save() throws ManagerSaveException {
        if (!Files.exists(fileBacked)) throw new ManagerSaveException("Указанный файл для записи не существует");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileBacked.toFile()))) {
            writer.write("type,id,name,description,status,id...\n");

            for (Task task : tasks.values()) {
                writer.write(task.toString() + "\n");
            }

            for (Epic epic : epics.values()) {
                writer.write(epic.toString() + "\n");
            }

            for (SubTask subTask : subTasks.values()) {
                writer.write(subTask.toString() + "\n");
            }

            writer.write(" \n");
            writer.write(InMemoryHistoryManager.toString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных");
        }
    }


    /**
     * WARNING!!!
     * Инициализация всех типов задач при загрузке реализована с помощью специальных конструкторов, которые должны
     * использоваться исключительно для загрузки задач из файла - хранилища, во избежании фатальных ошибок связанных
     * с некорректной инициализацией задач и коллизий id, которые могут возникать при использовании этих конструкторов
     * напрямую при попытке создания объектов задач вне процесса загрузки (так как они требуют записать id,
     * а не использовать idGenerator, гарантирующий, что коллизий id среди задач не будет)
     */
    public void loadFromFile(Path fileBacked) throws ManagerLoadException {
        if (fileBacked == null) throw new ManagerLoadException("Не указан файл для загрузки");
        if (!Files.exists(fileBacked)) throw new ManagerLoadException("Указанный файл для загрузки не существует");

        try {
            String mixedLine = new String(Files.readAllBytes(fileBacked));
            String[] arr = mixedLine.split(" \n");
            String allTasksInLine = arr[0];
            String historyInLine = arr[1];

            String[] entries = allTasksInLine.split("\n");

            for (int i = 1; i < entries.length; i++) {
                String entry = entries[i];
                String[] fields = entry.split(",");
                String taskType = fields[0];

                //первичная загрузка задач
                switch (TaskTypes.valueOf(taskType)) {
                    case TASK:
                        Task task = taskFromString(entry);
                        tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        Epic epic = epicFromString(entry);
                        epics.put(epic.getId(), epic);
                        break;
                    case SUB_TASK:
                        SubTask subTask = subTaskFromString(entry);
                        subTasks.put(subTask.getId(), subTask);
                        break;
                }
            }

            //догружаем все epic-и до валидного состояния
            for (Epic epic : epics.values()) {
                try {
                    fillEpicWithSubTasks(epic);
                } catch (ManagerLoadException e) {
                    e.printStackTrace();
                }
            }

            //заполняем историю просмотров
            loadHistory(historyInLine);

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке резервной копии");
        }
    }

    private Task taskFromString(String data) {
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
    private  Epic epicFromString(String data) {
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
    private  SubTask subTaskFromString(String data) throws ManagerLoadException {
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
            throw new ManagerLoadException("null epic при загрузке SubTask");
        }
        return new SubTask(id, name, description, status, epic);
    }

    /**
     * Второй этап загрузки эпиков: добавляет в эпики их подзадачи по ID хранящимся в Set<Long> subTasksId
     * внутри каждого эпика. Выполняется ПОСЛЕ ЗАГРУЗКИ SubTask-ов
     */
    private void fillEpicWithSubTasks(Epic epic) throws ManagerLoadException {
        if (epic.getSubTasksId().size() == 0) return;

        for (Long id : epic.getSubTasksId()) {
            if (subTasks.containsKey(id)) {
                epic.addSubTask(subTasks.get(id));
            } else {
                throw new ManagerLoadException("Не выполнена загрузка SubTask-ов");
            }
        }
    }

    /**
     * Загружает историю просмотров из файла-хранилища
     */
    private void loadHistory(String data) throws ManagerLoadException {
        List<Long> idS = InMemoryHistoryManager.fromString(data);

        for (Long id : idS) {
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

            throw new ManagerLoadException("Ошибка загрузки истории просмотров");
        }
    }
}
