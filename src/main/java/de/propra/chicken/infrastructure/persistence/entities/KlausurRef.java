package de.propra.chicken.infrastructure.persistence.entities;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("klausurbuchungen")
public class KlausurRef {
    @Column("klausur_id")
    Long klausur_id;

    public KlausurRef(Long klausur_id) {
        this.klausur_id = klausur_id;
    }

    public Long getKlausurId() {
        return klausur_id;
    }
}
