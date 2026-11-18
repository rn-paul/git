package repository;
import domain.Friendship;
import domain.User;
import domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.SortedMap;

/**
 * Database repository for friendships
 */
public class FriendshipDbRepository implements Repository<Long,Friendship> {
    private Validator<Friendship> validator;
    private String url;
    private String username;
    private String password;

    public FriendshipDbRepository(Validator<Friendship> validator, String url, String username, String password){
        this.validator=validator;
        this.url=url;
        this.username=username;
        this.password=password;
    }
    @Override
    public Optional<Friendship> findOne(Long id){throw new UnsupportedOperationException("Not needed");}

    /**
     * Retrieves all the entities in the database
     * @return an Iterable with all the entities contained in the database
     * @throws repository.RepositoryException if a database access error occurs
     */
    @Override
    public Iterable<Friendship> findAll(){
        List<Friendship> friendships=new ArrayList<>();
        String sql="SELECT * FROM friendships";
        try(Connection connection= DriverManager.getConnection(url,username,password);
            PreparedStatement statement=connection.prepareStatement(sql);
            ResultSet resultSet= statement.executeQuery()){
            long fakeid=1L;
            while(resultSet.next()){
                Long id1=resultSet.getLong("id1");
                Long id2=resultSet.getLong("id2");
                Timestamp date=resultSet.getTimestamp("friends_from");
                Friendship f=new Friendship(id1,id2,date.toLocalDateTime());
                f.setID(fakeid++);
                friendships.add(f);
            }
        }catch(SQLException e){
            throw new RepositoryException("Database error finding friendships",e);
        }
        return friendships;
    }

    /**
     *Saves a given entity to the database
     * The id is automatically generated upon insertion
     *
     * @param entity Friendship
     * @return an Optional containing the entity if an error occurs , or an empty entity if it has been saved successfully
     * @throws IllegalArgumentException if the provided entity is null
     * @throws domain.validators.ValidationException if the entity is not valid
     * @throws repository.RepositoryException if a database access error occurs
     */
    @Override
    public Optional<Friendship> save(Friendship entity){
        if(entity==null){
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);
        String sql="INSERT INTO friendships (id1,id2,friends_from) VALUES (?,?,?)";
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement= connection.prepareStatement(sql)){
            statement.setLong(1,entity.getIdUser1());
            statement.setLong(2,entity.getIdUser2());
            statement.setTimestamp(3,Timestamp.valueOf(entity.getData()));
            statement.executeUpdate();
            return Optional.empty();

        }catch (SQLException e){
            throw new RepositoryException("Database error saving friendship",e);
        }

    }
    @Override
    public Optional<Friendship> delete(Long id){
        return Optional.empty();
    }
}
