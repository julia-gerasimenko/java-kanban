package org.yandex.kanban.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yandex.kanban.model.Status;
import org.yandex.kanban.server.dto.SingleTaskDTO;
import org.yandex.kanban.service.TaskCreateDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    public KVServer kvServer;
    HttpTaskServer httpTaskServer;
    HttpClient httpClient = HttpClient.newHttpClient();
    private static final String TASK_SERVER_URL = "http://localhost:8080/tasks/";
    public static final String TASK_ENDPOINT_PATH = TASK_SERVER_URL + "task";
    public static final String HISTORY_ENDPOINT_PATH = TASK_SERVER_URL + "history";

    @BeforeEach
    void startServers() throws IOException {
        kvServer = new KVServer(8078);
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.run();
    }

    @AfterEach
    void tearDownServers() {
        httpTaskServer.taskManager.deleteAllTasks();
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void shouldSaveSingleTask() throws IOException {
        String payload = readResource("src/testResources/createSingleTask.json");
        String expected = readResource("src/testResources/singleTaskWithId.json");

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(TASK_ENDPOINT_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(payload)).build();
        Optional<String> response = sendRequest(request);

        assertTrue(response.isPresent());
        assertEquals(expected, response.get());
    }


    @Test
    public void shouldReturnSingleTask() throws IOException {
        TaskCreateDto singleTask = new TaskCreateDto("Single task", "testing single");
        httpTaskServer.taskManager.saveSingleTask(singleTask);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(TASK_ENDPOINT_PATH + "?id=0"))
                .GET().build();
        Optional<String> response = sendRequest(request);

        assertTrue(response.isPresent());
        SingleTaskDTO actualTask =
                HttpTaskManager.GSON_CONVERTER.fromJson(response.get(), SingleTaskDTO.class);
        SingleTaskDTO expected = new SingleTaskDTO();
        expected.setName("Single task");
        expected.setDescription("testing single");
        expected.setId(0);
        expected.setStatus(Status.NEW);
        assertEquals(expected, actualTask);
    }

    @Test
    public void shouldDeleteSingleTask() throws IOException {
        TaskCreateDto singleTask = new TaskCreateDto("Single task", "testing single");
        httpTaskServer.taskManager.saveSingleTask(singleTask);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(TASK_ENDPOINT_PATH + "?id=0"))
                .DELETE().build();
        Optional<String> response = sendRequest(request);

        assertTrue(response.isPresent());
        assertNull(httpTaskServer.taskManager.getTaskById(0));
    }

    private static String readResource(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    private Optional<String> sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers
                    .ofString(StandardCharsets.UTF_8));
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
}
