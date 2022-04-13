package tests;

import model.exceptions.TaskLoadException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.FileBackedTaskManager;
import service.HistoryManager;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import util.Managers;
import util.Util;

/**
 * This class should be used only for tests of FileBackedTaskManager
 */
public class FileBackedTest implements Test {

    private IdGenerator idGenerator = IdGenerator.getInstance();
    private HistoryManager historyManager;
    private FileBackedTaskManager taskManager;

    public FileBackedTest() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new FileBackedTaskManager(historyManager, Util.getBacked(), idGenerator);
    }

    @Override
    public void run() {
        genTasks();
        printCondition();
        print("Make several calls");
        makeSeveralCalls();
        printCondition();
        reload();
        printWarning();
        printCondition();
        print("Add one new Task, check id is OK");
        addNewTask();
        printCondition();
    }

    private void genTasks() {
        Task task1 = new Task("Task", "simple task", idGenerator);
        Task task2 = new Task("Task", "simple task", idGenerator);
        Epic epic1 = new Epic("Epic", "complex task", idGenerator);
        SubTask subTask1 = new SubTask("SubTask1", "sub task", idGenerator);
        SubTask subTask2 = new SubTask("SubTask2", "sub task", idGenerator);
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        subTask1.setEpic(epic1);
        subTask2.setEpic(epic1);
        Epic epic2 = new Epic("Epic2", "brand new complex task", idGenerator);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
    }

    private void makeSeveralCalls() {
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
    }

    private void printCondition() {
        print("----CONDITION----");
        for (Task task : taskManager.getTasksList()) {
            print(task.toString());
        }

        for (Epic epic : taskManager.getEpicsList()) {
            print(epic.toString());
        }

        for (SubTask subTask : taskManager.getSubTasksList()) {
            print(subTask.toString());
        }

        print("HISTORY:");
        print(InMemoryHistoryManager.toString(taskManager.getHistoryManager()));
        print("\n");
    }

    private void reload() {
        //создаём новые объекты менеджеров для имитации перезагрузки программы
        //history manager намеренно создаю минуя класс Managers, чтобы очистить историю просмотров
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, Util.getBacked(), IdGenerator.getInstance());
        print("Reboot has been started!\nPlease check, the history and condition get empty.");
        printCondition();
       idGenerator.setStartIdValue(-1);

        try {
            taskManager = FileBackedTaskManager.loadFromFile(Util.getBacked());
        } catch (TaskLoadException e) {
            e.printStackTrace();
        }
    }

    private void print(Object o) {
        System.out.println(o.toString());
    }

    private void printWarning() {
        print("Reboot has been performed!\nPlease check: condition has to be the same as before reboot!");
    }

    private void addNewTask() {
        Task task3 = new Task("Task3", "simple task", idGenerator);
        taskManager.createTask(task3);
    }
}
