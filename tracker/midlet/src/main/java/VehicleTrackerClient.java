import atcore.ATClass;
import atcore.ATNetwork;
import atcore.ATSystem;
import atcore.URCEventListener;
import dev.gps.GPSDevice;
import dev.gps.SIRF;
import io.GPIOProfile.GPIOEvent;
import io.SMSConnection.SMSEventListener;
import io.*;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.midlet.MIDlet;
import lib.gps.Localization;
import lib.gps.Localization.LocationListener;
import lib.gps.Location;
import lib.jabber.JabberClient;
import lib.jabber.JabberEventListener;
import lib.jabber.JabberProfile;
import lib.jabber.JabberWatchdogListener;
import lib.xml.XMLNode;
import util.Log;
import util.Log.Debug;
import util.Queue;

public class VehicleTrackerClient extends MIDlet {

    public static final String VERSION = "";    

    public void startApp() {
        init();
        //---------------StartGPS-----------------------------------------------
        startGPS();
        //---------------Debug--------------------------------------------------
        startDebug();
        //----------------Set GPIO Ports----------------------------------------
        startWatchdog();
        startGPIO();
        //--------------Queue---------------------------------------------------
        startPersistence();
        //---------------SMS-Listener-------------------------------------------
        startSMS();
        //--------------Jabber--------------------------------------------------
        startJabber();
        //----------------------------------------------------------------------
        while(true) {
            //send positions
            if(Position.getPositionsCount()>5) {                
                jel.sendMessage(Position.getPositions(), jabber_remote);
            }
            //feed watchdog            
            //TODO
            feedWatchdog();
        }
    }    

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public static void quitApp() {
        if (instance != null) {
            instance.destroyApp(true);
            instance.notifyDestroyed();
            instance = null;
        }
    }
    private static VehicleTrackerClient instance;

    private void init() {
        instance = this;
        try {
            Thread.sleep(3000); //let equipment do init stuff avoid serious bug TC65 after autostart
        } catch (Exception _) {
        }
        ATClass.setDebug(false); //no debug for AT messages
        ATNetwork.setSyncLED(ATNetwork.SYNC_LED_NETWORK);
    }

    private void startGPS() {
        COMProfile gpsp = new COMProfile();
        gpsp.setCOMPort("COM0");
        GPSDevice gpsDriver = new SIRF(gpsp);
        GPSConnection gpsc = new GPSConnection(gpsDriver);
        if (gpsc.open(gpsp)) {
            gps_is = gpsc.getInputStream();
            gps_os = gpsc.getOutputStream();
            Localization.setStreams(gps_is, gps_os);
            Localization l = Localization.getInstance();
            l.setLocationListener(new LocationListenerImpl());
        }
    }
    private InputStream gps_is;
    private OutputStream gps_os;

    private void startDebug() {
        if (readJADParam("comdebug", "true").equals("true")) {
            if (readJADParam("comdebugport", "COM0").equals("COM0")) {
                Log.setDebug(new Debug() {

                    public void add2Log(String stream, String source) {
                        try {                            
                            gps_os.write(new StringBuffer().append(source).append(": ").append(stream).append("\n\r").toString().getBytes());
                        } catch (Exception _) {
                        }
                    }
                });
            }
            Log.add2Log(VERSION);
        }
    }

    private void startWatchdog() {
        String config = readJADParam("watchdog", "");
        if (config.indexOf('O') != -1) {
            GPIOProfile watchdogp = new GPIOProfile();
            watchdogp.setGPIOConfiguration(config);
            //set default watchdog state
            if (readJADParam("feeddefaultstate", "low").equalsIgnoreCase("low")) {
                watchdog_default_state = 0;
            } else {
                watchdog_default_state = 1;
            }
            watchdogp.setDefaultOutputState(watchdog_default_state);
            Connection watchdog = new GPIOConnectionI();
            if (!watchdog.open(watchdogp)) {
                reset("Reset: WATCHDOG can't open connection");
            }
            watchdog_os = watchdog.getOutputStream();
            feedWatchdog();
        }
    }
    private OutputStream watchdog_os;
    private int watchdog_default_state;

