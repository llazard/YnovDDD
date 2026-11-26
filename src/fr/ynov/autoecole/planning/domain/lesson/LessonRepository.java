package fr.ynov.autoecole.planning.domain.lesson;

import java.util.Optional;

/** Un repository par aggregate : celui de la leçon. Le créneau n'en a pas, il est dans la leçon. */
public interface LessonRepository {

    Optional<Lesson> findById(LessonId id);

    void save(Lesson lesson);
}
