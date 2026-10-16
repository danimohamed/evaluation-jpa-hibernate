# Évaluation — JPA & Hibernate : gestion de stock, gestion de projets et état civil

Projet Maven autonome répondant aux **trois exercices** de l'évaluation pratique du cours
« JPA & Hibernate ». Chaque exercice est une application complète organisée en couches
(**persistance / DAO / service / démonstration console**) :

1. **Exercice 1 — Application de gestion de stock** : `Categorie`, `Produit`, `Commande`,
   `LigneCommande`, avec requêtes nommées et rapports d'affichage conformes à l'énoncé.
2. **Exercice 2 — Application de gestion de projets** : `Projet`, `Tache`, `Employe`,
   `EmployeTache`, calcul du coût réalisé, affectations et recherche multi-critères.
3. **Exercice 3 — Application de gestion de l'état civil** : héritage **`JOINED`**
   `Personne` → `Homme` / `Femme`, plus `Mariage` (mariages en cours et échoués), avec
   **requête native nommée** et **API Criteria** comme exigé.

Techniques couvertes : annotations JPA (`@Entity`, `@ManyToOne`, `@OneToMany`, `@OneToOne`,
`@Inheritance(JOINED)`, `@DiscriminatorColumn`), *Named Queries*, ***Named Native Query***,
JPQL (`JOIN FETCH`, agrégats, sous-requêtes), **API Criteria** avec sous-requête, DAO générique,
services métier, et configuration Hibernate via `application.properties`.

Base de données **H2 en mémoire** — aucune installation externe n'est requise.

---

## Prérequis

| Outil | Version testée |
|-------|----------------|
| JDK   | 21 (le code cible Java 11) |
| Maven | 3.9.9 |

---

## Démarrage rapide

```bash
# Compiler et lancer les tests
mvn test

# Construire le jar
mvn package

# Exécuter la démonstration console (les 3 exercices)
mvn clean compile exec:java
```

Le jar produit est `target/evaluation-jpa-hibernate-1.0.0.jar`.

---

## Structure du projet

```
evaluation-jpa-hibernate/
├── pom.xml
└── src
    ├── main
    │   ├── java/ma/projet
    │   │   ├── app
    │   │   │   └── EvaluationApp.java                # démonstration console (3 exercices, 23 sections)
    │   │   ├── util
    │   │   │   ├── HibernateUtil.java                # SessionFactory natif (application.properties)
    │   │   │   └── Formats.java                      # formats de dates et de montants de l'énoncé
    │   │   ├── dao
    │   │   │   ├── IDao.java                         # interface générique de persistance
    │   │   │   └── AbstractDao.java                  # implémentation générique (session + transaction)
    │   │   ├── classes                              # exercices 1 et 2
    │   │   │   ├── Categorie.java                    # @OneToMany(mappedBy = "categorie")
    │   │   │   ├── Produit.java                      # @ManyToOne + @NamedQueries
    │   │   │   ├── Commande.java                     # @OneToMany(mappedBy = "commande")
    │   │   │   ├── LigneCommande.java                # table porteuse (commande, produit, quantité)
    │   │   │   ├── Projet.java                       # @ManyToOne chef de projet
    │   │   │   ├── Tache.java                        # dates prévues / réelles, prix, projet
    │   │   │   ├── Employe.java                      # @NamedQueries (tâches réalisées, projets gérés)
    │   │   │   └── EmployeTache.java                 # table porteuse (employé, tâche, dates)
    │   │   ├── beans                                # exercice 3
    │   │   │   ├── Personne.java                     # racine @Inheritance(JOINED)
    │   │   │   ├── Homme.java                        # sous-classe (table homme)
    │   │   │   ├── Femme.java                        # sous-classe (table femme) + @NamedQueries
    │   │   │   └── Mariage.java                      # @NamedQuery + @NamedNativeQuery
    │   │   └── service
    │   │       ├── AbstractService.java              # CRUD générique + PrintStream injectable
    │   │       ├── CategorieService.java             # exercice 1
    │   │       ├── ProduitService.java               # exercice 1
    │   │       ├── CommandeService.java              # exercice 1
    │   │       ├── LigneCommandeService.java         # exercice 1
    │   │       ├── ProjetService.java                # exercice 2
    │   │       ├── TacheService.java                 # exercice 2
    │   │       ├── EmployeService.java               # exercice 2
    │   │       ├── EmployeTacheService.java          # exercice 2
    │   │       ├── HommeService.java                 # exercice 3 (Criteria)
    │   │       ├── FemmeService.java                 # exercice 3
    │   │       └── MariageService.java               # exercice 3
    │   └── resources
    │       └── application.properties                # configuration Hibernate native
    └── test/java/ma/projet
        ├── AbstractJpaTest.java                      # remise à zéro de la base + helpers d'assertion
        ├── JeuDeDonnees.java                         # jeu de données des 3 exercices (ordre de l'énoncé)
        ├── Exercice1Test.java                        # 17 tests
        ├── Exercice2Test.java                        # 21 tests
        └── Exercice3Test.java                        # 19 tests
```

