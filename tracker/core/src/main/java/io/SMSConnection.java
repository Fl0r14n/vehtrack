package io;

import atcore.ATClass;
import atcore.URCEventListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import util.Log;

/**
 * Class to work with SMS connections
 */
public class SMSConnection extends Connection {

    private static final String NAME = "SMSConnection";

    /**
     * Open a SMS connection
     *
     * @param cp connection profile. Storage location and index must be
     * specified
     * @return true if connection is opened
     */
    public boolean open(ConnectionProfile cp) {
        if (cp instanceof SMSProfile) {
            smsp = (SMSProfile) cp;
        } else {
            return false;
        }
        at_instance = ATClass.getInstance();
        at_instance.sendAT("AT+CPMS=" + smsp.storage); //set location
        is = new SMSInputStream();
        os = new SMSOutputStream();
        readSMS(smsp.index); //read sms set input stream
        if (smsp.delete) {
            deleteSMS(smsp.index);
        }
        return true;
    }
    private SMSProfile smsp;
    private ATClass at_instance;

    /**
     * Set a SMS Listener
     *
     * @param smsl a SMSEventListener impl
     */
    public void setSMSListener(SMSEventListener smsl) {
        smsl.setConnection(this);
        //at_instance.sendAT("AT+CNMI=1,1"); done in ATClass init
        at_instance.addURCListener(urcl = new URCEventListenerImpl(smsl), "AT+CNMI=1,1");
    }

    /**
     * Remove a SMS Listener
     */
    public void removeSMSListener() {
        at_instance.removeURCListener(urcl);
    }

    /**
     * Close this connection
     */
    public void close() {
        removeSMSListener();
    }

    private class SMSInputStream extends InputStream {

        public int read() throws IOException {
            return message.charAt(pos++);
        }

        public int available() throws IOException {
            return message.length() - pos;
        }
    }
    private String message;
    int pos;

    private class SMSOutputStream extends OutputStream {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        public void write(int arg0) throws IOException {
            baos.write(arg0);
        }

        public void write(byte[] arg0) throws IOException {
            baos.write(arg0);
        }

        public void write(byte[] b, int off, int len) {
            baos.write(b, off, len);
        }

        //this sends the actual sms
        public void flush() throws IOException {
            sendSMS(smsp.phone, baos.toString());
            baos.reset();
        }
    }

    //--------------------------------------------------
    /**
     * Generic method for sending sms.
     *
     * @param phone mobile phone
     * @param text
     */
    public void sendSMS(String phone, String text) {
        synchronized (ATClass.getInstance()) {
            at_instance.sendAT("AT+CMGF=1");
            if (at_instance.sendAT("AT+CMGS=\"" + phone + "\"").indexOf('>') > 0) {
                at_instance.sendAT(text + (char) 26);
            }
        }
    }

    /**
     * Reads SMS at specified index
     *
     * @param index SMS index in memory
     * @return phone number, date & message comma separated
     */
    public String readSMS(int index) {
        try {
            at_instance.sendAT("AT+CMGF=1");
            String response = at_instance.sendAT("AT+CMGR=" + index);
            if (response.indexOf(": 0,") > 0) {
                return ""; //no sms at that location
            }
            int reper = response.indexOf("\",\"") + 3;
            int reper2 = response.indexOf("\",,\"");
            smsp.phone = response.substring(reper, reper2);
            response = response.substring(reper2 + 4);
            reper = response.indexOf('\"');
            smsp.date = response.substring(0, reper);
            message = response.substring(reper + 3, response.indexOf("OK") - 4);
            pos = 0; //reset pos in inputstream buf
            return smsp.phone + "," + smsp.date + "," + message;
        } catch (Exception e) {
            Log.add2Log(e.getMessage(), NAME);
        }
        return "";
    }

    /**
     * Delete a SMS at index
     *
     * @param index
     */
    public void deleteSMS(int index) {
        at_instance.sendAT("AT+CMGD=" + index);
    }

    /**
     * Deletes all SMS from storage
     */
    public void deleteAllSMS() {
        final String response = at_instance.sendAT("AT+CPMS=" + smsp.storage);
        try {
            int length = Integer.parseInt(response.substring(response.indexOf(": ") + 2, response.indexOf(',')));
            for (int i = 1; i < length + 1; i++) {
                at_instance.sendAT("AT+CMGD=" + i);
                Thread.sleep(300);
            }
        } catch (Exception _) {
        }
    }

    private class URCEventListenerImpl extends URCEventListener {

        public URCEventListenerImpl(SMSEventListener smsl) {
            super.addEventType(URCEventListener.AT_CNMI_CMTI); //listen for sms's
            super.addEventType(URCEventListener.AT_CNMI_CMT); //listen also for otap events
            this.smsl = smsl;
        }
        SMSEventListener smsl;

        public void event(String event) {
            if (event.indexOf(AT_CNMI_CMT) > 0) { //about to OTAP
                smsl.eventOTAP();
            }
            if (event.indexOf(AT_CNMI_CMTI) > 0) {
                try {
                    final int reper = event.indexOf('\"');
                    final int reper2 = event.indexOf(',');
                    smsp.storage = event.substring(reper + 1, reper + 3);
                    try {
                        smsp.index = Integer.valueOf(event.substring(reper2 + 1, reper2 + 3)).intValue();
                    } catch (Exception _) {
                        smsp.index = Integer.valueOf(event.substring(reper2 + 1, reper2 + 2)).intValue();
                    }
                    at_instance.sendAT("AT+CPMS=" + smsp.storage); //set location
                    readSMS(smsp.index); // set input stream
                    if (smsp.delete) {
                        deleteSMS(smsp.index);
                    }
                    smsl.eventSMS(smsp.phone, smsp.date, message);
                } catch (Exception e) {
                    Log.add2Log(e.getMessage(), NAME);
                }
            }
        }
    }
    private URCEventListener urcl;

    /**
     * Abstract event class for SMSConnection
     *
     * @author Florian Chis
     */
    public static abstract class SMSEventListener {

        /**
         * Incoming SMS
         *
         * @param from phone numeber
         * @param date received date
         * @param message message
         */
        public abstract void eventSMS(String from, String date, String message);

        /**
         * Incoming OTAP SMS<br> If a OTAP event occures the recommended
         * approach is to destroy MIDlet
         */
        public abstract void eventOTAP();

        protected void setConnection(SMSConnection smsc) {
            this.smsc = smsc;
        }
        private SMSConnection smsc;

        /**
         * Method to send SMS
         *
         * @param to phone number
         * @param message SMS message
         */
        public void sendSMS(String to, String message) {
            smsc.sendSMS(to, message);
        }
    }
}
