package service;

import model.Task;

import java.util.*;

final class InMemoryHistoryManager implements HistoryManager {

    // делала по совету одного из наставников Ивана Бутрим,
    // он аргументировал логику хранения id вместо task, мне понравилось
    // вижу, что тогда в данном случае движения с HashMap по сути становятся излишними
    // но я выполнила все, что в задаче, чтобы показать, что смысл я поняла

    private CustomLinkedList<Integer> historyTaskIds = new CustomLinkedList<>();
    final private HashMap<Integer, Node<Integer>> historyMap = new HashMap<>();

    @Override
    public void addTaskToHistory(Task task) {
        removeTaskFromHistory(task.getId());
        historyTaskIds.linkLast(task.getId());
        historyMap.put(task.getId(), historyTaskIds.tail);
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
        Collections.reverse(historyIds); // здесь у меня изначально был итератор по списку с конца,
        // но потом я узнала про эту функцию, она показалась короче и более читабельна
        return historyIds;
    }

    @Override
    public void reset() {
        historyTaskIds = new CustomLinkedList<>();
        historyMap.clear();
    }

    private static class Node<E> {
        public E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        public void linkLast(T element) { // добавляем элемент в хвост списка
            final Node<T> newNode = new Node<>(tail, element, null);

            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }
            tail = newNode;

        }

        public List<T> getTasks() { // собираем все задачи в список
            List<T> resulted = new ArrayList<>();
            for (Node<T> currentElement = head; currentElement != null; currentElement = currentElement.next) {
                resulted.add(currentElement.data);
            }
            return Collections.unmodifiableList(resulted);
        }

        public void removeNode(Node<T> node) { // удаляем узел
            if (node == null) {
                return;
            }

            Node<T> previousNode = node.prev;
            Node<T> nextNode = node.next;

            if (previousNode == null) {
                head = nextNode;
            } else {
                previousNode.next = nextNode;
            }
            if (nextNode == null) {
                tail = previousNode;
            } else {
                nextNode.prev = previousNode;
            }

        }


    }
}
