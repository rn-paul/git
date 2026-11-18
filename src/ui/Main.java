package ui;

import domain.Friendship;
import domain.User;
import domain.validators.UserValidator;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryFactory;
import service.SocialNetworkService;

public class Main {
    public static void main(String[] args) {
        Validator<User> userValidator = new UserValidator();
        Validator<User> extraValidator=new UserValidator();
        Validator<User> userValidator1=extraValidator.andThen(u->{
            if(u.getFirstName().length()<3){
                throw new domain.validators.ValidationException("numele trebuie sa aiba cel putin 3 litere");
            }
        });
        Validator<Friendship> friendshipValidator = entity -> {};


        RepositoryFactory factory = new RepositoryFactory();

        Repository<Long, User> userRepo = factory.createUserRepository(userValidator);
        Repository<Long, Friendship> friendRepo = factory.createFriendshipRepository(friendshipValidator);

        SocialNetworkService service = new SocialNetworkService(userRepo, friendRepo);
        System.out.println("users that start with a");
        userRepo.findAll(u->u.getFirstName().startsWith("A"))
                .forEach(System.out::println);

        Console console = new Console(service);
        console.run();
    }
}