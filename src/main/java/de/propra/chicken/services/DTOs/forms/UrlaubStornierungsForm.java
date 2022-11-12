package de.propra.chicken.services.DTOs.forms;

import java.time.LocalDate;
import java.time.LocalTime;

public record UrlaubStornierungsForm(String tag, String start, String ende) {
}
