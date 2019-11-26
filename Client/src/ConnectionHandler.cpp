#include <ConnectionHandler.h>
 
using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

/**
 * Connection Handler Class.
 * @param host of the server.
 * @param port of the server.
 */
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_),newMessage(true){
}

/**
 * Destructor of the connection handler.
 */
ConnectionHandler::~ConnectionHandler() {
    close();
}

/**
 * Connect to the server.
 * @return if succeeded.
 */
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

/**
 * Insert next bytes into the given array.
 * @param bytes array to update.
 * @param bytesToRead -count of bytes to read.
 * @return boolean value.
 */
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
    }
    return false;
}

/**
 * Send bytes to the server.
 * @param bytes to send.
 * @param bytesToWrite  -count of bytes to write.
 * @return boolean values.
 */
bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

/**
 * Get new line from the server.
 * @param line to update.
 * @return if succeeded.
 */
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\0');
}

/**
 * Send new line to the server.
 * @param line to send.
 * @return if succeeded.
 */
bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\0');
}

 /**
  * GetFrameAscii
  * @param frame
  * @param delimiter
  * @return
  */
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
		do{
			getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

/**
 * SendFrameAscii
 * @param frame
 * @param delimiter
 * @return if succeeded.
 */
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}

/**
 * close the Connection Handler.
 */
// Close down the connection properly.
void ConnectionHandler::close() {
    try {
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}
