package ma.projet.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.util.Formats;

/**
 * Service de l'entite {@link Mariage} (exercice 3).
 *
 * <p>Le rapport principal de l'enonce est {@link #afficherMariagesDunHomme(int)} :
 * il separe les mariages en cours des mariages echoues et reprend le format
 * attendu, notamment la ligne de detail des mariages echoues qui place la date
 * de fin sur une seconde ligne.</p>
 */
public class MariageService extends AbstractService<Mariage> {

    /** Largeur des colonnes du rapport, identique a l'exemple de l'enonce. */
    private static final int COLONNE = 14;

    public MariageService() {
        super(Mariage.class);
    }

    /** @return les mariages de l'homme, epouse chargee via {@code JOIN FETCH}. */
    public List<Mariage> findByHomme(int hommeId) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Mariage.findByHomme", Mariage.class)
                    .setParameter("hommeId", hommeId)
                    .list();
        }
    }

    /** @return les mariages de l'homme toujours en cours. */
    public List<Mariage> findEnCours(int hommeId) {
        List<Mariage> resultat = new ArrayList<>();
        for (Mariage mariage : findByHomme(hommeId)) {
            if (mariage.isEnCours()) {
                resultat.add(mariage);
            }
        }
        return resultat;
    }

    /** @return les mariages echoues (divorces) de l'homme. */
    public List<Mariage> findEchoues(int hommeId) {
        List<Mariage> resultat = new ArrayList<>();
        for (Mariage mariage : findByHomme(hommeId)) {
            if (!mariage.isEnCours()) {
                resultat.add(mariage);
            }
        }
        return resultat;
    }

    /**
     * Enregistre un mariage entre un homme et une femme.
     *
     * @param dateFin date de divorce, ou {@code null} si le mariage est en cours.
     * @return le mariage cree, ou {@code null} si l'homme ou la femme est introuvable.
     */
    public Mariage marier(int hommeId, int femmeId, Date dateDebut, Date dateFin, int nombreEnfants) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            Homme homme = session.get(Homme.class, hommeId);
            Femme femme = session.get(Femme.class, femmeId);
            if (homme == null || femme == null) {
                transaction.rollback();
                return null;
            }
            Mariage mariage = new Mariage(dateDebut, dateFin, nombreEnfants);
            homme.ajouterMariage(mariage);
            femme.ajouterMariage(mariage);
            session.save(mariage);
            transaction.commit();
            return mariage;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /** Imprime le nombre de mariages d'un homme, en cours et echoues. */
    public void afficherStatistiques(int hommeId) {
        println("Mariages en cours : " + findEnCours(hommeId).size());
        println("Mariages échoués  : " + findEchoues(hommeId).size());
    }

    /**
     * Imprime tous les mariages d'un homme avec leurs details, au format de
     * l'enonce :
     *
     * <pre>
     * Nom : SAFI SAID
     * Mariages En Cours :
     * 1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4
     *
     * Mariages échoués :
     * 1. Femme : KARIMA ALAMI  Date Début : 03/09/1989
     * Date Fin : 03/09/1990    Nbr Enfants : 0
     * </pre>
     */
    public void afficherMariagesDunHomme(int hommeId) {
        Homme homme = new HommeService().findById(hommeId);
        if (homme == null) {
            println("Homme introuvable : " + hommeId);
            return;
        }
        println("Nom : " + homme.getNomComplet());

        println("Mariages En Cours :");
        int numero = 1;
        for (Mariage mariage : findEnCours(hommeId)) {
            println(numero++ + ". " + ligneMariageEnCours(mariage));
        }

        println("");
        println("Mariages échoués :");
        numero = 1;
        for (Mariage mariage : findEchoues(hommeId)) {
            println(numero++ + ". " + ligneMariageEchoue(mariage));
        }
    }

    /** Premiere ligne d'un mariage en cours : {@code Femme : ... Date Début : ... Nbr Enfants : ...}. */
    static String ligneMariageEnCours(Mariage mariage) {
        return "Femme : " + colonne(mariage.getFemme().getNomComplet())
                + "Date Début : " + colonne(Formats.dateCourte(mariage.getDateDebut()))
                + "Nbr Enfants : " + mariage.getNombreEnfants();
    }

    /** Lignes d'un mariage echoue : la date de fin occupe la seconde ligne. */
    static String ligneMariageEchoue(Mariage mariage) {
        return "Femme : " + colonne(mariage.getFemme().getNomComplet())
                + "Date Début : " + Formats.dateCourte(mariage.getDateDebut()) + System.lineSeparator()
                + "Date Fin : " + colonne(Formats.dateCourte(mariage.getDateFin()))
                + "Nbr Enfants : " + mariage.getNombreEnfants();
    }

    /** Complete une valeur avec des espaces jusqu'a la largeur de colonne. */
    private static String colonne(String valeur) {
        String texte = valeur == null ? "" : valeur;
        StringBuilder resultat = new StringBuilder(texte);
        while (resultat.length() < COLONNE) {
            resultat.append(' ');
        }
        return resultat.toString();
    }
}
