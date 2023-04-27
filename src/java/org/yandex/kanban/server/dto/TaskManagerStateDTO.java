package org.yandex.kanban.server.dto;

import org.yandex.kanban.model.EpicTask;
import org.yandex.kanban.model.SingleTask;

import java.util.List;

public class TaskManagerStateDTO {
    private List<Integer> history;
    private List<SingleTask> singleTasks;
    private List<EpicTask> epicTasks;

    public List<Integer> getHistory() {
        return history;
    }

    public void setHistory(List<Integer> history) {
        this.history = history;
    }

    public List<SingleTask> getSingleTasks() {
        return singleTasks;
    }

    public void setSingleTasks(List<SingleTask> singleTasks) {
        this.singleTasks = singleTasks;
    }

    public List<EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public void setEpicTasks(List<EpicTask> epicTasks) {
        this.epicTasks = epicTasks;
    }


}
