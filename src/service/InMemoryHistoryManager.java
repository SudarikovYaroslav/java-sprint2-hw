package service;

import model.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        boolean full = history.size() >= HISTORY_SIZE;
        if (full) history.remove(0);
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
