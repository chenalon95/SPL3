package bgu.spl.net.api.bidi;



/**
 * UserListAck message extending Ack.
 */
public class UserListAck extends Ack {

    private int numberOfUsers;
    private String userNameList;

    /**
     * Constructor.
     * @param opCode of the Ack-type Msg.
     * @param numberOfUsers registered to the social network.
     * @param userNameList of all registered users.
     */
    public UserListAck(short opCode, int numberOfUsers, String userNameList) {
        super(opCode);
        this.numberOfUsers=numberOfUsers;
        this.userNameList=userNameList;
    }



    //------------------getters and setters--------------//

    public String getUserNameList() {
        return userNameList;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }
}
