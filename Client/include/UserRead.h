
#ifndef BOOST_ECHO_CLIENT_USERREAD_H
#define BOOST_ECHO_CLIENT_USERREAD_H


#include <ConnectionHandler.h>


class UserRead {

public:
    ConnectionHandler &handler;

    UserRead(ConnectionHandler &handler_);

    void readMessage(bool &terminate,bool &isError,bool &lock);


    void lockInput(bool &lock);
};

#endif //BOOST_ECHO_CLIENT_USERREAD_H
