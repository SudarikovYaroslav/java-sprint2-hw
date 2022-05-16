package service.managers;

import com.google.gson.Gson;
import model.http.KVTaskClient;

import java.io.IOException;

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
        try {
            kvTaskClient.put(kvTaskClient.getApiKey(), gson.toJson(this));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getApiKey() {
        return api_key;
    }
}
