package main.util;

import main.Status;
import main.tasks.Epic;
import main.tasks.SubTask;

import java.util.List;

public class EpicStatusService {

    public Status calculateStatus(Epic epic) {
        boolean done = true;
        boolean brandNew = true;
        List<SubTask> subTasks = epic.getSubTasks();

        if (!subTasks.isEmpty()) {

            for (SubTask subTask : subTasks) {
                if (brandNew && subTask.getStatus() != Status.NEW) {
                    brandNew = false;
                }

                if (done && subTask.getStatus() != Status.DONE) {
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
