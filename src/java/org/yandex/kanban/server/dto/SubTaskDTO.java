package org.yandex.kanban.server.dto;

import org.yandex.kanban.model.Status;

import java.time.LocalDateTime;

public class SubTaskDTO {
    private String name;
    private Integer id;
    private String description;
    private Status status;
    private Long durationInMins;
    private LocalDateTime startTime;
    private Integer epicId;

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

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
}
