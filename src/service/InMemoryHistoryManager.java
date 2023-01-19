package service;

import model.Task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    final private List<Integer> historyTaskIds = new LinkedList<>();
    final private int MEMORY_TASK_COUNT = 10;

    public InMemoryHistoryManager() {
    }

    @Override
    public void addTaskToHistory(Task task) {
        if (historyTaskIds.size() < MEMORY_TASK_COUNT) {
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
