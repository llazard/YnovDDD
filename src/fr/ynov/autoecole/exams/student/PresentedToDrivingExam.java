package fr.ynov.autoecole.exams.student;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

public record PresentedToDrivingExam(StudentId studentId) implements DomainEvent {
}
