package tests.consoleLogTests;

import model.Status;
import model.exceptions.TaskCreateException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.IdGenerator;
import service.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.List;

/**
 * !! WARNING !!
 * Class ConsoleTest used only for console tests! It has only one public method runConsoleTest() to run testing;
 */
public class ConsoleTest implements Test {
    private final InMemoryTaskManager inMemoryTaskManager;
    private final Task task1;
    private final Task task2;
    private final SubTask subTask1;
    private final SubTask subTask2;
    private final SubTask subTask3;
    private final Epic epic1;
    private final Epic epic2;
    private final IdGenerator idGenerator;

    public ConsoleTest(InMemoryTaskManager inMemoryTaskManager) {
        idGenerator = IdGenerator.getInstance();
        this.inMemoryTaskManager = inMemoryTaskManager;
        task1 = new Task("Task1", "First action", idGenerator);
        task2 = new Task("Task2", "Second Action", idGenerator);
        subTask1 = new SubTask("SubTask1", "First SubTask - First Epic", idGenerator);
        subTask2 = new SubTask("SubTask2", "Second SubTask - First Epic", idGenerator);
        subTask3 = new SubTask("SubTask3", "First subTask - second Epic", idGenerator);

        List<SubTask> firstEpicSubTasks = new ArrayList<>();
        firstEpicSubTasks.add(subTask1);
        firstEpicSubTasks.add(subTask2);

        epic1 = new Epic("Epic1", "Several First Epic SubTasks", firstEpicSubTasks, idGenerator);
        epic2 = new Epic("Epic2", "The only Second Epic SubTask!", idGenerator);

        epic2.addSubTask(subTask3);
    }

    /**
     * Run test
     */
    @Override
    public void run() {
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
        try {
            inMemoryTaskManager.createTask(task1);
            inMemoryTaskManager.createTask(task2);
            inMemoryTaskManager.createSubTask(subTask1);
            inMemoryTaskManager.createSubTask(subTask2);
            inMemoryTaskManager.createSubTask(subTask3);
            inMemoryTaskManager.createEpic(epic1);
            inMemoryTaskManager.createEpic(epic2);
        } catch (TaskCreateException e) {
            e.printStackTrace();
        }
    }

    private void printTasksCondition() {
        for (Task task : inMemoryTaskManager.getTasksList()) {
            printTask(task);
        }

        for (Epic epic : inMemoryTaskManager.getEpicsList()) {
            printEpic(epic);
        }
    }

    /**
     * !! WARNING !!
     * Так как при обновлении "Треккера задач", согласно ТЗ, новая версия объекта с верным идентификатором передаются
     * в виде параметра в метод update(), уникальный для каждого типа "задачи", в классе Manager, и статусы задач не
     * меняются непосредственно в backend блоке программы, а приходят из frontend блока в изменённых "задачах", метод
     * updateTasks() ИММИТИРУЕТ обновление имеющихся "задач" всех типов с сохранинием уникального id,
     * изменение статуса задач и их передачу в объект InMemoryTaskManager;
     */
    private void updateTasks() {
        printUpdateWarning();
        Task updatedTask1 = new Task("updatedTask1", "Update firstAction", idGenerator);
        Task updatedTask2 = new Task("updatedTask2", "Update secondAction", idGenerator);
        updatedTask1.setId(1);
        updatedTask2.setId(2);
        updatedTask1.setStatus(Status.IN_PROGRESS);
        updatedTask2.setStatus(Status.DONE);

        SubTask updatedSubTask1 = new SubTask("updatedSubTask1", "updatedSubTask1 - Epic1", idGenerator);
        SubTask updatedSubTask2 = new SubTask("updatedSubTask2", "updatedSubTask2 - Epic1", idGenerator);
        SubTask updatedSubTask3 = new SubTask("updatedSubTask3", "updatedSubTask3 - Epic2", idGenerator);
        updatedSubTask1.setId(3);
        updatedSubTask2.setId(4);
        updatedSubTask3.setId(5);
        updatedSubTask1.setStatus(Status.IN_PROGRESS);
        updatedSubTask2.setStatus(Status.DONE);
        updatedSubTask3.setStatus(Status.DONE);

        List<SubTask> epic1UpdatedSubTasks = new ArrayList<>();
        epic1UpdatedSubTasks.add(updatedSubTask1);
        epic1UpdatedSubTasks.add(updatedSubTask2);

        Epic updatedEpic1 = new Epic("updatedEpic1", "Several updated First Epic SubTasks",
                epic1UpdatedSubTasks, idGenerator);

        Epic updatedEpic2 = new Epic("updatedEpic2", "The only updated Second Epic SubTask!",
                idGenerator);
        updatedEpic2.addSubTask(updatedSubTask3);

        updatedEpic1.setId(6);
        updatedEpic2.setId(7);

        inMemoryTaskManager.updateTask(updatedTask1);
        inMemoryTaskManager.updateTask(updatedTask2);
        inMemoryTaskManager.updateSubTask(updatedSubTask1);
        inMemoryTaskManager.updateSubTask(updatedSubTask2);
        inMemoryTaskManager.updateSubTask(updatedSubTask3);
        inMemoryTaskManager.updateEpic(updatedEpic1);
        inMemoryTaskManager.updateEpic(updatedEpic2);
    }

    private void deleteTest() {
        printDeleteWarning();
        inMemoryTaskManager.deleteTaskById(1);
        inMemoryTaskManager.deleteEpicById(6);
    }

    private void emptyEpicTest() {
        print("\nTrying to delete all subTasks from Epic2. CHECK: the Epic2 status should changed into NEW");
        inMemoryTaskManager.deleteSubTaskById(5);
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
