package service;

import model.History;
import model.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final History history = new History();

    @Override
    public void add(Task task) {
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }
}
