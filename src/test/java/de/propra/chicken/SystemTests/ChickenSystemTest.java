package de.propra.chicken.SystemTests;

import de.propra.chicken.domain.model.Student;
import de.propra.chicken.infrastructure.persistence.StudentRepositoryImpl;
import de.propra.chicken.services.StudentService;
import de.propra.chicken.services.auditlog.AuditLogPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.flyway.enabled = false")
@ActiveProfiles("test")
@Sql({"classpath:h2test.sql"})
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ChickenSystemTest {

    @Autowired
    MockMvc controller;

    @Autowired
    StudentService studentService;

    @Autowired
    StudentRepositoryImpl studentRepository;

    @MockBean
    AuditLogPublisher auditLog;

    private Map<String, Object> newUserSession;
    private Map<String, Object> ilumarySession;
    private long githubId;

    @BeforeEach
    private void testStudent() {
        this.githubId = new Random().nextLong(5000);
        newUserSession = Map.of("id", githubId, "handle", "e2euser");
        ilumarySession = Map.of("id", 777777L, "handle", "ilumary");
    }

    @Test
    @DisplayName("Wenn neuer Student auf / geht, wird er in der Datenbank eingetragen")
    @WithMockUser(roles = "STUDENT")
    void test_1() throws Exception {
        int oldNumStudents = studentRepository.findAlleStudenten().size();
        controller.perform(get("/").flashAttrs(newUserSession));
        assertThat(studentRepository.findAlleStudenten().size()).isEqualTo(oldNumStudents + 1);
    }

    @Test
    @DisplayName("Wenn Student Urlaub bucht, wird Urlaub in DB eingetragen")
    @WithMockUser(roles = "STUDENT")
    void test_2() throws Exception {
        LocalDate date = LocalDate.now().plusDays(3);

        controller.perform(get("/").flashAttrs(newUserSession));
        controller.perform(post("/buchen/urlaub").flashAttrs(newUserSession).with(csrf())
                .param("datum", date.toString())
                .param("start", "09:30")
                .param("ende", "10:30"));

        Student student = studentRepository.findStudentByGithubId(this.githubId);

        assertThat(student.getUrlaube()).isNotEmpty();
        assertThat(student.getSummeUrlaub()).isEqualTo(60);

        verify(auditLog).publishAuditEvent(contains("Neue Urlaubsbuchung"));
    }

    @Test
    @DisplayName("Student storniert Klausur")
    @WithMockUser(roles = "STUDENT")
    void test_3() throws Exception {
        controller.perform(get("/student").flashAttrs(ilumarySession));
        controller.perform(post("/stornieren/klausur").flashAttrs(ilumarySession).with(csrf())
                .param("klausurId", String.valueOf(123455)));

        Student student = studentService.getStudentByGithubId(777777L);

        assertThat(student.getKlausurBuchungen().size()).isEqualTo(1);
        assertThat(student.getKlausurIds().size()).isEqualTo(1);

        verify(auditLog).publishAuditEvent(contains("Klausurstornierung"));
    }
}