package org.yandex.kanban.server.dto;

import org.yandex.kanban.model.Status;

import java.time.LocalDateTime;
import java.util.Objects;

public class SingleTaskDTO {
    private String name;
    private Integer id;
    private String description;
    private Status status;
    private Long durationInMins;
    private LocalDateTime startTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getDurationInMins() {
        return durationInMins;
    }

    public void setDurationInMins(Long durationInMins) {
        this.durationInMins = durationInMins;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleTaskDTO that = (SingleTaskDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id) && Objects.equals(description,
                that.description) && status == that.status && Objects.equals(durationInMins, that.durationInMins)
                && Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, description, status, durationInMins, startTime);
    }

    @Override
    public String toString() {
        return "SingleTaskDTO{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", durationInMins=" + durationInMins +
                ", startTime=" + startTime +
                '}';
    }
}
