package service;

import model.MapLinkedList;
import model.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final MapLinkedList lastViewedTasks;

    public InMemoryHistoryManager() {
        lastViewedTasks = new MapLinkedList();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Can't add task to history. Task is null");
            return;
        }

        if (lastViewedTasks.size() >= HISTORY_SIZE) {
            lastViewedTasks.removeTask(lastViewedTasks.get(0));
        }

        lastViewedTasks.linkLast(task);
    }

    @Override
    public void remove(long id) {
        lastViewedTasks.removeTask(id);
    }

    @Override
    public List<Task> getLastViewedTasks() {
        return lastViewedTasks.getTasks();
    }
}
