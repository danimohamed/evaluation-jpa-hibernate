package ma.projet.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.util.Formats;

/**
 * Service de l'entite {@link Projet} (exercice 2).
 *
 * <p>Deux listes de taches sont exposees : les taches <em>planifiees</em>
 * (date de debut prevue renseignee) et les taches <em>realisees</em>
 * (dates reelles renseignees), cette derniere etant imprimee au format exact
 * demande par l'enonce.</p>
 */
public class ProjetService extends AbstractService<Projet> {

    /** En-tete du tableau des taches realisees. */
    private static final String ENTETE_TACHES = "%-4s%-15s%-20s%s%n";

    public ProjetService() {
        super(Projet.class);
    }

    /** @return les taches planifiees du projet, par date de debut prevue. */
    public List<Tache> findTachesPlanifiees(int projetId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT t FROM Tache t WHERE t.projet.id = :projetId "
                                    + "AND t.dateDebutPrevue IS NOT NULL "
                                    + "ORDER BY t.dateDebutPrevue ASC, t.id ASC",
                            Tache.class)
                    .setParameter("projetId", projetId)
                    .list();
        }
    }

    /** @return les taches realisees du projet, par date de debut reelle. */
    public List<Tache> findTachesRealisees(int projetId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT t FROM Tache t WHERE t.projet.id = :projetId "
                                    + "AND t.dateDebutReelle IS NOT NULL "
                                    + "ORDER BY t.dateDebutReelle ASC, t.id ASC",
                            Tache.class)
                    .setParameter("projetId", projetId)
                    .list();
        }
    }

    /**
     * Imprime les taches realisees d'un projet au format de l'enonce :
     *
     * <pre>
     * Projet : 4      Nom : Gestion de stock     Date début : 14 Janvier 2013
     * Liste des tâches:
     * Num Nom            Date Début Réelle   Date Fin Réelle
     * 12  Analyse        10/02/2013          20/02/2013
     * </pre>
     */
    public void afficherTachesRealisees(int projetId) {
        Projet projet = findById(projetId);
        if (projet == null) {
            println("Projet introuvable : " + projetId);
            return;
        }
        println("Projet : " + projet.getId() + "      Nom : " + projet.getNom()
                + "     Date début : " + Formats.dateLongue(projet.getDateDebut()));
        println("Liste des tâches:");
        printf(ENTETE_TACHES, "Num", "Nom", "Date Début Réelle", "Date Fin Réelle");
        for (Tache tache : findTachesRealisees(projetId)) {
            printf(ENTETE_TACHES, String.valueOf(tache.getId()), tache.getNom(),
                    Formats.dateCourte(tache.getDateDebutReelle()),
                    Formats.dateCourte(tache.getDateFinReelle()));
        }
    }

    /** Imprime les taches planifiees d'un projet, avec leurs dates prevues. */
    public void afficherTachesPlanifiees(int projetId) {
        Projet projet = findById(projetId);
        if (projet == null) {
            println("Projet introuvable : " + projetId);
            return;
        }
        println("Projet : " + projet.getId() + "      Nom : " + projet.getNom()
                + "     Date début : " + Formats.dateLongue(projet.getDateDebut()));
        println("Liste des tâches planifiées:");
        printf(ENTETE_TACHES, "Num", "Nom", "Date Début Prévue", "Date Fin Prévue");
        for (Tache tache : findTachesPlanifiees(projetId)) {
            printf(ENTETE_TACHES, String.valueOf(tache.getId()), tache.getNom(),
                    Formats.dateCourte(tache.getDateDebutPrevue()),
                    Formats.dateCourte(tache.getDateFinPrevue()));
        }
    }

    /** Imprime le cout total des taches realisees du projet. */
    public void afficherCoutRealise(int projetId) {
        List<Tache> taches = findTachesRealisees(projetId);
        double total = 0;
        for (Tache tache : taches) {
            total += tache.getPrix();
        }
        println("Coût total des tâches réalisées : " + Formats.montant(total) + " DH");
    }

    /** @return la date de debut la plus ancienne parmi les projets, ou {@code null}. */
    public Date findPremiereDateDebut() {
        try (Session session = openSession()) {
            return session.createQuery("SELECT MIN(p.dateDebut) FROM Projet p", Date.class)
                    .uniqueResult();
        }
    }
}
