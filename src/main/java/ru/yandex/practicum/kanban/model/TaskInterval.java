package ru.yandex.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskInterval {

    public static final Duration DEFAULT_DURATION = Duration.ofMinutes(15);
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public TaskInterval(LocalDateTime startTime) {
        this.startTime = LocalDateTime.of(startTime.getYear(), startTime.getMonth(), startTime.getDayOfMonth(),
                startTime.getHour(), roundMinutes(startTime.getMinute()));
        this.endTime = this.startTime.plus(DEFAULT_DURATION);
    }

    public static List<TaskInterval> of(LocalDateTime startTime, LocalDateTime endTime) {
        List<TaskInterval> taskIntervals = new ArrayList<>();
        TaskInterval next = new TaskInterval(startTime);
        taskIntervals.add(next);
        while (next.endTime.isBefore(endTime)) {
            next = next.nextInterval();
            taskIntervals.add(next);
        }
        return taskIntervals;
    }

    public TaskInterval nextInterval() {
        return new TaskInterval(endTime);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskInterval)) return false;
        TaskInterval taskInterval = (TaskInterval) o;
        return startTime.equals(taskInterval.startTime) && endTime.equals(taskInterval.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return "Interval{" +
                "startTime=" + startTime.format(formatter) +
                ", endTime=" + endTime.format(formatter) +
                '}';
    }

    private int roundMinutes(int minutes) {
        int duration = (int) DEFAULT_DURATION.toMinutes();
        if (minutes % duration == 0) {
            return minutes;
        } else {
            return duration * ((int) Math.floor(Math.abs(minutes / duration)));
        }
    }
}
