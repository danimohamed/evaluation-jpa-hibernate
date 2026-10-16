package ma.projet.classes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Ligne d'une commande : quantite commandee d'un produit donne (exercice 1).
 * C'est la table d'association porteuse entre {@link Commande} et {@link Produit}.
 */
@Entity
@Table(name = "ligne_commande")
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    private Produit produit;

    public LigneCommande() {
    }

    public LigneCommande(int quantite, Produit produit) {
        this.quantite = quantite;
        this.produit = produit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    @Override
    public String toString() {
        return "LigneCommande{id=" + id + ", quantite=" + quantite + "}";
    }
}
