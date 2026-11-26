package fr.ynov.autoecole.exams.domain.student;

import fr.ynov.autoecole.shared.AggregateRoot;
import fr.ynov.autoecole.shared.StudentId;

/**
 * L'élève, vu par les examens : l'examen du code, les heures de conduite données,
 * l'examen de conduite. La même personne qu'au secrétariat, la même identité, un autre
 * modèle.
 */
public class Student extends AggregateRoot {

    private final StudentId id;
    private boolean theoryExamPassed;
    private DrivingHours drivingHours = DrivingHours.ZERO;
    private boolean presented;

    public Student(StudentId id) {
        this.id = id;
    }

    public StudentId id() {
        return id;
    }

    public void passTheoryExam() {
        theoryExamPassed = true;
        record(new TheoryExamPassed(id));
    }

    public void countDrivingHour() {
        drivingHours = drivingHours.plusOne();
        if (drivingHours.equals(DrivingHours.TWENTY)) {
            record(new TwentyDrivingHoursReached(id));
        }
    }

    public void presentToDrivingExam() {
        if (!theoryExamPassed) {
            throw new IllegalStateException("The theory exam is not passed");
        }
        if (drivingHours.isLessThan(DrivingHours.TWENTY)) {
            throw new IllegalStateException("Twenty driving hours are not reached");
        }
        presented = true;
        record(new PresentedToDrivingExam(id));
    }

    public void obtainLicence() {
        if (!presented) {
            throw new IllegalStateException("The student was not presented to the driving exam");
        }
        record(new LicenceObtained(id));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Student student && id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
