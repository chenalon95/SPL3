package bgu.spl.net.api.bidi;



/**
 * StatAck message extending Ack.
 */
public class StatAck extends Ack {

    private int numberOfPosts;
    private int numberOfFollowers;
    private int numberOfFollowing;

    /**
     * Constructor.
     * @param opCode of the Ack-type Msg.
     * @param numberOfPosts published by the user.
     * @param numberOfFollowers of user.
     * @param numberOfFollowing representing number of people user is following.
     */
    public StatAck(short opCode,int numberOfPosts,int numberOfFollowers,
                   int numberOfFollowing){
        super(opCode);
        this.numberOfFollowers=numberOfFollowers;
        this.numberOfFollowing=numberOfFollowing;
        this.numberOfPosts=numberOfPosts;
    }


    //------------------getters and setters--------------//
    public int getNumberOfPosts() {
        return numberOfPosts;
    }

    public int getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public int getNumberOfFollowing() {
        return numberOfFollowing;
    }
}
