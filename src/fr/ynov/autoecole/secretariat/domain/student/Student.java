package fr.ynov.autoecole.secretariat.domain.student;

import fr.ynov.autoecole.shared.AggregateRoot;
import fr.ynov.autoecole.shared.FullName;
import fr.ynov.autoecole.shared.StudentId;

/** L'élève, vu par le secrétariat : un nom, et les heures achetées. */
public class Student extends AggregateRoot {

    private final StudentId id;
    private FullName name;
    private Hours remainingHours = Hours.ZERO;

    public Student(StudentId id, FullName name) {
        this.id = id;
        this.name = name;
        record(new StudentRegistered(id));
    }

    public StudentId id() {
        return id;
    }

    public Hours remainingHours() {
        return remainingHours;
    }

    public void buyHours(Hours hours) {
        remainingHours = remainingHours.plus(hours);
        record(new HoursPurchased(id, hours));
    }

    public void useHour() {
        if (remainingHours.isLessThan(Hours.ONE)) {
            throw new IllegalStateException("No remaining hours");
        }
        remainingHours = remainingHours.minus(Hours.ONE);
        record(new HourUsed(id, remainingHours));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Student student && id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
