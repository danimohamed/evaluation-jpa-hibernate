package ma.projet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.util.Formats;

/**
 * Tests de l'exercice 2 : projets, taches, employes et affectations.
 *
 * <p>Les valeurs attendues sont celles exigees par l'enonce et reproduites par
 * la demonstration du projet (voir le fichier {@code demo_out.txt}).</p>
 */
public class Exercice2Test extends AbstractJpaTest {

    private JeuDeDonnees jeu;

    @Before
    public void preparerLesDonnees() {
        jeu = JeuDeDonnees.complet();
    }

    private static List<Integer> identifiants(List<Tache> taches) {
        List<Integer> identifiants = new ArrayList<>();
        for (Tache tache : taches) {
            identifiants.add(Integer.valueOf(tache.getId()));
        }
        return identifiants;
    }

    private static List<String> noms(List<Tache> taches) {
        List<String> noms = new ArrayList<>();
        for (Tache tache : taches) {
            noms.add(tache.getNom());
        }
        return noms;
    }

    @Test
    public void lesIdentifiantsGeneresSuiventLOrdreDInsertionDeLEnonce() {
        assertEquals(1, jeu.said.getId());
        assertEquals(3, jeu.youssef.getId());
        assertEquals(1, jeu.web.getId());
        assertEquals(4, jeu.stock.getId());
        assertEquals(12, jeu.analyseStock.getId());
        assertEquals(15, jeu.recetteStock.getId());
    }

    @Test
    public void leProjet4EstGestionDeStock() {
        Projet projet = jeu.projetService.findById(jeu.stock.getId());

        assertNotNull(projet);
        assertEquals("Gestion de stock", projet.getNom());
        assertEquals(Formats.date(14, 1, 2013), projet.getDateDebut());
    }

    @Test
    public void lesTachesRealiseesDuProjet4SontLesNumeros12a14() {
        List<Tache> taches = jeu.projetService.findTachesRealisees(jeu.stock.getId());

        assertEquals(3, taches.size());
        assertEquals(Arrays.asList(12, 13, 14), identifiants(taches));
        assertEquals(Arrays.asList("Analyse", "Conception", "Développement"), noms(taches));
    }

    @Test
    public void lesTachesPlanifieesDuProjet4SontLesNumeros12a15() {
        List<Tache> taches = jeu.projetService.findTachesPlanifiees(jeu.stock.getId());

        assertEquals(4, taches.size());
        assertEquals(Arrays.asList(12, 13, 14, 15), identifiants(taches));
        assertEquals("Recette", taches.get(3).getNom());
        assertFalse(taches.get(3).isRealisee());
    }

