package service;

import model.Node;
import model.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final List<Node> lastViewedTasks;
    private final Map<Long, Node> nodeLinks;

    public InMemoryHistoryManager() {
        lastViewedTasks = new LinkedList<>();
        nodeLinks = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Warning: Task in InMemoryHistoryManager.add(Task task) is null!");
            return;
        }

        if (lastViewedTasks.size() >= HISTORY_SIZE) {
            remove(lastViewedTasks.get(0).getId());
        }

        linkLast(task);
    }

    @Override
    public void remove(long id) {
        removeNode(nodeLinks.get(id));
    }

    @Override
    public List<Task> getLastViewedTasks() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node node = new Node(task);
        if (nodeLinks.containsKey(node.getId())) {
            removeNode(node);
        }
        lastViewedTasks.add(node);
        nodeLinks.put(node.getId(), node);
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();

        for (Node node : lastViewedTasks) {
            history.add(node.getTask());
        }
        return history;
    }

    private void removeNode(Node node) {
        lastViewedTasks.remove(node);
        nodeLinks.remove(node.getId());
    }
}
