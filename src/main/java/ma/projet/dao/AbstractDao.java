package ma.projet.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ma.projet.util.HibernateUtil;

/**
 * Implementation generique de {@link IDao}, partagee par les services des trois
 * exercices : chaque service n'a donc a ecrire que ses requetes metier.
 *
 * @param <T> le type de l'entite persistee
 */
public abstract class AbstractDao<T> implements IDao<T> {

    private final Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    /** Ouvre une session dediee, a fermer par l'appelant. */
    protected Session openSession() {
        return HibernateUtil.openSession();
    }

    @Override
    public boolean create(T objet) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            session.save(objet);
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(transaction);
            System.err.println("Echec de create(" + entityClass.getSimpleName() + ") : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(T objet) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            session.update(objet);
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(transaction);
            System.err.println("Echec de update(" + entityClass.getSimpleName() + ") : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(T objet) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            session.delete(session.contains(objet) ? objet : session.merge(objet));
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(transaction);
            System.err.println("Echec de delete(" + entityClass.getSimpleName() + ") : " + e.getMessage());
            return false;
        }
    }

    @Override
    public T findById(int id) {
        try (Session session = openSession()) {
            return session.get(entityClass, id);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        } catch (RuntimeException e) {
            System.err.println("Echec de findAll(" + entityClass.getSimpleName() + ") : " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
