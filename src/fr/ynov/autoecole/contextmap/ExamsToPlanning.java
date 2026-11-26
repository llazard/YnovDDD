package fr.ynov.autoecole.contextmap;

import fr.ynov.autoecole.exams.domain.student.Student;
import fr.ynov.autoecole.planning.domain.lesson.LessonGiven;

/**
 * Une flèche de la context map : les examens dépendent du planning.
 * Ils comptent les heures de conduite données ; une leçon devient une heure de conduite.
 */
public final class ExamsToPlanning {

    private ExamsToPlanning() {
    }

    // Point chaud, encore une question pour le métier : les vingt heures comptent-elles les
    // leçons dues mais non faites ? En attendant sa réponse, seule une leçon donnée compte.
    public static void translate(LessonGiven given, Student student) {
        if (!given.studentId().equals(student.id())) {
            throw new IllegalArgumentException("This lesson was given to another student");
        }
        student.countDrivingHour();
    }
}
