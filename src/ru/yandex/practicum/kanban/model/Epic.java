package ru.yandex.practicum.kanban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(Integer id, String name, Status status, String description) {
        super(id, name, status, description);
        this.subTaskIds = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTaskIds.add(subTask.getId());
    }

    public void deleteSubTask(SubTask subTask) {
        subTaskIds.remove(subTask.getId());
    }

    public void clearSubTasks() {
        subTaskIds.clear();
    }

    public List<Integer> getSubTaskIds() {
        return Collections.unmodifiableList(subTaskIds);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", getId(), TaskType.EPIC, getName(), getStatus(), getDescription(), "");
    }
}
