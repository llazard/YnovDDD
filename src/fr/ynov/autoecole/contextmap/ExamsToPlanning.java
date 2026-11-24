package fr.ynov.autoecole.contextmap;

import fr.ynov.autoecole.exams.student.Student;
import fr.ynov.autoecole.planning.lesson.LessonGiven;

/**
 * An arrow of the context map: the exams depend on the planning.
 * They count the driving hours given; a lesson becomes one driving hour.
 */
public final class ExamsToPlanning {

    private ExamsToPlanning() {
    }

    // Hot spot, still a question for the business: do the twenty hours count the lessons
    // that are due but were not given? Until it answers, only a lesson given counts.
    public static void translate(LessonGiven given, Student student) {
        if (!given.studentId().equals(student.id())) {
            throw new IllegalArgumentException("This lesson was given to another student");
        }
        student.countDrivingHour();
    }
}
