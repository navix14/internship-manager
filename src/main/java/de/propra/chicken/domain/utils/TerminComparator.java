package de.propra.chicken.domain.utils;

import de.propra.chicken.domain.model.Termin;

import java.io.Serializable;
import java.util.Comparator;

public class TerminComparator implements Comparator<Termin>, Serializable {
    @Override
    public int compare(Termin o1, Termin o2) {
        if (o1.getTag().isBefore(o2.getTag())) {
            return -1;
        } else if (o1.getTag().compareTo(o2.getTag()) == 0) { //falls tage gleich, ueperpruefe zeiten
            if (o1.getZeitraum().getStart().isBefore(o2.getZeitraum().getStart())) {
                return -1;
            } else if (o2.getZeitraum().getStart().isBefore(o1.getZeitraum().getStart())) {
                return 1;
            } else {
                return 0;
            }
        }
        return 1;
    }
}
