package tests;

import service.TaskManager;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import util.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * WARNING!
 * This class should be used only for console tests of HistoryManager
 * There is only one public method run() for start testing
 */
public class HistoryConsoleTest {
    private static int counter = 0;
    private final TaskManager manager;
    private final List<Task> tasks;

    public HistoryConsoleTest() {
        manager = Managers.getDefault();
        tasks = createTasksList();
    }

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
                Task task = new Task("Task", "простая задача", manager.generatedId());
                manager.createTask(task);
                return task;
            case 1:
                Epic epic = new Epic("Epic", "сложная задача", manager.generatedId());
                manager.createEpic(epic);
                return epic;
            case 2:
                SubTask subTask = new SubTask("SubTask", "подзадача", manager.generatedId());
                manager.createSubTask(subTask);
                return subTask;
            default:
                Task defaultTask = new Task("DefaultTask", "простая задача", manager.generatedId());
                manager.createTask(defaultTask);
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
            manager.getSubTaskById(id);
        } catch (ClassCastException notSubTask) {
            try {
                Epic epic = (Epic) task;
                manager.getEpicById(id);
            } catch (ClassCastException notEpic) {
                manager.getTaskById(id);
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
        Task task = new Task("Task", "repeated task", manager.generatedId());
        manager.createTask(task);

        for (int i = 0; i < 10; i++) {
            callTaskById(task);
        }
    }

    private void printHistory() {
        print("History:");
        for (Task task : manager.history()) {
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
