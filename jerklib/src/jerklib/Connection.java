package jerklib;

import jerklib.Session.State;
import jerklib.events.IRCEvent;
import jerklib.events.listeners.WriteRequestListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection {
    Logger log = Logger.getLogger(this.getClass().getName());

    /* ConnectionManager for this Connection */
    private final ConnectionManager manager;

    /* SocketChannel this connection will use for reading/writing */
    private final SocketChannel socChannel;

    /* A Buffer of write request */
    private final List<WriteRequest> writeRequests = Collections.synchronizedList(new ArrayList<WriteRequest>());

    /* a Map to index currently joined channels by name */
    private final Map<String, Channel> channelMap = new HashMap<String, Channel>();

    /* ByteBuffer for readinging into */
    private final ByteBuffer readBuffer = ByteBuffer.allocate(2048);

    /* indicates if an event fragment is waiting */
    private boolean gotFragment;

    /* buffer for event fragments */
    private final StringBuffer stringBuff = new StringBuffer();

    /* actual hostname connected to */
    private String actualHostName;

    /* this is used for auto choosing a nick - if we have logged in successfully once
      * a new nick will not be auto chosen in event of NickInUse */
    private boolean loggedInSuccessfully;

    private final Session session;

    Connection(ConnectionManager manager, SocketChannel socChannel, Session session) {
        this.manager = manager;
        this.socChannel = socChannel;
        this.session = session;
    }


    boolean loggedInSuccessfully() {
        return loggedInSuccessfully;
    }

    void loginSuccess() {
        loggedInSuccessfully = true;
    }

    Profile getProfile() {
        return session.getRequestedConnection().getProfile();
    }

    void setHostName(String name) {
        session.connected();
        actualHostName = name;
    }

    String getHostName() {
        return actualHostName;
    }

    List<Channel> removeNickFromAllChannels(String nick) {
        List<Channel> returnList = new ArrayList<Channel>();
        for (Channel chan : channelMap.values()) {
            if (chan.removeNick(nick)) {
                returnList.add(chan);
            }
        }
        return returnList;
    }

    void nickChanged(String oldNick, String newNick) {
        if (log.isLoggable(Level.INFO)) {
            log.info("Looking for " + oldNick);
        }
        synchronized (channelMap) {
            for (Channel chan : channelMap.values()) {
                if (chan.getNicks().contains(oldNick)) {
                    log.severe("Found nick in " + chan.getName());
                    chan.nickChanged(oldNick, newNick);
                }
            }
        }
    }

    void removeChannel(Channel channel) {
        channelMap.remove(channel.getName());
    }

    Channel getChannel(String name) {
        return channelMap.get(name);
    }

    Collection<Channel> getChannels() {
        return Collections.unmodifiableCollection(channelMap.values());
    }

    void addChannel(Channel channel) {
        channelMap.put(channel.getName(), channel);
    }


    void addWriteRequest(WriteRequest request) {
        writeRequests.add(request);
    }

    boolean finishConnect() throws IOException {
        return socChannel.finishConnect();
    }

    void login() {
        writeRequests.add(new WriteRequest("NICK " + getProfile().getActualNick() + "\r\n", this));
        writeRequests.add
                (
                        new WriteRequest
                                (
                                        "USER " + getProfile().getName() + " 0 0 :" + getProfile().getName() + "\r\n", this
                                )
                );
    }

    int read() {
        if (log.isLoggable(Level.INFO)) {
            log.info("READ CALLED");
        }

        if (!socChannel.isConnected()) {
            log.severe("Read call while sochan.isConnected() == false");
            return -1;
        }


        readBuffer.clear();

        int numRead = 0;

        try {
            numRead = socChannel.read(readBuffer);
        }
        catch (Exception e) {
            e.printStackTrace();
            session.disconnected();
        }

        if (numRead == -1) {
            session.disconnected();
        }

        if (session.getState() == State.DISCONNECTED || numRead <= 0) return 0;

        readBuffer.flip();

        String tmpStr = new String(readBuffer.array(), 0, numRead);

        // read did not contain a \r\n
        if (tmpStr.indexOf("\r\n") == -1) {
            // append whole thing to buffer and set fragment flag
            stringBuff.append(tmpStr);
            gotFragment = true;

            return numRead;
        }

        // this read had a \r\n in it

        if (gotFragment) {
            // prepend fragment to front of current message
            tmpStr = stringBuff.toString() + tmpStr;
            stringBuff.delete(0, stringBuff.length());
            gotFragment = false;
        }

        String[] strSplit = tmpStr.split("\r\n");

        for (int i = 0; i < (strSplit.length - 1); i++) {
            manager.addToEventQueue(createDefaultIRCEvent(strSplit[i]));
        }

        String last = strSplit[strSplit.length - 1];

        if (!tmpStr.endsWith("\r\n")) {
            // since string did not end with \r\n we need to
            // append the last element in strSplit to a stringbuffer
            // for next read and set flag to indicate we have a fragment waiting
            stringBuff.append(last);
            gotFragment = true;
        } else {
            manager.addToEventQueue(createDefaultIRCEvent(last));
        }

        return numRead;
    }

    int doWrites() {
        int amount = 0;

        List<WriteRequest> tmpReqs = new ArrayList<WriteRequest>();
        synchronized (writeRequests) {
            tmpReqs.addAll(writeRequests);
            writeRequests.clear();
        }

        for (WriteRequest request : tmpReqs) {
            String data;

            if (request.getType() == WriteRequest.Type.CHANNEL_MSG) {
                data = "PRIVMSG " + request.getChannelName() + " :" + request.getMessage() + "\r\n";
            } else if (request.getType() == WriteRequest.Type.PRIVATE_MSG) {
                data = "PRIVMSG " + request.getNick() + " :" + request.getMessage() + "\r\n";
            } else {
                data = request.getMessage();
                if (!data.endsWith("\r\n")) data += "\r\n";
            }

            byte[] dataArray = data.getBytes();
            ByteBuffer buff = ByteBuffer.allocate(dataArray.length);
            buff.put(dataArray);
            buff.flip();

            try {
                amount += socChannel.write(buff);
            }
            catch (IOException e) {
                e.printStackTrace();
                session.disconnected();
            }


            if (session.getState() == State.DISCONNECTED) return amount;

            fireWriteEvent(request);
        }

        return amount;
    }

    void ping() {
        writeRequests.add(new WriteRequest("PING " + actualHostName + "\r\n", this));
        session.pingSent();
    }

    void pong(IRCEvent event) {
        session.gotResponse();
        String data = event.getRawEventData().substring(event.getRawEventData().lastIndexOf(":") + 1);
        writeRequests.add(new WriteRequest("PONG " + data + "\r\n", this));
    }

    void gotPong() {
        session.gotResponse();
    }

    void quit(String quitMessage) {
        try {

            if (quitMessage == null || quitMessage.length() == 0) {
                quitMessage = ConnectionManager.getVersion();
            }

            WriteRequest request = new WriteRequest("QUIT :" + quitMessage + "\r\n", this);

            writeRequests.add(request);

            // clear out write queue
            doWrites();

            //need to notify conman so it can
            //update session cache
            manager.removeSession(session);

            socChannel.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fireWriteEvent(WriteRequest request) {
        for (WriteRequestListener listener : manager.getWriteListeners()) {
            listener.receiveEvent(request);
        }
    }

    private IRCEvent createDefaultIRCEvent(final String rawData) {
        return
                new IRCEvent() {

                    public Session getSession() {
                        return session;
                    }

                    @SuppressWarnings("unused")
                    //TODO this should be in the interface
                    public String getHostName() {
                        return actualHostName;
                    }

                    public String getRawEventData() {
                        return rawData;
                    }

                    public Type getType() {
                        return IRCEvent.Type.DEFAULT;
                    }
                };
    }

}
