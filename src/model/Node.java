package model;

import model.tasks.Task;

import java.util.Objects;

/*
Не очень понимаю зачем по ТЗ просят создать отдельный класс для хранения тасков в истории просмотров ((
Чтобы для удобства ограничить набор методов от таска? Вроде для быстрой работы LinkedList в связке с HashMap
ссылки на сам таск и значения его id для ключей мапы ничего больше не надо, а их можно было бы и от самого таска
получить без создания доп класса.
*/
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
