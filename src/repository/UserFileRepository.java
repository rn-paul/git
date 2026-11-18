package repository;

import domain.User;
import domain.validators.Validator;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


public class UserFileRepository extends AbstractFileRepository<Long, User>{
    public UserFileRepository(Validator<User> validator,String filename){
        super(validator, filename);
    }
    @Override
    protected User extractEntity(List<String> attributes){
        User user=new User(attributes.get(1),attributes.get(2));
        user.setID(Long.parseLong(attributes.get(0)));
        return user;
    }
    @Override
    protected String createEntityAsString(User entity){
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
    @Override
    public Optional<User> save(User entity){
        if(entity.getId()==null){
            Long nextID= StreamSupport.stream(super.findAll().spliterator(),false)
                    .map(User::getId)
                    .max(Long::compareTo)
                    .orElse(0L)+1;
            entity.setID(nextID);
        }
        return super.save(entity);
    }
}
