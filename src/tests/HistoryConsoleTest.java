package tests;

import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.HistoryManager;
import service.IdGenerator;
import service.TaskManager;
import util.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * WARNING!
 * This class should be used only for console tests of HistoryManager
 * There is only one public method run() for start testing
 */
public class HistoryConsoleTest implements Test {
    private static int counter = 0;
    private final HistoryManager historyManager;
    private final TaskManager taskManager;
    private final List<Task> tasks;
    private final IdGenerator idGenerator;

    public HistoryConsoleTest() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        idGenerator = IdGenerator.getInstance();
        tasks = createTasksList();
    }

    @Override
    public void run() {
        printTasks();
        callNextFiveTasks();
        printHistory();
        callNextFiveTasks();
        printHistory();
        callNextFiveTasks();
        printWarning();
        printHistory();
        printRepeatedWarning();
        severalTimesTheSameTaskTest();
        printHistory();
    }

    private List<Task> createTasksList() {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            Task task = createTask();
            tasks.add(task);
        }
        return tasks;
    }

    private Task createTask() {
        Random random = new Random();
        int taskType = random.nextInt(3);

        switch (taskType) {
            case 0:
                Task task = new Task("Task", "простая задача", idGenerator);
                taskManager.createTask(task);
                return task;
            case 1:
                Epic epic = new Epic("Epic", "сложная задача", idGenerator);
                taskManager.createEpic(epic);
                return epic;
            case 2:
                SubTask subTask = new SubTask("SubTask", "подзадача", idGenerator);
                taskManager.createSubTask(subTask);
                return subTask;
            default:
                Task defaultTask = new Task("DefaultTask", "простая задача", idGenerator);
                taskManager.createTask(defaultTask);
                return defaultTask;
        }
    }

    /**
     * Because of each type of task in TaskManger should has it's own method to get task type by id
     * Method defines which concrete type of "Task" is variable task
     */
    private void callTaskById(Task task) {
        long id = task.getId();
        try {
            SubTask subTask = (SubTask) task;
            taskManager.getSubTaskById(id);
        } catch (ClassCastException notSubTask) {
            try {
                Epic epic = (Epic) task;
                taskManager.getEpicById(id);
            } catch (ClassCastException notEpic) {
                taskManager.getTaskById(id);
            }
        }
    }

    private void callNextFiveTasks() {
        int border = counter + 5;
        while (counter < border) {
            callTaskById(tasks.get(counter));
            counter++;
        }
    }

    private void severalTimesTheSameTaskTest() {
        Task task = new Task("Task", "repeated task", idGenerator);
        taskManager.createTask(task);

        for (int i = 0; i < 10; i++) {
            callTaskById(task);
        }
    }

    private void printHistory() {
        print("History:");
        for (Task task : historyManager.getLastViewedTasks()) {
            print("Name: " + task.getName() + "; description: " + task.getDescription() + "; id: " + task.getId());
        }
        print();
    }

    private void printWarning() {
        print("WARNING!\nCheck for first five tasks in history was deleted,\nand there are should be 5 last new tasks");
    }

    private void printRepeatedWarning() {
        print("WARNING!\nNow the same task, was called 10 times.\nHistory should contain 10 callings of the same task");
    }

    private void printTasks() {
        print("TasksList:");
        for (Task task : tasks) {
            print("Name: " + task.getName() + "; description: " + task.getDescription() + "; id: " + task.getId());
        }
        print();
    }

    private void print(String message) {
        System.out.println(message);
    }

    private void print() {
        print("\n");
    }
}
