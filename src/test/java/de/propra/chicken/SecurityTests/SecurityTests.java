package de.propra.chicken.SecurityTests;

import de.propra.chicken.domain.model.Student;
import de.propra.chicken.infrastructure.controller.StudentController;
import de.propra.chicken.infrastructure.configuration.MethodSecurityConfiguration;
import de.propra.chicken.services.KlausurService;
import de.propra.chicken.services.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.query.Param;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest
@AutoConfigureMockMvc
@Import(MethodSecurityConfiguration.class)
public class SecurityTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentService studentService;

    @MockBean
    KlausurService klausurService;

    @ParameterizedTest
    @DisplayName("Wenn keine Session vorhanden -> Redirect zu GitHub OAuth")
    @ValueSource(strings = {"/", "/student", "/tutor", "/organisator"})
    public void testUnauthenticated(String route) throws Exception {
        when(studentService.getStudentByGithubId(any())).thenReturn(new Student(1L, 1L, "lol"));
        mockMvc.perform(MockMvcRequestBuilders.get(route))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @ParameterizedTest
    @DisplayName("Organisator darf auf /student, /tutor und /organisator")
    @ValueSource(strings = {"/student", "/tutor", "/organisator"})
    public void testOrganisatorSessionAllowed(String route) throws Exception {
        when(studentService.getStudentByGithubId(any())).thenReturn(new Student(1L, 1L, "lol"));
        mockMvc.perform(MockMvcRequestBuilders.get(route).session(AuthenticationTemplates.organisatorSession()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @ParameterizedTest
    @DisplayName("Tutor darf auf /student und /tutor")
    @ValueSource(strings = {"/student", "/tutor"})
    public void testTutorSessionAllowed(String route) throws Exception {
        when(studentService.getStudentByGithubId(any())).thenReturn(new Student(1L, 1L, "lol"));
        mockMvc.perform(MockMvcRequestBuilders.get(route).session(AuthenticationTemplates.tutorSession()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Tutor darf nicht auf /organisator")
    public void testTutorSessionDisallowed() throws Exception {
        when(studentService.getStudentByGithubId(any())).thenReturn(new Student(1L, 1L, "lol"));
        mockMvc.perform(MockMvcRequestBuilders.get("/organisator").session(AuthenticationTemplates.tutorSession()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/student", "/tutor", "/organisator"})
    public void testEverythingDisallowedForSomebody(String route) throws Exception {
        MockHttpSession somebody = AuthenticationTemplates.somebody();
        mockMvc.perform(MockMvcRequestBuilders.get(route).session(somebody))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testStudentPageSecurityAsStudent() throws Exception {
        MockHttpSession student = AuthenticationTemplates.studentSession();
        when(studentService.getStudentByGithubId(any())).thenReturn(new Student(1L, 1L, "lol"));
        mockMvc.perform(MockMvcRequestBuilders.get("/student").session(student))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    public void testStudentPageSecurityAsTutor() throws Exception {
        MockHttpSession tutor = AuthenticationTemplates.tutorSession();
        when(studentService.getStudentByGithubId(any())).thenReturn(new Student(1L, 1L, "lol"));
        mockMvc.perform(MockMvcRequestBuilders.get("/student").session(tutor))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    public void testStudentPageSecurityAsOrganisator() throws Exception {
        MockHttpSession organisator = AuthenticationTemplates.organisatorSession();
        when(studentService.getStudentByGithubId(any())).thenReturn(new Student(1L, 1L, "lol"));
        mockMvc.perform(MockMvcRequestBuilders.get("/student").session(organisator))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


}
