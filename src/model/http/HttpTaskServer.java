package model.http;

import com.sun.net.httpserver.HttpServer;
import service.managers.HttpTaskManager;
import service.managers.InMemoryHistoryManager;
import service.managers.TaskManager;
import util.Managers;
import util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        taskManager = Managers.getDefault();
        server.createContext("/tasks", new HttpTaskHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("Запуск HttpTaskServer на порту: " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HttpTaskServer остановлен");
    }

    /**
     * Возвращает apiKey по которому будет производится запись и загрузка состояния HttpTaskManager в KVServer
     */
    public String getApiKey() {
         String apiKey = "";
        try {
            HttpTaskManager httpTaskManager = (HttpTaskManager) taskManager;
            apiKey = httpTaskManager.getApiKey();
        } catch (ClassCastException e) {
            System.out.println("Для работы HttpTaskServer должна использоваться реализация менеджера: HttpTaskManager");
            e.printStackTrace();
        }
        return apiKey;
    }

    // todo method MUST BE DELETED! after tests
    public TaskManager getTaskManager() {
        return taskManager;
    }
}

