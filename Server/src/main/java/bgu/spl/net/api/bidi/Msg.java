package bgu.spl.net.api.bidi;

/**
 * Abstract class representing a certain request from a client,
 * or a reply from the server to the client.
 * Is extended to all possible types of requests.
 */
public abstract class Msg {



    //------------------getters and setters--------------//

    public abstract short getOpCode();

}
