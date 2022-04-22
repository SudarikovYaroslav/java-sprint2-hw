package tests.myOldFashionedTests;

import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * WARNING!
 * This is class for InMemoryHistory work tests only!
 * Fields in the class should't be final for correct processing!!!
 */

public class LinkedListRealisationTest implements Test {
    private InMemoryHistoryManager historyManager;
    private InMemoryTaskManager taskManager;
    private IdGenerator idGen;
    private ArrayList<Task> testTasks;

    public LinkedListRealisationTest() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        idGen = IdGenerator.getInstance();
        testTasks = new ArrayList<>();
    }

    @Override
    public void run() {
        generateTestTasks();
        printTasks();
        makeSeveralCalls();
        //Make reload all data fo ease control
        reloadTestData();
        removeTaskTest();
    }

    private void generateTestTasks() {
        Task task1 = new Task("Task1", "task1", idGen);
        Task task2 = new Task("Task2", "task2", idGen);

        Epic epic1 = new Epic("Epic1", "epic with subTasks", idGen);
        SubTask subTask1 = new SubTask("SubTask1", "Epic1 - sub1", idGen);
        SubTask subTask2 = new SubTask("SubTask2", "Epic1 - sub2", idGen);
        SubTask subTask3 = new SubTask("SubTask3", "Epic1 - sub3", idGen);
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        epic1.addSubTask(subTask3);

        Epic epic2 = new Epic("Epic2", "epic without subTasks", idGen);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        testTasks.add(task1);
        testTasks.add(task2);
        testTasks.add(subTask1);
        testTasks.add(subTask2);
        testTasks.add(subTask3);
        testTasks.add(epic1);
        testTasks.add(epic2);
    }

    private void printTasks() {
        for (Task task : testTasks) {
            printTask(task);
        }
    }

    private void printHistory() {
        List<Task> history = historyManager.getLastViewedTasks();
        print("\n----History----");

        for (Task task : history) {
            printTask(task);
        }
    }

    private void printTask(Task task) {
        System.out.println(task);
    }

    private void print(String message) {
        System.out.println(message);
    }

    private void makeSeveralCalls() {
        int randomId;
        int min = 1;
        int max = testTasks.size() + 1;
        Random random = new Random();

        print("\nNow make several task calls, please check:\nthere are shouldn't be coincidences in history:" +
                "\n==================================================");
        printHistory();
        for (int i = 0; i < 10; i++) {
            randomId = random.nextInt(max - min) + min;
            int pos = randomId - 1;
            callTaskById(testTasks.get(pos));
            printHistory();
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

    private void reloadTestData() {
        print("\n********************************\n" +
                "WARNING!!!\nMake reload data to ease control\n" +
                "********************************");

        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        idGen = IdGenerator.getInstance();
        testTasks = new ArrayList<>();
    }

    private void removeTaskTest() {
        print("\n=============RemoveTest=============");
        print("Try to add Task to the history");
        Task task = new Task("Task", "task", idGen);
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        printHistory();
        print("\nNow delete Task, check it was deleted from the history:");
        taskManager.deleteTaskById(task.getId());
        printHistory();
        print("\nNow trying create new Epic with three subTasks and make call of every task");

        Epic epic1 = new Epic("Epic1", "epic with subTasks", idGen);
        SubTask subTask1 = new SubTask("SubTask1", "Epic1 - sub1", idGen);
        SubTask subTask2 = new SubTask("SubTask2", "Epic1 - sub2", idGen);
        SubTask subTask3 = new SubTask("SubTask3", "Epic1 - sub3", idGen);
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        epic1.addSubTask(subTask3);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        taskManager.createEpic(epic1);

        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask3.getId());

        printHistory();
        print("\nTrying to delete Epic. Please check, epic with all it's subTasks should be deleted from the history:");
        taskManager.deleteEpicById(epic1.getId());
        printHistory();
    }
}
