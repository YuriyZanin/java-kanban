package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> history;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null)
            return;

        remove(task.getId());
        history.put(task.getId(), linkLast(task));
    }

    public void removeNode(Node node) {
        final Node prev = node.prev;
        final Node next = node.next;

        if (prev != null)
            prev.next = next;
        else first = next;

        if (next != null)
            next.prev = prev;
        else last = prev;
    }

    private Node linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;

        return newNode;
    }

    private List<Task> getTasks() {
        final List<Task> result = new ArrayList<>();
        Node current = first;
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}
