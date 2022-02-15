package main.Util;

import main.Manager;
import main.Status;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.util.ArrayList;

/**
 * Class used only for tests!
 */
public class ConsoleTest {

    private Manager manager;

    public ConsoleTest(Manager manager) {
        this.manager = manager;
    }

    public void go() {
        Task task1 = new Task("Task One", "First action");
        Task task2 = new Task("Task two", "Second Action");

        SubTask subTask1 = new SubTask("First SubTask", "SubTask for First Epic");
        SubTask subTask2 = new SubTask("Second SubTask", "Second SubTask for First Epic");
        ArrayList<SubTask> firstSubTasks = new ArrayList<>();
        firstSubTasks.add(subTask1);
        firstSubTasks.add(subTask2);

        Epic epic1 = new Epic("First Epic", "Several First Epic firstSubTasks", firstSubTasks);

        SubTask subTask3 = new SubTask("Task Three", "First subTask for second Epic");
        Epic epic2 = new Epic("Second Epic", "The only Second Epic SubTask!");
        epic2.addSubTask(subTask3);

        printTask(task1);
        printTask(task2);
        printEpic(epic1);
        printEpic(epic2);

        //In this section make some status changes in all task types
        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);

        ArrayList<SubTask> firstEpicTasks = epic1.getSubTasks();
        firstEpicTasks.get(0).setStatus(Status.IN_PROGRESS);
        firstEpicTasks.get(1).setStatus(Status.DONE);

        ArrayList<SubTask> secondEpicTasks = epic2.getSubTasks();
        secondEpicTasks.get(0).setStatus(Status.DONE);

        print("Now you'll see the same tasks with another statuses, \n please compare it with check section after" +
                " \"========\" line");

        print("=====================\nSHOULD BE:\ntask1 status: IN_PROGRESS\ntask2 status: DONE" +
                "\nepic1 status: IN_PROGRESS\n epic1 subTasks: subTask1 - IN_PROGRESS, subTask2 - DONE" +
                "\nepic2 status: DONE\n epic2 subTasks: subTask3 - DONE" );

    }

    private void print(String message) {
        System.out.println(message);
    }

    private void printTask(Task task) {
        print("TaskName: " + task.getName() + " - id: " + task.getId() + " - status: " + task.getStatus() + "\n");
    }

    private void printSubTask(SubTask subTask) {
        print("SubTaskName: " + subTask.getName() + " - id: " + subTask.getId() + " - status: " + subTask.getStatus() + "\n");
    }

    private void printEpic(Epic epic) {
        print("EpicName: " + epic.getName() + " - id: " + epic.getId() + " - status: " + epic.getStatus());
        print(epic.getName() + " sub tasks:");

        for (SubTask subTask : epic.getSubTasks()) {
            printSubTask(subTask);
        }
    }



}
