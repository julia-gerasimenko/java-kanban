package model;

public class SingleTask extends Task {
    private TaskStatus taskStatus;

    public SingleTask(String taskName, int taskId, String taskDescription, TaskStatus taskStatus) {
        super(taskName, taskId, taskDescription);
        this.taskStatus = taskStatus;
    }

    public SingleTask withNewTaskStatus(TaskStatus taskStatus) {
        return new SingleTask(
                this.getTaskName(),
                this.getTaskId(),
                this.getTaskDescription(),
                taskStatus
        );
    }

    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    @Override
    public Type getType() {
        return Type.SINGLE;
    }

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
