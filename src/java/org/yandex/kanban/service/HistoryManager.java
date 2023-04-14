package org.yandex.kanban.service;

import org.yandex.kanban.model.Task;

import java.util.List;

public interface HistoryManager {

    void addTaskToHistory(Task task);

    void removeTaskFromHistory(int id);

    List<Integer> getHistoryIds();

    void reset();
}



