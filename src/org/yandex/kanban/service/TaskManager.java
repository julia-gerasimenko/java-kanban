package org.yandex.kanban.service;

import org.yandex.kanban.model.EpicTask;
import org.yandex.kanban.model.SubTask;
import org.yandex.kanban.model.Task;
import org.yandex.kanban.model.Type;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    void saveSingleTask(TaskCreateDto taskCreateDto);

    void saveEpicTask(TaskCreateDto taskCreateDto);

    void saveSubTask(TaskCreateDto taskCreateDto, EpicTask epicTask);

    ArrayList<Task> getAllTasks();

    ArrayList<Task> filterTasksByType(Type type);

    List<SubTask> getEpicSubTasks(int epicId);

    Task getTaskById(int id);

    Task findTaskById(int id);

    void deleteTaskById(int id);

    void deleteTaskByType(Type type);

    void deleteSubTasksForEpic(int epicId);

    void deleteAllTasks();

    void removeTaskFromHistory(Task task);

    void update(Task task);


}
