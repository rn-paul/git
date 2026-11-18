package repository;

import domain.Entity;
import domain.validators.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    protected Map<ID, E> entities;
    public InMemoryRepository(Validator<E> validator){
        this.validator=validator;
        this.entities=new HashMap<>();

    }
    @Override
    public Optional<E> findOne(ID id){
        if(id==null) throw new IllegalArgumentException("Id cannot be empty;");
        return Optional.ofNullable(entities.get(id));
    }
    @Override
    public Iterable<E> findAll(){
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity){
        if(entity==null) throw new IllegalArgumentException("Entity cannot be empty");
        validator.validate(entity);
        if(entities.containsKey(entity.getId())){
            return Optional.of(entities.get(entity.getId()));
        }
        entities.put(entity.getId(),entity);
        return Optional.empty();
    }
    @Override
    public Optional<E> delete(ID id){
        if(id==null) throw new IllegalArgumentException("ID cannot be null");
        return Optional.ofNullable(entities.remove(id));

    }



}
