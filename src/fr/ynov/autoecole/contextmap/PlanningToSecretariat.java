package fr.ynov.autoecole.contextmap;

import fr.ynov.autoecole.planning.lesson.LessonBooked;
import fr.ynov.autoecole.secretariat.student.Hours;
import fr.ynov.autoecole.secretariat.student.Student;

/**
 * Une flèche de la context map : le planning dépend du secrétariat.
 * Entre deux contextes, on traduit, explicitement.
 */
public final class PlanningToSecretariat {

    private PlanningToSecretariat() {
    }

    /** « Lit les heures restantes de l'élève » : R1, pas de leçon sans heure achetée. */
    public static boolean hasHoursLeft(Student student) {
        return !student.remainingHours().isLessThan(Hours.ONE);
    }

    /** Sur la frise, « Leçon réservée » est suivie de « Heure décomptée ». */
    public static void translate(LessonBooked booked, Student student) {
        if (!booked.studentId().equals(student.id())) {
            throw new IllegalArgumentException("This lesson was booked by another student");
        }
        student.useHour();
    }

    // Point chaud, apparu en écrivant cette classe : l'heure est décomptée quand la leçon
    // est réservée. Une leçon annulée plus de 48 heures avant rend-elle son heure ?
    // Une question pour le métier : rien n'est traduit tant qu'il n'a pas répondu.
}
