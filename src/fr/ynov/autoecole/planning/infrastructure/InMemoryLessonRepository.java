package fr.ynov.autoecole.planning.infrastructure;

import fr.ynov.autoecole.planning.domain.lesson.Lesson;
import fr.ynov.autoecole.planning.domain.lesson.LessonId;
import fr.ynov.autoecole.planning.domain.lesson.LessonRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Les leçons, gardées en mémoire. */
public class InMemoryLessonRepository implements LessonRepository {

    private final Map<LessonId, Lesson> lessons = new HashMap<>();

    @Override
    public Optional<Lesson> findById(LessonId id) {
        return Optional.ofNullable(lessons.get(id));
    }

    @Override
    public void save(Lesson lesson) {
        lessons.put(lesson.id(), lesson);
    }
}
