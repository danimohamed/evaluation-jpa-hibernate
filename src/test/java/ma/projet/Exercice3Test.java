package ma.projet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ma.projet.beans.Femme;
import ma.projet.beans.Mariage;
import ma.projet.util.Formats;

/**
 * Tests de l'exercice 3 : heritage des personnes et gestion des mariages.
 *
 * <p>Les valeurs attendues sont celles exigees par l'enonce et reproduites par
 * la demonstration du projet (voir le fichier {@code demo_out.txt}).</p>
 */
public class Exercice3Test extends AbstractJpaTest {

    private static final java.util.Date DEBUT_1990 = Formats.date(1, 1, 1990);
    private static final java.util.Date FIN_1995 = Formats.date(31, 12, 1995);

    private JeuDeDonnees jeu;

    @Before
    public void preparerLesDonnees() {
        jeu = JeuDeDonnees.complet();
    }

    private static List<String> nomsComplets(List<Femme> femmes) {
        List<String> noms = new ArrayList<>();
        for (Femme femme : femmes) {
            noms.add(femme.getNomComplet());
        }
        return noms;
    }

    @Test
    public void lHeritageJointePartageLaCleDeLaTablePersonne() {
        assertEquals(1, jeu.salima.getId());
        // Les 5 hommes sont inseres apres les 10 femmes : SAFI SAID est la 11e personne.
        assertEquals(11, jeu.hommeSaid.getId());
        assertEquals("SALIMA RAMI", jeu.salima.getNomComplet());
        assertEquals("SAFI SAID", jeu.hommeSaid.getNomComplet());
    }

    @Test
    public void laFemmeLaPlusAgeeEstSalimaRami() {
        Femme plusAgee = jeu.femmeService.findPlusAgee();

        assertNotNull(plusAgee);
        assertEquals("SALIMA RAMI", plusAgee.getNomComplet());
        assertEquals("01/01/1950", Formats.dateCourte(plusAgee.getDateNaissance()));
    }

    @Test
    public void leRapportDeLaFemmeLaPlusAgee() {
        String sortie = capturer(jeu.femmeService, () -> jeu.femmeService.afficherPlusAgee());

        assertEquals("Femme la plus âgée : SALIMA RAMI (née le 01/01/1950)", lignes(sortie).get(0));
    }

    @Test
    public void leRapportDeToutesLesFemmesCompteDixLignesOrdonnees() {
        String sortie = capturer(jeu.femmeService, () -> jeu.femmeService.afficherListe());
        List<String> lignes = lignes(sortie);

        assertEquals("Liste des femmes :", lignes.get(0));
        assertEquals(11, lignes.size());
        assertEquals("1. SALIMA RAMI (née le 01/01/1950)", lignes.get(1));
        assertEquals("10. IMANE TAZI (née le 07/08/1985)", lignes.get(10));
    }

    @Test
    public void salimaRamiAQuatreEnfantsNesEntre1990Et1995() {
        int nombre = jeu.femmeService.nombreEnfantsEntreDates(jeu.salima.getId(), DEBUT_1990, FIN_1995);

        assertEquals(4, nombre);

        String sortie = capturer(jeu.femmeService, () -> jeu.femmeService
                .afficherNombreEnfantsEntreDates(jeu.salima.getId(), DEBUT_1990, FIN_1995));
        assertEquals("Nombre d'enfants de SALIMA RAMI nés entre le 01/01/1990 et le 31/12/1995 : 4",
                lignes(sortie).get(0));
    }

    @Test
    public void uneFemmeInconnueEstSignaleeSansPlanter() {
        String sortie = capturer(jeu.femmeService,
                () -> jeu.femmeService.afficherNombreEnfantsEntreDates(999, DEBUT_1990, FIN_1995));

        assertEquals("Femme introuvable : 999", lignes(sortie).get(0));
        assertEquals(0, jeu.femmeService.nombreEnfantsEntreDates(999, DEBUT_1990, FIN_1995));
    }

    @Test
    public void lesFemmesMarieesAuMoinsDeuxFoisSontNadiaBenaniEtLeilaZahra() {
        List<Femme> femmes = jeu.femmeService.findMarieesAuMoinsDeuxFois();

        assertEquals(2, femmes.size());
        List<String> noms = nomsComplets(femmes);
        assertTrue(noms.contains("NADIA BENANI"));
        assertTrue(noms.contains("LEILA ZAHRA"));
    }

    @Test
    public void leRapportDesFemmesMarieesAuMoinsDeuxFois() {
        String sortie = capturer(jeu.femmeService, () -> jeu.femmeService.afficherMarieesAuMoinsDeuxFois());
        List<String> lignes = lignes(sortie);

        assertEquals("Femmes mariées au moins 2 fois :", lignes.get(0));
        assertEquals(3, lignes.size());
        assertNotNull(ligne(lignes, "1. NADIA BENANI"));
        assertNotNull(ligne(lignes, "2. LEILA ZAHRA"));
    }

    @Test
    public void lesEpousesDeSaidMarieesEntre1990Et1995SontSalimaEtAmal() {
        List<Femme> epouses = jeu.hommeService.findEpousesEntreDates(jeu.hommeSaid.getId(),
                DEBUT_1990, FIN_1995);

        assertEquals(2, epouses.size());
        assertEquals("SALIMA RAMI", epouses.get(0).getNomComplet());
        assertEquals("AMAL ALI", epouses.get(1).getNomComplet());
    }

