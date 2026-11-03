package fr.ynov.autoecole.shared;

public record FullName(String firstName, String lastName) {

    public FullName {
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("A full name needs a first name and a last name");
        }
    }
}
