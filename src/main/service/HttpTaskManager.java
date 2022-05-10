package main.service;

import com.google.gson.Gson;
import main.model.KVTaskClient;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTaskManager {
    private static String API_KEY;

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(HistoryManager historyManager, String kvServerUrl) {
        super(historyManager, kvServerUrl);
        kvTaskClient = new KVTaskClient(kvServerUrl);
        API_KEY = kvTaskClient.getAPI_KEY();
        gson = new Gson();
    }

    @Override
    public void save() {
        try {
            kvTaskClient.put(kvTaskClient.getAPI_KEY(), gson.toJson(this));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getApiKey() {
        return API_KEY;
    }

    /*public HttpTaskManager loadHTTPTaskManagerFromKVServer() throws IOException, InterruptedException {
        String jsonHTTPTaskManager = kvTaskClient.load(API_KEY);
        return gson.fromJson(jsonHTTPTaskManager, HttpTaskManager.class);
    }*/
}
