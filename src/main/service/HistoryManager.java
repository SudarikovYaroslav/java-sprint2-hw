package main.service;

import main.model.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(long id);

    List<Task> getLastViewedTasks();
}