---

## Dépendances (`pom.xml`)

| Dépendance | Version | Portée | Rôle |
|------------|---------|--------|------|
| `javax.persistence-api` | 2.2 | compile | API JPA (annotations, JPQL, Criteria) |
| `hibernate-core` | 5.6.15.Final | compile | Implémentation JPA |
| `com.h2database:h2` | 2.1.214 | runtime | Base en mémoire |
| `junit:junit` | 4.13.2 | test | Tests unitaires |
| `net.bytebuddy:byte-buddy` | 1.14.18 | compile | **Correctif JDK 21** (voir ci-dessous) |

> **Note sur les versions.** L'énoncé ne fixe aucune version de dépendance : il impose seulement
> la génération d'une base **MySQL**, la classe `HibernateUtil` et un fichier
> `application.properties`. Aucun serveur MySQL n'étant disponible dans l'environnement
> d'exécution, le projet utilise **H2 en mémoire** en conservant **exactement** les noms imposés :
> packages `ma.projet.classes`, `ma.projet.beans`, `ma.projet.dao`, `ma.projet.service`,
> `ma.projet.util`, et les noms de tables et de colonnes de l'énoncé. `byte-buddy 1.14.18` est
> épinglé car la version embarquée par Hibernate 5.6 est incapable de lire les fichiers de classe
> du JDK 21 (`Unsupported class file major version 65`).

---

## Configuration JPA

`src/main/resources/application.properties` :

```properties
hibernate.connection.driver_class=org.h2.Driver
hibernate.connection.url=jdbc:h2:mem:evaluation_jpa;DB_CLOSE_DELAY=-1
hibernate.connection.username=sa
hibernate.connection.password=
hibernate.dialect=org.hibernate.dialect.H2Dialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=false
hibernate.format_sql=true
hibernate.current_session_context_class=thread
```

`HibernateUtil` construit la `SessionFactory` à partir de ce fichier et enregistre les
**12 classes annotées** des trois exercices. Les équivalents MySQL de chaque propriété sont
laissés en commentaire dans le fichier.

`DB_CLOSE_DELAY=-1` maintient la base en mémoire vivante pendant toute la JVM : le schéma et les
données **survivent à la fermeture de la `SessionFactory`**. Les tests doivent donc nettoyer
explicitement les tables entre chaque méthode (voir `AbstractJpaTest`).

---

## Points clés du TP

### 1. Couche DAO / Service générique

`IDao<T>` définit le contrat (`create`, `update`, `delete`, `findById`, `findAll`) et
`AbstractDao<T>` l'implémente une seule fois pour les douze entités : chaque opération ouvre une
session (`HibernateUtil.openSession()`), l'écriture s'exécute dans une transaction refermée par
`rollback` sur `RuntimeException`, et la session est fermée par *try-with-resources*.

`AbstractService<T>` complète la couche avec l'affichage : il expose un `PrintStream` **injectable**
(`getOut()` / `setOut(...)`), ce qui permet aux tests de capturer la sortie console sans toucher à
`System.out`.

