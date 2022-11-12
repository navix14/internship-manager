package de.propra.chicken.UnitTests;


import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurBuchung;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentTests {
    private Student student;

    private final LocalDate startDatum = LocalDate.now().minusDays(5);
    private final LocalDate endDatum = LocalDate.now().plusDays(20);

    @BeforeEach
    private void student() {
        student = new Student(1L, new Random().nextLong(), "handle");
        student.setPraktikumsFristen(startDatum, endDatum);
    }

    @Test
    @DisplayName("ein valider Urlaub im Praktikumszeitraum kann hinzugefügt werden")
    public void testValidateMitEinemUrlaub_1() {
        boolean b = student.addUrlaub(new Urlaub(new Zeitraum("10:30", "12:15"), LocalDate.now().plusDays(2)));
        assertThat(b).isTrue();
    }

    //Urlaub wird nicht hinzugefüt

    @Test
    @DisplayName("ein zu langer Urlaub wird nicht hinzugefügt")
    public void testAddEinUrlaub_1() {
        Urlaub urlaub = new Urlaub(new Zeitraum("09:00", "12:00"), LocalDate.now().plusDays(2));

        boolean b = student.addUrlaub(urlaub);

        assertThat(student.getUrlaube()).doesNotContain(urlaub);
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("ein zu kurzer Urlaub")
    public void testAddEinUrlaub_2() {
        Urlaub urlaub = new Urlaub(new Zeitraum("09:00", "09:10"), LocalDate.now().plusDays(2));

        boolean b = student.addUrlaub(urlaub);

        assertThat(student.getUrlaube()).doesNotContain(urlaub);
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("ein Urlaub ausserhalb des Praktikumszeitraumes (Zeit)")
    public void testAddEinUrlaub_3() {
        Urlaub urlaub = new Urlaub(new Zeitraum("12:00", "13:15"), LocalDate.now().plusDays(2));

        boolean b = student.addUrlaub(urlaub);
        assertThat(student.getUrlaube()).doesNotContain(urlaub);
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("ein Urlaub ausserhalb des Praktikumszeitraumes (Datum)")
    public void testAddEinUrlaub_4() {
        Urlaub urlaub = new Urlaub(new Zeitraum("10:00", "11:00"), LocalDate.now().plusDays(21));

        boolean b = student.addUrlaub(urlaub);
        assertThat(student.getUrlaube()).doesNotContain(urlaub);
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Urlaub nicht als 15 minuten Intervall")
    public void testAddEinUrlaub_5() {
        Urlaub u = new Urlaub(new Zeitraum("10:00", "11:13"), LocalDate.now());

        boolean b = student.addUrlaub(u);
        assertThat(student.getUrlaube()).doesNotContain(u);
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("wenn keine klausur am tag, dann darf urlaub nicht > 2.5h")
    public void testAddEinUrlaub_6() {

        Urlaub urlaub = new Urlaub(new Zeitraum("08:30", "12:00"), LocalDate.now().plusDays(1));
        boolean b = student.addUrlaub(urlaub);

        assertThat(student.getUrlaube()).doesNotContain(urlaub);
        assertThat(b).isFalse();
    }


    //mehrere Urlaube buchen

    @Test
    @DisplayName("wenn keine klausur am tag, und zwei Urlaub nicht am anfang und ende, dann max. 1 urlaub buchbar")
    public void testAddEinUrlaub_7() {

        Urlaub urlaub1 = new Urlaub(new Zeitraum("09:30", "10:00"), LocalDate.now().plusDays(1));
        boolean b1 = student.addUrlaub(urlaub1);

        Urlaub urlaub2 = new Urlaub(new Zeitraum("11:00", "11:45"), LocalDate.now().plusDays(1));
        boolean b2 = student.addUrlaub(urlaub2);

        assertThat(student.getUrlaube()).doesNotContain(urlaub2);
        assertThat(b2).isFalse();
    }

    @Test
    @DisplayName("mit schon bestehenden Urlaub versucht mehr als 4h Urlaub zu buchen")
    public void testAddEinUrlaub_8() {
        Urlaub u = new Urlaub(new Zeitraum("10:00", "11:15"), LocalDate.now());
        Student customStudent = new Student(null, new Random().nextLong(), "customStudent", new ArrayList<>(), new ArrayList<>(), 180);
        customStudent.setPraktikumsFristen(startDatum, endDatum);

        boolean b = customStudent.addUrlaub(u);

        assertThat(student.getUrlaube()).doesNotContain(u);
        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Urlaub fuer heute kann nicht mehr gebucht werden")
    public void urlaubDeadline_1() {
        Urlaub urlaub = new Urlaub(new Zeitraum("09:30", "11:00"), LocalDate.now());

        Student customStudent = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), new ArrayList<>(), 0);
        customStudent.setPraktikumsFristen(startDatum, endDatum);

        boolean b = customStudent.addUrlaub(urlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("die summe an urlaub wird richtig hinzugefuegt, mehrere Urlaubsbuchungen")
    public void urlaubDeadline_2() {
        Urlaub urlaub1 = new Urlaub(new Zeitraum("08:30", "09:30"), LocalDate.now().plusDays(1));
        Urlaub urlaub2 = new Urlaub(new Zeitraum("11:30", "12:30"), LocalDate.now().plusDays(1));

        student.addUrlaub(urlaub1);
        student.addUrlaub(urlaub2);

        assertThat(student.getSummeUrlaub()).isEqualTo(120);
    }

    //Urlaube verschmelzen
    @Test
    @DisplayName("Neuer Urlaub überlappt (rechts) mit vorhandenen Urlaub und verschmilzt zu einem komplett neuen urlaub")
    public void testUeberlappungUrlaub_1() {
        List<Urlaub> urlaube = new ArrayList<>();
        urlaube.add(new Urlaub(new Zeitraum("10:00", "11:15"), LocalDate.now().plusDays(1)));
        Student customStudent = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), urlaube, 0);
        customStudent.setPraktikumsFristen(startDatum, endDatum);

        boolean b = customStudent.addUrlaub(new Urlaub(new Zeitraum("10:30", "12:15"), LocalDate.now().plusDays(1)));
        List<Urlaub> neueUrlaube = customStudent.getUrlaube();

        assertThat(neueUrlaube.size()).isEqualTo(1);
        assertThat(neueUrlaube.get(0).getZeitraum().getDauerInMinuten()).isEqualTo(135);
    }

    @Test
    @DisplayName("Neuer Urlaub ist mit anderem Urlaub benachbart und verschmilzt zu einem komplett neuen urlab")
    public void testBenachbartenUrlaub_1() {
        List<Urlaub> urlaube = new ArrayList<Urlaub>();
        urlaube.add(new Urlaub(new Zeitraum("10:00", "11:15"), LocalDate.now().plusDays(1)));
        Student customStudent = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), urlaube, 0);
        customStudent.setPraktikumsFristen(startDatum, endDatum);

        boolean b = customStudent.addUrlaub(new Urlaub(new Zeitraum("11:15", "12:15"), LocalDate.now().plusDays(1)));
        List<Urlaub> neueUrlaube = customStudent.getUrlaube();

        assertThat(neueUrlaube.size()).isEqualTo(1);
        assertThat(neueUrlaube.get(0).getZeitraum().getDauerInMinuten()).isEqualTo(135);
    }

    //Urlaub und Klausur

    @Test
    @DisplayName("neue urlaubsbuchung kollidiert mit online klausur")
    public void testUrlaubKollisionMitKlausur1() {
        List<KlausurBuchung> klausuren = new ArrayList<>();
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now().plusDays(1), new Zeitraum("09:00", "11:00"), true);

        klausuren.add(propra3.toKlausurBuchung());
        student.setKlausurBuchungen(klausuren);

        boolean b = student.addUrlaub(new Urlaub(new Zeitraum("10:00", "12:00"), LocalDate.now().plusDays(1)));
        List<Urlaub> neueUrlaube = student.getUrlaube();

        assertThat(neueUrlaube.size()).isEqualTo(1);
        assertThat(neueUrlaube.get(0).getZeitraum().getDauerInMinuten()).isEqualTo(60);
    }

    @Test
    @DisplayName("neue urlaubsbuchung kollidiert mit praesenz klausur")
    public void testUrlaubKollisionMitKlausur2() {
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now().plusDays(1), new Zeitraum("09:00", "10:00"), false);
        student.addKlausur(propra3.toKlausurBuchung());

        boolean b = student.addUrlaub(new Urlaub(new Zeitraum("10:00", "12:30"), LocalDate.now().plusDays(1)));
        List<Urlaub> neueUrlaube = student.getUrlaube();

        assertThat(neueUrlaube.size()).isEqualTo(1);
        assertThat(neueUrlaube.get(0).getZeitraum().getDauerInMinuten()).isEqualTo(30);
    }

    @Test
    @DisplayName("wenn klausur am tag, dann darf urlaub > 2.5h")
    public void testUrlaubKollisionMitKlausur3() {
        List<KlausurBuchung> klausuren = new ArrayList<>();
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now().plusDays(1), new Zeitraum("09:00", "10:00"), false);
        klausuren.add(propra3.toKlausurBuchung());
        Student student = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), new ArrayList<>(), 0);
        student.setPraktikumsFristen(startDatum, endDatum);
        student.setKlausurBuchungen(klausuren);

        boolean b = student.addUrlaub(new Urlaub(new Zeitraum("08:30", "12:30"), LocalDate.now().plusDays(1)));
        List<Urlaub> neueUrlaube = student.getUrlaube();

        assertThat(neueUrlaube.size()).isEqualTo(1);
        assertThat(neueUrlaube.get(0).getZeitraum().getDauerInMinuten()).isEqualTo(30);
    }

    @Test
    @DisplayName("zwei klausuren an einem tag, urlaub dazwischen, der beide klausuren schneidet")
    public void testUrlaubKollisionMitKlausur4() {
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now().plusDays(1), new Zeitraum("08:30", "09:00"), true);
        Klausur propra4 = new Klausur(new Random().nextLong(), 654321L, "Propra4", LocalDate.now().plusDays(1), new Zeitraum("10:00", "11:00"), true);
        List<KlausurBuchung> klausuren = new ArrayList<>(List.of(propra3.toKlausurBuchung(), propra4.toKlausurBuchung()));

        student.setKlausurBuchungen(klausuren);

        boolean b = student.addUrlaub(new Urlaub(new Zeitraum("08:45", "09:45"), LocalDate.now().plusDays(1)));
        List<Urlaub> neueUrlaube = student.getUrlaube();

        assertThat(neueUrlaube.size()).isEqualTo(1);
        assertThat(neueUrlaube.get(0).getZeitraum().getDauerInMinuten()).isEqualTo(30);
    }

    @Test
    @DisplayName("urlaub liegt innerhalb neuer klausurbuchung")
    public void testKlausurbuchung_1() {
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now(), new Zeitraum("08:30", "10:00"), false);

        Urlaub urlaub = new Urlaub(new Zeitraum("08:30", "9:30"), LocalDate.now());
        List<Urlaub> urlaube = new ArrayList<>();
        urlaube.add(urlaub);

        Student student = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), urlaube, 0);
        student.addKlausur(propra3.toKlausurBuchung());

        assertThat(student.getUrlaube().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("urlaub ragt rechts aus klausurbuchung heraus")
    public void testKlausurbuchung_2() {
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now(), new Zeitraum("08:30", "10:00"), true);

        List<Urlaub> urlaube = new ArrayList<>(List.of(new Urlaub(new Zeitraum("09:30", "11:00"), LocalDate.now())));

        Student customStudent = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), urlaube, 0);
        customStudent.setPraktikumsFristen(startDatum, endDatum);
        customStudent.addKlausur(propra3.toKlausurBuchung());

        assertThat(customStudent.getUrlaube().size()).isEqualTo(1);
        assertThat(customStudent.getUrlaube().get(0).getZeitraum().getDauerInMinuten()).isEqualTo(60);
    }

    //Storno

    @Test
    @DisplayName("Gebuchter Urlaub wird richtig storniert")
    public void urlaubeStornieren_1() {
        Urlaub urlaub1 = new Urlaub(new Zeitraum("08:30", "09:30"), LocalDate.now().plusDays(1));
        Student student = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), new ArrayList<>(), 0);
        student.setPraktikumsFristen(startDatum, endDatum);
        student.addUrlaub(urlaub1);

        student.removeUrlaub(urlaub1);
        assertThat(student.getSummeUrlaub()).isEqualTo(0);
        assertThat(student.getUrlaube()).hasSize(0);
    }

    @Test
    @DisplayName("Gebuchte Klausur wird richtig storniert")
    public void klausurStornieren_1() {
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now().plusDays(3), new Zeitraum("08:30", "10:00"), true);
        Student student = new Student(1L, new Random().nextLong(), "customStudent", new ArrayList<>(), new ArrayList<>(), 0);
        student.addKlausur(propra3.toKlausurBuchung());

        student.removeKlausurBuchung(propra3.toKlausurBuchung());

        assertThat(student.getKlausurBuchungen().size()).isEqualTo(0);
        assertThat(student.getKlausurIds()).hasSize(0);
    }

    @Test
    @DisplayName("Gebuchte Urlaube werden invalid wenn Klausur storniert wird")
    public void klausurStornieren_2() {
        Klausur propra3 = new Klausur(new Random().nextLong(), 123456L, "Propra3", LocalDate.now().plusDays(3), new Zeitraum("10:00", "11:00"), true);
        Urlaub urlaub1 = new Urlaub(new Zeitraum("08:30", "09:30"), LocalDate.now().plusDays(3));
        Urlaub urlaub2 = new Urlaub(new Zeitraum("11:00", "11:30"), LocalDate.now().plusDays(3));

        student.addKlausur(propra3.toKlausurBuchung());
        student.addUrlaub(urlaub1);
        student.addUrlaub(urlaub2);

        student.removeKlausurBuchung(propra3.toKlausurBuchung());

        assertThat(student.getKlausurBuchungen().size()).isEqualTo(0);
        assertThat(student.getKlausurIds().size()).isEqualTo(0);
        assertThat(student.getSummeUrlaub()).isEqualTo(0);
        assertThat(student.getUrlaube().size()).isEqualTo(0);
    }
}
