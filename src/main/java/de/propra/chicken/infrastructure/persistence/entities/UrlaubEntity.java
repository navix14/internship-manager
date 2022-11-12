package de.propra.chicken.infrastructure.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("urlaub")
public class UrlaubEntity {
    @Id
    Long id;

    @Column("student_id")
    Long student_id;
    LocalDate datum;
    LocalTime start;
    LocalTime ende;

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return student_id;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnde() {
        return ende;
    }

    public UrlaubEntity(Long id, Long student_id, LocalDate datum, LocalTime start, LocalTime ende) {
        this.id = id;
        this.student_id = student_id;
        this.datum = datum;
        this.start = start;
        this.ende = ende;
    }
}