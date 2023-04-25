package org.yandex.kanban.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final Status status;
    private final LocalDateTime startTime;
    private final Long durationInMins;
    private final int epicId;

    public SubTask(String name, int id, String description, Status status,
                   LocalDateTime startTime, Long durationInMins, int epicId) {
        super(name, id, description);
        this.status = status;
        this.startTime = startTime;
        this.durationInMins = durationInMins;
        this.epicId = epicId;
    }

    public SubTask withNewStatus(Status status) {
        return new SubTask(
                this.getName(),
                this.getId(),
                this.getDescription(),
                status,
                startTime,
                durationInMins,
                this.getEpicId()
        );
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.SUB;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
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
    public Long getDurationInMins() {
        return durationInMins;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        if (this.getStartTime() == null && ((SubTask) obj).getStartTime() == null) {
            return this.getId() == ((SubTask) obj).getId()
                    && (Objects.equals(this.getName(), ((SubTask) obj).getName())
                    && Objects.equals(this.getDescription(), ((SubTask) obj).getDescription())
                    && this.getStatus() == ((SubTask) obj).getStatus()
                    && this.getEpicId() == ((SubTask) obj).getEpicId()
            );

        } else {
            return this.getId() == ((SubTask) obj).getId()
                    && (Objects.equals(this.getName(), ((SubTask) obj).getName())
                    && Objects.equals(this.getDescription(), ((SubTask) obj).getDescription())
                    && this.getStatus() == ((SubTask) obj).getStatus()
                    && this.getStartTime().equals(((SubTask) obj).getStartTime())
                    && this.getDurationInMins().equals(((SubTask) obj).getDurationInMins())
                    && this.getEpicId() == ((SubTask) obj).getEpicId()
            );
        }
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", taskStatus=" + getStatus() +
                ", taskName=" + getName() +
                ", taskDescription=" + getDescription() +
                ", epicId=" + getEpicId() +
                '}';
    }
}
