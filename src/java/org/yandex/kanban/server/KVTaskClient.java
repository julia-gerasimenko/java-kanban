package org.yandex.kanban.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class KVTaskClient {
    //только обращается в KVserver, чтобы зарегистрироваться, записать или прочить данные.
    private final String apiToken;
    private final HttpClient httpClient;
    private final URL serverUrl;

    public KVTaskClient(URL kvServerUrl) throws MalformedURLException, URISyntaxException {
        httpClient = HttpClient.newHttpClient();
        serverUrl = kvServerUrl;
        apiToken = register();
    }

    public void put(String key, String json) throws MalformedURLException, URISyntaxException {
        //должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
        URI saveRequestUri = new URL(serverUrl, "/save/" + key + "?API_TOKEN=" + apiToken).toURI();
        HttpRequest saveRequest = HttpRequest.newBuilder()
                .uri(saveRequestUri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        sendRequest(saveRequest);
    }

    public String load(String key) throws MalformedURLException, URISyntaxException {
        //должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
        URI loadRequestUri = new URL(serverUrl, "/load/" + key + "?API_TOKEN=" + apiToken).toURI();
        HttpRequest loadRequest = HttpRequest.newBuilder()
                .uri(loadRequestUri)
                .GET()
                .build();
        Optional<String> value = sendRequest(loadRequest);
        return value.orElseThrow(() -> new RuntimeException("Не удалось получить значение по ключу"));
    }

    private String register() throws MalformedURLException, URISyntaxException {
        URI registerUri = new URL(serverUrl, "/register").toURI();
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(registerUri)
                .GET()
                .build();
        Optional<String> apiToken = sendRequest(registerRequest);
        return apiToken.orElseThrow(() -> new RuntimeException("Не удалось получить API TOKEN"));
    }

    private Optional<String> sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Сервер ответил ошибкой.\n" +
                        "Код ошибки: " + response.statusCode() + "\nСообщение:" + response.body());
                return Optional.empty();
            }
            return Optional.ofNullable(response.body());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        new KVServer(8078).start();
        KVTaskClient client = new KVTaskClient(new URL("http://localhost:8078"));
        client.put("testKey","text");
        System.out.println(client.load("testKey"));
    }

}
