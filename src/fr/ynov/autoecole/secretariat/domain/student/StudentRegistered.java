package fr.ynov.autoecole.secretariat.domain.student;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

public record StudentRegistered(StudentId studentId) implements DomainEvent {
}
