package ma.projet.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Tache d'un projet (exercice 2).
 *
 * <p>L'enonce exige une requete nommee pour les taches dont le prix depasse un
 * seuil : {@code Tache.findPrixSuperieurA}.</p>
 */
@Entity
@Table(name = "tache")
@NamedQueries({
        @NamedQuery(name = "Tache.findPrixSuperieurA",
                query = "SELECT t FROM Tache t JOIN FETCH t.projet WHERE t.prix > :prix ORDER BY t.id"),
        @NamedQuery(name = "Tache.findByProjet",
                query = "SELECT t FROM Tache t WHERE t.projet.id = :projetId ORDER BY t.id"),
        @NamedQuery(name = "Tache.findRealiseesEntreDates",
                query = "SELECT t FROM Tache t WHERE t.dateDebutReelle BETWEEN :debut AND :fin ORDER BY t.id")
})
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", length = 80, nullable = false)
    private String nom;

    /** Date de debut planifiee. */
    @Temporal(TemporalType.DATE)
    @Column(name = "date_debut_prevue")
    private Date dateDebutPrevue;

    /** Date de fin planifiee. */
    @Temporal(TemporalType.DATE)
    @Column(name = "date_fin_prevue")
    private Date dateFinPrevue;

    /** Date de debut reellement constatee (nulle si la tache n'est pas realisee). */
    @Temporal(TemporalType.DATE)
    @Column(name = "date_debut_reelle")
    private Date dateDebutReelle;

    /** Date de fin reellement constatee (nulle si la tache n'est pas terminee). */
    @Temporal(TemporalType.DATE)
    @Column(name = "date_fin_reelle")
    private Date dateFinReelle;

    /** Prix de la tache en dirhams. */
    @Column(name = "prix", nullable = false)
    private double prix;

    @ManyToOne(fetch = FetchType.LAZY)
    private Projet projet;

    @OneToMany(mappedBy = "tache", fetch = FetchType.LAZY)
    private List<EmployeTache> affectations = new ArrayList<>();

    public Tache() {
    }

    public Tache(String nom, double prix) {
        this.nom = nom;
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateDebutPrevue() {
        return dateDebutPrevue;
    }

    public void setDateDebutPrevue(Date dateDebutPrevue) {
        this.dateDebutPrevue = dateDebutPrevue;
    }

    public Date getDateFinPrevue() {
        return dateFinPrevue;
    }

    public void setDateFinPrevue(Date dateFinPrevue) {
        this.dateFinPrevue = dateFinPrevue;
    }

    public Date getDateDebutReelle() {
        return dateDebutReelle;
    }

    public void setDateDebutReelle(Date dateDebutReelle) {
        this.dateDebutReelle = dateDebutReelle;
    }

    public Date getDateFinReelle() {
        return dateFinReelle;
    }

    public void setDateFinReelle(Date dateFinReelle) {
        this.dateFinReelle = dateFinReelle;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    public List<EmployeTache> getAffectations() {
        return affectations;
    }

    public void setAffectations(List<EmployeTache> affectations) {
        this.affectations = affectations;
    }

    /** @return {@code true} si la tache possede un debut et une fin reels. */
    public boolean isRealisee() {
        return dateDebutReelle != null && dateFinReelle != null;
    }

    @Override
    public String toString() {
        return "Tache{id=" + id + ", nom=" + nom + ", prix=" + prix + "}";
    }
}
