package fr.ynov.autoecole.planning.domain.lesson;

import fr.ynov.autoecole.planning.domain.instructor.InstructorId;
import fr.ynov.autoecole.shared.AggregateRoot;
import fr.ynov.autoecole.shared.StudentId;

import java.time.Duration;
import java.time.LocalDateTime;

/** La leçon, vue par le planning. L'élève n'y est qu'une identité. */
public class Lesson extends AggregateRoot {

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
        record(new LessonBooked(id, studentId, slot));
    }

    public LessonId id() {
        return id;
    }

    public void give() {
        ensureBooked();
        status = LessonStatus.GIVEN;
        record(new LessonGiven(id, studentId));
    }

    public void cancel(LocalDateTime at) {
        ensureBooked();
        status = LessonStatus.CANCELLED;
        due = Duration.between(at, slot.start()).compareTo(MINIMUM_NOTICE) < 0;
        record(new LessonCancelled(id, studentId, at));
        // Point chaud, encore une question pour le métier : une leçon annulée pour maladie,
        // avec un certificat médical, reste-t-elle due ? En attendant, la règle des 48 heures
        // s'applique.
        if (due) {
            record(new LessonDue(id, studentId));
        }
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