    //calling this method feeds the hardware watchdog
    private void feedWatchdog() {
        if (watchdog_os != null) {
            try {
                watchdog_os.write(watchdog_default_state ^ 1);
                Thread.sleep(1000);
                watchdog_os.write(watchdog_default_state);
            } catch (Exception _) {
                Log.add2Log("WATCHDOG: " + _.getMessage());
            }
        }
    }

    private void startGPIO() {
        String config = readJADParam("gpioport", "");
        if (config.length() > 9) {
            config = config.substring(0, 9); //last pin used for watchdog
        }
        GPIOProfile gpiop = new GPIOProfile();
        gpiop.setGPIOConfiguration(config);
        gpiop.setGPIOListener(new GPIOEventImpl(), gpio_poll_rate);
        //set gpio default state
        if (readJADParam("outputdefaultstate", "low").equalsIgnoreCase("low")) {
            gpio_output_default_state = 0;
        } else {
            gpio_output_default_state = 1;
        }
        gpiop.setDefaultOutputState(gpio_output_default_state);
        GPIOConnectionI gpioc = new GPIOConnectionI();
        if (!gpioc.open(gpiop)) {
            reset("Reset: WATCHDOG can't open gpio port");
        }
        gpio_os = gpioc.getOutputStream();
    }
    private static OutputStream gpio_os;
    private static int gpio_input_default_state;
    private static int gpio_output_default_state;
    private static final int gpio_poll_rate = 1000;

    private static void imobilize(boolean choice) { //imobilize vehicle        
        if (gpio_os != null) {
            try {
                if (choice) {
                    gpio_os.write(gpio_output_default_state ^ 1); //imobilize on                
                } else {
                    gpio_os.write(gpio_output_default_state); //imobilize off
                }
            } catch (Exception _) {
                Log.add2Log("IMOBILIZE: " + _.getMessage());
            }
        }
    }

    private void startPersistence() {
        journey = new Journey();
        //messages queue
        q = new Queue();
    }
    private static Journey journey;
    private static Queue q;

    private void startSMS() {
        SMSProfile smsp = new SMSProfile();
        smsp.setDeleteSMSAfterRead(true);
        smsp.setMessageStorage(SMSProfile.MESAGE_STORAGE_BOTH);
        SMSConnection sms = new SMSConnection();
        if (!sms.open(smsp)) {
            Log.add2Log(ATSystem.getSystemTimeAsString() + "SMS: can't open connection");
        }
        sms.setSMSListener(new SMSEventListenerImpl());
        sms.deleteAllSMS(); //clean SIM card of sms messages
    }

    private void startJabber() {
        ATNetwork.setGPRSparam();
        jp = new JabberProfile();
        //jp.setDebugXMPP(true);
        jp.setHost(readJADParam("jabberhost", ""));
        jp.setPort(Integer.parseInt(readJADParam("jabberport", "")));
        jp.setJabberResource("VehicleTracker");
        jabber_domain = readJADParam("jabberdomain", "");
        jp.setJabberUser(ATSystem.getIMEI() + "@" + jabber_domain);
        jp.setJabberPassword(readJADParam("jabberpassword", ""));
        Log.add2Log(jp.toString());
        jabber_remote = readJADParam("jabberremote", "")+"@"+jabber_domain;
        jc = new JabberClient(jp);
        jc.addJabberEventListener(jel = new JabberEventListenerImpl());
        jc.setWatchdogEventListener((JabberWatchdogListener) jel);
        //add network listener
        ATNetwork.addNetworkListener(new NetworkEventListenerImpl());
    }
    private static JabberProfile jp;
    private static JabberClient jc;
    private static JabberEventListenerImpl jel;
    private static String jabber_domain;
    private static String jabber_remote;

