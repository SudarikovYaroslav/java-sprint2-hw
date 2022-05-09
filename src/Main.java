import main.model.HttpTaskServer;
import main.model.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();

        /*try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
