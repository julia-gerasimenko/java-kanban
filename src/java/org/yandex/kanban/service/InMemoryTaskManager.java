package org.yandex.kanban.service;

import org.yandex.kanban.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.yandex.kanban.model.Type.EPIC;

public class InMemoryTaskManager implements TaskManager {
    private final IdGenerator idGenerator;
    final HashMap<Integer, Task> taskById;
    static HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        InMemoryTaskManager.historyManager = historyManager;
        this.idGenerator = new IdGenerator();
        this.taskById = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyTasks = new ArrayList<>();
        for (int id : historyManager.getHistoryIds()) {
            historyTasks.add(findTaskById(id));
        }
        return historyTasks;
    }


    @Override
    public void saveSingleTask(TaskCreateDto taskCreateDto) {
        int nextFreeId = idGenerator.getNextFreeId();
        SingleTask singleTask = new SingleTask(
                taskCreateDto.getName(),
                nextFreeId,
                taskCreateDto.getDescription(),
                Status.NEW,
                taskCreateDto.getStartTime(),
                taskCreateDto.getDurationInMins());
        taskById.put(singleTask.getId(), singleTask);
    }

    @Override
    public void saveEpicTask(TaskCreateDto taskCreateDto) {
        int nextFreeId = idGenerator.getNextFreeId();
        ArrayList<SubTask> subTasks = new ArrayList<>();
        EpicTask epicTask = new EpicTask(
                taskCreateDto.getName(),
                nextFreeId,
                taskCreateDto.getDescription(),
                subTasks,
                Status.NEW
        );
        taskById.put(epicTask.getId(), epicTask);
    }

    @Override
    public void saveSubTask(TaskCreateDto taskCreateDto, EpicTask epicTask) {
        int nextFreeId = idGenerator.getNextFreeId();
        List<SubTask> updatedSubTasks = epicTask.getSubTasks();
        SubTask subTask = new SubTask(
                taskCreateDto.getName(),
                nextFreeId,
                taskCreateDto.getDescription(),
                Status.NEW,
                taskCreateDto.getStartTime(),
                taskCreateDto.getDurationInMins());
        updatedSubTasks.add(subTask);
        epicTask.setSubTasks(updatedSubTasks);
        epicTask.getStatus();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskById.values());
    }

    @Override
    public List<Task> filterTasksByType(Type type) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            if (type.equals(task.getType())) {
                tasks.add(task);
            }
        }
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return tasks;
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        for (Task task : taskById.values()) {
            if (task.getId() == epicId) {
                EpicTask epicTask = (EpicTask) task;
                return epicTask.getSubTasks();
            }
        }
        return List.of();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = findTaskById(id);
        if (task != null) {
            historyManager.addTaskToHistory(task);
        }
        return task;
    }

    @Override
    public Task findTaskById(int id) {
        if (taskById.containsKey(id)) {
            return taskById.get(id);
        }

        for (Task task : taskById.values()) {
            if (EPIC.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;
                for (SubTask subTask : epicTask.getSubTasks()) {
                    if (subTask.getId() == id) {
                        return subTask;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        if (taskById.containsKey(id)) {
            removeTaskFromHistory(taskById.get(id));
            taskById.remove(id);
            return;
        }
        for (Task task : taskById.values()) {
            if (EPIC.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;
                for (SubTask subTask : epicTask.getSubTasks()) {
                    if (subTask.getId() == id) {
                        removeTaskFromHistory(subTask);
                        epicTask.getSubTasks().remove(subTask);
                        epicTask.getStatus();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void deleteTaskByType(Type type) {

        switch (type) {
            case SINGLE:
            case EPIC:
                for (int i = taskById.size()-1; i > -1; i--) {
                    if (type.equals(taskById.get(i).getType())) {
                        removeTaskFromHistory(taskById.get(i));
                        taskById.remove(taskById.get(i).getId());
                    }
                }
            case SUB: // если мы хотим удалить все сабтаски у всех эпиков
                for (int i = 0; i < taskById.size(); i++) {
                    if (EPIC.equals(taskById.get(i).getType())) {
                        EpicTask epicTask = (EpicTask) taskById.get(i);
                        epicTask.getSubTasks().clear();
                    }
                }
        }
    }

    @Override
    public void deleteSubTasksForEpic(int epicId) {
        if (!taskById.containsKey(epicId)) {
            return;
        }
        Task task = taskById.get(epicId);

        if (EPIC.equals(task.getType())) {
            EpicTask epicTask = (EpicTask) task;
            epicTask.getSubTasks().forEach(this::removeTaskFromHistory);
            epicTask.getSubTasks().clear();
            epicTask.getStatus();
        }
    }

    @Override
    public void deleteAllTasks() {
        taskById.clear();
        historyManager.reset();
    }

    @Override
    public void removeTaskFromHistory(Task task) {
        historyManager.removeTaskFromHistory(task.getId());
        if (EPIC.equals(task.getType())) {
            EpicTask epicTask = (EpicTask) task;

            if (epicTask.getSubTasks().isEmpty()) {
                return;
            }
            for (int i = 0; i < epicTask.getSubTasks().size(); i++) {
                historyManager.removeTaskFromHistory(epicTask.getSubTasks().get(i).getId());
            }
        }
    }


    @Override
    public void update(Task task) {
        if (!task.getType().equals(Type.SUB)) {
            taskById.put(task.getId(), task);
        }
        for (Task epicTaskToFind : taskById.values()) {
            if (!EPIC.equals(epicTaskToFind.getType())) {
                continue;
            }
            EpicTask epicTask = (EpicTask) epicTaskToFind;
            List<SubTask> listOfSubTasks = epicTask.getSubTasks();
            for (SubTask subToFind : listOfSubTasks) {
                if (subToFind.getId() == task.getId()) {
                    int indexOfSub = epicTask.getSubTasks().indexOf(subToFind);
                    listOfSubTasks.remove(indexOfSub);
                    listOfSubTasks.add(indexOfSub, (SubTask) task);
                    epicTask.setSubTasks(listOfSubTasks);
                    epicTask.getStatus();
                    break;
                }
            }
        }
    }

    public final class IdGenerator {

        public int getNextFreeId() {
            if (taskById.isEmpty()) {
                return 0;
            }

            int maxId = 0;

            for (Integer id : taskById.keySet()) {
                if (id > maxId) {
                    maxId = id;
                }
            }

            for (int id : taskById.keySet()) {
                if (EPIC.equals(taskById.get(id).getType())) {
                    List<SubTask> subTasks = getEpicSubTasks(id);
                    for (SubTask task : subTasks) {
                        if (task.getId() > maxId) {
                            maxId = task.getId();
                        }
                    }
                }
            }
            return maxId + 1;
        }
    }
}