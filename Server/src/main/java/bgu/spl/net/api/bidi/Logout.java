package bgu.spl.net.api.bidi;


/**
 * Logout message extending Msg.
 */
public class Logout extends Msg {

    private final int opCode=3;

    /**
     * Constructor.
     */
    public Logout(){

    }



    //------------------getters and setters--------------//

    @Override
    public short getOpCode(){
        return opCode;
    }


}