### 2. Exercice 1 — gestion de stock

| Exigence de l'énoncé | Implémentation |
|----------------------|----------------|
| Produits par catégorie | `CategorieService.findProduits(int)` + `afficherProduits(int)` |
| Produits dont le prix > 100 DH, **via requête nommée** | `@NamedQuery Produit.findPrixSuperieurA` → `ProduitService.findByPrixSuperieurA(double)` |
| Produits commandés entre deux dates | `ProduitService.findCommandesEntreDates(Date, Date)` + `afficherProduitsCommandesEntreDates(...)` |
| Produits d'une commande donnée | `ProduitService.findLignesDuneCommande(int)` + `afficherProduitsDuneCommande(int)` |
| Commandes entre deux dates | `CommandeService.findByDates(Date, Date)` + `afficherCommandesEntreDates(...)` |
| Ajout d'une ligne de commande | `LigneCommandeService.ajouterLigne(int commande, int produit, int quantite)` |

Les rapports reproduisent **au caractère près** les tableaux de l'énoncé (en-têtes
`Référence   Prix    Quantité`, dates longues françaises `14 Mars 2013`).

### 3. Exercice 2 — gestion de projets

- `ProjetService.findTachesRealisees(int)` filtre sur `dateDebutReelle IS NOT NULL` et
  `findTachesPlanifiees(int)` sur `dateDebutPrevue IS NOT NULL` — deux requêtes JPQL distinctes,
  triées par date.
- `ProjetService.afficherCoutRealise(int)` agrège `SUM(t.prix)` sur les tâches réalisées
  (**4600 DH** pour le projet 4 dans le jeu de données).
- `ProjetService.findPremiereDateDebut()` renvoie la plus ancienne date de début de projet
  (**2 Janvier 2012**) via une requête d'agrégat `MIN`.
- `EmployeService.findTachesRealisees(int)` / `findProjetsGeres(int)` exploitent les requêtes
  nommées de `Employe`, avec **`JOIN FETCH et.tache.projet`** pour que le rapport console puisse
  lire le nom du projet sans `LazyInitializationException`.
- `TacheService` : tâches dont le prix dépasse un seuil, tâches réalisées entre deux dates,
  employés affectés à une tâche ; `EmployeTacheService.affecter(...)` crée une affectation et
  renvoie `null` si l'employé ou la tâche est inconnu.
- `Tache.isRealisee()` dérive l'état de la tâche de la présence de ses dates réelles.

### 4. Exercice 3 — gestion de l'état civil

- **Héritage `JOINED`** : `Personne` (table `personne`, colonne discriminante `type_personne`) est
  la racine de `Homme` (table `homme`) et `Femme` (table `femme`). Les deux sous-classes
  **partagent la clé primaire** de `personne` : dans le jeu de données, la première femme
  (`SALIMA RAMI`) porte l'identifiant **1** et le premier homme (`SAFI SAID`) l'identifiant **11**,
  puisqu'il est inséré après les dix femmes — c'est exactement ce que vérifie
  `lHeritageJointePartageLaCleDeLaTablePersonne`.
