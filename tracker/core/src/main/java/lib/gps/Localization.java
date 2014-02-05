package lib.gps;

import io.TCPConnection;
import io.TCPProfile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Vector;
import util.Hex;
import util.Log;
import util.MD5;

/**
 * Location based system. Also NTRIP function is implemented
 */
public class Localization {

    private Localization() {
    }
    private static Localization instance;

    public static void setStreams(InputStream is, OutputStream os) {
        gps_is = is;
        gps_os = os;
    }
    private static InputStream gps_is;
    private static OutputStream gps_os;

    /**
     * @return Localization instance
     */
    public static synchronized Localization getInstance() {
        if (instance == null) {
            instance = new Localization();
                    gps_active = true;
                    l = new Location();
                    geofences = new Vector(1);
                    final Calendar cal = Calendar.getInstance();
                    //start reading
                    new Thread(new Runnable() {
                        //6 for's / cicle (1read+1chk+1parse)x2nmea sentences parsed

                        public void run() {
                            while (gps_active) {
                                try {
                                    if (gps_is.available() > 512) { //avoid deadlock's
                                        for (int i = 0; i < lock_threshold; i++) {
                                            gps_is.read();
                                        }
                                    }
                                    while (gps_is.available() > 0) {
                                        ch = gps_is.read();
                                        if (ch != '\n') {
                                            buff.append((char) ch);
                                        } else {
                                            stream = buff.toString();
                                            if ((pos = stream.indexOf("$GPGGA")) >= 0) { //we have a gga sentence
                                                stream = stream.substring(pos, stream.indexOf(0x0D));
                                                gga = stream;
                                                //System.out.println(stream); //TODO erase
                                                pos = stream.indexOf('*');
                                                if (checkSum(1, pos, stream).equals(stream.substring(pos + 1))) { //sentence is ok
                                                    //send gga to NTRIP server to acquire DGPS correntions
                                                    if (ntrip_active && nmea_interval > 0 && l.fix > 0) {
                                                        if (gga_counter >= nmea_interval) {
                                                            gga_counter = 0;
                                                            try {
                                                                tcp_os.write((stream + "\r\n").getBytes());
                                                            } catch (Exception e) {
                                                                //happens that ntrip caster closes the connection;
                                                                Log.add2Log(e.getMessage(), this.getClass().getName());
                                                                ntrip_active = false;
                                                            }
                                                        } else {
                                                            gga_counter++;
                                                        }
                                                    }
                                                    comma = 0;
                                                    for (int i = 1; i < pos; i++) { //parse
                                                        if ((ch = stream.charAt(i)) != ',') {
                                                            buff.append((char) ch);
                                                        } else {
                                                            switch (comma) {
                                                                case 6: { //fix
                                                                    try {
                                                                        l.fix = Integer.parseInt(buff.toString());
                                                                    } catch (Exception _) {
                                                                        l.fix = -1;
                                                                    }
                                                                    break;
                                                                }
                                                                case 7: { //satellites
                                                                    try {
                                                                        l.satellites = Integer.parseInt(buff.toString());
                                                                    } catch (Exception _) {
                                                                        l.satellites = -1;
                                                                    }
                                                                    break;
                                                                }
                                                                case 8: { //hdop
                                                                    try {
                                                                        l.hdop = Float.parseFloat(buff.toString());
                                                                    } catch (Exception _) {
                                                                        l.hdop = Float.NaN;
                                                                    }
                                                                    break;
                                                                }
                                                                case 9: { //altitude
                                                                    try {
                                                                        l.altitude = Double.parseDouble(buff.toString());
                                                                    } catch (Exception e) {
                                                                        l.altitude = Double.NaN;
                                                                    }
                                                                    break;
                                                                }
                                                                case 11: { //geo sep
                                                                    try {
                                                                        l.separation = Float.parseFloat(buff.toString());
                                                                    } catch (Exception _) {
                                                                        l.separation = Float.NaN;
                                                                    }
                                                                    break;
                                                                }
                                                                default:
                                                                    break;
                                                            }
                                                            comma++;
                                                            buff = new StringBuffer();
                                                        }
                                                    }
                                                }
                                            }
                                            if ((pos = stream.indexOf("$GPRMC")) >= 0) { //we have a rmc sentence
                                                stream = stream.substring(pos, stream.indexOf(0x0D));
                                                rmc = stream;
                                                //System.out.println(stream); //TODO erase
                                                pos = stream.indexOf('*');
                                                buff = new StringBuffer();
                                                if (checkSum(1, pos, stream).equals(stream.substring(pos + 1))) {//chk ok
                                                    comma = 0;
                                                    for (int i = 1; i < pos; i++) { //parse
                                                        if ((ch = stream.charAt(i)) != ',') {
                                                            buff.append((char) ch);
                                                        } else {
                                                            switch (comma) {
                                                                case 1: { //time utc
                                                                    String s = buff.toString();
                                                                    if (s.length() >= 6) {
                                                                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
                                                                        cal.set(Calendar.MINUTE, Integer.parseInt(s.substring(2, 4)));
                                                                        cal.set(Calendar.SECOND, Integer.parseInt(s.substring(4, 6)));
                                                                    }
                                                                    break;
                                                                }
                                                                case 3: { //latitude
                                                                    l.latitude = normalizeLocation(buff.toString());
                                                                    break;
                                                                }
                                                                case 4: { //latitude hemisphere
                                                                    if (buff.toString().equals("S")) {
                                                                        l.latitude *= -1;
                                                                    }
                                                                    break;
                                                                }
                                                                case 5: { //longitude
                                                                    l.longitude = normalizeLocation(buff.toString());
                                                                    break;
                                                                }
                                                                case 6: { //longitude hemisphere
                                                                    if (buff.toString().equals("W")) {
                                                                        l.longitude *= -1;
                                                                    }
                                                                    break;
                                                                }
                                                                case 7: { //speed
                                                                    try {
                                                                        l.speed = Double.parseDouble(buff.toString()) * 1.852;
                                                                    } catch (Exception _) {
                                                                        l.speed = Double.NaN;
                                                                    }
                                                                    break;
                                                                }
                                                                case 8: { //azimuth
                                                                    try {
                                                                        l.azimuth = Double.parseDouble(buff.toString());
                                                                    } catch (Exception _) {
                                                                        l.azimuth = Double.NaN;
                                                                    }
                                                                    break;
                                                                }
                                                                case 9: { //date utc
                                                                    String s = buff.toString();
                                                                    if (s.length() >= 6) {
                                                                        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, 2)));
                                                                        cal.set(Calendar.MONTH, Integer.parseInt(s.substring(2, 4)));
                                                                        cal.set(Calendar.YEAR, Integer.parseInt("20" + s.substring(2, 4)));
                                                                        l.utc_timestamp = cal.getTime().getTime();
                                                                    }
                                                                    break;
                                                                }
                                                                default:
                                                                    break;
                                                            }
                                                            comma++;
                                                            buff = new StringBuffer();
                                                        }
                                                    }
                                                    //throw LocationListener event
                                                    if (ll != null) {
                                                        ll.locationUpdated(l);
                                                    }
                                                    //finally do the geofence stuff if proximity is enabled
                                                    if (pl != null) { //listener is active
                                                        for (int i = 0; i < geofences.size(); i++) { //all geofences
                                                            p = (Location) geofences.elementAt(i);
                                                            //calculate euclidian distance
                                                            dlt = (l.latitude - p.latitude) * 111.023;
                                                            dlg = (l.longitude - p.longitude) * 96.448;
                                                            if (Math.abs(dlt) < 0.005 && Math.abs(dlg) < 0.005) {//avoid denormalized numbers
                                                                radius = 0.005;
                                                            } else {
                                                                radius = Math.sqrt((dlg * dlg) + (dlt * dlt)); //transformed in km
                                                            }
                                                            //TODO 2'nd degree numerical filtering to avoid gps drift?
                                                            if (radius < p.radius) { //in event
                                                                if (p.status_geofence == 0) {
                                                                    p.status_geofence = 1;
                                                                    pl.proximityEvent(l, ProximityListener.ZONE_ENTER); //trow in event
                                                                } else if (p.status_geofence == -1) {
                                                                    p.status_geofence = 1;
                                                                }
                                                            } else { //out event
                                                                if (p.status_geofence == 1) {
                                                                    p.status_geofence = 0;
                                                                    pl.proximityEvent(l, ProximityListener.ZONE_ESCAPE); //throw out event
                                                                } else if (p.status_geofence == -1) {
                                                                    p.status_geofence = 0;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            stream = null;
                                            buff = new StringBuffer();
                                        }
                                    }
                                    Thread.sleep(200);
                                } catch (Exception e) {
                                    Log.add2Log(e.getMessage(), this.getClass().getName());
                                }
                            }
                        }
                        int gga_counter; //count's gga sentences
                        StringBuffer buff = new StringBuffer(); //parsing buffer
                        int ch, pos, comma;
                        String stream; //the NMEA sentence
                        //geofence variables
                        Location p;
                        double dlg, dlt, radius;
                    }).start();
                }
        return instance;
    }
    private static final int lock_threshold = 512; //avoid program lock by dumping bytes
    private static boolean gps_active;

    /**
     * Get current location. <br><b>OBS:</b> Not synchronized.
     * It is better to implement setLocationListener
     * @return
     */
    public Location getLocation() {
        return l;
    }
    private static Location l;

    /**
     * close connections, release the sources
     */
    public void close() {
        closeNTRIP(); //if ntrip client is active
        gps_active = false;
        l = null;
        geofences = null;
        instance = null;
    }

    public String getRMC() {
        return rmc;
    }
    private static String rmc="";
    
    public String getGGA() {
        return gga;
    }
    private static String gga="";

    //do ggmm.mmmm to gg.gggggg
    private static double normalizeLocation(String value) {
        try {
            location = Double.parseDouble(value) / 100;
        } catch (Exception e) {
            return Double.NaN;
        }
        arg = location % 1;
        return (location - arg) + (arg / 60 * 100);
    }
    private static double location,  arg;

    private static String checkSum(int off, int len, String stream) {
        int cksum = 0;
        for (int i = off; i < len; i++) {
            cksum ^= stream.charAt(i);
        }
        return "" + Hex.toHexChar((cksum >> 4) & 0xF) + Hex.toHexChar(cksum & 0xF);
    }

    /**
     * Add NTRIP DGPS corrections.
     * @param ntrip NTRIP protocol client
     */
    public void openNTRIP(final NTRIPClient ntrip) {
        //does ntrip mountpoint require nmea gga?
        nmea_interval = ntrip.nmea_interval;
        //open TCP connection with ntrip caster
        tcpc = new TCPConnection();
        TCPProfile tcpp = new TCPProfile();
        tcpp.setHost(ntrip.host);
        tcpp.setPort(ntrip.port);
        if (tcpc.open(tcpp)) {
            tcp_is = tcpc.getInputStream();
            tcp_os = tcpc.getOutputStream();
            String auth = MD5.toBase64((ntrip.user + ":" + ntrip.paswd).getBytes());
            String msg = "GET /" + ntrip.mountpoint + " HTTP/1.1\r\n";
            msg += "User-Agent: NTRIP monitorizareflota.ro\r\n";
            msg += "Authorization: Basic " + auth + "\r\n";
            msg += "Accept: */*\r\nConnection: close\r\n";
            msg += "\r\n";
            try {
                tcp_os.write(msg.getBytes());
            } catch (IOException ex) {
                Log.add2Log(ex.getMessage(), this.getClass().getName());
                closeNTRIP();
                return; //no luck :( maybe next time
            }
            ntrip_active = true;
            new Thread(new Runnable() {

                ByteArrayOutputStream baos = new ByteArrayOutputStream(); //buffer

                public void run() {
                    while (ntrip_active) {
                        try {
                            while (tcp_is.available() > 0) {
                                baos.write(tcp_is.read());
                            }
                            if (baos.size() > 0 && gps_os != null) {
                                gps_os.write(baos.toByteArray());
                                gps_os.flush();
                            }
                            baos.reset();
                            Thread.sleep(30);
                        } catch (Exception e) {
                            Log.add2Log(e.getMessage(), this.getClass().getName());
                            closeNTRIP();
                        }
                    }
                    closeNTRIP();
                }
            }).start();
        }
    }
    private static boolean ntrip_active = false;
    private static int nmea_interval;
    private static TCPConnection tcpc;
    private static InputStream tcp_is;
    private static OutputStream tcp_os;

    /**
     * closes TCP connection with NTRIP server
     */
    public void closeNTRIP() {
        ntrip_active = false;
        Localization.tcpc.close();
    }

    /**
     * @return true if NTRIP connection is active
     */
    public boolean isNTRIP() {
        return ntrip_active;
    }

    /**
     * Set Location Listener
     * @param ll LocationListener implementation
     */
    public void setLocationListener(LocationListener ll) {
        Localization.ll = ll;
    }
    private static LocationListener ll;

    /**
     * Set Proximity Listener
     * @param pl ProximityListener implementation
     */
    public void setProximityListener(ProximityListener pl) {
        Localization.pl = pl;
    }
    private static ProximityListener pl;

    /**
     * Add a Location to the Proximity Listener
     * @param l
     */
    public void addProximityLocation(Location l) {
        geofences.addElement(l);
    }
    private static Vector geofences;

    /**
     * Remove all Locations used by Proximity Listener
     */
    public void clearProximityLocations() {
        geofences.removeAllElements();
    }

    /**
     * Interface to be implemented when listening for position changes
     * @author Chis Florian
     */
    public static interface LocationListener {
        /**
         * Event thrown when current position changed
         * @param l
         */
        public void locationUpdated(Location l);
    }

    /**
     * Interface to be implemented when listening for geofence events
     * @author Chis Florian
     */
    public static interface ProximityListener {

        public static final int ZONE_ENTER = 1;
        public static final int ZONE_ESCAPE = 2;

        /**
         * Event thrown when enter or escape a zone is present
         * @param l
         * @param type
         */
        public void proximityEvent(Location l, int type);
    }

    /**
     * NTRIP Client profile class
     * @author Chis Florian
     */
    public static class NTRIPClient {
        /**
         * @return NTRIP host server
         */
        public String getHost() {
            return host;
        }

        /**
         * Set NTRIP host server
         * @param host host
         */
        public void setHost(String host) {
            this.host = host;
        }

        /**
         * @return Mountpoint used to get DGPS corrections
         */
        public String getMountpoint() {
            return mountpoint;
        }

        /**
         * Set DGPS corrections source
         * @param mountpoint mountpoint
         */
        public void setMountpoint(String mountpoint) {
            this.mountpoint = mountpoint;
        }

        /**
         * @return Password user for NTRIP authentification
         */
        public String getPaswd() {
            return paswd;
        }

        /**
         * Set password used for NTRIP autentification
         * @param paswd password
         */
        public void setPaswd(String paswd) {
            this.paswd = paswd;
        }

        /**
         * @return TCP port
         */
        public int getPort() {
            return port;
        }

        /**
         * Get TCP port
         * @param port port
         */
        public void setPort(int port) {
            this.port = port;
        }

        /**
         * @return Time interval in seconds in which NMEA GGA sentence is sent to the NTRIP server
         */
        public int getNMEASendInterval() {
            return nmea_interval;
        }

        /**
         * Set interval in seconds to pass NMEA GGA sentence to NTRIP server
         * @param seconds seconds
         */
        public void sendNMEA(int seconds) {
            nmea_interval = seconds;
        }

        /**
         * @return Username used for NTRIP authentification
         */
        public String getUser() {
            return user;
        }

        /**
         * Set username used for NTRIP authentification
         * @param user user
         */
        public void setUser(String user) {
            this.user = user;
        }
        protected String host,  user,  paswd,  mountpoint;
        protected int nmea_interval = 0;
        protected int port;
    }
}
