package ma.projet.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Tache;
import ma.projet.util.Formats;

/**
 * Service de l'entite porteuse {@link EmployeTache} (exercice 2) : elle relie un
 * employe a une tache et porte la periode d'affectation.
 */
public class EmployeTacheService extends AbstractService<EmployeTache> {

    public EmployeTacheService() {
        super(EmployeTache.class);
    }

    /** @return les affectations de l'employe, tache chargee en une seule requete. */
    public List<EmployeTache> findByEmploye(int employeId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT et FROM EmployeTache et JOIN FETCH et.tache "
                                    + "WHERE et.employe.id = :employeId ORDER BY et.id",
                            EmployeTache.class)
                    .setParameter("employeId", employeId)
                    .list();
        }
    }

    /** @return les affectations de la tache, employe charge en une seule requete. */
    public List<EmployeTache> findByTache(int tacheId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT et FROM EmployeTache et JOIN FETCH et.employe "
                                    + "WHERE et.tache.id = :tacheId ORDER BY et.id",
                            EmployeTache.class)
                    .setParameter("tacheId", tacheId)
                    .list();
        }
    }

    /**
     * Affecte un employe a une tache sur une periode.
     *
     * <p>L'entite porteuse est construite puis sauvegardee dans une seule
     * transaction, afin que les deux cotes de l'association restent coherents.</p>
     *
     * @return l'affectation creee, ou {@code null} si l'employe ou la tache n'existe pas.
     */
    public EmployeTache affecter(int employeId, int tacheId, Date debut, Date fin) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            Employe employe = session.get(Employe.class, employeId);
            Tache tache = session.get(Tache.class, tacheId);
            if (employe == null || tache == null) {
                transaction.rollback();
                return null;
            }
            EmployeTache affectation = new EmployeTache(employe, tache, debut, fin);
            session.save(affectation);
            transaction.commit();
            return affectation;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /** Imprime la charge de travail d'un employe : une ligne par affectation. */
    public void afficherAffectations(int employeId) {
        Employe employe = new EmployeService().findById(employeId);
        if (employe == null) {
            println("Employé introuvable : " + employeId);
            return;
        }
        List<EmployeTache> affectations = findByEmploye(employeId);
        println("Affectations de " + employe.getNomComplet() + " :");
        printf("%-4s%-20s%-12s%-12s%s%n", "Num", "Tâche", "Début", "Fin", "Réalisée");
        for (EmployeTache affectation : affectations) {
            printf("%-4s%-20s%-12s%-12s%s%n",
                    String.valueOf(affectation.getTache().getId()),
                    affectation.getTache().getNom(),
                    Formats.dateCourte(affectation.getDateDebut()),
                    Formats.dateCourte(affectation.getDateFin()),
                    affectation.getTache().isRealisee() ? "oui" : "non");
        }
    }
}
