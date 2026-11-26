package fr.ynov.autoecole.planning.application;

import fr.ynov.autoecole.planning.domain.instructor.InstructorId;
import fr.ynov.autoecole.planning.domain.lesson.LessonId;
import fr.ynov.autoecole.planning.domain.lesson.LessonRepository;
import fr.ynov.autoecole.planning.domain.lesson.TimeSlot;
import fr.ynov.autoecole.planning.infrastructure.InMemoryLessonRepository;
import fr.ynov.autoecole.secretariat.domain.student.Hours;
import fr.ynov.autoecole.secretariat.domain.student.Student;
import fr.ynov.autoecole.secretariat.domain.student.StudentRepository;
import fr.ynov.autoecole.secretariat.infrastructure.InMemoryStudentRepository;
import fr.ynov.autoecole.shared.FullName;
import fr.ynov.autoecole.shared.StudentId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LessonBookingTest {

    private final StudentRepository students = new InMemoryStudentRepository();
    private final LessonRepository lessons = new InMemoryLessonRepository();
    private final LessonBooking booking = new LessonBooking(students, lessons);
    private final StudentId lea = StudentId.generate();
    private final InstructorId paul = InstructorId.generate();
    private final TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 12, 10, 10, 0), Duration.ofHours(1));

    @BeforeEach
    void registerLeaWithOneHour() {
        Student student = new Student(lea, new FullName("Lea", "Martin"));
        student.buyHours(new Hours(1));
        students.save(student);
    }

    @Test
    void bookingALessonUsesOneHour() {
        LessonId id = booking.book(lea, paul, slot);

        assertTrue(lessons.findById(id).isPresent());
        assertEquals(Hours.ZERO, students.findById(lea).orElseThrow().remainingHours());
    }

    @Test
    void aStudentCannotBookMoreHoursThanBought() {
        booking.book(lea, paul, slot);

        assertThrows(IllegalStateException.class, () -> booking.book(lea, paul, slot));
    }
}
