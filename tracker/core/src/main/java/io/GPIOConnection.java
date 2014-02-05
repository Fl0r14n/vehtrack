package io;

//import com.siemens.icm.io.ATCommand;
//import com.siemens.icm.io.ATCommandFailedException;
//import com.siemens.icm.io.ATCommandListener;
import atcore.ATClass;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import util.Log;

/**
 * Class interacts with device's GPIO pins
 */
public class GPIOConnection extends Connection {

    private static final String NAME = "GPIOConnection";
    
    /**
     * Open a GPIOconnection
     * @param cp gpio config profile
     * @return true if connection opened
     */
    public boolean open(ConnectionProfile cp) {
        if (cp instanceof GPIOProfile) {
            gp = (GPIOProfile) cp;
        } else {
            return false;
        }
        String config = gp.getProfile();
        atc = ATClass.getInstance();
        atc.sendAT("AT^SPIO=1"); //open gpio port
        StringBuffer input = new StringBuffer(); //input buffer
        StringBuffer output = new StringBuffer(); //output buffer
        int j = 0;
        for (int i = 0; i < config.length(); i++) {
            switch (config.charAt(i)) {
                case 'I': { //input
                    pinConfiguration(1, i, 0); //set gpio as input
                    input.append(i).append(",");
                    break;
                }
                case 'O': { //output
                    pinConfiguration(1, i, 1, (gp.output_state >> j) & 1);
                    output.append(i).append(",");
                    j++;
                    break;
                }
                default:
                    break; //pin unused
            }
        }
        try {
            di_port = createPort(input.deleteCharAt(input.length() - 1).toString());
        } catch (Exception _) {
            Log.add2Log("Error in creating GPIO input port", NAME);
        }
        try {
            do_port = createPort(output.deleteCharAt(output.length() - 1).toString());
        } catch (Exception _) {
            Log.add2Log("Error in creating GPIO output port", NAME);
        }
        is = new GPIOInputStream();
        os = new GPIOOutputStream();
        if (gp.gpio_event != null) { //enable GPIO listener
            new Thread(new Runnable() {

                public void run() {
                    active = true;
                    int value = 0;
                    int old_value = 0;
                    while (active) {
                        try {
                            if ((value = is.read()) != old_value) {
                                old_value = value;
                                gp.gpio_event.eventInput(value);
                            }
                            Thread.sleep(gp.poll_rate_ms);
                        } catch (Exception e) {
                            Log.add2Log(e.getMessage(), NAME);
                        }
                    }
                }
            }).start();
            /*
            ATClass.sendAT("AT^SCPOL=1," + di_port); //enable hardware pooling
            try {
            listener = new ATCommand(false); //starting listener
            } catch (ATCommandFailedException ex) {
            ex.printStackTrace();
            } catch (IllegalStateException ex) {
            ex.printStackTrace();
            }
            listener.addListener(new ATCommandListener() {

            public void ATEvent(String event) {
            if (event.indexOf("^SCPOL:") >= 0) {
            event = event.substring(event.indexOf(',') + 1);
            //throw gpio event
            gp.gpio_event.eventInput(Integer.parseInt(event.substring(0, event.indexOf((char) 0x0D))));
            }
            }

            public void RINGChanged(boolean arg0) {
            }

            public void DCDChanged(boolean arg0) {
            }

            public void DSRChanged(boolean arg0) {
            }

            public void CONNChanged(boolean arg0) {
            }
            });
             */
        }
        input = null;
        output = null;
        return true;
    }
    private boolean active = false;
    private GPIOProfile gp;
    private ATClass atc;
    //ATCommand listener;
    private static final int PORT_ERROR = 9999;
    private int di_port = PORT_ERROR, do_port = PORT_ERROR; //non existing values for default

    /**
     * Set pin configuration<br>
     * @param mode 0=Close pin 1=Open pin
     * @param pinId 0 to 9 Gpio
     * @param direction 0=Input 1=Output
     */
    private void pinConfiguration(int mode, int pinId, int direction) {
        atc.sendAT("AT^SCPIN=" + mode + ',' + pinId + ',' + direction);
    }

    /**
     * Set pin configuration<br>
     * @param mode 0=Close pin 1=Open pin
     * @param pinId 0 to 9 Gpio
     * @param direction 0=Input 1=Output
     * @param defaultState 0=low 1=high
     */
    private void pinConfiguration(int mode, int pinId, int direction, int defaultState) {
        atc.sendAT("AT^SCPIN=" + mode + ',' + pinId + ',' + direction + ',' + defaultState);
    }

    private int createPort(String config) {
        int port = 0;
        String stream = atc.sendAT("AT^SCPORT=" + config);
        try {
            port = Integer.parseInt(stream.substring(stream.indexOf(':') + 2, stream.indexOf("OK") - 4));
        } catch (Exception e) {
            Log.add2Log(e.getMessage(), NAME);
        }
        return port;
    }

    /**
     * Close this connection
     */
    public void close() {
        super.close();
        /*
        if (listener != null) {
        try {
        listener.release();
        } catch (ATCommandFailedException ex) {
        ex.printStackTrace();
        }
        listener = null;
        }
         */
        active = false;
        atc.sendAT("AT^SDPORT=" + di_port); //delete port
        atc.sendAT("AT^SDPORT=" + do_port);
        atc.sendAT("AT^SPIO=0"); //close port
        gp = null;
    }

    private class GPIOInputStream extends InputStream {

        public int read() throws IOException {
            final String stream = atc.sendAT("AT^SGIO=" + di_port);
            return Integer.parseInt(stream.substring(stream.indexOf(':') + 2, stream.indexOf("OK") - 4));
        }

        public int available() throws IOException {
            return 1; //we can read anytime the gpio port
        }
    }

    private class GPIOOutputStream extends OutputStream {

        public void write(int value) throws IOException {
            atc.sendAT("AT^SSIO=" + do_port + ',' + value);
            if (gp.gpio_event != null) {
                //throw output event if we have a listener active
                gp.gpio_event.eventOutput(value);
            }
        }
    }
}
