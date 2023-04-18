package org.yandex.kanban.service;

import org.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // делала по совету одного из наставников Ивана Бутрим,
    // он аргументировал логику хранения id вместо task, мне понравилось
    // вижу, что тогда в данном случае движения с HashMap по сути становятся излишними
    // но я выполнила все, что в задаче, чтобы показать, что смысл я поняла

    CustomLinkedList<Integer> historyTaskIds = new CustomLinkedList<>();
    private final HashMap<Integer, Node<Integer>> historyMap = new HashMap<>();

    @Override
    public void addTaskToHistory(Task task) {
        if (task != null) {
            removeTaskFromHistory(task.getId());
            historyTaskIds.linkLast(task.getId());
            historyMap.put(task.getId(), historyTaskIds.tail);
        }
    }

    @Override
    public void removeTaskFromHistory(int id) {
        if (!historyMap.containsKey(id)) {
            return;
        }
        historyTaskIds.removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public List<Integer> getHistoryIds() {
        List<Integer> historyIds = new ArrayList<>(historyTaskIds.getTasks());
        Collections.reverse(historyIds);
        return historyIds;
    }

    @Override
    public void reset() {
        historyTaskIds = new CustomLinkedList<>();
        historyMap.clear();
    }

}