- **Requête native nommée** (exigée par l'énoncé) :
  `@NamedNativeQuery Mariage.nombreEnfantsFemmeEntreDates` →
  `SELECT COALESCE(SUM(m.nombre_enfants), 0) FROM mariage m WHERE m.femme_id = :femmeId AND m.date_debut BETWEEN :debut AND :fin`
- **Requête nommée** pour les femmes mariées au moins deux fois :
  `@NamedQuery Femme.findMarieesAuMoinsDeuxFois` (sous-requête de comptage `>= :nombre`).
- **API Criteria avec sous-requête** (exigée par l'énoncé) :
  `HommeService.compterHommesMariesAQuatreFemmesEntreDates(...)` construit un `CriteriaQuery<Long>`
  dont la sous-requête compte les épouses distinctes de chaque homme sur la période.
- `MariageService.afficherMariagesDunHomme(int)` restitue le rapport de l'énoncé : mariages
  **en cours** (sans date de fin) puis mariages **échoués** (avec date de fin), alignés en colonnes
  de 14 caractères, et `afficherStatistiques(int)` affiche les deux compteurs.

### 5. Pièges rencontrés et corrigés

| Piège | Symptôme | Correction |
|-------|----------|------------|
| `SELECT DISTINCT` + `ORDER BY <association>.id` | H2 : `Order by expression "X.PRODUIT_ID" must be in the result list` | Trier par une **colonne scalaire** de l'entité sélectionnée (`lc.produit.reference`, `et.tache.nom`) |
| Association `LAZY` lue après fermeture de la session | `LazyInitializationException` sur `Projet$HibernateProxy.getNom()` | Ajouter **`JOIN FETCH`** dans les requêtes nommées concernées |
| `CONCAT` à trois arguments | Non portable sur H2 / Hibernate 5.6 | `CONCAT('%', CONCAT(:terme, '%'))` |
| Compteurs `IDENTITY` non réinitialisés par `DELETE` | Identifiants imprévisibles entre deux tests | `ALTER TABLE <table> ALTER COLUMN id RESTART WITH 1` |

---

## Tests

```
Tests run: 57, Failures: 0, Errors: 0, Skipped: 0
```

| Classe de test | Tests | Couverture |
|----------------|-------|------------|
| `Exercice1Test` | 17 | produits par catégorie, prix > 100 DH (requête nommée), produits commandés entre deux dates, lignes d'une commande, commandes entre deux dates, ajout de ligne, et les trois rapports console de l'énoncé |
| `Exercice2Test` | 21 | tâches réalisées / planifiées d'un projet, coût réalisé (4600 DH), tâches réalisées par un employé, projets gérés, tâches réalisées entre deux dates, tâches > 1000 DH, employés d'une tâche, affectations d'un employé, première date de début, rapports console |
| `Exercice3Test` | 19 | liste des femmes, femme la plus âgée, épouses entre deux dates, nombre d'enfants (requête native nommée), femmes mariées au moins deux fois, hommes mariés à quatre femmes (Criteria), mariages d'un homme, statistiques, clé partagée de l'héritage `JOINED` |

**Remise à zéro entre les tests.** `AbstractJpaTest` supprime les tables dans l'ordre imposé par
les clés étrangères :

```
ligne_commande → employe_tache → mariage → commande → tache → produit
              → projet → employe → categorie → homme → femme → personne
```

`DELETE` ne réinitialise pas les compteurs d'`IDENTITY` : la même méthode exécute donc
`ALTER TABLE <table> ALTER COLUMN id RESTART WITH 1` sur les **dix** tables dotées d'une colonne
`id` générée. `homme` et `femme` en sont volontairement exclues : issues d'un héritage `JOINED`,
elles **partagent** la clé primaire de `personne` et ne possèdent pas de colonne `IDENTITY`
propre — `RESTART` y échoue.

**Assertions sur les rapports console.** `AbstractJpaTest.capturer(service, action)` remplace le
`PrintStream` du service par un flux UTF-8, exécute l'action, puis restaure le flux précédent.
Les tests vérifient la **présence** et le **contenu** de chaque ligne attendue, et le **nombre de
lignes** de chaque rapport ; ils ne dépendent donc pas de `System.out`.

---

## Résultat de la démonstration

`java -Dfile.encoding=UTF-8 -cp target/classes:<dépendances> ma.projet.app.EvaluationApp` :

```text
========================================================================
EXERCICE 1 : GESTION DES COMMANDES
========================================================================

--- 1.1 - Produits de la categorie Informatique ---
Categorie : Informatique (3 produit(s))
Référence   Prix      
ES12        120 DH    
ZR85        100 DH    
EE85        200 DH    

--- 1.2 - Produits dont le prix est superieur a 100 DH ---
Produits dont le prix dépasse 100 DH :
Référence   Prix    
ES12        120 DH  
EE85        200 DH  

--- 1.3 - Produits commandes entre le 01/01/2013 et le 28/02/2013 ---
Produits commandés entre le 01/01/2013 et le 28/02/2013 :
Référence   Prix    
CD45        50 DH   
EE85        200 DH  
ES12        120 DH  
LL10        90 DH   
ZR85        100 DH  

--- 1.4 - Produits de la commande numero 4 ---
Commande : 4     Date : 14 Mars 2013
Liste des produits :
Référence   Prix    Quantité
ES12        120 DH  7
ZR85        100 DH  14
EE85        200 DH  5

--- 1.5 - Commandes passees entre le 01/01/2013 et le 28/02/2013 ---
Commandes entre le 01/01/2013 et le 28/02/2013 :
Num   Date
1     10 Janvier 2013
2     20 Janvier 2013
3     5 Février 2013

========================================================================
EXERCICE 2 : GESTION DES PROJETS ET DES TACHES
========================================================================

--- 2.1 - Taches realisees du projet 4 (Gestion de stock) ---
Projet : 4      Nom : Gestion de stock     Date début : 14 Janvier 2013
Liste des tâches:
Num Nom            Date Début Réelle   Date Fin Réelle
12  Analyse        10/02/2013          20/02/2013
13  Conception     10/03/2013          15/03/2013
14  Développement  10/04/2013          25/04/2013

--- 2.2 - Taches planifiees du projet 4 (Gestion de stock) ---
Projet : 4      Nom : Gestion de stock     Date début : 14 Janvier 2013
Liste des tâches planifiées:
Num Nom            Date Début Prévue   Date Fin Prévue
12  Analyse        01/02/2013          15/02/2013
13  Conception     01/03/2013          10/03/2013
14  Développement  01/04/2013          20/04/2013
15  Recette        01/05/2013          31/05/2013

--- 2.3 - Cout des taches realisees du projet 4 ---
Coût total des tâches réalisées : 4600 DH

--- 2.4 - Taches realisees par l'employe SAID SAFI ---
Tâches réalisées par SAID SAFI :
Num Nom                 Prix        Début Réelle  Fin Réelle    Projet
12  Analyse             1200 DH     10/02/2013    20/02/2013    Gestion de stock
13  Conception          900 DH      10/03/2013    15/03/2013    Gestion de stock
14  Développement       2500 DH     10/04/2013    25/04/2013    Gestion de stock

--- 2.5 - Projets geres par l'employe SAID SAFI ---
Projets gérés par SAID SAFI :
Num Nom                      Date Début
1   Refonte du site web      2 Janvier 2012
4   Gestion de stock         14 Janvier 2013

--- 2.6 - Taches realisees entre le 01/01/2013 et le 31/12/2013 ---
Tâches réalisées entre le 01/01/2013 et le 31/12/2013 :
Num Nom                 Début Réelle        Fin Réelle
12  Analyse             10/02/2013          20/02/2013
13  Conception          10/03/2013          15/03/2013
14  Développement       10/04/2013          25/04/2013

--- 2.7 - Taches dont le prix depasse 1000 DH ---
Tâches dont le prix dépasse 1000 DH :
Num Nom                 Prix        Projet
3   Integration         1400 DH     Refonte du site web
5   Deploiement         1500 DH     Migration du parc informatique
8   Analyse             1100 DH     Application de facturation
9   Conception          1300 DH     Application de facturation
12  Analyse             1200 DH     Gestion de stock
14  Développement       2500 DH     Gestion de stock

--- 2.8 - Employes affectes a la tache Analyse (numero 12) ---
Employés affectés à la tâche 12 :
Num Employé                       Début       Fin
1   SAID SAFI                     01/02/2013  20/02/2013

--- 2.9 - Affectations de l'employe YOUSSEF BENANI ---
Affectations de YOUSSEF BENANI :
Num Tâche               Début       Fin         Réalisée
4   Inventaire          12/03/2012  29/03/2012  oui
5   Deploiement         02/04/2012  14/06/2012  oui
6   Formation           18/06/2012  29/06/2012  oui

--- 2.10 - Date de debut du premier projet ---
Premiere date de debut : 2 Janvier 2012

========================================================================
EXERCICE 3 : ETAT CIVIL
========================================================================

--- 3.1 - Liste des femmes ---
Liste des femmes :
1. SALIMA RAMI (née le 01/01/1950)
2. AMAL ALI (née le 12/05/1962)
3. WAFA ALAOUI (née le 03/07/1970)
4. KARIMA ALAMI (née le 22/11/1965)
5. NADIA BENANI (née le 14/02/1968)
6. LEILA ZAHRA (née le 30/06/1972)
7. SANA IDRISSI (née le 09/09/1975)
8. HIND OUAZZANI (née le 18/03/1978)
9. GHITA BERADA (née le 25/12/1980)
10. IMANE TAZI (née le 07/08/1985)

--- 3.2 - Femme la plus agee ---
Femme la plus âgée : SALIMA RAMI (née le 01/01/1950)

--- 3.3 - Epouses de SAFI SAID mariees entre le 01/01/1990 et le 31/12/1995 ---
Épouses de SAFI SAID mariées entre le 01/01/1990 et le 31/12/1995 :
  SALIMA RAMI (née le 01/01/1950)
  AMAL ALI (née le 12/05/1962)

--- 3.4 - Nombre d'enfants de SALIMA RAMI entre le 01/01/1990 et le 31/12/1995 ---
Nombre d'enfants de SALIMA RAMI nés entre le 01/01/1990 et le 31/12/1995 : 4

--- 3.5 - Femmes mariees au moins deux fois ---
Femmes mariées au moins 2 fois :
1. NADIA BENANI
2. LEILA ZAHRA

--- 3.6 - Nombre d'hommes maries a quatre femmes entre le 01/01/1990 et le 31/12/1995 ---
Nombre d'hommes mariés à quatre femmes entre le 01/01/1990 et le 31/12/1995 : 1

--- 3.7 - Mariages de SAFI SAID avec tous les details ---
Nom : SAFI SAID
Mariages En Cours :
1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4
2. Femme : AMAL ALI      Date Début : 03/09/1995    Nbr Enfants : 2
3. Femme : WAFA ALAOUI   Date Début : 04/11/2000    Nbr Enfants : 3

Mariages échoués :
1. Femme : KARIMA ALAMI  Date Début : 03/09/1989
Date Fin : 03/09/1990    Nbr Enfants : 0

--- 3.8 - Statistiques des mariages de SAFI SAID ---
Mariages en cours : 3
Mariages échoués  : 1
```

---

## Écarts assumés par rapport à l'énoncé

| Point de l'énoncé | Ce qui est livré | Raison |
|-------------------|------------------|--------|
| « Générer la base de données **MySQL** » | H2 2.1.214 en mémoire (`jdbc:h2:mem:evaluation_jpa;DB_CLOSE_DELAY=-1`) | Aucun serveur MySQL disponible dans l'environnement d'exécution ; les propriétés MySQL équivalentes sont fournies en commentaire dans `application.properties` |
| Contexte « Java EE » (couche service destinée à un conteneur) | Application **Java SE** packagée en `jar`, avec une démonstration console | Aucun conteneur de servlets disponible ; la séparation persistance / DAO / service reste identique |
| « Écrire des programmes de test pour valider chaque fonctionnalité » | 57 tests JUnit 4 répartis en trois classes, un par exercice | Les tests couvrent chaque méthode publique et chaque rapport de l'énoncé |
| `hibernate-core` / `h2` : aucune version imposée | `hibernate-core 5.6.15.Final` + `h2 2.1.214` + `byte-buddy 1.14.18` | Seule combinaison compatible avec le JDK 21 de l'environnement |
| Ordre de tri des requêtes avec `DISTINCT` | Tri par colonne scalaire (`reference`, `nom`) plutôt que par clé étrangère | Contrainte H2 : `ORDER BY` d'une association absente de la liste de sélection |

---

## Auteur

**Mohamed Dani** — [@danimohamed](https://github.com/danimohamed)
