package model;

public class SubTask extends Task{
    private Status status;

    public SubTask(String name, int id, String description, Status status) {
        super(name, id, description);
        this.status = status;
    }

    public SubTask withNewStatus(Status status) {
        return new SubTask(
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
    public Type getType () { return Type.SUB; }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", taskStatus=" + getStatus() +
                ", taskName=" + getName() +
                ", taskDescription=" + getDescription() +
                '}';
    }
}
