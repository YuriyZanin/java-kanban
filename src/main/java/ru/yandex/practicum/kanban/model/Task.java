package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.DateTimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = Duration.ZERO;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description, startTime);
        this.duration = duration;
    }

    public Task(Integer id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        this(name, description, startTime, duration);
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        } else return null;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                getId(),
                TaskType.TASK,
                getName(),
                getStatus(),
                getDescription(),
                getStartTime() == null ? " " : DateTimeUtil.DATE_TIME_FORMATTER.format(getStartTime()),
                getDuration() == null ? " " : getDuration().toMinutes(),
                "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return name.equals(task.name) && Objects.equals(description, task.description) && id.equals(task.id)
                && status == task.status && Objects.equals(duration, task.duration)
                && Objects.equals(getStartTime(), task.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, duration, startTime);
    }
}
