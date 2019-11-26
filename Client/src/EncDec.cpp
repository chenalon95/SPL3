
#include <EncDec.h>
#include <string>
#include <iostream>

EncDec::EncDec(ConnectionHandler &connectionHandler):currentMessage("NULL"),newMessage(true),numOfUsers(-1),opCode(-1),counter(0),userList(""),messageOpCode(-1),opCodeCountr(0)
,opCodeAck(0),numOfUsersTemp(-1),numOfUsersCounter(0),numPosts(-1),numPostsCountr(0),numPostsTemp(-1),numFollowers(-1),
numFollowersCountr(0),numFollowersTemp(-1), numFollowing(-1),numFollowingCountr(0), numFollowingTemp(-1),numOfUsersTemp2(0),postingUser(""),content(""),notification(""),
opCodeError(-1), connectionHandler(connectionHandler){}

EncDec::~EncDec() {
}


//----------------------encode--------------------------/

/**
 * Create new message from the server.
 * @param line
 * @param msg
 */
void EncDec::createMessage(std::string line) {

    std::string temp=line.substr(0,line.find(" "));
    line=line.substr(temp.length());
    if (temp=="REGISTER") {
        createMsg(line,1);
    }
    else if(temp=="LOGIN") {
        createMsg(line,2);
    }
    else if(temp=="LOGOUT") {
        char opcode_[2];
        shortToBytes(3,opcode_);
        connectionHandler.sendBytes(opcode_, 2);
    }
    else if(temp=="FOLLOW") {
        createFollow(line);
    }
    else if(temp=="POST") {
        createStatPost(line,5);
    }
    else if(temp=="PM") {
        createMsg(line,6);
    }
    else if(temp=="USERLIST") {
        char opcode_[2];
        shortToBytes(7,opcode_);
        connectionHandler.sendBytes(opcode_, 2);
    }
    else {
        createStatPost(line,8);
    }

}


/**
 * Create REGISTER, PM and LOGIN messages.
 * @param message string from the server.
 * @param opCode of the message.
 * @param bytesArr represents the message.
 */
void EncDec::createMsg(std::string message,short opCode) {

    char opCode_[2];
    shortToBytes(opCode, opCode_);

    connectionHandler.sendBytes(opCode_,2);



    message=message.substr(1); //handle first string.
    int nextSpace=message.find(" ");
    std::string temp= message.substr(0, nextSpace) ;

    connectionHandler.sendFrameAscii(temp, '\0');


    message = message.substr(message.find(" ") + 1); //handle second string.

    connectionHandler.sendFrameAscii(message, '\0');
}

/**
 * Create Follow bytes message.
 * @param message to create.
 */
void EncDec::createFollow(std::string message) {

    char opCode_[2];
    shortToBytes(4, opCode_);

    connectionHandler.sendBytes(opCode_,2); //sending the opCode.


    std::string::size_type sz;
    std::string follow;

    follow=message.at(1);
    short followOrUnfollow = std::stoi (follow,&sz);

    char followOrUnfollowByte [2];
    shortToBytes(followOrUnfollow, followOrUnfollowByte);
    char secondDigit[1];
    secondDigit[0] = followOrUnfollowByte[1];
    connectionHandler.sendBytes(secondDigit,1); //sending the follow/unfollow byte.

    message=message.substr(3);

    char numOfUsers[2];


    std::string::size_type sz1;
    std::string usersNumber;

    usersNumber=message.substr(0,message.find(" "));
    short users = std::stoi (usersNumber,&sz1);
    shortToBytes(users, numOfUsers);

    connectionHandler.sendBytes(numOfUsers,2); //sending number of users.

    //initialize values to convert
    message=message.substr(message.find(" ")+1);
    int index=(int)message.find(" ");
    std::string temp;

    while(index!=-1){ // while there is another user.

        temp=message.substr(0,index);
        //insert new user to follow/unfollow.
        connectionHandler.sendFrameAscii(temp, '\0'); //add zero byte between two user names.

        message=message.substr(index+1); //delete the recent user name.
        index=(int)message.find(" ");
    }


    //insert new user to follow/unfollow.
    connectionHandler.sendFrameAscii(message, '\0');

}

