package model;

import model.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Коллекция предназначена для работы с классом Task для ведения истории вызовов Task-ов в HistoryManager
 */
 /*
 Класс Node вместе с коллекцией MapLinkedList намеренно не делаю параметризованными, т.к они заточены специально для
 работы с классом Task, и не смогут корректно работать с ЛЮБЫМ произвольным классом отличным по структуре от Task.
 Связанно с тем, что Коллекции (для работы с HashMap) необходимо вызывать метод getId() у значения value, передаваемого
 в качестве параметра при создании объекта Node.
 */
public class MapLinkedList {
    private final Map<Long, Node> nodeLinks;
    private Node head = null;
    private Node tail = null;
    private int size = 0;

    public MapLinkedList() {
        nodeLinks = new HashMap<>();
    }

    /**
     * Add next Nod to the collection
     */
    public void linkLast(Task task) {
        Node node = new Node(task);
        long id = task.getId();

        if (nodeLinks.containsKey(id)) {
            removeNode(id);
        }
        nodeLinks.put(id, node);
        addNode(node);
    }

    /**
     * Returns first element of collection without removing
     */
    public Task getFirst() {
        return head.getValue();
    }

    public Task getLast() {
        return tail.getValue();
    }

    public Task getTaskById(long id) {
        return nodeLinks.get(id).getValue();
    }

    public Task get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        if (index == 0) return head.getValue();
        if (index == size - 1) return tail.getValue();

        Node current;

        if (index < size / 2) {
            current = head;
            for (int i = 1; i <= index; i++) {
                current = current.getNext();
            }
        } else {
            current = tail;
            for (int i = size - 2; i >= index; i--) {
                current = current.getPrev();
            }
        }
        return current.getValue();
    }

    public int size() {
        return size;
    }

    public void removeTask(Task task) {
        if (nodeLinks.containsKey(task.getId())) {
            removeNode(task.getId());
        }
    }

    public void removeTask(long id) {
        if (nodeLinks.containsKey(id)) {
            removeNode(id);
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;

        if (current != null) {
            tasks.add(head.getValue());

            while ((current = current.getNext()) != null) {
                tasks.add(current.getValue());
            }
        }
        return tasks;
    }

    private void addNode(Node node) {
        if (head == null) {
            head = node;
            tail = node;
            size++;
            return;
        }

        Node prevTail = tail;
        prevTail.setNext(node);
        tail = node;
        tail.setPrev(prevTail);
        size++;
    }

    private void removeNode(long id) {
        Node current = nodeLinks.get(id);

        if (current == head) {
            boolean moreThenOneNode = head.getNext() != null;

            if (moreThenOneNode) {
                head = head.getNext();
                head.setPrev(null);
            } else {
                head = null;
            }

        } else if (current == tail) {
            tail = tail.getPrev();
            tail.setNext(null);
        } else {
            Node prev = current.getPrev();
            Node next = current.getNext();

            prev.setNext(next);
            next.setPrev(prev);
        }
        nodeLinks.remove(id);
        size--;
    }
}
