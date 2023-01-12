package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final TaskIdGenerator taskIdGenerator;
    private final HashMap<Integer, Task> taskById;

    public TaskManager() {
        this.taskIdGenerator = new TaskIdGenerator();
        this.taskById = new HashMap<>();
    }

    public void saveSingleTask(ToCreate toCreate) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        SingleTask singleTask = new SingleTask(
                toCreate.getTaskName(),
                nextFreeId,
                toCreate.getTaskDescription(),
                TaskStatus.NEW
        );
        taskById.put(singleTask.getTaskId(), singleTask);
    }

    public void saveEpicTask(ToCreate toCreate) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        ArrayList<SubTask> subTasks = new ArrayList<>();
        EpicTask epicTask = new EpicTask(
                toCreate.getTaskName(),
                nextFreeId,
                toCreate.getTaskDescription(),
                subTasks,
                null
        );
        taskById.put(epicTask.getTaskId(), epicTask);
    }

    public void saveSubTask(ToCreate toCreate, EpicTask epicTask) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        ArrayList<SubTask> updatedSubTasks = epicTask.getSubTasks();
        SubTask subTask = new SubTask(
                toCreate.getTaskName(),
                nextFreeId,
                toCreate.getTaskDescription(),
                TaskStatus.NEW
        );
        updatedSubTasks.add(subTask);
        epicTask.setSubTasks(updatedSubTasks);
        epicTask.getTaskStatus();
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
            if (task.getTaskId() == epicId) {
                EpicTask epicTask = (EpicTask) task;
                return epicTask.getSubTasks();
            }
        }
        return List.of();
    }

    public Task getTaskById(int taskId) {
        for (Task task : taskById.values()) {
            if (task.getTaskId() == taskId) {
                return taskById.get(taskId);
            }
        }
        for (Task task : taskById.values()) {
            if (Type.EPIC.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;
                for (SubTask subTask : epicTask.getSubTasks()) {
                    if (subTask.getTaskId() == taskId) {
                        return subTask;
                    }
                }
            }
        }
        return null;
    }

    public void deleteTaskById(int taskId) {
        for (Task task : taskById.values()) {
            if (task.getTaskId() == taskId) {
                taskById.remove(taskId);
                break;
            }
        }
        for (Task task : taskById.values()) {
            if (Type.EPIC.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;
                for (SubTask subTask : epicTask.getSubTasks()) {
                    if (subTask.getTaskId() == taskId) {
                        epicTask.getSubTasks().remove(subTask);
                        epicTask.getTaskStatus();
                        break;
                    }
                }
            }
        }
    }

    public void deleteTaskByType(Type type) {
        for (Task task : new ArrayList<>(taskById.values())) {
            if (type.equals(task.getType())) {
                taskById.remove(task.getTaskId());
            }
        }
    }

    public void deleteSubTasksForEpic(int epicId) {
        for (Task task : taskById.values()) {
            if ((Type.EPIC.equals(task.getType())) && (task.getTaskId() == epicId)) {
                EpicTask epicTask = (EpicTask) task;
                epicTask.getSubTasks().clear();
                epicTask.setSubTasks(new ArrayList<>());
                epicTask.getTaskStatus();
                break;
            }
        }
    }

    public void deleteAllTasks() {
        taskById.clear();
    }

    public void update(Task task) {
        if (!task.getType().equals(Type.SUB)) {
            taskById.put(task.getTaskId(), task);
        }
        for (Task epicTaskToFind : taskById.values()) {
            if (!Type.EPIC.equals(task.getType())) {
                continue;
            }
            EpicTask epicTask = (EpicTask) epicTaskToFind;
            for (SubTask subToFind : epicTask.getSubTasks()) {
                if (subToFind.getTaskId() == task.getTaskId()) {
                    int indexOfSub = epicTask.getSubTasks().indexOf(subToFind);
                    epicTask.getSubTasks().remove(indexOfSub);
                    epicTask.getSubTasks().add(indexOfSub, (SubTask) task);
                    epicTask.setSubTasks(epicTask.getSubTasks());
                    epicTask.getTaskStatus();
                    break;
                }
            }
        }
    }

    public static final class TaskIdGenerator {
        public int nextFreeId = 0;

        public int getNextFreeId() {
            return nextFreeId++;
        }
    }
}
