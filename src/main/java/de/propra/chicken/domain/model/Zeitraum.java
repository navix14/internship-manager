package de.propra.chicken.domain.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Zeitraum {
    private LocalTime start;
    private LocalTime ende;

    public Zeitraum(LocalTime start, LocalTime ende) {
        this.start = start;
        this.ende = ende;
    }

    public Zeitraum(String start, String ende) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        this.start = LocalTime.parse(start, formatter);
        this.ende = LocalTime.parse(ende, formatter);
        if(!this.ende.isAfter(this.start)) {
            throw new IllegalArgumentException("Start muss vor Ende liegen");
        }
    }

    public long getDauerInMinuten() {
        return ChronoUnit.MINUTES.between(start, ende);
    }

    public boolean pruefeUeberlappung(Zeitraum z1) {
        return  this.start.compareTo(z1.start)==0 || this.ende.compareTo(z1.ende)==0 || // Anfang oder Ende stimmen überein
                this.ende.isAfter(z1.start) && this.ende.isBefore(z1.ende) ||           // z1 ragt nach rechts raus
                this.start.isAfter(z1.start) && this.start.isBefore(z1.ende);           // z1 ragt nach links raus
    }

    public boolean pruefeNachbar(Zeitraum z1) {
        // liegen genau nebeneinander (ohne Schnitt)
        return (this.ende.compareTo(z1.start) == 0 || this.start.compareTo(z1.ende) == 0);
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnde() {
        return ende;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public void setEnde(LocalTime ende) {
        this.ende = ende;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zeitraum zeitraum = (Zeitraum) o;
        return Objects.equals(start, zeitraum.start) && Objects.equals(ende, zeitraum.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, ende);
    }
}
