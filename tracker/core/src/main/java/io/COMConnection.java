package io;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import util.Log;

/**
 * Class that handles serial connections
 */
public class COMConnection extends Connection {
    
    private static final String NAME = "COMConnection";

    /**
     * Get existing COM ports
     * @return Enumeration with available ports
     */
    public static Enumeration getCOMPorts() {
        String coms = System.getProperty("microedition.commports");
        Vector v = new Vector(2);
        StringBuffer buf = new StringBuffer();
        char ch;
        for (int i = 0; i < coms.length(); i++) {
            if ((ch = coms.charAt(i)) != ',') {
                buf.append(ch);
            } else {
                v.addElement(buf.toString());
                buf = new StringBuffer();
            }
        }
        if (buf.length() > 0) {
            v.addElement(buf.toString());
        }
        return v.elements();
    }

    /**
     * Opens com port specified
     * @param ascp connection profile
     * @return true if port is opened
     */
    public boolean open(ConnectionProfile ascp) {
        try {
            asc = (CommConnection) Connector.open(ascp.getProfile());
            is = asc.openInputStream();
            os = asc.openOutputStream();
            return true;
        } catch (Exception e) {
            close();
            Log.add2Log(e.getMessage(), NAME);
            return false;
        }
    }
    private CommConnection asc;

    /**
     * Get baudrate
     * @return connection baudrate
     */
    public int getBaudRate() {
        return asc.getBaudRate();
    }

    /**
     * Set connection baudrate without reopening the connection again
     * @param baudrate
     */
    public void setBaudRate(int baudrate) {
        asc.setBaudRate(baudrate);
    }

    /**
     * Close this connection
     */
    public void close() {
        super.close();
        try {
            if (this.asc != null) {
                this.asc.close();
                this.asc = null;
            }
        } catch (Exception e) {
        }
    }
}
