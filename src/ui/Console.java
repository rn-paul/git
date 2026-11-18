package ui;

import domain.User;
import service.SocialNetworkService;
import java.util.Scanner;


public class Console {
    private final SocialNetworkService service;
    private final Scanner scanner;

    public Console(SocialNetworkService service){
        this.service=service;
        this.scanner=new Scanner(System.in);
    }
    public void run(){
        while(true){
            printMenu();
            System.out.print("Choose option");
            String option=scanner.nextLine();

            try{
                switch (option){
                    case "1":
                        addUser();
                        break;
                    case "2":
                        deleteUser();
                        break;
                    case "3":
                        addFriendship();
                        break;
                    case "4":
                        printUsers();
                        break;
                    case "5":
                        printFriendships();
                        break;
                    case "6":
                        System.out.println("Number of communities "+service.getNumberOfCommunities());
                        break;
                    case "7":
                        System.out.println("The most sociable community "+service.getMostSociableCommunity());
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("invalid option");


                }
            }catch (NumberFormatException e){
                System.out.println("error:id has to be a valid number");

            }catch(IllegalArgumentException | domain.validators.ValidationException |repository.RepositoryException e) {
                System.out.println("error" + e.getMessage());
            }
        }
    }
    private void printMenu(){
        System.out.println("Menu");
        System.out.println("1.Add user");
        System.out.println("2.Delete user");
        System.out.println("3.Add friendship");
        System.out.println("4.Show all users");
        System.out.println("5.Show all frienships");
        System.out.println("6.Show number of communities");
        System.out.println("7.Show the most sociable community");
        System.out.println("0.Exit");

    }
    private void addUser(){
        System.out.print("Type in your first name");
        String firstName=scanner.nextLine();
        System.out.print("Type in your last name");
        String lastName=scanner.nextLine();
        User saveduser=service.addUser(firstName,lastName);
        System.out.println("success, the user's id is:"+saveduser.getId());

    }
    private void deleteUser(){
        System.out.println("Enter the id of the user you want to delete");
        Long id=Long.parseLong(scanner.nextLine());
        service.deleteUser(id);
        System.out.println("success");

    }
    private void addFriendship(){
        System.out.print("Enter the first user's id");
        Long idUser1=Long.parseLong(scanner.nextLine());
        System.out.print("Enter the second user's id");
        Long idUser2=Long.parseLong(scanner.nextLine());
        service.addFriendship(idUser1,idUser2);
        System.out.println("success");

    }
    private void printUsers(){
        service.getAllUsers().forEach(System.out::println);
    }
    private void printFriendships(){
        service.getAllFriendships().forEach(System.out::println);

    }

}
