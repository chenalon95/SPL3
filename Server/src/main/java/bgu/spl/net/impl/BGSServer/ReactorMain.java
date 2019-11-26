package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.EncDecImpl;
import bgu.spl.net.api.bidi.BidiProtocolImpl;

import bgu.spl.net.srv.Server;


/**
 * Main class of reactor.
 */
public class ReactorMain {


    public static void main(String[] args) {
        DataBase data = new DataBase(); //one shared object



        Server.reactor(
                Integer.parseInt(args[1]), //number of threads
                Integer.parseInt(args[0]), //port
                () ->  new BidiProtocolImpl<>(data), //protocol factory
                ()-> new EncDecImpl() //message encoder decoder factory
        ).serve();
    }
}
