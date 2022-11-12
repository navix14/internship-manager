package de.propra.chicken.infrastructure.persistence.entities;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("klausur")
public record KlausurEntity(@Id Long id, Long lsfId, String name, LocalDate datum, LocalTime start, LocalTime ende,
                            boolean online) {
    @PersistenceConstructor
    public KlausurEntity(Long id, Long lsfId, String name, LocalDate datum, LocalTime start, LocalTime ende, boolean online) {
        this.id = id;
        this.lsfId = lsfId;
        this.name = name;
        this.datum = datum;
        this.start = start;
        this.ende = ende;
        this.online = online;
    }

    public KlausurEntity(Klausur k) {
        this(k.getKlausurId(), k.getLsfId(), k.getFachName(), k.getTag(), k.getZeitraum().getStart(), k.getZeitraum().getEnde(), k.isOnline());
    }
}
