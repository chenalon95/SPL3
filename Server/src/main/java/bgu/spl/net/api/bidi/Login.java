package bgu.spl.net.api.bidi;



/**
 * Login message extending Msg.
 */
public class Login extends Msg {

    private final int opCode=2;
    private String userName;
    private String password;


    /**
     * Constructor.
     * @param userName of the user logging in.
     * @param password to login to username.
     */
    public Login(String userName,String password){
        this.userName=userName;
        this.password=password;
    }




    //------------------getters and setters--------------//

    @Override
    public short getOpCode(){
        return opCode;
    }



    /**
     * Returns userName.
     * @return userName.
     */
    public String getUserName(){
        return userName;
    }

    /**
     * Returns userName's password.
     * @return password.
     */
    public String getPassword(){
        return password;
    }
}
