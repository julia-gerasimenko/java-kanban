package model;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<SubTask> subTasks;
    private TaskStatus taskStatus;
    private Type taskType;

    public EpicTask(String taskName, int taskId, String taskDescription, ArrayList<SubTask> subTasks,
                    TaskStatus taskStatus, Type taskType) {
        super(taskName, taskId, taskDescription);
        this.subTasks = subTasks;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
    }

    @Override
    public TaskStatus getTaskStatus() {
        TaskStatus taskStatus = null;
        int taskStatusQuantity = 0;

        for (SubTask subTask : subTasks) {
            if (subTask.getTaskStatus().equals(TaskStatus.NEW)) {
                taskStatusQuantity++;
                if (taskStatusQuantity == subTasks.size()) {
                    taskStatus = TaskStatus.NEW;
                };
            } else if (subTask.getTaskStatus().equals(TaskStatus.DONE)) {
                taskStatusQuantity++;
                if (taskStatusQuantity == subTasks.size()) {
                    taskStatus = TaskStatus.DONE;
                }
            } else {
                taskStatus = TaskStatus.IN_PROGRESS;
            }
        }
        return taskStatus;
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
                ", subTasks=" + getSubTasks() +
                '}';
    }
}
