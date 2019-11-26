#include <stdlib.h>
#include <ConnectionHandler.h>
#include <echoClient.h>
#include <UserRead.h>
#include <UserWrite.h>
#include <thread>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }


    bool shouldTerminate = false,isError = false,lock = false;

    UserRead readFromUser(connectionHandler);
    UserWrite sendToUser(connectionHandler);

    std::thread th1(&UserWrite::SendMessage,&sendToUser,std::ref(shouldTerminate),std::ref(isError),std::ref(lock));
    std::thread th2(&UserRead::readMessage,&readFromUser,std::ref(shouldTerminate),std::ref(isError),std::ref(lock));

    th1.join();
    th2.join();


    return 0;
}





