package ma.projet.classes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Produit du magasin informatique (exercice 1).
 *
 * <p>La requete nommee {@code Produit.findPrixSuperieurA} est explicitement
 * exigee par l'enonce.</p>
 */
@Entity
@Table(name = "produit")
@NamedQueries({
        @NamedQuery(name = "Produit.findPrixSuperieurA",
                query = "SELECT p FROM Produit p WHERE p.prix > :prix ORDER BY p.id"),
        @NamedQuery(name = "Produit.findByCategorie",
                query = "SELECT p FROM Produit p WHERE p.categorie.id = :categorieId ORDER BY p.id"),
        @NamedQuery(name = "Produit.findCommandesEntreDates",
                query = "SELECT DISTINCT lc.produit FROM LigneCommande lc "
                        + "WHERE lc.commande.date BETWEEN :debut AND :fin ORDER BY lc.produit.reference")
})
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reference", length = 30, nullable = false, unique = true)
    private String reference;

    /** Prix unitaire en dirhams. */
    @Column(name = "prix", nullable = false)
    private double prix;

    @ManyToOne(fetch = FetchType.LAZY)
    private Categorie categorie;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneCommande> lignes = new ArrayList<>();

    public Produit() {
    }

    public Produit(String reference, double prix) {
        this.reference = reference;
        this.prix = prix;
    }

    public Produit(String reference, double prix, Categorie categorie) {
        this.reference = reference;
        this.prix = prix;
        this.categorie = categorie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    @Override
    public String toString() {
        return "Produit{id=" + id + ", reference=" + reference + ", prix=" + prix + "}";
    }
}
