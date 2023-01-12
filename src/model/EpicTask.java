package model;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<SubTask> subTasks;
    private Status status;

    public EpicTask(String name, int id, String description, ArrayList<SubTask> subTasks,
                    Status status) {
        super(name, id, description);
        this.subTasks = subTasks;
        this.status = status;
    }

    @Override
    public Status getStatus() {
        Status status = null;
        int statusQuantity = 0;

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus().equals(Status.NEW)) {
                statusQuantity++;
                if (statusQuantity == subTasks.size()) {
                    status = Status.NEW;
                };
            } else if (subTask.getStatus().equals(Status.DONE)) {
                statusQuantity++;
                if (statusQuantity == subTasks.size()) {
                    status = Status.DONE;
                }
            } else {
                status = Status.IN_PROGRESS;
            }
        }
        return status;
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
                "id=" + getId() +
                ", taskStatus=" + getStatus() +
                ", taskName=" + getName() +
                ", taskDescription=" + getDescription() + ",\r\n" +
                "subTasks=" + getSubTasks() +
                '}';
    }
}
