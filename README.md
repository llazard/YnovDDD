# Auto-école : de la modélisation au code, J3

Le code d'exemple du cours de Domain-Driven Design, Mastère 1, Ynov. Il reprend
l'auto-école des cours : la même frise, les mêmes contextes, les mêmes mots.

Cette branche, `j3`, part de la branche `j2` (la frise et les contextes dans le code) et
ajoute les couches, les repositories, un cas d'usage et les tests du domaine.

## Les packages : un contexte, puis une couche, puis un aggregate

```text
src/fr/ynov/autoecole/
├── secretariat/
│   ├── domain/student/        Student, Hours, leurs événements, StudentRepository
│   └── infrastructure/        InMemoryStudentRepository
├── planning/
│   ├── domain/lesson/         Lesson, LessonId, TimeSlot, leurs événements, LessonRepository
│   ├── domain/instructor/     Instructor, InstructorId
│   ├── application/           LessonBooking, le cas d'usage « réserver une leçon »
│   └── infrastructure/        InMemoryLessonRepository
├── exams/
│   └── domain/student/        Student, DrivingHours, leurs événements
├── shared/                    StudentId, FullName, DomainEvent, AggregateRoot
├── contextmap/                PlanningToSecretariat, ExamsToPlanning
└── Main.java                  la couche interface, ici la console
test/fr/ynov/autoecole/        les tests, rangés comme les classes qu'ils vérifient
```

Un contexte n'a que les couches dont il a besoin : les examens n'ont ni cas d'usage ni
repository dans cet exemple.

| Couche | Ce qu'elle contient | Elle dépend de |
|---|---|---|
| interface | `Main`, la console | toutes les autres, pour les assembler |
| application | les cas d'usage, sans règle du métier | le domaine |
| domaine | entités, value objects, aggregates, événements, interfaces des repositories | rien |
| infrastructure | les repositories en mémoire | le domaine, dont elle implémente les interfaces |

## Les règles et leurs tests

| Règle | Où elle vit | Le test |
|---|---|---|
| Des heures ne sont jamais négatives | `Hours` | `hoursAreNeverNegative` |
| R1 : un élève ne peut pas réserver plus d'heures qu'il n'en a achetées | `Student.useHour`, appelé par `LessonBooking` | `aStudentCannotBookMoreHoursThanBought` |
| R2 : une leçon annulée moins de 48 heures avant reste due | `Lesson.cancel` | `aLessonCancelledLessThan48HoursBeforeIsDue` |
| Une leçon donnée ou annulée ne change plus | `Lesson.ensureBooked` | `aGivenLessonCannotBeCancelled` |
| Pas d'examen de conduite avant vingt heures | `exams.Student.presentToDrivingExam` | `aStudentIsNotPresentedToTheDrivingExamBeforeTwentyDrivingHours` |

## Lancer

Java 21 ou plus. Maven n'a pas besoin d'être installé : `mvnw` le télécharge.

```bash
./mvnw test
java -cp target/classes fr.ynov.autoecole.Main
```

Sous Windows, en PowerShell :

```powershell
.\mvnw.cmd test
java -cp target/classes fr.ynov.autoecole.Main
```
