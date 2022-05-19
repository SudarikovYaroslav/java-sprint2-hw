package service.managers;

import com.google.gson.Gson;
import model.http.HttpTaskManagerCondition;
import model.http.KVTaskClient;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.generators.HttpManagerApiKeyGenerator;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTaskManager {
    private final String apiKey;
    private final KVTaskClient kvTaskClient;
    private final Gson gson;


    public HttpTaskManager(HistoryManager historyManager, String kvServerUrl) {
        super(historyManager, kvServerUrl);
        kvTaskClient = new KVTaskClient(kvServerUrl);
        apiKey = HttpManagerApiKeyGenerator.generate();
        gson = new Gson();
    }

    @Override
    public void save() {
        HttpTaskManagerCondition taskManagerCondition = new HttpTaskManagerCondition(this);
        String jsonHttpTaskManagerCondition = gson.toJson(taskManagerCondition);

        try {
            kvTaskClient.put(apiKey, jsonHttpTaskManagerCondition);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getApiKey() {
        return apiKey;
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
