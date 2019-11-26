package bgu.spl.net.api.bidi;



/**
 * Notification message extending Msg.
 */
public class Notification extends Msg {
    private short opCode=9;
    private boolean isPublic;
    private String PostingUser;
    private String Content;

    /**
     * Constructor.
     * @param isPublic or private message.
     * @param postingUser name of user sending message.
     * @param content of the post or PM.
     */
    public Notification(boolean isPublic,String postingUser, String content){
        this.isPublic=isPublic;
        this.PostingUser=postingUser;
        this.Content=content;
        //todo: cancel MsgopCode field.
    }




    //------------------getters and setters--------------//

    @Override
    public short getOpCode(){
        return opCode;
    }


    /**
     * Returns if the notification is a post or not(PM).
     * @return isPublic.
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Returns name of the user posting to send the notification to.
     * @return name of posting user.
     */
    public String getPostingUser() {
        return PostingUser;
    }

    /**
     * Returns content of the message.
     * @return content.
     */
    public String getContent() {
        return Content;
    }
}
