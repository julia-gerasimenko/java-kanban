package org.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import org.yandex.kanban.model.SingleTask;
import org.yandex.kanban.model.Task;
import org.yandex.kanban.server.to.SingleTaskTO;
import org.yandex.kanban.service.Managers;
import org.yandex.kanban.service.TaskCreateDto;
import org.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final String ROOT_SUBPATH = "/";
    private static final String TASK_SUBPATH = "/task";
    private static final String EPIC_SUBPATH = "/epic";
    private static final String SUBTASK_SUBPATH = "/subtask";
    private static final String SUBTASK_EPIC_SUBPATH = "/subtask/epic";
    private static final String HISTORY_SUBPATH = "/history";

    private static final String GET = "GET";
    private static final String DELETE = "DELETE";
    private static final String POST = "POST";


    private static final Gson GSON_CONVERTER = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,new LocalDateTimeAdapter())
            .create();

    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public HttpTaskServer() {
        taskManager = Managers.taskManager();
    }

    public void run() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", httpExchange -> {
            try {
                String subPath = httpExchange.getRequestURI().getPath().
                        replace(httpExchange.getHttpContext().getPath(), "");
                switch (subPath) {
                    case ROOT_SUBPATH:
                        if (httpExchange.getRequestMethod().equals(GET)) {
                            String responseBody = GSON_CONVERTER.toJson(taskManager.getPrioritizedTasks());
                            httpExchange.sendResponseHeaders(200, responseBody.getBytes().length);
                            httpExchange.getResponseBody().write(responseBody.getBytes());
                            break;
                        }
                        httpExchange.sendResponseHeaders(405, 0);
                        break;
                    case TASK_SUBPATH:
                        if (httpExchange.getRequestMethod().equals(GET)) {

                            UrlParams urlParams = UrlParams.getParams(httpExchange.getRequestURI());
                            Optional<String> optionalId = urlParams.getFirst("id");
                            if (optionalId.isPresent()) {
                                Task task = taskManager.getTaskById(Integer.parseInt(optionalId.get()));
                                if (task == null) {
                                    httpExchange.sendResponseHeaders(404, 0);
                                    break;
                                }
                                //TODO sendResponse(HttpExchange h, Object o)
                                byte[] responseBody = GSON_CONVERTER.toJson(task).getBytes();
                                httpExchange.sendResponseHeaders(200, responseBody.length);
                                httpExchange.getResponseBody().write(responseBody);
                                break;
                            }
                            List<Task> allTasks = taskManager.getAllTasks();
                            byte[] responseBody = GSON_CONVERTER.toJson(allTasks).getBytes();
                            httpExchange.sendResponseHeaders(200, responseBody.length);
                            httpExchange.getResponseBody().write(responseBody);
                            break;
                        }
                        if (httpExchange.getRequestMethod().equals(POST)) {
                            SingleTask taskToReturn = null;
                            SingleTaskTO task = GSON_CONVERTER.fromJson(new InputStreamReader(httpExchange.getRequestBody()),
                                    SingleTaskTO.class);
                            if (task.getId() == null) {
                                TaskCreateDto singleTask = new TaskCreateDto(task.getName(), task.getDescription(),
                                        task.getStartTime(), task.getDurationInMins());
                                taskToReturn = taskManager.saveSingleTask(singleTask);
                            } else {
                                SingleTask singleTask = new SingleTask(task.getName(), task.getId(), task.getDescription(),
                                        task.getStatus(), task.getStartTime(), task.getDurationInMins());
                                taskManager.update(singleTask);
                                taskToReturn = singleTask;
                            }
                            byte[] responseBody = GSON_CONVERTER.toJson(taskToReturn).getBytes();
                            httpExchange.sendResponseHeaders(200, responseBody.length);
                            httpExchange.getResponseBody().write(responseBody);
                            break;
                        }
                        if (httpExchange.getRequestMethod().equals(DELETE)) {

                            break;
                        }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                byte[] errorBytes = ex.getMessage().getBytes();
                httpExchange.sendResponseHeaders(500, errorBytes.length);
                httpExchange.getResponseBody().write(errorBytes);
            } finally {
                httpExchange.close();
            }
        });
        server.start();

    }


    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer server = new HttpTaskServer();
        server.run();
        TimeUnit.SECONDS.sleep(30);
    }

    private static final class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.toString());
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString());
        }
    }
}
