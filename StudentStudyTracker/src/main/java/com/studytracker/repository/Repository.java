package com.studytracker.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD repository contract.
 * All data repositories implement this interface (OOP: abstraction + polymorphism).
 *
 * @param <T>  Entity type
 * @param <ID> Identifier type
 */
public interface Repository<T, ID> {

    /**
     * Saves a new entity or updates an existing one.
     */
    T save(T entity);

    /**
     * Finds an entity by its unique ID.
     */
    Optional<T> findById(ID id);

    /**
     * Returns all stored entities.
     */
    List<T> findAll();

    /**
     * Deletes the entity with the given ID.
     * @return true if deletion was successful
     */
    boolean deleteById(ID id);

    /**
     * Returns the number of entities in the repository.
     */
    int count();

    /**
     * Returns true if an entity with the given ID exists.
     */
    boolean existsById(ID id);
}
