package ma.projet.service;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.Session;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.util.Formats;

/**
 * Service de l'entite {@link Homme} (exercice 3).
 *
 * <p>Deux fonctionnalites de l'enonce y sont implementees : la liste des epouses
 * d'un homme entre deux dates (requete nommee
 * {@code Homme.findEpousesEntreDates}) et le comptage, <strong>via l'API
 * Criteria</strong>, des hommes maries a quatre femmes entre deux dates.</p>
 */
public class HommeService extends AbstractService<Homme> {

    /** Nombre d'epouses recherche par la requete Criteria de l'enonce. */
    public static final int QUATRE_EPOUSES = 4;

    public HommeService() {
        super(Homme.class);
    }

    /** @return les epouses de l'homme dont le mariage a debute sur la periode. */
    public List<Femme> findEpousesEntreDates(int hommeId, Date debut, Date fin) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Homme.findEpousesEntreDates", Femme.class)
                    .setParameter("hommeId", hommeId)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    /** @return les mariages de l'homme avec leur epouse, via {@code JOIN FETCH}. */
    public List<Mariage> findMariagesAvecDetails(int hommeId) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Homme.findMariagesAvecDetails", Mariage.class)
                    .setParameter("hommeId", hommeId)
                    .list();
        }
    }

    /**
     * Compte les hommes maries a quatre femmes entre deux dates, en utilisant
     * l'API Criteria : une sous-requete compte les epouses distinctes de chaque
     * homme sur la periode, et seuls les hommes dont ce total vaut quatre sont
     * retenus.
     *
     * @return le nombre d'hommes concernes.
     */
    public long compterHommesMariesAQuatreFemmesEntreDates(Date debut, Date fin) {
        try (Session session = openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> requete = builder.createQuery(Long.class);
            Root<Mariage> mariage = requete.from(Mariage.class);

            Subquery<Long> epouses = requete.subquery(Long.class);
            Root<Mariage> sousMariage = epouses.from(Mariage.class);
            epouses.select(builder.countDistinct(sousMariage.get("femme")));
            epouses.where(
                    builder.equal(sousMariage.get("homme"), mariage.get("homme")),
                    builder.between(sousMariage.<Date>get("dateDebut"), debut, fin));

            requete.select(builder.countDistinct(mariage.get("homme")));
            requete.where(
                    builder.between(mariage.<Date>get("dateDebut"), debut, fin),
                    builder.equal(epouses, (long) QUATRE_EPOUSES));

            Long resultat = session.createQuery(requete).uniqueResult();
            return resultat == null ? 0L : resultat;
        }
    }

    /** Imprime les epouses d'un homme dont le mariage a debute sur la periode. */
    public void afficherEpousesEntreDates(int hommeId, Date debut, Date fin) {
        Homme homme = findById(hommeId);
        if (homme == null) {
            println("Homme introuvable : " + hommeId);
            return;
        }
        List<Femme> epouses = findEpousesEntreDates(hommeId, debut, fin);
        println("Épouses de " + homme.getNomComplet() + " mariées entre le "
                + Formats.dateCourte(debut) + " et le " + Formats.dateCourte(fin) + " :");
        for (Femme epouse : epouses) {
            println("  " + epouse.getNomComplet() + " (née le "
                    + Formats.dateCourte(epouse.getDateNaissance()) + ")");
        }
        if (epouses.isEmpty()) {
            println("  Aucune");
        }
    }

    /** Imprime le nombre d'hommes maries a quatre femmes entre deux dates. */
    public void afficherHommesMariesAQuatreFemmesEntreDates(Date debut, Date fin) {
        long nombre = compterHommesMariesAQuatreFemmesEntreDates(debut, fin);
        println("Nombre d'hommes mariés à quatre femmes entre le "
                + Formats.dateCourte(debut) + " et le " + Formats.dateCourte(fin)
                + " : " + nombre);
    }
}
