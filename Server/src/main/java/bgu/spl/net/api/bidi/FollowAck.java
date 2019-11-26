package bgu.spl.net.api.bidi;

import java.util.Queue;

/**
 * FollowAck message extends Ack.
 */
public class FollowAck extends Ack {
    private int numberOfFollows;
    //private String userNameList;
    private Queue<String> userNameList;


    /**
     * Constructor.
     * @param opcode of Follow Msg.
     * @param numberOfFollows representing number of successful follow/unfollow actions.
     * @param userNameList of users successfully followed/unfollowed.
     */
    public FollowAck(short opcode,int numberOfFollows,Queue<String> userNameList){
        super(opcode);
        this.numberOfFollows=numberOfFollows;
        this.userNameList=userNameList;

    }



    //------------------getters and setters--------------//

    /**
     * Returns number of users followed or unfollowed.
     * @return number of folowed users.
     */
    public int getNumberOfFollows() {
        return numberOfFollows;
    }


    /**
     * Returns a list of usernames followed or unfollowed.
     * @return userNameList.
     */
    public Queue<String> getUserNameList() {
        return userNameList;
    }
}
