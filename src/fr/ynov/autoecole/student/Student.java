package fr.ynov.autoecole.student;

import fr.ynov.autoecole.shared.FullName;
import fr.ynov.autoecole.shared.Hours;

public class Student {

    private final StudentId id;
    private FullName name;
    private Hours remainingHours = Hours.ZERO;

    public Student(StudentId id, FullName name) {
        this.id = id;
        this.name = name;
    }

    public StudentId id() {
        return id;
    }

    public Hours remainingHours() {
        return remainingHours;
    }

    public void buyHours(Hours hours) {
        remainingHours = remainingHours.plus(hours);
    }

    public void useHour() {
        if (remainingHours.isLessThan(Hours.ONE)) {
            throw new IllegalStateException("No remaining hours");
        }
        remainingHours = remainingHours.minus(Hours.ONE);
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
