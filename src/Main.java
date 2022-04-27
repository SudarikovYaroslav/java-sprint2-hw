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
        IdGenerator idGenerator = IdGenerator.getInstance();
        LocalDateTime localDateTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(150);

        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(historyManager, Util.getBackedPath(), idGenerator);

        Task task = new Task("Task", "test task", idGenerator);
        taskManager.createTask(task);
        task.setStartTime(localDateTime);
        task.setDuration(duration);

        System.out.println("source task: " + task.toString());
        long taskId = task.getId();
        //taskManager.getTaskById(taskId);
        taskManager.save();
        FileBackedTaskManager loaded = Managers.loadFromFile(Util.getBackedPath());
        Task loadedTask = loaded.getTaskById(taskId);
        System.out.println("loaded task: " + loadedTask.toString());


        /*Epic epic = new Epic("Epic", "test epic", idGenerator);
        SubTask subTask = new SubTask("SubTask", "test subTask", idGenerator);
        subTask.setEpic(epic);
        epic.addSubTask(subTask);
        subTask.setStartTime(localDateTime);
        subTask.setDuration(duration);
        epic.setDuration(duration);
        epic.setStartTime(localDateTime);

        System.out.println(task.toString());
        System.out.println(epic);
        System.out.println(subTask);

        long epicId = epic.getId();
        long subTaskId = subTask.getId();


        try {
            taskManager.createTask(task);
            taskManager.createEpic(epic);
            taskManager.createSubTask(subTask);

            taskManager.save();
        } catch (TaskCreateException | TaskSaveException e) {
            e.printStackTrace();
        }

        System.out.println("************* После загрузки *************");

        try {
            FileBackedTaskManager reloadedTaskManager = Managers.loadFromFile(Util.getBackedPath());
            System.out.println(reloadedTaskManager.getTaskById(taskId));
            System.out.println(reloadedTaskManager.getEpicById(epicId));
            System.out.println(reloadedTaskManager.getSubTaskById(subTaskId));
        } catch (TaskLoadException e) {
            e.printStackTrace();
        }*/
    }
}
