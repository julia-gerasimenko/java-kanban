package org.yandex.kanban.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yandex.kanban.model.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// для подзадачи наличие Эпика не проверяю - у меня подзадачи складываются в лист внутри эпика и сами по себе
// информацию об эпике внутри себя не несут

public abstract class TaskManagerTest<TTaskManager extends TaskManager> {
    public TTaskManager taskManager;

    @Test
    public void shouldGetPrioritizedTasks() {
        assertEquals("[]", taskManager.getPrioritizedTasks().toString(),
                "Некорректный вывод отсортированных задач при отсутствии задач");
        taskManager.saveSingleTask(new TaskCreateDto("Single task", "The first step",
                LocalDateTime.of(2023, Month.APRIL, 30, 0, 0, 0), 30L));
        // id = 0
        String toCompareSingle = "[SingleTask{id=0, taskStatus=NEW, taskName=Single task, taskType=SINGLE, " +
                "taskDescription=The first step}]";
        assertEquals(toCompareSingle, taskManager.getPrioritizedTasks().toString(),
                "Некорректный вывод задач при сортировке по startTime одной сохраненной задачи");

        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id =1
        taskManager.saveSubTask(new TaskCreateDto("Sub task", "Sub step",
                LocalDateTime.of(2023, Month.APRIL, 27, 0, 0, 0),
                30L), (EpicTask) taskManager.findTaskById(1)); // id = 2
        List<String> listToCompare = List.of(
                "SubTask{id=2, taskStatus=NEW, taskName=Sub task, taskDescription=Sub step, epicId=1}",
                "SingleTask{id=0, taskStatus=NEW, taskName=Single task, taskType=SINGLE, " +
                        "taskDescription=The first step}");
        assertEquals(listToCompare.toString(), taskManager.getPrioritizedTasks().toString(),
                "Некорректная сортировка задач по startTime");

        taskManager.saveSubTask(new TaskCreateDto("Sub task", "Sub 2 step"),
                (EpicTask) taskManager.findTaskById(1)); // id = 3
        List<String> listToCompareTwo = List.of(
                "SubTask{id=2, taskStatus=NEW, taskName=Sub task, taskDescription=Sub step, epicId=1}",
                "SingleTask{id=0, taskStatus=NEW, taskName=Single task, taskType=SINGLE, " +
                        "taskDescription=The first step}",
                "SubTask{id=3, taskStatus=NEW, taskName=Sub task, taskDescription=Sub 2 step, epicId=1}");
        assertEquals(listToCompareTwo.toString(), taskManager.getPrioritizedTasks().toString(),
                "Некорректная сортировка задач при добавлении задачи с отсутствующим временем");

    }


