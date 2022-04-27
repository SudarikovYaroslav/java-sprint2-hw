package main.model;

import main.model.tasks.Task;

import java.util.Objects;

/**
 * Class used as regular node of MapLinkedList collection in HistoryManager
 */
public class Node {
    private Node prev = null;
    private Node next = null;
    private Task value;

    public Node(Task value) {
        this.value = value;
    }

    public Task getValue() {
        return value;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(prev, node.prev) &&
                Objects.equals(next, node.next) &&
                Objects.equals(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prev, next, value);
    }
}
