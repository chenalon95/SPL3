package bgu.spl.net.api;
import bgu.spl.net.api.bidi.*;
import bgu.spl.net.api.bidi.Error;

import java.util.Arrays;
import java.util.Queue;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 *A class designed to Encode Messages before sending to clients,
 * and decoding bytes received from clients to Strings.
 */
public class EncDecImpl implements MessageEncoderDecoder<Msg> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int zeroCounter = 0; //count \0 to end of line
    private short opCode = -1; //saves opCode to know how many \0 bytes till end of line
    private int numOfFollowZero = 0;

    @Override
    public Msg decodeNextByte(byte nextByte) {


        if (opCode == -1) { //haven't received the opCode yet
            pushByte(nextByte);
            if (len == 2) { //not first byte
                opCode = getOpCode(bytes);
                if (opCode == 3 || opCode == 7) //logout, stat
                    return popMsg();
            }
        }
        else if (opCode == 4 && numOfFollowZero==0) { //follow message
            pushByte(nextByte);
            if (len == 5) {
                numOfFollowZero = getOpCode(Arrays.copyOfRange(bytes, 3, 5));
            }
        }
        else { //we know the opCode
            if (nextByte == '\0') { //count zero bytes received
                pushByte((byte)' ');
                zeroCounter++;
                if (zeroCounter == 1 && (opCode == 5 || opCode == 7 || opCode == 8))
                    return popMsg();
                if (zeroCounter == 2 && (opCode == 1 || opCode == 2 || opCode == 6))
                    return popMsg();
                if (zeroCounter == numOfFollowZero && opCode == 4)
                    return popMsg();
            }else{
                pushByte(nextByte);
            }
        }
        return null; //not a complete line yet
    }

    @Override
    public byte[] encode(Msg message) {
        if(message.getOpCode()==9)
            return encodeNotification((Notification)message);
        else if(message.getOpCode()==10)
            return encodeAck((Ack)message);
        else // opCode=11
            return encodeError((Error)message);

    }


    /**
     * Copy two byte arrays into one
     * @param arr1 of bytes.
     * @param arr2 bytes.
     * @return array containing bytes from both arrays.
     */

   private byte[] copyValuesToArray(byte[] arr1, byte[] arr2){

       byte[] toReturn = new  byte[arr1.length+arr2.length];
       int i=0;
       for(byte byteToAdd: arr1){
           toReturn[i]=byteToAdd;
           i++;
       }
       for(byte byteToAdd: arr2){
           toReturn[i]=byteToAdd;
           i++;
       }
        return toReturn;

   }


    /**
     * Encoding Errors before sending to client.
     * @param message to encode.
     * @return encoded message.
     */
    private byte[] encodeError(Error message) {

        byte[] ErrorOpCode= encodeOpCode(message.getOpCode());

        byte[] messageOpCode = encodeOpCode(message.getMessageOpCode());

        byte[] toReturn = copyValuesToArray(ErrorOpCode,messageOpCode);

        return toReturn;

    }


    /**
     * Encoding Notifications before sending to client.
     * @param message to encode.
     * @return encoded message.
     */
    private byte[] encodeNotification(Notification message) {



        byte[] NotificationOpeCode= encodeOpCode(message.getOpCode());
        byte[] type= new byte[1];
        if(message.isPublic()) //post
            type[0]=(byte)(1); //public byte
        else
            type[0]=(byte)(0); //pm(private) byte
        byte[] OpeCodeAndType=copyValuesToArray(NotificationOpeCode,type);
        byte[] additionalInfo= (message.getPostingUser()+ '\0'+ message.getContent()+'\0').getBytes(UTF_8);
        byte[] toReturn= copyValuesToArray(OpeCodeAndType,additionalInfo);


        return toReturn;



    }



    /**
     * Encoding Acks before sending to client.
     * @param message to encode.
     * @return encoded message.
     */
    private byte[] encodeAck(Ack message) {

        byte[]toReturn;

        byte[] AckOpCode= encodeOpCode(message.getOpCode()); //Ack opcode

        byte[] messageOpCode = encodeOpCode(message.getMessageOpcode()); // message opcode

        byte[] OpcodeAndMessageOpCode = copyValuesToArray(AckOpCode,messageOpCode); //combine opcodes to one array

        if(message.getMessageOpcode()==8) {//statAck


            //add additional fields to a new array
            byte[] numOfPosts= encodeOpCode((short)((StatAck) message).getNumberOfPosts());
            byte[] numOfFollowers= encodeOpCode((short) ((StatAck) message).getNumberOfFollowers());
            byte[] numOfFollowing= encodeOpCode((short)((StatAck) message).getNumberOfFollowing());

            byte[] numInfo = copyValuesToArray(numOfPosts,numOfFollowers);
            byte[] additionalInfo = copyValuesToArray(numInfo, numOfFollowing);

            toReturn=copyValuesToArray(OpcodeAndMessageOpCode,additionalInfo);
        }

        else if(message.getMessageOpcode()==4) { //followAck

            //add additional fields to a new array
            byte[] additionalInfo= new byte[2+numOfUserListChars(((FollowAck) message).getUserNameList())];


           byte[] numFollowersEncoded=encodeOpCode((short)((FollowAck)message).getNumberOfFollows()); //number of people followed

            additionalInfo[0]=numFollowersEncoded[0];
            additionalInfo[1]=numFollowersEncoded[1];


            int index=2;// after adding opCode to additionalInfo


            //add encoding of followed users' usernames to additionalInfo bytes
            for(String user: ((FollowAck) message).getUserNameList()){
                byte[] encodedUser= user.getBytes(UTF_8);
                for(byte userByte: encodedUser){
                    additionalInfo[index]=userByte;
                    additionalInfo[index]=userByte;
                    index++;
                }

                    additionalInfo[index] = 0;
                    index++;


            }


            //combine all arrays to a single array
            toReturn=copyValuesToArray(OpcodeAndMessageOpCode,additionalInfo);

        }
        else if(message.getMessageOpcode()==7) {//userListAck
            //add additional fields to a new array
            byte[] numOfUsers= encodeOpCode((short) ((UserListAck) message).getNumberOfUsers());

            byte[] additionalInfo = (""  + (((UserListAck) message).getUserNameList())+'\0').getBytes(UTF_8);

            byte[] numAndIfo= copyValuesToArray(numOfUsers,additionalInfo);
            toReturn=copyValuesToArray(OpcodeAndMessageOpCode,numAndIfo);

        }
        else{//regularAck
            toReturn=OpcodeAndMessageOpCode;
        }




        return toReturn;
    }


        private int numOfUserListChars(Queue<String> userNameList){
            int counter=0;

            for(String name: userNameList){
                counter+=name.length();
                counter++; //zero byte between names
            }


            return counter;
        }





    /**
     * Add byte to the byte array.
     *
     * @param nextByte to be added to the byte array.
     */
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }


    /**
     * Add byte to the byte array.
     *
     * @param nextByte to be added to the byte array.
     */
    private void pushByteTo(byte[] nextByte, byte[] byteArr, int length) {


        for(byte byteToAdd: nextByte) {
            if (length >= byteArr.length) {
                byteArr = Arrays.copyOf(bytes, length * 2);
            }
            byteArr[length++] = byteToAdd;
        }

    }

    /**
     * @return message of type Msg to be processed
     */
    private Msg popMsg() {
        if (bytes.length > 1 && bytes[1] == 0) {
            return null;
        }
        short opCode = getOpCode(bytes);
        Msg message = createMessage(opCode);
        len = 0;
        return message;
    }

    /**
     * Turns first two bytes to opCode.
     *
     * @param byteArr to turn into opCode.
     * @return said opCode.
     */
    private short getOpCode(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }


    private byte[] encodeOpCode(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }


    /**
     * finds the right type of message received by opcode,
     * and initializes it using sub-methods.
     *
     * @param opCode to indicate message type.
     */
    private Msg createMessage(short opCode) {

        String message = new String(bytes, 2, len-2, UTF_8);
        Msg toReturn;
        if (opCode < 5)
            toReturn= msgCreator1(opCode, message);
        else
            toReturn= msgCreator2(opCode,message);
        clearBytesArray();
        return toReturn;
    }


    /**
     * Create Msg in case of opCode between 1 and 4.
     * @param opCode to determine type of Msg.
     * @param message to be turned into Msg.
     * @return type of Msg to be processed.
     */
    private Msg msgCreator1(short opCode, String message) {
        String[] initArray;
        if (opCode == 1) {
            initArray = getValuesToInitMsg(message);
            return new Register(initArray[0], initArray[1]);
        } else if (opCode == 2) {
            initArray = getValuesToInitMsg(message);
            return new Login(initArray[0], initArray[1]);
        } else if (opCode == 3) {
            return new Logout();
        } else { //follow Msg
            boolean toFollow = !(bytes[2] == 1);
            short numOfUsers = (short) ((bytes[3] & 0xff) << 8);
            numOfUsers += (short) (bytes[4] & 0xff);
            message = new String(bytes, 5, len-5, UTF_8);
            return new Follow(numOfUsers, message, toFollow);//message represents users to follow.
        }
    }

    /**
     * Create Msg in case of opCode between 5 and 7.
     * @param opCode to determine type of Msg.
     * @param message to be turned into Msg.
     * @return type of Msg to be processed.
     */
    private Msg msgCreator2(short opCode, String message) {
        String[] initArray;
        if (opCode == 5) {
            return new Post(message);
        } else if (opCode == 6) {
            initArray = getValuesToInitMsg(message);
            return new PM(initArray[0], initArray[1]);
        } else if (opCode == 7) {
            return new UserList();
        } else {//in case of Stat msg: opcode 8
            return new Stat(message.substring(0, message.length() - 1));
        }
    }



        /**
         * returns the two Strings separated by 0 byte in a message
         * @param message to take string from
         * @return a String array with two strings to initiate a Msg object
         * */
    private String[] getValuesToInitMsg(String message){
        String toReturn[]=new String [2];
        int index=message.indexOf(' ');
        toReturn[0]= message.substring(0, index);
        toReturn[1]= message.substring(index+1,message.length()-1);
        return toReturn;

    }
    private void clearBytesArray(){
        len=0;
        opCode=-1;
        zeroCounter=0;
        numOfFollowZero=0;
    }

}
