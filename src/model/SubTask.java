package model;

public class SubTask extends Task{
    private TaskStatus taskStatus;
    private Type taskType;

    public SubTask(String taskName, int taskId, String taskDescription, TaskStatus taskStatus, Type taskType) {
        super(taskName, taskId, taskDescription);
        this.taskStatus = taskStatus;
        this.taskType = taskType;
    }

    public SubTask withNewTaskStatus(TaskStatus taskStatus) {
        return new SubTask(
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
    public Type getType () { return Type.SUB; }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getTaskId() +
                ", taskStatus=" + getTaskStatus() +
                ", taskName=" + getTaskName() +
                ", taskDescription=" + getTaskDescription() +
                '}';
    }
}
