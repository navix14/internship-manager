package de.propra.chicken.UnitTests;

import de.propra.chicken.domain.model.Zeitraum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ZeitraumTests {

    @Test
    @DisplayName("Teste Ueberlappung zweier Zeiträume, z1 < z2")
    public void testeUeberlappung_1() {
        Zeitraum z1 = new Zeitraum("09:30", "10:30");
        Zeitraum z2 = new Zeitraum("10:00", "11:00");

        boolean uberlappung = z1.pruefeUeberlappung(z2);

        assertThat(uberlappung).isTrue();
    }

    @Test
    @DisplayName("Teste Ueberlappung zweier Zeiträume, z1 > z2")
    public void testeUeberlappung_2() {
        Zeitraum z1 = new Zeitraum("10:00", "11:00");
        Zeitraum z2 = new Zeitraum("09:30", "10:30");

        boolean uberlappung = z1.pruefeUeberlappung(z2);

        assertThat(uberlappung).isTrue();
    }

    @Test
    @DisplayName("Teste Ueberlappung zweier Zeiträume, z1 innerhalb z2")
    public void testeUeberlappung_3() {
        Zeitraum z1 = new Zeitraum("09:00", "10:00");
        Zeitraum z2 = new Zeitraum("08:30", "10:30");

        boolean uberlappung = z1.pruefeUeberlappung(z2);

        assertThat(uberlappung).isTrue();
    }

    @Test
    @DisplayName("Teste nicht Ueberlappung zweier Zeiträume")
    public void testeUeberlappung_4() {
        Zeitraum z1 = new Zeitraum("09:00", "10:00");
        Zeitraum z2 = new Zeitraum("11:30", "12:30");

        boolean uberlappung = z1.pruefeUeberlappung(z2);

        assertThat(uberlappung).isFalse();
    }

    @Test
    @DisplayName("Teste benachbarte Zeiträume, z1 zu z2")
    public void testeNachbar_1() {
        Zeitraum z1 = new Zeitraum("09:00", "10:00");
        Zeitraum z2 = new Zeitraum("10:00", "12:30");

        boolean nachbar = z1.pruefeNachbar(z2);

        assertThat(nachbar).isTrue();
    }

    @Test
    @DisplayName("Teste benachbarte Zeiträume, z2 zu z1")
    public void testeNachbar_2() {
        Zeitraum z1 = new Zeitraum("09:00", "10:00");
        Zeitraum z2 = new Zeitraum("10:00", "12:30");

        boolean nachbar = z2.pruefeNachbar(z1);

        assertThat(nachbar).isTrue();
    }

    @Test
    @DisplayName("Teste nicht benachbarte Zeiträume")
    public void testeNachbar_3() {
        Zeitraum z1 = new Zeitraum("09:00", "10:00");
        Zeitraum z2 = new Zeitraum("11:00", "12:30");

        boolean nachbar = z1.pruefeNachbar(z2);

        assertThat(nachbar).isFalse();
    }

    @Test
    @DisplayName("Teste invaliden Konstruktoraufruf")
    public void testeKonstruktor_1() {
        assertThatThrownBy(() -> {new Zeitraum("12:00", "10:00");}).isInstanceOf(IllegalArgumentException.class);
    }

}
