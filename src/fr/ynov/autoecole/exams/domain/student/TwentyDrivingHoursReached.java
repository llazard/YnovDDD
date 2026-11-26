package fr.ynov.autoecole.exams.domain.student;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

public record TwentyDrivingHoursReached(StudentId studentId) implements DomainEvent {
}
