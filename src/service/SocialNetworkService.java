package service;

import domain.Friendship;
import domain.User;
import jdk.jfr.Frequency;
import repository.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;

/**
 * Class responsible for managing the business logic
 */
public class SocialNetworkService {
    private final Repository<Long, User> userRepo;
    private final Repository<Long, Friendship> friendshipRepo;

    /**
     * Constructs a new service with the specified repositories
     * @param userRepo repository for managing user data
     * @param friendshipRepo repository for managing friendship data
     */

    public SocialNetworkService(Repository<Long, User> userRepo,Repository<Long,Friendship>friendshipRepo){
        this.userRepo=userRepo;
        this.friendshipRepo=friendshipRepo;

    }
    private <T> Stream<T> getStream(Iterable<T> iterable){
        return StreamSupport.stream(iterable.spliterator(),false);
    }


    /**
     * Adds a new user in the network
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @return the saved User entity containing the automatically generated id
     * @throws domain.validators.ValidationException if the provided names are invalid
     * @throws repository.RepositoryException if an error occurs during saving
     */
    public User addUser(String firstName,String lastName){
        User u=new User(firstName,lastName);

        userRepo.save(u);
        return u;
    }

    /**
     * Deletes user based on id
     * The function deletes the user's firendships first and then the user itself
     * @param Id
     * @throws IllegalArgumentException if the user is not in the network
     * @throws repository.RepositoryException if an error occurs during saving
     */
    public void deleteUser(Long Id){
       userRepo.findOne(Id).orElseThrow(()->new IllegalArgumentException("user not found"));

       getStream(friendshipRepo.findAll())
               .filter(f->f.getIdUser1().equals(Id)||f.getIdUser2().equals(Id))
               .map(Friendship::getId)
               .collect(Collectors.toList())
               .forEach(friendshipRepo::delete);
       userRepo.delete(Id);
    }

    /**
     * Creates a friendship between two users
     * @param id1 - id of the first user
     * @param id2 -id of the second user
     * @throws IllegalArgumentException if one of the users is not found or if the friendship already exists
     * @throws repository.RepositoryException if an error occurs during saving
     */
    public void addFriendship(Long id1,Long id2){
        if(userRepo.findOne(id1).isEmpty()||userRepo.findOne(id2).isEmpty()){
            throw new IllegalArgumentException("one of the users does not exist");
        }
        boolean exists=getStream(friendshipRepo.findAll())
                .anyMatch(f->(f.getIdUser1().equals(id1)&&f.getIdUser2().equals(id2))||
                        f.getIdUser1().equals(id2)&&f.getIdUser2().equals(id1));
        if(exists){
            throw new IllegalArgumentException("Friendship already exists");
        }
        Friendship f=new Friendship(id1,id2,LocalDateTime.now());

        friendshipRepo.save(f);
    }

    /**
     * Retrieves all users in the network
     * @return an iterable object containing all users
     */

    public Iterable<User> getAllUsers(){
        return userRepo.findAll();
    }

    /**
     * Retrieves all friendships in the network
     * @return an iterable object containing all friendships
     */
    public Iterable<Friendship> getAllFriendships(){
        return friendshipRepo.findAll();
    }


    private Map<Long,List<Long>> buildGraph(){
        Map<Long,List<Long>> adjList=getStream(userRepo.findAll())
                .collect(Collectors.toMap(User::getId,u->new ArrayList<>()));
        getStream(friendshipRepo.findAll()).forEach(f->{
            adjList.get(f.getIdUser1()).add(f.getIdUser2());
            adjList.get(f.getIdUser2()).add(f.getIdUser1());
        });
        return adjList;
    }

    /**
     * Determines the numbers of communities in the network
     * A community is defined as a related component in the friendship graph
     * @return the number of distinc communities
     */
    public int getNumberOfCommunities(){
        Map<Long,List<Long>> graph=buildGraph();
        Set<Long> visited=new HashSet<>();
        return (int) graph.keySet().stream()
                .filter(userId->!visited.contains(userId))
                .peek(userId->dfs(userId,graph,visited,new ArrayList<>()))
                .count();
    }
    private void dfs(Long node,Map<Long,List<Long>> graph,Set<Long> visited,List<Long> currentComponent){
        visited.add(node);
        currentComponent.add(node);
        graph.get(node).stream()
                .filter(neighbor->!visited.contains(neighbor))
                .forEach(neighbor->dfs(neighbor,graph,visited,currentComponent));
    }

    /**
     * Determines the most sociable community
     * The related component which contains the longest path
     * @return list of the users' ids in the community
     */
    public List<Long> getMostSociableCommunity(){
        Map<Long,List<Long>> graph=buildGraph();
        Set<Long> visited=new HashSet<>();
        return graph.keySet().stream()
                .filter(userId->!visited.contains(userId))
                .map(userId->{
                    List<Long> component=new ArrayList<>();
                    dfs(userId,graph,visited,component);
                    return component;
                })
                .max(Comparator.comparingInt(comp->findLongestPathInComponent(comp,graph)))
                .orElse(new ArrayList<>());
    }
    private int findLongestPathInComponent(List<Long> component,Map<Long,List<Long>> graph){
        return component.stream()
                .mapToInt(startNode->dfsLongestPath(startNode,graph,new HashSet<>()))
                .max()
                .orElse(0);
    }
    private int dfsLongestPath(Long node,Map<Long,List<Long>> graph,Set<Long> visitedPath ){
        visitedPath.add(node);
        int maxDepth=graph.get(node).stream()
                .filter(neighbor->!visitedPath.contains(neighbor))
                .mapToInt(neighbor->1+dfsLongestPath(neighbor,graph,visitedPath))
                .max()
                .orElse(0);
        visitedPath.remove(node);
        return maxDepth;
    }
}
