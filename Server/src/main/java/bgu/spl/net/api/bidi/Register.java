package bgu.spl.net.api.bidi;



/**
 * Register message extending Msg.
 */
public class Register extends Msg {
    private final int opCode=1;
    private String userName;
    private String password;


    /**
     * Constructor.
     * @param userName chosen by the user registering..
     * @param password chosen by the user registering.
     */
    public Register(String userName,String password){
        this.userName=userName;
        this.password=password;
    }



    //------------------getters and setters--------------//


    /**
     * Returns username.
     * @return userName.
     */
    public String getUserName(){
        return userName;
    }

    @Override
    public short getOpCode(){
        return opCode;
    }


    /**
     * Returns password.
     * @return password.
     */
    public String getPassword(){
        return password;
    }

}
