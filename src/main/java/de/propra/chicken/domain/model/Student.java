package de.propra.chicken.domain.model;

import de.propra.chicken.domain.utils.TerminComparator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

//@Aggregate
public class Student {
    private final Long studentId; //DB ID
    private final Long githubId; //Aggreate ID
    private final String handle;
    private List<KlausurBuchung> klausurBuchungen;
    private List<Long> klausurIds; //reference to Klausurbuchungen that is loaded from DB
    private List<Urlaub> urlaube;
    private int summeUrlaub;

    private LocalDate startDatum;
    private LocalDate endDatum;

    private LocalTime start;
    private LocalTime ende;

    public void setPraktikumsFristen(LocalDate startDatum, LocalDate endDatum) {
        this.startDatum = startDatum;
        this.endDatum = endDatum;
    }

    public List<Long> getKlausurIds() {
        return new ArrayList<>(klausurIds);
    }

    public List<Urlaub> getUrlaube() {
        return new ArrayList<>(urlaube);
    }

    public void setKlausurBuchungen(List<KlausurBuchung> klausurBuchungen) {
        this.klausurBuchungen = new ArrayList<>(klausurBuchungen);
    }

    public List<KlausurBuchung> getKlausurBuchungen() {
        return new ArrayList<>(klausurBuchungen);
    }

    public Student(Long studentId, Long githubId, String handle) {
        this.studentId = studentId;
        this.githubId = githubId;
        this.handle = handle;
        this.summeUrlaub = 0;
        this.klausurIds = new ArrayList<>();
        this.urlaube = new ArrayList<>();
        this.klausurBuchungen = new ArrayList<>();
        this.start = LocalTime.parse("08:30:00");
        this.ende = LocalTime.parse("12:30:00");
    }

    public Student(Long studentId, Long githubId, String github_handle, List<Long> klausurIds, List<Urlaub> urlaube, int summeUrlaub) {
        this(studentId, githubId, github_handle);
        this.klausurIds = new ArrayList<>(klausurIds);
        this.urlaube = new ArrayList<>(urlaube);
        this.summeUrlaub = summeUrlaub;
    }

    public boolean addUrlaub(Urlaub urlaub) {
        if (isTooLate(urlaub.getTag())) return false;
        if (doesNotPassBaseRules(urlaub)) return false;
        this.urlaube.add(urlaub);
        handlePossibleCollisions(urlaub);
        if (!validateUrlaub(urlaub)) {
            this.urlaube.remove(urlaub);
            return false;
        }
        this.summeUrlaub = calculateNewUrlaubSumme();
        return true;
    }

    public boolean removeUrlaub(Urlaub urlaub) {
        if (isTooLate(urlaub.getTag())) return false;
        this.summeUrlaub -= urlaub.getZeitraum().getDauerInMinuten();
        return this.urlaube.remove(urlaub);
    }

    public boolean addKlausur(KlausurBuchung buchung) {
        LocalDate tag = buchung.getTag();
        List<Urlaub> urlaubeAmTag = getUrlaubeAmTagSortiert(tag);

        if (urlaubeAmTag.size() >= 2)
            return false;

        this.klausurIds.add(buchung.getKlausurId());
        this.klausurBuchungen.add(buchung);
        urlaubeAmTag.forEach(this::checkKlausurCollisionsMit);
        return true;
    }

    public boolean removeKlausurBuchung(KlausurBuchung klausurBuchung) {
        LocalDate tag = klausurBuchung.getTag();
        if (isTooLate(tag)) return false;

        this.klausurBuchungen.remove(klausurBuchung);
        this.klausurIds.remove(klausurBuchung.getKlausurId());

        List<Urlaub> urlaubeAmTag = getUrlaubeAmTagSortiert(tag);

        Optional<Urlaub> any = urlaubeAmTag.stream().filter(Predicate.not(this::validateUrlaub)).findAny(); //gibt es einen Urlaub der die Validation nicht erfullt?
        if (any.isPresent()) urlaubeAmTag.forEach(this::removeUrlaub); //dann remove alle urlaube für diesen Tag

        return true;
    }

