package ma.projet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommande;
import ma.projet.classes.Produit;
import ma.projet.util.Formats;

/**
 * Tests de l'exercice 1 : categories, produits, commandes et lignes de commande.
 *
 * <p>Les valeurs attendues sont celles exigees par l'enonce et reproduites par
 * la demonstration du projet (voir le fichier {@code demo_out.txt}).</p>
 */
public class Exercice1Test extends AbstractJpaTest {

    private JeuDeDonnees jeu;

    @Before
    public void preparerLesDonnees() {
        jeu = JeuDeDonnees.complet();
    }

    private static List<String> references(List<Produit> produits) {
        List<String> references = new ArrayList<>();
        for (Produit produit : produits) {
            references.add(produit.getReference());
        }
        return references;
    }

    private static List<Integer> quantites(List<LigneCommande> lignes) {
        List<Integer> quantites = new ArrayList<>();
        for (LigneCommande ligne : lignes) {
            quantites.add(Integer.valueOf(ligne.getQuantite()));
        }
        return quantites;
    }

    private static List<String> referencesDesLignes(List<LigneCommande> lignes) {
        List<String> references = new ArrayList<>();
        for (LigneCommande ligne : lignes) {
            references.add(ligne.getProduit().getReference());
        }
        return references;
    }

    @Test
    public void lesIdentifiantsGeneresSuiventLOrdreDInsertionDeLEnonce() {
        assertEquals(1, jeu.informatique.getId());
        assertEquals(2, jeu.bureautique.getId());
        assertEquals(1, jeu.es12.getId());
        assertEquals(5, jeu.ll10.getId());
        assertEquals(4, jeu.commande4.getId());
    }

    @Test
    public void lesProduitsDeLaCategorieInformatiqueSontES12ZR85EtEE85() {
        List<Produit> produits = jeu.categorieService.findProduits(jeu.informatique.getId());
        assertEquals(3, produits.size());
        assertEquals(Arrays.asList("ES12", "ZR85", "EE85"), references(produits));
    }

    @Test
    public void lesProduitsDeLaCategorieBureautiqueSontCD45EtLL10() {
        List<Produit> produits = jeu.categorieService.findProduits(jeu.bureautique.getId());
        assertEquals(2, produits.size());
        assertEquals(Arrays.asList("CD45", "LL10"), references(produits));
    }

