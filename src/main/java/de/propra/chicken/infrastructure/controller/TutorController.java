package de.propra.chicken.infrastructure.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ROLE_TUTOR')")
public class TutorController {

    @GetMapping("/tutor")
    public String tutor() {
        return "tutor";
    }
}
