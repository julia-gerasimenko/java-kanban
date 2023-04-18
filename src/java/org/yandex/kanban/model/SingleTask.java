package org.yandex.kanban.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SingleTask extends Task {
    private final Status status;
    private final Long durationInMins;
    private final LocalDateTime startTime;


    public SingleTask(String name, int id, String description, Status status, LocalDateTime startTime,
                      Long durationInMins) {
        super(name, id, description);
        this.status = status;
        this.startTime = startTime;
        this.durationInMins = durationInMins;
    }

    public SingleTask withNewStatus(Status status) {
        return new SingleTask(
                this.getName(),
                this.getId(),
                this.getDescription(),
                status, startTime, durationInMins);
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
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public Long getDurationInMins() {
        return durationInMins;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        if (durationInMins == null) {
            return startTime;
        }
        return startTime.plusMinutes(durationInMins);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        if (this.getStartTime() == null && ((SingleTask) obj).getStartTime() == null) {
            return this.getId() == ((SingleTask) obj).getId()
                    && (Objects.equals(this.getName(), ((SingleTask) obj).getName())
                    && Objects.equals(this.getDescription(), ((SingleTask) obj).getDescription())
                    && this.getStatus() == ((SingleTask) obj).getStatus());
        } else {
            return this.getId() == ((SingleTask) obj).getId()
                    && (Objects.equals(this.getName(), ((SingleTask) obj).getName())
                    && Objects.equals(this.getDescription(), ((SingleTask) obj).getDescription())
                    && this.getStatus() == ((SingleTask) obj).getStatus()
                    && this.getStartTime().equals(((SingleTask) obj).getStartTime())
                    && this.getDurationInMins().equals(((SingleTask) obj).getDurationInMins())
            );
        }
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
