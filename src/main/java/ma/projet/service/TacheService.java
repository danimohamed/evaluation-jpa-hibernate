package ma.projet.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Tache;
import ma.projet.util.Formats;

/**
 * Service de l'entite {@link Tache} (exercice 2).
 *
 * <p>Deux requetes nommees sont exigees par l'enonce :
 * {@code Tache.findPrixSuperieurA} (taches a plus de 1000 DH) et
 * {@code Tache.findRealiseesEntreDates}.</p>
 */
public class TacheService extends AbstractService<Tache> {

    public TacheService() {
        super(Tache.class);
    }

    /** @return les taches dont le prix depasse {@code prix}, via la requete nommee. */
    public List<Tache> findByPrixSuperieurA(double prix) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Tache.findPrixSuperieurA", Tache.class)
                    .setParameter("prix", prix)
                    .list();
        }
    }

    /** @return les taches realisees sur la periode, via la requete nommee. */
    public List<Tache> findRealiseesEntreDates(Date debut, Date fin) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Tache.findRealiseesEntreDates", Tache.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    /** @return les affectations de la tache, employe charge en une seule requete. */
    public List<EmployeTache> findAffectations(int tacheId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT et FROM EmployeTache et JOIN FETCH et.employe "
                                    + "WHERE et.tache.id = :tacheId ORDER BY et.id",
                            EmployeTache.class)
                    .setParameter("tacheId", tacheId)
                    .list();
        }
    }

    /** @return les employes affectes a la tache. */
    public List<Employe> findEmployes(int tacheId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT et.employe FROM EmployeTache et "
                                    + "WHERE et.tache.id = :tacheId ORDER BY et.employe.nom",
                            Employe.class)
                    .setParameter("tacheId", tacheId)
                    .list();
        }
    }

    /** Imprime les taches dont le prix depasse {@code prix}. */
    public void afficherTachesPrixSuperieurA(double prix) {
        List<Tache> taches = findByPrixSuperieurA(prix);
        println("Tâches dont le prix dépasse " + Formats.montant(prix) + " DH :");
        printf("%-4s%-20s%-12s%s%n", "Num", "Nom", "Prix", "Projet");
        for (Tache tache : taches) {
            printf("%-4s%-20s%-12s%s%n", String.valueOf(tache.getId()), tache.getNom(),
                    Formats.montant(tache.getPrix()) + " DH", tache.getProjet().getNom());
        }
    }

    /** Imprime les taches realisees entre deux dates. */
    public void afficherTachesRealiseesEntreDates(Date debut, Date fin) {
        List<Tache> taches = findRealiseesEntreDates(debut, fin);
        println("Tâches réalisées entre le " + Formats.dateCourte(debut)
                + " et le " + Formats.dateCourte(fin) + " :");
        printf("%-4s%-20s%-20s%s%n", "Num", "Nom", "Début Réelle", "Fin Réelle");
        for (Tache tache : taches) {
            printf("%-4s%-20s%-20s%s%n", String.valueOf(tache.getId()), tache.getNom(),
                    Formats.dateCourte(tache.getDateDebutReelle()),
                    Formats.dateCourte(tache.getDateFinReelle()));
        }
    }

    /** Imprime les employes affectes a une tache, avec leur periode d'affectation. */
    public void afficherEmployes(int tacheId) {
        List<EmployeTache> affectations = findAffectations(tacheId);
        println("Employés affectés à la tâche " + tacheId + " :");
        printf("%-4s%-30s%-12s%s%n", "Num", "Employé", "Début", "Fin");
        for (EmployeTache affectation : affectations) {
            printf("%-4s%-30s%-12s%s%n", String.valueOf(affectation.getEmploye().getId()),
                    affectation.getEmploye().getNomComplet(),
                    Formats.dateCourte(affectation.getDateDebut()),
                    Formats.dateCourte(affectation.getDateFin()));
        }
    }
}
