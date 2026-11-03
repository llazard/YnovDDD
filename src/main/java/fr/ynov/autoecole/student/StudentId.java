package fr.ynov.autoecole.student;

import java.util.Objects;
import java.util.UUID;

public record StudentId(UUID value) {

    public StudentId {
        Objects.requireNonNull(value);
    }

    public static StudentId generate() {
        return new StudentId(UUID.randomUUID());
    }
}
