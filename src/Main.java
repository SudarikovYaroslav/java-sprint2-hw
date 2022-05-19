import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.http.HttpTaskServer;
import model.http.KVServer;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.HttpTaskManager;
import service.managers.InMemoryHistoryManager;
import service.managers.TaskManager;
import util.Managers;
import util.SubTaskSerializer;
import util.Util;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Epic epic = new Epic("Epic", "epic description", 1);
        SubTask subTask = new SubTask("SubTask", "subTask description", 2);
        epic.addSubTask(subTask);
        subTask.setEpic(epic);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(SubTask.class, new SubTaskSerializer()).create();
        String epicJson = gson.toJson(epic);
        String subTaskJson = gson.toJson(subTask);

        System.out.println("epicJson: " + epicJson);
        System.out.println("subTaskJson: " + epicJson);

        Epic epicFromJson = gson.fromJson(epicJson, Epic.class);
        SubTask subTaskFromJson = gson.fromJson(subTaskJson, SubTask.class);

        System.out.println("EpicFromJson:" + epicFromJson + "equals test = " + epic.equals(epicFromJson));
        System.out.println("SubTaskFromJson: " + subTaskFromJson + "equals test = " + subTask.equals(subTaskFromJson));

        System.out.println("===========SubTask==========");
        System.out.println("subTaskJson:  " + subTaskJson);
        System.out.println("get epic from subTsk source: " + subTask.getEpic());
        System.out.println("get epic from subTsk deseri: " + subTaskFromJson.getEpic());

        System.out.println("\n============Epic=================");
        System.out.println("epic source: " + epic);
        System.out.println("epic deseri: " + epicFromJson);
        epicFromJson.deleteSubTaskById(2);
        System.out.println("epic deseri: " + epicFromJson);

    }
}
