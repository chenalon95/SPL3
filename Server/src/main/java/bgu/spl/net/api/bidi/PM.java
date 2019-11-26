package bgu.spl.net.api.bidi;



/**
 * PM message extending Msg.
 */
public class PM extends Msg {
    private short opCode=6;
    private String userName;
    private String content;

    /**
     * Constructor.
     * @param userName of the PM receiver.
     * @param content of the PM.
     */
    public PM(String userName,String content){
        this.userName=userName;
        this.content=content;
    }



    //------------------getters and setters--------------//

    @Override
    public short getOpCode(){
        return opCode;
    }


    /**
     * Returns user name of the PM receiver.
     * @return userName.
     */
    public String getUserName(){
        return userName;
    }

    /**
     * Returns content of the PM.
     * @return content.
     */
    public String getContent(){
        return content;
    }
}
