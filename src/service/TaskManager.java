package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import model.Type;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void saveSingleTask(TaskCreateDto taskCreateDto);

    void saveEpicTask(TaskCreateDto taskCreateDto);

    void saveSubTask(TaskCreateDto taskCreateDto, EpicTask epicTask);

    ArrayList<Task> getAllTasks();

    ArrayList<Task> filterTasksByType(Type type);

    List<SubTask> getEpicSubTasks(int epicId);

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void deleteTaskByType(Type type);

    void deleteSubTasksForEpic(int epicId);

    void deleteAllTasks();

    void update(Task task);

}
