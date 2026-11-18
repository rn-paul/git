package domain;

import javax.swing.*;
import java.util.Objects;

/**
 * Representes a user in the network
 * Extends class Entity which has a type Long identifier
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;

    public User(String firstName,String lastName){
        this.firstName=firstName;
        this.lastName=lastName;
    }

    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public void setFirstName(String firstName){this.firstName=firstName;}
    public void setLastName(String lastName){this.lastName=lastName;}

    @Override
    public String toString(){
        return "User{" + "id=" + getId() + ", First Name='"+ firstName + '\'' + " , Last Name='"+ lastName+'\''+'}';

    }
    @Override
    public boolean equals(Object o){
        if(this==o)return true;
        if(!(o instanceof User)) return false;
        User that=(User) o;
        return getId().equals(that.getId());
    }
    @Override
    public int hashCode(){
        return Objects.hash(getId(),getFirstName(),getLastName());
    }

}
