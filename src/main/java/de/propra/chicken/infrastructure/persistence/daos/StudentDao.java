package de.propra.chicken.infrastructure.persistence.daos;

import de.propra.chicken.infrastructure.persistence.entities.StudentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentDao extends CrudRepository<StudentEntity, Long> {
    Optional<StudentEntity> findById(Long id);

    Optional<StudentEntity> findByGithubId(Long githubId);

    List<StudentEntity> findAll();
}
