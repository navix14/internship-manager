package de.propra.chicken.services;

import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurBuchung;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.repositories.StudentRepository;
import de.propra.chicken.domain.utils.TerminComparator;

import de.propra.chicken.services.DTOs.forms.UrlaubStornierungsForm;
import de.propra.chicken.services.auditlog.AuditLogPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final KlausurService klausurService;
    private final AuditLogPublisher publisher;

    final Properties properties;

    LocalDate startDatum;
    LocalDate endDatum;


    public StudentService(StudentRepository studentRepository, KlausurService klausurService, AuditLogPublisher publisher, Properties properties){
        this.studentRepository = studentRepository;
        this.klausurService = klausurService;
        this.publisher = publisher;

        this.properties = properties;
        startDatum = properties.getStartDatum();
        endDatum = properties.getEndDatum();
    }

    public Student getStudentByGithubId(Long githubId) {
        Student student = studentRepository.findStudentByGithubId(githubId);
        if (student != null) {
            student.setPraktikumsFristen(startDatum, endDatum);
            klausurService.fetchGebuchteKlausurenForStudent(student);
        }
        return student;
    }

    public void registerStudent(Long githubId, String handle) {
        studentRepository.saveStudent(new Student(null, githubId, handle));
        publisher.publishAuditEvent("Neue Registrierung für Github ID " + githubId + " mit handle \"" + handle + "\"");
    }

    public boolean bucheUrlaub(Long githubId, Urlaub urlaub) {
        Student student = getStudentByGithubId(githubId);

        if (student.addUrlaub(urlaub)) {
            studentRepository.saveStudent(student);
            publisher.publishAuditEvent("Neue Urlaubsbuchung für Studenten mit Github ID " + githubId + " am " +
                    urlaub.getTag() + ", von " + urlaub.getZeitraum().getStart() + " bis " + urlaub.getZeitraum().getEnde());
            return true;
        }
        return false;
    }

    public boolean bucheKlausur(Long githubId, Long klausurId) {
        Student student = getStudentByGithubId(githubId);

        Klausur klausur = klausurService.getKlausurById(klausurId);
        if(klausur == null) return false;

        if (student.getKlausurIds().contains(klausur.getKlausurId()))
            return false;

        if (student.addKlausur(klausur.toKlausurBuchung())) {
            studentRepository.saveStudent(student);
            publisher.publishAuditEvent("Neue Klausurbuchung im Fach " + klausur.getFachName()
                    + " für Studenten mit Github ID " + githubId + " am " + klausur.getTag() +
                    ", von " + klausur.getZeitraum().getStart() + " bis " + klausur.getZeitraum().getEnde());
            return true;
        }
        return false;
    }

    public boolean storniereKlausur(Long githubId, Long klausurId) {
        Student student = getStudentByGithubId(githubId);
        Klausur klausur = klausurService.getKlausurById(klausurId);

        if (student.removeKlausurBuchung(klausur.toKlausurBuchung())) { //getKlausur might be NULL!
            studentRepository.saveStudent(student);
            publisher.publishAuditEvent("Klausurstornierung im Fach " + klausur.getFachName()
                    + " vom Studenten mit Github ID " + githubId + " am " + klausur.getTag() +
                    ", von " + klausur.getZeitraum().getStart() + " bis " + klausur.getZeitraum().getEnde());
            return true;
        }

        return false;
    }

    public boolean storniereUrlaub(Long githubId, UrlaubStornierungsForm form) {
        Student student = getStudentByGithubId(githubId);
        Urlaub urlaub = new Urlaub(new Zeitraum(form.start(), form.ende()), LocalDate.parse(form.tag()));

        if (student.removeUrlaub(urlaub)) {
            studentRepository.saveStudent(student);
            publisher.publishAuditEvent("Urlaubsstornierung für Studenten mit Github ID " + githubId + " am " +
                    urlaub.getTag() + ", von " + urlaub.getZeitraum().getStart() + " bis " + urlaub.getZeitraum().getEnde());
            return true;
        }

        return false;
    }

    public List<KlausurBuchung> getKlausurenSortedByTag(Student student) {
        return student.getKlausurBuchungen().stream().filter(k->k.getTag().compareTo(LocalDate.now()) >= 0).sorted(new TerminComparator()).collect(Collectors.toList());
    }

    public List<Urlaub> getUrlaubeSortedByTag(Student student) {
        return student.getUrlaube().stream().sorted(new TerminComparator()).collect(Collectors.toList());
    }
}
