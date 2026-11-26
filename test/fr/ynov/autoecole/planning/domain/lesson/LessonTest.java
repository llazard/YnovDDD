package fr.ynov.autoecole.planning.domain.lesson;

import fr.ynov.autoecole.planning.domain.instructor.InstructorId;
import fr.ynov.autoecole.shared.StudentId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LessonTest {

    private final LocalDateTime start = LocalDateTime.of(2026, 12, 10, 10, 0);
    private final Lesson lesson = new Lesson(LessonId.generate(), StudentId.generate(),
            InstructorId.generate(), new TimeSlot(start, Duration.ofHours(1)));

    @Test
    void aLessonCancelledLessThan48HoursBeforeIsDue() {
        lesson.cancel(start.minusHours(24));

        assertTrue(lesson.isDue());
    }

    @Test
    void aLessonCancelledMoreThan48HoursBeforeIsNotDue() {
        lesson.cancel(start.minusHours(72));

        assertFalse(lesson.isDue());
    }

    @Test
    void aGivenLessonCannotBeCancelled() {
        lesson.give();

        assertThrows(IllegalStateException.class, () -> lesson.cancel(start.minusHours(72)));
    }
}
