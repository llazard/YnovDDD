package fr.ynov.autoecole.planning.domain.lesson;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

public record LessonBooked(LessonId lessonId, StudentId studentId, TimeSlot slot) implements DomainEvent {
}
