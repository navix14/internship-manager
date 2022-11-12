# Internship management system for students
Simple Spring Boot App to showcase possible implementation of Domain Driven Design concepts.

This was done as part of a group project at university.

### Anleitung:
* Grundsätzlich ist es möglich, das Datum über die Umgebungsvariablen `START_DATUM` und `END_DATUM` zu konfigurieren, andernfalls wird ein valider Default-Zeitraum verwendet.

1. Setzen der `CLIENT_ID` und `CLIENT_SECRET` Umgebungsvariablen
2. Ausführen der Datenbank mithilfe von `docker-compose up`
3. Starten der Spring Boot-Anwendung: `gradle bootRun`