    @Test
    public void leRapportDesEpousesEntreDeuxDates() {
        String sortie = capturer(jeu.hommeService, () -> jeu.hommeService
                .afficherEpousesEntreDates(jeu.hommeSaid.getId(), DEBUT_1990, FIN_1995));
        List<String> lignes = lignes(sortie);

        assertEquals("Épouses de SAFI SAID mariées entre le 01/01/1990 et le 31/12/1995 :",
                lignes.get(0));
        assertEquals("  SALIMA RAMI (née le 01/01/1950)", lignes.get(1));
        assertEquals("  AMAL ALI (née le 12/05/1962)", lignes.get(2));
        assertEquals(3, lignes.size());
    }

    @Test
    public void unHommeSansEpouseSurLaPeriodeAfficheAucune() {
        String sortie = capturer(jeu.hommeService, () -> jeu.hommeService
                .afficherEpousesEntreDates(jeu.hommeRachid.getId(), DEBUT_1990, FIN_1995));
        List<String> lignes = lignes(sortie);

        assertEquals("  Aucune", lignes.get(1));
        assertTrue(jeu.hommeService.findEpousesEntreDates(jeu.hommeRachid.getId(),
                DEBUT_1990, FIN_1995).isEmpty());
    }

    @Test
    public void unSeulHommeEstMarieAQuatreFemmesEntre1990Et1995() {
        long nombre = jeu.hommeService.compterHommesMariesAQuatreFemmesEntreDates(DEBUT_1990, FIN_1995);

        assertEquals(1L, nombre);

        String sortie = capturer(jeu.hommeService, () -> jeu.hommeService
                .afficherHommesMariesAQuatreFemmesEntreDates(DEBUT_1990, FIN_1995));
        assertEquals("Nombre d'hommes mariés à quatre femmes entre le 01/01/1990 et le 31/12/1995 : 1",
                lignes(sortie).get(0));
    }

    @Test
    public void aucunHommeNestMarieAQuatreFemmesApres2000() {
        assertEquals(0L, jeu.hommeService.compterHommesMariesAQuatreFemmesEntreDates(
                Formats.date(1, 1, 2000), Formats.date(31, 12, 2010)));
    }

    @Test
    public void saidTotaliseQuatreMariagesDontUnEchoue() {
        List<Mariage> mariages = jeu.mariageService.findByHomme(jeu.hommeSaid.getId());

        assertEquals(4, mariages.size());
        assertEquals(3, jeu.mariageService.findEnCours(jeu.hommeSaid.getId()).size());
        assertEquals(1, jeu.mariageService.findEchoues(jeu.hommeSaid.getId()).size());
    }

    @Test
    public void leRapportDesMariagesDunHommeReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.mariageService,
                () -> jeu.mariageService.afficherMariagesDunHomme(jeu.hommeSaid.getId()));
        List<String> lignes = lignes(sortie);

        assertEquals("Nom : SAFI SAID", lignes.get(0));
        assertEquals("Mariages En Cours :", lignes.get(1));
        assertTrue(ligne(lignes, "1.").contains("SALIMA RAMI"));
        assertTrue(ligne(lignes, "1.").contains("Nbr Enfants : 4"));
        assertTrue(ligne(lignes, "2.").contains("AMAL ALI"));
        assertTrue(ligne(lignes, "3.").contains("WAFA ALAOUI"));
        assertEquals("", lignes.get(5));
        assertEquals("Mariages échoués :", lignes.get(6));
        assertTrue(ligne(lignes, "1. Femme : KARIMA ALAMI").contains("03/09/1989"));
        assertTrue(ligne(lignes, "Date Fin :").contains("03/09/1990"));
        assertEquals(9, lignes.size());
    }

    @Test
    public void leRapportStatistiqueDunHomme() {
        String sortie = capturer(jeu.mariageService,
                () -> jeu.mariageService.afficherStatistiques(jeu.hommeSaid.getId()));
        List<String> lignes = lignes(sortie);

        assertEquals("Mariages en cours : 3", lignes.get(0));
        assertEquals("Mariages échoués  : 1", lignes.get(1));
        assertEquals(2, lignes.size());
    }

    @Test
    public void unHommeInconnuEstSignaleSansPlanter() {
        String sortie = capturer(jeu.mariageService, () -> jeu.mariageService.afficherMariagesDunHomme(999));

        assertEquals("Homme introuvable : 999", lignes(sortie).get(0));
        assertTrue(jeu.mariageService.findEnCours(999).isEmpty());
        assertTrue(jeu.mariageService.findEchoues(999).isEmpty());
    }

    @Test
    public void marierUnHommeEtUneFemmeExistantsCreeUnMariageEnCours() {
        Mariage mariage = jeu.mariageService.marier(jeu.hommeHassan.getId(), jeu.sana.getId(),
                Formats.date(1, 1, 2015), null, 2);

        assertNotNull(mariage);
        assertTrue(mariage.isEnCours());
        assertEquals(2, mariage.getNombreEnfants());
        assertEquals(1, jeu.mariageService.findEnCours(jeu.hommeHassan.getId()).size());
        assertEquals(2, jeu.mariageService.findByHomme(jeu.hommeHassan.getId()).size());
    }

    @Test
    public void marierAvecUnIdentifiantInconnuRetourneNull() {
        int avant = jeu.mariageService.findAll().size();

        assertNull(jeu.mariageService.marier(999, jeu.sana.getId(),
                Formats.date(1, 1, 2015), null, 0));
        assertNull(jeu.mariageService.marier(jeu.hommeHassan.getId(), 999,
                Formats.date(1, 1, 2015), null, 0));

        assertEquals(avant, jeu.mariageService.findAll().size());
    }
}
