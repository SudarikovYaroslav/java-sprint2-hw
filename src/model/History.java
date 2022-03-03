package model;

import model.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class History {
    private static final int HISTORY_SIZE = 10;
    private final List<Task> history;

    public History() {
        history = new ArrayList<>();
    }

    public void add(Task task) {
        boolean full = history.size() >= HISTORY_SIZE;
        if (full) history.remove(0);
        history.add(task);
    }

    public List<Task> getHistory() {
        return history;
    }
}
