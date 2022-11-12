package de.propra.chicken.ControllerTests;

import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurBuchung;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.services.DTOs.KlausurDTO;

import java.time.LocalDate;
import java.util.List;

public class ModelTemplates {
    public static final Urlaub normalerUrlaub1 = new Urlaub(new Zeitraum("09:30", "10:30"), LocalDate.now().plusDays(3));
    public static final Urlaub normalerUrlaub2 = new Urlaub(new Zeitraum("08:30", "09:00"), LocalDate.now().plusDays(2));
    public static final Urlaub normalerUrlaub3 = new Urlaub(new Zeitraum("10:00", "12:00"), LocalDate.now().plusDays(4));
    public static final List<Urlaub> urlaube = List.of(normalerUrlaub1, normalerUrlaub2, normalerUrlaub3);

    public static final KlausurBuchung normaleKlausur1 = new KlausurBuchung(1L, "Klausur 1", new Zeitraum("08:30", "10:00"), LocalDate.now().plusDays(1), true);
    public static final KlausurBuchung normaleKlausur2 = new KlausurBuchung(2L, "Klausur 2", new Zeitraum("09:30", "10:00"), LocalDate.now().plusDays(5), true);
    public static final KlausurBuchung normaleKlausur3 = new KlausurBuchung(3L, "Klausur 3", new Zeitraum("10:30", "12:30"), LocalDate.now().plusDays(7), true);
    public static final List<KlausurBuchung> klausuren = List.of(normaleKlausur1, normaleKlausur2, normaleKlausur3);

    public static final Klausur klausur1 = new Klausur(1l, 100L, "globale Klausur 1", LocalDate.now().plusDays(4), new Zeitraum("08:30", "10:00"), true);
    public static final Klausur klausur2 = new Klausur(2l, 101L, "globale Klausur 2", LocalDate.now().plusDays(6), new Zeitraum("10:30", "12:00"), true);
    public static final Klausur klausur3 = new Klausur(3l, 102L, "globale Klausur 3", LocalDate.now().plusDays(7), new Zeitraum("08:00", "10:30"), false);
    public static final List<KlausurDTO> globaleKlasuren = List.of(new KlausurDTO(klausur1), new KlausurDTO(klausur2), new KlausurDTO(klausur3));
}
