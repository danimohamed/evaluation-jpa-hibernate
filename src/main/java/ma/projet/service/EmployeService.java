package ma.projet.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import ma.projet.classes.Employe;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.util.Formats;

/**
 * Service de l'entite {@link Employe} (exercice 2).
 *
 * <p>Deux rapports sont exiges par l'enonce : la liste des taches realisees par
 * un employe et la liste des projets qu'il gere.</p>
 */
public class EmployeService extends AbstractService<Employe> {

    public EmployeService() {
        super(Employe.class);
    }

    /** @return les taches realisees par l'employe, via la requete nommee. */
    public List<Tache> findTachesRealisees(int employeId) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Employe.findTachesRealisees", Tache.class)
                    .setParameter("employeId", employeId)
                    .list();
        }
    }

    /** @return les projets geres par l'employe, via la requete nommee. */
    public List<Projet> findProjetsGeres(int employeId) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Employe.findProjetsGeres", Projet.class)
                    .setParameter("employeId", employeId)
                    .list();
        }
    }

    /** @return les taches realisees par l'employe sur une periode. */
    public List<Tache> findTachesRealiseesEntreDates(int employeId, Date debut, Date fin) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT et.tache FROM EmployeTache et "
                                    + "WHERE et.employe.id = :employeId "
                                    + "AND et.tache.dateDebutReelle BETWEEN :debut AND :fin "
                                    + "ORDER BY et.tache.nom",
                            Tache.class)
                    .setParameter("employeId", employeId)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    /** Imprime la liste des taches realisees par un employe. */
    public void afficherTachesRealisees(int employeId) {
        Employe employe = findById(employeId);
        if (employe == null) {
            println("Employé introuvable : " + employeId);
            return;
        }
        List<Tache> taches = findTachesRealisees(employeId);
        println("Tâches réalisées par " + employe.getNomComplet() + " :");
        printf("%-4s%-20s%-12s%-14s%-14s%s%n", "Num", "Nom", "Prix", "Début Réelle",
                "Fin Réelle", "Projet");
        for (Tache tache : taches) {
            printf("%-4s%-20s%-12s%-14s%-14s%s%n", String.valueOf(tache.getId()), tache.getNom(),
                    Formats.montant(tache.getPrix()) + " DH",
                    Formats.dateCourte(tache.getDateDebutReelle()),
                    Formats.dateCourte(tache.getDateFinReelle()),
                    tache.getProjet() == null ? "-" : tache.getProjet().getNom());
        }
    }

    /** Imprime la liste des projets geres par un employe. */
    public void afficherProjetsGeres(int employeId) {
        Employe employe = findById(employeId);
        if (employe == null) {
            println("Employé introuvable : " + employeId);
            return;
        }
        List<Projet> projets = findProjetsGeres(employeId);
        println("Projets gérés par " + employe.getNomComplet() + " :");
        printf("%-4s%-25s%s%n", "Num", "Nom", "Date Début");
        for (Projet projet : projets) {
            printf("%-4s%-25s%s%n", String.valueOf(projet.getId()), projet.getNom(),
                    Formats.dateLongue(projet.getDateDebut()));
        }
    }

    /** Imprime les taches realisees par un employe entre deux dates. */
    public void afficherTachesRealiseesEntreDates(int employeId, Date debut, Date fin) {
        List<Tache> taches = findTachesRealiseesEntreDates(employeId, debut, fin);
        println("Tâches réalisées entre le " + Formats.dateCourte(debut)
                + " et le " + Formats.dateCourte(fin) + " :");
        for (Tache tache : taches) {
            println("  " + tache.getId() + " - " + tache.getNom()
                    + " (" + Formats.dateCourte(tache.getDateDebutReelle())
                    + " -> " + Formats.dateCourte(tache.getDateFinReelle()) + ")");
        }
    }
}
