import model.http.HttpTaskServer;
import model.http.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        KVServer kvServer = new KVServer();
        httpTaskServer.start();
        kvServer.start();
    }
}
