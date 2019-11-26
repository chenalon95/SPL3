package bgu.spl.net.api.bidi;


/**
 * Sts message extending Msg.
 */
public class Stat extends Msg {
    private short opCode=8;
    private String userName;

    /**
     * Constructor.
     * @param userName of a user.
     */
    public Stat(String userName){
        this.userName=userName;
    }



    //------------------getters and setters--------------//

    @Override
    public short getOpCode(){
        return opCode;
    }

    /**
     * Returns userName of the user.
     * @return userName.
     */
    public String getUserName(){
        return userName;
    }

}

