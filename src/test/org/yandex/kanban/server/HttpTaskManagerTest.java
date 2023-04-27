package org.yandex.kanban.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yandex.kanban.model.EpicTask;
import org.yandex.kanban.model.Task;
import org.yandex.kanban.service.HistoryManager;
import org.yandex.kanban.service.InMemoryHistoryManager;
import org.yandex.kanban.service.Managers;
import org.yandex.kanban.service.TaskCreateDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    private static final String KEY_VALUE_URL = "http://localhost:8078/";
    public KVServer kvServer;
    HttpTaskManager httpTaskManager;

    @BeforeEach
    void startServers() throws IOException, URISyntaxException {
        kvServer = new KVServer(8078);
        kvServer.start();
        httpTaskManager = new HttpTaskManager(new InMemoryHistoryManager(), KEY_VALUE_URL);
    }

    @AfterEach
    void tearDownServers() {
        kvServer.stop();
    }

    @Test
    public void shouldLoadEmptyTaskManagerOnFirstStartUp() {
        assertTrue(httpTaskManager.getAllTasks().isEmpty(), "Список задач не пустой");
    }

    @Test
    public void shouldSaveAndLoadEpicWithNoSubtasksAndEmptyHistory() throws IOException, URISyntaxException {

        EpicTask epicTask = httpTaskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic"));
        // id = 0
        assertFalse(httpTaskManager.getAllTasks().isEmpty(), "Список задач пустой");

        httpTaskManager.loadFromServer();

        assertFalse(httpTaskManager.getAllTasks().isEmpty(),
                "Список задач после перезапуска HttpTaskServer пустой");

        List<Task> result = httpTaskManager.getAllTasks();

        assertEquals(List.of(epicTask),
                result, "Список задач в файле после сохранения не совпадает");
        assertTrue(httpTaskManager.getEpicSubTasks(0).isEmpty(),
                "Subtasks у эпика не пустые");
        assertTrue(httpTaskManager.getHistory().isEmpty(), "История не пустая");
    }
}