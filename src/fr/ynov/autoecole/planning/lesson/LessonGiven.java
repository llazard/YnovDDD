package fr.ynov.autoecole.planning.lesson;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

public record LessonGiven(LessonId lessonId, StudentId studentId) implements DomainEvent {
}
