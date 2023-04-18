package org.yandex.kanban.service;

import java.io.File;
import java.nio.file.Path;

public abstract class Managers {
    private static HistoryManager historyManager;
    private static final Path STORAGE_PATH = Path.of("src/resources/org/yandex/kanban/taskManagerStorage.csv");
    private static final File TASK_MANAGER_STORAGE =
            Path.of("src/resources/org/yandex/kanban/taskManagerStorage.csv").toFile();

    public static HistoryManager historyManager() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }

    public static TaskManager taskManager() {
        TaskManager taskManager;
        if (!TASK_MANAGER_STORAGE.isFile()) {
            taskManager = new FileBackedTasksManager(historyManager(), STORAGE_PATH);
        } else taskManager = FileBackedTasksManager.loadFromFile(STORAGE_PATH);
        return taskManager;
    }
}

