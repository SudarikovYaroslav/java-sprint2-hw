package tests.consoleLogTests;

import model.MapLinkedList;
import model.tasks.Task;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

import java.util.List;

public class MapLinkedListTest implements Test {

    private final InMemoryHistoryManager historyManager;
    private final InMemoryTaskManager taskManager;
    private final IdGenerator idGen;

    public MapLinkedListTest() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        idGen = IdGenerator.getInstance();
    }

    private static void print(String s) {
        System.out.println(s);
    }

    @Override
    public void run() {
        MapLinkedList list = new MapLinkedList();

        //This part needs to the delete Test
        Task task1 = null;
        Task task3 = null;
        Task task5 = null;
        Task task7 = null;
        //////////////////

        print("----Test: linkLast() addNode() -------" +
                "\nshould be correct sequence 1-7");
        for (int i = 1; i <= 7; i++) {
            switch (i) {
                case 1:
                    task1 = new Task("Task1", "1", idGen);
                    list.linkLast(task1);
                    break;
                case 3:
                    task3 = new Task("Task3", "3", idGen);
                    list.linkLast(task3);
                    break;
                case 5:
                    task5 = new Task("Task5", "5", idGen);
                    list.linkLast(task5);
                    break;
                case 7:
                    task7 = new Task("Task7", "7", idGen);
                    list.linkLast(task7);
                    break;
                default:
                    Task task = new Task("Task" + i, "" + i, idGen);
                    list.linkLast(task);
            }
        }

        List<Task> tasks = list.getTasks();

        for (Task task : tasks) {
            print(task.toString());
        }

        print("\n----Size test----");
        print("Size should be 7 = " + list.size());

        print("\n----get(index) test----");
        print("List.get(0) = " + list.get(0));
        print("List.get(1) = " + list.get(1));
        print("List.get(2) = " + list.get(2));
        print("List.get(3) = " + list.get(3));
        print("List.get(4) = " + list.get(4));
        print("List.get(5) = " + list.get(5));
        print("List.get(6) = " + list.get(6));

        print("\n----getFirst test----");
        print("First is 1 = " + list.getFirst());

        print("\n----getLast test----");
        print("Last is 7 = " + list.getLast());

        print("\n----getTaskById(id) test----");
        print("getTaskById(2) = " + list.getTaskById(2));
        print("getTaskById(5) = " + list.getTaskById(5));
        print("getTaskById(7) = " + list.getTaskById(7));

        print("\n----removeTask() removeNode() test----");
        print("Now Tasks: 1, 3, 5, 7 has been deleted!\nCheck is it works\nCurrent size should be - 3\n");
        list.removeTask(task1);
        list.removeTask(task3);
        list.removeTask(task5);
        list.removeTask(task7);

        tasks = list.getTasks();

        for (Task task : tasks) {
            print(task.toString());
        }

        print("Current size = " + list.size());
    }
}
