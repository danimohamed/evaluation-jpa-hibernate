package ma.projet.classes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/** Employe du bureau d'etudes (exercice 2). */
@Entity
@Table(name = "employe")
@NamedQueries({
        @NamedQuery(name = "Employe.findProjetsGeres",
                query = "SELECT p FROM Projet p WHERE p.chef.id = :employeId ORDER BY p.id"),
        @NamedQuery(name = "Employe.findTachesRealisees",
                query = "SELECT DISTINCT et.tache FROM EmployeTache et "
                        + "JOIN FETCH et.tache.projet "
                        + "WHERE et.employe.id = :employeId "
                        + "AND et.tache.dateDebutReelle IS NOT NULL "
                        + "AND et.tache.dateFinReelle IS NOT NULL "
                        + "ORDER BY et.tache.nom")
})
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", length = 60, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 60, nullable = false)
    private String prenom;

    /** Projets dont cet employe est le chef de projet. */
    @OneToMany(mappedBy = "chef", fetch = FetchType.LAZY)
    private List<Projet> projetsGeres = new ArrayList<>();

    /** Affectations de cet employe aux taches. */
    @OneToMany(mappedBy = "employe", fetch = FetchType.LAZY)
    private List<EmployeTache> affectations = new ArrayList<>();

    public Employe() {
    }

    public Employe(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public List<Projet> getProjetsGeres() {
        return projetsGeres;
    }

    public void setProjetsGeres(List<Projet> projetsGeres) {
        this.projetsGeres = projetsGeres;
    }

    public List<EmployeTache> getAffectations() {
        return affectations;
    }

    public void setAffectations(List<EmployeTache> affectations) {
        this.affectations = affectations;
    }

    /** @return le nom complet, prenom suivi du nom. */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "Employe{id=" + id + ", nom=" + nom + ", prenom=" + prenom + "}";
    }
}
