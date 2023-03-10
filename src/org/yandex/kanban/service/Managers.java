package org.yandex.kanban.service;

public abstract class Managers {

    private static InMemoryHistoryManager IN_MEMORY_HISTORY_MANAGER;
    private static InMemoryTaskManager IN_MEMORY_TASK_MANAGER;

    public static HistoryManager historyManager() {
        if (IN_MEMORY_HISTORY_MANAGER == null) {
            IN_MEMORY_HISTORY_MANAGER = new InMemoryHistoryManager();
        }
        return IN_MEMORY_HISTORY_MANAGER;
    }

    public static TaskManager taskManager() {
        if (IN_MEMORY_TASK_MANAGER == null) {
            IN_MEMORY_TASK_MANAGER = new InMemoryTaskManager(historyManager());
        }
        return IN_MEMORY_TASK_MANAGER;
    }

}
