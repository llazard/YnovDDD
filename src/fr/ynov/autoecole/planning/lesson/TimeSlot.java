package fr.ynov.autoecole.planning.lesson;

import java.time.Duration;
import java.time.LocalDateTime;

public record TimeSlot(LocalDateTime start, Duration duration) {

    public TimeSlot {
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("A time slot must have a positive duration");
        }
    }

    public LocalDateTime end() {
        return start.plus(duration);
    }
}
