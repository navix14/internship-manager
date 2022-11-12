package de.propra.chicken.services.DTOs.forms;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record UrlaubBuchenForm(@NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime start,
                               @NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime ende,
                               @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate datum) {
}
