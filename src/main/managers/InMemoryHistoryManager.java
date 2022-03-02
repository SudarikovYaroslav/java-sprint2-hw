package main.managers;

import main.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    /**
     * Method should marks tasks as viewed
     */
    @Override
    public void add(Task task) {
        byte maxHistorySize = 10;
        boolean full = history.size() >= maxHistorySize;
        if (full) history.remove(0);
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
