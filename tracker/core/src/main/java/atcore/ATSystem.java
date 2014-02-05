package atcore;

import java.util.Calendar;
import java.util.TimeZone;
import util.Log;

/**
 * Class for system features. Main functions of TC65
 */
public class ATSystem {

    private static final String NAME = "ATSystem";
    
    /**
     * Get if PIN code si required
     * @return true if PIN code is required
     */
    public boolean isPINRequired() {
        String response = ATClass.getInstance().sendAT("AT+CPIN?");
        return response.indexOf("READY") == 0;
    }

    /**
     * Unlock SIM with pin code
     * @param pin PIN code
     * @return true if pin accepted
     */
    public boolean setPIN(int pin) {
        String response = ATClass.getInstance().sendAT("AT+CPIN=" + pin);
        return response.indexOf("OK") > 0;
    }

    /**
     * Get terminal's IMEI code
     * @return the terminal IMEI
     */
    public static String getIMEI() {
        String raspuns = ATClass.getInstance().sendAT("AT+GSN");
        try {
            raspuns = raspuns.substring(9);
            return raspuns.substring(0, raspuns.indexOf('\n') - 1);
        } catch (Exception _) {
            return "";
        }
    }

    /**
     * Get SIM card identification number
     * @return SIM card identification number
     */
    public static String getSCID() {
        String value = ATClass.getInstance().sendAT("AT^SCID");
        try{
            value = value.substring(value.indexOf(": ")+2);
            return value.substring(0,value.indexOf('\n')-1);
        }catch(Exception _) {
            return "";
        }
    }

    /**
     * Soft reset the module
     */
    public static void reset() {
        ATClass.getInstance().sendAT("AT+CFUN=1,1");
    }

    /**
     * Soft shutdown the module
     */
    public static void shutDown() {
        ATClass.getInstance().sendAT("AT^SMSO");
    }

    /**
     * Get system time UTC or by timezone
     * @param timezone Timezone if such
     * @return Calendar with now time
     */
    public static Calendar getSystemTime(String timezone) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        //assume module time is UTC
        String str = ATClass.getInstance().sendAT("AT+CCLK?");
        int pos;
        int val = Integer.parseInt(str.substring(str.indexOf('"')+1,pos = str.indexOf('/'))); //year
        str = str.substring(pos+1);
        c.set(Calendar.YEAR, 2000+val);
        val = Integer.parseInt(str.substring(0,pos = str.indexOf('/'))); //month
        str = str.substring(pos+1);
        c.set(Calendar.MONTH, val-1);
        val = Integer.parseInt(str.substring(0,pos = str.indexOf(','))); //day
        str = str.substring(pos+1);
        c.set(Calendar.DAY_OF_MONTH, val);
        val = Integer.parseInt(str.substring(0,pos = str.indexOf(':'))); //hour
        str = str.substring(pos+1);
        c.set(Calendar.HOUR_OF_DAY, val);
        val = Integer.parseInt(str.substring(0,pos = str.indexOf(':'))); //minute
        str = str.substring(pos+1);
        c.set(Calendar.MINUTE, val);
        val = Integer.parseInt(str.substring(0,str.indexOf('"'))); //month
        c.set(Calendar.SECOND, val);
        if(timezone!=null && !timezone.equals("")) {
            c.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        return c;
    }

    /**
     * To String system time
     * @return time as received from module
     */
    public static String getSystemTimeAsString() {
        String stream = "";
        try {
            stream = ATClass.getInstance().sendAT("AT+CCLK?");
            stream = stream.substring(19, 36);
        } catch (Exception e) {
            Log.add2Log(e.getMessage(), NAME);
        }
        return stream;
    }

    /**
     * Set system time from calendar
     * @param c Calendar object
     * @return true if operation successful
     */
    public static boolean setSystemTime(Calendar c) {
        //05/07/06,22:11:35
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        StringBuffer buf = new StringBuffer();
        buf.append(c.get(Calendar.YEAR)).delete(0, 2).append('/');
        int val = c.get(Calendar.MONTH)+1;
        buf.append((val<10?"0":"")).append(val).append('/');
        val = c.get(Calendar.DAY_OF_MONTH);
        buf.append((val<10?"0":"")).append(val).append(',');
        val = c.get(Calendar.HOUR_OF_DAY);
        buf.append((val<10?"0":"")).append(val).append(':');
        val = c.get(Calendar.MINUTE);
        buf.append((val<10?"0":"")).append(val).append(':');
        val = c.get(Calendar.SECOND);
        buf.append((val<10?"0":"")).append(val);
        return setSystemTime(buf.toString());
    }

    /**
     * Set system time like 05/07/06,22:11:35. Try to keep it UTC
     * @param time time string as yy/mm/dd,hh:MM:ss
     * @return true if time was set
     */
    public static boolean setSystemTime(String time) {
        return ATClass.getInstance().sendAT("AT+CCLK=\"" + time + "\"").indexOf("OK") >= 0;
    }
}
