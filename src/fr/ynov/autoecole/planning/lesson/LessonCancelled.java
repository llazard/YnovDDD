package fr.ynov.autoecole.planning.lesson;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

import java.time.LocalDateTime;

public record LessonCancelled(LessonId lessonId, StudentId studentId, LocalDateTime at) implements DomainEvent {
}
