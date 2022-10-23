package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    private LocalDateTime endTime;

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(Integer id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                getId(),
                TaskType.EPIC,
                getName(),
                getStatus(),
                getDescription(),
                getStartTime() == null ? " " : Managers.dateTimeFormatter.format(getStartTime()),
                getDuration() == null ? " " : getDuration().toMinutes(),
                "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        String endTime = getEndTime() == null ? "" : Managers.dateTimeFormatter.format(getEndTime());
        String taskEndTime = epic.getEndTime() == null ? "" : Managers.dateTimeFormatter.format(epic.getEndTime());
        return Objects.equals(subTaskIds, epic.subTaskIds)
                && endTime.equals(taskEndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds, endTime);
    }
}
