package service;

import model.Task;

import java.util.List;

public interface HistoryManager {

    void addTaskToHistory(Task task);

    // так рекомендовал наставник из 2 когорты на вебинаре, и аргументировал, мне понравилась его идея
    List<Integer> getHistoryIds();

}
