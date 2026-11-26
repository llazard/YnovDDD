package fr.ynov.autoecole.secretariat.infrastructure;

import fr.ynov.autoecole.secretariat.domain.student.Student;
import fr.ynov.autoecole.secretariat.domain.student.StudentRepository;
import fr.ynov.autoecole.shared.StudentId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Les élèves, gardés en mémoire. Une base de données prendrait sa place sans toucher au domaine. */
public class InMemoryStudentRepository implements StudentRepository {

    private final Map<StudentId, Student> students = new HashMap<>();

    @Override
    public Optional<Student> findById(StudentId id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public void save(Student student) {
        students.put(student.id(), student);
    }
}
