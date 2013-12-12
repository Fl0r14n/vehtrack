package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import util.Log;

/**
 * Class that handles UDP connections
 * @author Florian Chis
 */
public class UDPConnection extends Connection {

    private static final String NAME = "UDPConnection";
    
    /**
     * Open a UDP Connection
     * @param cp udp parameters
     * @return true if connection is opened
     */
    public boolean open(final ConnectionProfile cp) {
        try {
            dc = (DatagramConnection) Connector.open(cp.getProfile());
            is = new UDPInputStream();
            os = new UDPOutputStream();
            //thread needed because receive() locks it self waiting
            new Thread(new Runnable() {

                public void run() {
                    try {
                        dgs = dc.newDatagram(dc.getMaximumLength()); //to send
                        dgr = dc.newDatagram(dc.getMaximumLength()); //to receive
                        while (active) {
                            dgr.reset();
                            dc.receive(dgr);
                        }
                    } catch (Exception e) {
                        Log.add2Log(e.getMessage(), NAME);
                        close();
                    }
                }
            }).start();
            return true;
        } catch (IOException ex) {
            Log.add2Log(ex.getMessage(), NAME);
        }
        return false;
    }
    private boolean active = true;
    private DatagramConnection dc;
    private Datagram dgs, dgr;

    /**
     * Close this connection
     */
    public void close() {
        super.close();
        active = false;
        if (dc != null) {
            try {
                dc.close();
            } catch (IOException _) {
            }
        }
        dgs = null;
        dgr = null;
    }

    private class UDPInputStream extends InputStream {

        public int read() throws IOException {
            return dgr.readUnsignedByte();
        }

        public int available() throws IOException {
            return dgr.getLength() - dgr.getOffset();
        }
    }

    private class UDPOutputStream extends OutputStream {

        public void write(int arg0) throws IOException {
            dgs.write(arg0);
        }

        public void write(byte b[]) throws IOException {
            dgs.write(b);
        }

        public void write(byte b[], int off, int len) throws IOException {
            dgs.write(b, off, len);
        }

        public void flush() throws IOException {
            dc.send(dgs);
            dgs.reset();
        }
    }
}
