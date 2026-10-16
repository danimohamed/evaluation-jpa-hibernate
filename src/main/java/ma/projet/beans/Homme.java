package ma.projet.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/** Citoyen de sexe masculin (exercice 3). */
@Entity
@Table(name = "homme")
@NamedQueries({
        @NamedQuery(name = "Homme.findEpousesEntreDates",
                query = "SELECT m.femme FROM Mariage m "
                        + "WHERE m.homme.id = :hommeId AND m.dateDebut BETWEEN :debut AND :fin "
                        + "ORDER BY m.id"),
        @NamedQuery(name = "Homme.findMariagesAvecDetails",
                query = "SELECT m FROM Mariage m JOIN FETCH m.femme "
                        + "WHERE m.homme.id = :hommeId ORDER BY m.id")
})
public class Homme extends Personne {

    @OneToMany(mappedBy = "homme", fetch = FetchType.LAZY)
    private List<Mariage> mariages = new ArrayList<>();

    public Homme() {
        super();
    }

    public Homme(String nom, String prenom, Date dateNaissance) {
        super(nom, prenom, dateNaissance);
    }

    public List<Mariage> getMariages() {
        return mariages;
    }

    public void setMariages(List<Mariage> mariages) {
        this.mariages = mariages;
    }

    /** Ajoute un mariage en maintenant les deux cotes de l'association. */
    public void ajouterMariage(Mariage mariage) {
        mariages.add(mariage);
        mariage.setHomme(this);
    }
}
