package fr.ynov.autoecole;

import fr.ynov.autoecole.contextmap.ExamsToPlanning;
import fr.ynov.autoecole.contextmap.PlanningToSecretariat;
import fr.ynov.autoecole.planning.instructor.Instructor;
import fr.ynov.autoecole.planning.instructor.InstructorId;
import fr.ynov.autoecole.planning.lesson.Lesson;
import fr.ynov.autoecole.planning.lesson.LessonBooked;
import fr.ynov.autoecole.planning.lesson.LessonGiven;
import fr.ynov.autoecole.planning.lesson.LessonId;
import fr.ynov.autoecole.planning.lesson.TimeSlot;
import fr.ynov.autoecole.secretariat.student.Hours;
import fr.ynov.autoecole.secretariat.student.Student;
import fr.ynov.autoecole.shared.AggregateRoot;
import fr.ynov.autoecole.shared.DomainEvent;
import fr.ynov.autoecole.shared.FullName;
import fr.ynov.autoecole.shared.StudentId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Plays the timeline of the event storming, from left to right: who acts, which command,
 * which events. Who chains these calls in a real application? The use cases, on day 3.
 */
public class Main {

    private static Student secretariat;
    private static fr.ynov.autoecole.exams.student.Student exams;
    private static boolean quiet;

    public static void main(String[] args) {
        StudentId lea = StudentId.generate();
        Instructor paul = new Instructor(InstructorId.generate(), new FullName("Paul", "Durand"));

        title("Secretariat: the student registers, the payer buys hours");
        secretariat = new Student(lea, new FullName("Lea", "Martin"));
        tell("secretariat", "register a student", secretariat);
        exams = new fr.ynov.autoecole.exams.student.Student(lea);
        secretariat.buyHours(new Hours(22));
        tell("payer", "buy hours", secretariat);

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

        title("Planning, R1: a student cannot reserve more hours than bought");
        System.out.println("Remaining hours: " + secretariat.remainingHours().value());
        attempt("Reserve one more lesson", () -> reserveLesson(paul, day(23)));

        title("Hot spots, still questions for the business");
        System.out.println("- Is a lesson cancelled for illness, with a certificate, still due?");
        System.out.println("- Do the twenty hours count the lessons due but not given?");
        System.out.println("- Does a lesson cancelled in time give its hour back?");
    }

    /** The command "reserve a lesson": the planning reads the secretariat, then books. */
    private static Lesson reserveLesson(Instructor instructor, LocalDateTime start) {
        if (!PlanningToSecretariat.hasHoursLeft(secretariat)) {
            throw new IllegalStateException("R1, no hour left to reserve a lesson");
        }
        TimeSlot slot = new TimeSlot(start, Duration.ofHours(1));
        Lesson lesson = new Lesson(LessonId.generate(), secretariat.id(), instructor.id(), slot);
        tell("student", "reserve a lesson", lesson);
        return lesson;
    }

    /** One line of the timeline, then what the other contexts do with its events. */
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

    /** The arrows of the context map: an event of one context, a command in another. */
    private static void translate(DomainEvent event) {
        switch (event) {
            case LessonBooked booked -> {
                PlanningToSecretariat.translate(booked, secretariat);
                tell("  after LessonBooked", "use an hour", secretariat);
            }
            case LessonGiven given -> {
                ExamsToPlanning.translate(given, exams);
                tell("  after LessonGiven", "count a driving hour", exams);
            }
            default -> {
            }
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
