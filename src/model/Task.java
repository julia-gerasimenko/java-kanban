package model;

public abstract class Task {
    private String taskName;
    private Integer taskId;

    public Task (String taskName, Integer taskId) {
        this.taskName = taskName;
        this.taskId = taskId;
    }

    public String getTaskName() {

        return taskName;
    }

    public void setTaskId (Integer taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {

        return taskId;
    }

    public abstract TaskStatus getTaskStatus ();
}
