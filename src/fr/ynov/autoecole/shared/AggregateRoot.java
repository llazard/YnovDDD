package fr.ynov.autoecole.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * The root of an aggregate records the events its commands produce.
 * Whoever called the command pulls them, and passes them to the other contexts.
 */
public abstract class AggregateRoot {

    private final List<DomainEvent> events = new ArrayList<>();

    protected void record(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> pulled = List.copyOf(events);
        events.clear();
        return pulled;
    }
}
