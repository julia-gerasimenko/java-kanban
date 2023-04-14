package org.yandex.kanban.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yandex.kanban.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.yandex.kanban.model.Status.*;

public class EpicTaskTest {
    public TaskManager taskManager;
    static final Path testFilePath = Path.of("src\\taskManagerStorageTest.csv");

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @BeforeEach
    public void beforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTasksManager(historyManager,
                testFilePath);
        taskManager.deleteAllTasks();
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing status")); // id = 0

        taskManager.saveSubTask(new TaskCreateDto("Test subTask 1", "The first Step"),
                (EpicTask) taskManager.findTaskById(0)); // id = 1
        taskManager.saveSubTask(new TaskCreateDto("Test subTask 2", "The second Step"),
                (EpicTask) taskManager.findTaskById(0)); // id = 2
    }

    @Test
    public void shouldReturnEpicStatusNewWhen2SubtasksAreNew() {
        assertEquals(NEW, taskManager.getTaskById(0).getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgressWhenAllSubtasksAreInProgress() {
        SubTask subTaskOne = (SubTask) taskManager.getTaskById(1);
        taskManager.update(subTaskOne.withNewStatus(IN_PROGRESS));

        SubTask subTaskTwo = (SubTask) taskManager.getTaskById(2);
        taskManager.update(subTaskTwo.withNewStatus(IN_PROGRESS));

        assertEquals(IN_PROGRESS, taskManager.getTaskById(0).getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgressWhenSubtasksHaveDifferentStatus() {
        SubTask subTaskOne = (SubTask) taskManager.getTaskById(1);
        taskManager.update(subTaskOne.withNewStatus(DONE));

        assertEquals(IN_PROGRESS, taskManager.getTaskById(0).getStatus());
    }

    @Test
    public void shouldReturnEpicStatusDoneWhen2SubtasksAreDone() {
        SubTask subTaskOne = (SubTask) taskManager.getTaskById(1);
        taskManager.update(subTaskOne.withNewStatus(DONE));

        SubTask subTaskTwo = (SubTask) taskManager.getTaskById(2);
        taskManager.update(subTaskTwo.withNewStatus(DONE));

        assertEquals(DONE, taskManager.getTaskById(0).getStatus());
    }

    @Test
    public void shouldReturnEpicStatusNullWhen0Subtasks() {
        taskManager.deleteSubTasksForEpic(0);
        assertEquals(NEW, taskManager.getTaskById(0).getStatus());
    }
}