    private boolean isTooLate(LocalDate tag) {
        return tag.isBefore(LocalDate.now().plusDays(1)); // der Tag vor morgen, ist heute; (morgen := now+1), same as "urlaub.getTag().isAfter(LocalDate.now().minusDays(1))"
    }

    private void handlePossibleCollisions(Urlaub urlaub) {
        checkUrlaubCollisionMit(urlaub);
        checkKlausurCollisionsMit(urlaub);
    }

    private boolean validateUrlaub(Urlaub urlaub) { //base rules zum buchen eines urlaubs
        if (doesNotPassBaseRules(urlaub)) return false;
        LocalDate tag = urlaub.getTag();
        long dauerInMinuten = urlaub.getZeitraum().getDauerInMinuten();

        if (getKlausurenAmTag(tag).isEmpty()) { //Keine Klausur am Tag
            return passesRulesForNoExamForDay(tag, dauerInMinuten);
        }
        return true;
    }

    private boolean doesNotPassBaseRules(Urlaub urlaub) {
        int anzahlUrlaube = this.getUrlaubeAmTagSortiert(urlaub.getTag()).size();
        long dauerInMinuten = urlaub.getZeitraum().getDauerInMinuten();
        if (dauerInMinuten + summeUrlaub > 240 || summeUrlaub < 0) return true;
        if (dauerInMinuten % 15 != 0 || dauerInMinuten == 0) return true;
        if (anzahlUrlaube > 2) return true;
        return urlaub.getZeitraum().getStart().isBefore(start) || urlaub.getZeitraum().getEnde().isAfter(ende)
                || urlaub.getTag().isBefore(startDatum) || urlaub.getTag().isAfter(endDatum);
    }

    private boolean passesRulesForNoExamForDay(LocalDate tag, long dauerInMinuten) {
        List<Urlaub> urlaubeAmTag = this.getUrlaubeAmTagSortiert(tag);
        //Urlaubsdauer max 2,5h oder 4h genau
        if (dauerInMinuten > 150 && dauerInMinuten != 240)
            return false;

        if (urlaubeAmTag.size() == 2) { //Falls genau zwei Urlaube
            Urlaub u1 = urlaubeAmTag.get(0);
            Urlaub u2 = urlaubeAmTag.get(1);

            // Zeit zwischen zwei Urlauben mindestens 90 Minuten
            long between = ChronoUnit.MINUTES.between(u1.getZeitraum().getEnde(), u2.getZeitraum().getStart());
            if (between < 90) return false;

            // zwei Urlaube liegen jeweils am Anfang bzw. Ende des Tages
            return u1.getZeitraum().getStart().compareTo(start) == 0 &&
                    u2.getZeitraum().getEnde().compareTo(ende) == 0;
        }
        return true;
    }

    // Prüft, ob zwei Urlaube überlappen und verschmilzt sie ggf.
    private void checkUrlaubCollisionMit(Urlaub thisOne) {
        LocalDate tag = thisOne.getTag();
        List<Urlaub> urlaubeAmTag = getUrlaubeAmTagSortiert(tag);

        for (Urlaub that : urlaubeAmTag) {
            if (that == thisOne) continue; //checke collision nicht mit sich selber
            Zeitraum thisOneZeitraum = thisOne.getZeitraum();
            Zeitraum thatZeitraum = that.getZeitraum();
            if (thisOneZeitraum.pruefeUeberlappung(thatZeitraum) || thisOneZeitraum.pruefeNachbar(thatZeitraum)) {
                Urlaub neu = verschmelzeUrlaub(thisOneZeitraum, thatZeitraum, tag);

                this.urlaube.remove(that);
                this.urlaube.remove(thisOne);
                this.urlaube.add(neu);

                break;
            }
        }
    }