    private static String readJADParam(String param, String param_default) {
        String aux = instance.getAppProperty(param);
        if (aux == null) {
            Log.add2Log("Param " + param + " not found");
            return param_default;
        }
        return aux;
    }

    public static void reset(String str) {
        //TODO do a smart reset, only when not in a journey
        ATSystem.reset();
    }

    private static String processMessage(String message, String from) throws Exception {
        String result;
        if (message.equalsIgnoreCase("signal?")) {
            result = "Signal strength: " + ATNetwork.getSignalDBM() + "dBm";
        } else if (message.equalsIgnoreCase("network?")) {
            result = ATNetwork.networkToString();
        } else if (message.equalsIgnoreCase("position?")) {
            result = location != null ? location.toString() : "no data";
        } else if (message.startsWith("imobilize ")) {
            //TODO only if admin check if from is allowed
            message = message.substring(message.indexOf(" ") + 1);
            if (message.toLowerCase().equals("true")) {
                imobilize(true);
            } else {
                imobilize(false);
            }
            Thread.sleep(1000); //waith for it to happen
            result = "Imobilized: " + isImobilized;
        } else if (message.equalsIgnoreCase("reboot")) {
            result = "Rebooting...";
            reset("Reset sent by: " + from);
        } else {
            result = "No such command: " + message;
        }
        return result;
    }

    //----------------------LISTENERS-------------------------------------------
    private static class SMSEventListenerImpl extends SMSEventListener {

        public void eventSMS(String from, String date, String message) {
            Log.add2Log(message + " from: " + from);
            try {
                sendSMS(from, processMessage(message, from));
            } catch (Exception e) {
                Log.add2Log("SMS message exception: " + e.getMessage());
            }
        }

        public void eventOTAP() {
            Log.add2Log("OTAP");
            //quitApp();
            reset("Reset: OTAP"); //reset we don't want otap
        }
    }

    private static class JabberEventListenerImpl extends JabberEventListener implements JabberWatchdogListener {

        public JabberEventListenerImpl() {
            init();
        }

        private void init() {
            new Thread(new Runnable() {

                public void run() {
                    int i = 0;
                    while (true) {
                        try {
                            Thread.sleep(120000); //once every 2min
                            if (i == 5) {
                                presence(); //iq first then presence to confirm that we are indeed online
                                i = 0;
                            } else {
                                presenceBulk(); //just presence
                                i++;
                            }
                        } catch (Exception _) {
                        }
                    }
                }
            }).start();
        }

        //JabberEventListener
        public void eventMessage(String message, String from) {
            server_reply = true;
            try {
                Log.add2Log(message + " <- " + from);
                sendMessage(processMessage(message, from), from);
            } catch (Exception e) {
                Log.add2Log("Event message exception");
                if (!isConnecting) {
                    if (jc != null) {
                        jc.reconnect();
                    }
                }
            }
        }

        public void eventIq(XMLNode node) {
            server_reply = true;
            try {
                if (node.getChild("query").getAttr("xmlns").equals("jabber:iq:time")) {
                    StringBuffer buf = new StringBuffer();
                    String time = node.getChild("query").getChild("utc").getValue();
                    buf.append(time.substring(2));
                    buf.setCharAt(6, ',');
                    buf.insert(2, '/');
                    buf.insert(5, '/');
                    // 07/05/02,08:37:02
                    ATSystem.setSystemTime(buf.toString());
                }
            } catch (Exception e) {
                Log.add2Log("Event iq exception");
                if (!isConnecting) {
                    if (jc != null) {
                        jc.reconnect();
                    }
                }
            }
        }

        public void eventPresence(XMLNode node) {
            server_reply = true;
        }

