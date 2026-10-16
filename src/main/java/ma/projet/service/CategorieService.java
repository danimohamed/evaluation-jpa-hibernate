package ma.projet.service;

import java.util.List;

import org.hibernate.Session;

import ma.projet.classes.Categorie;
import ma.projet.classes.Produit;
import ma.projet.util.Formats;

/** Service de l'entite {@link Categorie} (exercice 1). */
public class CategorieService extends AbstractService<Categorie> {

    public CategorieService() {
        super(Categorie.class);
    }

    /** @return les produits rattaches a la categorie, via la requete nommee dediee. */
    public List<Produit> findProduits(int categorieId) {
        try (Session session = openSession()) {
            return session.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("categorieId", categorieId)
                    .list();
        }
    }

    /** Imprime le catalogue d'une categorie : une ligne par produit. */
    public void afficherProduits(int categorieId) {
        Categorie categorie = findById(categorieId);
        if (categorie == null) {
            println("Categorie introuvable : " + categorieId);
            return;
        }
        List<Produit> produits = findProduits(categorieId);
        println("Categorie : " + categorie.getNom() + " (" + produits.size() + " produit(s))");
        printf("%-12s%-10s%n", "Référence", "Prix");
        for (Produit produit : produits) {
            printf("%-12s%-10s%n", produit.getReference(),
                    Formats.montant(produit.getPrix()) + " DH");
        }
    }
}