    @Test
    public void leRapportDesTachesRealiseesDUneProjetReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.projetService,
                () -> jeu.projetService.afficherTachesRealisees(jeu.stock.getId()));
        List<String> lignes = lignes(sortie);

        assertTrue(lignes.get(0).startsWith("Projet : 4"));
        assertTrue(lignes.get(0).contains("Gestion de stock"));
        assertTrue(lignes.get(0).endsWith("14 Janvier 2013"));
        assertEquals("Liste des tâches:", lignes.get(1));
        assertTrue(ligne(lignes, "12").contains("10/02/2013"));
        assertTrue(ligne(lignes, "13").contains("10/03/2013"));
        assertTrue(ligne(lignes, "14").contains("10/04/2013"));
        assertEquals(6, lignes.size());
    }

    @Test
    public void leRapportDesTachesPlanifieesDUneProjetReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.projetService,
                () -> jeu.projetService.afficherTachesPlanifiees(jeu.stock.getId()));
        List<String> lignes = lignes(sortie);

        assertEquals("Liste des tâches planifiées:", lignes.get(1));
        assertTrue(ligne(lignes, "15").contains("01/05/2013"));
        assertTrue(ligne(lignes, "15").contains("31/05/2013"));
        assertEquals(7, lignes.size());
    }

    @Test
    public void leCoutDesTachesRealiseesDuProjet4EstDe4600DH() {
        String sortie = capturer(jeu.projetService,
                () -> jeu.projetService.afficherCoutRealise(jeu.stock.getId()));

        assertEquals("Coût total des tâches réalisées : 4600 DH", lignes(sortie).get(0));
    }

    @Test
    public void laPremiereDateDeDebutDesProjetsEstLe2Janvier2012() {
        assertEquals(Formats.date(2, 1, 2012), jeu.projetService.findPremiereDateDebut());
        assertEquals("2 Janvier 2012",
                Formats.dateLongue(jeu.projetService.findPremiereDateDebut()));
    }

    @Test
    public void lesTachesRealiseesParSaidSontLesNumeros12a14() {
        List<Tache> taches = jeu.employeService.findTachesRealisees(jeu.said.getId());

        assertEquals(3, taches.size());
        assertEquals(Arrays.asList(12, 13, 14), identifiants(taches));
    }

    @Test
    public void leRapportDesTachesRealiseesDUneEmployeReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.employeService,
                () -> jeu.employeService.afficherTachesRealisees(jeu.said.getId()));
        List<String> lignes = lignes(sortie);

        assertEquals("Tâches réalisées par SAID SAFI :", lignes.get(0));
        assertNotNull(ligne(lignes, "Num"));
        assertTrue(ligne(lignes, "12").contains("1200 DH"));
        assertTrue(ligne(lignes, "14").contains("2500 DH"));
        assertTrue(ligne(lignes, "14").contains("Gestion de stock"));
        assertEquals(5, lignes.size());
    }

    @Test
    public void lesProjetsGeresParSaidSontLesNumeros1Et4() {
        List<Projet> projets = jeu.employeService.findProjetsGeres(jeu.said.getId());

        assertEquals(2, projets.size());
        assertEquals(1, projets.get(0).getId());
        assertEquals("Refonte du site web", projets.get(0).getNom());
        assertEquals(4, projets.get(1).getId());
        assertEquals("Gestion de stock", projets.get(1).getNom());
    }

    @Test
    public void leRapportDesProjetsGeresReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.employeService,
                () -> jeu.employeService.afficherProjetsGeres(jeu.said.getId()));
        List<String> lignes = lignes(sortie);

        assertEquals("Projets gérés par SAID SAFI :", lignes.get(0));
        assertTrue(ligne(lignes, "1").contains("2 Janvier 2012"));
        assertTrue(ligne(lignes, "4").contains("14 Janvier 2013"));
        assertEquals(4, lignes.size());
    }

    @Test
    public void lesTachesRealiseesParSaidEn2013SontLesNumeros12a14() {
        List<Tache> taches = jeu.employeService.findTachesRealiseesEntreDates(jeu.said.getId(),
                Formats.date(1, 1, 2013), Formats.date(31, 12, 2013));

        assertEquals(Arrays.asList(12, 13, 14), identifiants(taches));

        String sortie = capturer(jeu.employeService, () -> jeu.employeService
                .afficherTachesRealiseesEntreDates(jeu.said.getId(),
                        Formats.date(1, 1, 2013), Formats.date(31, 12, 2013)));
        List<String> lignes = lignes(sortie);
        assertEquals("Tâches réalisées entre le 01/01/2013 et le 31/12/2013 :", lignes.get(0));
        assertEquals(4, lignes.size());
    }

    @Test
    public void lesTachesDontLePrixDepasse1000DHSontLesNumeros3a14() {
        List<Tache> taches = jeu.tacheService.findByPrixSuperieurA(1000);

        assertEquals(6, taches.size());
        assertEquals(Arrays.asList(3, 5, 8, 9, 12, 14), identifiants(taches));
    }

    @Test
    public void leRapportDesTachesDontLePrixDepasse1000DH() {
        String sortie = capturer(jeu.tacheService,
                () -> jeu.tacheService.afficherTachesPrixSuperieurA(1000));
        List<String> lignes = lignes(sortie);

        assertEquals("Tâches dont le prix dépasse 1000 DH :", lignes.get(0));
        assertTrue(ligne(lignes, "3").contains("Refonte du site web"));
        assertTrue(ligne(lignes, "14").contains("Gestion de stock"));
        assertEquals(8, lignes.size());
    }

    @Test
    public void lesTachesRealiseesEntreJanvierEtDecembre2013SontLesNumeros12a14() {
        List<Tache> taches = jeu.tacheService.findRealiseesEntreDates(
                Formats.date(1, 1, 2013), Formats.date(31, 12, 2013));

        assertEquals(Arrays.asList(12, 13, 14), identifiants(taches));

        String sortie = capturer(jeu.tacheService, () -> jeu.tacheService
                .afficherTachesRealiseesEntreDates(Formats.date(1, 1, 2013), Formats.date(31, 12, 2013)));
        assertEquals(5, lignes(sortie).size());
    }

    @Test
    public void laTache12EstAffecteeAuSeulEmployeSaid() {
        List<Employe> employes = jeu.tacheService.findEmployes(jeu.analyseStock.getId());
        assertEquals(1, employes.size());
        assertEquals("SAID SAFI", employes.get(0).getNomComplet());

        String sortie = capturer(jeu.tacheService,
                () -> jeu.tacheService.afficherEmployes(jeu.analyseStock.getId()));
        List<String> lignes = lignes(sortie);
        assertEquals("Employés affectés à la tâche 12 :", lignes.get(0));
        assertTrue(ligne(lignes, "1").contains("SAID SAFI"));
        assertEquals(3, lignes.size());
    }

    @Test
    public void lesAffectationsDeYoussefSontLesTaches4a6ToutesRealisees() {
        List<EmployeTache> affectations = jeu.employeTacheService.findByEmploye(jeu.youssef.getId());

        assertEquals(3, affectations.size());
        assertEquals(4, affectations.get(0).getTache().getId());
        assertEquals(5, affectations.get(1).getTache().getId());
        assertEquals(6, affectations.get(2).getTache().getId());
        for (EmployeTache affectation : affectations) {
            assertTrue(affectation.getTache().isRealisee());
        }
    }

    @Test
    public void leRapportDesAffectationsDUneEmployeReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.employeTacheService,
                () -> jeu.employeTacheService.afficherAffectations(jeu.youssef.getId()));
        List<String> lignes = lignes(sortie);

        assertEquals("Affectations de YOUSSEF BENANI :", lignes.get(0));
        assertTrue(ligne(lignes, "Num").contains("Réalisée"));
        assertTrue(ligne(lignes, "4").contains("Inventaire"));
        assertTrue(ligne(lignes, "5").contains("Deploiement"));
        assertTrue(ligne(lignes, "6").contains("Formation"));
        assertEquals(5, lignes.size());
    }

    @Test
    public void lAffectationDeLaTache12RevientAuxBonnesDates() {
        List<EmployeTache> affectations = jeu.employeTacheService.findByTache(jeu.analyseStock.getId());

        assertEquals(1, affectations.size());
        assertEquals("SAID SAFI", affectations.get(0).getEmploye().getNomComplet());
        assertEquals(Formats.date(1, 2, 2013), affectations.get(0).getDateDebut());
        assertEquals(Formats.date(20, 2, 2013), affectations.get(0).getDateFin());
    }

    @Test
    public void affecterUnEmployeOuUneTacheInexistantNeCreeRien() {
        int avant = jeu.employeTacheService.findAll().size();

        assertNull(jeu.employeTacheService.affecter(999, jeu.analyseStock.getId(),
                Formats.date(1, 1, 2014), Formats.date(2, 1, 2014)));
        assertNull(jeu.employeTacheService.affecter(jeu.youssef.getId(), 999,
                Formats.date(1, 1, 2014), Formats.date(2, 1, 2014)));

        assertEquals(avant, jeu.employeTacheService.findAll().size());
    }
}
