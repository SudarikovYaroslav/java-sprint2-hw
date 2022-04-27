package main.service;

import main.model.MapLinkedList;
import main.model.tasks.Task;
import main.util.Util;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final MapLinkedList lastViewedTasks;

    public InMemoryHistoryManager() {
        lastViewedTasks = new MapLinkedList();
    }

    public static String toString(HistoryManager manager) {
        StringBuilder resultBuilder = new StringBuilder();

        for (Task task : manager.getLastViewedTasks()) {
            resultBuilder.append(task.getId()).append(",");
        }
        return resultBuilder.toString();
    }

    public static List<Long> fromString(String value) {
        List<Long> historyID = new ArrayList<>();
        String[] idS = value.split(",");

        for (int i = 0; i < idS.length; i++) {
            long id = Util.getIdFromString(idS[i], "Неверный формат id при загрузке истории просмотров");
            historyID.add(id);
        }
        return historyID;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new NullPointerException("Нельзя добавить в историю просмотров Task = null !");
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
