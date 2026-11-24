package fr.ynov.autoecole.secretariat.student;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

public record HourUsed(StudentId studentId, Hours remainingHours) implements DomainEvent {
}
