package fr.ynov.autoecole.planning.application;

import fr.ynov.autoecole.contextmap.PlanningToSecretariat;
import fr.ynov.autoecole.planning.domain.instructor.InstructorId;
import fr.ynov.autoecole.planning.domain.lesson.Lesson;
import fr.ynov.autoecole.planning.domain.lesson.LessonBooked;
import fr.ynov.autoecole.planning.domain.lesson.LessonId;
import fr.ynov.autoecole.planning.domain.lesson.LessonRepository;
import fr.ynov.autoecole.planning.domain.lesson.TimeSlot;
import fr.ynov.autoecole.secretariat.domain.student.Student;
import fr.ynov.autoecole.secretariat.domain.student.StudentRepository;
import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.StudentId;

/**
 * Le cas d'usage « réserver une leçon ». Il enchaîne les appels au domaine, sans règle à lui :
 * R1 reste dans l'élève, qui refuse de décompter une heure qu'il n'a plus.
 */
public class LessonBooking {

    private final StudentRepository students;
    private final LessonRepository lessons;

    public LessonBooking(StudentRepository students, LessonRepository lessons) {
        this.students = students;
        this.lessons = lessons;
    }

    public LessonId book(StudentId studentId, InstructorId instructorId, TimeSlot slot) {
        Student student = students.findById(studentId)                         // 1. charge l'élève
                .orElseThrow(() -> new IllegalArgumentException("Unknown student"));
        Lesson lesson = new Lesson(LessonId.generate(), studentId, instructorId, slot);  // 2. crée la leçon
        for (DomainEvent event : lesson.events()) {                            // 3. lit ses événements
            if (event instanceof LessonBooked booked) {
                PlanningToSecretariat.translate(booked, student);              // 4. passe « Leçon réservée »
            }
        }
        students.save(student);                                                // 5. enregistre les deux,
        lessons.save(lesson);                                                  //    si rien n'a refusé
        return lesson.id();
    }
}
