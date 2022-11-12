package de.propra.chicken.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class Termin {
    private Zeitraum zeitraum;
    private LocalDate tag;

    public Termin(Zeitraum zeitraum, LocalDate tag) {
        this.zeitraum = new Zeitraum(zeitraum.getStart(), zeitraum.getEnde());
        this.tag = tag;
    }

    public Zeitraum getZeitraum() {
        return new Zeitraum(zeitraum.getStart(), zeitraum.getEnde());
    }

    public LocalDate getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Termin that = (Termin) o;
        return Objects.equals(zeitraum, that.zeitraum) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zeitraum, tag);
    }
}
