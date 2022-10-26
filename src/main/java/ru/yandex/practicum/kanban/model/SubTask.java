package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.DateTimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final int parentId;

    public SubTask(String name, String description, LocalDateTime startTime, Duration duration, int parentId) {
        super(name, description, startTime, duration);
        this.parentId = parentId;
    }

    public SubTask(Integer id, String name, Status status, String description, LocalDateTime startTime, Duration duration, int parentId) {
        super(id, name, status, description, startTime, duration);
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                getId(),
                TaskType.SUBTASK,
                getName(),
                getStatus(),
                getDescription(),
                getStartTime() == null ? " " : DateTimeUtil.DATE_TIME_FORMATTER.format(getStartTime()),
                getDuration() == null ? " " : getDuration().toMinutes(),
                getParentId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubTask)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SubTask subTask = (SubTask) o;
        return parentId == subTask.parentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentId);
    }
}
