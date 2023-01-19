package service;

import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    final List<Integer> historyTaskIds = new ArrayList<>();

    public InMemoryHistoryManager() {
    }

    @Override
    public void addTaskToHistory(Task task) {
        if (historyTaskIds.size() < 10) {
            this.historyTaskIds.add(task.getId());
        } else {
            historyTaskIds.remove(0);
            this.historyTaskIds.add(task.getId());
        }
    }

    @Override
    public List<Integer> getHistoryIds() {
        return Collections.unmodifiableList(historyTaskIds);
    }


}
