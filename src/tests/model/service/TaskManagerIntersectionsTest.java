package model.service;

import model.exceptions.TaskCreateException;
import model.exceptions.TimeIntersectionException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.InMemoryHistoryManager;
import service.managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.service.TaskForTestsGenerator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class TaskManagerIntersectionsTest<T extends TaskManager> {

    protected InMemoryHistoryManager historyManager;
    protected T taskManager;

    public abstract void preparation();

    // методы checkTimeIntersectionsTest 1-8  проверяют на корректность работы все возможные варианты
    // затрагивающие механизм проверки пересечения задач по времени
    @Test
    public void checkTimeIntersectionsTest1() throws TaskCreateException, TimeIntersectionException {
        // у обоих задач время не задано
        Task checkedTaskNullNull = testTaskTemplateGen();
        Task existsTaskNullNull = testTaskTemplateGen();

        taskManager.createTask(existsTaskNullNull);
        taskManager.createTask(checkedTaskNullNull);
        // проверяем, что вторая задача действительно создалась
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void checkTimeIntersectionsTest2() throws TaskCreateException, TimeIntersectionException {
        //отдельная проверка пары Epic - SubTas. Время старта рассчитается одинаковое,
        //но в этом случае так и должно быть: особенность работы класса Epic
        Epic existsEpic = testEpicTemplateGen();
        taskManager.createEpic(existsEpic);
        SubTask checkedSubTask = testSubTaskTemplateGen();
        checkedSubTask.setStartTime(LocalDateTime.now());
        checkedSubTask.setEpic(existsEpic);
        existsEpic.addSubTask(checkedSubTask);
        taskManager.createSubTask(checkedSubTask);
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void checkTimeIntersectionsTest3() throws TaskCreateException, TimeIntersectionException {
        // одинаковое время старта обоих задач
        LocalDateTime time = LocalDateTime.of(2022,4,29,11,0);
        Task existsTask = testTaskTemplateGen();
        Task checkedTask = testTaskTemplateGen();

        existsTask.setStartTime(time);
        taskManager.createTask(existsTask);

        checkedTask.setStartTime(time);

        assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(checkedTask));
    }

    @Test
    public void checkTimeIntersectionsTest4() throws TaskCreateException, TimeIntersectionException {
        //старт добавляемой задачи задан, у существующей null
        LocalDateTime time = LocalDateTime.of(2022,4,29,11,0);
        Task existsTask = testTaskTemplateGen();
        Task checkedTask = testTaskTemplateGen();

        taskManager.createTask(existsTask);
        checkedTask.setStartTime(time);
        taskManager.createTask(checkedTask);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void checkTimeIntersectionsTest5() throws TaskCreateException, TimeIntersectionException {
        //старт добавляемой задачи null, у существующей задан
        LocalDateTime time = LocalDateTime.of(2022,4,29,11,0);
        Task existsTask = testTaskTemplateGen();
        Task checkedTask = testTaskTemplateGen();

        existsTask.setStartTime(time);
        taskManager.createTask(existsTask);
        taskManager.createTask(checkedTask);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void checkTimeIntersectionsTest6() throws TaskCreateException, TimeIntersectionException {
        // пересечение!
        // у проверяемой задачи задан период, у существующей только начало
        LocalDateTime time = LocalDateTime.of(2022,4,29,11,0);
        Duration duration = Duration.ofHours(4);
        Task existsTask = testTaskTemplateGen();
        Task checkedTask = testTaskTemplateGen();

        checkedTask.setStartTime(time);
        checkedTask.setDuration(duration);
        existsTask.setStartTime(time.plus(duration.minusHours(2)));
        taskManager.createTask(existsTask);

        assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(checkedTask));
    }

    @Test
    public void checkTimeIntersectionsTest7() throws TaskCreateException, TimeIntersectionException {
        // пересечение!
        // у проверяемой задачи только начало, у существующей задан период
        LocalDateTime time = LocalDateTime.of(2022,4,29,11,0);
        Duration duration = Duration.ofHours(4);
        Task existsTask = testTaskTemplateGen();
        Task checkedTask = testTaskTemplateGen();

        existsTask.setStartTime(time);
        existsTask.setDuration(duration);
        taskManager.createTask(existsTask);

        checkedTask.setStartTime(time.plusHours(2));

        assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(checkedTask));
    }

    @Test
    public void checkTimeIntersectionsTest8() throws TaskCreateException, TimeIntersectionException {
        // пересечение периодов выполнения обоих задач
        LocalDateTime time = LocalDateTime.of(2022,4,29,11,0);
        Duration duration = Duration.ofHours(4);
        Task existsTask = testTaskTemplateGen();
        Task checkedTask = testTaskTemplateGen();

        existsTask.setStartTime(time);
        existsTask.setDuration(duration);
        taskManager.createTask(existsTask);

        checkedTask.setStartTime(time.plusHours(2));
        checkedTask.setDuration(duration.plusHours(2));

        assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(checkedTask));
    }
}
