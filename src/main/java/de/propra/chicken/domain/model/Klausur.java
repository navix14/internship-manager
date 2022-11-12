package de.propra.chicken.domain.model;

import java.time.LocalDate;
import java.util.Objects;

//@Aggregate
public class Klausur {

    private final Long klausurId;
    private Long lsfId;
    private String fachName;
    private LocalDate tag;
    private Zeitraum zeitraum;
    private boolean online;

    public Long getLsfId() {
        return lsfId;
    }

    public String getFachName() {
        return fachName;
    }

    public LocalDate getTag() {
        return tag;
    }

    public Zeitraum getZeitraum() {
        return new Zeitraum(zeitraum.getStart(), zeitraum.getEnde());
    }

    public boolean isOnline() {
        return online;
    }

    public Klausur(Long klausurId, Long lsfId, String fachName, LocalDate klausurTermin, Zeitraum klausurZeitraum, boolean online) {
        this.lsfId = lsfId;
        this.fachName = fachName;
        this.tag = klausurTermin;
        this.zeitraum = new Zeitraum(klausurZeitraum.getStart(), klausurZeitraum.getEnde());
        this.online = online;
        this.klausurId = klausurId;
    }

    public Long getKlausurId() {
        return klausurId;
    }

    public KlausurBuchung toKlausurBuchung() {
        return new KlausurBuchung(this.klausurId, this.fachName, this.zeitraum, this.tag, this.online);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Klausur klausur = (Klausur) o;
        return online == klausur.online && Objects.equals(lsfId, klausur.lsfId)
                && Objects.equals(fachName, klausur.fachName)
                && Objects.equals(tag, klausur.tag) && Objects.equals(zeitraum, klausur.zeitraum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fachName, tag, zeitraum, online);
    }
}
