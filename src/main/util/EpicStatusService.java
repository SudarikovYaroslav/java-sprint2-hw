package main.util;

import main.Status;
import main.tasks.Epic;
import main.tasks.Task;

import java.util.ArrayList;

public class EpicStatusService {

    public Status calculateStatus(Epic epic) {
        boolean done = true;
        boolean brandNew = true;
        ArrayList<Task> subTasks = epic.getSubTasks();

        if (!subTasks.isEmpty()) {

            for (Task task : subTasks) {
                if (brandNew && task.getStatus() != Status.NEW) {
                    brandNew = false;
                }

                if (done && task.getStatus() != Status.DONE) {
                    done = false;
                }
            }

        } else {
            done = false;
        }

        if (done) {
            return Status.DONE;
        } else if (brandNew) {
            return Status.NEW;
        } else {
            return Status.IN_PROGRESS;
        }
    }
}