/**
 * Create Follow bytes message.
 * @param message to create.
 * @param opCode of the message.
 */
void EncDec::createStatPost(std::string message,short opCode) {

    char opCode_[2];
    shortToBytes(opCode, opCode_);
    connectionHandler.sendBytes(opCode_,2);
    message = message.substr(1);
    connectionHandler.sendFrameAscii(message, '\0');
}

/**
 * Converts short to bytes.
 * @param num to convert.
 * @param bytesArr to update.
 */
void EncDec::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}





//----------------------decode--------------------------/

/**
 * Decode message from the server.
 * @param msg - new char to read.
 * @return if the message is done.
 */
bool EncDec::decodeMessage(char * msg) {
    bool isDone;
    if(newMessage) { // in case of new message
        opCode = bytesToShort(msg); //update the opcode
        isDone = false;
        if (opCode>=9 && opCode<=11) { //in case the opcode is valid
            newMessage = false;
        }
    }
    else {
        if (opCode == 9) { //Notification msg
            isDone=createNotification(msg);
        } else if (opCode == 10) { // Ack msg
            isDone=createAckMessage(msg);
        } else if (opCode == 11) { //Error msg
            isDone=createErrorMessage(msg);
        }
    }
    return isDone;
}

/**
 * Decode notification message from the server.
 * @param msg to decode.
 * @return if the message is done.
 */
bool EncDec::createNotification(char * msg){
    bool isDone=false;
    if (notification=="") { //update the message type
        if (msg[0] == 1) {
            notification = "Public ";
        } else {
            notification = "Private ";
        }
    }
    else { //update the rest of the message


        if (msg[0] == 0 && counter != 1) {
            notification = notification+" ";
            counter++;
        } else if (msg[0] == 0) {
            counter++;
        } else {
            notification = notification + msg[0];
        }
    }
    if (counter == 2) { //in case the message is done
        isDone=true;
        currentMessage="NOTIFICATION "+notification;
        newMessage=true;
    }
    return isDone;

}


/**
 * Decode Ack message from the server.
 * @param msg to decode.
 * @return if the message is done.
 */
bool EncDec::createAckMessage(char * msg){

        bool isDone=false;
        if (messageOpCode==-1) {
            if(opCodeCountr==0) {
                //convert first byte
                opCodeAck = (short) ((msg[0] & 0xff) << 8);
                opCodeCountr++;
            }
            else {
                //convert second byte
                opCodeAck += (short) (msg[0] & 0xff);
               messageOpCode=opCodeAck;
            }
        }
        else {

            if (messageOpCode == 4){ //Follow ACK
                return  createUserAck(msg);
            }
            else if (messageOpCode == 7){ //User ACK

                return  createUserAck(msg);

            } else if (messageOpCode == 8) //Stat ACK
            {
                return  StatAck(msg);
            }
        }
        if (messageOpCode!=-1 &&  messageOpCode != 4 && messageOpCode != 7 && messageOpCode != 8) { //in case the message is done
            currentMessage = "ACK " + std::to_string(messageOpCode);
            newMessage = true;
            isDone = true;
        }

    return isDone;
}

/**
 *
 * Decode Stat Ack message from the server.
 * @param msg to decode.
 * @return if the message is done.
 */
