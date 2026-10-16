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

/** Citoyen de sexe feminin (exercice 3). */
@Entity
@Table(name = "femme")
@NamedQueries({
        @NamedQuery(name = "Femme.findMarieesAuMoinsDeuxFois",
                query = "SELECT f FROM Femme f "
                        + "WHERE (SELECT COUNT(m) FROM Mariage m WHERE m.femme.id = f.id) >= :nombre "
                        + "ORDER BY f.id"),
        @NamedQuery(name = "Femme.findPlusAgee",
                query = "SELECT f FROM Femme f WHERE f.dateNaissance IS NOT NULL "
                        + "ORDER BY f.dateNaissance ASC, f.id ASC")
})
public class Femme extends Personne {

    @OneToMany(mappedBy = "femme", fetch = FetchType.LAZY)
    private List<Mariage> mariages = new ArrayList<>();

    public Femme() {
        super();
    }

    public Femme(String nom, String prenom, Date dateNaissance) {
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
        mariage.setFemme(this);
    }
}
