package main.test;

import main.Manager;
import main.Status;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.util.ArrayList;

/**
 * !! WARNING !!
 * Class ConsoleTest used only for console tests! It has only one public method runConsoleTest() to run testing;
 */
public class ConsoleTest {
    private final Manager manager;
    private final Task task1;
    private final Task task2;
    private final SubTask subTask1;
    private final SubTask subTask2;
    private final SubTask subTask3;
    private final Epic epic1;
    private final Epic epic2;

    public ConsoleTest(Manager manager) {
        this.manager = manager;
        task1 = new Task("Task1", "First action");
        task2 = new Task("Task2", "Second Action");
        subTask1 = new SubTask("SubTask1", "First SubTask - First Epic");
        subTask2 = new SubTask("SubTask2", "Second SubTask - First Epic");
        subTask3 = new SubTask("SubTask3", "First subTask - second Epic");

        ArrayList<SubTask> firstEpicSubTasks = new ArrayList<>();
        firstEpicSubTasks.add(subTask1);
        firstEpicSubTasks.add(subTask2);

        epic1 = new Epic("Epic1", "Several First Epic SubTasks", firstEpicSubTasks);
        epic2 = new Epic("Epic2", "The only Second Epic SubTask!");

        epic2.addSubTask(subTask3);
    }

    /**
     * Run test
     */
    public void runConsoleTest() {
        createTestTasks();
        printTasksCondition();
        updateTasks();
        printTasksCondition();
        printCheckSection();
        deleteTest();
        printTasksCondition();
        emptyEpicTest();
        printTasksCondition();
    }

    private void print(String message) {
        System.out.println(message);
    }

    private void createTestTasks() {
        printStartTestMessage();
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
    }

    private void printTasksCondition() {
        for (Task task : manager.getTasksList()) {
            printTask(task);
        }

        for (Epic epic : manager.getEpicsList()) {
            printEpic(epic);
        }
    }

    /**
     * !! WARNING !!
     * Так как при обновлении "Треккера задач", согласно ТЗ, новая версия объекта с верным идентификатором передаются
     * в виде параметра в метод update(), уникальный для каждого типа "задачи", в классе Manager, и статусы задач не
     * меняются непосредственно в backend блоке программы, а приходят из frontend блока в изменённых "задачах", метод
     * updateTasks() ИММИТИРУЕТ обновление имеющихся "задач" всех типов с сохранинием уникального id,
     * изменение статуса задач и их передачу в объект Manager;
     */
    private void updateTasks() {
        printUpdateWarning();
        Task updatedTask1 = new Task("updatedTask1", "Update firstAction");
        Task updatedTask2 = new Task("updatedTask2", "Update secondAction");
        updatedTask1.setId(1);
        updatedTask2.setId(2);
        updatedTask1.setStatus(Status.IN_PROGRESS);
        updatedTask2.setStatus(Status.DONE);

        SubTask updatedSubTask1 = new SubTask("updatedSubTask1", "updatedSubTask1 - Epic1");
        SubTask updatedSubTask2 = new SubTask("updatedSubTask2", "updatedSubTask2 - Epic1");
        SubTask updatedSubTask3 = new SubTask("updatedSubTask3", "updatedSubTask3 - Epic2");
        updatedSubTask1.setId(3);
        updatedSubTask2.setId(4);
        updatedSubTask3.setId(5);
        updatedSubTask1.setStatus(Status.IN_PROGRESS);
        updatedSubTask2.setStatus(Status.DONE);
        updatedSubTask3.setStatus(Status.DONE);

        ArrayList<SubTask> firstEpicUpdatedSubTasks = new ArrayList<>();
        firstEpicUpdatedSubTasks.add(updatedSubTask1);
        firstEpicUpdatedSubTasks.add(updatedSubTask2);

        Epic updatedEpic1 = new Epic("updatedEpic1", "Several updated First Epic SubTasks",
                firstEpicUpdatedSubTasks);

        Epic updatedEpic2 = new Epic("updatedEpic2", "The only updated Second Epic SubTask!");
        updatedEpic2.addSubTask(updatedSubTask3);

        updatedEpic1.setId(6);
        updatedEpic2.setId(7);

        manager.updateTask(updatedTask1);
        manager.updateTask(updatedTask2);
        manager.updateSubTask(updatedSubTask1);
        manager.updateSubTask(updatedSubTask2);
        manager.updateSubTask(updatedSubTask3);
        manager.updateEpic(updatedEpic1);
        manager.updateEpic(updatedEpic2);
    }

    private void deleteTest() {
        printDeleteWarning();
        manager.deleteTaskById(1);
        manager.deleteEpicById(6);
    }

    private void emptyEpicTest() {
        print("\nTrying to delete all subTasks from Epic2. CHECK: the Epic2 status should changed into NEW");
        manager.deleteSubTaskById(5);
    }

    private void printTask(Task task) {
        print("TaskName: " + task.getName() + " - id:" + task.getId() + " - status: " + task.getStatus());
    }

    private void printSubTask(SubTask subTask) {
        print("SubTaskName: " + subTask.getName() + " - id:" + subTask.getId() + " - status: " + subTask.getStatus());
    }

    private void printEpic(Epic epic) {
        print("EpicName: " + epic.getName() + " - id:" + epic.getId() + " - status: " + epic.getStatus());
        print(epic.getName() + " sub tasks:");

        for (SubTask subTask : epic.getSubTasks()) {
            printSubTask(subTask);
        }
    }

    private void printStartTestMessage() {
        print("CREATE: 2 Tasks, 1 Epic with 2 subTasks, 1 Epic whit 1 subTask. All statuses should be NEW");
    }

    private void printUpdateWarning() {
        print("\n*************************************************************\n" +
                "Now calling Manager.update(...) " +
                "methods,\nplease compare statuses with check section after" +
                " \"===========\" line\n");
    }

    private void printCheckSection() {
        print("\n==========check section===========\nSTATUSES OF UPDATED TASKS SHOULD BE:\ntask1 status: IN_PROGRESS" +
                "\ntask2 status: DONE\nepic1 status: IN_PROGRESS\nepic1 subTasks: subTask1 - IN_PROGRESS, " +
                "subTask2 - DONE\nepic2 status: DONE\nepic2 subTasks: subTask3 - DONE\n==============================");
    }

    private void printDeleteWarning() {
        print("\nNow trying to delete task1 and epic1. PLEASE CHECK: task1, epic1, subTask1, subTask2" +
                " will have to be deleted!");
    }
}
