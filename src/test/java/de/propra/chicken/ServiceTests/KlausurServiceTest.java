package de.propra.chicken.ServiceTests;

import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.repositories.KlausurRepository;
import de.propra.chicken.services.LSFValidator;
import de.propra.chicken.services.auditlog.AuditLogPublisher;
import de.propra.chicken.services.KlausurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class KlausurServiceTest {
    KlausurRepository klausurRepository;
    KlausurService klausurService;

    LSFValidator lsfValidator;

    @BeforeEach
    void setup() {
        this.klausurRepository = mock(KlausurRepository.class);
        lsfValidator = mock(LSFValidator.class);
        this.klausurService = new KlausurService(klausurRepository, mock(AuditLogPublisher.class), lsfValidator);
    }

    @Test
    @DisplayName("getKlausur ruft das Repository auf")
    void test_1() {
        klausurService.getKlausurById(123L);
        verify(klausurRepository).findKlausurById(123L);
    }

    @Test
    @DisplayName("getKlausurById ruft das Repository auf")
    void test_2() {
        klausurService.getKlausurById(1L);
        verify(klausurRepository).findKlausurById(1L);
    }

    @Test
    @DisplayName("LSF-Check (valide Klausur)")
    void test_3() throws IOException {
        Klausur dataScienceKlausur = new Klausur(123L, 225282L, "Data Science", LocalDate.now().plusDays(5), new Zeitraum("9:00", "10:00"), true);
        when(lsfValidator.checkLsf(dataScienceKlausur)).thenReturn(true);

        boolean check = klausurService.registriereKlausur(dataScienceKlausur);

        verify(klausurRepository).registriereKlausur(dataScienceKlausur);
        assertThat(check).isTrue();
    }

    @Test
    @DisplayName("LSF-Check (valider Name aber inkorrekte ID)")
    void test_4() throws IOException {
        Klausur dataScienceKlausur = new Klausur(123L, 925285L, "Data Science", LocalDate.now().plusDays(5), new Zeitraum("9:00", "10:00"), true);
        when(lsfValidator.checkLsf(dataScienceKlausur)).thenReturn(false);
        boolean check = klausurService.registriereKlausur(dataScienceKlausur);

        verify(klausurRepository, times(0)).registriereKlausur(dataScienceKlausur);
        assertThat(check).isFalse();
    }

    @Test
    @DisplayName("LSF-Check (valide ID aber inkorrekter Name)")
    void test_5() throws IOException {
        Klausur dataScienceKlausur = new Klausur(123L, 225282L, "Data Sciense", LocalDate.now().plusDays(5), new Zeitraum("9:00", "10:00"), true);
        when(lsfValidator.checkLsf(dataScienceKlausur)).thenReturn(false);
        boolean check = klausurService.registriereKlausur(dataScienceKlausur);

        verify(klausurRepository, times(0)).registriereKlausur(dataScienceKlausur);
        assertThat(check).isFalse();
    }

    @Test
    @DisplayName("KlausurBuchungen für einen Studenten über die KlausurIds setzen")
    void test_6() {
        Student student = new Student(1L, 123L, "handle");
        student.setKlausurIds(List.of(100L, 200L));
        Klausur testKlausur1  = new Klausur(2L, 100L, "Klausur 1",
                LocalDate.parse("2022-03-25"), new Zeitraum("10:30", "12:00"), true);
        Klausur testKlausur2 = new Klausur(3L, 200L, "Klausur 2",
                LocalDate.parse("2022-03-27"), new Zeitraum("10:30", "12:00"), true);

        when(klausurRepository.findKlausurById(100L)).thenReturn(testKlausur1);
        when(klausurRepository.findKlausurById(200L)).thenReturn(testKlausur2);

        klausurService.fetchGebuchteKlausurenForStudent(student);

        assertThat(student.getKlausurBuchungen()).contains(testKlausur1.toKlausurBuchung(),testKlausur2.toKlausurBuchung());
    }

    @Test
    @DisplayName("getAlleKlausuren ruft das Repository auf")
    void test_7() {
        klausurService.getAlleKlausuren();
        verify(klausurRepository).findAlleKlausuren();
    }
}
