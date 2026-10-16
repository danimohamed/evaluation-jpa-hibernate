package ma.projet.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import ma.projet.beans.Femme;
import ma.projet.util.Formats;

/**
 * Service de l'entite {@link Femme} (exercice 3).
 *
 * <p>Regroupe les trois lectures de l'enonce portant sur les femmes : le nombre
 * d'enfants d'une femme entre deux dates (requete nommee <strong>native</strong>
 * {@code Mariage.nombreEnfantsFemmeEntreDates}), la liste des femmes mariees au
 * moins deux fois et la femme la plus agee.</p>
 */
public class FemmeService extends AbstractService<Femme> {

    /** Seuil de l'enonce : femmes mariees au moins deux fois. */
    public static final int MARIAGES_MINIMUM = 2;

    public FemmeService() {
        super(Femme.class);
    }

    /**
     * Somme des enfants nes d'une femme dont le mariage a debute entre deux
     * dates, calculee par la requete nommee native
     * {@code Mariage.nombreEnfantsFemmeEntreDates}.
     *
     * @return le nombre d'enfants, {@code 0} si aucun mariage sur la periode.
     */
    public int nombreEnfantsEntreDates(int femmeId, Date debut, Date fin) {
        try (Session session = openSession()) {
            Object resultat = session.createNamedQuery("Mariage.nombreEnfantsFemmeEntreDates")
                    .setParameter("femmeId", femmeId)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .uniqueResult();
            return resultat == null ? 0 : ((Number) resultat).intValue();
        }
    }

    /** @return les femmes mariees au moins deux fois. */
    public List<Femme> findMarieesAuMoinsDeuxFois() {
        try (Session session = openSession()) {
            return session.createNamedQuery("Femme.findMarieesAuMoinsDeuxFois", Femme.class)
                    .setParameter("nombre", (long) MARIAGES_MINIMUM)
                    .list();
        }
    }

    /** @return la femme la plus agee, ou {@code null} si aucune n'est enregistree. */
    public Femme findPlusAgee() {
        try (Session session = openSession()) {
            List<Femme> femmes = session.createNamedQuery("Femme.findPlusAgee", Femme.class)
                    .setMaxResults(1)
                    .list();
            return femmes.isEmpty() ? null : femmes.get(0);
        }
    }

    /** Imprime toutes les femmes enregistrees. */
    public void afficherListe() {
        List<Femme> femmes = findAll();
        println("Liste des femmes :");
        int numero = 1;
        for (Femme femme : femmes) {
            println(numero++ + ". " + femme.getNomComplet()
                    + " (née le " + Formats.dateCourte(femme.getDateNaissance()) + ")");
        }
    }

    /** Imprime la femme la plus agee. */
    public void afficherPlusAgee() {
        Femme femme = findPlusAgee();
        if (femme == null) {
            println("Aucune femme enregistrée.");
            return;
        }
        println("Femme la plus âgée : " + femme.getNomComplet()
                + " (née le " + Formats.dateCourte(femme.getDateNaissance()) + ")");
    }

    /** Imprime les femmes mariees au moins deux fois. */
    public void afficherMarieesAuMoinsDeuxFois() {
        println("Femmes mariées au moins " + MARIAGES_MINIMUM + " fois :");
        int numero = 1;
        for (Femme femme : findMarieesAuMoinsDeuxFois()) {
            println(numero++ + ". " + femme.getNomComplet());
        }
    }

    /** Imprime le nombre d'enfants d'une femme nes entre deux dates. */
    public void afficherNombreEnfantsEntreDates(int femmeId, Date debut, Date fin) {
        Femme femme = findById(femmeId);
        if (femme == null) {
            println("Femme introuvable : " + femmeId);
            return;
        }
        println("Nombre d'enfants de " + femme.getNomComplet() + " nés entre le "
                + Formats.dateCourte(debut) + " et le " + Formats.dateCourte(fin)
                + " : " + nombreEnfantsEntreDates(femmeId, debut, fin));
    }
}
