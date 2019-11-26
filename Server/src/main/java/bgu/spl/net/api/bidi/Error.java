package bgu.spl.net.api.bidi;

/**
 * Error message extending Msg.
 */
public class Error extends Msg {
    private final short ErrorOpCode=11;
    private final short MessageOpCode;

    /**
     * Constructor.
     * @param messageOpcode to separate types of Msg that resulted in error.
     */
    public Error(short messageOpcode){
        this.MessageOpCode=messageOpcode;
    }


    //------------------getters and setters--------------//

    @Override
    public short getOpCode() {
        return ErrorOpCode;
    }


    /**
     * Returns the opCode of the succedded message.
     * @return message Opcode.
     */
    public short getMessageOpCode(){
        return MessageOpCode;
    }



}
