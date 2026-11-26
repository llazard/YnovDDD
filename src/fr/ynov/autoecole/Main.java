package fr.ynov.autoecole;

import fr.ynov.autoecole.contextmap.ExamsToPlanning;
import fr.ynov.autoecole.planning.application.LessonBooking;
import fr.ynov.autoecole.planning.domain.instructor.Instructor;
import fr.ynov.autoecole.planning.domain.instructor.InstructorId;
import fr.ynov.autoecole.planning.domain.lesson.Lesson;
import fr.ynov.autoecole.planning.domain.lesson.LessonGiven;
import fr.ynov.autoecole.planning.domain.lesson.LessonId;
import fr.ynov.autoecole.planning.domain.lesson.LessonRepository;
import fr.ynov.autoecole.planning.domain.lesson.TimeSlot;
import fr.ynov.autoecole.planning.infrastructure.InMemoryLessonRepository;
import fr.ynov.autoecole.secretariat.domain.student.Hours;
import fr.ynov.autoecole.secretariat.domain.student.Student;
import fr.ynov.autoecole.secretariat.domain.student.StudentRepository;
import fr.ynov.autoecole.secretariat.infrastructure.InMemoryStudentRepository;
import fr.ynov.autoecole.shared.AggregateRoot;
import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.FullName;
import fr.ynov.autoecole.shared.StudentId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * La couche interface, ici la console. Elle assemble les couches, puis déroule la frise de
 * gauche à droite : qui agit, quelle commande, quels événements. Réserver une leçon passe
 * par le cas d'usage LessonBooking ; les autres commandes appellent encore la racine.
 */
public class Main {

    private static final StudentRepository students = new InMemoryStudentRepository();
    private static final LessonRepository lessons = new InMemoryLessonRepository();
    private static final LessonBooking booking = new LessonBooking(students, lessons);

    private static Student secretariat;
    private static fr.ynov.autoecole.exams.domain.student.Student exams;
    private static boolean quiet;

    public static void main(String[] args) {
        StudentId lea = StudentId.generate();
        Instructor paul = new Instructor(InstructorId.generate(), new FullName("Paul", "Durand"));

        title("Secretariat: the student registers, the payer buys hours");
        secretariat = new Student(lea, new FullName("Lea", "Martin"));
        tell("secretariat", "register a student", secretariat);
        exams = new fr.ynov.autoecole.exams.domain.student.Student(lea);
        secretariat.buyHours(new Hours(22));
        tell("payer", "buy hours", secretariat);
        students.save(secretariat);

        title("Exams: the theory exam");
        exams.passTheoryExam();
        tell("exam result", "", exams);

        title("Planning: a lesson booked, then given");
        Lesson first = reserveLesson(paul, day(1));
        first.give();
        tell("instructor", "give a lesson", first);

        title("Planning, R2: a lesson cancelled less than 48 hours before is still due");
        Lesson early = reserveLesson(paul, day(2));
        early.cancel(day(2).minusHours(72));
        tell("student", "cancel a lesson, 72 hours before", early);
        Lesson late = reserveLesson(paul, day(3));
        late.cancel(day(3).minusHours(24));
        tell("student", "cancel a lesson, 24 hours before", late);

        title("Exams: the rules refuse what the business forbids");
        attempt("Present Lea to the driving exam after 1 driving hour", exams::presentToDrivingExam);

        title("Planning: 18 more lessons, without printing them, then the twentieth");
        quiet = true;
        for (int day = 4; day < 22; day++) {
            Lesson lesson = reserveLesson(paul, day(day));
            lesson.give();
            tell("instructor", "give a lesson", lesson);
        }
        quiet = false;
        Lesson twentieth = reserveLesson(paul, day(22));
        twentieth.give();
        tell("instructor", "give a lesson", twentieth);

        title("Exams: the student is presented, then gets the licence");
        exams.presentToDrivingExam();
        tell("secretariat", "present a student to the exam", exams);
        exams.obtainLicence();
        tell("exam result", "", exams);

        title("Planning, R1: a student cannot book more hours than bought");
        System.out.println("Remaining hours: " + secretariat.remainingHours().value());
        attempt("Reserve one more lesson", () -> reserveLesson(paul, day(23)));

        title("Hot spots, still questions for the business");
        System.out.println("- Is a lesson cancelled for illness, with a certificate, still due?");
        System.out.println("- Do the twenty hours count the lessons due but not given?");
        System.out.println("- Does a lesson cancelled in time give its hour back?");
    }

    /** La commande « réserver une leçon » : le cas d'usage enchaîne, l'élève refuse s'il n'a plus d'heure. */
    private static Lesson reserveLesson(Instructor instructor, LocalDateTime start) {
        TimeSlot slot = new TimeSlot(start, Duration.ofHours(1));
        LessonId id = booking.book(secretariat.id(), instructor.id(), slot);
        Lesson lesson = lessons.findById(id).orElseThrow();
        tell("student", "reserve a lesson", lesson);
        tell("  by LessonBooking", "use an hour", secretariat);
        return lesson;
    }

    /** Une ligne de la frise, puis ce que les autres contextes font de ses événements. */
    private static void tell(String actor, String command, AggregateRoot root) {
        List<DomainEvent> events = root.pullEvents();
        if (!quiet) {
            String names = events.stream()
                    .map(event -> event.getClass().getSimpleName())
                    .collect(Collectors.joining(", "));
            System.out.printf("%-22s %-34s %s%n", actor, command, names.isEmpty() ? "-" : names);
        }
        events.forEach(Main::translate);
    }

    /** Les flèches de la context map qui n'ont pas encore leur cas d'usage. */
    private static void translate(DomainEvent event) {
        if (event instanceof LessonGiven given) {
            ExamsToPlanning.translate(given, exams);
            tell("  after LessonGiven", "count a driving hour", exams);
        }
    }

    private static LocalDateTime day(int number) {
        return LocalDateTime.of(2026, 12, 1, 10, 0).plusDays(number);
    }

    private static void title(String text) {
        System.out.println();
        System.out.println("== " + text);
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
