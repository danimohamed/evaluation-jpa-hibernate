package ma.projet.app;

import java.util.Date;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.Employe;
import ma.projet.classes.Produit;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.CategorieService;
import ma.projet.service.CommandeService;
import ma.projet.service.EmployeService;
import ma.projet.service.EmployeTacheService;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.MariageService;
import ma.projet.service.ProduitService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import ma.projet.util.Formats;
import ma.projet.util.HibernateUtil;

/**
 * Programme de demonstration de l'evaluation : il enchaine les trois exercices
 * (gestion des commandes, gestion de projets, etat civil), alimente la base
 * avec un jeu de donnees reproduisant les exemples de l'enonce, puis imprime
 * les rapports attendus.
 */
public final class EvaluationApp {

    private static final String SEPARATEUR =
            "========================================================================";

    private EvaluationApp() {
    }

    public static void main(String[] args) {
        try {
            exercice1();
            exercice2();
            exercice3();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // Exercice 1 : produits, commandes et lignes de commande
    // ------------------------------------------------------------------

    private static void exercice1() {
        titre("EXERCICE 1 : GESTION DES COMMANDES");

        CategorieService categorieService = new CategorieService();
        ProduitService produitService = new ProduitService();
        CommandeService commandeService = new CommandeService();
        LigneCommandeService ligneService = new LigneCommandeService();

        Categorie informatique = new Categorie("Informatique");
        Categorie bureautique = new Categorie("Bureautique");
        categorieService.create(informatique);
        categorieService.create(bureautique);

        Produit es12 = new Produit("ES12", 120, informatique);
        Produit zr85 = new Produit("ZR85", 100, informatique);
        Produit ee85 = new Produit("EE85", 200, informatique);
        Produit cd45 = new Produit("CD45", 50, bureautique);
        Produit ll10 = new Produit("LL10", 90, bureautique);
        produitService.create(es12);
        produitService.create(zr85);
        produitService.create(ee85);
        produitService.create(cd45);
        produitService.create(ll10);

        Commande commande1 = new Commande(Formats.date(10, 1, 2013));
        Commande commande2 = new Commande(Formats.date(20, 1, 2013));
        Commande commande3 = new Commande(Formats.date(5, 2, 2013));
        Commande commande4 = new Commande(Formats.date(14, 3, 2013));
        commandeService.create(commande1);
        commandeService.create(commande2);
        commandeService.create(commande3);
        commandeService.create(commande4);

        ligneService.ajouterLigne(commande1.getId(), es12.getId(), 3);
        ligneService.ajouterLigne(commande2.getId(), cd45.getId(), 10);
        ligneService.ajouterLigne(commande2.getId(), ll10.getId(), 4);
        ligneService.ajouterLigne(commande3.getId(), zr85.getId(), 6);
        ligneService.ajouterLigne(commande3.getId(), ee85.getId(), 2);
        ligneService.ajouterLigne(commande4.getId(), es12.getId(), 7);
        ligneService.ajouterLigne(commande4.getId(), zr85.getId(), 14);
        ligneService.ajouterLigne(commande4.getId(), ee85.getId(), 5);

        sousTitre("1.1 - Produits de la categorie Informatique");
        categorieService.afficherProduits(informatique.getId());

        sousTitre("1.2 - Produits dont le prix est superieur a 100 DH");
        produitService.afficherProduitsPrixSuperieurA(100);

        sousTitre("1.3 - Produits commandes entre le 01/01/2013 et le 28/02/2013");
        produitService.afficherProduitsCommandesEntreDates(
                Formats.date(1, 1, 2013), Formats.date(28, 2, 2013));

        sousTitre("1.4 - Produits de la commande numero " + commande4.getId());
        produitService.afficherProduitsDuneCommande(commande4.getId());

        sousTitre("1.5 - Commandes passees entre le 01/01/2013 et le 28/02/2013");
        commandeService.afficherCommandesEntreDates(
                Formats.date(1, 1, 2013), Formats.date(28, 2, 2013));
    }

    // ------------------------------------------------------------------
    // Exercice 2 : projets, taches et employes
    // ------------------------------------------------------------------

    private static void exercice2() {
        titre("EXERCICE 2 : GESTION DES PROJETS ET DES TACHES");

        EmployeService employeService = new EmployeService();
        ProjetService projetService = new ProjetService();
        TacheService tacheService = new TacheService();
        EmployeTacheService affectationService = new EmployeTacheService();

        Employe said = new Employe("SAFI", "SAID");
        Employe omar = new Employe("ALAMI", "OMAR");
        Employe youssef = new Employe("BENANI", "YOUSSEF");
        employeService.create(said);
        employeService.create(omar);
        employeService.create(youssef);

        Projet web = new Projet("Refonte du site web", Formats.date(2, 1, 2012), said);
        Projet parc = new Projet("Migration du parc informatique", Formats.date(5, 3, 2012), omar);
        Projet facturation = new Projet("Application de facturation", Formats.date(10, 9, 2012), omar);
        Projet stock = new Projet("Gestion de stock", Formats.date(14, 1, 2013), said);
        projetService.create(web);
        projetService.create(parc);
        projetService.create(facturation);
        projetService.create(stock);

        // Projets 1 a 3 : onze taches, afin que les taches du projet 4 portent
        // les numeros 12, 13, 14 et 15 comme dans l'enonce.
        Tache etude = creerTache(tacheService, web, "Etude", 800,
                Formats.date(9, 1, 2012), Formats.date(31, 1, 2012));
        Tache maquettage = creerTache(tacheService, web, "Maquettage", 600,
                Formats.date(1, 2, 2012), Formats.date(28, 2, 2012));
        Tache integration = creerTache(tacheService, web, "Integration", 1400,
                Formats.date(1, 3, 2012), Formats.date(30, 4, 2012));

        Tache inventaire = creerTache(tacheService, parc, "Inventaire", 500,
                Formats.date(12, 3, 2012), Formats.date(30, 3, 2012));
        Tache deploiement = creerTache(tacheService, parc, "Deploiement", 1500,
                Formats.date(2, 4, 2012), Formats.date(15, 6, 2012));
        Tache formation = creerTache(tacheService, parc, "Formation", 700,
                Formats.date(18, 6, 2012), Formats.date(30, 6, 2012));
        Tache recetteParc = creerTache(tacheService, parc, "Recette", 400,
                Formats.date(2, 7, 2012), Formats.date(20, 7, 2012));

        Tache analyseFacturation = creerTache(tacheService, facturation, "Analyse", 1100,
                Formats.date(17, 9, 2012), Formats.date(12, 10, 2012));
        Tache conceptionFacturation = creerTache(tacheService, facturation, "Conception", 1300,
                Formats.date(15, 10, 2012), Formats.date(30, 11, 2012));
        Tache developpementFacturation = creerTache(tacheService, facturation, "Developpement", 300,
                Formats.date(3, 12, 2012), Formats.date(28, 2, 2013));
        Tache testsFacturation = creerTache(tacheService, facturation, "Tests", 450,
                Formats.date(1, 3, 2013), Formats.date(30, 4, 2013));

        realiser(etude, Formats.date(9, 1, 2012), Formats.date(30, 1, 2012));
        realiser(maquettage, Formats.date(1, 2, 2012), Formats.date(27, 2, 2012));
        realiser(integration, Formats.date(2, 3, 2012), Formats.date(28, 4, 2012));
        realiser(inventaire, Formats.date(12, 3, 2012), Formats.date(29, 3, 2012));
        realiser(deploiement, Formats.date(2, 4, 2012), Formats.date(14, 6, 2012));
        realiser(formation, Formats.date(18, 6, 2012), Formats.date(29, 6, 2012));
        realiser(analyseFacturation, Formats.date(17, 9, 2012), Formats.date(11, 10, 2012));
        realiser(conceptionFacturation, Formats.date(15, 10, 2012), Formats.date(29, 11, 2012));
        tacheService.update(etude);
        tacheService.update(maquettage);
        tacheService.update(integration);
        tacheService.update(inventaire);
        tacheService.update(deploiement);
        tacheService.update(formation);
        tacheService.update(analyseFacturation);
        tacheService.update(conceptionFacturation);

        Tache analyseStock = creerTache(tacheService, stock, "Analyse", 1200,
                Formats.date(1, 2, 2013), Formats.date(15, 2, 2013));
        Tache conceptionStock = creerTache(tacheService, stock, "Conception", 900,
                Formats.date(1, 3, 2013), Formats.date(10, 3, 2013));
        Tache developpementStock = creerTache(tacheService, stock, "Développement", 2500,
                Formats.date(1, 4, 2013), Formats.date(20, 4, 2013));
        Tache recetteStock = creerTache(tacheService, stock, "Recette", 600,
                Formats.date(1, 5, 2013), Formats.date(31, 5, 2013));

        realiser(analyseStock, Formats.date(10, 2, 2013), Formats.date(20, 2, 2013));
        realiser(conceptionStock, Formats.date(10, 3, 2013), Formats.date(15, 3, 2013));
        realiser(developpementStock, Formats.date(10, 4, 2013), Formats.date(25, 4, 2013));
        tacheService.update(analyseStock);
        tacheService.update(conceptionStock);
        tacheService.update(developpementStock);

        affectationService.affecter(said.getId(), analyseStock.getId(),
                Formats.date(1, 2, 2013), Formats.date(20, 2, 2013));
        affectationService.affecter(said.getId(), conceptionStock.getId(),
                Formats.date(1, 3, 2013), Formats.date(15, 3, 2013));
        affectationService.affecter(said.getId(), developpementStock.getId(),
                Formats.date(1, 4, 2013), Formats.date(25, 4, 2013));
        affectationService.affecter(omar.getId(), etude.getId(),
                Formats.date(9, 1, 2012), Formats.date(30, 1, 2012));
        affectationService.affecter(omar.getId(), maquettage.getId(),
                Formats.date(1, 2, 2012), Formats.date(27, 2, 2012));
        affectationService.affecter(omar.getId(), integration.getId(),
                Formats.date(2, 3, 2012), Formats.date(28, 4, 2012));
        affectationService.affecter(youssef.getId(), inventaire.getId(),
                Formats.date(12, 3, 2012), Formats.date(29, 3, 2012));
        affectationService.affecter(youssef.getId(), deploiement.getId(),
                Formats.date(2, 4, 2012), Formats.date(14, 6, 2012));
        affectationService.affecter(youssef.getId(), formation.getId(),
                Formats.date(18, 6, 2012), Formats.date(29, 6, 2012));

        sousTitre("2.1 - Taches realisees du projet " + stock.getId()
                + " (" + stock.getNom() + ")");
        projetService.afficherTachesRealisees(stock.getId());

        sousTitre("2.2 - Taches planifiees du projet " + stock.getId()
                + " (" + stock.getNom() + ")");
        projetService.afficherTachesPlanifiees(stock.getId());

        sousTitre("2.3 - Cout des taches realisees du projet " + stock.getId());
        projetService.afficherCoutRealise(stock.getId());

        sousTitre("2.4 - Taches realisees par l'employe " + said.getNomComplet());
        employeService.afficherTachesRealisees(said.getId());

        sousTitre("2.5 - Projets geres par l'employe " + said.getNomComplet());
        employeService.afficherProjetsGeres(said.getId());

        sousTitre("2.6 - Taches realisees entre le 01/01/2013 et le 31/12/2013");
        tacheService.afficherTachesRealiseesEntreDates(
                Formats.date(1, 1, 2013), Formats.date(31, 12, 2013));

        sousTitre("2.7 - Taches dont le prix depasse 1000 DH");
        tacheService.afficherTachesPrixSuperieurA(1000);

        sousTitre("2.8 - Employes affectes a la tache " + analyseStock.getNom()
                + " (numero " + analyseStock.getId() + ")");
        tacheService.afficherEmployes(analyseStock.getId());

        sousTitre("2.9 - Affectations de l'employe " + youssef.getNomComplet());
        affectationService.afficherAffectations(youssef.getId());

        sousTitre("2.10 - Date de debut du premier projet");
        Date premiereDate = projetService.findPremiereDateDebut();
        System.out.println("Premiere date de debut : " + Formats.dateLongue(premiereDate));
    }

    private static Tache creerTache(TacheService service, Projet projet, String nom,
            double prix, Date debutPrevue, Date finPrevue) {
        Tache tache = new Tache(nom, prix);
        tache.setDateDebutPrevue(debutPrevue);
        tache.setDateFinPrevue(finPrevue);
        tache.setProjet(projet);
        service.create(tache);
        return tache;
    }

    private static void realiser(Tache tache, Date debutReelle, Date finReelle) {
        tache.setDateDebutReelle(debutReelle);
        tache.setDateFinReelle(finReelle);
    }

    // ------------------------------------------------------------------
    // Exercice 3 : etat civil
    // ------------------------------------------------------------------

    private static void exercice3() {
        titre("EXERCICE 3 : ETAT CIVIL");

        FemmeService femmeService = new FemmeService();
        HommeService hommeService = new HommeService();
        MariageService mariageService = new MariageService();

        Femme[] femmes = {
                new Femme("SALIMA", "RAMI", Formats.date(1, 1, 1950)),
                new Femme("AMAL", "ALI", Formats.date(12, 5, 1962)),
                new Femme("WAFA", "ALAOUI", Formats.date(3, 7, 1970)),
                new Femme("KARIMA", "ALAMI", Formats.date(22, 11, 1965)),
                new Femme("NADIA", "BENANI", Formats.date(14, 2, 1968)),
                new Femme("LEILA", "ZAHRA", Formats.date(30, 6, 1972)),
                new Femme("SANA", "IDRISSI", Formats.date(9, 9, 1975)),
                new Femme("HIND", "OUAZZANI", Formats.date(18, 3, 1978)),
                new Femme("GHITA", "BERADA", Formats.date(25, 12, 1980)),
                new Femme("IMANE", "TAZI", Formats.date(7, 8, 1985))
        };
        for (Femme femme : femmes) {
            femmeService.create(femme);
        }

        Homme[] hommes = {
                new Homme("SAFI", "SAID", Formats.date(15, 4, 1955)),
                new Homme("ALAMI", "OMAR", Formats.date(10, 10, 1960)),
                new Homme("BENANI", "YOUSSEF", Formats.date(20, 1, 1963)),
                new Homme("KADIRI", "HASSAN", Formats.date(5, 5, 1970)),
                new Homme("TAZI", "RACHID", Formats.date(11, 11, 1975))
        };
        for (Homme homme : hommes) {
            hommeService.create(homme);
        }

        Homme said = hommes[0];
        Homme omar = hommes[1];
        Homme youssef = hommes[2];
        Homme hassan = hommes[3];
        Homme rachid = hommes[4];

        mariageService.marier(said.getId(), femmes[0].getId(),
                Formats.date(3, 9, 1990), null, 4);
        mariageService.marier(said.getId(), femmes[1].getId(),
                Formats.date(3, 9, 1995), null, 2);
        mariageService.marier(said.getId(), femmes[2].getId(),
                Formats.date(4, 11, 2000), null, 3);
        mariageService.marier(said.getId(), femmes[3].getId(),
                Formats.date(3, 9, 1989), Formats.date(3, 9, 1990), 0);

        mariageService.marier(omar.getId(), femmes[4].getId(),
                Formats.date(15, 1, 1990), null, 3);
        mariageService.marier(omar.getId(), femmes[5].getId(),
                Formats.date(20, 6, 1991), null, 1);
        mariageService.marier(omar.getId(), femmes[6].getId(),
                Formats.date(12, 3, 1993), null, 2);
        mariageService.marier(omar.getId(), femmes[7].getId(),
                Formats.date(8, 9, 1994), null, 0);

        mariageService.marier(youssef.getId(), femmes[4].getId(),
                Formats.date(2, 2, 1985), Formats.date(1, 1, 1988), 1);
        mariageService.marier(hassan.getId(), femmes[5].getId(),
                Formats.date(5, 5, 1986), Formats.date(4, 4, 1989), 0);
        mariageService.marier(rachid.getId(), femmes[9].getId(),
                Formats.date(10, 10, 2005), null, 2);

        sousTitre("3.1 - Liste des femmes");
        femmeService.afficherListe();

        sousTitre("3.2 - Femme la plus agee");
        femmeService.afficherPlusAgee();

        sousTitre("3.3 - Epouses de " + said.getNomComplet()
                + " mariees entre le 01/01/1990 et le 31/12/1995");
        hommeService.afficherEpousesEntreDates(said.getId(),
                Formats.date(1, 1, 1990), Formats.date(31, 12, 1995));

        sousTitre("3.4 - Nombre d'enfants de " + femmes[0].getNomComplet()
                + " entre le 01/01/1990 et le 31/12/1995");
        femmeService.afficherNombreEnfantsEntreDates(femmes[0].getId(),
                Formats.date(1, 1, 1990), Formats.date(31, 12, 1995));

        sousTitre("3.5 - Femmes mariees au moins deux fois");
        femmeService.afficherMarieesAuMoinsDeuxFois();

        sousTitre("3.6 - Nombre d'hommes maries a quatre femmes entre le 01/01/1990"
                + " et le 31/12/1995");
        hommeService.afficherHommesMariesAQuatreFemmesEntreDates(
                Formats.date(1, 1, 1990), Formats.date(31, 12, 1995));

        sousTitre("3.7 - Mariages de " + said.getNomComplet() + " avec tous les details");
        mariageService.afficherMariagesDunHomme(said.getId());

        sousTitre("3.8 - Statistiques des mariages de " + said.getNomComplet());
        mariageService.afficherStatistiques(said.getId());
    }

    // ------------------------------------------------------------------
    // Mise en forme du rapport
    // ------------------------------------------------------------------

    private static void titre(String libelle) {
        System.out.println();
        System.out.println(SEPARATEUR);
        System.out.println(libelle);
        System.out.println(SEPARATEUR);
    }

    private static void sousTitre(String libelle) {
        System.out.println();
        System.out.println("--- " + libelle + " ---");
    }
}
