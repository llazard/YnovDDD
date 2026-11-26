package fr.ynov.autoecole.secretariat.domain.student;

public record Hours(int value) {

    public static final Hours ZERO = new Hours(0);
    public static final Hours ONE = new Hours(1);

    public Hours {
        if (value < 0) {
            throw new IllegalArgumentException("Hours cannot be negative");
        }
    }

    public Hours plus(Hours other) {
        return new Hours(value + other.value);
    }

    public Hours minus(Hours other) {
        return new Hours(value - other.value);
    }

    public boolean isLessThan(Hours other) {
        return value < other.value;
    }
}
