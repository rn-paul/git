package repository;

import domain.Friendship;
import domain.validators.Validator;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


public class FriendshipFileRepository extends AbstractFileRepository<Long,Friendship>{
    public FriendshipFileRepository(Validator<Friendship> validator,String filename){
        super(validator, filename);
    }
    @Override
    protected Friendship extractEntity(List<String> attributes){
        Friendship friendship=new Friendship(
                Long.parseLong(attributes.get(1)),
                Long.parseLong(attributes.get(2)),
                LocalDateTime.parse(attributes.get(3))
        );
        friendship.setID(Long.parseLong(attributes.get(0)));
        return friendship;
    }
    @Override
    protected String createEntityAsString(Friendship entity){
        return entity.getId()+";"+entity.getIdUser1()+";"+entity.getIdUser2()+";"+entity.getData();
    }
    @Override
    public Optional<Friendship> save(Friendship entity){
        if(entity.getId()==null){
            Long nextId= StreamSupport.stream(super.findAll().spliterator(),false)
                    .map(Friendship::getId)
                    .max(Long::compareTo)
                    .orElse(0L)+1;
            entity.setID(nextId);
        }
        return super.save(entity);
    }
}