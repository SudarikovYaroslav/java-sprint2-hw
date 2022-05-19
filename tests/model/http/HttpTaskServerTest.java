package model.http;

import model.service.TaskForTestsGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import org.junit.jupiter.api.*;
import service.managers.TaskManager;
import util.Managers;
import util.SubTaskSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Тесты проверяют корректную работу цепочки HttpTaskServer -> HttpTaskManager <-> KVClient <-> KVServer
 * К httpTaskServer делаются запросы, он в свою очередь согласно мапингу вызывает нужный метод у HttpTaskManager,
 * в соответствии с переданным запросом. Далее HttpTaskManager выполняет нужный метод, и при помощи KVTaskClient
 * выполняет сохранение или загрузку своего состояния на сервере KVServer
 */

public class HttpTaskServerTest {
    private static final int STATUS_OK = 200;

    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SubTask.class, new SubTaskSerializer())
            .serializeNulls()
            .create();
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void preparation() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void termination() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void createTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task testTask = TaskForTestsGenerator.testTaskTemplateGen();
        String json = gson.toJson(testTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, response.statusCode());
    }

    @Test
    public void createEpicTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Epic testEpic = TaskForTestsGenerator.testEpicTemplateGen();
        String json = gson.toJson(testEpic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, response.statusCode());
    }

    @Test
    public void createSubTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        Epic testEpic = TaskForTestsGenerator.testEpicTemplateGen();
        SubTask testSubTask = TaskForTestsGenerator.testSubTaskTemplateGen();
        testEpic.addSubTask(testSubTask);
        testSubTask.setEpic(testEpic);

        String json = gson.toJson(testSubTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, response.statusCode());
    }

    @Test
    public void getTasksListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, response.statusCode());
    }

    @Test
    public void getEpicsListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, response.statusCode());
    }

    @Test
    public void getSubTasksListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, response.statusCode());
    }

    @Test
    public void deleteTasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task testTask = TaskForTestsGenerator.testTaskTemplateGen();
        String json = gson.toJson(testTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteTasksRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteTasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, deleteResponse.statusCode());
        assertEquals(0, deleteResponse.body().length());
    }

    @Test
    public void deleteEpicsTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        Epic testEpic = TaskForTestsGenerator.testEpicTemplateGen();
        String json = gson.toJson(testEpic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest createEpicRequest = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteEpicsRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteEpicsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, deleteResponse.statusCode());
        assertEquals(0, deleteResponse.body().length());
    }

    @Test
    public void deleteSubTasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        Epic testEpic = TaskForTestsGenerator.testEpicTemplateGen();
        SubTask testSubTask = TaskForTestsGenerator.testSubTaskTemplateGen();
        testEpic.addSubTask(testSubTask);
        testSubTask.setEpic(testEpic);

        String json = gson.toJson(testSubTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest createSubTaskRequest = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(createSubTaskRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteSubTasksRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteSubTasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, deleteResponse.statusCode());
        assertEquals(0, deleteResponse.body().length());
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        Task task = TaskForTestsGenerator.testTaskTemplateGen();
        long id = task.getId();
        URI createTaskUrl = URI.create("http://localhost:8080/tasks/task");
        URI taskByIdUrl = URI.create("http://localhost:8080/tasks/task?id=" + id);

        String jsonTask = gson.toJson(task);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest createTaskRequest = HttpRequest.newBuilder().uri(createTaskUrl).POST(body).build();
        HttpResponse<String> createResponse = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, createResponse.statusCode());

        HttpRequest checkTasksListNotEmpty = HttpRequest.newBuilder().uri(createTaskUrl).GET().build();
        HttpResponse<String> getTasksListResponse = client.send(checkTasksListNotEmpty,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, getTasksListResponse.statusCode());
        assertNotEquals(0, getTasksListResponse.body().length());

        HttpRequest getTaskByIdRequest = HttpRequest.newBuilder().uri(taskByIdUrl).GET().build();
        HttpResponse<String> getTaskByIdResponse = client.send(getTaskByIdRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, getTaskByIdResponse.statusCode());

        String loadedTask = getTaskByIdResponse.body();
        Task deserializeTask = gson.fromJson(loadedTask, Task.class);

        assertEquals(task, deserializeTask);
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = TaskForTestsGenerator.testEpicTemplateGen();
        long id = epic.getId();
        URI createEpicUrl = URI.create("http://localhost:8080/tasks/epic");
        URI epicByIdUrl = URI.create("http://localhost:8080/tasks/epic?id=" + id);

        String jsonEpic = gson.toJson(epic);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest createEpicRequest = HttpRequest.newBuilder(createEpicUrl).POST(body).build();
        HttpResponse<String> createResponse = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createResponse.statusCode());

        HttpRequest getEpicByIdRequest = HttpRequest.newBuilder(epicByIdUrl).GET().build();
        HttpResponse<String> getEpicResponse = client.send(getEpicByIdRequest, HttpResponse.BodyHandlers.ofString());

        String loadedEpic = getEpicResponse.body();
        Epic deserializeEpic = gson.fromJson(loadedEpic, Epic.class);

        assertEquals(epic, deserializeEpic);
    }

    @Test
    public void getSubTaskByIdTest() throws IOException, InterruptedException {
        Epic epic = TaskForTestsGenerator.testEpicTemplateGen();
        SubTask subTask = TaskForTestsGenerator.testSubTaskTemplateGen();
        long id = subTask.getId();
        epic.addSubTask(subTask);
        subTask.setEpic(epic);
        URI createSubTaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        URI subTaskByIdUrl = URI.create("http://localhost:8080/tasks/subtask?id=" + id);

        String jsonSubTak = gson.toJson(subTask);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonSubTak);
        HttpRequest createSubTaskRequest = HttpRequest.newBuilder(createSubTaskUrl).POST(body).build();

        HttpResponse<String> createResponse = client.send(createSubTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createResponse.statusCode());

        HttpRequest getSubTaskById = HttpRequest.newBuilder(subTaskByIdUrl).GET().build();
        HttpResponse<String> getSubTaskResponse = client.send(getSubTaskById, HttpResponse.BodyHandlers.ofString());

        String loadedSubTask = getSubTaskResponse.body();
        SubTask deserializeSubTask = gson.fromJson(loadedSubTask, SubTask.class);

        assertEquals(subTask, deserializeSubTask);
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task = TaskForTestsGenerator.testTaskTemplateGen();
        String name = task.getName();
        long id = task.getId();
        URI taskUrl = URI.create("http://localhost:8080/tasks/task");
        URI taskByIdUrl = URI.create("http://localhost:8080/tasks/task?id=" + id);

        String taskJson = gson.toJson(task);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest createTaskRequest = HttpRequest.newBuilder(taskUrl).POST(body).build();
        HttpResponse<String> createTaskResponse = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createTaskResponse.statusCode());

        Task updatedTask = new Task(name, "Updated description", id);
        String updateTaskJson = gson.toJson(updatedTask);

        HttpRequest.BodyPublisher updateTaskBoy = HttpRequest.BodyPublishers.ofString(updateTaskJson);
        HttpRequest updateRequest = HttpRequest.newBuilder(taskUrl).POST(updateTaskBoy).build();
        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, updateResponse.statusCode());

        HttpRequest getTaskRequest = HttpRequest.newBuilder(taskByIdUrl).GET().build();
        HttpResponse<String> getTaskResponse = client.send(getTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, getTaskResponse.statusCode());
        String loadedTask = getTaskResponse.body();
        Task deserializeUpdatedTask = gson.fromJson(loadedTask, Task.class);

        assertEquals(updatedTask, deserializeUpdatedTask);
    }

    @Test
    public void updateEpicTest() throws IOException, InterruptedException {
        Epic epic = TaskForTestsGenerator.testEpicTemplateGen();
        String name = epic.getName();
        long id = epic.getId();
        URI epicUrl = URI.create("http://localhost:8080/tasks/epic");
        URI epicByIdUrl = URI.create("http://localhost:8080/tasks/epic?id=" + id);

        String epicJson = gson.toJson(epic);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest createEpicRequest = HttpRequest.newBuilder(epicUrl).POST(body).build();
        HttpResponse<String> createEpicResponse = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createEpicResponse.statusCode());

        Epic updatedEpic = new Epic(name, "updated description", id);
        String updatedEpicJson = gson.toJson(updatedEpic);

        HttpRequest.BodyPublisher updatedBody = HttpRequest.BodyPublishers.ofString(updatedEpicJson);

        HttpRequest updateEpicRequest = HttpRequest.newBuilder(epicUrl).POST(updatedBody).build();
        HttpResponse<String> updateResponse = client.send(updateEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, updateResponse.statusCode());

        HttpRequest getUpdatedEpicRequest = HttpRequest.newBuilder(epicByIdUrl).GET().build();
        HttpResponse<String> getUpdatedEpicResponse = client.send(getUpdatedEpicRequest,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, getUpdatedEpicResponse.statusCode());

        String loadedEpic = getUpdatedEpicResponse.body();
        Epic deserializeEpic = gson.fromJson(loadedEpic, Epic.class);

        assertEquals(updatedEpic, deserializeEpic);
    }

    @Test
    public void updateSubTaskTest() throws IOException, InterruptedException {
        SubTask subTask = TaskForTestsGenerator.testSubTaskTemplateGen();
        Epic epic = TaskForTestsGenerator.testEpicTemplateGen();
        epic.addSubTask(subTask);
        subTask.setEpic(epic);
        String name = subTask.getName();
        long id = subTask.getId();
        URI subTaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        URI subTaskByIdUrl = URI.create("http://localhost:8080/tasks/subtask?id=" + id);

        String subTaskJson = gson.toJson(subTask);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTaskJson);
        HttpRequest createSubTaskRequest = HttpRequest.newBuilder(subTaskUrl).POST(body).build();
        HttpResponse<String> createSubTaskResponse = client.send(createSubTaskRequest,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createSubTaskResponse.statusCode());

        SubTask updatedSubTask = new SubTask(name, "Updated description", id);
        updatedSubTask.setEpic(epic);
        String updatedSubTaskJson = gson.toJson(updatedSubTask);

        HttpRequest.BodyPublisher updatedBody = HttpRequest.BodyPublishers.ofString(updatedSubTaskJson);
        HttpRequest subTaskUpdateRequest = HttpRequest.newBuilder(subTaskUrl).POST(updatedBody).build();
        HttpResponse<String> subTaskUpdateResponse = client.send(subTaskUpdateRequest,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, subTaskUpdateResponse.statusCode());

        HttpRequest getSubTaskRequest = HttpRequest.newBuilder(subTaskByIdUrl).GET().build();
        HttpResponse<String> getSubTaskResponse = client.send(getSubTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, getSubTaskResponse.statusCode());

        String subTaskResponseBody = getSubTaskResponse.body();
        SubTask deserializeSubTask = gson.fromJson(subTaskResponseBody, SubTask.class);

        assertEquals(updatedSubTask, deserializeSubTask);
    }

    @Test
    public void deleteTaskByIdTest() throws IOException, InterruptedException {
        Task task = TaskForTestsGenerator.testTaskTemplateGen();
        long id = task.getId();
        URI taskUrl = URI.create("http://localhost:8080/tasks/task");
        URI taskByIdUrl = URI.create("http://localhost:8080/tasks/task?id=" + id);

        String json = gson.toJson(task);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createTaskRequest = HttpRequest.newBuilder(taskUrl).POST(body).build();
        HttpResponse<String> createResponse = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createResponse.statusCode());

        HttpRequest deleteTaskByIdRequest = HttpRequest.newBuilder(taskByIdUrl).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteTaskByIdRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, deleteResponse.statusCode());
    }

    @Test
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = TaskForTestsGenerator.testEpicTemplateGen();
        long id = epic.getId();
        URI epicUrl = URI.create("http://localhost:8080/tasks/epic");
        URI epicByIdUrl = URI.create("http://localhost:8080/tasks/epic?id=" + id);

        String json = gson.toJson(epic);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createEpicRequest = HttpRequest.newBuilder(epicUrl).POST(body).build();
        HttpResponse<String> createEpicResponse = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createEpicResponse.statusCode());

        HttpRequest deleteEpicByIdRequest = HttpRequest.newBuilder(epicByIdUrl).DELETE().build();
        HttpResponse<String> deleteByIdResponse = client.send(deleteEpicByIdRequest,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, deleteByIdResponse.statusCode());
    }

    @Test
    public void deleteSubTaskByIdTest() throws IOException, InterruptedException {
        Epic epic = TaskForTestsGenerator.testEpicTemplateGen();
        SubTask subTask = TaskForTestsGenerator.testSubTaskTemplateGen();
        epic.addSubTask(subTask);
        subTask.setEpic(epic);
        long id = subTask.getId();

        URI subTaskUrl = URI.create("http://localhost:8080/tasks/subtask");
        URI subTaskByIdUrl = URI.create("http://localhost:8080/tasks/subtask?id=" + id);

        String json = gson.toJson(subTask);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createSubTaskRequest = HttpRequest.newBuilder(subTaskUrl).POST(body).build();
        HttpResponse<String> createSubTaskResponse = client.send(createSubTaskRequest,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, createSubTaskResponse.statusCode());

        HttpRequest deleteSubTaskByIdRequest = HttpRequest.newBuilder(subTaskByIdUrl).DELETE().build();
        HttpResponse<String> deleteSubTaskByIdResponse = client.send(deleteSubTaskByIdRequest,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, deleteSubTaskByIdResponse.statusCode());
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");

        HttpRequest getAllTasksRequest = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(getAllTasksRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, response.statusCode());
    }

    @Test
    public void historyTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history");

        HttpRequest getHistoryRequest = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(getHistoryRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(STATUS_OK, response.statusCode());
    }
