package repository;
import domain.User;
import domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.SortedMap;

/**
 * Database repository for users
 */
public class UserDbRepository implements Repository<Long,User> {
    private Validator<User> validator;
    private String url;
    private String username;
    private String password;

    public UserDbRepository(Validator<User> validator, String url, String username, String password){
        this.validator=validator;
        this.url=url;
        this.username=username;
        this.password=password;
    }
    @Override
    public Optional<User> findOne(Long id){
        if(id==null){
            throw new IllegalArgumentException("id cannot be null");
        }
        String sql="SELECT * FROM users WHERE id=?";
        try(Connection connection= DriverManager.getConnection(url,username,password);
            PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id);
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    String firstName=resultSet.getString("first_name");
                    String lastName=resultSet.getString("last_name");
                    User user=new User(firstName,lastName);
                    user.setID(id);
                    return Optional.of(user);
                }
            }

        }catch(SQLException e){
            throw new RepositoryException("Database error finding user",e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all the entities from the database
     * @return an Iterable containing all the entities found in the database
     * @throws repository.RepositoryException if a database access error occurs
     *
     */
    @Override
    public Iterable<User> findAll(){
        List<User> users=new ArrayList<>();
        String sql="SELECT * FROM users";
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement=connection.prepareStatement(sql);
            ResultSet resultSet= statement.executeQuery()){
            while(resultSet.next()){
                Long id=resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                User user=new User(firstName,lastName);
                user.setID(id);
                users.add(user);
            }
        }catch(SQLException e){
            throw new RepositoryException("Database error finding users",e);
        }
        return users;
    }

    /**
     * Saves a given entity to the database
     * The id is automatically generated upon insertion
     * @param entity User
     * @return an Optional containing the entity if an error occurs , or an empty Optional if it has been saved successfully
     * @throws IllegalArgumentException if the provided entity is null
     * @throws domain.validators.ValidationException if the entity is not valid
     * @throws repository.RepositoryException if a database access error occurs
     */
    @Override
    public Optional<User> save(User entity){
        if(entity==null){
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);
        String sql="INSERT INTO users (first_name,last_name) VALUES (?,?) RETURNING id";
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement= connection.prepareStatement(sql)){
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            try(ResultSet resultSet= statement.executeQuery()){
                if(resultSet.next()){
                    entity.setID(resultSet.getLong("id"));
                    return Optional.empty();
                }
            }
        }catch (SQLException e){
            throw new RepositoryException("Database error saving user",e);
        }
        return Optional.of(entity);
    }

    /**
     * Deletes an entity with the specified id from the database
     *
     * @param id The id of the entity to be deleted , not null
     * @return an Optional containing the entity if it was found, an empty Optional if the entity has not been found
     * @throws IllegalArgumentException if the provided entity is null
     * @throws repository.RepositoryException if a database access error occurs
     */
    @Override
    public Optional<User> delete(Long id){
        if(id==null){
            throw new IllegalArgumentException("id cannot be null");
        }
        Optional<User> userToDelete=findOne(id);
        if(userToDelete.isEmpty()){
            return Optional.empty();
        }
        String sql="DELETE FROM users WHERE id=?";
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement= connection.prepareStatement(sql)){
            statement.setLong(1,id);
            statement.executeUpdate();
            return userToDelete;
        } catch (SQLException e) {
            throw new RepositoryException("Database error deleting user",e);
        }
    }
}
