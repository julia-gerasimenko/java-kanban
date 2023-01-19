package service;

public abstract class Managers {

    // пока не разобралась, если убираю статик, ругается main. Буду разбираться :)

    private static InMemoryHistoryManager IN_MEMORY_HISTORY_MANAGER;
    private static InMemoryTaskManager IN_MEMORY_TASK_MANAGER;

    public static InMemoryHistoryManager inMemoryHistoryManager() {
        if (IN_MEMORY_HISTORY_MANAGER == null) {
            IN_MEMORY_HISTORY_MANAGER = new InMemoryHistoryManager();
        }
        return IN_MEMORY_HISTORY_MANAGER;
    }

    public static InMemoryTaskManager inMemoryTaskManager() {
        if (IN_MEMORY_TASK_MANAGER == null) {
            IN_MEMORY_TASK_MANAGER = new InMemoryTaskManager(inMemoryHistoryManager());
        }
        return IN_MEMORY_TASK_MANAGER;
    }

}
