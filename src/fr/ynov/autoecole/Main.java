package fr.ynov.autoecole;

import fr.ynov.autoecole.instructor.Instructor;
import fr.ynov.autoecole.instructor.InstructorId;
import fr.ynov.autoecole.lesson.Lesson;
import fr.ynov.autoecole.lesson.LessonId;
import fr.ynov.autoecole.lesson.TimeSlot;
import fr.ynov.autoecole.shared.FullName;
import fr.ynov.autoecole.shared.Hours;
import fr.ynov.autoecole.student.Student;
import fr.ynov.autoecole.student.StudentId;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        Student student = new Student(StudentId.generate(), new FullName("Lea", "Martin"));
        Instructor instructor = new Instructor(InstructorId.generate(), new FullName("Paul", "Durand"));

        System.out.println("== Student: remaining hours are never negative");
        student.buyHours(new Hours(2));
        student.useHour();
        student.useHour();
        System.out.println("Remaining hours: " + student.remainingHours().value());
        attempt("Use a third hour", student::useHour);

        System.out.println();
        System.out.println("== Lesson, R2: a lesson cancelled less than 48 hours before is still due");
        LocalDateTime start = LocalDateTime.of(2026, 11, 20, 10, 0);
        Lesson early = book(student, instructor, start);
        early.cancel(start.minusHours(72));
        System.out.println("Cancelled 72 hours before, due: " + early.isDue());
        Lesson late = book(student, instructor, start);
        late.cancel(start.minusHours(24));
        System.out.println("Cancelled 24 hours before, due: " + late.isDue());
        attempt("Give a cancelled lesson", late::give);

        System.out.println();
        System.out.println("== Value objects validate themselves");
        attempt("Create -1 hours", () -> new Hours(-1));
        attempt("Create a time slot of 0 minutes", () -> new TimeSlot(start, Duration.ZERO));
        attempt("Create a name without a last name", () -> new FullName("Lea", ""));
    }

    private static Lesson book(Student student, Instructor instructor, LocalDateTime start) {
        TimeSlot slot = new TimeSlot(start, Duration.ofHours(1));
        return new Lesson(LessonId.generate(), student.id(), instructor.id(), slot);
    }

    private static void attempt(String action, Runnable rule) {
        try {
            rule.run();
            System.out.println(action + ": accepted");
        } catch (RuntimeException refused) {
            System.out.println(action + ": refused, " + refused.getMessage());
        }
    }
}
