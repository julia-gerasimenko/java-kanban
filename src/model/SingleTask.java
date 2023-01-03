package model;

public class SingleTask extends Task{
    private TaskStatus taskStatus;

    public SingleTask(String taskName, Integer taskId, TaskStatus taskStatus) {
        super(taskName, taskId);
        this.taskStatus = taskStatus;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
}
