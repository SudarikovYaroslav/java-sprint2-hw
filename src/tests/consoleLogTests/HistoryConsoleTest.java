package tests.consoleLogTests;

import model.exceptions.TaskCreateException;
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
    protected final HistoryManager historyManager;
    protected final TaskManager taskManager;
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

    protected List<Task> createTasksList() {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            Task task = createTask();
            tasks.add(task);
        }
        return tasks;
    }

    protected Task createTask() {
        Random random = new Random();
        int taskType = random.nextInt(3);

        try {
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
        } catch (TaskCreateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Because of each type of task in TaskManger should has it's own method to get task type by id
     * Method defines which concrete type of "Task" is variable task
     */
    protected void callTaskById(Task task) {
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

    protected void callNextFiveTasks() {
        int border = counter + 5;
        while (counter < border) {
            callTaskById(tasks.get(counter));
            counter++;
        }
    }

    protected void severalTimesTheSameTaskTest() {
        Task task = new Task("Task", "repeated task", idGenerator);
        try {
            taskManager.createTask(task);
        } catch (TaskCreateException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10; i++) {
            callTaskById(task);
        }
    }

    protected void printHistory() {
        print("History:");
        for (Task task : historyManager.getLastViewedTasks()) {
            print("Name: " + task.getName() + "; description: " + task.getDescription() + "; id: " + task.getId());
        }
        print();
    }

    protected void printWarning() {
        print("WARNING!\nCheck for first five tasks in history was deleted,\nand there are should be 5 last new tasks");
    }

    protected void printRepeatedWarning() {
        print("WARNING!\nNow the same task, was called 10 times.\nHistory should contain 10 callings of the same task");
    }

    protected void printTasks() {
        print("TasksList:");
        for (Task task : tasks) {
            print("Name: " + task.getName() + "; description: " + task.getDescription() + "; id: " + task.getId());
        }
        print();
    }

    protected void print(String message) {
        System.out.println(message);
    }

    protected void print() {
        print("\n");
    }
}
