import main.model.HttpTaskServer;
import main.model.KVServer;
import main.model.KVTaskClient;
import main.service.HttpTaskManager;
import main.service.TaskManager;
import main.util.Managers;
import main.util.Util;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        KVTaskClient client = new KVTaskClient(Util.getKVServerUrl());

        String task1 = "{\"name\":\"TestTask1\",\"description\":\"task1 description\",\"id\":1}";
        String task2 = "{\"name\":\"TestTask2\",\"description\":\"task2 description\",\"id\":2}";

        client.put("1", task1);
        System.out.println(client.load("1"));
        client.put("2", task2);
        System.out.println(client.load("2"));

        TaskManager taskManager = Managers.getDefault();

        /*try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
