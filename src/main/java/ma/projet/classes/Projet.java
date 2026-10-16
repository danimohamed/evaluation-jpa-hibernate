package ma.projet.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** Projet du bureau d'etudes (exercice 2). */
@Entity
@Table(name = "projet")
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", length = 80, nullable = false)
    private String nom;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_debut", nullable = false)
    private Date dateDebut;

    /** Employe qui gere le projet (peut etre nul). */
    @ManyToOne(fetch = FetchType.LAZY)
    private Employe chef;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tache> taches = new ArrayList<>();

    public Projet() {
    }

    public Projet(String nom, Date dateDebut) {
        this.nom = nom;
        this.dateDebut = dateDebut;
    }

    public Projet(String nom, Date dateDebut, Employe chef) {
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.chef = chef;
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

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Employe getChef() {
        return chef;
    }

    public void setChef(Employe chef) {
        this.chef = chef;
    }

    public List<Tache> getTaches() {
        return taches;
    }

    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }

    /** Ajoute une tache en maintenant les deux cotes de l'association. */
    public void ajouterTache(Tache tache) {
        taches.add(tache);
        tache.setProjet(this);
    }

    @Override
    public String toString() {
        return "Projet{id=" + id + ", nom=" + nom + "}";
    }
}
