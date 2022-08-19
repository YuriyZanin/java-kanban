package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskIds = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTaskIds.add(subTask.getId());
    }

    public void deleteSubTask(SubTask subTask) {
        subTaskIds.remove(subTask.getId());
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", tasks.size()=" + subTaskIds.size() +
                '}';
    }
}
