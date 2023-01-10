package model;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<SubTask> subTasks;
    private Type taskType;

    public EpicTask(String taskName, int taskId, String taskDescription, ArrayList<SubTask> subTasks, Type taskType) {
        super(taskName, taskId, taskDescription);
        this.subTasks = subTasks;
        this.taskType = taskType;
    }

    @Override
    public SingleTask withTaskStatus(TaskStatus taskStatus) {
        return new SingleTask(
                this.getTaskName(),
                this.getTaskId(),
                this.getTaskDescription(),
                getTaskStatus(),
                this.getType());
    }

    @Override
    public TaskStatus getTaskStatus() {
        TaskStatus epicTaskStatus = null;
        int taskStatusQuantity = 1;

        for (SubTask subTask : subTasks) {
            if (subTask.getTaskStatus() == TaskStatus.NEW) {
                taskStatusQuantity++;
                if (taskStatusQuantity == subTasks.size()) {
                    epicTaskStatus = TaskStatus.NEW;
                };
            } else if (subTask.getTaskStatus() == TaskStatus.DONE) {
                taskStatusQuantity++;
                if (taskStatusQuantity == subTasks.size()) {
                    epicTaskStatus = TaskStatus.DONE;
                }
            } else {
                epicTaskStatus = TaskStatus.IN_PROGRESS;
            }
        }
        return epicTaskStatus;
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public Type getType () {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + getTaskId() +
                ", taskStatus=" + getTaskStatus() +
                ", taskName=" + getTaskName() +
                ", taskDescription=" + getTaskDescription() +
                ", subTasks=" + subTasks +
                '}';
    }
}
