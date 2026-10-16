package ma.projet.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Citoyen de l'etat civil (exercice 3). Classe racine de la hierarchie
 * {@code Homme} / {@code Femme}.
 *
 * <p>La strategie d'heritage retenue est {@code JOINED} : une table par classe,
 * reliee par une cle etrangere vers {@code personne}. C'est le choix le plus
 * adapte a un etat civil ou les deux sous-classes portent des associations
 * propres (leurs mariages).</p>
 */
@Entity
@Table(name = "personne")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_personne")
public abstract class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", length = 60, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 60, nullable = false)
    private String prenom;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_naissance")
    private Date dateNaissance;

    protected Personne() {
    }

    protected Personne(String nom, String prenom, Date dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
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

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    /** @return le nom complet, nom suivi du prenom (format de l'enonce). */
    public String getNomComplet() {
        return nom + " " + prenom;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", nom=" + nom + ", prenom=" + prenom + "}";
    }
}
