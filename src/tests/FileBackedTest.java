package tests;

import model.exceptions.ManagerLoadException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.FileBackedTaskManager;
import service.HistoryManager;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import util.Managers;
import util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class should be used only for tests of FileBackedTaskManager
 */
public class FileBackedTest implements Test{

    private List<Task> tasks;
    private final IdGenerator idGenerator = IdGenerator.getInstance();
    private HistoryManager historyManager;
    private FileBackedTaskManager taskManager;

    public FileBackedTest() {
        tasks = new ArrayList<>();
        historyManager = Managers.getDefaultHistory();
        taskManager = new FileBackedTaskManager(historyManager, Util.createFileBacked());
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

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(epic1);
        tasks.add(subTask1);
        tasks.add(subTask2);
        tasks.add(epic2);
    }

    private void makeSeveralCalls() {
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
    }

    private void printCondition() {
        print("----CONDITION----");
        for (Task task : tasks) {
            print(task.toString());
        }
        print("HISTORY:");
        print(InMemoryHistoryManager.toString(historyManager));
        print("\n");
    }

    //history manager намеренно создаю минуя класс Managers, чтобы получить чистую историю
    private void reload() {
        print("Reboot has been started!\nPlease check, the history and condition get empty.");
        tasks = new ArrayList<>();
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, Util.createFileBacked());
        printCondition();

        try {
            taskManager.loadFromFile(Util.createFileBacked());
        } catch (ManagerLoadException e) {
            e.printStackTrace();
        }
        reloadTasksList();
    }

    private void reloadTasksList() {
        tasks.addAll(taskManager.getTasksList());
        tasks.addAll(taskManager.getEpicsList());
        tasks.addAll(taskManager.getSubTasksList());
    }

    private void print(Object o) {
        System.out.println(o.toString());
    }

    private void printWarning() {
        print("Reboot has been performed!\nPlease check: condition has to be the same as before reboot!");
    }
}