    @Test
    public void leRapportDesProduitsDUneCategorieReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.categorieService,
                () -> jeu.categorieService.afficherProduits(jeu.informatique.getId()));
        List<String> lignes = lignes(sortie);

        assertEquals("Categorie : Informatique (3 produit(s))", lignes.get(0));
        assertNotNull(ligne(lignes, "Référence"));
        assertNotNull(ligne(lignes, "ES12"));
        assertNotNull(ligne(lignes, "ZR85"));
        assertNotNull(ligne(lignes, "EE85"));
        assertEquals(5, lignes.size());
    }

    @Test
    public void uneCategorieInconnueEstSignaleeSansPlanter() {
        String sortie = capturer(jeu.categorieService,
                () -> jeu.categorieService.afficherProduits(999));
        assertEquals("Categorie introuvable : 999", lignes(sortie).get(0));
    }

    @Test
    public void lesProduitsDontLePrixDepasse100DHSontES12EtEE85() {
        List<Produit> produits = jeu.produitService.findByPrixSuperieurA(100);
        assertEquals(2, produits.size());
        assertEquals(Arrays.asList("ES12", "EE85"), references(produits));
    }

    @Test
    public void leRapportDesProduitsDontLePrixDepasse100DH() {
        String sortie = capturer(jeu.produitService,
                () -> jeu.produitService.afficherProduitsPrixSuperieurA(100));
        List<String> lignes = lignes(sortie);

        assertEquals("Produits dont le prix dépasse 100 DH :", lignes.get(0));
        assertNotNull(ligne(lignes, "ES12"));
        assertNotNull(ligne(lignes, "EE85"));
        assertEquals(4, lignes.size());
    }

    @Test
    public void lesProduitsCommandesEntreJanvierEtFevrier2013SontTriesParReference() {
        List<Produit> produits = jeu.produitService.findByCommandesEntreDates(
                Formats.date(1, 1, 2013), Formats.date(28, 2, 2013));
        assertEquals(Arrays.asList("CD45", "EE85", "ES12", "LL10", "ZR85"), references(produits));
    }

    @Test
    public void leRapportDesProduitsCommandesEntreDeuxDates() {
        String sortie = capturer(jeu.produitService, () -> jeu.produitService
                .afficherProduitsCommandesEntreDates(Formats.date(1, 1, 2013), Formats.date(28, 2, 2013)));
        List<String> lignes = lignes(sortie);

        assertEquals("Produits commandés entre le 01/01/2013 et le 28/02/2013 :", lignes.get(0));
        assertEquals(7, lignes.size());
    }

    @Test
    public void laCommandeNumero4ContientSeptES12QuatorzeZR85EtCinqEE85() {
        List<LigneCommande> lignes = jeu.ligneCommandeService.findByCommande(jeu.commande4.getId());

        assertEquals(3, lignes.size());
        assertEquals(Arrays.asList(7, 14, 5), quantites(lignes));
        assertEquals(Arrays.asList("ES12", "ZR85", "EE85"), referencesDesLignes(lignes));
    }

    @Test
    public void leRapportDesProduitsDUneCommandeReprendLesLignesDeLEnonce() {
        String sortie = capturer(jeu.produitService,
                () -> jeu.produitService.afficherProduitsDuneCommande(jeu.commande4.getId()));
        List<String> lignes = lignes(sortie);

        assertTrue(lignes.get(0).startsWith("Commande : 4"));
        assertTrue(lignes.get(0).endsWith("14 Mars 2013"));
        assertEquals("Liste des produits :", lignes.get(1));
        assertNotNull(ligne(lignes, "Référence"));
        assertTrue(ligne(lignes, "Référence").contains("Quantité"));
        assertEquals(6, lignes.size());
    }

    @Test
    public void lesProduitsDeLaCommande1SontUniquementES12() {
        List<LigneCommande> lignes = jeu.ligneCommandeService.findByCommande(jeu.commande1.getId());

        assertEquals(1, lignes.size());
        assertEquals(Arrays.asList(3), quantites(lignes));
        assertEquals(Arrays.asList("ES12"), referencesDesLignes(lignes));
    }

    @Test
    public void lesLignesDuProduitES12AppartiennentAuxCommandes1Et4() {
        List<LigneCommande> lignes = jeu.ligneCommandeService.findByProduit(jeu.es12.getId());

        assertEquals(2, lignes.size());
        assertEquals(Arrays.asList(3, 7), quantites(lignes));
    }

    @Test
    public void lesCommandesPasseesEntreJanvierEtFevrier2013SontLesNumeros1a3() {
        List<Commande> commandes = jeu.commandeService.findByDates(
                Formats.date(1, 1, 2013), Formats.date(28, 2, 2013));

        assertEquals(3, commandes.size());
        assertEquals(1, commandes.get(0).getId());
        assertEquals(2, commandes.get(1).getId());
        assertEquals(3, commandes.get(2).getId());
    }

    @Test
    public void leRapportDesCommandesEntreDeuxDates() {
        String sortie = capturer(jeu.commandeService, () -> jeu.commandeService
                .afficherCommandesEntreDates(Formats.date(1, 1, 2013), Formats.date(28, 2, 2013)));
        List<String> lignes = lignes(sortie);

        assertEquals("Commandes entre le 01/01/2013 et le 28/02/2013 :", lignes.get(0));
        assertNotNull(ligne(lignes, "Num"));
        assertTrue(ligne(lignes, "1").contains("10 Janvier 2013"));
        assertTrue(ligne(lignes, "2").contains("20 Janvier 2013"));
        assertTrue(ligne(lignes, "3").contains("5 Février 2013"));
        assertEquals(5, lignes.size());
    }

    @Test
    public void ajouterUneLigneAUneCommandeExistanteEstEnregistre() {
        assertTrue(jeu.ligneCommandeService.ajouterLigne(jeu.commande1.getId(), jeu.cd45.getId(), 2));

        List<LigneCommande> lignes = jeu.ligneCommandeService.findByCommande(jeu.commande1.getId());
        assertEquals(2, lignes.size());
        assertEquals(Arrays.asList(3, 2), quantites(lignes));
    }

    @Test
    public void ajouterUneLigneAvecUnProduitOuUneCommandeInexistantEchoue() {
        int avant = jeu.ligneCommandeService.findAll().size();

        assertFalse(jeu.ligneCommandeService.ajouterLigne(999, jeu.es12.getId(), 1));
        assertFalse(jeu.ligneCommandeService.ajouterLigne(jeu.commande1.getId(), 999, 1));

        assertEquals(avant, jeu.ligneCommandeService.findAll().size());
    }
}
