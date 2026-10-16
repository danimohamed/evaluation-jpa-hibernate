package ma.projet.classes;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Affectation d'un employe a une tache, avec les dates reelles de realisation
 * (exercice 2). Table d'association porteuse entre {@link Employe} et
 * {@link Tache}.
 */
@Entity
@Table(name = "employe_tache")
public class EmployeTache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_debut")
    private Date dateDebut;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_fin")
    private Date dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    private Tache tache;

    public EmployeTache() {
    }

    public EmployeTache(Employe employe, Tache tache) {
        this.employe = employe;
        this.tache = tache;
    }

    public EmployeTache(Employe employe, Tache tache, Date dateDebut, Date dateFin) {
        this.employe = employe;
        this.tache = tache;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Tache getTache() {
        return tache;
    }

    public void setTache(Tache tache) {
        this.tache = tache;
    }

    @Override
    public String toString() {
        return "EmployeTache{id=" + id + "}";
    }
}
