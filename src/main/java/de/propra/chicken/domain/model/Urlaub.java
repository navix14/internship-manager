package de.propra.chicken.domain.model;

import java.time.LocalDate;

public class Urlaub extends Termin {
    public Urlaub(Zeitraum zeitraum, LocalDate tag) {
        super(zeitraum, tag);
    }
}
