package org.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.yandex.kanban.model.EpicTask;
import org.yandex.kanban.model.SingleTask;
import org.yandex.kanban.model.Type;
import org.yandex.kanban.server.dto.TaskManagerStateDTO;
import org.yandex.kanban.server.util.LocalDateTimeAdapter;
import org.yandex.kanban.service.FileBackedTasksManager;
import org.yandex.kanban.service.HistoryManager;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String STATE_KEY = "task-manager-storage-state";
    private final KVTaskClient kvTaskClient;

    protected static final Gson GSON_CONVERTER = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskManager(HistoryManager historyManager, String kvUrl) throws MalformedURLException, URISyntaxException {
        super(historyManager, null);
        this.kvTaskClient = new KVTaskClient(new URL(kvUrl));
        loadFromServer();
    }

    @Override
    public void save() {
        TaskManagerStateDTO taskManagerStateDTO = new TaskManagerStateDTO();
        List<Integer> history = historyManager.getHistoryIds();
        Collections.reverse(history);
        taskManagerStateDTO.setHistory(history);
        taskManagerStateDTO.setSingleTasks(filterTasksByType(Type.SINGLE).stream()
                .map(task -> (SingleTask) task).collect(Collectors.toList()));
        taskManagerStateDTO.setEpicTasks(filterTasksByType(Type.EPIC).stream()
                .map(task -> (EpicTask) task).collect(Collectors.toList()));
        try {
            kvTaskClient.put(STATE_KEY, GSON_CONVERTER.toJson(taskManagerStateDTO));
        } catch (Throwable ex) {
            System.out.println("Не удалось сохранить состояние.");
            ex.printStackTrace();
        }
    }

    public void loadFromServer() {
        try {
            String stateJson = kvTaskClient.load(STATE_KEY);
            TaskManagerStateDTO taskManagerState = GSON_CONVERTER.fromJson(stateJson, TaskManagerStateDTO.class);
            prioritizedTasks.clear();
            taskById.clear();
            historyManager.reset();

            for (SingleTask singleTask : taskManagerState.getSingleTasks()) {
                prioritizedTasks.add(singleTask);
               taskById.put(singleTask.getId(), singleTask);
            }
            for (EpicTask epicTask : taskManagerState.getEpicTasks()) {
                taskById.put(epicTask.getId(), epicTask);
                prioritizedTasks.addAll(epicTask.getSubTasks());
            }
            for (int id : taskManagerState.getHistory()) {
                historyManager.addTaskToHistory(findTaskById(id));
            }

        } catch (Throwable ex) {
            System.out.println("Не удалось загрузить состояние");
            ex.printStackTrace();
        }
    }
}
