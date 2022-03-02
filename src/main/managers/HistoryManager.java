package main.managers;

import main.tasks.Task;

import java.util.List;

public interface HistoryManager {
    /**
     * Method should marks tasks as viewed
     */
    void add(Task task);

    List<Task> getHistory();
}