    @Test
    public void shouldReturnHistory() {
        assertTrue(taskManager.getHistory().isEmpty(), "История не пустая");

        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.getTaskById(0); // создаем историю
        final List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "Неверное количество задач в истории");
        assertEquals(taskManager.getTaskById(0), history.get(0),
                "Задачи в истории не совпадают");
    }

    @Test
    public void shouldDeleteSubFromPrioritizedTasks() {
        taskManager.saveSingleTask(new TaskCreateDto("Single task", "The first step",
                LocalDateTime.of(2023, Month.APRIL, 30, 0, 0, 0), 30L));
        // id = 0
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id =1
        taskManager.saveSubTask(new TaskCreateDto("Sub task", "Sub step",
                LocalDateTime.of(2023, Month.APRIL, 27, 0, 0, 0),
                30L), (EpicTask) taskManager.findTaskById(1)); // id = 2

        taskManager.deleteTaskById(2);
        String toCompareSingle = "[SingleTask{id=0, taskStatus=NEW, taskName=Single task, taskType=SINGLE, " +
                "taskDescription=The first step}]";
        assertEquals(toCompareSingle, taskManager.getPrioritizedTasks().toString(),
                "Некорректный вывод PrioritizedTasks после удаления SubTask");
    }

    @Test
    public void shouldUpdatePrioritizedTasks() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0

        SingleTask singleTask = new SingleTask("Corrected single", 0, "Testing correction",
                Status.IN_PROGRESS, null, null);
        taskManager.update(singleTask);

        assertEquals(List.of(singleTask).toString(), taskManager.getPrioritizedTasks().toString(),
                "Не корректно вносятся изменения в prioritizedTasks при обновлении задачи");

    }

    @Test
    public void shouldCheckIfTasksAreOverlapped() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task", "Testing single",
                LocalDateTime.of(2023, Month.APRIL, 30, 0, 0, 0), 30L));
        // id = 0
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 1
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub",
                        LocalDateTime.of(2023, Month.APRIL, 30, 5, 0, 0),
                        30L),
                (EpicTask) taskManager.getTaskById(1)); // id = 2
        taskManager.saveSubTask(new TaskCreateDto("Test Sub 2 task", "Testing 2 sub",
                        LocalDateTime.of(2023, Month.APRIL, 30, 0, 15, 0),
                        30L),
                (EpicTask) taskManager.getTaskById(1)); // id = 3

        assertEquals(List.of("SingleTask{id=0, taskStatus=NEW, taskName=Test Single task, " +
                                "taskType=SINGLE, taskDescription=Testing single}",
                        "SubTask{id=2, taskStatus=NEW, taskName=Test Sub task, " +
                                "taskDescription=Testing sub, epicId=1}").toString(),
                taskManager.getPrioritizedTasks().toString(),
                "Пересекающиеся задачи сохраняются некорректно");
    }

    @Test
    public void shouldSaveSingleTaskWithNoDateTime() {

        SingleTask singleTask = new SingleTask("Test Single task", 0, "Testing single", Status.NEW,
                null, null);
        taskManager.saveSingleTask(new TaskCreateDto(singleTask.getName(),
                singleTask.getDescription())); // id = 0

        final SingleTask savedTask = (SingleTask) taskManager.getTaskById(0);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(singleTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(singleTask, taskManager.getTaskById(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldSaveSingleTaskWithDateTime() {

        SingleTask singleTask = new SingleTask("Test Single task", 0, "Testing single", Status.NEW,
                LocalDateTime.of(2023, Month.APRIL, 30, 0, 0, 0), 30L);
        taskManager.saveSingleTask(new TaskCreateDto(singleTask.getName(), singleTask.getDescription(),
                LocalDateTime.of(2023, Month.APRIL, 30, 0, 0, 0), 30L));
        // id = 0

        final SingleTask savedTask = (SingleTask) taskManager.getTaskById(0);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(singleTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(singleTask, taskManager.getTaskById(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldSaveEpicTaskWithNoDateTime() {
        EpicTask epicTask = new EpicTask("Test Epic task", 0, "Testing epic", null, Status.NEW);
        taskManager.saveEpicTask(new TaskCreateDto(epicTask.getName(),
                epicTask.getDescription())); // id = 0

        final EpicTask savedTask = (EpicTask) taskManager.getTaskById(0);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(epicTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epicTask, taskManager.getTaskById(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldSavSubTask() {
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id =0
        SubTask subTask = new SubTask("Test Sub task", 1, "Testing sub",
                Status.NEW, null, null, 0);
        taskManager.saveSubTask(new TaskCreateDto(subTask.getName(),
                subTask.getDescription()), (EpicTask) taskManager.findTaskById(0)); // id = 1

        final SubTask savedTask = (SubTask) taskManager.getTaskById(1);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(subTask, savedTask, "Задачи не совпадают.");

        final List<SubTask> tasks = taskManager.getEpicSubTasks(0);

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(subTask, taskManager.getTaskById(1), "Задачи не совпадают.");
    }

    // здесь также проверка изменения времени Эпика при изменении в одной из сабтасков
    @Test
    public void shouldSavSubTaskWithDateTimeAndUpdateEpicsDateTime() {
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id =0
        SubTask subTask = new SubTask("Test Sub task", 1, "Testing sub", Status.NEW,
                LocalDateTime.of(2023, Month.APRIL, 27, 0, 0, 0), 30L, 0);
        taskManager.saveSubTask(new TaskCreateDto(subTask.getName(), subTask.getDescription(),
                LocalDateTime.of(2023, Month.APRIL, 27, 0, 0, 0),
                30L), (EpicTask) taskManager.findTaskById(0)); // id = 1

        final SubTask savedTask = (SubTask) taskManager.getTaskById(1);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(subTask, savedTask, "Задачи не совпадают.");

        final List<SubTask> tasks = taskManager.getEpicSubTasks(0);

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(subTask, taskManager.getTaskById(1), "Задачи не совпадают.");

        taskManager.saveSubTask(new TaskCreateDto(subTask.getName(), subTask.getDescription(),
                LocalDateTime.of(2023, Month.APRIL, 30, 0, 0, 0),
                60L), (EpicTask) taskManager.findTaskById(0)); // id = 2
        assertEquals(LocalDateTime.of(2023, Month.APRIL, 27, 0, 0, 0),
                taskManager.getTaskById(0).getStartTime(), "startTime Эпика не совпадает");
        assertEquals(LocalDateTime.of(2023, Month.APRIL, 30, 1, 0, 0),
                taskManager.getTaskById(0).getEndTime(), "endTime Эпика не совпадает");
        assertEquals(90L, taskManager.getTaskById(0).getDurationInMins(),
                "durationInMins Эпика не совпадает");
    }

    @Test
    public void shouldGetAllSavedTasks() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 1

        EpicTask epicTask = new EpicTask("Test Epic task", 1, "Testing epic",
                null, Status.NEW);
        SingleTask singleTask = new SingleTask("Test Single task", 0, "Testing single", Status.NEW, null, null);

        assertNotNull(taskManager.getAllTasks(), "Задачи на возвращаются.");
        assertEquals(2, taskManager.getAllTasks().size(), "Неверное количество задач.");

        assertEquals(epicTask, taskManager.getTaskById(1), "Задачи Epic не совпадают");
        assertEquals(singleTask, taskManager.getTaskById(0), "Задачи Single не совпадают");
    }

    @Test
    public void shouldFilterTasksByTypesOrThrowIllegalArgumentExceptionWhenTypeDoesNotExist() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveSingleTask(new TaskCreateDto("Test 2nd Single task",
                "Testing 2nd single")); // id = 1
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 2

        List<Task> singleTasks = taskManager.filterTasksByType(Type.SINGLE);
        List<Task> epicTasks = taskManager.filterTasksByType(Type.EPIC);

        assertNotNull(singleTasks, "SingleTasks не возвращаются");
        assertNotNull(epicTasks, "EpicTasks не возвращаются");

        assertEquals(2, singleTasks.size(), "Неверное количество сохраненных SingleTasks");
        assertEquals(1, epicTasks.size(), "Неверное количество сохраненных EpicTasks");

        assertEquals(Type.SINGLE, singleTasks.get(1).getType(), "Не возвращает тип SINGLE");
        assertEquals(Type.EPIC, epicTasks.get(0).getType(), "Не возвращает тип EPIC");

        taskManager.deleteAllTasks();
        assertTrue(taskManager.filterTasksByType(Type.SINGLE).isEmpty(),
                "Неверно отображает список для удаленного типа задач");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.filterTasksByType(Type.valueOf("SAB")));
        assertEquals("No enum constant org.yandex.kanban.model.Type.SAB", ex.getMessage(),
                "Неверная обработка некорректного Типа задачи");
    }

    @Test
    public void shouldFilterSubTasksFromEpic() {
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id =0

        SubTask subTask = new SubTask("Test Sub task", 1, "Testing sub", Status.NEW,
                null, null, 0);
        taskManager.saveSubTask(new TaskCreateDto(subTask.getName(),
                subTask.getDescription()), (EpicTask) taskManager.findTaskById(0)); // id = 1

        SubTask subTaskTwo = new SubTask("Test 2nd Sub task", 2, "Testing sub 2", Status.NEW,
                null, null, 0);
        taskManager.saveSubTask(new TaskCreateDto(subTaskTwo.getName(),
                subTaskTwo.getDescription()), (EpicTask) taskManager.findTaskById(0)); // id = 2

        List<SubTask> subTasks = taskManager.getEpicSubTasks(0);

        assertNotNull(subTasks, "SubTasks не возвращаются");
        assertFalse(subTasks.isEmpty(), "Возвращает пустой subTasks");
        assertEquals(2, subTasks.size(), "Неверное количество сохраненных SubTasks");
        assertEquals(subTaskTwo, subTasks.get(1), "Возвращает неверный SubTask");
        assertEquals(Type.SUB, subTasks.get(0).getType(), "Не возвращает тип SUB");

        taskManager.deleteSubTasksForEpic(0);
        assertTrue(taskManager.getEpicSubTasks(0).isEmpty());

    }

    @Test
    public void shouldReturnTaskById() {
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 0
        EpicTask epicTask = new EpicTask("Test Epic task", 0, "Testing epic",
                null, Status.NEW);
        assertEquals(epicTask, taskManager.getTaskById(0), "Задача с id 0 не найдена");

        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(0)); // id = 1
        SubTask subTask = new SubTask("Test Sub task", 1, "Testing sub", Status.NEW,
                null, null, 0);
        assertEquals(subTask, taskManager.getTaskById(1), "Задача с id 1 не найдена");

        assertNull(taskManager.getTaskById(3), "Некорректная обработка задачи с несуществующим id");
    }

    @Test
    public void shouldDeleteTaskById() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 1
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(1)); // id = 2

        taskManager.deleteTaskById(2);
        assertEquals(2, taskManager.getAllTasks().size(), "Неверное количество задач после удаления");
        assertNull(taskManager.getTaskById(2), "Некорректная обработка удаленной ранее задачи");
        assertEquals("[SingleTask{id=0, taskStatus=NEW, taskName=Test Single task, taskType=SINGLE, " +
                        "taskDescription=Testing single}]", taskManager.getPrioritizedTasks().toString(),
                "Некорректное обновление списка prioritizedTasks после удаления задачи");

        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(0);
        taskManager.deleteTaskById(10); // удаляем несуществующий id
        assertTrue(taskManager.getAllTasks().isEmpty(), "Некорректное удаление всех сохраненных задач");
    }

    @Test
    public void shouldDeleteTaskByTypeOrThrowIllegalArgumentExceptionWhenTypeDoesNotExist() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task 2",
                "Testing single 2")); // id = 1
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 2
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(2)); // id = 3

        taskManager.deleteTaskByType(Type.SUB);
        taskManager.deleteTaskByType(Type.EPIC);
        assertEquals(2, taskManager.getAllTasks().size(), "Неверное количество задач после удаления");
        assertTrue(taskManager.filterTasksByType(Type.EPIC).isEmpty(),
                "Некорректная обработка удаленных задач EPIC");

        taskManager.deleteTaskByType(Type.SINGLE);
        assertTrue(taskManager.getAllTasks().isEmpty(), "Некорректное удаление всех сохраненных задач");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.filterTasksByType(Type.valueOf("SAB")));
        assertEquals("No enum constant org.yandex.kanban.model.Type.SAB", ex.getMessage(),
                "Неверная обработка некорректного Типа задачи");
    }

    @Test
    public void shouldDeleteSubTasksForEpic() {
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 0
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(0)); // id = 1
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(0)); // id = 2

        taskManager.deleteSubTasksForEpic(0);
        assertEquals(1, taskManager.getAllTasks().size(),
                "Неверное количество задач после удаления SubTasks");
        assertNull(taskManager.getTaskById(1), "Некорректное удаление SubTask id 1");
        assertTrue(taskManager.getEpicSubTasks(0).isEmpty(),
                "Некорректное удаление SubTasks");
    }

    @Test
    public void shouldDeleteAllTasks() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task 2",
                "Testing single 2")); // id = 1
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 2
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(2)); // id = 3

        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Неверное количество задач после удаления");
        assertTrue(taskManager.filterTasksByType(Type.EPIC).isEmpty(),
                "Некорректная обработка удаленных задач EPIC");
        assertNull(taskManager.getTaskById(1), "Некорректное удаление SingleTask id 1");
        assertTrue(taskManager.getAllTasks().isEmpty(), "Некорректное удаление всех сохраненных задач");
    }

    @Test
    public void shouldRemoveTaskFromHistory() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task 2",
                "Testing single 2")); // id = 1
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 2
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(2)); // id = 3

        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(0);

        taskManager.removeTaskFromHistory(taskManager.getTaskById(2)); // удаляем эпик с сабтасками
        assertEquals(2, taskManager.getHistory().size(), "EpicTask и его SubTasks удалены не корректно");
        taskManager.removeTaskFromHistory(taskManager.getTaskById(0));
        assertEquals(1, taskManager.getHistory().size(), "SingleTask удалена не корректно");
        assertEquals(taskManager.getHistory().get(0), taskManager.getTaskById(1),
                "В истории сохранилась неверная задача");
        taskManager.removeTaskFromHistory(taskManager.getTaskById(1));
        assertTrue(taskManager.getHistory().isEmpty(), "История не очистилась полностью");
    }

    @Test
    public void shouldUpdateTask() {
        taskManager.saveSingleTask(new TaskCreateDto("Test Single task",
                "Testing single")); // id = 0
        taskManager.saveEpicTask(new TaskCreateDto("Test Epic task", "Testing epic")); // id = 1
        taskManager.saveSubTask(new TaskCreateDto("Test Sub task", "Testing sub"),
                (EpicTask) taskManager.getTaskById(1)); // id = 2

        SingleTask singleTask = new SingleTask("Corrected single", 0, "Testing correction",
                Status.IN_PROGRESS, null, null);
        EpicTask epicTask = new EpicTask("Corrected Epic", 1, "Testing correction", null,
                Status.DONE);
        SubTask subTask = new SubTask("corrected Sub", 2, "Testing correction", Status.IN_PROGRESS,
                null, null, 1);

        taskManager.update(singleTask);
        assertEquals(singleTask, taskManager.getTaskById(0), "SingeTask не обновлена");
        taskManager.update(subTask);
        assertEquals(subTask, taskManager.getTaskById(2), "SubTask не обновлена");
        taskManager.update(epicTask);
        assertEquals(epicTask, taskManager.getTaskById(1), "EpicTask не обновлена");

    }


}
