package fr.ynov.autoecole.planning.domain.instructor;

import java.util.Objects;
import java.util.UUID;

public record InstructorId(UUID value) {

    public InstructorId {
        Objects.requireNonNull(value);
    }

    public static InstructorId generate() {
        return new InstructorId(UUID.randomUUID());
    }
}
