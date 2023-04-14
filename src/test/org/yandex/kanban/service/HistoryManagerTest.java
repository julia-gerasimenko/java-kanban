package org.yandex.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yandex.kanban.model.EpicTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    public TaskManager taskManager;
    public HistoryManager historyManager;
    static final Path testFilePath = Path.of("src\\taskManagerStorageTest.csv");

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTasksManager(historyManager, Path.of("src\\taskManagerStorage.csv"));

        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 1
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.findTaskById(1)); // id = 2
    }

    @Test
    public void shouldAddAllTypesOfTasksToHistoryOrThrowNullPointerExceptionWhenTaskDoesNotExist() {
        historyManager.addTaskToHistory(taskManager.findTaskById(1));
        historyManager.addTaskToHistory(taskManager.findTaskById(2));
        historyManager.addTaskToHistory(taskManager.findTaskById(0));

        assertEquals(3, historyManager.getHistoryIds().size(),
                "Неверное количество задач в истории");
        assertEquals("[0, 2, 1]", historyManager.getHistoryIds().toString(), "Неверные Id в истории");

        historyManager.addTaskToHistory(taskManager.findTaskById(2));
        assertEquals(3, historyManager.getHistoryIds().size(),
                "Неверное количество задач в истории после добавления дублирующего ID 2");
        assertEquals("[2, 0, 1]", historyManager.getHistoryIds().toString(),
                "Неверные Id в истории после добавления дублирующего ID 2");

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> historyManager.addTaskToHistory(taskManager.findTaskById(10)));
        assertNull(ex.getMessage());
    }

    @Test
    public void shouldRemoveTaskFromHistory() {
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task 2", "Testing sub"),
                (EpicTask) taskManager.findTaskById(1)); // id = 3
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task 3", "Testing sub"),
                (EpicTask) taskManager.findTaskById(1)); // id = 4

        historyManager.addTaskToHistory(taskManager.findTaskById(2));
        historyManager.addTaskToHistory(taskManager.findTaskById(1));
        historyManager.addTaskToHistory(taskManager.findTaskById(2));
        historyManager.addTaskToHistory(taskManager.findTaskById(0));
        historyManager.addTaskToHistory(taskManager.findTaskById(4));
        historyManager.addTaskToHistory(taskManager.findTaskById(3));

        historyManager.removeTaskFromHistory(2);
        assertEquals(4, historyManager.getHistoryIds().size(),
                "Неверное количество задач в истории после удаления из конца истории");
        assertEquals("[3, 4, 0, 1]", historyManager.getHistoryIds().toString(),
                "Неверные Id в истории после удаления из конца истории");

        historyManager.removeTaskFromHistory(3);
        assertEquals(3, historyManager.getHistoryIds().size(),
                "Неверное количество задач в истории после удаления из начала истории");
        assertEquals("[4, 0, 1]", historyManager.getHistoryIds().toString(),
                "Неверные Id в истории после удаления из начала истории");

        historyManager.removeTaskFromHistory(0);
        assertEquals(2, historyManager.getHistoryIds().size(),
                "Неверное количество задач в истории после удаления из середины истории");
        assertEquals("[4, 1]", historyManager.getHistoryIds().toString(),
                "Неверные Id в истории после удаления из середины истории");

        historyManager.removeTaskFromHistory(10);
        assertEquals("[4, 1]", historyManager.getHistoryIds().toString(),
                "Неверные Id в истории после удаления из истории несуществующего ID");
    }

    @Test
    public void shouldGetHistoryIds() {
        assertTrue(historyManager.getHistoryIds().isEmpty(), "История не пустая");

        historyManager.addTaskToHistory(taskManager.findTaskById(1));
        historyManager.addTaskToHistory(taskManager.findTaskById(2));
        historyManager.addTaskToHistory(taskManager.findTaskById(0));

        assertFalse(historyManager.getHistoryIds().isEmpty(), "История пустая");
        assertEquals("[0, 2, 1]", historyManager.getHistoryIds().toString(), "Неверные Id в истории");
    }

    @Test
    public void shouldResetAllHistory() {
        historyManager.addTaskToHistory(taskManager.findTaskById(1));
        historyManager.addTaskToHistory(taskManager.findTaskById(2));
        historyManager.addTaskToHistory(taskManager.findTaskById(0));

        historyManager.reset();
        assertTrue(historyManager.getHistoryIds().isEmpty(), "История не пустая");
        assertEquals("[]", historyManager.getHistoryIds().toString(), "Список id истории не пустой");
    }
}