/*
    @Test
    public void loadHttpTaskManagerTest() throws IOException, InterruptedException {
        System.out.println("Начало теста на загрузку");
        Task testTask = TaskForTestsGenerator.testTaskTemplateGen();
        long id = testTask.getId();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI taskByIdUrl = URI.create("http://localhost:8080/tasks/task?id=" + id);

        // получаем у HttpTaskServer ключ по которому будет происходить запись/загрузка из KVServer
        String apiKey = httpTaskServer.getApiKey();

        String json = gson.toJson(testTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        // создали задачу
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, response.statusCode());

        // добавили задачу в историю просмотров
        // todo: задачи не добавляются в историю просмотров
        HttpRequest getTaskByIdRequest = HttpRequest.newBuilder().uri(taskByIdUrl).GET().build();
        HttpResponse<String> getTaskByIdResponse = client.send(getTaskByIdRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("История просмотров: " + httpTaskServer.getTaskManager().history());
        assertEquals(STATUS_OK, getTaskByIdResponse.statusCode());
        *//*
        // проверяем историю просмотров
        URI historyUrl = URI.create("http://localhost:8080/tasks/history");
        HttpRequest getHistoryRequest = HttpRequest.newBuilder(historyUrl).GET().build();
        HttpResponse<String> historyResponse = client.send(getHistoryRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("history: " + historyResponse.body());
        *//*

        // выполняем загрузку менеджера
        TaskManager loadedHttpTaskManager = Managers.loadHttpTaskManagerFromKVServer(apiKey);

        // проверяем, что состояние загруженного менеджера соответствует сохраняемому:
        // созданная задача действительно загрузилась
        assertEquals(testTask, loadedHttpTaskManager.getTaskById(id));

        // сохранилась история просмотров
        //assertNotEquals(0, loadedHttpTaskManager.history().size());
        System.out.println("Тест на загрузку завершон");
    }*/
}
