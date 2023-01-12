package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final IdGenerator idGenerator;
    private final HashMap<Integer, Task> taskById;

    public TaskManager() {
        this.idGenerator = new IdGenerator();
        this.taskById = new HashMap<>();
    }

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

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public ArrayList<Task> filterTasksByType(Type type) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            if (type.equals(task.getType())) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<SubTask> getEpicSubTasks(int epicId) {
        for (Task task : taskById.values()) {
            if (task.getId() == epicId) {
                EpicTask epicTask = (EpicTask) task;
                return epicTask.getSubTasks();
            }
        }
        return List.of();
    }

    public Task getTaskById(int id) {
        for (Task task : taskById.values()) {
            if (task.getId() == id) {
                return taskById.get(id);
            }
        }
        for (Task task : taskById.values()) {
            if (Type.EPIC.equals(task.getType())) {
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

    public void deleteTaskByType(Type type) {
        for (Task task : new ArrayList<>(taskById.values())) {
            if (type.equals(task.getType())) {
                taskById.remove(task.getId());
            }
        }
    }

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

    public void deleteAllTasks() {
        taskById.clear();
    }

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
