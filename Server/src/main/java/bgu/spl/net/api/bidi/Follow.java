package bgu.spl.net.api.bidi;

import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Follow message extending Msg.
 */
public class Follow extends Msg {
    private final int opCode=4;
    private int numOfUsers;
    private Queue<String> userNameList;
    private boolean toFollow;
    private Timestamp time;

    /**
     * Constructor.
     * @param numOfUsers to follow/unfollow.
     * @param userNameList of users to follow/unfollow.
     * @param toFollow or unfollow boolean.
     */
    public Follow(int numOfUsers,String userNameList,boolean toFollow){
        this.numOfUsers=numOfUsers;
        this.userNameList=createUserNameList(userNameList);
        this.toFollow=toFollow;
        time=new Timestamp(System.currentTimeMillis());

    }

    /**
     * Converts string of usernames to queue of usernames.
     * @param userNameList string of users to follow/unfollow.
     * @return queue of usernames to follow/unfollow.
     */
    private Queue<String> createUserNameList(String userNameList){
        Queue<String> users=new ArrayDeque<>();
        while (userNameList.length()>1){
            users.add(userNameList.substring(0,userNameList.indexOf(' ')));
            userNameList=userNameList.substring(userNameList.indexOf(' ')+1);
        }
        return users;
    }



    //------------------getters and setters--------------//


    @Override
    public short getOpCode(){
        return opCode;
    }



    /**
     * Returns a boolean that signifies if the user wants
     * to follow or unfollow the userNameList.
     * @return toFollow.
     */
    public boolean getToFollow() {
        return toFollow;
    }

    /**
     * Returns list of userNames to follow/unfollow.
     * @return userNameList.
     */
    public Queue<String> getUserNameList(){
        return userNameList;
    }




}
