package bgu.spl.net.api.bidi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Connections implementation.
 * @param <T> type.
 */
public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, bgu.spl.net.srv.bidi.ConnectionHandler> userIDConnectionHandler;

    /**
     * Constructor.
     */
    public ConnectionsImpl(){
        userIDConnectionHandler=new ConcurrentHashMap<>();

    }

    @Override
    public boolean send(int connectionId, T msg) {
        userIDConnectionHandler.get(connectionId).send(msg);
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for(Map.Entry<Integer, bgu.spl.net.srv.bidi.ConnectionHandler> users:userIDConnectionHandler.entrySet()){
            send(users.getKey(),msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if(userIDConnectionHandler.get(connectionId)!=null)
            userIDConnectionHandler.remove(userIDConnectionHandler.get(connectionId));
    }

    /**
     * Add a new client's id and connection handler to connectionsImpl's hash map.
     * @param connectionId to be added to hash map.
     * @param handler of the client with id connectionId.
     */
    public void connect(Integer connectionId, bgu.spl.net.srv.bidi.ConnectionHandler handler){
        userIDConnectionHandler.put(connectionId,handler);
    }

}
