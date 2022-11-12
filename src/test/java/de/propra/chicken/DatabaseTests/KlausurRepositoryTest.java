package de.propra.chicken.DatabaseTests;

import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.infrastructure.persistence.KlausurRepositoryImpl;
import de.propra.chicken.infrastructure.persistence.daos.KlausurDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(properties = "spring.flyway.enabled = false")
@ActiveProfiles("test")
@Sql({"classpath:h2test.sql"})
public class KlausurRepositoryTest {
    @Autowired
    KlausurDao klausurDao;

    KlausurRepositoryImpl klausurRepository;

    @BeforeEach
    void setup() {
        this.klausurRepository = new KlausurRepositoryImpl(klausurDao);
    }

    @Test
    @DisplayName("Vorhanden Klausur wird gefunden")
    void test_1() {
        Klausur propraKlausur = klausurRepository.findKlausurById(123456L);

        assertThat(propraKlausur).isNotNull();
        assertThat(propraKlausur.getKlausurId()).isEqualTo(123456L);
        assertThat(propraKlausur.getLsfId()).isEqualTo(111111L);
        assertThat(propraKlausur.getFachName()).isEqualTo("Propra II");
    }

    @Test
    @DisplayName("Neue Klausur kann registriert werden")
    void test_2() {
        LocalDate klausurTermin = LocalDate.now().plusDays(5);
        Zeitraum klausurZeitraum = new Zeitraum("10:30", "12:00");
        Klausur neueKlausur = new Klausur(null, 8888888L, "Neue Klausur",
                klausurTermin, klausurZeitraum, false);

        klausurRepository.registriereKlausur(neueKlausur);

        Klausur geladeneKlausur = klausurRepository.findKlausurByLsfId(8888888L);

        assertThat(geladeneKlausur).isEqualTo(neueKlausur);
    }

    @Test
    @DisplayName("Hole alle global eingetragenen Klausuren")
    void test_3() {
        List<Klausur> alleKlausuren = klausurRepository.findAlleKlausuren();

        assertThat(alleKlausuren).hasSize(2);
    }

    @Test
    @DisplayName("getKlausurById und getKlausurByLsfId liefert selbe Klausur")
    void test_4() {
        Klausur klausurById = klausurRepository.findKlausurById(123456L);
        Klausur klausurByLsfId = klausurRepository.findKlausurByLsfId(111111L);

        assertThat(klausurById).isEqualTo(klausurByLsfId);
    }


}
