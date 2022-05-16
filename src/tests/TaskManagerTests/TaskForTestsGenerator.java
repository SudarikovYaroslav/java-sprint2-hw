package TaskManagerTests;

import model.Status;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.IdGenerator;

/**
 * Утилитарный класс, генерирующий задачи для тестов
 */
// перенёс сначала этот класс в пакет tests, но там его идея в упор не хочет видеть почему-то, в итоге перетащил сюда ((
public class TaskForTestsGenerator {

    public static Task testTaskTemplateGen() {
        return new Task("TestTask", "TestDescription",
                IdGenerator.generate());
    }

    public static Epic testEpicTemplateGen() {
        return new Epic("TestEpic", "TestEpic description",
                IdGenerator.generate());
    }

    public static SubTask testSubTaskTemplateGen() {
        return new SubTask("TestSubTask", "TestSubTask description", IdGenerator.generate());
    }

    public static Task testInProgressTaskTemplateGen() {
        Task task = new Task("TestUpdatedTask", "Task in progress status", IdGenerator.generate());
        task.setStatus(Status.IN_PROGRESS);
        return task;
    }

    public static Epic testInProgressEpicTemplateGen() {
        Epic epic = new Epic("TestUpdatedEpic", "Epic in progress status", IdGenerator.generate());
        epic.setStatus(Status.IN_PROGRESS);
        return epic;
    }

    public static SubTask testInProgressSubTaskTemplateGen() {
        SubTask subTask = new SubTask("TestUpdatedSubTask", "SubTask in progress status",
                IdGenerator.generate());
        subTask.setStatus(Status.IN_PROGRESS);
        return subTask;
    }
}
