package de.propra.chicken.services.DTOs;

import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Klausur;

import java.time.LocalDate;

public record KlausurDTO (Long klausurId, String fachName, LocalDate tag, Zeitraum zeitraum){
    public KlausurDTO(Long klausurId, String fachName, LocalDate tag, Zeitraum zeitraum) {
        this.klausurId = klausurId;
        this.fachName = fachName;
        this.tag = tag;
        this.zeitraum = zeitraum;
    }

    public KlausurDTO(Klausur klausur) {
        this(klausur.getKlausurId(), klausur.getFachName(), klausur.getTag(), klausur.getZeitraum());
    }
}
