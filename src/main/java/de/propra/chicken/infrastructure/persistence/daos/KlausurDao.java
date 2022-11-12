package de.propra.chicken.infrastructure.persistence.daos;

import de.propra.chicken.infrastructure.persistence.entities.KlausurEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface KlausurDao extends CrudRepository<KlausurEntity, Long> {
    Optional<KlausurEntity> getKlausurEntityById(Long klausurId);

    Optional<KlausurEntity> findByLsfId(Long lsfId);

    @Override
    List<KlausurEntity> findAll();
}
