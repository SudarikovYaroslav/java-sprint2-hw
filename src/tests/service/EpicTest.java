package service;

import main.model.Status;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import org.junit.jupiter.api.Test;
import main.service.EpicStatusService;
import main.service.IdGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    private final IdGenerator idGenerator = IdGenerator.getInstance();
    private final EpicStatusService epicStatusService = new EpicStatusService();
    private SubTask subTask1;
    private SubTask subTask2;
    private Epic epic;

    private void preparation() {
        subTask1 = new SubTask("TestSubTask1", "TestSubTask1 description", idGenerator);
        subTask2 = new SubTask("TestSubTask2", "TestSubTask2 description", idGenerator);
        epic = new Epic("Test epic", "Test description", idGenerator);
        subTask1.setEpic(epic);
        subTask2.setEpic(epic);
    }


    @Test
    public void checkEmptyEpicStatusWhenItJustInit() {
        Epic epic = new Epic("Test epic", "Test description", idGenerator);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void checkEpicStatusWhenAllSubTasksNew() {
        preparation();
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void checkEpicStatusWhenAllSubTasksDone() {
        preparation();
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void checkEpicStatusWhenSubTasksNewAndDone() {
        preparation();
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.DONE);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkEpicStatusWhenSubTasksInProgress() {
        preparation();
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}