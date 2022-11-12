package de.propra.chicken.services.DTOs.forms;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Zeitraum;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@NotNull
public record KlausurRegistrierungForm(@Digits(integer = 6, fraction = 0) Long lsfId,
                                       @NotEmpty @Size(max = 50) String fachName,
                                       @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate klausurTermin,
                                       @NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime start,
                                       @NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime ende,
                                       Boolean online) {

    public Klausur toKlausur() {
        Zeitraum klausurZeitraum = new Zeitraum(start(), ende());
        return new Klausur(null, lsfId(), fachName(), klausurTermin(), klausurZeitraum, online());
    }
}
