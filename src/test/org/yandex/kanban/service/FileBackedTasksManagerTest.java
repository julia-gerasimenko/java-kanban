package org.yandex.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.yandex.kanban.service.FileBackedTasksManager.loadFromFile;

class FileBackedTasksManagerTest extends TaskManagerTest<TaskManager> {
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
    }

    @Test
    public void shouldSaveEmptyTaskManagerAndLoadFromFileEmptyTaskManager() {

        HistoryManager historyManagerTwo = new InMemoryHistoryManager();
        FileBackedTasksManager taskManagerTwo = new FileBackedTasksManager
                (historyManagerTwo, testFilePath);
        assertTrue(taskManagerTwo.getAllTasks().isEmpty(), "Список задач не пустой");
        taskManagerTwo.save();
        FileBackedTasksManager taskManagerThree = loadFromFile(testFilePath);
        assertTrue(taskManagerThree.getAllTasks().isEmpty(),
                "Список задач после загрузки не пустой");
        assertEquals(taskManagerTwo.getAllTasks(), taskManagerThree.getAllTasks(),
                "Список задач не совпадает после выгрузки из файла");
        //здесь я не разобралась немного про AssertAll, спрошу у наставника
    }

    @Test
    public void shouldSaveAndLoadFromFileEpicWithNoSubtasksAndEmptyHistory() throws IOException {

        HistoryManager historyManagerTwo = new InMemoryHistoryManager();
        FileBackedTasksManager taskManagerTwo = new FileBackedTasksManager
                (historyManagerTwo, testFilePath);
        taskManagerTwo.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 0
        assertFalse(taskManagerTwo.getAllTasks().isEmpty(), "Список задач пустой");

        taskManagerTwo.save();
        List<String> result = Files.readAllLines(testFilePath);
        assertEquals(List.of(
                        "id,type,name,status,description,epic,startTime,durationInMins",
                        "0,EPIC,Test Epic task,NEW,Testing epic,,,,",""),
                result, "Список задач в файле после сохранения не совпадает");


        FileBackedTasksManager taskManagerThree = loadFromFile(testFilePath);
        assertFalse(taskManagerThree.getAllTasks().isEmpty(),
                "Список задач после загрузки пустой");
        assertEquals(taskManagerTwo.getAllTasks(), taskManagerThree.getAllTasks(),
                "Список задач не совпадает после выгрузки из файла");
        assertTrue(taskManagerThree.getEpicSubTasks(0).isEmpty(),
                "Subtasks у эпика не пустые");
        assertTrue(taskManagerThree.getHistory().isEmpty(), "История не пустая");
    }

}