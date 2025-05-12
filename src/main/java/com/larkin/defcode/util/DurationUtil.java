package com.larkin.defcode.util;

import com.larkin.defcode.exception.IllegalDurationFormatException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGInterval;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@Slf4j
public class DurationUtil {

    public static Duration parseInputDuration(String input) {
        log.debug("Parsing Duration: {}", input);
        if (input == null || input.trim().isEmpty()) {
            log.error("Invalid duration format");
            throw new IllegalDurationFormatException();
        }

        Pattern pattern = Pattern.compile("^(\\d+)([smhd])$");
        Matcher matcher = pattern.matcher(input.trim().toLowerCase());

        if (!matcher.matches()) {
            log.error("Invalid duration format");
            throw new IllegalDurationFormatException();
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        switch (unit) {
            case "s" -> {
                log.debug("Lifetime in seconds");
                return Duration.ofSeconds(value);
            }
            case "m" -> {
                log.debug("Lifetime in minutes");
                return Duration.ofMinutes(value);
            }
            case "h" -> {
                log.debug("Lifetime in hours");
                return Duration.ofHours(value);
            }
            case "d" -> {
                log.debug("Lifetime in days");
                return Duration.ofDays(value);
            }
            default -> {
                log.error("Invalid duration format");
                throw new IllegalDurationFormatException();
            }
        }
    }

    public static Duration pgIntervalToDuration(PGInterval pgInterval) {
        log.debug("Converting PG interval to duration");
        Duration duration = Duration.ZERO;
        duration = duration.plusDays(pgInterval.getDays());
        duration = duration.plusHours(pgInterval.getHours());
        duration = duration.plusMinutes(pgInterval.getMinutes());
        duration = duration.plusSeconds((long) pgInterval.getSeconds());
        log.debug("Successfully converted PG interval to duration");
        return duration;
    }

    public static PGInterval durationToPgInterval(Duration duration) {
        if (duration == null) {
            return null;
        }
        long seconds = duration.getSeconds();
        int days = (int) (seconds / (24 * 3600));
        seconds %= (24 * 3600);
        int hours = (int) (seconds / 3600);
        seconds %= 3600;
        int minutes = (int) (seconds / 60);
        seconds %= 60;
        return new PGInterval(0, 0, days, hours, minutes, seconds);
    }
}
