package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Msg;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A class containing all information regarding registered users.
 */
public class DataBase {

    private ConcurrentHashMap<String,Integer> Users; //list of all registered users that matches user-names to id numbers
    private ConcurrentHashMap<String, String> RegisteredUsers; // list of user-names and corresponding passwords
    private ConcurrentLinkedQueue<String> WhoIsLoggedIn;//list of names of logged in users
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<String>> WhoIsFollowingUser; //hash map representing each user and all the users that follow him
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<String>> WhoIsFollowedByUser; //hash map representing each user and all the users he follows
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<Msg>> PostsAndPMs; // list of user names and the posts sent to them
    private ConcurrentHashMap<String,AtomicInteger> numOfPostsBySender; // list of user names and the number of posts they sent
    private ConcurrentLinkedQueue<String> AllUsersByReversedRegistationOrder; // list of users and a counter to keep them in registration order


    /**
     * Constructor.
     */
    public DataBase(){
        Users=new ConcurrentHashMap<>();
        WhoIsLoggedIn=new ConcurrentLinkedQueue<>();
        WhoIsFollowingUser=new ConcurrentHashMap<>();
        PostsAndPMs=new ConcurrentHashMap<>();
        RegisteredUsers=new ConcurrentHashMap<>();
        numOfPostsBySender=new ConcurrentHashMap<>();
        WhoIsFollowedByUser=new ConcurrentHashMap<>();
        AllUsersByReversedRegistationOrder = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentHashMap<String, String> getRegisteredUsers() {
        return RegisteredUsers;
    }

    /**
     * adds a new queue for user in each of the data structures we hold.
     * @param userName of the user.
     * @param password of the user.
     * @param userID of the user.
     */
    public String setRegisteredUser(String userName, String password, int userID) {

        String value =RegisteredUsers.putIfAbsent(userName,password);
        if(value==null){ //if user doesn't already exist
            WhoIsFollowedByUser.put(userName,new ConcurrentLinkedQueue<>());
            WhoIsFollowingUser.put(userName, new ConcurrentLinkedQueue<>());
            PostsAndPMs.put(userName,new ConcurrentLinkedQueue<>());
            numOfPostsBySender.put(userName,new AtomicInteger(0));
            Users.put(userName,userID);
            AllUsersByReversedRegistationOrder.add(userName);
        }

        return value;




    }



    /**
     * Remove user from logged-in-users list.
     * @param username to remove.
     */
    public void logout(String username){
        WhoIsLoggedIn.remove(username);
    }

    /**
     * Returns list of all logged in clients.
     * @return a list of all logged in clients.
     */
    public ConcurrentLinkedQueue<String> getWhoIsLoggedIn() {
        return WhoIsLoggedIn;
    }


    /**
     * Adds a user to the list of logged in users.
     * @param userName that has just logged in.
     */
    public void setWhoIsLoggedIn(String userName) {
        this.WhoIsLoggedIn.add(userName);
    }

    /**
     * returns username's followers.
     * @param userName who's being followed.
     * @return followers of username.
     */
    public ConcurrentLinkedQueue<String> getFollowedByUser(String userName) {
        return WhoIsFollowedByUser.get(userName);
    }

    /**
     * adds followingUser to queue of users following followedUser.
     * adds followedUser to queue of users followed by followingUser.
     * @param followingUser to be added as a follower.
     * @param followedUser to be followed by followingUser.
     */
    public void setFollowers(String followingUser,String followedUser,boolean follow) {
        if(follow) { //add a follower
            this.WhoIsFollowingUser.get(followedUser).add(followingUser);
            this.WhoIsFollowedByUser.get(followingUser).add(followedUser);
        }else{ //remove a follower
            this.WhoIsFollowingUser.get(followedUser).remove(followingUser);
            this.WhoIsFollowedByUser.get(followingUser).remove(followedUser);

        }
    }

    /**
     * returns all users that userName follows.
     * @param userName that follows other users.
     * @return queue of users followed by userName.
     */
    public ConcurrentLinkedQueue<String> getFollowersOfUser(String userName){
        return WhoIsFollowingUser.get(userName);
    }

    /**
     * adds 1 to the posts counter of username.
     * @param userName that has just published a new post.
     */
    public void setNumOfPostsBySender(String userName) {
        numOfPostsBySender.get(userName).set(numOfPostsBySender.get(userName).intValue()+1);
    }

    /**
     * returns hash map of posts and pms sent to logged out users.
     * @return postAndPms.
     */
    public ConcurrentHashMap<String,ConcurrentLinkedQueue<Msg>> getPostsAndPMs() {
        return PostsAndPMs;
    }

    /**
     * Adds new PM or post that was sent to a logged out user.
     * @param userName of logged out receiver of PM or post.
     * @param postOrPM that was sent to a logged out user.
     */
    public void setPostsAndPMs(String userName,Msg postOrPM) {
        PostsAndPMs.get(userName).add(postOrPM);
    }

    /**
     * Returns a user's id according to userName.
     * @param userName who is registered.
     * @return id of userName
     */
    public int getUser(String userName){
        return Users.get(userName);
    }

    /**
     * Returns the number of posts a user has published.
     * @param name of the publishing user.
     * @return number of posts published.
     */
    public int getNumOfPosts(String name){
        return numOfPostsBySender.get(name).get();
    }


    /**
     * Returns all users with a number representing their registeration order.
     * @return AllUsersByRegistrationOrder.
     */
    public ConcurrentLinkedQueue<String> getAllUsersByRegistationOrder(){
        return AllUsersByReversedRegistationOrder;
    }


    /**
     * Changes userName's id number to idNum in data's structures.
     * @param userName of user who's just logged in.
     * @param idNum to be updated.
     */
    public void updateId(String userName,int idNum){

        Users.remove(userName);
        Users.put(userName, idNum);

    }



}
