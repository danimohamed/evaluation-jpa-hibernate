package ma.projet.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.beans.Personne;
import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.LigneCommande;
import ma.projet.classes.Produit;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;

/**
 * Point d'entree unique vers la SessionFactory Hibernate.
 *
 * <p>La configuration est lue depuis {@code application.properties} (present sur
 * le classpath), comme demande par l'enonce, puis les classes annotees des trois
 * exercices sont enregistrees explicitement : l'evaluation n'utilise donc ni
 * {@code persistence.xml} ni {@code hibernate.cfg.xml}.</p>
 */
public final class HibernateUtil {

    private static final String CONFIG_FILE = "application.properties";

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
        // classe utilitaire
    }

    /**
     * @return la SessionFactory, construite au premier appel puis reutilisee.
     *         Elle est reconstruite si elle a ete fermee par {@link #shutdown()}.
     */
    public static SessionFactory getSessionFactory() {
        SessionFactory local = sessionFactory;
        if (local == null || local.isClosed()) {
            synchronized (HibernateUtil.class) {
                local = sessionFactory;
                if (local == null || local.isClosed()) {
                    local = buildSessionFactory();
                    sessionFactory = local;
                }
            }
        }
        return local;
    }

    /** @return une nouvelle session, a fermer par l'appelant. */
    public static Session openSession() {
        return getSessionFactory().openSession();
    }

    /**
     * @return la session liee au contexte courant ({@code thread}).
     *         Necessite une transaction en cours.
     */
    public static Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }

    /** Ferme la SessionFactory et libere le pool de connexions. */
    public static void shutdown() {
        SessionFactory local = sessionFactory;
        if (local != null && !local.isClosed()) {
            local.close();
        }
        sessionFactory = null;
    }

    private static SessionFactory buildSessionFactory() {
        Properties properties = loadProperties();
        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        registerAnnotatedClasses(configuration);
        return configuration.buildSessionFactory();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream in = HibernateUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(CONFIG_FILE + " est introuvable sur le classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Lecture impossible de " + CONFIG_FILE, e);
        }
        return properties;
    }

    private static void registerAnnotatedClasses(Configuration configuration) {
        // Exercice 1 - gestion de stock
        configuration.addAnnotatedClass(Categorie.class);
        configuration.addAnnotatedClass(Produit.class);
        configuration.addAnnotatedClass(Commande.class);
        configuration.addAnnotatedClass(LigneCommande.class);
        // Exercice 2 - gestion de projets
        configuration.addAnnotatedClass(Projet.class);
        configuration.addAnnotatedClass(Tache.class);
        configuration.addAnnotatedClass(Employe.class);
        configuration.addAnnotatedClass(EmployeTache.class);
        // Exercice 3 - etat civil
        configuration.addAnnotatedClass(Personne.class);
        configuration.addAnnotatedClass(Homme.class);
        configuration.addAnnotatedClass(Femme.class);
        configuration.addAnnotatedClass(Mariage.class);
    }
}
