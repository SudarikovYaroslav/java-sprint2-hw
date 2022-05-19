import com.google.gson.Gson;
import model.http.HttpTaskServer;
import model.http.KVServer;
import model.tasks.Task;
import service.managers.HttpTaskManager;
import service.managers.InMemoryHistoryManager;
import service.managers.TaskManager;
import util.Managers;
import util.Util;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Task task = new Task("Task", "description", 1);
        Gson gson = new Gson();
        String json = gson.toJson(task);
        Task returnedTask = gson.fromJson(json, Task.class);

        System.out.println("Task: " + task);
        System.out.println("returnedTask: " + returnedTask);
    }
}
