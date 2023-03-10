package org.yandex.kanban.model;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private List<SubTask> subTasks;
    private Status status;

    public EpicTask(String name, int id, String description, List<SubTask> subTasks,
                    Status status) {
        super(name, id, description);
        this.subTasks = subTasks == null ? new ArrayList<>() : subTasks;
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
                }
                ;
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

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public Type getType() {
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
