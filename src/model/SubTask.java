package model;

public class SubTask extends Task{
    private TaskStatus taskStatus; // "NEW" / "IN_PROGRESS" / "DONE"
    private EpicTask epicTask;
    private Type taskType;

    public SubTask(String taskName, int taskId, String taskDescription, TaskStatus taskStatus, EpicTask epicTask,
                   Type taskType) {
        super(taskName, taskId, taskDescription);
        this.taskStatus = taskStatus;
        this.epicTask = epicTask;
        this.taskType = taskType;
    }

    // почему singleTask, если у нас subtask
    @Override
    public SingleTask withTaskStatus(TaskStatus taskStatus) {
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
    public Type getType () { return Type.SUB; }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getTaskId() +
                ", taskStatus=" + taskStatus +
                ", taskName=" + getTaskName() +
                ", taskDescription=" + getTaskDescription() +
                ", epicTask=" + epicTask +
                '}';
    }
}
