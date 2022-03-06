package service;

import model.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final List<Task> lastViewedTasks;

    public InMemoryHistoryManager() {
        lastViewedTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        boolean full = lastViewedTasks.size() >= HISTORY_SIZE;
        if (full) lastViewedTasks.remove(0);
        lastViewedTasks.add(task);
    }

    @Override
    public List<Task> getLastViewedTasks() {
        return lastViewedTasks;
    }
}
