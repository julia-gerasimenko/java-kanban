package model;

public class SingleTask extends Task{
    private TaskStatus taskStatus;
    private Type taskType;

    public SingleTask(String taskName, int taskId, String taskDescription, TaskStatus taskStatus, Type taskType) {
        super(taskName, taskId, taskDescription);
        this.taskStatus = taskStatus;
        this.taskType = taskType;
    }

    public SingleTask withNewTaskStatus(TaskStatus taskStatus) {
        return new SingleTask(
               this.getTaskName(),
               this.getTaskId(),
               this.getTaskDescription(),
               taskStatus,
               this.getType());
    }

    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    @Override
    public Type getType () { return Type.SINGLE; }

    @Override
    public String toString() {
        return "SingleTask{" +
                "id=" + getTaskId() +
                ", taskStatus=" + getTaskStatus() +
                ", taskName=" + getTaskName() +
                ", taskType=" + getType() +
                ", taskDescription=" + getTaskDescription() +
                '}';
    }
}
