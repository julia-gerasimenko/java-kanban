package org.yandex.kanban.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CustomLinkedList<T> {
    private Node<T> head;
    Node<T> tail;

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
