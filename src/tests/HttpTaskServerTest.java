import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.HttpTaskServer;
import main.model.KVServer;
import main.model.serializators.SubTaskSerializer;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.model.tasks.Task;
import main.service.TaskForTestsGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    private static final int PORT = 8080;
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SubTask.class, new SubTaskSerializer())
            .serializeNulls()
            .create();
    private final HttpClient client = HttpClient.newHttpClient();
    private static final int STATUS_OK = 200;


    @BeforeEach
    public void preparation() throws IOException {
        httpTaskServer = new HttpTaskServer();
        kvServer = new KVServer();
        httpTaskServer.start();
        kvServer.start();
    }
/*

    @AfterEach
    public void termination() {
        httpTaskServer.stop();
        kvServer.start();
    }
*/

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
        URI url = URI.create("http://localhost:8080/tasks/subTask");
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
        URI url = URI.create("http://localhost:8080/tasks/subTask");
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
//
//    @Test
//    public void deleteSubTasksTest() {}
//
//    @Test
//    public void getTaskByIdTest() {}
//
//    @Test
//    public void getEpicByIdTest() {}
//
//    @Test
//    public void getSubTaskByIdTest() {}
//
//    @Test
//    public void updateTaskTest() {}
//
//    @Test
//    public void updateEpicTest() {}
//
//    @Test
//    public void updateSubTaskTest() {}
//
//    @Test
//    public void deleteTaskByIdTest() {
//    }
//
//    @Test
//    public void deleteEpicByIdTest() {
//    }
//
//    @Test
//    public void deleteSubTaskByIdTest() {
//    }
//
//    @Test
//    public void getPrioritizedTasksTest(){}
//
//    @Test
//    public void historyTest() {}
}
