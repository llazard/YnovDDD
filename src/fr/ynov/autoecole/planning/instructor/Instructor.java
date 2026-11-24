package fr.ynov.autoecole.planning.instructor;

import fr.ynov.autoecole.shared.FullName;

public class Instructor {

    private final InstructorId id;
    private FullName name;

    public Instructor(InstructorId id, FullName name) {
        this.id = id;
        this.name = name;
    }

    public InstructorId id() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Instructor instructor && id.equals(instructor.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