        public void sendMessage(String body, String to) {
            try{
                Log.add2Log(body + " -> " + to);
                super.sendMessage(body, to);
            } catch(Exception e) {
                Log.add2Log("Send message exception");
                if (!isConnecting) {
                    if (jc != null) {
                        jc.reconnect();
                    }
                }
            }
        }

        public void presence() throws Exception {
            try {
                if (jc != null) {
                    Log.add2Log("Seting time"); //ask server for time
                    sendIq("get", "jabber:iq:time", jabber_domain);
                    int i = 0;
                    server_reply = false;
                    while (!server_reply) { //connection still active
                        Thread.sleep(100);
                        i++;
                        if (i > 150) {
                            throw new Exception("Server not replying");
                        }
                    }
                    sendPresence("", ""); //tell server we are still here
                }
            } catch (Exception e) {
                Log.add2Log("Send presence exception");
                if (!isConnecting) {
                    if (jc != null) {
                        jc.reconnect();
                    }
                }
            }
        }
        private boolean isConnecting = true;
        private boolean server_reply = false;

        public void presenceBulk() throws Exception {
            try { //just a plain presence used for keep alive function
                if (jc != null) {
                    super.sendPresence("", "");
                }
            } catch (Exception e) {
                if (!isConnecting) {
                    if (jc != null) {
                        jc.reconnect();
                    }
                }
            }
        }

        //JabberWatchdogListener
        public void connecting() {
            isConnecting = true;
            Log.add2Log("Jabber status connecting");
            Log.add2Log(ATNetwork.networkToString());
        }

        public void online() {
            isConnecting = false;
            Log.add2Log("Jabber status online");
            Log.add2Log(ATNetwork.networkToString());
        }

        public void connectionNotPossible() {
            String msg = "Reset: Jabber connection not possible";
            Log.add2Log(msg);
            Log.add2Log(ATNetwork.networkToString());
            reset(msg);
        }

        public void exceptionOcured(Exception ex) {
            Log.add2Log("Jabber exception ocured");
        }

        public void errorOccured(Error er) {
            Log.add2Log("Reset: Out of memory (Jabber connection)");
            reset("Reset: Out of memory (Jabber connection)");
        }
    }

    private static final class NetworkEventListenerImpl extends URCEventListener {
        
        public NetworkEventListenerImpl() {
            event(null);
        }

        public void event(String event) {
            switch (networkStatus = ATNetwork.getNetworkStatus()) {
                case ATNetwork.STATUS_NOT_REGISTERD: {
                    if (isGPRS) {
                        isGPRS = false;
                        break;
                    } else {
                        return;
                    }
                }
                case ATNetwork.STATUS_REGISTRATION_DENIED: {
                    if (isGPRS) {
                        isGPRS = false;
                        break;
                    } else {
                        return;
                    }
                }
                case ATNetwork.STATUS_UNKNOWN: {
                    if (isGPRS) {
                        isGPRS = false;
                        break;
                    } else {
                        return;
                    }
                }
                case ATNetwork.STATUS_REGISTERED_HOME: {
                    if (!isGPRS) {
                        isGPRS = true;
                        break;
                    } else {
                        return;
                    }
                }
                case ATNetwork.STATUS_ROAMING: {
                    if (!isGPRS) {
                        isGPRS = true;
                        break;
                    } else {
                        return;
                    }
                }
                case ATNetwork.STATUS_SEARCHING: {
                    if ((search_retry++) > 15) { //15 is the optimal number
                        reset("Reset: No GSM Network");
                    }
                    Log.add2Log("Search retry: " + search_retry);
                    return;
                }
            }
            if (isGPRS) {
                search_retry = 0;
                Log.add2Log(ATSystem.getSystemTimeAsString() + " GPRS: HomeNetwork or Roaming");
                if (jc == null) {
                    jc = new JabberClient(jp);
                    jc.addJabberEventListener(jel);
                    jc.setWatchdogEventListener((JabberWatchdogListener) jel);
                }
            } else {
                Log.add2Log(ATSystem.getSystemTimeAsString() + " GPRS: No SIM card or GPRS");
                if (jc != null) {
                    jc.close();
                    jc = null;
                }
            }
        }
        boolean isGPRS = true;
        int search_retry = 0;
    }
    private static int networkStatus;

