package main.model;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskDeleteException;
import main.model.exceptions.TaskUpdateException;
import main.model.exceptions.TimeIntersectionException;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.FileBackedTaskManager;
import main.util.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final FileBackedTaskManager taskManager = Managers.getFileBackedTaskManager();
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new HttpTaskHandler());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public class HttpTaskHandler implements HttpHandler {
        private static final int RESPONSE_OK = 200;
        private static final int RESPONSE_NOT_FOUND = 404;
        private static final int RESPONSE_BAD_REQUEST = 400;
        private static final int RESPONSE_METHOD_NOT_ALLOWED = 405;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = null;
            Gson gson = new Gson();
            String requestMethod = exchange.getRequestMethod();
            String uriPath = exchange.getRequestURI().getPath();
            boolean processed = false;

            switch (requestMethod) {
                case "GET" :
                    if (uriPath.endsWith("/tasks/")) {
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/history")) {
                        response = gson.toJson(taskManager.history());
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/task")) {
                        response = gson.toJson(taskManager.getTasksList());
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/epic")) {
                        response = gson.toJson(taskManager.getEpicsList());
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/subTask")) {
                        response = gson.toJson(taskManager.getSubTasksList());
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.contains("/task") && uriPath.contains("?")) {
                        int id = getIdFromUriPath(uriPath);
                        Task requestedTask = taskManager.getTaskById(id);

                        if (requestedTask != null) {
                            response = gson.toJson(requestedTask);
                            exchange.sendResponseHeaders(RESPONSE_OK, 0);
                        } else {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                    }

                    if (uriPath.contains("/epic") && uriPath.contains("?")) {
                        int id = getIdFromUriPath(uriPath);
                        Epic requestedEpic = taskManager.getEpicById(id);

                        if (requestedEpic != null) {
                            response = gson.toJson(requestedEpic);
                            exchange.sendResponseHeaders(RESPONSE_OK, 0);
                        } else {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                    }

                    if (uriPath.contains("/subTask") && uriPath.contains("?")) {
                        int id = getIdFromUriPath(uriPath);
                        SubTask requestedSubTask = taskManager.getSubTaskById(id);

                        if (requestedSubTask != null) {
                            response = gson.toJson(requestedSubTask);
                            exchange.sendResponseHeaders(RESPONSE_OK, 0);
                        } else {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                    }

                    if (!processed) exchange.sendResponseHeaders(RESPONSE_BAD_REQUEST, 0);
                    break;

                case "POST" :
                    if (uriPath.endsWith("/task")) {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        Task task = gson.fromJson(body, Task.class);

                        // Проверяем, существует ли задача с id как у принятой, если да - обновляем существующую,
                        // если нет - просто добавляем новую
                        try {
                            boolean update = false;
                            for (Task existedTask : taskManager.getTasksList()) {
                                if (task.getId() == existedTask.getId()) {
                                    taskManager.updateTask(task);
                                    update = true;
                                    break;
                                }
                            }

                            if (!update) taskManager.createTask(task);
                        } catch (TaskUpdateException | TimeIntersectionException | TaskCreateException e) {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/epic")) {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        Epic epic = gson.fromJson(body, Epic.class);

                        try {
                            boolean update = false;
                            for (Epic exitedEpic : taskManager.getEpicsList()) {
                                if (epic.getId() == exitedEpic.getId()) {
                                    taskManager.updateEpic(epic);
                                    update = true;
                                    break;
                                }
                            }

                            if (!update) taskManager.createEpic(epic);
                        } catch (TaskUpdateException | TimeIntersectionException | TaskCreateException e) {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/subTask")) {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        SubTask subTask = gson.fromJson(body, SubTask.class);

                        try {
                            boolean update = false;
                            for (SubTask existedSubTask : taskManager.getSubTasksList()) {
                                if (subTask.getId() == existedSubTask.getId()) {
                                    taskManager.updateSubTask(subTask);
                                    update = true;
                                    break;
                                }
                            }

                            if (!update) taskManager.createSubTask(subTask);
                        } catch (TaskUpdateException | TimeIntersectionException | TaskCreateException e) {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (!processed) exchange.sendResponseHeaders(RESPONSE_BAD_REQUEST, 0);
                    break;

                case "DELETE" :
                    if (uriPath.endsWith("/task")) {
                        taskManager.deleteTasks();
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/epic")) {
                        taskManager.deleteEpics();
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.endsWith("/subTask")) {
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.contains("/task") && uriPath.contains("?")) {
                        int id = getIdFromUriPath(uriPath);
                        try {
                            taskManager.deleteTaskById(id);
                        } catch (TaskDeleteException e) {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.contains("/epic") && uriPath.contains("?")) {
                        int id = getIdFromUriPath(uriPath);
                        try {
                            taskManager.deleteEpicById(id);
                        } catch (TaskDeleteException e) {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (uriPath.contains("/subTask") && uriPath.contains("?")) {
                        int id = getIdFromUriPath(uriPath);
                        try {
                            taskManager.deleteSubTaskById(id);
                        } catch (TaskDeleteException e) {
                            exchange.sendResponseHeaders(RESPONSE_NOT_FOUND, 0);
                        }
                        processed = true;
                        exchange.sendResponseHeaders(RESPONSE_OK, 0);
                    }

                    if (!processed) exchange.sendResponseHeaders(RESPONSE_BAD_REQUEST, 0);
                    break;

                default :
                    exchange.sendResponseHeaders(RESPONSE_METHOD_NOT_ALLOWED, 0);
            }

            try(OutputStream os = exchange.getResponseBody()) {
                if (response != null) {
                    os.write(response.getBytes());
                }
            }
        }

        private int getIdFromUriPath(String uriPath) {
            String idField = uriPath.split("\\?")[1];  // поле вида id=value
            return Integer.parseInt(idField.substring(idField.lastIndexOf("=") + 1));
        }
    }
}

