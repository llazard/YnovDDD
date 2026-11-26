package fr.ynov.autoecole.secretariat.domain.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HoursTest {

    @Test
    void hoursAreNeverNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Hours(-1));
    }
}
