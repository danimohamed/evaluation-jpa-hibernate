package ma.projet.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Mariage entre un {@link Homme} et une {@link Femme} (exercice 3).
 *
 * <p>Un mariage est dit <em>en cours</em> lorsque {@code dateFin} est nulle, et
 * <em>echoue</em> (divorce) dans le cas contraire.</p>
 *
 * <p>Deux requetes exigees par l'enonce sont declarees ici : la requete
 * <strong>native nommee</strong> qui totalise les enfants d'une femme sur une
 * periode, et une requete JPQL qui charge le mariage avec son epouse en une
 * seule instruction ({@code JOIN FETCH}).</p>
 */
@Entity
@Table(name = "mariage")
@NamedQueries({
        @NamedQuery(name = "Mariage.findByHomme",
                query = "SELECT m FROM Mariage m JOIN FETCH m.femme "
                        + "WHERE m.homme.id = :hommeId ORDER BY m.id")
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "Mariage.nombreEnfantsFemmeEntreDates",
                query = "SELECT COALESCE(SUM(m.nombre_enfants), 0) FROM mariage m "
                        + "WHERE m.femme_id = :femmeId AND m.date_debut BETWEEN :debut AND :fin")
})
public class Mariage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_debut", nullable = false)
    private Date dateDebut;

    /** Date de fin : nulle tant que le mariage est en cours. */
    @Temporal(TemporalType.DATE)
    @Column(name = "date_fin")
    private Date dateFin;

    /** Nombre d'enfants nes de cette union. */
    @Column(name = "nombre_enfants", nullable = false)
    private int nombreEnfants;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Homme homme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Femme femme;

    public Mariage() {
    }

    public Mariage(Date dateDebut, int nombreEnfants) {
        this.dateDebut = dateDebut;
        this.nombreEnfants = nombreEnfants;
    }

    public Mariage(Date dateDebut, Date dateFin, int nombreEnfants) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nombreEnfants = nombreEnfants;
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

    public int getNombreEnfants() {
        return nombreEnfants;
    }

    public void setNombreEnfants(int nombreEnfants) {
        this.nombreEnfants = nombreEnfants;
    }

    public Homme getHomme() {
        return homme;
    }

    public void setHomme(Homme homme) {
        this.homme = homme;
    }

    public Femme getFemme() {
        return femme;
    }

    public void setFemme(Femme femme) {
        this.femme = femme;
    }

    /** @return {@code true} si le mariage est toujours en cours. */
    public boolean isEnCours() {
        return dateFin == null;
    }

    @Override
    public String toString() {
        return "Mariage{id=" + id + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin
                + ", nombreEnfants=" + nombreEnfants + "}";
    }
}
