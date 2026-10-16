package ma.projet.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommande;
import ma.projet.classes.Produit;
import ma.projet.util.Formats;

/**
 * Service de l'entite {@link Produit} (exercice 1).
 *
 * <p>Les trois listes exigees par l'enonce sont couvertes :
 * produits d'une categorie, produits commandes entre deux dates, et produits
 * d'une commande donnee. La quatrieme requete demandee (prix superieur a
 * 100 DH) s'appuie sur la requete nommee {@code Produit.findPrixSuperieurA}.</p>
 */
public class ProduitService extends AbstractService<Produit> {

    /** En-tete du tableau des produits d'une commande, tel qu'attendu par l'enonce. */
    private static final String ENTETE_PRODUITS = "%-12s%-8s%s%n";

    public ProduitService() {
        super(Produit.class);
    }

    /** @return les produits de la categorie, via {@code Produit.findByCategorie}. */
    public List<Produit> findByCategorie(int categorieId) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("categorieId", categorieId)
                    .list();
        }
    }

    /** @return les produits dont le prix depasse {@code prix}, via la requete nommee. */
    public List<Produit> findByPrixSuperieurA(double prix) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Produit.findPrixSuperieurA", Produit.class)
                    .setParameter("prix", prix)
                    .list();
        }
    }

    /** @return les produits commandes sur la periode, via {@code Produit.findCommandesEntreDates}. */
    public List<Produit> findByCommandesEntreDates(Date debut, Date fin) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Produit.findCommandesEntreDates", Produit.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    /** @return les lignes de la commande, produit charge en une seule requete. */
    public List<LigneCommande> findLignesDuneCommande(int commandeId) {
        try (Session session = openSession()) {
            return session.createQuery(
                            "SELECT lc FROM LigneCommande lc JOIN FETCH lc.produit "
                                    + "WHERE lc.commande.id = :commandeId ORDER BY lc.id",
                            LigneCommande.class)
                    .setParameter("commandeId", commandeId)
                    .list();
        }
    }

    /**
     * Imprime le detail d'une commande au format de l'enonce :
     *
     * <pre>
     * Commande : 4     Date : 14 Mars 2013
     * Liste des produits :
     * Référence   Prix    Quantité
     * ES12        120 DH  7
     * </pre>
     */
    public void afficherProduitsDuneCommande(int commandeId) {
        Commande commande;
        try (Session session = openSession()) {
            commande = session.get(Commande.class, commandeId);
        }
        if (commande == null) {
            println("Commande introuvable : " + commandeId);
            return;
        }
        println("Commande : " + commande.getId() + "     Date : " + Formats.dateLongue(commande.getDate()));
        println("Liste des produits :");
        printf(ENTETE_PRODUITS, "Référence", "Prix", "Quantité");
        for (LigneCommande ligne : findLignesDuneCommande(commandeId)) {
            printf(ENTETE_PRODUITS,
                    ligne.getProduit().getReference(),
                    Formats.montant(ligne.getProduit().getPrix()) + " DH",
                    String.valueOf(ligne.getQuantite()));
        }
    }

    /** Imprime les produits dont le prix depasse {@code prix}. */
    public void afficherProduitsPrixSuperieurA(double prix) {
        List<Produit> produits = findByPrixSuperieurA(prix);
        println("Produits dont le prix dépasse " + Formats.montant(prix) + " DH :");
        printf(ENTETE_PRODUITS, "Référence", "Prix", "");
        for (Produit produit : produits) {
            printf(ENTETE_PRODUITS, produit.getReference(),
                    Formats.montant(produit.getPrix()) + " DH", "");
        }
    }

    /** Imprime les produits commandes entre deux dates. */
    public void afficherProduitsCommandesEntreDates(Date debut, Date fin) {
        List<Produit> produits = findByCommandesEntreDates(debut, fin);
        println("Produits commandés entre le " + Formats.dateCourte(debut)
                + " et le " + Formats.dateCourte(fin) + " :");
        printf(ENTETE_PRODUITS, "Référence", "Prix", "");
        for (Produit produit : produits) {
            printf(ENTETE_PRODUITS, produit.getReference(),
                    Formats.montant(produit.getPrix()) + " DH", "");
        }
    }
}
