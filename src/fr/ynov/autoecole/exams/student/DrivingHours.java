package fr.ynov.autoecole.exams.student;

/** Pour les examens, une heure est une heure de conduite donnée, qui compte dans les vingt heures. */
public record DrivingHours(int value) {

    public static final DrivingHours ZERO = new DrivingHours(0);
    public static final DrivingHours TWENTY = new DrivingHours(20);

    public DrivingHours {
        if (value < 0) {
            throw new IllegalArgumentException("Driving hours cannot be negative");
        }
    }

    public DrivingHours plusOne() {
        return new DrivingHours(value + 1);
    }

    public boolean isLessThan(DrivingHours other) {
        return value < other.value;
    }
}
