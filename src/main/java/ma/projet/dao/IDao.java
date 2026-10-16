package ma.projet.dao;

import java.util.List;

/**
 * Contrat generique de la couche DAO, commun aux trois exercices.
 *
 * @param <T> le type de l'entite persistee
 */
public interface IDao<T> {

    /** Persiste une nouvelle entite. */
    boolean create(T objet);

    /** Met a jour une entite deja persistee. */
    boolean update(T objet);

    /** Supprime une entite persistee. */
    boolean delete(T objet);

    /** @return l'entite d'identifiant donne, ou {@code null} si absente. */
    T findById(int id);

    /** @return toutes les entites de la table, triees par identifiant. */
    List<T> findAll();
}
