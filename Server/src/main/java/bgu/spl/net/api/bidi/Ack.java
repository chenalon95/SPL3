package bgu.spl.net.api.bidi;

public abstract class Ack extends Msg {
    protected final short MessageOpcode;
    protected final int opCode=10;


    /**
     * Constructor.
     * @param MessageOpcode of Ack-type Msg.
     */
    public Ack(short MessageOpcode){
        this.MessageOpcode=MessageOpcode;
    }

    @Override
    public short getOpCode() {
        return opCode;
    }


    /**
     * Returns the opCode of the succedded message.
     * @return message Opcode.
     */
    public short getMessageOpcode() {
        return MessageOpcode;
    }

    @Override
    public  String toString() {
        return ""+opCode+MessageOpcode;
    }
}
