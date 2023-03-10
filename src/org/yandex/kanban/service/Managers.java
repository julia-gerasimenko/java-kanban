package org.yandex.kanban.service;

import java.io.File;
import java.nio.file.Path;

public abstract class Managers {
    private static HistoryManager IN_MEMORY_HISTORY_MANAGER;
    private static TaskManager IN_MEMORY_TASK_MANAGER;
    private static final Path STORAGE_PATH = Path.of("src\\taskManagerStorage.csv");
    private static final File TASK_MANAGER_STORAGE = Path.of("src\\taskManagerStorage.csv").toFile();

    public static HistoryManager historyManager() {
        if (IN_MEMORY_HISTORY_MANAGER == null) {
            IN_MEMORY_HISTORY_MANAGER = new InMemoryHistoryManager();
        }
        return IN_MEMORY_HISTORY_MANAGER;
    }

    public static TaskManager taskManager() {
        if (!TASK_MANAGER_STORAGE.isFile()) {
            IN_MEMORY_TASK_MANAGER = new FileBackedTasksManager(historyManager(), STORAGE_PATH);
        } else IN_MEMORY_TASK_MANAGER = FileBackedTasksManager.loadFromFile(STORAGE_PATH);
        return IN_MEMORY_TASK_MANAGER;
    }

}

