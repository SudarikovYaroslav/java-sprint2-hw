package service;

import model.Status;
import model.tasks.Epic;
import model.tasks.SubTask;

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

        if (done) return Status.DONE;
        if (brandNew) return Status.NEW;
        return Status.IN_PROGRESS;
    }
}
