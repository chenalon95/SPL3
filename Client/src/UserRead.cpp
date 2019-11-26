

#include <UserRead.h>
#include <EncDec.h>
#include <ConnectionHandler.h>


/**
 * Responsible of socket reading.
 * @param handler_
 */

UserRead::UserRead(ConnectionHandler &handler_): handler(handler_) {}


/**
 * Reads Strings sent from the clients and creates message.
 * @param terminate boolean.
 * @param isError boolean.
 * @param lock boolean.
 */
void UserRead::readMessage(bool &terminate, bool &isError, bool &lock) {
    EncDec encDec(handler);
    while (!terminate) {
        isError = false;
        std::string line;
        getline(std::cin, line);

        if (!line.compare("") == 0) {


            encDec.createMessage(line);

            //locking the input.
            lock = true;

            if(line.compare("LOGOUT")==0){
                while (!terminate && !isError){
                }
            }
            lockInput(lock);
        }
    }

}

void UserRead::lockInput(bool &lock) {
    while (lock){}
}