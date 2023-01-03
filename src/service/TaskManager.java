package service;

import model.SingleTask;
import model.SingleTaskToCreate;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private TaskIdGenerator taskIdGenerator = new TaskIdGenerator();
    private HashMap<Integer, Task> taskById;

    public void saveNewTask (SingleTaskToCreate singleTaskToCreate) {
        int nexFreeId = taskIdGenerator.getNextFreeId();
        SingleTask singleTask = new SingleTask(
            singleTaskToCreate.getTaskName(),
            nexFreeId,
            TaskStatus.NEW
        );
        taskById.put(singleTask.getTaskId(), singleTask);
    }

    public ArrayList<Task> getAllTasks () {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskById.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public ArrayList<Task> getTaskById (List<Integer> taskIds) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int id : taskIds) {
            tasks.add(taskById.get(id));
        }
        return tasks;
    }

    /* public Task getTaskById (int taskId) {
        Task taskNameById = null;
        if (taskById.containsKey(taskId)) {
            taskNameById = taskById.get(taskId);
        }
        return taskNameById;
    }*/

    public static final class TaskIdGenerator {
        public int nextFreeId = 0;

        public int getNextFreeId () {

            return nextFreeId++;
        }
    }
}
