package fr.ynov.autoecole.planning.domain.lesson;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeSlotTest {

    @Test
    void aTimeSlotLastsMoreThanZero() {
        LocalDateTime start = LocalDateTime.of(2026, 12, 10, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(start, Duration.ZERO));
    }
}
