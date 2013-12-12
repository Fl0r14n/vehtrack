package atcore;

import java.util.Vector;
import util.Log;

/**
 * Class used for network registration
 * @author Florian Chis
 */
public class ATNetwork {
    
    private static final String NAME = "ATNetwork";

    /**
     * Get signal strength in dBm
     * @return received signal strength in dBm
     */
    public static int getSignalDBM() {
        int signal = 0;
        try {
            String stream = ATClass.getInstance().sendAT("AT+CSQ");
            stream = stream.substring(stream.indexOf(": ") + 2);
            signal = Integer.valueOf(stream.substring(0, stream.indexOf(','))).intValue();
        } catch (Exception _) {
        }
        if (signal == 99) {
            return -113;
        } else {
            return -113 + (signal * 2);
        }
    }

    /**
     * Get HOME network name
     * @return name of the current registered network
     */
    public static String getNetworkName() {
        String response = ATClass.getInstance().sendAT("AT+COPS?");
        if (response.indexOf("0,0,") > 0) {
            response = response.substring(response.indexOf('\"') + 1);
            return response.substring(0, response.indexOf('\"'));
        }
        return null;
    }

    /**
     * Get network status
     * @return netowrk status else -1. Compare with class constants.
     */
    public static int getNetworkStatus() {
        String response = ATClass.getInstance().sendAT("AT+CREG?");
        try {
            response = response.substring(response.indexOf(',') + 1);
            return Integer.parseInt(response.substring(0, response.indexOf(0x0D)));
        } catch (Exception _) {
        }
        return -1;
    }
    public static final int STATUS_NOT_REGISTERD = 0;
    public static final int STATUS_REGISTERED_HOME = 1;
    public static final int STATUS_SEARCHING = 2;
    public static final int STATUS_REGISTRATION_DENIED = 3;
    public static final int STATUS_UNKNOWN = 4;
    public static final int STATUS_ROAMING = 5;

    /**
     * To String (detailed) of network status
     * @return full network status
     */
    public static String networkToString() {
        StringBuffer buf = new StringBuffer();
        switch (getNetworkStatus()) {
            case -1: {
                buf.append("Status ERROR");
                break;
            }
            case STATUS_NOT_REGISTERD: {
                buf.append("Status NOT REGISTERED");
                break;
            }
            case STATUS_REGISTERED_HOME: {
                buf.append("Status HOME NETWORK");
                break;
            }
            case STATUS_SEARCHING: {
                buf.append("Status SEARCHING");
                break;
            }
            case STATUS_REGISTRATION_DENIED: {
                buf.append("Status REGISTRATION DENIED");
                break;
            }
            case STATUS_UNKNOWN: {
                buf.append("Status UNKNOWN");
                break;
            }
            case STATUS_ROAMING: {
                buf.append("Status ROAMING");
                break;
            }
        }
        buf.append(" Network Name: ").append(ATNetwork.getNetworkName());
        buf.append(" CELL: ").append(ATNetwork.cellMonitoring().elementAt(0));
        buf.append(" Signal(DBm): ").append(ATNetwork.getSignalDBM());
        return buf.toString();
    }

    /**
     * Set GPRS Parameters
     * @param apn Access Point Name
     * @param username Username
     * @param password Password
     * @param dns Access Point Name
     */
    public static void setGPRSparam(String apn, String username, String password, String dns) {
        ATClass.getInstance().sendAT("at^sjnet=\"gprs\",\"" + apn + "\",\"" + username + "\",\"" + password + "\",\"" + dns + "\",0");
    }

    /**
     * Try to auto set the GPRS parameters<br>
     * Known networks (Orange RO, Vodafone RO, Cosmote RO)
     */
    public static void setGPRSparam() {
        String value = ATSystem.getSCID();
        ATClass atc = ATClass.getInstance();
        if (value.startsWith("894010")) {
            Log.add2Log("Found ORANGE RO", NAME);
            atc.sendAT("at^sjnet=gprs,internet,\"\",\"\",\"\",0");
            return;
        }
        if (value.startsWith("894001")) {
            Log.add2Log("Found VODAFONE RO", NAME);
            atc.sendAT("at^sjnet=gprs,internet.vodafone.ro,internet.vodafone.ro,vodafone,\"\",0");
            return;
        }
        if (value.startsWith("894003")) {
            Log.add2Log("Found COSMOTE RO", NAME);
            atc.sendAT("at^sjnet=gprs,vpn-shared,\"\",\"\",\"\",0");            
        }
    }

    /**
     * Cell monitoring method
     * @return Vector<String> with network cells. MCC+MNC+CELL is returned
     */
    public static Vector cellMonitoring() {
        String cells = ATClass.getInstance().sendAT("AT^SMONC");
        cells = cells.substring(cells.indexOf(": ") + 2, cells.indexOf("OK") - 4);
        StringBuffer buf = new StringBuffer();
        Vector v = new Vector(7);
        for (int i = 0; i < 7; i++) { //max 7 cells
            if (cells.startsWith("000")) { //no cell here
                try {
                    cells = cells.substring(29); //cut
                } catch (Exception _) {
                }
            } else {
                if (!cells.substring(12, 16).equalsIgnoreCase("FFFF")) { //did we read a valid cell?
                    buf.append(cells.substring(0, 3)).append(cells.substring(4, 6)).append(cells.substring(12, 16));
                    v.addElement(buf.toString());
                    buf = new StringBuffer();
                    try {
                        cells = cells.substring(32);
                    } catch (Exception _) {
                    }
                }
            }
        }
        return v;
    }

    /**
     * Add a network registration event listener.
     * @param urcl URCEventListener interface implementation
     */
    public static void addNetworkListener(URCEventListener urcl) {
        //ATClass.getInstance().sendAT("AT+CREG=1");
        urcl.addEventType(URCEventListener.AT_CREG);
        ATClass.getInstance().addURCListener(urcl,"AT+CREG=1");
    }

    /**
     * Set sync LED mode
     * @param mode mode from class constants
     */
    public static void setSyncLED(int mode) {
        ATClass.getInstance().sendAT("AT^SSYNC="+mode);
    }
    public static final int SYNC_LED_POWER = 0;
    public static final int SYNC_LED_NETWORK = 1;
    public static final int SYNC_LED_NETWORK_AND_SLEEP = 2;
}
