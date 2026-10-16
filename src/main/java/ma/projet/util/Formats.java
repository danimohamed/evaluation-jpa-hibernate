package ma.projet.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Mises en forme partagees par les trois exercices : dates au format long
 * francais ({@code 14 Mars 2013}), dates au format court ({@code 14/03/2013})
 * et montants en dirhams sans decimales inutiles.
 */
public final class Formats {

    private static final String[] MOIS = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };

    private static final ThreadLocal<SimpleDateFormat> DATE_COURTE =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE));

    private Formats() {
    }

    /**
     * @return la date au format long de l'enonce, par exemple {@code 14 Mars 2013}.
     */
    public static String dateLongue(Date date) {
        if (date == null) {
            return "";
        }
        Calendar calendrier = Calendar.getInstance();
        calendrier.setTime(date);
        return calendrier.get(Calendar.DAY_OF_MONTH) + " "
                + MOIS[calendrier.get(Calendar.MONTH)] + " "
                + calendrier.get(Calendar.YEAR);
    }

    /**
     * @return la date au format court de l'enonce, par exemple {@code 14/03/2013}.
     */
    public static String dateCourte(Date date) {
        return date == null ? "" : DATE_COURTE.get().format(date);
    }

    /**
     * @return le montant sans decimales lorsqu'il est entier ({@code 120} et non
     *         {@code 120.0}), avec deux decimales sinon.
     */
    public static String montant(double valeur) {
        if (valeur == Math.rint(valeur) && !Double.isInfinite(valeur)) {
            return String.valueOf((long) valeur);
        }
        return String.format(Locale.FRANCE, "%.2f", valeur);
    }

    /** Construit une date a partir de son jour, son mois (1-12) et son annee. */
    public static Date date(int jour, int mois, int annee) {
        Calendar calendrier = Calendar.getInstance();
        calendrier.clear();
        calendrier.set(annee, mois - 1, jour);
        return calendrier.getTime();
    }
}
