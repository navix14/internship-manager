package de.propra.chicken.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Component
public class Properties {

    private LocalDate startDatum;
    private LocalDate endDatum;

    public Properties() {
        this("","");
    }

    public Properties(@Value("${chicken.startdatum}") String startDatumString, @Value("${chicken.enddatum}") String endDatumString) {
        this.startDatum = (Objects.equals(startDatumString, "")) ? LocalDate.now().minusDays(10) : LocalDate.parse(startDatumString);
        this.endDatum = (Objects.equals(endDatumString, "")) ? LocalDate.now().plusDays(20) : LocalDate.parse(endDatumString);
    }

    public LocalDate getStartDatum() {
        return startDatum;
    }

    public LocalDate getEndDatum() {
        return endDatum;
    }
}
