package http;

import com.sun.net.httpserver.HttpServer;
import service.managers.TaskManager;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        TaskManager taskManager = Managers.getDefault();
        server.createContext("/tasks", new HttpTaskHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("Запуск сервера HttpTaskServer на порту: " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер HttpTaskServer остановлен");
    }
}

