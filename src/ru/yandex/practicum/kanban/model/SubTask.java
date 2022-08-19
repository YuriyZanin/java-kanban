package ru.yandex.practicum.kanban.model;

public class SubTask extends Task {
    private Epic parent;

    public SubTask(String name, String description, Epic parent) {
        super(name, description);
        this.parent = parent;
    }

    public Epic getParent() {
        return parent;
    }

    public void setParent(Epic parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", parent=" + parent +
                '}';
    }
}
