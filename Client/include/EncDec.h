

#ifndef BOOST_ECHO_CLIENT_ENCDEC_H
#define BOOST_ECHO_CLIENT_ENCDEC_H

#include <ConnectionHandler.h>
#include <string>

class EncDec {
private:
    std::string currentMessage;
    //---------------encode-------------------/
    void createMsg(std::string message,short opCode);
    void createFollow(std::string message);
    void createStatPost(std::string message,short opCode);
    void shortToBytes(short num, char* bytesArr);
    //--------------decode-------------------/
    bool createNotification(char * msg);
    bool createAckMessage(char * msg);
    bool StatAck(char * msg);
    bool createUserAck(char * msg);
    bool createErrorMessage(char * msg);
    short bytesToShort(char* bytesArr);
    bool newMessage;
    short numOfUsers;
    short opCode;
    short counter;
    std::string userList;
    short messageOpCode;
    int opCodeCountr;
    short opCodeAck;
    short numOfUsersTemp;
    int numOfUsersCounter;
    int numPosts;
    int numPostsCountr;
    int numPostsTemp;
    int numFollowers;
    int numFollowersCountr;
    int numFollowersTemp;
    int numFollowing;
    int numFollowingCountr;
    int numFollowingTemp;
    int numOfUsersTemp2;
    std::string postingUser;
    std::string content;
    std::string notification;
    short opCodeError;
    ConnectionHandler &connectionHandler;

public:
    EncDec(ConnectionHandler &connectionHandler);
    virtual ~EncDec();
    void createMessage(std::string line);
    bool decodeMessage(char * msg);
    void resetValues();
    bool isNewMessage();
    std::string getMessage();


};
#endif //BOOST_ECHO_CLIENT_ENCDEC_H
