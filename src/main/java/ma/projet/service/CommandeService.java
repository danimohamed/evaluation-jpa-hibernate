package ma.projet.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import ma.projet.classes.Commande;
import ma.projet.util.Formats;

/** Service de l'entite {@link Commande} (exercice 1). */
public class CommandeService extends AbstractService<Commande> {

    public CommandeService() {
        super(Commande.class);
    }

    /** @return les commandes passees entre deux dates incluses. */
    public List<Commande> findByDates(Date debut, Date fin) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT c FROM Commande c WHERE c.date BETWEEN :debut AND :fin "
                                    + "ORDER BY c.id",
                            Commande.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    /** Imprime la liste des commandes de la periode. */
    public void afficherCommandesEntreDates(Date debut, Date fin) {
        List<Commande> commandes = findByDates(debut, fin);
        println("Commandes entre le " + Formats.dateCourte(debut)
                + " et le " + Formats.dateCourte(fin) + " :");
        printf("%-6s%s%n", "Num", "Date");
        for (Commande commande : commandes) {
            printf("%-6s%s%n", String.valueOf(commande.getId()), Formats.dateLongue(commande.getDate()));
        }
    }
}
