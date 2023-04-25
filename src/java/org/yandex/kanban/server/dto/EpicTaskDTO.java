package org.yandex.kanban.server.dto;

import org.yandex.kanban.model.Status;
import org.yandex.kanban.model.SubTask;

import java.util.List;

public class EpicTaskDTO {
    private String name;
    private Integer id;
    private String description;
    private Status status;
    private List<SubTask> subTasks;

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

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