    private int calculateNewUrlaubSumme() {
        return (int) this.urlaube.stream().mapToLong(u -> u.getZeitraum().getDauerInMinuten()).sum();
    }

    // Prüft, ob Klausur und Urlaub überlappen und schneidet den Urlaub ggf.
    private void checkKlausurCollisionsMit(Urlaub urlaub) {
        LocalDate tag = urlaub.getTag();
        List<KlausurBuchung> klausurenAmTag = getKlausurenAmTag(tag);
        for (KlausurBuchung k : klausurenAmTag) {
            Zeitraum urlaubZeitraum = urlaub.getZeitraum();
            if (urlaubZeitraum.pruefeUeberlappung(k.getGebuchterZeitraum())) {
                Urlaub neuerUrlaub = schneideUrlaub(k, urlaubZeitraum, tag);
                this.urlaube.remove(urlaub);
                if (neuerUrlaub == null) return;
                this.urlaube.add(neuerUrlaub);
                urlaub = neuerUrlaub;
            }
        }
    }

    private Urlaub schneideUrlaub(KlausurBuchung k, Zeitraum urlaub, LocalDate tag) {
        Zeitraum klausur = k.getGebuchterZeitraum();

        // urlaub komplett innerhalb klausur
        if (!urlaub.getStart().isBefore(klausur.getStart()) && !urlaub.getEnde().isAfter(klausur.getEnde()))
            return null;

        // urlaub schneidet klausur von links
        if (urlaub.getStart().isBefore(klausur.getStart())) {
            Urlaub neu = new Urlaub(new Zeitraum(urlaub.getStart(), klausur.getStart()), tag);
            if (validateUrlaub(neu)) return neu;
        } else { // urlaub schneidet klausur von rechts
            Urlaub neu = new Urlaub(new Zeitraum(klausur.getEnde(), urlaub.getEnde()), tag);
            if (validateUrlaub(neu)) return neu;
        }
        return null;
    }

    private Urlaub verschmelzeUrlaub(Zeitraum u1, Zeitraum u2, LocalDate tag) {
        // u1 schneidet u2 von links
        if (u1.getStart().isBefore(u2.getStart())) {
            return new Urlaub(new Zeitraum(u1.getStart(), u2.getEnde()), tag);
        } else { // u1 schneidet u2 von rechts
            return new Urlaub(new Zeitraum(u2.getStart(), u1.getEnde()), tag);
        }
    }

    private List<KlausurBuchung> getKlausurenAmTag(LocalDate tag) {
        return klausurBuchungen.stream()
                .filter(k -> tag.isEqual(k.getTag()))
                .sorted(new TerminComparator()).toList();
    }

    private List<Urlaub> getUrlaubeAmTagSortiert(LocalDate tag) {
        return urlaube.stream()
                .filter(u -> tag.isEqual(u.getTag()))
                .sorted(new TerminComparator()).toList();
    }

    public int getSummeUrlaub() {
        return summeUrlaub;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getGithubId() {
        return githubId;
    }

    public String getHandle() {
        return handle;
    }

    public void setKlausurIds(List<Long> klausurIds) {
        this.klausurIds = new ArrayList<>(klausurIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentId.equals(student.studentId) && githubId.equals(student.githubId) && Objects.equals(handle, student.handle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, githubId, handle);
    }

    @Override
    public String toString() {
        return "Student{" +
                ", studentId=" + studentId +
                ", github_handle='" + handle + '\'' +
                ", klausurBuchungen=" + klausurBuchungen +
                ", klausurIds=" + klausurIds +
                ", urlaube=" + urlaube +
                ", summeUrlaub=" + summeUrlaub +
                ", start='" + startDatum + '\'' +
                ", ende='" + endDatum + '\'' +
                ", start=" + start +
                ", ende=" + ende +
                '}';
    }
}
