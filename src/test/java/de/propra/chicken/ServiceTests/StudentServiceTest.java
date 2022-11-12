package de.propra.chicken.ServiceTests;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.repositories.StudentRepository;
import de.propra.chicken.services.DTOs.forms.UrlaubStornierungsForm;
import de.propra.chicken.services.KlausurService;
import de.propra.chicken.services.Properties;
import de.propra.chicken.services.StudentService;
import de.propra.chicken.services.auditlog.AuditLogPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StudentServiceTest {
    StudentRepository studentRepository;
    StudentService studentService;
    KlausurService klausurService;


    Student testStudent;

    @BeforeEach
    void setup() {
        this.studentRepository = mock(StudentRepository.class);
        this.klausurService = mock(KlausurService.class);
        this.studentService = new StudentService(studentRepository, klausurService, mock(AuditLogPublisher.class), new Properties());
        testStudent = new Student(1L, 123L, "handle");
    }

    @Test
    @DisplayName("`getStudentByGithubId` ruft Repository und Klausurservice auf (valider Student)")
    void test_1() {
        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);
        studentService.getStudentByGithubId(123L);

        verify(klausurService).fetchGebuchteKlausurenForStudent(testStudent);
    }

    @Test
    @DisplayName("`getStudentByGithubId` ruft Repository und Klausurservice auf (invalider Student)")
    void test_2() {
        when(studentRepository.findStudentByGithubId(123L)).thenReturn(null);
        studentService.getStudentByGithubId(123L);

        verify(klausurService, times(0)).fetchGebuchteKlausurenForStudent(any());
    }

    @Test
    @DisplayName("`registerStudent` legt einen neuen Studenten an")
    void test_3() {
        studentService.registerStudent(123L, "handle");
        verify(studentRepository).saveStudent(any(Student.class));
    }

    @Test
    @DisplayName("`bucheUrlaub` mit validem Urlaub")
    void test_4() {
        Urlaub testUrlaub = new Urlaub(new Zeitraum("10:30", "11:30"), LocalDate.now().plusDays(3));

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);

        boolean success = studentService.bucheUrlaub(123L, testUrlaub);

        verify(studentRepository).saveStudent(testStudent);
        assertThat(success).isTrue();
    }

    @Test
    @DisplayName("`bucheUrlaub` mit invalidem Urlaub")
    void test_5() {
        Urlaub testUrlaub = new Urlaub(new Zeitraum("10:30", "13:30"), LocalDate.now().plusDays(3));

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);

        boolean success = studentService.bucheUrlaub(123L, testUrlaub);

        verify(studentRepository, times(0)).saveStudent(testStudent);
        assertThat(success).isFalse();
    }

    @Test
    @DisplayName("`bucheKlausur` mit nicht vorhandener Klausur")
    void test_6() {
        Klausur testKlausur = new Klausur(1L, 100L, "testKlausur",
                LocalDate.now().plusDays(3), new Zeitraum("09:30", "11:00"), true);

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);
        boolean success = studentService.bucheKlausur(testStudent.getGithubId(), testKlausur.getKlausurId());

        verify(studentRepository, times(0)).saveStudent(testStudent);
        assertThat(success).isFalse();
    }

    @Test
    @DisplayName("`bucheKlausur` mit vorhandener Klausur")
    void test_7() {
        Klausur testKlausur = new Klausur(1L, 100L, "testKlausur",
                LocalDate.now().plusDays(3), new Zeitraum("09:30", "11:00"), true);

        testStudent.setKlausurIds(List.of(1L));

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);
        boolean success = studentService.bucheKlausur(testStudent.getGithubId(), testKlausur.getKlausurId());

        verify(studentRepository, times(0)).saveStudent(testStudent);
        assertThat(success).isFalse();
    }

    @Test
    @DisplayName("`storniereKlausur`")
    void test_8() {
        Klausur testKlausur = new Klausur(1L, 100L, "testKlausur",
                LocalDate.now().plusDays(3), new Zeitraum("09:30", "11:00"), true);

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);
        when(klausurService.getKlausurById(1L)).thenReturn(testKlausur);

        boolean success = studentService.storniereKlausur(testStudent.getGithubId(), testKlausur.getKlausurId());

        verify(studentRepository).saveStudent(testStudent);
        assertThat(success).isTrue();
    }

    @Test
    @DisplayName("`storniereKlausur` - Stornierung wird zu spät getätigt")
    void test_9() {
        Klausur testKlausur = new Klausur(1L, 100L, "testKlausur",
                LocalDate.now(), new Zeitraum("09:30", "11:00"), true);

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);
        when(klausurService.getKlausurById(1L)).thenReturn(testKlausur);

        boolean success = studentService.storniereKlausur(testStudent.getGithubId(), testKlausur.getKlausurId());

        verify(studentRepository, times(0)).saveStudent(testStudent);
        assertThat(success).isFalse();
    }

    @Test
    @DisplayName("`storniereUrlaub` rechtzeitig")
    void test_10() {
        Urlaub gebuchterUrlaub = new Urlaub(new Zeitraum("09:30", "11:00"), LocalDate.now().plusDays(3));

        Student testStudent = new Student(1L, 123L, "handle",
                new ArrayList<>(), new ArrayList<>(List.of(gebuchterUrlaub)), 90);

        UrlaubStornierungsForm urlaubStornierungsForm = new UrlaubStornierungsForm(gebuchterUrlaub.getTag().toString(),
                gebuchterUrlaub.getZeitraum().getStart().toString(), gebuchterUrlaub.getZeitraum().getEnde().toString());

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);

        boolean success = studentService.storniereUrlaub(testStudent.getGithubId(), urlaubStornierungsForm);

        verify(studentRepository).saveStudent(testStudent);
        assertThat(success).isTrue();
    }

    @Test
    @DisplayName("`storniereUrlaub` zu spät")
    void test_11() {
        Urlaub gebuchterUrlaub = new Urlaub(new Zeitraum("09:30", "11:00"), LocalDate.now());

        Student testStudent = new Student(1L, 123L, "handle",
                new ArrayList<>(), new ArrayList<>(List.of(gebuchterUrlaub)), 90);

        UrlaubStornierungsForm urlaubStornierungsForm = new UrlaubStornierungsForm(gebuchterUrlaub.getTag().toString(),
                gebuchterUrlaub.getZeitraum().getStart().toString(), gebuchterUrlaub.getZeitraum().getEnde().toString());

        when(studentRepository.findStudentByGithubId(123L)).thenReturn(testStudent);

        boolean success = studentService.storniereUrlaub(testStudent.getGithubId(), urlaubStornierungsForm);

        verify(studentRepository, times(0)).saveStudent(testStudent);
        assertThat(success).isFalse();
    }
}
