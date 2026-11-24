package fr.ynov.autoecole.contextmap;

import fr.ynov.autoecole.planning.lesson.LessonBooked;
import fr.ynov.autoecole.secretariat.student.Hours;
import fr.ynov.autoecole.secretariat.student.Student;

/**
 * An arrow of the context map: the planning depends on the secretariat.
 * Between two contexts, we translate, explicitly.
 */
public final class PlanningToSecretariat {

    private PlanningToSecretariat() {
    }

    /** "Reads the student's remaining hours": R1, no lesson without an hour bought. */
    public static boolean hasHoursLeft(Student student) {
        return !student.remainingHours().isLessThan(Hours.ONE);
    }

    /** On the timeline, "Lesson booked" is followed by "Hour used". */
    public static void translate(LessonBooked booked, Student student) {
        if (!booked.studentId().equals(student.id())) {
            throw new IllegalArgumentException("This lesson was booked by another student");
        }
        student.useHour();
    }

    // Hot spot, found while writing this class: the hour is used when the lesson is
    // booked. Does a lesson cancelled more than 48 hours before give its hour back?
    // A question for the business: nothing is translated until it answers.
}
