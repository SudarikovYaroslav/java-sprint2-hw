package main.Util;

import main.Manager;
import main.Status;
import main.tasks.Epic;
import main.tasks.SubTask;

import java.util.ArrayList;

public class Util {

    public static void checkEpicStatus(Epic epic){
        boolean isDone = true;
        boolean isNew = true;
        ArrayList<SubTask> subTasks = epic.getSubTasks();


        if (!subTasks.isEmpty()) {

            for (SubTask subTask : subTasks) {
                if (isNew && subTask.getStatus() != Status.NEW) {
                    isNew = false;
                }

                if (isDone && subTask.getStatus() != Status.DONE) {
                    isDone = false;
                }
            }

        } else {
            isDone = false;
        }

        if (isDone) {
            epic.setStatus(Status.DONE);
        } else if (isNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    /**Method used only for tests*/
    public static void simpleConsoleTest(Manager manager){
        ConsoleTest test = new ConsoleTest(manager);
        test.go();
    }
}