    private static class LocationListenerImpl implements LocationListener {

        public LocationListenerImpl() {
            init();
        }

        private void init() {
            dtcity = Long.parseLong(readJADParam("dtcity", "60"))*1000;
            dtroad = Long.parseLong(readJADParam("dtroad", "300"))*1000;
            dtroaming = Long.parseLong(readJADParam("dtroaming", "300"))*1000;
            dtstop = Long.parseLong(readJADParam("dtstop", "1800"))*1000;
            timestampPositions = System.currentTimeMillis();
        }
        long dtcity = 60000, dtroad = 300000, dtroaming = 300000, dtstop = 1800000, positionSendTime, timestampPositions;

        public void locationUpdated(Location l) {
            //Log.add2Log(l.toString(),this.getClass());
            location = l;
            double speedKmh = l.getSpeed();
            if (isJourney) {
                //calculate distance, set maxSpeed
                distance += speedKmh * 0.277777778; //we trust that gps sends gga at 1s interval
                if (speedKmh > maxSpeed) {
                    maxSpeed = speedKmh;
                }
                
                if(networkStatus == ATNetwork.STATUS_ROAMING) {
                    positionSendTime = dtroaming;
                } else {
                    positionSendTime = 0;
                }
                if(speedKmh < 50) {
                    positionSendTime += dtcity;
                } else {
                    positionSendTime += dtroad;
                }                
                if ((System.currentTimeMillis() - timestampPositions) > positionSendTime) {
                        timestampPositions = System.currentTimeMillis();
                        addPosition();
                }                
            } else {
                if ((System.currentTimeMillis() - timestampPositions) > dtstop) {
                    timestampPositions = System.currentTimeMillis();
                    addPosition();
                }
            }
        }

        private void addPosition() {
            if (location.getFix() > Location.FIX_INVALID) {
                p = new Position();
                p.latitude = location.getLatitude();
                p.longitude = location.getLongitude();
                p.timestamp = location.getUNIXTimestamp();
                p.speed = location.getSpeed();
                Position.addPosition(p);            
            }
        }
        Position p;
    }
    private static Location location;
    private static double distance = 0; //in meters
    private static double maxSpeed = 0; //in km/h    

    private static class GPIOEventImpl implements GPIOEvent {

        public void eventInput(int value) {
            //start journey
            //send status to jabber
            if (value == gpio_input_default_state) {
                stopJourney(); //stop journey
            } else {
                startJourney(); //start journey
            }
        }

        public void eventOutput(int value) {
            //imob event
            isImobilized = (value != gpio_output_default_state);
        }
    }
    private static boolean isImobilized;

    //--------------------------------------------------------------------------
    private static void startJourney() {
        isJourney = true;
        if (location.getFix() > Location.FIX_INVALID) {
            journey.startTimestamp = location.getUNIXTimestamp();
            journey.startLatitude = location.getLatitude();
            journey.startLongitude = location.getLongitude();
        }
    }
    private static boolean isJourney;

    private static void stopJourney() {
        isJourney = false;
        if (location.getFix() > Location.FIX_INVALID) {
            journey.finishTimestamp = location.getUNIXTimestamp();
            journey.finishLatitude = location.getLatitude();
            journey.finishLongitude = location.getLongitude();
            journey.distance = distance;
            journey.maxSpeed = maxSpeed;
        }
        distance = 0;
        maxSpeed = 0;
        //send journey                
        jel.sendMessage(journey.toString(), jabber_remote);                
        //send positions        
        jel.sendMessage(Position.getPositions(), jabber_remote);
    }
}