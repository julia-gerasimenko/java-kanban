package org.yandex.kanban.service;

import org.yandex.kanban.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    Set<Task> getPrioritizedTasks(); // да
    boolean isOvelappedTask(Task t); // ---
    List<Task> getHistory(); // да

    SingleTask saveSingleTask(TaskCreateDto taskCreateDto); // да

    EpicTask saveEpicTask(TaskCreateDto taskCreateDto); // да

    SubTask saveSubTask(TaskCreateDto taskCreateDto, EpicTask epicTask); // !!!

    ArrayList<Task> getAllTasks(); // да
    List<Task> filterTasksByType(Type type); // да

    List<SubTask> getEpicSubTasks(int epicId); // да

    Task getTaskById(int id); // да

    Task findTaskById(int id); // ---

    void deleteTaskById(int id); // да

    void deleteTaskByType(Type type); // да

    void deleteSubTasksForEpic(int epicId); // да

    void deleteAllTasks(); // да

    void removeTaskFromHistory(Task task); // ---

    void update(Task task); // да для SingleTask, EpicTask, !!!


}
