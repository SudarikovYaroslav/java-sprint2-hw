import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskLoadException;
import main.model.exceptions.TaskSaveException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.FileBackedTaskManager;
import main.service.HistoryManager;
import main.service.IdGenerator;
import main.service.InMemoryHistoryManager;
import main.util.Managers;
import main.util.Util;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws TaskSaveException, TaskCreateException, TaskLoadException {
//        IdGenerator idGenerator = IdGenerator.getInstance();
//        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
//        LocalDateTime startTime = LocalDateTime.now();
//        Duration duration = Duration.ofMinutes(150);
//        FileBackedTaskManager taskManager = new FileBackedTaskManager(
//                historyManager,
//                Util.getBackedPath(),
//                idGenerator
//        );
//
//        Task task = new Task("Task", "test task", idGenerator);
//        Epic epic = new Epic("Epic", "test epic", idGenerator);
//        SubTask subTask = new SubTask("SubTask", "test subTask", idGenerator);
//
//        subTask.setEpic(epic);
//        epic.addSubTask(subTask);
//        task.setStartTime(startTime);
//        task.setDuration(duration);
//
//        taskManager.createTask(task);
//        taskManager.createEpic(epic);
//        taskManager.createSubTask(subTask);
//
//        taskManager.save();


        FileBackedTaskManager reloadedTaskManager = Managers.loadFromFile(Util.getBackedPath());

        System.out.println(reloadedTaskManager.getTaskById(1));
        System.out.println(reloadedTaskManager.getEpicById(2));
        System.out.println(reloadedTaskManager.getSubTaskById(3));
    }
}
