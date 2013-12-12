package io;

import com.siemens.icm.io.InPort;
import com.siemens.icm.io.InPortListener;
import com.siemens.icm.io.OutPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import util.Log;

/**
 * Class interacts with TC65i's GPIO pins (using TC65i's low level classes)
 * @author Florian Chis
 */
public class GPIOConnectionI extends Connection {
    
    private static final String NAME = "GPIOConnectionI";

    /**
     * Open connection
     * @param cp connection profile
     * @return true if successful
     */
    public boolean open(ConnectionProfile cp) {
        if (cp instanceof GPIOProfile) {
            gp = (GPIOProfile) cp;
        } else {
            return false;
        }
        Vector inPins = new Vector(10);
        Vector outPins = new Vector(10);
        Vector outVals = new Vector(10);
        String config = gp.getProfile();
        int j=0;
        for (int i = 0; i < config.length(); i++) {
            switch (config.charAt(i)) {
                case 'I': { //input
                    //Log.add2Log("IGPIO"+(i+1), this.getClass());
                    inPins.addElement("GPIO"+(i+1));
                    break;
                }
                case 'O': { //output
                    //Log.add2Log("OGPIO"+(i+1), this.getClass());
                    outPins.addElement("GPIO"+(i+1));
                    outVals.addElement(Integer.valueOf(""+((gp.output_state>>j)&1)));
                    j++;
                    break;
                }
                default:
                    break; //pin unused
            }
        }
        try{
            if(!inPins.isEmpty()) {
                inPort = new InPort(inPins);
                is = new GPIOInputStream();
                if (gp.gpio_event!=null) { //enable GPIO listener
                    inPort.addListener(new InPortListener() {

                        public void portValueChanged(int portValue) {
                            gp.gpio_event.eventInput(portValue);
                        }
                    });
                }
            }
            if(!outPins.isEmpty()) {
                outPort = new OutPort(outPins,outVals);
                os = new GPIOOutputStream();
            }
            return inPins.size()>0 || outPins.size()>0;
        }catch(IOException ioe) {
            Log.add2Log(ioe.getMessage(), NAME);
            return false;
        }
    }
    private GPIOProfile gp;
    private InPort inPort;
    private OutPort outPort;

    private class GPIOInputStream extends InputStream {

        public int read() throws IOException {
            return inPort.getValue();
        }

        public int available() throws IOException {
            return 1; //we can read anytime the gpio port
        }
    }

    private class GPIOOutputStream extends OutputStream {

        public void write(int value) throws IOException {
            outPort.setValue(value);
            if(gp.gpio_event!=null) {
                //throw output event if we have a listener active
                gp.gpio_event.eventOutput(value);
            }
        }
    }
}
