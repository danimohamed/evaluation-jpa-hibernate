package ma.projet;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

import ma.projet.service.AbstractService;
import ma.projet.util.HibernateUtil;

/**
 * Base commune des tests Hibernate.
 *
 * <p>La base H2 est declaree avec {@code DB_CLOSE_DELAY=-1} et
 * {@code hbm2ddl.auto=update} : le schema et les donnees survivent donc a la
 * fermeture d'une session. Sans nettoyage explicite les tests se pollueraient
 * mutuellement, et les identifiants generes ne seraient plus predictibles.</p>
 *
 * <p>Les tables sont videes avant et apres chaque test en respectant l'ordre des
 * cles etrangeres, puis les compteurs IDENTITY sont redemarres a 1. Les tables
 * {@code homme} et {@code femme} ne sont <strong>pas</strong> redemarrees : ce
 * sont des tables filles de l'heritage {@code JOINED}, leur cle primaire est
 * partagee avec {@code personne} et n'est pas une colonne IDENTITY.</p>
 */
public abstract class AbstractJpaTest {

    /** Tables filles avant tables parentes (les contraintes de FK l'exigent). */
    private static final List<String> TABLES_PAR_ORDRE_DE_SUPPRESSION = Arrays.asList(
            "ligne_commande",
            "employe_tache",
            "mariage",
            "commande",
            "tache",
            "produit",
            "projet",
            "employe",
            "categorie",
            "homme",
            "femme",
            "personne"
    );

    /**
     * Tables dont la cle primaire est une colonne IDENTITY : {@code DELETE} ne
     * remet pas le compteur a zero, on le redemarre pour que les identifiants
     * generes soient predictibles (1, 2, 3, ...) dans chaque test.
     */
    private static final List<String> TABLES_A_IDENTITY = Arrays.asList(
            "categorie",
            "commande",
            "employe",
            "employe_tache",
            "ligne_commande",
            "mariage",
            "personne",
            "produit",
            "projet",
            "tache"
    );

    @Before
    public void viderAvantLeTest() {
        viderToutesLesTables();
    }

    @After
    public void viderApresLeTest() {
        viderToutesLesTables();
    }

    protected static void viderToutesLesTables() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            for (String table : TABLES_PAR_ORDRE_DE_SUPPRESSION) {
                session.createNativeQuery("DELETE FROM " + table).executeUpdate();
            }
            for (String table : TABLES_A_IDENTITY) {
                session.createNativeQuery("ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH 1")
                        .executeUpdate();
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Execute une methode d'affichage d'un service en capturant son flux de
     * sortie. Le flux est injecte via {@link AbstractService#setOut(PrintStream)}
     * : {@code System.setOut} n'est donc pas necessaire et le test peut lire la
     * sortie brute, sans les journaux Hibernate (ecrits sur stderr).
     *
     * @return la sortie produite, decodee en UTF-8.
     */
    protected static String capturer(AbstractService<?> service, Runnable action) {
        ByteArrayOutputStream tampon = new ByteArrayOutputStream();
        PrintStream flux = new PrintStream(tampon, true, StandardCharsets.UTF_8);
        PrintStream precedent = service.getOut();
        service.setOut(flux);
        try {
            action.run();
        } finally {
            flux.flush();
            service.setOut(precedent);
        }
        return new String(tampon.toByteArray(), StandardCharsets.UTF_8);
    }

    /** Decoupe une sortie capturee en lignes (les lignes vides sont conservees). */
    protected static List<String> lignes(String sortie) {
        String[] decoupage = sortie.split("\\R", -1);
        List<String> resultat = new ArrayList<>(Arrays.asList(decoupage));
        while (!resultat.isEmpty() && resultat.get(resultat.size() - 1).isEmpty()) {
            resultat.remove(resultat.size() - 1);
        }
        return resultat;
    }

    /** @return la premiere ligne commencant par le prefixe donne, ou {@code null}. */
    protected static String ligne(List<String> lignes, String prefixe) {
        for (String ligne : lignes) {
            if (ligne.startsWith(prefixe)) {
                return ligne;
            }
        }
        return null;
    }

    /** Verifie qu'au moins une ligne de la sortie commence par le prefixe donne. */
    protected static boolean contientLigne(List<String> lignes, String prefixe) {
        return ligne(lignes, prefixe) != null;
    }
}
