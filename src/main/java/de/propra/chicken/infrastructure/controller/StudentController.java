package de.propra.chicken.infrastructure.controller;

import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.services.DTOs.forms.KlausurRegistrierungForm;
import de.propra.chicken.services.DTOs.forms.UrlaubBuchenForm;
import de.propra.chicken.services.DTOs.forms.UrlaubStornierungsForm;
import de.propra.chicken.services.KlausurService;
import de.propra.chicken.services.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@PreAuthorize("hasRole('ROLE_STUDENT')")
public class StudentController {
    private final StudentService studentService;
    private final KlausurService klausurService;

    public StudentController(StudentService studentService, KlausurService klausurService) {
        this.studentService = studentService;
        this.klausurService = klausurService;
    }

    @GetMapping("/")
    public String login(@ModelAttribute("id") Long githubId, @ModelAttribute("handle") String handle) {
        if (studentService.getStudentByGithubId(githubId) == null) studentService.registerStudent(githubId, handle);
        return "redirect:/student";
    }

    @GetMapping("/student")
    public String index(Model model, @ModelAttribute("id") Long githubId) {
        Student student = studentService.getStudentByGithubId(githubId);

        model.addAttribute("urlaube", studentService.getUrlaubeSortedByTag(student));
        model.addAttribute("klausuren", studentService.getKlausurenSortedByTag(student));
        model.addAttribute("gesamturlaub", student.getSummeUrlaub());
        model.addAttribute("resturlaub", 240 - student.getSummeUrlaub());

        System.out.println(student);
        return "index";
    }

    @GetMapping("buchen/urlaub")
    public String urlaub_buchen(UrlaubBuchenForm urlaubBuchenForm) {
        return "urlaubsbuchung";
    }

    @PostMapping("buchen/urlaub")
    public String urlaub_buchen(@Valid UrlaubBuchenForm urlaubBuchenForm, BindingResult bindingResult, @ModelAttribute("id") Long githubId) {
        if (bindingResult.hasErrors()) {
            return "urlaubsbuchung";
        }

        Urlaub urlaub = new Urlaub(new Zeitraum(urlaubBuchenForm.start(), urlaubBuchenForm.ende()), urlaubBuchenForm.datum());
        studentService.bucheUrlaub(githubId, urlaub);
        return "redirect:/student";
    }

    @PostMapping("stornieren/urlaub")
    public String urlaub_stornieren(Model model, UrlaubStornierungsForm form, @ModelAttribute("id") Long githubId) {
        studentService.storniereUrlaub(githubId, form);
        return "redirect:/student";
    }

    @GetMapping("buchen/klausur")
    public String klausur_buchen(Model model) {
        model.addAttribute("klausuren", klausurService.getAlleKlausuren());
        return "klausurbuchung";
    }

    @PostMapping("buchen/klausur")
    public String klausur_buchen(Long klausurId, @ModelAttribute("id") Long githubId) {
        studentService.bucheKlausur(githubId, klausurId);
        return "redirect:/student";
    }

    @PostMapping("stornieren/klausur")
    public String storniere_klausur(Long klausurId, @ModelAttribute("id") Long githubId) {
        studentService.storniereKlausur(githubId, klausurId);
        return "redirect:/student";
    }

    @GetMapping("registrieren/klausur") //neue klausur im system anlegen
    public String klausur_registrieren(KlausurRegistrierungForm klausurRegistrierungForm) {
        return "klausurregistrierung";
    }

    @PostMapping("registrieren/klausur") //neue klausur im system anlegen
    public String klausur_registrieren(Model model, @Valid KlausurRegistrierungForm klausurRegistrierungForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "klausurregistrierung";
        }

        if (!klausurService.registriereKlausur(klausurRegistrierungForm.toKlausur())) {
            return "redirect:/registrieren/klausur";
        }
        return "redirect:/buchen/klausur";
    }

    @ModelAttribute("id")
    private Integer handle(@AuthenticationPrincipal OAuth2User user) {
        return user.getAttribute("id");
    }

    @ModelAttribute("handle")
    private String handle(Principal principal) {
        return principal.getName();
    }
}
