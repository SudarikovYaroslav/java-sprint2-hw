package model;

import model.exceptions.TaskTimeException;
import model.tasks.Task;

public class IntersectionSearchResult {
    private final boolean intersect;
    private final Task intersectedTask;

    public IntersectionSearchResult(boolean intersect, Task intersectedTask) {
        this.intersect = intersect;
        this.intersectedTask = intersectedTask;
    }

    public String generateMessage() {
        String message = null;

        if(intersectedTask.getDuration() == null) {
            message = "Пересечение по времени с " +
                    intersectedTask.getId() + " startTime: " + intersectedTask.getStartTime() + "\n";
            return message;
        }

        try {
            message = "Пересечение по времени с " +
                    intersectedTask.getId() + " startTime: " + intersectedTask.getStartTime() + " endTime: "
                    + intersectedTask.getEndTime() + "\n";
        } catch (TaskTimeException e) {
            e.printStackTrace();
        }
        return message;
    }

    public boolean isIntersection() {
        return intersect;
    }
}