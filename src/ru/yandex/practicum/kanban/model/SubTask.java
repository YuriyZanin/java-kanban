package ru.yandex.practicum.kanban.model;

public class SubTask extends Task {
    private final int parentId;

    public SubTask(String name, String description, int parentId) {
        super(name, description);
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", parentId=" + parentId +
                '}';
    }
}