bool EncDec::StatAck(char * msg){
    bool isDone=false;
    if (numPosts==-1) {
        if(numPostsCountr==0) {
            //convert first byte
            numPostsTemp = (short) ((msg[0] & 0xff) << 8);
            numPostsCountr++;
        }
        else {
            //convert second byte
            numPostsTemp += (short) (msg[0] & 0xff);
            numPosts=numPostsTemp;
        }
    }
    else if (numFollowers==-1) {
        if(numFollowersCountr==0) {
            //convert first byte
            numFollowersTemp = (short) ((msg[0] & 0xff) << 8);
            numFollowersCountr++;
        }
        else {
            //convert second byte
            numFollowersTemp += (short) (msg[0] & 0xff);
            numFollowers=numFollowersTemp;
        }
    }
    else if (numFollowing==-1) {
        if(numFollowingCountr==0) {
            //convert first byte
            numFollowingTemp = (short) ((msg[0] & 0xff) << 8);
            numFollowingCountr++;
        }
        else {
            //convert second byte
            numFollowingTemp += (short) (msg[0] & 0xff);
            numFollowing=numFollowingTemp;
            isDone=true;
        }
    }
    if(isDone){
        currentMessage = "ACK " + std::to_string(messageOpCode)+" "+std::to_string(numPosts)+" "+std::to_string(numFollowers)+" "+std::to_string(numFollowing);
        newMessage = true;
    }
    return isDone;
}


/**
 * Creates an encoded Ack message as a response to USERLIST request.
 * @param msg to encode.
 * @return
 */
bool EncDec::createUserAck(char *msg){



    bool ans=false;
    if(numOfUsers==-1) {
        if (numOfUsersCounter==0){
            //convert first byte
            numOfUsersTemp = (short) ((msg[0] & 0xff) << 8);
            numOfUsersCounter++;
        }
        else {
            //convert second byte
            numOfUsersTemp += (short) (msg[0] & 0xff);
            numOfUsers=numOfUsersTemp;
        }
    }

    else {
        if (msg[0] == 0 && counter != numOfUsers - 1) {
            userList = userList+" ";
            counter++;
        } else if (msg[0] == 0) {
            counter++;
        } else {
            userList = userList + msg[0];
        }
    }
    if (counter == numOfUsers) {
        ans = true;
        currentMessage = "ACK "+std::to_string(opCodeAck)+" " + std::to_string(numOfUsers) + " " + userList;
        newMessage = true;
    }
    return ans;
}



/**
 * This method converts error message to the client.
 * @param msg to convert.
 * @return
 */
bool EncDec::createErrorMessage(char * msg){

    bool isDone=false;
    if (messageOpCode==-1) {
        if(opCodeCountr==0) {
            //convert first byte
            opCodeError = (short) ((msg[0] & 0xff) << 8);
            opCodeCountr++;
        }
        else {
            //convert second byte
            opCodeError += (short) (msg[0] & 0xff);
            messageOpCode=opCodeError;
        }
    }
    if (messageOpCode!=-1){
        currentMessage="ERROR "+std::to_string(messageOpCode);
        newMessage=true;
        isDone=true;
    }
    return isDone;
    //short messageOpCode = bytesToShort(msg);


}


/**
 * Converts bytes array to short.
 * @param bytesArr
 * @return
 */
short EncDec::bytesToShort(char* bytesArr)
   {
       short result = (short)((bytesArr[0] & 0xff) << 8);
       result += (short)(bytesArr[1] & 0xff);
       return result;
   }

   bool EncDec::isNewMessage(){
    return newMessage;
}

std::string EncDec:: getMessage(){
    return currentMessage;
}

   void EncDec::resetValues(){
    numOfUsers=-1;
    counter=0;
    userList="";
    messageOpCode=-1;
    opCodeCountr=0;
    opCodeAck=0;
       numOfUsersTemp2=0;
       numOfUsersTemp=-1;
    numOfUsersCounter=0;
    numPosts=-1;
    numPostsCountr=0;
    numPostsTemp=-1;
    numFollowers=-1;
    numFollowersCountr=0;
    numFollowersTemp=-1;
    numFollowing=-1;
    numFollowingCountr=0;
    numFollowingTemp=-1;
    postingUser="";
    content="";
    notification="";
    opCodeError=-1;
    opCode=-1;
    currentMessage="NULL";

}


