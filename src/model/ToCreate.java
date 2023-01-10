package model;

public class ToCreate {
    private String taskName;
    private String taskDescription;
    private Type taskType;

    public ToCreate(String taskName, String taskDescription, Type taskType) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Type getTaskType() { return taskType; }
}