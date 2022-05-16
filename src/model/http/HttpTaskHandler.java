package model.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.exceptions.TaskCreateException;
import model.exceptions.TaskDeleteException;
import model.exceptions.TaskUpdateException;
import model.exceptions.TimeIntersectionException;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import service.managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskHandler implements HttpHandler {

    private static final int STATUS_OK = 200;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final TaskManager taskManager;

    public HttpTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

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
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.endsWith("/history")) {
                    response = gson.toJson(taskManager.history());
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.endsWith("/task")) {
                    response = gson.toJson(taskManager.getTasksList());
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.endsWith("/epic")) {
                    response = gson.toJson(taskManager.getEpicsList());
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.endsWith("/subtask")) {
                    response = gson.toJson(taskManager.getSubTasksList());
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.contains("/task") && uriPath.contains("?")) {
                    int id = getIdFromUriPath(uriPath);
                    Task requestedTask = taskManager.getTaskById(id);

                    if (requestedTask != null) {
                        response = gson.toJson(requestedTask);
                        exchange.sendResponseHeaders(STATUS_OK, 0);
                    } else {
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                }

                if (uriPath.contains("/epic") && uriPath.contains("?")) {
                    int id = getIdFromUriPath(uriPath);
                    Epic requestedEpic = taskManager.getEpicById(id);

                    if (requestedEpic != null) {
                        response = gson.toJson(requestedEpic);
                        exchange.sendResponseHeaders(STATUS_OK, 0);
                    } else {
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                }

                if (uriPath.contains("/subtask") && uriPath.contains("?")) {
                    int id = getIdFromUriPath(uriPath);
                    SubTask requestedSubTask = taskManager.getSubTaskById(id);

                    if (requestedSubTask != null) {
                        response = gson.toJson(requestedSubTask);
                        exchange.sendResponseHeaders(STATUS_OK, 0);
                    } else {
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                }

                if (!processed) exchange.sendResponseHeaders(STATUS_BAD_REQUEST, 0);
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
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
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
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.endsWith("/subtask")) {
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
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (!processed) exchange.sendResponseHeaders(STATUS_BAD_REQUEST, 0);
                break;

            case "DELETE" :
                if (uriPath.endsWith("/task")) {
                    taskManager.deleteTasks();
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.endsWith("/epic")) {
                    taskManager.deleteEpics();
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.endsWith("/subtask")) {
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.contains("/task") && uriPath.contains("?")) {
                    int id = getIdFromUriPath(uriPath);
                    try {
                        taskManager.deleteTaskById(id);
                    } catch (TaskDeleteException e) {
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.contains("/epic") && uriPath.contains("?")) {
                    int id = getIdFromUriPath(uriPath);
                    try {
                        taskManager.deleteEpicById(id);
                    } catch (TaskDeleteException e) {
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (uriPath.contains("/subtask") && uriPath.contains("?")) {
                    int id = getIdFromUriPath(uriPath);
                    try {
                        taskManager.deleteSubTaskById(id);
                    } catch (TaskDeleteException e) {
                        exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0);
                    }
                    processed = true;
                    exchange.sendResponseHeaders(STATUS_OK, 0);
                }

                if (!processed) exchange.sendResponseHeaders(STATUS_BAD_REQUEST, 0);
                break;

            default :
                exchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, 0);
        }

        try(OutputStream os = exchange.getResponseBody()) {
            if (response != null) {
                os.write(response.getBytes());
            }
        }
    }

    // обрабатывает uriPath вида /tasks/task/?id=1"
    private int getIdFromUriPath(String uriPath) {
        String idField = uriPath.split("\\?")[1];  // поле вида id=value
        return Integer.parseInt(idField.substring(idField.lastIndexOf("=") + 1));
    }
}
