package ru.yandex.practicum.kanban.utils;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final Duration DEFAULT_DURATION_OF_A_TASK = Duration.ofMinutes(15);

    private DateTimeUtil() {
    }
}
