package de.propra.chicken.domain.repositories;

import de.propra.chicken.domain.model.Klausur;

import java.util.List;

public interface KlausurRepository {
    Klausur findKlausurById(Long klausurId);

    Klausur findKlausurByLsfId(Long lsfId);

    void registriereKlausur(Klausur klausur);

    List<Klausur> findAlleKlausuren();
}
