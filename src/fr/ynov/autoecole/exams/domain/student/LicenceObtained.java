package fr.ynov.autoecole.exams.domain.student;

import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

public record LicenceObtained(StudentId studentId) implements DomainEvent {
}
