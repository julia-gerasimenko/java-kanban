package org.yandex.kanban.service;

import org.yandex.kanban.server.HttpTaskManager;
import org.yandex.kanban.server.KVServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public abstract class Managers {
    private static HistoryManager historyManager;
    private static TaskManager taskManager;
    private static KVServer kvServer;
    private static final int KEY_VALUE_SERVER_PORT = 8078;
    private static final String KEY_VALUE_URL = "http://localhost:" + KEY_VALUE_SERVER_PORT + "/";

    public static HistoryManager historyManager() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }

    public static TaskManager taskManager() throws MalformedURLException, URISyntaxException {
        if (taskManager == null) {
            taskManager = new HttpTaskManager(historyManager(), KEY_VALUE_URL);
        }
        return taskManager;
    }

    public static KVServer getKVServer() throws IOException {
        if (kvServer == null) {
            kvServer = new KVServer(KEY_VALUE_SERVER_PORT);
        }
        return kvServer;
    }
}

