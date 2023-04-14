package org.yandex.kanban.service;

import java.time.LocalDateTime;

public class TaskCreateDto {
    private final String name;
    private final String description;
    private final LocalDateTime startTime;
    private final Long durationInMins;

    public TaskCreateDto(String name, String description) {
        this.name = name;
        this.description = description;
        this.durationInMins = null;
        this.startTime = null;
    }

    public TaskCreateDto(String name, String description, LocalDateTime startTime, Long durationInMins) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.durationInMins = durationInMins;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Long getDurationInMins() {
        return durationInMins;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}