package org.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;


class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
            public void beforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

}