package bgu.spl.net.api.bidi;



/**
 * RegularAck message extending Ack.
 * Represents an Ack Map without any optional information.
 */
public class RegularAck extends Ack {
    /**
     * Constructor.
     *
     * @param MessageOpcode of Ack-type Msg.
     */
    public RegularAck(short MessageOpcode) {
        super(MessageOpcode);
    }
}
