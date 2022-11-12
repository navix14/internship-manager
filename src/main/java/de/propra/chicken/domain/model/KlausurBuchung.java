package de.propra.chicken.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class KlausurBuchung extends Termin {
    private final Long klausurId;
    private final String fachName;
    private final boolean online;
    private final Zeitraum gebuchterZeitraum;

    LocalTime start = LocalTime.parse("08:30:00");
    LocalTime ende = LocalTime.parse("12:30:00");

    public KlausurBuchung(Long klausurId, String fachName, Zeitraum zeitraum, LocalDate tag, boolean online) {
        super(zeitraum, tag);
        this.klausurId = klausurId;
        this.fachName = fachName;
        this.online = online;

        this.gebuchterZeitraum = berechneBuchungsZeitraumFuerKlausur();
        passeGrenzenAnPraktikumsZeitraumAn();
    }

    private void passeGrenzenAnPraktikumsZeitraumAn() {
        if (gebuchterZeitraum.getStart().isBefore(start)) gebuchterZeitraum.setStart(start);
        if (gebuchterZeitraum.getEnde().isAfter(ende)) gebuchterZeitraum.setEnde(ende);
    }

    public Zeitraum berechneBuchungsZeitraumFuerKlausur() {
        if (online) {
            return new Zeitraum(getZeitraum().getStart().minusMinutes(30), getZeitraum().getEnde());
        } else {
            return new Zeitraum(getZeitraum().getStart().minusMinutes(120), getZeitraum().getEnde().plusMinutes(120));
        }
    }

    public String getFachName() { //benoetigt Thymeleaf
        return fachName;
    }

    public Zeitraum getGebuchterZeitraum() {
        return new Zeitraum(gebuchterZeitraum.getStart(), gebuchterZeitraum.getEnde());
    }

    public Long getKlausurId() {
        return klausurId;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KlausurBuchung that = (KlausurBuchung) o;
        return klausurId.equals(that.klausurId) && Objects.equals(fachName, that.fachName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(klausurId, fachName);
    }

    @Override
    public String toString() {
        return "KlausurBuchung{" +
                "klausurId=" + klausurId +
                ", fachName='" + fachName + '\'' +
                ", tag=" + getTag() +
                ", zeitraum=" + getZeitraum() +
                ", online=" + online +
                ", gebuchterZeitraum=" + getZeitraum() +
                ", start='" + start + '\'' +
                ", ende='" + ende + '\'' +
                '}';
    }
}
