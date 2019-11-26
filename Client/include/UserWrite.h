
#ifndef BOOST_ECHO_CLIENT_USERWRITE_H
#define BOOST_ECHO_CLIENT_USERWRITE_H


#include <ConnectionHandler.h>



class UserWrite {
public:
    ConnectionHandler &handler;

    UserWrite(ConnectionHandler &handler_);

    void SendMessage(bool &terminate,bool &isError,bool &lock);
};


#endif //BOOST_ECHO_CLIENT_USERWRITE_H
