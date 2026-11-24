package fr.ynov.autoecole.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * La racine d'un aggregate enregistre les événements que ses commandes produisent.
 * Celui qui a appelé la commande les récupère, et les passe aux autres contextes.
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
