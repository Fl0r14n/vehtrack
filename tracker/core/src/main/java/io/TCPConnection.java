package io;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import util.Log;

/**
 * Class that handles TCP connections.
 */
public class TCPConnection extends Connection {

    private static final String NAME = "TCPConnection";
    
    /**
     * Open a TCP connection
     * @param cp connection profile with tcp parameters
     * @return true if connection is opened
     */
    public boolean open(ConnectionProfile cp) {
        try {
            s = (SocketConnection) Connector.open(cp.getProfile(), Connector.READ_WRITE, false);
            s.setSocketOption(SocketConnection.KEEPALIVE, 1);
            is = s.openInputStream();
            os = s.openOutputStream();
            return true;
        } catch (IOException e) {
            close();
            Log.add2Log(e.getMessage(), NAME);
            return false;
        }
    }
    private SocketConnection s;

    /**
     * Close this connection
     */
    public void close() {
        super.close();
        try {
            if (this.s != null) {
                this.s.close();
                this.s = null;
            }
        } catch (IOException _) {
        }
    }
}
