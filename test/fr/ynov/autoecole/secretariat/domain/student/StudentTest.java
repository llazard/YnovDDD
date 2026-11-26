package fr.ynov.autoecole.secretariat.domain.student;

import fr.ynov.autoecole.shared.FullName;
import fr.ynov.autoecole.shared.StudentId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentTest {

    private final Student student = new Student(StudentId.generate(), new FullName("Lea", "Martin"));

    @Test
    void aStudentCannotUseAnHourNotBought() {
        assertThrows(IllegalStateException.class, student::useHour);
    }

    @Test
    void usingAnHourLeavesOneHourLess() {
        student.buyHours(new Hours(2));

        student.useHour();

        assertEquals(new Hours(1), student.remainingHours());
    }
}
