package service.managers;

import com.google.gson.Gson;
import model.http.HttpTaskManagerCondition;
import model.http.KVTaskClient;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class HttpTaskManager extends FileBackedTaskManager {
    private final String api_key;
    private final KVTaskClient kvTaskClient;
    private final Gson gson;


    public HttpTaskManager(HistoryManager historyManager, String kvServerUrl) {
        super(historyManager, kvServerUrl);
        kvTaskClient = new KVTaskClient(kvServerUrl);
        api_key = kvTaskClient.getApiKey();
        gson = new Gson();
    }

    @Override
    public void save() {
        String key = kvTaskClient.getApiKey();
        HttpTaskManagerCondition taskManagerCondition = new HttpTaskManagerCondition(this);
        String jsonHttpTaskManagerCondition = gson.toJson(taskManagerCondition);

        try {
            kvTaskClient.put(key, jsonHttpTaskManagerCondition);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getApiKey() {
        return api_key;
    }

    public void setManagerCondition(HttpTaskManagerCondition condition) {
        for (Task task : condition.getTasks()) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }

        for (Epic epic : condition.getEpics()) {
            epics.put(epic.getId(), epic);
            prioritizedTasks.add(epic);
        }

        for (SubTask subTask : condition.getSubTasks()) {
            subTasks.put(subTask.getId(), subTask);
            prioritizedTasks.add(subTask);
        }

        for (Task viewedTask : condition.getLastViewedTasks()) {
            historyManager.add(viewedTask);
        }
    }
}
