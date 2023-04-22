package org.yandex.kanban.service;

import org.yandex.kanban.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    Set<Task> getPrioritizedTasks(); // проверила на сортировку по startTime
    boolean isOvelappedTask(Task t);
    List<Task> getHistory(); // проверила на совпадение сохраненной истории

    SingleTask saveSingleTask(TaskCreateDto taskCreateDto); // проверила, сохраняется ли, и что сохраняется

    void saveEpicTask(TaskCreateDto taskCreateDto); // проверила, сохраняется ли, и что сохраняется

    void saveSubTask(TaskCreateDto taskCreateDto, EpicTask epicTask); // проверила, сохраняется ли, и что сохраняется

    ArrayList<Task> getAllTasks(); // проверила на null, по количеству тасков в списке
    List<Task> filterTasksByType(Type type); // проверяем на null, а также сравниваем по типу
    //, если вызвать незаведенный тип

    List<SubTask> getEpicSubTasks(int epicId); // проверяем на null, а также по количеству тасков в списке
    // также проверяем работу с пустым списком задач

    Task getTaskById(int id); // проверено со стандартным поведением и несуществующим id

    Task findTaskById(int id); // вспомогательный метод, входит в метод getTaskById, не тестировала отдельно

    void deleteTaskById(int id);

    void deleteTaskByType(Type type);

    void deleteSubTasksForEpic(int epicId);

    void deleteAllTasks();

    void removeTaskFromHistory(Task task);

    void update(Task task);


}
