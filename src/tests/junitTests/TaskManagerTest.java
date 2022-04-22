import model.Status;
import model.exceptions.TaskCreateException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.IdGenerator;
import service.InMemoryHistoryManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {

    protected InMemoryHistoryManager historyManager;
    protected TaskManager taskManager;
    protected IdGenerator idGenerator;

    @BeforeEach
    protected abstract void preparation();

    @Test
    public void checkEmptyTasksListNotNull() {
        assertNotNull(taskManager.getTasksList());
    }

    @Test
    public void checkStandardTasksListAddedRight() throws TaskCreateException {
        Task task = new Task("TestTask", "TestDescription", idGenerator);
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTasksList().size());
    }

    @Test
    public void checkTasksIdValidatesAndTasksListIsEmpty() {
        Task task = new Task("TestTask", "TestDescription", idGenerator);
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
    public void checkStandardEpicsListAddedRight() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicsList().size());
    }

    @Test
    public void checkEpicIdValidatesAndEpicsListIsEmpty() {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
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
    public void checkStandardSubTasksListAddedRight() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        SubTask subTask = new SubTask("TestSubTask", "TestSubTask description", idGenerator);
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(1, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkSubTaskIdValidatesAndSubTasksListIsEmpty() {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        SubTask subTask = new SubTask("TestSubTask", "TestSubTask description", idGenerator);
        subTask.setId(-1);
        subTask.setEpic(epic);
        TaskCreateException ex = assertThrows(
                TaskCreateException.class,
                () -> taskManager.createSubTask(subTask)
        );
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteTaskFunctionDeleteTaskFromTasksList() throws TaskCreateException {
        Task task = new Task("TestTask", "TestDescription", idGenerator);
        taskManager.createTask(task);
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void  checkDeleteTaskFunctionWorkWithEmptyTasksList() throws TaskCreateException {
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkDeleteEpicFunctionDeleteEpicFromEpicsList() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
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
    public void checkDeleteSubTaskFunctionDeleteSubTaskFromEpicsList() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        SubTask subTask = new SubTask("TestSubTask", "TestSubTask description", idGenerator);
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
        taskManager.deleteSubTasks();
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteSubTaskFunctionWorkWithEmptySubTaskList() {
        taskManager.deleteSubTasks();
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkGetTaskByIdStandard() throws TaskCreateException {
        Task task = new Task("TestTask", "TestDescription", idGenerator);
        taskManager.createTask(task);
        long controlId = task.getId();
        assertEquals(task, taskManager.getTaskById(controlId));
    }

    @Test
    public void checkGetTaskByIdWhenTasksEmpty() throws TaskCreateException {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetTaskByIdWithIncorrectId() throws TaskCreateException {
        Task task = new Task("TestTask", "TestDescription", idGenerator);
        taskManager.createTask(task);
        long testId = 2L;
        assertNotEquals(task.getId(), testId);
        assertEquals(1, taskManager.getTasksList().size());
        assertNull(taskManager.getTaskById(testId));
    }

    @Test
    public void checkGetEpicByIdStandard() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        taskManager.createEpic(epic);
        long id = epic.getId();
        assertEquals(epic, taskManager.getEpicById(id));
    }

    @Test
    public void checkGetEpicByIdWhenEpicsEmpty() throws TaskCreateException {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetEpicByIdWithIncorrectId() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        taskManager.createEpic(epic);
        long testId = 2L;
        assertNotEquals(testId, epic.getId());
        assertEquals(1, taskManager.getEpicsList().size());
        assertNull(taskManager.getEpicById(testId));
    }

    @Test
    public void checkGetSubTaskByIdStandard() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        SubTask subTask = new SubTask("TestSubTask", "TestSubTask description", idGenerator);
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
        long id  = subTask.getId();
        assertEquals(subTask, taskManager.getSubTaskById(id));
    }

    @Test
    public void checkGetSubTaskByIdWhenSubTasksEmpty() throws TaskCreateException {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetSubTaskByIdWithIncorrectId() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        SubTask subTask = new SubTask("TestSubTask", "TestSubTask description", idGenerator);
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
        long testId = 2L;
        assertNotEquals(testId, subTask.getId());
        assertNull(taskManager.getSubTaskById(testId));
    }

    @Test
    public void checkCreateTaskStandard() throws TaskCreateException {
        Task task = new Task("TestTask", "TestDescription", idGenerator);
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
        Task task = new Task("TestTask", "TestDescription", idGenerator);
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
    public void checkCreateEpicStandard() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
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
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
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
    public void checkCreateSubTaskStandard() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        SubTask subTask = new SubTask("TestSubTask", "TestSubTask description", idGenerator);
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());

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
    public void checkCreateSubTaskWithIncorrectId() throws TaskCreateException {
        Epic epic = new Epic("TestEpic", "TestEpic description", idGenerator);
        SubTask subTask = new SubTask("TestSubTask", "TestSubTask description", idGenerator);
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
    public void checkUpdateTaskStandard() {

    }
}
