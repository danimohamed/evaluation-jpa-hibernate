package ma.projet;

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

/**
 * Jeu de donnees de reference des tests : il reproduit exactement les donnees
 * inserees par {@code EvaluationApp} afin que les identifiants generes et les
 * rapports attendus soient identiques a ceux de la demonstration.
 *
 * <p>Les tables doivent etre vides avant l'appel a {@link #complet()} : la base
 * de test est nettoyee par {@link AbstractJpaTest} avant et apres chaque test.</p>
 */
public final class JeuDeDonnees {

    // --- Services partages -------------------------------------------------

    public final CategorieService categorieService = new CategorieService();
    public final ProduitService produitService = new ProduitService();
    public final CommandeService commandeService = new CommandeService();
    public final LigneCommandeService ligneCommandeService = new LigneCommandeService();

    public final EmployeService employeService = new EmployeService();
    public final ProjetService projetService = new ProjetService();
    public final TacheService tacheService = new TacheService();
    public final EmployeTacheService employeTacheService = new EmployeTacheService();

    public final FemmeService femmeService = new FemmeService();
    public final HommeService hommeService = new HommeService();
    public final MariageService mariageService = new MariageService();

    // --- Exercice 1 -------------------------------------------------------

    public Categorie informatique;
    public Categorie bureautique;
    public Produit es12;
    public Produit zr85;
    public Produit ee85;
    public Produit cd45;
    public Produit ll10;
    public Commande commande1;
    public Commande commande2;
    public Commande commande3;
    public Commande commande4;

    // --- Exercice 2 -------------------------------------------------------

    public Employe said;
    public Employe omar;
    public Employe youssef;
    public Projet web;
    public Projet parc;
    public Projet facturation;
    public Projet stock;
    public Tache analyseStock;
    public Tache conceptionStock;
    public Tache developpementStock;
    public Tache recetteStock;

    // --- Exercice 3 -------------------------------------------------------

    public Femme salima;
    public Femme amal;
    public Femme wafa;
    public Femme karima;
    public Femme nadia;
    public Femme leila;
    public Femme sana;
    public Femme hind;
    public Femme ghita;
    public Femme imane;

    public Homme hommeSaid;
    public Homme hommeOmar;
    public Homme hommeYoussef;
    public Homme hommeHassan;
    public Homme hommeRachid;

    private JeuDeDonnees() {
    }

    /**
     * Insere l'integralite du jeu de donnees de la demonstration : les trois
     * exercices sont alimentes dans le meme ordre que dans {@code EvaluationApp}.
     */
    public static JeuDeDonnees complet() {
        JeuDeDonnees jeu = new JeuDeDonnees();
        jeu.exercice1();
        jeu.exercice2();
        jeu.exercice3();
        return jeu;
    }

    // ------------------------------------------------------------------
    // Exercice 1 : produits, commandes et lignes de commande
    // ------------------------------------------------------------------

    public void exercice1() {
        informatique = new Categorie("Informatique");
        bureautique = new Categorie("Bureautique");
        categorieService.create(informatique);
        categorieService.create(bureautique);

        es12 = new Produit("ES12", 120, informatique);
        zr85 = new Produit("ZR85", 100, informatique);
        ee85 = new Produit("EE85", 200, informatique);
        cd45 = new Produit("CD45", 50, bureautique);
        ll10 = new Produit("LL10", 90, bureautique);
        produitService.create(es12);
        produitService.create(zr85);
        produitService.create(ee85);
        produitService.create(cd45);
        produitService.create(ll10);

        commande1 = new Commande(Formats.date(10, 1, 2013));
        commande2 = new Commande(Formats.date(20, 1, 2013));
        commande3 = new Commande(Formats.date(5, 2, 2013));
        commande4 = new Commande(Formats.date(14, 3, 2013));
        commandeService.create(commande1);
        commandeService.create(commande2);
        commandeService.create(commande3);
        commandeService.create(commande4);

        ligneCommandeService.ajouterLigne(commande1.getId(), es12.getId(), 3);
        ligneCommandeService.ajouterLigne(commande2.getId(), cd45.getId(), 10);
        ligneCommandeService.ajouterLigne(commande2.getId(), ll10.getId(), 4);
        ligneCommandeService.ajouterLigne(commande3.getId(), zr85.getId(), 6);
        ligneCommandeService.ajouterLigne(commande3.getId(), ee85.getId(), 2);
        ligneCommandeService.ajouterLigne(commande4.getId(), es12.getId(), 7);
        ligneCommandeService.ajouterLigne(commande4.getId(), zr85.getId(), 14);
        ligneCommandeService.ajouterLigne(commande4.getId(), ee85.getId(), 5);
    }

