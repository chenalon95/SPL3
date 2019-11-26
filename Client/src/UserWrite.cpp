
#include <UserWrite.h>
#include <EncDec.h>


/**
 * Decodes Msg sent from the server to the client.
 * @param handler_ connection handler.
 */
UserWrite::UserWrite(ConnectionHandler &handler_):handler(handler_) {}


/**
 *
 * @param terminate
 * @param isError
 * @param lock
 */
void UserWrite::SendMessage(bool &terminate,bool &isError,bool &lock) {
    std::string answer;
    EncDec encDec(handler);


    try {
        do{

            bool flag=false;
            if(encDec.isNewMessage()) { // get the opCode of the message.
                char bytes[2];
                handler.getBytes(bytes, 2);
                encDec.decodeMessage(bytes);
                answer = "NULL";
            }
            else{ // get the rest of the message
                char nextByte[1];
                handler.getBytes(nextByte,1);
                flag = encDec.decodeMessage(nextByte);
            }
            if (!flag)//the message is not done
                answer= "NULL";
            else {
                answer= encDec.getMessage();
            }

            if(answer != "NULL"){//in case we received a message.
                std::cout<<answer<<std::endl;
                lock = false;
                if(answer.compare("ACK 3")==0) {
                    terminate = true;
                    handler.close();
                }
                else if(answer.compare("ERROR 3") == 0){
                    isError = true;
                }
                encDec.resetValues();
            }
        }while (!terminate);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        terminate = true;
    }
}
