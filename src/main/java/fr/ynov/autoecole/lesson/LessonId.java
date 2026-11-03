package fr.ynov.autoecole.lesson;

import java.util.Objects;
import java.util.UUID;

public record LessonId(UUID value) {

    public LessonId {
        Objects.requireNonNull(value);
    }

    public static LessonId generate() {
        return new LessonId(UUID.randomUUID());
    }
}
