package ru.yandex.practicum.kanban.model;

public class SubTask extends Task {
    private final int parentId;

    public SubTask(String name, String description, int parentId) {
        super(name, description);
        this.parentId = parentId;
    }

    public SubTask(Integer id, String name, Status status, String description, int parentId) {
        super(id, name, status, description);
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(), getParentId());
    }
}
