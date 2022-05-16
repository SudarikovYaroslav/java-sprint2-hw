package model.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiKey;
    private final String kvServerUrl;
    private final HttpClient httpClient;

    public KVTaskClient(String kvServerUrl) {
        this.kvServerUrl = kvServerUrl;
        httpClient = HttpClient.newHttpClient();

        URI uri = URI.create(kvServerUrl + "/register");
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> registerResponse = httpClient.send(registerRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (registerResponse.statusCode() != 200) {
                System.out.println("Не удалось зарегистрировать KVTaskClient! Response code: "
                        + registerResponse.statusCode());
                return;
            }
            apiKey = registerResponse.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        // должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_KEY=
        URI uri = URI.create(kvServerUrl + "/save/" + key + "?API_KEY=" + apiKey);
        HttpRequest saveRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();

        HttpResponse<String> response = httpClient.send(saveRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.out.println("Сохранение менеджера не удалось! StatusCode: " + response.statusCode());
        } else {
            System.out.println("Сохранение менеджера прошло успешно!");
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        // должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_KEY=
        URI uri = URI.create(kvServerUrl + "/load/" + key + "?API_KEY=" + apiKey);
        HttpRequest loadRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> httpResponse = httpClient.send(loadRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() != 200) {
            System.out.println("Не удалось загрузить TaskManager из хранилища! StatusCode: "
                    + httpResponse.statusCode());
            return null;
        }

        return httpResponse.body();
    }
}
