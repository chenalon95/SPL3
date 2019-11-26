package bgu.spl.net.api.bidi;


/**
 * Post message extending Msg.
 */
public class Post extends Msg {
    private final int opCode=5;
    private String content;
    private int senderID;

    /**
     * Constructor.
     * @param content of the post.
     */
    public Post(String content){
        this.content=content;
    }





    //------------------getters and setters--------------//

    @Override
    public short getOpCode(){
        return opCode;
    }

    /**
     * Retuns content of the post.
     * @return content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the field representing the id of the sender.
     * @param senderID number of the post-sender's id.
     */
    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

}
