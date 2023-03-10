package org.yandex.kanban.model;

public class SingleTask extends Task {
    private Status status;

    public SingleTask(String name, int id, String description, Status status) {
        super(name, id, description);
        this.status = status;
    }

    public SingleTask withNewStatus(Status status) {
        return new SingleTask(
                this.getName(),
                this.getId(),
                this.getDescription(),
                status
        );
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.SINGLE;
    }

    @Override
    public String toString() {
        return "SingleTask{" +
                "id=" + getId() +
                ", taskStatus=" + getStatus() +
                ", taskName=" + getName() +
                ", taskType=" + getType() +
                ", taskDescription=" + getDescription() +
                '}';
    }
}
