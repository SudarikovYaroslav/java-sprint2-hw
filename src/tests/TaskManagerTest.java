package test;

import main.model.Status;
import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskDeleteException;
import main.model.exceptions.TaskUpdateException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import org.junit.jupiter.api.Test;
import main.service.IdGenerator;
import main.service.InMemoryHistoryManager;
import main.service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {

    protected InMemoryHistoryManager historyManager;
    protected TaskManager taskManager;
    protected IdGenerator idGenerator;

    private void linkEpicWithSubTask(Epic epic, SubTask subTask) throws TaskCreateException {
        subTask.setEpic(epic);
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);
        assertNotNull(subTask.getEpic());
        assertEquals(Status.NEW, epic.getStatus());
    }

    private Task testTaskTemplateGen() {
        return new Task("TestTask", "TestDescription", idGenerator);
    }

    private Epic testEpicTemplateGen() {
        return new Epic("TestEpic", "TestEpic description", idGenerator);
    }

    private SubTask testSubTaskTemplateGen() {
        return new SubTask("TestSubTask", "TestSubTask description", idGenerator);
    }

    private Task testInProgressTaskTemplateGen() {
        Task task = new Task("TestUpdatedTask", "Task in progress status", idGenerator);
        task.setStatus(Status.IN_PROGRESS);
        return task;
    }
    private Epic testInProgressEpicTemplateGen() {
        Epic epic = new Epic("TestUpdatedEpic", "Epic in progress status", idGenerator);
        epic.setStatus(Status.IN_PROGRESS);
        return epic;
    }

    private SubTask testInProgressSubTaskTemplateGen() {
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
    public void checkStandardTasksListAddedRight() throws TaskCreateException {
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
    public void checkStandardEpicsListAddedRight() throws TaskCreateException {
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
    public void checkStandardSubTasksListAddedRight() throws TaskCreateException {
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
    public void checkDeleteTaskFunctionDeleteTaskFromTasksList() throws TaskCreateException {
        Task task = testTaskTemplateGen();
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
    public void checkDeleteSubTaskFunctionDeleteSubTaskFromEpicsList() throws TaskCreateException {
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
    public void checkGetTaskByIdStandard() throws TaskCreateException {
        Task task = testTaskTemplateGen();
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
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long testId = 2L;
        assertNotEquals(task.getId(), testId);
        assertEquals(1, taskManager.getTasksList().size());
        assertNull(taskManager.getTaskById(testId));
    }

    @Test
    public void checkGetEpicByIdStandard() throws TaskCreateException {
        Epic epic = testEpicTemplateGen();
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
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long testId = 2L;
        assertNotEquals(testId, epic.getId());
        assertEquals(1, taskManager.getEpicsList().size());
        assertNull(taskManager.getEpicById(testId));
    }

    @Test
    public void checkGetSubTaskByIdStandard() throws TaskCreateException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        long id  = subTask.getId();
        assertEquals(subTask, taskManager.getSubTaskById(id));
    }

    @Test
    public void checkGetSubTaskByIdWhenSubTasksEmpty() throws TaskCreateException {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void checkGetSubTaskByIdWithIncorrectId() throws TaskCreateException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic,subTask);
        long testId = 2L;
        assertNotEquals(testId, subTask.getId());
        assertNull(taskManager.getSubTaskById(testId));
    }

    @Test
    public void checkCreateTaskStandard() throws TaskCreateException {
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
    public void checkCreateEpicStandard() throws TaskCreateException {
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
    public void checkCreateSubTaskStandard() throws TaskCreateException {
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
    public void checkCreateSubTaskWithIncorrectId() throws TaskCreateException {
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
    public void checkUpdateTaskStandard() throws TaskCreateException, TaskUpdateException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();

        Task updatedTask = testInProgressTaskTemplateGen();
        updatedTask.setId(id);
        taskManager.updateTask(updatedTask);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(id).getStatus());
    }

    @Test
    public void checkUpdateTaskNull() throws TaskCreateException {
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
    public void checkUpdateTaskWithIncorrectId() throws TaskCreateException {
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
    public void checkUpdateEpicStandard() throws TaskCreateException, TaskUpdateException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();

        Epic updatedEpic = testInProgressEpicTemplateGen();
        updatedEpic.setId(id);
        taskManager.updateEpic(updatedEpic);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(id).getStatus());
    }

    @Test
    public void checkUpdateEpicNull() throws TaskCreateException {
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
    public void checkUpdateEpicWithIncorrectId() throws TaskCreateException {
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
    public void checkUpdateSubTaskStandard() throws TaskCreateException, TaskUpdateException {
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
    public void checkUpdateSubTaskNull() throws TaskCreateException {
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
    public void checkUpdateSubTaskWithIncorrectId() throws TaskCreateException {
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
    public void checkDeleteTaskById() throws TaskCreateException, TaskDeleteException {
        Task task = testTaskTemplateGen();
        taskManager.createTask(task);
        long id = task.getId();
        taskManager.deleteTaskById(id);
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void checkDeleteTaskByIncorrectId() throws TaskCreateException {
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
    public void checkDeleteEpicById() throws TaskCreateException, TaskDeleteException {
        Epic epic = testEpicTemplateGen();
        taskManager.createEpic(epic);
        long id = epic.getId();
        taskManager.deleteEpicById(id);
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void checkDeleteEpicByIncorrectId() throws TaskCreateException {
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
    public void checkDeleteSubTaskById() throws TaskCreateException, TaskDeleteException {
        Epic epic = testEpicTemplateGen();
        SubTask subTask = testSubTaskTemplateGen();
        linkEpicWithSubTask(epic, subTask);
        taskManager.createSubTask(subTask);

        long id = subTask.getId();
        taskManager.deleteSubTaskById(id);
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    public void checkDeleteSubTaskByIncorrectId() throws TaskCreateException {
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
    public void checkGetSubTasks() throws TaskCreateException {
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
    public void checkGetSubTasksFromNullEpic() throws TaskCreateException {
        Epic epic = null;

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.getSubTasks(epic)
        );

        assertEquals("Epic = null! при попытке getSubTasks()", ex.getMessage());
    }
}
