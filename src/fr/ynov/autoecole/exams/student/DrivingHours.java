package fr.ynov.autoecole.exams.student;

/** For the exams, an hour is an hour of driving given, counted towards the twenty hours. */
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
