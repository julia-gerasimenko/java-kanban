package service;

import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final IdGenerator idGenerator;
    private final HashMap<Integer, Task> taskById;
    private final InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager(InMemoryHistoryManager inMemoryHistoryManager) {
        this.inMemoryHistoryManager = inMemoryHistoryManager;
        this.idGenerator = new IdGenerator();
        this.taskById = new HashMap<>();
    }

    public List<Task> getHistory() {
        List<Task> historyTasks = new ArrayList<>();
        for (int id : inMemoryHistoryManager.getHistoryIds()) {
            if (taskById.containsKey(id)) {
                historyTasks.add(taskById.get(id));
            }
        }
        return Collections.unmodifiableList(historyTasks);
    }

    @Override
    public void saveSingleTask(TaskCreateDto taskCreateDto) {
        int nextFreeId = idGenerator.getNextFreeId();
        SingleTask singleTask = new SingleTask(
                taskCreateDto.getName(),
                nextFreeId,
                taskCreateDto.getDescription(),
                Status.NEW
        );
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
                null
        );
        taskById.put(epicTask.getId(), epicTask);
    }

    @Override
    public void saveSubTask(TaskCreateDto taskCreateDto, EpicTask epicTask) {
        int nextFreeId = idGenerator.getNextFreeId();
        ArrayList<SubTask> updatedSubTasks = epicTask.getSubTasks();
        SubTask subTask = new SubTask(
                taskCreateDto.getName(),
                nextFreeId,
                taskCreateDto.getDescription(),
                Status.NEW
        );
        updatedSubTasks.add(subTask);
        epicTask.setSubTasks(updatedSubTasks);
        epicTask.getStatus();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskById.values());
    }

    @Override
    public ArrayList<Task> filterTasksByType(Type type) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            if (type.equals(task.getType())) {
                tasks.add(task);
            }
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
        for (Task task : taskById.values()) {
            if (task.getId() == id) {
                inMemoryHistoryManager.addTaskToHistory(task);
                return taskById.get(id);
            }
        }
        for (Task task : taskById.values()) {
            if (Type.EPIC.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;
                for (SubTask subTask : epicTask.getSubTasks()) {
                    if (subTask.getId() == id) {
                        inMemoryHistoryManager.addTaskToHistory(subTask);
                        return subTask;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        for (Task task : taskById.values()) {
            if (task.getId() == id) {
                taskById.remove(id);
                break;
            }
        }
        for (Task task : taskById.values()) {
            if (Type.EPIC.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;
                for (SubTask subTask : epicTask.getSubTasks()) {
                    if (subTask.getId() == id) {
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
        for (Task task : new ArrayList<>(taskById.values())) {
            if (type.equals(task.getType())) {
                taskById.remove(task.getId());
            }
        }
    }

    @Override
    public void deleteSubTasksForEpic(int epicId) {
        for (Task task : taskById.values()) {
            if ((Type.EPIC.equals(task.getType())) && (task.getId() == epicId)) {
                EpicTask epicTask = (EpicTask) task;
                epicTask.getSubTasks().clear();
                epicTask.setSubTasks(new ArrayList<>());
                epicTask.getStatus();
                break;
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        taskById.clear();
    }

    @Override
    public void update(Task task) {
        if (!task.getType().equals(Type.SUB)) {
            taskById.put(task.getId(), task);
        }
        for (Task epicTaskToFind : taskById.values()) {
            if (!Type.EPIC.equals(task.getType())) {
                continue;
            }
            EpicTask epicTask = (EpicTask) epicTaskToFind;
            for (SubTask subToFind : epicTask.getSubTasks()) {
                if (subToFind.getId() == task.getId()) {
                    int indexOfSub = epicTask.getSubTasks().indexOf(subToFind);
                    epicTask.getSubTasks().remove(indexOfSub);
                    epicTask.getSubTasks().add(indexOfSub, (SubTask) task);
                    epicTask.setSubTasks(epicTask.getSubTasks());
                    epicTask.getStatus();
                    break;
                }
            }
        }
    }

    public static final class IdGenerator {
        public int nextFreeId = 0;

        public int getNextFreeId() {
            return nextFreeId++;
        }
    }
}
