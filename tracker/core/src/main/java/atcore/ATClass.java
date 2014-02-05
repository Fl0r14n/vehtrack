package atcore;

import com.siemens.icm.io.ATCommand;
import com.siemens.icm.io.ATCommandListener;
import io.Connection;
import io.ConnectionProfile;
import java.util.Vector;
import util.Log;

/**
 * Class for sending AT commands
 */
public class ATClass {
    
    private static final String NAME = "ATClass";

    private ATClass() {
    }

    //singleton
    public static synchronized ATClass getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new ATClass();
            listeners = new Vector(5);
            try {
                atc1 = new ATCommand(false);
                atc2 = new ATCommand(false);
                atc3 = new ATCommand(false);
                atc2.addListener(new ATCommandListener() {

                    public void ATEvent(String event) {
                        if (debug) {
                            Log.add2Log(event, NAME);
                        }
                        for (int i = 0; i < listeners.size(); i++) { //all listeners
                            URCEventListener urcl = (URCEventListener) listeners.elementAt(i);
                            for (int j = 0; j < urcl.event_headers.size(); j++) { //see if event macthes listeners choice
                                String event_type = (String) urcl.event_headers.elementAt(j);
                                if (event.indexOf(event_type) > 0) {
                                    urcl.event(event);
                                    return;
                                }
                            }
                        }
                    }

                    public void RINGChanged(boolean SignalState) {
                    }

                    public void DCDChanged(boolean SignalState) {
                    }

                    public void DSRChanged(boolean SignalState) {
                    }

                    public void CONNChanged(boolean SignalState) {
                    }
                });
            } catch (Exception e) {
                //this is realy bad
                Log.add2Log(e.getMessage(), NAME);
            }
            return instance;
        }
    }
    private static ATClass instance = null;
    private static ATCommand atc1, atc2, atc3;

    /**
     * Add at event's listener
     * @param urcl URCEventListener implementation
     */
    public void addURCListener(URCEventListener urcl) {
        addURCListener(urcl, null);
    }

    /**
     * Add at event's listener
     * @param urcl URCEventListener implementation
     * @param atCommand required AT command if listerer is trigered through AT command; otherwise null
     * @return true if command succesful
     */
    public boolean addURCListener(URCEventListener urcl, String atCommand) {
        if (atCommand != null) {
            try {
                String response = atc2.send(atCommand + "\r");
                if (debug) {
                    Log.add2Log(response, NAME);
                }
                if (response.indexOf("OK") == -1) {
                    throw new Exception("Can't activate URC listener: " + response);
                }
            } catch (Exception e) {
                Log.add2Log(e.getMessage(), NAME);
                return false;
            }
        }
        if (urcl != null) {
            listeners.addElement(urcl);
        }
        return true;
    }
    static Vector listeners;

    /**
     * Remove AT event listener
     * @param urcl URCEventListener to be removed
     */
    public void removeURCListener(URCEventListener urcl) {
        removeURCListener(urcl, null);
    }

    /**
     * Remove AT event listener
     * @param urcl URCEventListener to be removed
     * @param atCommand close command to be sent if any
     * @return true if command succesful
     */
    public boolean removeURCListener(URCEventListener urcl, String atCommand) {
        if (atCommand != null) {
            try {
                String response = atc2.send(atCommand + "\r");
                if (debug) {
                    Log.add2Log(response, NAME);
                }
                if (response.indexOf("OK") == -1) {
                    throw new Exception("Can't remove URC listener: " + response);
                }
            } catch (Exception e) {
                Log.add2Log(e.getMessage(), NAME);
                return false;
            }
        }
        if (urcl != null) {
            listeners.removeElement(urcl);
        }
        return true;
    }

    /**
     * Clear all listeners
     */
    public void removeAllURCListeners() {
        listeners.removeAllElements();
    }

    /**
     * Method to send AT commands like at+gsn etc.
     * @param cmd command
     * @return the answer returned by the command execution
     */
    public synchronized String sendAT(String cmd) {
        String response = "";
        try {
            response = atc1.send(cmd + '\r');
            atc1.release();
            atc1 = new ATCommand(false);
        } catch (Exception e) {
            Log.add2Log(e.getMessage(), NAME);
        }
        if (debug) {
            Log.add2Log(response, NAME);
        }
        return response;
    }

    /**
     * Set debug messages true or false
     * @param choice choice
     */
    public static void setDebug(boolean choice) {
        debug = choice;
    }
    private static boolean debug = true;

    /**
     * Get a data connection through AT interface.<br>
     * <b>Only one data connecion possible at one time<b>
     * @return Connection interface implementation
     */
    public Connection getATDataConnection() {
        Connection c = new Connection() {

            public boolean open(ConnectionProfile cp) {
                try {
                    String response = atc3.send(cp.getProfile() + '\r');
                    if (response.indexOf("OK") > 0 || response.indexOf("CONNECT") > 0) {
                        is = atc3.getDataInputStream();
                        os = atc3.getDataOutputStream();
                        return true;
                    }
                } catch (Exception e) {
                    Log.add2Log(e.getMessage(), NAME);
                }
                return false;
            }
        };
        return c;
    }
}
