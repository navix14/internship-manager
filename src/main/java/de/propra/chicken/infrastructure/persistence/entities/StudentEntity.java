package de.propra.chicken.infrastructure.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("student")
public class StudentEntity {
    @Id
    Long id;
    Long githubId;
    String handle;
    Integer gebuchter_urlaub;

    @MappedCollection(idColumn = "student_id")
    Set<KlausurRef> klausurbuchungen;

    @MappedCollection(idColumn = "student_id")
    Set<UrlaubEntity> urlaube;

    public StudentEntity(Long id, Long githubId, String handle, Integer gebuchter_urlaub, Set<KlausurRef> klausurbuchungen, Set<UrlaubEntity> urlaube) {
        this.id = id;
        this.githubId = githubId;
        this.handle = handle;
        this.gebuchter_urlaub = gebuchter_urlaub;
        this.klausurbuchungen = klausurbuchungen;
        this.urlaube = urlaube;
    }

    public Long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public Integer getGebuchterUrlaub() {
        return gebuchter_urlaub;
    }

    public Set<KlausurRef> getKlausurbuchungen() {
        return klausurbuchungen;
    }

    public Set<UrlaubEntity> getUrlaube() {
        return urlaube;
    }

    public Long getGithubId() {
        return githubId;
    }
}