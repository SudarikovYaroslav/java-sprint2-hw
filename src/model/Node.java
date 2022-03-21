package model;

import model.tasks.Task;

import java.util.Objects;

public class Node {
    private final Task task;
    private final long id;

    public Node(Task task) {
        this.task = task;
        id = task.getId();
    }

    public Task getTask() {
        return task;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id &&
                Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, id);
    }
}
