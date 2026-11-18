package domain.validators;
import domain.User;
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) {
        String errors="";
        if(entity.getFirstName()==null || entity.getFirstName().trim().isEmpty()){
            errors+="First name cannot be empty!\n";
        }
        if(entity.getLastName()==null || entity.getLastName().trim().isEmpty() ){
            errors+="Last name cannot be empty!\n";
        }
        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
