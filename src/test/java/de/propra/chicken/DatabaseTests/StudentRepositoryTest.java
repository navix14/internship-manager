package de.propra.chicken.DatabaseTests;

import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.infrastructure.persistence.StudentRepositoryImpl;
import de.propra.chicken.infrastructure.persistence.daos.StudentDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(properties = "spring.flyway.enabled = false")
@ActiveProfiles("test")
@Sql({"classpath:h2test.sql"})
public class StudentRepositoryTest {

    @Autowired
    StudentDao studentDao;

    StudentRepositoryImpl studentRepository;

    @BeforeEach
    void setup() {
        this.studentRepository = new StudentRepositoryImpl(studentDao);
    }

    @Test
    @DisplayName("Student mit ID '1' und Handle 'ilumary' wird gefunden")
    void test_1_1() {
        Student ilumary = studentRepository.findStudentById(1L);
        assertThat(ilumary.getHandle()).isEqualTo("ilumary");
    }

    @Test
    @DisplayName("Student mit Github ID '777777' und Handle 'causebencancode' wird gefunden")
    void test_1_2() {
        Student ilumary = studentRepository.findStudentByGithubId(888888L);
        assertThat(ilumary.getHandle()).isEqualTo("causebencancode");
    }

    @Test
    @DisplayName("findStudentById und findStudentByGithubId liefern selben Studenten")
    void test_2() {
        Student studentByGithubId = studentRepository.findStudentByGithubId(777777L);
        Student studentById = studentRepository.findStudentById(1L);

        assertThat(studentByGithubId).isEqualTo(studentById);
    }

    @Test
    @DisplayName("Alle Studenten aus Datenbank laden")
    void test_3() {
        List<Student> students = studentRepository.findAlleStudenten();
        assertThat(students.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Einen Studenten speichern")
    void test_4() {
        Student student = new Student(null, 83745649L, "testStudent");
        studentRepository.saveStudent(student);
        Student testStudent = studentRepository.findStudentByGithubId(83745649L);
        assertThat(testStudent.getHandle()).isEqualTo("testStudent");
        assertThat(testStudent.getGithubId()).isEqualTo(83745649L);
    }

    @Test
    @DisplayName("auch Urlaubsbuchungen werden korrekt über studentRepo abgespeichert")
    void test_5() {
        ArrayList<Urlaub> urlaube = new ArrayList<>(List.of(new Urlaub(new Zeitraum("09:30", "11:30"), now())));
        Student student = new Student(null, 83745649L, "testStudent", new ArrayList<>(), urlaube, 0);
        studentRepository.saveStudent(student);
        Student testStudent = studentRepository.findStudentByGithubId(83745649L);
        assertThat(testStudent.getHandle()).isEqualTo("testStudent");
        assertThat(testStudent.getUrlaube()).containsAll(urlaube);
    }

    @Test
    @DisplayName("Einen Studenten updaten mit Klausur")
    void test_6() {
        Student ilumary = studentRepository.findStudentById(1L);
        ilumary.setKlausurIds(List.of(123455L));

        studentRepository.saveStudent(ilumary);

        Student testStudent = studentRepository.findStudentById(1L);
        assertThat(testStudent.getKlausurIds()).containsOnly(123455L);
    }
}
