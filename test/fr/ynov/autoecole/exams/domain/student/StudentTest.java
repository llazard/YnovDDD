package fr.ynov.autoecole.exams.domain.student;

import fr.ynov.autoecole.shared.StudentId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentTest {

    @Test
    void aStudentIsNotPresentedToTheDrivingExamBeforeTwentyDrivingHours() {
        Student student = new Student(StudentId.generate());
        student.passTheoryExam();
        for (int hour = 0; hour < 19; hour++) {
            student.countDrivingHour();
        }

        assertThrows(IllegalStateException.class, student::presentToDrivingExam);
    }
}
