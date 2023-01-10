package model;

public abstract class Task {
    private String taskName;
    private int taskId;
    private String taskDescription;

    public Task (String taskName, int taskId, String taskDescription) {
        this.taskName = taskName;
        this.taskId = taskId;
        this.taskDescription = taskDescription;
    }
    // повторить про геттеры и сеттеры
    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() { return taskDescription; }

    public void setTaskId (int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    // изучить теорию по методу ниже, а также по абстрактным классам
    public abstract SingleTask withTaskStatus(TaskStatus taskStatus);

    public abstract TaskStatus getTaskStatus ();

    public abstract Type getType();

    public abstract String toString ();
}
