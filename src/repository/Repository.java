package repository;

import domain.Entity;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *CRUD repository interface
 *
 * @param <ID>
 * @param <E>
 */

public interface Repository <ID,E extends Entity<ID>>{
    /**
     * Searches for an entity based on id
     * @param id
     * @return the entity or an empty optional
     */
    Optional<E> findOne(ID id);


    Iterable<E> findAll();

    /**
     * Filters the entities in the repository based on a given condition
     * @param filter representing the given condition
     * @return List containing the entities that match the filter condition
     */
    default List<E> findAll(Predicate<E> filter){
        return StreamSupport.stream(findAll().spliterator(),false)
                .filter(filter)
                .collect(Collectors.toList());

    }

    /**
     * Stores a given entity
     * @param Entity
     * @return the entity
     */
    Optional<E> save(E Entity);

    /**
     * Deletes an entity based on id
     * @param id
     * @return link to the found entity , or an empty optional
     * @throws IllegalArgumentException if the provided id is null
     */
    Optional<E> delete(ID id);
}
