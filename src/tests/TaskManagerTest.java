import main.model.Status;
import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskDeleteException;
import main.model.exceptions.TaskUpdateException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.IdGenerator;
import main.service.InMemoryHistoryManager;
import main.service.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {

    protected InMemoryHistoryManager historyManager;
    protected T taskManager;
    protected IdGenerator idGenerator;

    private void linkEpicWithSubTask(Epic epic, SubTask subTask) throws TaskCreateException, TimeIntersectionException {
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
    }

    protected Task testTaskTemplateGen() {
        return new Task("TestTask", "TestDescription", idGenerator);
    }

    protected Epic testEpicTemplateGen() {
        return new Epic("TestEpic", "TestEpic description", idGenerator);
    }

    protected SubTask testSubTaskTemplateGen() {
        return new SubTask("TestSubTask", "TestSubTask description", idGenerator);
    }

    protected Task testInProgressTaskTemplateGen() {
        Task task = new Task("TestUpdatedTask", "Task in progress status", idGenerator);
        task.setStatus(Status.IN_PROGRESS);
        return task;
    }
    protected Epic testInProgressEpicTemplateGen() {
        Epic epic = new Epic("TestUpdatedEpic", "Epic in progress status", idGenerator);
        epic.setStatus(Status.IN_PROGRESS);
        return epic;
    }

    protected SubTask testInProgressSubTaskTemplateGen() {
        SubTask subTask = new SubTask("TestUpdatedSubTask", "SubTask in progress status", idGenerator);
        subTask.setStatus(Status.IN_PROGRESS);
        return subTask;
    }

    protected abstract void preparation();

    @Test
    public void checkEmptyTasksListNotNull() {
        assertNotNull(taskManager.getTasksList());
    }

    @Test
    public void checkStandardTasksListAddedRight() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTasksList().size());
    }

    @Test
    public void checkTasksIdValidatesAndTasksListIsEmpty() {
        Task task = testTaskTemplateGen();
        task.setId(-1);

        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );

        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkEmptyEpicsListNotNull() {
        assertNotNull(taskManager.getEpicsList());
    }

    @Test
    public void checkStandardEpicsListAddedRight() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicsList().size());
    }

    @Test
    public void checkEpicIdValidatesAndEpicsListIsEmpty() {
        Epic epic = testEpicTemplateGen();
        epic.setId(-1);

        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(epic)
        );

        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkEmptySubTasksListNotNull() {
        assertNotNull(taskManager.getSubTasksList());
    }

    @Test
    public void checkStandardSubTasksListAddedRight() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        assertEquals(1, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkSubTaskIdValidatesAndSubTasksListIsEmpty() {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        subTask.setId(-1);
        subTask.setEpic(epic);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteTaskFunctionDeleteTaskFromTasksList() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void  checkDeleteTaskFunctionWorkWithEmptyTasksList() {
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkDeleteEpicFunctionDeleteEpicFromEpicsList() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkDeleteEpicFunctionWorkWithEmptyTasksList() {
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkDeleteSubTaskFunctionDeleteSubTaskFromEpicsList() throws TaskCreateException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.deleteSubTasks();
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteSubTaskFunctionWorkWithEmptySubTaskList() {
        taskManager.deleteSubTasks();
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkGetTaskByIdStandard() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long controlId = task.getId();
        assertEquals(task, taskManager.getTaskById(controlId));
    }

    @Test
    public void checkGetTaskByIdWhenTasksEmpty() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetTaskByIdWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long testId = 2L;
        assertNotEquals(task.getId(), testId);
        assertEquals(1, taskManager.getTasksList().size());
        assertNull(taskManager.getTaskById(testId));
    }

    @Test
    public void checkGetEpicByIdStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();
        assertEquals(epic, taskManager.getEpicById(id));
    }

    @Test
    public void checkGetEpicByIdWhenEpicsEmpty() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetEpicByIdWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long testId = 2L;
        assertNotEquals(testId, epic.getId());
        assertEquals(1, taskManager.getEpicsList().size());
        assertNull(taskManager.getEpicById(testId));
    }

    @Test
    public void checkGetSubTaskByIdStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        long id  = subTask.getId();
        assertEquals(subTask, taskManager.getSubTaskById(id));
    }

    @Test
    public void checkGetSubTaskByIdWhenSubTasksEmpty() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetSubTaskByIdWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic,subTask);
        long testId = 2L;
        assertNotEquals(testId, subTask.getId());
        assertNull(taskManager.getSubTaskById(testId));
    }

    @Test
    public void checkCreateTaskStandard() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTasksList().size());
        assertEquals(task, taskManager.getTasksList().get(0));
    }

    @Test
    public void checkCreateTaskWithNullValue() {
        Task task = null;
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );
        assertEquals("При создании task == null", ex.getMessage());
    }

    @Test
    public void checkCreateTaskWithIncorrectId() {
        Task task = testTaskTemplateGen();
        task.setId(0);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );
        assertEquals("При создании Task id должен быть больше 0. Actual: " + task.getId(), ex.getMessage());

        task.setId(-1);
        ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createTask(task)
        );
        assertEquals("При создании Task id должен быть больше 0. Actual: " + task.getId(), ex.getMessage());
    }

    @Test
    public void checkCreateEpicStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicsList().size());
        assertEquals(epic, taskManager.getEpicsList().get(0));
    }

    @Test
    public void checkCreateEpicWithNullValue() {
        Epic epic = null;
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createEpic(epic)
        );
        assertEquals("При создании Epic == null", ex.getMessage());
    }

    @Test
    public void checkCreateEpicWithIncorrectId() {
        Epic epic = testEpicTemplateGen();
        epic.setId(0);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createEpic(epic)
        );
        assertEquals("При создании Epic id должен быть больше 0. Actual: " + epic.getId(), ex.getMessage());

        epic.setId(-1);
        ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createEpic(epic)
        );
        assertEquals("При создании Epic id должен быть больше 0. Actual: " + epic.getId(), ex.getMessage());
    }

    @Test
    public void checkCreateSubTaskStandard() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        assertEquals(1, taskManager.getSubTasksList().size());
        assertEquals(subTask, taskManager.getSubTasksList().get(0));
    }

    @Test
    public void checkCreateSubTaskWithNullValue() {
        SubTask subTask = null;
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals("При создании SubTask == null", ex.getMessage());
    }

    @Test
    public void checkCreateSubTaskWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);

        subTask.setId(0);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals("При создании SubTask id должен быть больше 0. Actual: "
                + subTask.getId(), ex.getMessage()
        );

        subTask.setId(-1);
        ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals("При создании SubTask id должен быть больше 0. Actual: "
                + subTask.getId(), ex.getMessage()
        );
    }

    @Test
    public void checkUpdateTaskStandard() throws TaskCreateException, TaskUpdateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();

        Task updatedTask = testInProgressTaskTemplateGen();
        updatedTask.setId(id);
        taskManager.updateTask(updatedTask);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(id).getStatus());
    }

    @Test
    public void checkUpdateTaskNull() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);

        Task updatedTask = null;
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () -> taskManager.updateTask(updatedTask)
        );

        assertEquals("Обновляемая Task = null", ex.getMessage());
    }

    @Test
    public void checkUpdateTaskWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);

        Task updatedTask = testInProgressTaskTemplateGen();
        updatedTask.setId(0);

        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateTask(updatedTask)
        );
        assertEquals("id обновляемой Task должен быть больше 0! Actual: " + updatedTask.getId(),
                ex.getMessage()
        );

        updatedTask.setId(-1);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateTask(updatedTask)
        );
        assertEquals("id обновляемой Task должен быть больше 0! Actual: " + updatedTask.getId(),
                ex.getMessage()
        );

        long id = 123;
        assertNotEquals(task.getId(), id);
        updatedTask.setId(id);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateTask(updatedTask)
        );
        assertEquals("Task с id: " + updatedTask.getId() + " не существует. Обновление невозможно!",
                ex.getMessage()
        );
    }

    @Test
    public void checkUpdateEpicStandard() throws TaskCreateException, TaskUpdateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();

        Epic updatedEpic = testInProgressEpicTemplateGen();
        updatedEpic.setId(id);
        taskManager.updateEpic(updatedEpic);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(id).getStatus());
    }

    @Test
    public void checkUpdateEpicNull() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);

        Epic updatedEpic = null;
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () -> taskManager.updateEpic(updatedEpic)
        );

        assertEquals("Обновляемый Epic = null", ex.getMessage());
    }

    @Test
    public void checkUpdateEpicWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);

        Epic updatedEpic = testInProgressEpicTemplateGen();
        updatedEpic.setId(0);

        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateEpic(updatedEpic)
        );
        assertEquals("id обновляемого Epic должен быть больше 0! Actual: " + updatedEpic.getId(),
                ex.getMessage()
        );

        updatedEpic.setId(-1);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateEpic(updatedEpic)
        );
        assertEquals("id обновляемого Epic должен быть больше 0! Actual: " + updatedEpic.getId(),
                ex.getMessage()
        );

        long id = 123;
        assertNotEquals(epic.getId(), id);
        updatedEpic.setId(id);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateEpic(updatedEpic)
        );
        assertEquals("Epic с id: " + updatedEpic.getId() + " не существует. Обновление невозможно!",
                ex.getMessage()
        );
    }

    @Test
    public void checkUpdateSubTaskStandard() throws TaskCreateException, TaskUpdateException,
            TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask  = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        long id = subTask.getId();

        SubTask updatedSubTask = testInProgressSubTaskTemplateGen();
        epic.addSubTask(updatedSubTask);
        updatedSubTask.setEpic(epic);
        updatedSubTask.setId(id);
        taskManager.updateSubTask(updatedSubTask);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubTaskById(id).getStatus());
    }

    @Test
    public void checkUpdateSubTaskNull() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        SubTask updatedSubTask = null;
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () -> taskManager.updateSubTask(updatedSubTask)
        );

        assertEquals("Обновляемая SubTask = null", ex.getMessage());
    }

    @Test
    public void checkUpdateSubTaskWithIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        SubTask updatedSubTask = testInProgressSubTaskTemplateGen();
        updatedSubTask.setEpic(epic);

        updatedSubTask.setId(0);
        TaskUpdateException ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateSubTask(updatedSubTask)
        );
        assertEquals("id обновляемой SubTask должен быть больше 0! Actual: " + updatedSubTask.getId(),
                ex.getMessage()
        );

        updatedSubTask.setId(-1);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateSubTask(updatedSubTask)
        );
        assertEquals("id обновляемой SubTask должен быть больше 0! Actual: " + updatedSubTask.getId(),
                ex.getMessage()
        );

        long id = 123;
        assertNotEquals(subTask.getId(), id);
        updatedSubTask.setId(id);
        ex = assertThrows(
                TaskUpdateException.class,
                () ->  taskManager.updateSubTask(updatedSubTask)
        );
        assertEquals("SubTask с id: " + updatedSubTask.getId() + " не существует. Обновление невозможно!",
                ex.getMessage()
        );
    }

    @Test
    public void checkDeleteTaskById() throws TaskCreateException, TaskDeleteException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.deleteTaskById(id);
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkDeleteTaskByIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = 123;
        assertNotEquals(id, task.getId());

        TaskDeleteException ex = assertThrows(
                TaskDeleteException.class,
                () -> taskManager.deleteTaskById(id)
        );
        assertEquals("Task с id: " + id + " не существует. Удаление не возможно!", ex.getMessage());
    }

    @Test
    public void checkDeleteEpicById() throws TaskCreateException, TaskDeleteException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();
        taskManager.deleteEpicById(id);
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkDeleteEpicByIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = 123;
        assertNotEquals(id, epic.getId());

        TaskDeleteException ex = assertThrows(
                TaskDeleteException.class,
                () -> taskManager.deleteEpicById(id)
        );
        assertEquals("Epic с id: " + id + " не существует. Удаление не возможно!", ex.getMessage());
    }

    @Test
    public void checkDeleteSubTaskById() throws TaskCreateException, TaskDeleteException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        long id = subTask.getId();
        taskManager.deleteSubTaskById(id);
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteSubTaskByIncorrectId() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        long id = 123;
        assertNotEquals(id, subTask.getId());

        TaskDeleteException ex = assertThrows(
                TaskDeleteException.class,
                () -> taskManager.deleteSubTaskById(id)
        );
        assertEquals("SubTask с id: " + id + " не существует. Удаление не возможно!", ex.getMessage());
    }

    @Test
    public void checkGetSubTasks() throws TaskCreateException, TimeIntersectionException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);

        List<SubTask> epicsSubTasks = taskManager.getSubTasks(epic);
        assertEquals(subTask.getId(), epicsSubTasks.get(0).getId());

        epic = testEpicTemplateGen();
        assertEquals(0, taskManager.getSubTasks(epic).size());
    }

    @Test
    public void checkGetSubTasksFromNullEpic() {
        Epic epic = null;

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.getSubTasks(epic)
        );

        assertEquals("Epic = null! при попытке getSubTasks()", ex.getMessage());
    }

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
