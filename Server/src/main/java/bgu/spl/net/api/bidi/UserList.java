package bgu.spl.net.api.bidi;



/**
 * UserList message extending Msg.
 */
public class UserList extends Msg {
    private short opCode=7;

    /**
     * Constructor.
     */
    public UserList(){}



    //------------------getters and setters--------------//

    @Override
    public short getOpCode(){
        return opCode;
    }


}


