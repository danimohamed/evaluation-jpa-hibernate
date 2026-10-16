package ma.projet.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommande;
import ma.projet.classes.Produit;

/** Service de l'entite {@link LigneCommande} (exercice 1). */
public class LigneCommandeService extends AbstractService<LigneCommande> {

    public LigneCommandeService() {
        super(LigneCommande.class);
    }

    /** @return les lignes de la commande, produit charge en une seule requete. */
    public List<LigneCommande> findByCommande(int commandeId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT lc FROM LigneCommande lc JOIN FETCH lc.produit "
                                    + "WHERE lc.commande.id = :commandeId ORDER BY lc.id",
                            LigneCommande.class)
                    .setParameter("commandeId", commandeId)
                    .list();
        }
    }

    /** @return les lignes portant sur le produit donne. */
    public List<LigneCommande> findByProduit(int produitId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT lc FROM LigneCommande lc WHERE lc.produit.id = :produitId "
                                    + "ORDER BY lc.id",
                            LigneCommande.class)
                    .setParameter("produitId", produitId)
                    .list();
        }
    }

    /**
     * Ajoute une ligne a une commande existante en renseignant les deux cotes de
     * l'association dans la meme transaction.
     *
     * @return {@code true} si la ligne a ete enregistree
     */
    public boolean ajouterLigne(int commandeId, int produitId, int quantite) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            Commande commande = session.get(Commande.class, commandeId);
            Produit produit = session.get(Produit.class, produitId);
            if (commande == null || produit == null) {
                transaction.rollback();
                return false;
            }
            LigneCommande ligne = new LigneCommande(quantite, produit);
            commande.ajouterLigne(ligne);
            session.save(ligne);
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Echec de ajouterLigne : " + e.getMessage());
            return false;
        }
    }
}
