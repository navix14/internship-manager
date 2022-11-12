package de.propra.chicken.infrastructure.persistence;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.repositories.KlausurRepository;
import de.propra.chicken.infrastructure.persistence.daos.KlausurDao;
import de.propra.chicken.infrastructure.persistence.entities.KlausurEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class KlausurRepositoryImpl implements KlausurRepository {
    private final KlausurDao klausurDao;

    public KlausurRepositoryImpl(KlausurDao klausurDao) {
        this.klausurDao = klausurDao;
    }

    @Override
    public Klausur findKlausurById(Long klausurId) {
        Optional<KlausurEntity> klausurEntityById = klausurDao.getKlausurEntityById(klausurId);
        return klausurEntityById.map(this::toKlausur).orElse(null);
    }

    @Override
    public Klausur findKlausurByLsfId(Long lsfId) {
        Optional<KlausurEntity> klausurEntity = klausurDao.findByLsfId(lsfId);
        return klausurEntity.map(this::toKlausur).orElse(null);
    }

    @Override
    public void registriereKlausur(Klausur klausur) {
        if (findKlausurByLsfId(klausur.getLsfId()) != null) return;
        klausurDao.save(toKlausurEntity(klausur));
    }

    @Override
    public List<Klausur> findAlleKlausuren() {
        return klausurDao.findAll().stream().map(this::toKlausur).collect(Collectors.toList());
    }

    private KlausurEntity toKlausurEntity(Klausur klausur) {
        return new KlausurEntity(klausur);
    }

    private Klausur toKlausur(KlausurEntity klausurEntity) {
        return new Klausur(klausurEntity.id(), klausurEntity.lsfId(), klausurEntity.name(),
                klausurEntity.datum(), new Zeitraum(klausurEntity.start(), klausurEntity.ende()), klausurEntity.online());
    }

}
