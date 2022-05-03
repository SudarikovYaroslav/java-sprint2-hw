package TaskManagerTests;

import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskDeleteException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.InMemoryHistoryManager;
import main.service.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static main.service.TaskForTestsGenerator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class TaskManagerPrioritizedSetTest<T extends TaskManager> {

    protected InMemoryHistoryManager historyManager;
    protected T taskManager;

    public abstract void preparation();

    @Test
    public void checkGetPrioritizedTasks() throws TaskCreateException, TimeIntersectionException {
        Task taskLatest = testTaskTemplateGen();
        Task taskEarliest = testTaskTemplateGen();
        Task taskMiddle = testTaskTemplateGen();

        taskEarliest.setStartTime(LocalDateTime.of(2022,4,21,18,30));
        taskMiddle.setStartTime(LocalDateTime.of(2022,4,22,10,0));
        taskLatest.setStartTime(LocalDateTime.of(2022,4,22,18,30));

        taskManager.createTask(taskLatest);
        taskManager.createTask(taskEarliest);
        taskManager.createTask(taskMiddle);

        List<Task> testList = new ArrayList<>(taskManager.getPrioritizedTasks());

        for (Task task : testList) {
            System.out.println(task);
        }

        final int EARLIEST_POS = 0;
        final int LATEST_POS = 2;
        final int MIDDLE_POS = 1;

        assertEquals(taskEarliest, testList.get(EARLIEST_POS));
        assertEquals(taskMiddle, testList.get(MIDDLE_POS));
        assertEquals(taskLatest, testList.get(LATEST_POS));
    }

    @Test
    public void checkDeleteTaskFromPrioritizedSet() throws TaskCreateException, TaskDeleteException,
            TimeIntersectionException {
        Task task = testTaskTemplateGen();
        long id = task.getId();

        taskManager.createTask(task);
        assertNotEquals(0, taskManager.getPrioritizedTasks().size());

        taskManager.deleteTaskById(id);
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void checkDeleteEpicFromPrioritizedSet() throws TaskCreateException, TaskDeleteException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        long id = epic.getId();

        taskManager.createEpic(epic);
        assertNotEquals(0, taskManager.getPrioritizedTasks().size());

        taskManager.deleteEpicById(id);
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void checkDeleteSubTaskFromPrioritizedSet() throws TaskCreateException, TaskDeleteException,
            TimeIntersectionException {
        SubTask subTask = testSubTaskTemplateGen();
        Epic epic = testEpicTemplateGen();
        subTask.setEpic(epic);
        epic.addSubTask(subTask);
        long id = subTask.getId();

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        assertNotEquals(0, taskManager.getPrioritizedTasks().size());

        taskManager.deleteSubTaskById(id);
        // остаться должен только Epic
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public  void checkDeleteTasksFromPrioritizedSet() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        Task task1 = testTaskTemplateGen();
        task1.setStartTime(LocalDateTime.of(28,4,22,18,30));
        taskManager.createTask(task1);
        taskManager.createTask(task);
        assertNotEquals(0, taskManager.getPrioritizedTasks().size());

        taskManager.deleteTasks();
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public  void checkDeleteEpicsFromPrioritizedSet() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        assertNotEquals(0, taskManager.getPrioritizedTasks().size());

        taskManager.deleteEpics();
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public  void checkDeleteSubTasksFromPrioritizedSet() throws TaskCreateException, TimeIntersectionException {
        SubTask subTask1 = testSubTaskTemplateGen();
        SubTask subTask2 = testSubTaskTemplateGen();
        Epic epic = testEpicTemplateGen();

        subTask1.setEpic(epic);
        subTask2.setEpic(epic);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        subTask2.setStartTime(LocalDateTime.now());

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertNotEquals(0, taskManager.getPrioritizedTasks().size());

        taskManager.deleteSubTasks();
        // остаться должен только 1 Epic
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }
}
