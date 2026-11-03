package fr.ynov.autoecole.lesson;

import fr.ynov.autoecole.instructor.InstructorId;
import fr.ynov.autoecole.student.StudentId;

import java.time.Duration;
import java.time.LocalDateTime;

public class Lesson {

    private static final Duration MINIMUM_NOTICE = Duration.ofHours(48);

    private final LessonId id;
    private final StudentId studentId;
    private final InstructorId instructorId;
    private final TimeSlot slot;
    private LessonStatus status = LessonStatus.BOOKED;
    private boolean due = true;

    public Lesson(LessonId id, StudentId studentId, InstructorId instructorId, TimeSlot slot) {
        this.id = id;
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.slot = slot;
    }

    public LessonId id() {
        return id;
    }

    public void give() {
        ensureBooked();
        status = LessonStatus.GIVEN;
    }

    public void cancel(LocalDateTime at) {
        ensureBooked();
        status = LessonStatus.CANCELLED;
        due = Duration.between(at, slot.start()).compareTo(MINIMUM_NOTICE) < 0;
    }

    public boolean isDue() {
        return due;
    }

    public TimeSlot slot() {
        return slot;
    }

    private void ensureBooked() {
        if (status != LessonStatus.BOOKED) {
            throw new IllegalStateException("A given or cancelled lesson cannot change");
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Lesson lesson && id.equals(lesson.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
