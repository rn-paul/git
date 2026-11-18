package repository;

import domain.Friendship;
import domain.User;
import domain.validators.Validator;
import jdk.jfr.Frequency;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class RepositoryFactory {
    private Properties properties;
    public RepositoryFactory(){
        properties=new Properties();
        try(InputStream input=getClass().getClassLoader().getResourceAsStream("config.properties")){
            if(input==null){
                throw new RuntimeException("file config.properties not found");
            }properties.load(input);
        } catch (IOException e) {

            throw new RepositoryException("Error when loading settings",e);
        }

    }
    private String getProperty(String key){
        String value=properties.getProperty(key);
        if(value==null||value.trim().isEmpty()){
            throw new IllegalArgumentException("unknown value or value not found"+key);
        }
        return value;

    }
    public Repository<Long,User> createUserRepository(Validator<User> validator){
        RepositoryType type=RepositoryType.valueOf(getProperty("repository.type").toUpperCase());
        Map<RepositoryType,Function<Validator<User>,Repository<Long,User>>>userRepoCreators=new EnumMap<>(RepositoryType.class);
        userRepoCreators.put(RepositoryType.MEMORY,val->new InMemoryRepository<Long,User>(val){
            /**
             * Saves an entity in the memory repository
             * The id is automatically generated
             * @param entity User
             * @return
             */
            @Override
            public Optional<User> save(User entity){
                if(entity.getId()==null){
                    Long nextId=entities.keySet().stream().max(Long::compareTo).orElse(0L)+1;
                    entity.setID(nextId);
                }
                return super.save(entity);
            }

        });
        userRepoCreators.put(RepositoryType.DATABASE,val->new UserDbRepository(val,getProperty("db.url"),getProperty("db.user"),getProperty("db.password")));
        userRepoCreators.put(RepositoryType.FILE,val->new UserFileRepository(val,getProperty("users.file")));

        if(!userRepoCreators.containsKey(type)){
            throw new IllegalArgumentException("invalid repo type");
        }
        return userRepoCreators.get(type).apply(validator);
    }

    /**
     * Creates a new repository that matches the specified type
     * @param validator
     * @return repository of specified type
     * @throws IllegalArgumentException if type is invalid
     */
    public Repository<Long, Friendship> createFriendshipRepository(Validator<Friendship> validator){
        RepositoryType type=RepositoryType.valueOf(getProperty("repository.type").toUpperCase());
        Map<RepositoryType,Function<Validator<Friendship>,Repository<Long, Friendship>>>friendshipRepoCreators=new EnumMap<>(RepositoryType.class);
        friendshipRepoCreators.put(RepositoryType.MEMORY,InMemoryRepository::new);
        friendshipRepoCreators.put(RepositoryType.DATABASE,val->new FriendshipDbRepository(val,getProperty("db.url"),getProperty("db.user"),getProperty("db.password")));
        friendshipRepoCreators.put(RepositoryType.FILE,val->new FriendshipFileRepository(val,getProperty("friendships.file")));

        if(!friendshipRepoCreators.containsKey(type)){
            throw new IllegalArgumentException("invalid repo type");
        }
        return friendshipRepoCreators.get(type).apply(validator);
    }
}
