package model;

public class SubTask extends Task{
    private TaskStatus taskStatus;

    public SubTask(String taskName, int taskId, String taskDescription, TaskStatus taskStatus) {
        super(taskName, taskId, taskDescription);
        this.taskStatus = taskStatus;
    }

    public SubTask withNewTaskStatus(TaskStatus taskStatus) {
        return new SubTask(
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