    // ------------------------------------------------------------------
    // Exercice 2 : projets, taches et employes
    // ------------------------------------------------------------------

    public void exercice2() {
        said = new Employe("SAFI", "SAID");
        omar = new Employe("ALAMI", "OMAR");
        youssef = new Employe("BENANI", "YOUSSEF");
        employeService.create(said);
        employeService.create(omar);
        employeService.create(youssef);

        web = new Projet("Refonte du site web", Formats.date(2, 1, 2012), said);
        parc = new Projet("Migration du parc informatique", Formats.date(5, 3, 2012), omar);
        facturation = new Projet("Application de facturation", Formats.date(10, 9, 2012), omar);
        stock = new Projet("Gestion de stock", Formats.date(14, 1, 2013), said);
        projetService.create(web);
        projetService.create(parc);
        projetService.create(facturation);
        projetService.create(stock);

        // Onze taches dans les projets 1 a 3 : les taches du projet 4 portent
        // ainsi les numeros 12, 13, 14 et 15 comme dans l'enonce.
        Tache etude = creerTache(web, "Etude", 800,
                Formats.date(9, 1, 2012), Formats.date(31, 1, 2012));
        Tache maquettage = creerTache(web, "Maquettage", 600,
                Formats.date(1, 2, 2012), Formats.date(28, 2, 2012));
        Tache integration = creerTache(web, "Integration", 1400,
                Formats.date(1, 3, 2012), Formats.date(30, 4, 2012));

        Tache inventaire = creerTache(parc, "Inventaire", 500,
                Formats.date(12, 3, 2012), Formats.date(30, 3, 2012));
        Tache deploiement = creerTache(parc, "Deploiement", 1500,
                Formats.date(2, 4, 2012), Formats.date(15, 6, 2012));
        Tache formation = creerTache(parc, "Formation", 700,
                Formats.date(18, 6, 2012), Formats.date(30, 6, 2012));
        creerTache(parc, "Recette", 400,
                Formats.date(2, 7, 2012), Formats.date(20, 7, 2012));

        Tache analyseFacturation = creerTache(facturation, "Analyse", 1100,
                Formats.date(17, 9, 2012), Formats.date(12, 10, 2012));
        Tache conceptionFacturation = creerTache(facturation, "Conception", 1300,
                Formats.date(15, 10, 2012), Formats.date(30, 11, 2012));
        creerTache(facturation, "Developpement", 300,
                Formats.date(3, 12, 2012), Formats.date(28, 2, 2013));
        creerTache(facturation, "Tests", 450,
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

        analyseStock = creerTache(stock, "Analyse", 1200,
                Formats.date(1, 2, 2013), Formats.date(15, 2, 2013));
        conceptionStock = creerTache(stock, "Conception", 900,
                Formats.date(1, 3, 2013), Formats.date(10, 3, 2013));
        developpementStock = creerTache(stock, "Développement", 2500,
                Formats.date(1, 4, 2013), Formats.date(20, 4, 2013));
        recetteStock = creerTache(stock, "Recette", 600,
                Formats.date(1, 5, 2013), Formats.date(31, 5, 2013));

        realiser(analyseStock, Formats.date(10, 2, 2013), Formats.date(20, 2, 2013));
        realiser(conceptionStock, Formats.date(10, 3, 2013), Formats.date(15, 3, 2013));
        realiser(developpementStock, Formats.date(10, 4, 2013), Formats.date(25, 4, 2013));
        tacheService.update(analyseStock);
        tacheService.update(conceptionStock);
        tacheService.update(developpementStock);

        employeTacheService.affecter(said.getId(), analyseStock.getId(),
                Formats.date(1, 2, 2013), Formats.date(20, 2, 2013));
        employeTacheService.affecter(said.getId(), conceptionStock.getId(),
                Formats.date(1, 3, 2013), Formats.date(15, 3, 2013));
        employeTacheService.affecter(said.getId(), developpementStock.getId(),
                Formats.date(1, 4, 2013), Formats.date(25, 4, 2013));
        employeTacheService.affecter(omar.getId(), etude.getId(),
                Formats.date(9, 1, 2012), Formats.date(30, 1, 2012));
        employeTacheService.affecter(omar.getId(), maquettage.getId(),
                Formats.date(1, 2, 2012), Formats.date(27, 2, 2012));
        employeTacheService.affecter(omar.getId(), integration.getId(),
                Formats.date(2, 3, 2012), Formats.date(28, 4, 2012));
        employeTacheService.affecter(youssef.getId(), inventaire.getId(),
                Formats.date(12, 3, 2012), Formats.date(29, 3, 2012));
        employeTacheService.affecter(youssef.getId(), deploiement.getId(),
                Formats.date(2, 4, 2012), Formats.date(14, 6, 2012));
        employeTacheService.affecter(youssef.getId(), formation.getId(),
                Formats.date(18, 6, 2012), Formats.date(29, 6, 2012));
    }

    private Tache creerTache(Projet projet, String nom, double prix,
            Date debutPrevue, Date finPrevue) {
        Tache tache = new Tache(nom, prix);
        tache.setDateDebutPrevue(debutPrevue);
        tache.setDateFinPrevue(finPrevue);
        tache.setProjet(projet);
        tacheService.create(tache);
        return tache;
    }

    private void realiser(Tache tache, Date debutReelle, Date finReelle) {
        tache.setDateDebutReelle(debutReelle);
        tache.setDateFinReelle(finReelle);
    }

    // ------------------------------------------------------------------
    // Exercice 3 : etat civil
    // ------------------------------------------------------------------

    public void exercice3() {
        salima = new Femme("SALIMA", "RAMI", Formats.date(1, 1, 1950));
        amal = new Femme("AMAL", "ALI", Formats.date(12, 5, 1962));
        wafa = new Femme("WAFA", "ALAOUI", Formats.date(3, 7, 1970));
        karima = new Femme("KARIMA", "ALAMI", Formats.date(22, 11, 1965));
        nadia = new Femme("NADIA", "BENANI", Formats.date(14, 2, 1968));
        leila = new Femme("LEILA", "ZAHRA", Formats.date(30, 6, 1972));
        sana = new Femme("SANA", "IDRISSI", Formats.date(9, 9, 1975));
        hind = new Femme("HIND", "OUAZZANI", Formats.date(18, 3, 1978));
        ghita = new Femme("GHITA", "BERADA", Formats.date(25, 12, 1980));
        imane = new Femme("IMANE", "TAZI", Formats.date(7, 8, 1985));
        Femme[] femmes = {salima, amal, wafa, karima, nadia, leila, sana, hind, ghita, imane};
        for (Femme femme : femmes) {
            femmeService.create(femme);
        }

        hommeSaid = new Homme("SAFI", "SAID", Formats.date(15, 4, 1955));
        hommeOmar = new Homme("ALAMI", "OMAR", Formats.date(10, 10, 1960));
        hommeYoussef = new Homme("BENANI", "YOUSSEF", Formats.date(20, 1, 1963));
        hommeHassan = new Homme("KADIRI", "HASSAN", Formats.date(5, 5, 1970));
        hommeRachid = new Homme("TAZI", "RACHID", Formats.date(11, 11, 1975));
        Homme[] hommes = {hommeSaid, hommeOmar, hommeYoussef, hommeHassan, hommeRachid};
        for (Homme homme : hommes) {
            hommeService.create(homme);
        }

        mariageService.marier(hommeSaid.getId(), salima.getId(),
                Formats.date(3, 9, 1990), null, 4);
        mariageService.marier(hommeSaid.getId(), amal.getId(),
                Formats.date(3, 9, 1995), null, 2);
        mariageService.marier(hommeSaid.getId(), wafa.getId(),
                Formats.date(4, 11, 2000), null, 3);
        mariageService.marier(hommeSaid.getId(), karima.getId(),
                Formats.date(3, 9, 1989), Formats.date(3, 9, 1990), 0);

        mariageService.marier(hommeOmar.getId(), nadia.getId(),
                Formats.date(15, 1, 1990), null, 3);
        mariageService.marier(hommeOmar.getId(), leila.getId(),
                Formats.date(20, 6, 1991), null, 1);
        mariageService.marier(hommeOmar.getId(), sana.getId(),
                Formats.date(12, 3, 1993), null, 2);
        mariageService.marier(hommeOmar.getId(), hind.getId(),
                Formats.date(8, 9, 1994), null, 0);

        mariageService.marier(hommeYoussef.getId(), nadia.getId(),
                Formats.date(2, 2, 1985), Formats.date(1, 1, 1988), 1);
        mariageService.marier(hommeHassan.getId(), leila.getId(),
                Formats.date(5, 5, 1986), Formats.date(4, 4, 1989), 0);
        mariageService.marier(hommeRachid.getId(), imane.getId(),
                Formats.date(10, 10, 2005), null, 2);
    }
}
