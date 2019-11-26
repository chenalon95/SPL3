package bgu.spl.net.impl.BGSServer;


import bgu.spl.net.api.EncDecImpl;
import bgu.spl.net.api.bidi.BidiProtocolImpl;

import bgu.spl.net.srv.Server;

/**
 * TPC Main.
 */
public class TPCMain {


    public static void main(String[] args) {
        DataBase data = new DataBase(); //one shared object



        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new BidiProtocolImpl(data), //protocol factory
                () -> new EncDecImpl() //message encoder decoder factory
        ).serve();



    }
}
