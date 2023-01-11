package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final TaskIdGenerator taskIdGenerator;
    private final HashMap<Integer, Task> taskById;
    public static final int STARTING_ID_FOR_SUB = 1000;

    public TaskManager() {
        this.taskIdGenerator = new TaskIdGenerator();
        this.taskById = new HashMap<>();
    }
    // здесь хочу сделать, чтобы тип подтягивался из объекта, а не из toCreate
    // что-то типа singleTask.getType вместо Type.SINGLE
    public void saveSingleTask (ToCreate toCreate) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        SingleTask singleTask = new SingleTask(
            toCreate.getTaskName(),
            nextFreeId,
            toCreate.getTaskDescription(),
            TaskStatus.NEW,
            Type.SINGLE
        );
        taskById.put(singleTask.getTaskId(), singleTask);
    }

    public void saveEpicTask (ToCreate toCreate) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        ArrayList<SubTask> subTasks  = new ArrayList<>();
        EpicTask epicTask = new EpicTask(
                toCreate.getTaskName(),
                nextFreeId,
                toCreate.getTaskDescription(),
                subTasks,
                null,
                Type.EPIC
        );
        taskById.put(epicTask.getTaskId(), epicTask);
    }

    public void saveSubTask (ToCreate toCreate, EpicTask epicTask) {
        int nextFreeId = STARTING_ID_FOR_SUB + taskIdGenerator.getNextFreeId();
        ArrayList<SubTask> updatedSubTasks = epicTask.getSubTasks();
        SubTask subTask = new SubTask(
                toCreate.getTaskName(),
                nextFreeId,
                toCreate.getTaskDescription(),
                TaskStatus.NEW,
                Type.SUB
        );
        updatedSubTasks.add(subTask);
        epicTask.setSubTasks(updatedSubTasks);
        epicTask.getTaskStatus();
    }

    public ArrayList<Task> getAllTasks () {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public ArrayList<Task> TasksByType (Type type) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            if (task.getType().equals(type)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public ArrayList<SubTask> TasksForEpic (int taskId) {
        ArrayList<SubTask> listOfSubTasks = null;
        for (Task task : taskById.values()) {
            if (task.getTaskId() == taskId) {
                EpicTask epicTask = (EpicTask) task;
                listOfSubTasks = epicTask.getSubTasks();
            }
        }
        return listOfSubTasks;
    }

    public Task getTaskById (int taskId) {
        Task taskGotById = null;
        if (taskId > STARTING_ID_FOR_SUB) {
            for (Task task : taskById.values()) {
                if (task.getType().equals(Type.EPIC)) {
                    EpicTask epicTask = (EpicTask) task;
                    ArrayList<SubTask> listOfSubTasks = epicTask.getSubTasks();
                    for (SubTask subTask : listOfSubTasks) {
                        if (subTask.getTaskId() == taskId) {
                            taskGotById = subTask;
                        }
                    }
                }
            }
        } else { taskGotById = taskById.get(taskId);}
        return taskGotById;
    }

    public void deleteTaskById (int taskId) {
        if (taskId > STARTING_ID_FOR_SUB) {
            for (Task task : taskById.values()) {
                if (task.getType().equals(Type.EPIC)) {
                    EpicTask epicTask = (EpicTask) task;
                    ArrayList<SubTask> listOfSubTasks = epicTask.getSubTasks();
                    for (SubTask subTask : listOfSubTasks) {
                        if (subTask.getTaskId() == taskId) {
                            int indexOfSub = listOfSubTasks.indexOf(subTask);
                            listOfSubTasks.remove(indexOfSub);
                            epicTask.setSubTasks(listOfSubTasks);
                            epicTask.getTaskStatus();
                            break;
                        }
                    }
                }
            }
        } else {taskById.remove(taskId);}
    }

    public void deleteTaskByType (Type type) {
        for (Task task : taskById.values()) {
            if (task.getType().equals(type)) {
                taskById.remove(task.getTaskId());
            }
        }
    }

    public void deleteTaskForEpicId (int taskId) {
        for (Task task : taskById.values()) {
            if ((task.getType().equals(Type.EPIC)) && (task.getTaskId() == taskId)) {
                EpicTask epicTask = (EpicTask) task;
                epicTask.getSubTasks().clear();
                epicTask.getTaskStatus();
                break;
            }
        }
    }

    public void deleteAllTasks () {
        taskById.clear();
    }

    public void update(Task task) {
        if (task.getType().equals(Type.SUB)) {
            for (Task epicToFind : taskById.values()) {
                if (epicToFind.getType().equals(Type.EPIC)) {
                    EpicTask epicTask = (EpicTask) epicToFind;
                    ArrayList<SubTask> listOfSubTasks = epicTask.getSubTasks();
                    for (SubTask subToFind : listOfSubTasks) {
                        if (subToFind.getTaskId() == task.getTaskId()) {
                            int indexOfSub = listOfSubTasks.indexOf(subToFind);
                            listOfSubTasks.remove(indexOfSub);
                            listOfSubTasks.add(indexOfSub, (SubTask) task);
                            epicTask.setSubTasks(listOfSubTasks);
                            epicTask.getTaskStatus();
                            break;
                        }
                    }
                }
            }
        } else taskById.put(task.getTaskId(), task);
    }

    public static final class TaskIdGenerator {
        public int nextFreeId = 0;

        public int getNextFreeId () {
            return nextFreeId++;
        }
    }
}
