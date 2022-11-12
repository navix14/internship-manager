package de.propra.chicken.services;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurBuchung;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.repositories.KlausurRepository;
import de.propra.chicken.services.DTOs.KlausurDTO;
import de.propra.chicken.services.auditlog.AuditLogPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KlausurService {

    private final KlausurRepository repository;
    private final AuditLogPublisher publisher;
    private final LSFValidator lsfValidator;

    public KlausurService(KlausurRepository repository, AuditLogPublisher publisher, LSFValidator lsfValidator) {
        this.repository = repository;
        this.publisher = publisher;
        this.lsfValidator = lsfValidator;
    }

    public Klausur getKlausurById(Long klausurId) {
        return repository.findKlausurById(klausurId);
    }

    public boolean registriereKlausur(Klausur klausur) {
        Klausur byLsfId = repository.findKlausurByLsfId(klausur.getLsfId());
        if (byLsfId != null) return false;
        try {
            if (!lsfValidator.checkLsf(klausur)) return false;
        } catch (IOException ex) {
            return false;
        }

        repository.registriereKlausur(klausur);
        publisher.publishAuditEvent("Neue Klausurregistrierung im System für " + klausur.getFachName()
                + "  mit LsfID " + klausur.getLsfId() + " am " + klausur.getTag() + ", von "
                + klausur.getZeitraum().getStart() + " bis " + klausur.getZeitraum().getEnde());
        return true;
    }

    public void fetchGebuchteKlausurenForStudent(Student student) {
        List<KlausurBuchung> klausurBuchungen = student.getKlausurIds().stream()
                .map(repository::findKlausurById)
                .map(Klausur::toKlausurBuchung)
                .collect(Collectors.toList());
        student.setKlausurBuchungen(klausurBuchungen);
    }

    public List<KlausurDTO> getAlleKlausuren() {
        return repository.findAlleKlausuren().stream().map(KlausurDTO::new).collect(Collectors.toList());
    }
}
