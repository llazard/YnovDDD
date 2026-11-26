package fr.ynov.autoecole.secretariat.domain.student;

import fr.ynov.autoecole.shared.StudentId;

import java.util.Optional;

/**
 * Ce dont le domaine a besoin pour retrouver et enregistrer un élève, l'aggregate entier.
 * Le domaine dit quoi ; l'infrastructure dit comment.
 */
public interface StudentRepository {

    Optional<Student> findById(StudentId id);

    void save(Student student);
}
