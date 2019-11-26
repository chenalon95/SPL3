package bgu.spl.net.api.bidi;

import bgu.spl.net.impl.BGSServer.DataBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BidiProtocolImpl<T> implements BidiMessagingProtocol<Msg> {

    private int connectionID;
    private String userName;
    private Connections<Msg> connections;
    private AtomicBoolean shouldTerminate;
    private DataBase data;


    /**
     * Constructor.
     *
     * @param data containing information about all social network's users.
     */
    public BidiProtocolImpl(DataBase data) {
        this.data = data;
        this.shouldTerminate = new AtomicBoolean();
    }

    @Override
    public void process(Msg message) {
        short opCode = message.getOpCode();

        if (opCode == 1) {
            processRegister((Register) message);
        } else if (opCode == 2) {
            processLogin((Login) message);
        } else if (opCode == 3) {
            processLogout((Logout) message);
        } else if (opCode == 4) {
            processFollow((Follow) message);
        } else if (opCode == 5) {
            processPost((Post) message);
        } else if (opCode == 6) {
            processPM((PM) message);
        } else if (opCode == 7) {
            processUserList((UserList) message);
        } else {
            processStat((Stat) message);
        }

    }


    @Override
    public void start(int connectionId, Connections<Msg> connections) {
        this.connectionID = connectionId;
        this.connections = connections;
        this.shouldTerminate.set(false);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate.get();
    }


    /**
     * process for Register messages.
     *
     * @param msg of type Register.
     */
    private void processRegister(Register msg) {
        Msg message;
        String value = data.setRegisteredUser(msg.getUserName(), msg.getPassword(), connectionID);
        if (value == null) { //if user successfully registered
            message = new RegularAck(msg.getOpCode());
            // userName = msg.getUserName();
        } else {
            message = new Error(msg.getOpCode());

        }
        connections.send(connectionID, message);
    }


    /**
     * process for follow messages.
     *
     * @param msg of type Follow.
     */

    private void processFollow(Follow msg) {
        Msg message;
        int successfulCounter = 0;
        ConcurrentLinkedQueue<String> successUsers = new ConcurrentLinkedQueue<>();

        if (data.getWhoIsLoggedIn().contains(userName)) {
            if (msg.getToFollow()) { //follow all users
                successfulCounter = processFollowAll(msg, successfulCounter, successUsers);
            } else { //un-follow all users
                successfulCounter = processUnFollowAll(msg, successfulCounter, successUsers);
            }
            if (successfulCounter > 0) {
                // String userNameList = String.join(""+'\0',successUsers);
                message = new FollowAck(msg.getOpCode(), successfulCounter, successUsers);
                connections.send(connectionID, message);
                return;
            }
        }
        message = new Error(msg.getOpCode());
        connections.send(connectionID, message);

    }

    /**
     * handle follow messages.
     *
     * @param msg               containing info about who to follow.
     * @param successfulCounter counting how many people were successfully followed.
     * @param successUsers      queue of users who were successfully followed.
     */
    private int processFollowAll(Follow msg, int successfulCounter, ConcurrentLinkedQueue<String> successUsers) {

        Queue<String> toFollow = msg.getUserNameList();
        while (!toFollow.isEmpty()) {
            String userToFollow = toFollow.poll();
            if (data.getRegisteredUsers().containsKey(userToFollow))
                synchronized (data.getFollowersOfUser(userToFollow)) {
                    if (!data.getFollowersOfUser(userToFollow).contains(userName)) {
                        data.setFollowers(userName, userToFollow, true);
                        successUsers.add(userToFollow); //follow the user
                        successfulCounter++;
                    }
                }
        }
        return successfulCounter;

    }

    /**
     * handle unfollow messages.
     *
     * @param msg               containing info about who to unfollow.
     * @param successfulCounter counting how many people were successfully unfollowed.
     * @param successUsers      queue of users who were successfully unfollowed.
     **/
    private int processUnFollowAll(Follow msg, int successfulCounter, ConcurrentLinkedQueue<String> successUsers) {


        Queue<String> toUnFollow = msg.getUserNameList();
        while (!toUnFollow.isEmpty()) {
            String userToUnFollow = toUnFollow.poll();
            if (data.getRegisteredUsers().containsKey(userToUnFollow))
                synchronized (data.getFollowersOfUser(userToUnFollow)) {
                    if (data.getFollowersOfUser(userToUnFollow).contains(userName)) {

                        data.setFollowers(userName, userToUnFollow, false); //unfollow the user
                        successUsers.add(userToUnFollow);
                        successfulCounter++;
                    }
                }
        }
        return successfulCounter;

    }

    /**
     * process for PM messages.
     *
     * @param msg of type PM.
     */
    private void processPM(PM msg) {
        Msg message;
        if (userName != null &&
                data.getWhoIsLoggedIn().contains(userName) &&
                data.getRegisteredUsers().containsKey(msg.getUserName())) {
            connections.send(connectionID, new RegularAck(msg.getOpCode()));
            message = new Notification(false, userName, msg.getContent());
            synchronized (data.getWhoIsLoggedIn()) {
                if (data.getWhoIsLoggedIn().contains(msg.getUserName()))
                    connections.send(data.getUser(msg.getUserName()), message);
                else
                    data.setPostsAndPMs(msg.getUserName(), message);
            }
        }
        else{
                message = new Error(msg.getOpCode());
                connections.send(connectionID, message);

        }
    }

    /**
     * process for Stat messages.
     *
     * @param msg of type Stat.
     */
    private void processStat(Stat msg) {

        Msg message;
        if (data.getWhoIsLoggedIn().contains(userName) &&
                data.getRegisteredUsers().containsKey(msg.getUserName())) {
            int numOfFollowers = data.getFollowersOfUser(msg.getUserName()).size();
            int numOfFollowing = data.getFollowedByUser(msg.getUserName()).size();
            message = new StatAck(msg.getOpCode(), data.getNumOfPosts(msg.getUserName()), numOfFollowers,
                    numOfFollowing);
        } else {
            message = new Error(msg.getOpCode());
        }
        connections.send(connectionID, message);
    }


    /**
     * process for Login messages.
     *
     * @param msg of type Login.
     */
    private void processLogin(Login msg) {

        Msg message = null;
        boolean logged = false;
        synchronized (data.getWhoIsLoggedIn()) {
            if (data.getRegisteredUsers().containsKey(msg.getUserName()) &&
                    data.getRegisteredUsers().get(msg.getUserName()).equals(msg.getPassword())
                    && !data.getWhoIsLoggedIn().contains(msg.getUserName()) &&
                    !data.getWhoIsLoggedIn().contains(userName)) {
                data.updateId(msg.getUserName(), connectionID);
                userName = msg.getUserName();
                message = new RegularAck(msg.getOpCode());
                data.setWhoIsLoggedIn(msg.getUserName());
                logged = true;
            }
        }
        if (logged) { //send all notifications sent to the user while he was logged out
            while (!data.getPostsAndPMs().get(msg.getUserName()).isEmpty()) {
                connections.send(connectionID, data.getPostsAndPMs().get(msg.getUserName()).poll());
            }
        } else {
            message = new Error(msg.getOpCode());
        }
        connections.send(connectionID, message);
    }


    /**
     * process for Logout messages.
     *
     * @param msg of type Logout.
     */
    private void processLogout(Logout msg) {

        Msg message;
        if (data.getWhoIsLoggedIn().contains(userName)) {
            message = new RegularAck(msg.getOpCode());
            data.logout(userName);
            userName = "";
            connections.send(connectionID, message);
            shouldTerminate.set(true);
        } else {
            message = new Error(msg.getOpCode());
            connections.send(connectionID, message);
        }

    }



    /**
     * process for Post messages.
     *
     * @param msg of type Post.
     */
    private void processPost(Post msg) {

        Msg message;
        if (data.getWhoIsLoggedIn().contains(userName)) {
            data.setNumOfPostsBySender(userName); //add one to the sender's number-of-posts-posted counter
            msg.setSenderID(connectionID);

            ConcurrentLinkedQueue<String> usersToSend = getTaggedUsers(msg.getContent());
            message = new Notification(true, userName, msg.getContent()); //notification to send to all logged in users


            //send post to users tagged in the message
            sendPostToUsers(usersToSend, (Notification) message);
            //send post to all users following user
            usersToSend = data.getFollowersOfUser(userName);
            //send post to users following in the message
            sendPostToUsers(usersToSend, (Notification) message);


            message = new RegularAck(msg.getOpCode());
            connections.send(connectionID, message);

        } else { //sending user is logged out
            message = new Error(msg.getOpCode());
            connections.send(connectionID, message);
        }
    }


    /**
     * Sub-function of processPost.
     * Sends a notification to a queue of users, if they are logged in.
     * If not,stores the notifications in data.
     * @param usersToSend the notification to.
     * @param message to be sent to all users.
     */
    private void sendPostToUsers(ConcurrentLinkedQueue<String> usersToSend,Notification message) {
        for (String userToSend : usersToSend) {
            synchronized (data.getWhoIsLoggedIn()) {
                if (data.getWhoIsLoggedIn().contains(userToSend)) {
                    connections.send(data.getUser(userToSend), message);
                } else {
                    data.setPostsAndPMs(userToSend, message); //add post to receiver's queue
                }
            }
        }
    }


    /**
     * process for UserList messages.
     * @param msg of type UserList.
     */
    private void processUserList(UserList msg) {

        Msg message;
        if (data.getWhoIsLoggedIn().contains(userName)) {

            //create user-name list by registration order, separated by zero bytes
            List<String> orderedUsers=new ArrayList<>();
            ConcurrentLinkedQueue<String> allUsersReversed = new ConcurrentLinkedQueue<String>(data.getAllUsersByRegistationOrder());
            while(!allUsersReversed.isEmpty())
                orderedUsers.add(allUsersReversed.poll());

            String userNameList=String.join(""+"\0",orderedUsers);

        //create Ack
            message=new UserListAck(msg.getOpCode(),data.getRegisteredUsers().size(),userNameList);
        } else {
            message = new Error(msg.getOpCode());
        }
        connections.send(connectionID, message);
    }


    /**
     * method finds all @username that appears in content.
     * @param content1 to find usernames in.
     * @return queue of usernames tagged in content.
     */
    private ConcurrentLinkedQueue<String> getTaggedUsers(String content1){
        String content=new String(content1);
        ConcurrentLinkedQueue<String> userNames= new ConcurrentLinkedQueue<>();
        content=content+' ';
        String sendTo;
        int index=0;
        do {
            index=content.indexOf('@');
            if(index>=0) {//if there is another tagged user.
                content = content.substring(index);
                sendTo = content.substring(1, content.indexOf(' '));
                if(!userNames.contains(sendTo) &&
                        !data.getFollowersOfUser(userName).contains(sendTo) &&
                        data.getRegisteredUsers().containsKey(sendTo) &&
                        !userNames.contains(sendTo))
                    userNames.add(sendTo);
                content = content.substring(content.indexOf(' ')+1);
            }
            else{
                break;
            }
        }while(content.length()>0);
        return userNames;
    }
}






