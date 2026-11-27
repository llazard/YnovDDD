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

        title("Secrétariat : l'élève s'inscrit, le payeur achète des heures");
        secretariat = new Student(lea, new FullName("Léa", "Martin"));
        tell("secrétariat", "inscrire un élève", secretariat);
        exams = new fr.ynov.autoecole.exams.domain.student.Student(lea);
        secretariat.buyHours(new Hours(22));
        tell("payeur", "acheter des heures", secretariat);
        students.save(secretariat);

        title("Examens : l'examen du code");
        exams.passTheoryExam();
        tell("résultat d'examen", "", exams);

        title("Planning : une leçon réservée, puis donnée");
        Lesson first = reserveLesson(paul, day(1));
        first.give();
        tell("moniteur", "donner une leçon", first);

        title("Planning, R2 : une leçon annulée moins de 48 heures avant reste due");
        Lesson early = reserveLesson(paul, day(2));
        early.cancel(day(2).minusHours(72));
        tell("élève", "annuler une leçon, 72 heures avant", early);
        Lesson late = reserveLesson(paul, day(3));
        late.cancel(day(3).minusHours(24));
        tell("élève", "annuler une leçon, 24 heures avant", late);

        title("Examens : les règles refusent ce que le métier interdit");
        attempt("Présenter Léa à l'examen de conduite après 1 heure de conduite", exams::presentToDrivingExam);

        title("Planning : 18 leçons de plus, sans les afficher, puis la vingtième");
        quiet = true;
        for (int day = 4; day < 22; day++) {
            Lesson lesson = reserveLesson(paul, day(day));
            lesson.give();
            tell("moniteur", "donner une leçon", lesson);
        }
        quiet = false;
        Lesson twentieth = reserveLesson(paul, day(22));
        twentieth.give();
        tell("moniteur", "donner une leçon", twentieth);

        title("Examens : l'élève est présenté, puis obtient le permis");
        exams.presentToDrivingExam();
        tell("secrétariat", "présenter un élève à l'examen", exams);
        exams.obtainLicence();
        tell("résultat d'examen", "", exams);

        title("Planning, R1 : un élève ne réserve pas plus d'heures qu'il n'en a acheté");
        System.out.println("Heures restantes : " + secretariat.remainingHours().value());
        attempt("Réserver une leçon de plus", () -> reserveLesson(paul, day(23)));

        title("Points chauds, encore des questions pour le métier");
        System.out.println("- Une leçon annulée pour maladie, avec un certificat, reste-t-elle due ?");
        System.out.println("- Les vingt heures comptent-elles les leçons dues mais non données ?");
        System.out.println("- Une leçon annulée à temps rend-elle son heure ?");
    }

    /** La commande « réserver une leçon » : le cas d'usage enchaîne, l'élève refuse s'il n'a plus d'heure. */
    private static Lesson reserveLesson(Instructor instructor, LocalDateTime start) {
        TimeSlot slot = new TimeSlot(start, Duration.ofHours(1));
        LessonId id = booking.book(secretariat.id(), instructor.id(), slot);
        Lesson lesson = lessons.findById(id).orElseThrow();
        tell("élève", "réserver une leçon", lesson);
        tell("  par LessonBooking", "utiliser une heure", secretariat);
        return lesson;
    }

    /** Une ligne de la frise, puis ce que les autres contextes font de ses événements. */
    private static void tell(String actor, String command, AggregateRoot root) {
        List<DomainEvent> events = root.pullEvents();
        if (!quiet) {
            String names = events.stream()
                    .map(event -> event.getClass().getSimpleName())
                    .collect(Collectors.joining(", "));
            System.out.printf("%-22s %-36s %s%n", actor, command, names.isEmpty() ? "-" : names);
        }
        events.forEach(Main::translate);
    }

    /** Les flèches de la context map qui n'ont pas encore leur cas d'usage. */
    private static void translate(DomainEvent event) {
        if (event instanceof LessonGiven given) {
            ExamsToPlanning.translate(given, exams);
            tell("  après LessonGiven", "compter une heure de conduite", exams);
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
            System.out.println(action + " : accepté");
        } catch (RuntimeException refused) {
            System.out.println(action + " : refusé, " + refused.getMessage());
        }
    }
}
