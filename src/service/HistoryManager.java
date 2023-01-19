package service;

import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface HistoryManager {
    void addTaskToHistory(Task task);
    List<Integer> getHistoryIds();

}
