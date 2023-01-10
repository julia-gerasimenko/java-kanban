package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final TaskIdGenerator taskIdGenerator;
    private final HashMap<Integer, Task> taskById;

    public TaskManager() {
        this.taskIdGenerator = new TaskIdGenerator();
        this.taskById = new HashMap<>();
    }
    // здесь хочу сделать, чтобы тип подтягивался из объекта, а не из toCreate
    // что-то типа singleTask.getType вместо toCreate.getTaskType()
    public void saveSingleTask (ToCreate toCreate) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        SingleTask singleTask = new SingleTask(
            toCreate.getTaskName(),
            nextFreeId,
            toCreate.getTaskDescription(),
            TaskStatus.NEW,
            toCreate.getTaskType()
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
                toCreate.getTaskType()
        );
        taskById.put(epicTask.getTaskId(), epicTask);
    }

    public void saveSubTask (ToCreate toCreate, EpicTask epicTask) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        ArrayList<SubTask> updatedSubTasks = epicTask.getSubTasks();
        SubTask subTask = new SubTask(
                toCreate.getTaskName(),
                nextFreeId,
                toCreate.getTaskDescription(),
                TaskStatus.NEW,
                epicTask,
                toCreate.getTaskType()
        );
        updatedSubTasks.add(subTask);
        epicTask.setSubTasks(updatedSubTasks);
    }

    public ArrayList<Task> getAllTasks () {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public Task getTaskById (int taskId) {
        return taskById.get(taskId);
    }

    // этот метод разорать подробнее
    public void update(Task task) {
        taskById.put(task.getTaskId(), task);
    }

    // этот класс разобрать подробнее

    public static final class TaskIdGenerator {
        public int nextFreeId = 0;

        public int getNextFreeId () {
            return nextFreeId++;
        }
    }
}
