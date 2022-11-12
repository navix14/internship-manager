package de.propra.chicken.ControllerTests;

import de.propra.chicken.domain.model.Student;
import de.propra.chicken.infrastructure.controller.StudentController;
import de.propra.chicken.services.KlausurService;
import de.propra.chicken.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StudentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentService studentService;

    @MockBean
    KlausurService klausurService;

    private Student testStudent;
    Map<String, Object> sessionAttrs;

    @BeforeEach
    private void testStudent() {
        testStudent = new Student(1L, 123L, "ilumary");

        sessionAttrs = Map.of("id", 123L, "handle", "ilumary");
    }

    @Test
    @DisplayName("GET `/`-Mapping redirected auf `/student` für existierenden Studenten")
    void test_1() throws Exception {
        when(studentService.getStudentByGithubId(any())).thenReturn(testStudent);

        mockMvc.perform(get("/").flashAttrs(sessionAttrs))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(studentService, times(0)).registerStudent(any(), any());
    }

    @Test
    @DisplayName("GET `/`-Mapping registriert Studenten, falls nicht existierend")
    void test_2() throws Exception {
        when(studentService.getStudentByGithubId(any())).thenReturn(null);

        mockMvc.perform(get("/").flashAttrs(sessionAttrs))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(studentService).registerStudent(testStudent.getGithubId(), testStudent.getHandle());
    }

    @Test
    @DisplayName("GET `/student`")
    void test_3() throws Exception {
        when(studentService.getStudentByGithubId(123L)).thenReturn(testStudent);
        when(studentService.getUrlaubeSortedByTag(testStudent)).thenReturn(ModelTemplates.urlaube);
        when(studentService.getKlausurenSortedByTag(testStudent)).thenReturn(ModelTemplates.klausuren);

        mockMvc.perform(get("/student").flashAttrs(sessionAttrs))
                .andExpect(model().attribute("urlaube", ModelTemplates.urlaube))
                .andExpect(model().attribute("klausuren", ModelTemplates.klausuren))
                .andExpect(model().attribute("gesamturlaub", 0))
                .andExpect(model().attribute("resturlaub", 240))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("`GET /urlaub_buchen` zeigt das `urlaubsbuchung`-Template an")
    void test_4() throws Exception {
        mockMvc.perform(get("/buchen/urlaub").flashAttrs(sessionAttrs))
                .andExpect(status().isOk())
                .andExpect(view().name("urlaubsbuchung"));
    }

    @Test
    @DisplayName("`POST /urlaub_buchen` bei valider Urlaub redirected und ruft studentservice.bucheUrlaub auf")
    void test_5_1() throws Exception {
        when(studentService.bucheUrlaub(any(),any())).thenReturn(true);
        mockMvc.perform(post("/buchen/urlaub").flashAttrs(sessionAttrs)
                        .param("datum", LocalDate.now().plusDays(3).toString())
                        .param("start", "09:30")
                        .param("ende", "10:30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(studentService).bucheUrlaub(eq(testStudent.getGithubId()), any());
    }

    @Test
    @DisplayName("/stornieren/urlaub ruft studentService.storniereUrlaub auf")
    void test_6_1() throws Exception {
        when(studentService.storniereUrlaub(any(),any())).thenReturn(true);
        mockMvc.perform(post("/stornieren/urlaub").flashAttrs(sessionAttrs)
                        .param("datum", LocalDate.now().plusDays(3).toString())
                        .param("start", "09:30")
                        .param("ende", "10:30"))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/student"));

        verify(studentService).storniereUrlaub(eq(testStudent.getGithubId()), any());
    }

    @Test
    @DisplayName("`GET /buchen/klausur` zeigt das `urlaubsbuchung`-Template an")
    void test_7() throws Exception {
        when(klausurService.getAlleKlausuren()).thenReturn(ModelTemplates.globaleKlasuren);
        mockMvc.perform(get("/buchen/klausur").flashAttrs(sessionAttrs))
                .andExpect(status().isOk())
                .andExpect(model().attribute("klausuren", ModelTemplates.globaleKlasuren))
                .andExpect(view().name("klausurbuchung"));
    }

    @Test
    @DisplayName("`POST /buchen/klausur` bucht vorhandene Klausur: redirected und ruft studentservice.bucheKlausur auf")
    void test_8_1() throws Exception {
        when(studentService.getStudentByGithubId(123L)).thenReturn(testStudent);
        when(klausurService.getKlausurById(2L)).thenReturn(ModelTemplates.klausur2);
        mockMvc.perform(post("/buchen/klausur").flashAttrs(sessionAttrs)
                        .param("klausurId", String.valueOf(2L)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
        verify(studentService).bucheKlausur(eq(testStudent.getGithubId()), any());
    }

    @Test
    @DisplayName("/stornieren/klausur ruft studentService.storniereKlausur auf")
    void test_10() throws Exception {
        mockMvc.perform(post("/stornieren/klausur").flashAttrs(sessionAttrs)
                        .param("klausurId", String.valueOf(2L)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(studentService).storniereKlausur(testStudent.getGithubId(), 2L);
    }

    @Test
    @DisplayName("/registrieren/klausur liefert klausurregistrierung template")
    void test_11() throws Exception {
        mockMvc.perform(get("/registrieren/klausur").flashAttrs(sessionAttrs))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("klausurregistrierung"));
    }

    @Test
    @DisplayName("Neuanlegen einer Klausur: Post /registrieren/klausur ruft klausurservice.registriereklausur auf")
    void test_12() throws Exception {
        mockMvc.perform(post("/registrieren/klausur").flashAttrs(sessionAttrs)
                        .param("lsfId", String.valueOf(4L))
                        .param("fachName", "Propra 4")
                        .param("klausurTermin", "2022-03-26")
                        .param("start", "11:00")
                        .param("ende", "12:30")
                        .param("online", String.valueOf(false)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrieren/klausur"));

        verify(klausurService).registriereKlausur(any());

    }
}
