package org.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.yandex.kanban.model.*;
import org.yandex.kanban.server.dto.EpicTaskDTO;
import org.yandex.kanban.server.dto.SingleTaskDTO;
import org.yandex.kanban.server.dto.SubTaskDTO;
import org.yandex.kanban.server.util.LocalDateTimeAdapter;
import org.yandex.kanban.server.util.UrlParams;
import org.yandex.kanban.service.Managers;
import org.yandex.kanban.service.TaskCreateDto;
import org.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    private static final Gson GSON_CONVERTER = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    protected final TaskManager taskManager;
    protected HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public HttpTaskServer() {
        try {
            taskManager = Managers.taskManager();
        } catch (Throwable ex) {
            throw new IllegalStateException("Не удалось создать taskManager", ex);
        }
    }

    public void run() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", httpExchange -> {
            try {
                String subPath = httpExchange.getRequestURI().getPath().
                        replace(httpExchange.getHttpContext().getPath(), "");
                switch (subPath) {
                    case ROOT_SUBPATH:
                        if (httpExchange.getRequestMethod().equals(GET)) {
                            sendPositiveResponse(httpExchange, taskManager.getPrioritizedTasks());
                            break;
                        }
                        httpExchange.sendResponseHeaders(405, 0);
                        break;
                    case TASK_SUBPATH:
                        if (httpExchange.getRequestMethod().equals(GET)) {
                            getTask(httpExchange);
                            break;
                        }
                        if (httpExchange.getRequestMethod().equals(POST)) {
                            saveNewTaskOrUpdate(httpExchange, subPath);
                            break;
                        }
                        if (httpExchange.getRequestMethod().equals(DELETE)) {
                            deleteTask(httpExchange);
                            break;
                        }
                    case EPIC_SUBPATH:
                        if (httpExchange.getRequestMethod().equals(POST)) {
                            saveNewTaskOrUpdate(httpExchange, subPath);
                            break;
                        }
                    case SUBTASK_EPIC_SUBPATH: {
                        if (httpExchange.getRequestMethod().equals(GET)) {
                            GetEpicSubTaskList(httpExchange);
                            break;
                        }
                        if (httpExchange.getRequestMethod().equals(DELETE)) {
                            deleteEpicSubTasks(httpExchange);
                            break;
                        }
                    }
                    case SUBTASK_SUBPATH:
                        if (httpExchange.getRequestMethod().equals(POST)) {
                            saveNewTaskOrUpdate(httpExchange, subPath);
                            break;
                        }
                    case HISTORY_SUBPATH:
                        if (httpExchange.getRequestMethod().equals(GET)) {
                            getHistoryTasks(httpExchange);
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

    public void stop() {
        server.stop(0);
    }

    public void sendPositiveResponse(HttpExchange h, Object o) throws IOException {
        byte[] responseBody = GSON_CONVERTER.toJson(o).getBytes();
        h.sendResponseHeaders(200, responseBody.length);
        h.getResponseBody().write(responseBody);
    }

    protected void getHistoryTasks(HttpExchange httpExchange) throws IOException {
        List<Task> historyTasks = taskManager.getHistory();
        sendPositiveResponse(httpExchange, historyTasks);
    }

    public void getTask(HttpExchange httpExchange) throws IOException {
        UrlParams urlParams = UrlParams.getParams(httpExchange.getRequestURI());
        Optional<String> optionalId = urlParams.getFirst("id");
        if (optionalId.isPresent()) {
            Task task = taskManager.getTaskById(Integer.parseInt(optionalId.get()));
            if (task == null) {
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            sendPositiveResponse(httpExchange, task);
            return;
        }
        Optional<String> optionalType = urlParams.getFirst("type");
        if (optionalType.isPresent()) {
            if (taskManager.filterTasksByType(Type.valueOf(optionalType.get().toUpperCase())) == null) {
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            List<Task> tasksFilteredByType = taskManager.filterTasksByType(
                    Type.valueOf(optionalType.get().toUpperCase()));
            sendPositiveResponse(httpExchange, tasksFilteredByType);
            return;
        }
        List<Task> allTasks = taskManager.getAllTasks();
        sendPositiveResponse(httpExchange, allTasks);
    }

    protected void deleteTask(HttpExchange httpExchange) throws IOException {
        UrlParams urlParams = UrlParams.getParams(httpExchange.getRequestURI());
        Optional<String> optionalId = urlParams.getFirst("id");
        if (optionalId.isPresent()) {
            if (taskManager.getTaskById(Integer.parseInt(optionalId.get())) == null) {
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            taskManager.deleteTaskById(Integer.parseInt(optionalId.get()));
            sendPositiveResponse(httpExchange, 0);
            return;
        }
        Optional<String> optionalType = urlParams.getFirst("type");
        if (optionalType.isPresent()) {
            if (taskManager.filterTasksByType(Type.valueOf(optionalType.get().toUpperCase())) == null) {
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            taskManager.deleteTaskByType(Type.valueOf(optionalType.get().toUpperCase()));
            sendPositiveResponse(httpExchange, 0);
            return;
        }
        taskManager.deleteAllTasks();
        sendPositiveResponse(httpExchange, 0);
    }

    protected void saveNewTaskOrUpdate(HttpExchange httpExchange, String subPath) throws IOException {
        Task taskToReturn = null;
        switch (subPath) {
            case TASK_SUBPATH:
                SingleTaskDTO incomingSingleTask = GSON_CONVERTER.fromJson(new InputStreamReader(
                                httpExchange.getRequestBody()),
                        SingleTaskDTO.class);
                if (incomingSingleTask.getId() == null) {
                    TaskCreateDto singleTask = new TaskCreateDto(
                            incomingSingleTask.getName(),
                            incomingSingleTask.getDescription(),
                            incomingSingleTask.getStartTime(),
                            incomingSingleTask.getDurationInMins());
                    taskToReturn = taskManager.saveSingleTask(singleTask);
                } else {
                    SingleTask singleTask = new SingleTask(
                            incomingSingleTask.getName(),
                            incomingSingleTask.getId(),
                            incomingSingleTask.getDescription(),
                            incomingSingleTask.getStatus(),
                            incomingSingleTask.getStartTime(),
                            incomingSingleTask.getDurationInMins());
                    taskManager.update(singleTask);
                    taskToReturn = singleTask;
                }
                if (taskManager.getTaskById(taskToReturn.getId()) == null) {
                    // если задача не сохранилась из-за overlapped
                    httpExchange.sendResponseHeaders(406, 0);
                    return;
                }
                sendPositiveResponse(httpExchange, taskToReturn);
                break;
            case EPIC_SUBPATH:
                EpicTaskDTO incomingEpicTask = GSON_CONVERTER.fromJson(new InputStreamReader(
                                httpExchange.getRequestBody()),
                        EpicTaskDTO.class);
                if (incomingEpicTask.getId() == null) {
                    TaskCreateDto epicTask = new TaskCreateDto(
                            incomingEpicTask.getName(),
                            incomingEpicTask.getDescription());
                    taskToReturn = taskManager.saveEpicTask(epicTask);
                } else {
                    EpicTask epicTask = new EpicTask(
                            incomingEpicTask.getName(),
                            incomingEpicTask.getId(),
                            incomingEpicTask.getDescription(),
                            incomingEpicTask.getSubTasks(),
                            incomingEpicTask.getStatus());
                    taskManager.update(epicTask);
                    taskToReturn = epicTask;
                }
                if (taskManager.getTaskById(taskToReturn.getId()) == null) {
                    httpExchange.sendResponseHeaders(406, 0);
                    return;
                }
                sendPositiveResponse(httpExchange, taskToReturn);
                break;
            case SUBTASK_SUBPATH:
                SubTaskDTO incomingSubTask = GSON_CONVERTER.fromJson(new InputStreamReader(
                                httpExchange.getRequestBody()),
                        SubTaskDTO.class);
                if (incomingSubTask.getId() == null) {
                    TaskCreateDto subTask = new TaskCreateDto(
                            incomingSubTask.getName(),
                            incomingSubTask.getDescription(),
                            incomingSubTask.getStartTime(),
                            incomingSubTask.getDurationInMins());
                    taskToReturn = taskManager.saveSubTask(subTask,
                            (EpicTask) taskManager.getTaskById(incomingSubTask.getEpicId()));
                } else {
                    SubTask subTask = new SubTask(
                            incomingSubTask.getName(),
                            incomingSubTask.getId(),
                            incomingSubTask.getDescription(),
                            incomingSubTask.getStatus(),
                            incomingSubTask.getStartTime(),
                            incomingSubTask.getDurationInMins(),
                            incomingSubTask.getEpicId());
                    taskManager.update(subTask);
                    taskToReturn = subTask;
                }
                if (taskManager.getTaskById(taskToReturn.getId()) == null) {
                    httpExchange.sendResponseHeaders(406, 0);
                    return;
                }
                sendPositiveResponse(httpExchange, taskToReturn);
                break;
        }
    }

    protected void deleteEpicSubTasks(HttpExchange httpExchange) throws IOException {
        UrlParams urlParams = UrlParams.getParams(httpExchange.getRequestURI());
        Optional<String> optionalEpicId = urlParams.getFirst("id");
        if (optionalEpicId.isPresent()) {
            if (taskManager.getTaskById(Integer.parseInt(optionalEpicId.get())) == null) {
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            taskManager.deleteSubTasksForEpic(Integer.parseInt(optionalEpicId.get()));
            sendPositiveResponse(httpExchange, 0);
        }
    }

    protected void GetEpicSubTaskList(HttpExchange httpExchange) throws IOException {
        UrlParams urlParams = UrlParams.getParams(httpExchange.getRequestURI());
        Optional<String> optionalEpicId = urlParams.getFirst("id");
        if (optionalEpicId.isPresent()) {
            Task task = taskManager.getTaskById(Integer.parseInt(optionalEpicId.get()));
            if (task == null) {
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            List<SubTask> epicSubTasks = taskManager.getEpicSubTasks(Integer.parseInt(optionalEpicId.get()));
            sendPositiveResponse(httpExchange, epicSubTasks);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        new HttpTaskServer().run();
    }


}

