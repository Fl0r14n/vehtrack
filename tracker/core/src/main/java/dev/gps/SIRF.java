package dev.gps;

import io.COMConnection;
import io.COMProfile;
import io.Connection;
import io.ConnectionProfile;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import util.Hex;

/**
 * General SIRF based GPS modules
 */
public class SIRF implements GPSDevice {

    public SIRF(COMProfile comp) {
        comc = new COMConnection();
        this.comp = new COMProfile();
        this.comp.setCOMPort(comp.getCOMPort());
        this.comp.setBaudrate(COMProfile.BAUDRATE_9600); //default baudrate        
    }
    private COMProfile comp;

    public boolean init() {
        if (comc.open(comp)) {
            com_is = comc.getInputStream();
            com_os = comc.getOutputStream();
            synchronized (comc) {
                toSirf(COMProfile.BAUDRATE_115200);
                try {
                    Thread.sleep(100);
                } catch (Exception _) {
                }
                toNMEA();
                try {
                    Thread.sleep(100);
                } catch (Exception _) {
                }
            }
            comc.close();
        }
        comp.setBaudrate(COMProfile.BAUDRATE_115200);
        if (comc.open(comp)) {
            com_is = comc.getInputStream();
            com_os = comc.getOutputStream();
            synchronized (comc) {
                toSirf(COMProfile.BAUDRATE_115200);
                try {
                    Thread.sleep(100);
                } catch (Exception _) {
                }
                toNMEA();
                try {
                    Thread.sleep(100);
                } catch (Exception _) {
                }
            }
            return true;
        }
        return false;
    }
    private final COMConnection comc;
    private InputStream com_is;
    private OutputStream com_os;

    public Connection connect(ConnectionProfile cp) {
        return new Connection() {

            public boolean open(ConnectionProfile cp) {
                if (init()) {
                    is = com_is;
                    os = com_os;
                    return true;
                }
                return false;
            }

            public void close() {
                if (comc != null) {
                    comc.close(); //close COM Connection
                }
            }
        };
    }

    public void initializeNavigation(double X, double Y, double Z, int startMode) {
        synchronized (comc) {
            initialize(0, 0, 0, startMode);
        }
    }

    public void setPowerState(int powerState) {
        synchronized (comc) {
            toSirf(COMProfile.BAUDRATE_115200);
            try {
                Thread.sleep(100);
            } catch (Exception _) {
            }
            setPowerMode(this.powerState = powerState);
            try {
                Thread.sleep(100);
            } catch (Exception _) {
            }
            toNMEA();
            try {
                Thread.sleep(100);
            } catch (Exception _) {
            }
        }
    }
    private int powerState = MODE_CONTINUOUS;

    public int getPowerState() {
        boolean isPowerState;
        synchronized (comc) {
            StringBuffer buf = new StringBuffer();
            int ch;
            long now = System.currentTimeMillis();
            try {
                while ((System.currentTimeMillis() - now) < 2000) { //read for 2 sec               
                    if ((ch = com_is.read()) != -1) {
                        buf.append((char) ch);
                    }
                }
            } catch (Exception _) {
            }
            String str = buf.toString();
            isPowerState = (str.indexOf("OkToSend") > -1) || (str.indexOf("$PSRF150") > -1);
        }
        return isPowerState ? powerState : MODE_CONTINUOUS;
    }

    public void reset() {
        synchronized(comc) {
            initialize(0, 0, 0, START_CLEAR_MEMORY); //reset to factory defaults
        }
    }

    public boolean readDevice(ConnectionProfile cp) {
        return false;
    }

    public boolean writeDevice(ConnectionProfile cp) {
        return false;
    }

    //--------------------------------------------------------------------------
    private void toSirf(int baudrate) {
        StringBuffer buf = new StringBuffer();
        buf.append("$PSRF100,0,").append(baudrate).append(",8,1,0").append(checkSum(buf.toString()));
        try {
            //System.out.println(buf.toString());
            com_os.write(buf.toString().getBytes());
        } catch (Exception _) {
        }
    }

    //not all GPS devices support this function
    private void initialize(double X, double Y, double Z, int startMode) {
        //$PSRF101,-2686700,-4304200,3851624,96000,497260,921,12,3*1C
        StringBuffer buf = new StringBuffer();
        buf.append("$PSRF101,").append((long) X).append(",").append((long) Y).append(",").append((long) Y).append(",0,0,0,0,").append(startMode).append(checkSum(buf.toString()));
        try {
            //System.out.println(buf.toString());
            com_os.write(buf.toString().getBytes());
        } catch (Exception _) {
        }
    }

    private void toNMEA() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(startSequence);
            baos.write(new byte[]{
                        0x00, 0x18, //length
                        (byte) 0x81, //MessageID
                        0x01, //Mode (disable NMEA debug)
                        0x01, 0x01, //GGA sec and checksum
                        0x00, 0x01, //GLL sec and checksum
                        0x00, 0x01, //GSA sec and checksum
                        0x00, 0x01, //GSV sec and checksum
                        0x01, 0x01, //RMC sec and checksum
                        0x00, 0x01, //VTG sec and checksum
                        0x00, 0x01, //MSS sec and checksum
                        0x00, 0x01, //EPE sec and checksum
                        0x00, 0x01, //ZDA sec and checksum
                        0x00, 0x00, //unused
                        0x00, 0x00 //baudrate (keep the same)
                    });
            baos.write(checkSum(baos.toByteArray()));
            baos.write(endSequence);
            com_os.write(baos.toByteArray());
        } catch (Exception _) {
        }
    }
    private static final byte[] startSequence = {(byte) 0xA0, (byte) 0xA2};
    private static final byte[] endSequence = {(byte) 0xB0, (byte) 0xB3};

    public void setPowerMode(int mode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //Tricke-mode: interval=on-time/duty cycle 200ms/200% = 1sec
            //Push-to-Fix:
            baos.write(startSequence);
            baos.write(new byte[]{
                        0x00, 0x09, //length
                        (byte) 0x97, //message id
                        0x00, mode == MODE_PUSH_TO_FIX ? (byte) MODE_PUSH_TO_FIX : MODE_TRICKLE_POWER, //mode                            
                        mode == MODE_CONTINUOUS ? (byte) 0x03 : 0x00, mode == MODE_CONTINUOUS ? (byte) 0xE8 : (byte) 0xC8, //duty cycle *10%
                        0x00, 0x00, 0x00, (byte) 0xC8 //on time (ms)
                    });
            baos.write(checkSum(baos.toByteArray()));
            baos.write(endSequence);
            com_os.write(baos.toByteArray());
            baos.reset();
            //acquisition parameters
            baos.write(startSequence);
            baos.write(new byte[]{
                        0x00, 0x0F, //length
                        (byte) 0xA7, //message id
                        0x00, 0x00, 0x75, 0x30, //max off time
                        0x00, 0x01, (byte) 0xD4, (byte) 0xC0, //max search time
                        0x00, 0x00, 0x00, 0x3C, //push-to-fix cycle period
                        0x00, 0x00 //adaptive tricke-power enable
                    });
            baos.write(checkSum(baos.toByteArray()));
            baos.write(endSequence);
            com_os.write(baos.toByteArray());
        } catch (Exception _) {
        }
    }

    private String checkSum(String payload) {
        int cksum = 0;
        for (int i = 1; i < payload.length(); i++) {
            cksum ^= payload.charAt(i);
        }
        return "*" + Hex.toHexChar((cksum >> 4) & 0xF) + Hex.toHexChar(cksum & 0xF) + (char) 0x0D + (char) 0x0A;
    }

    private byte[] checkSum(byte[] payload) {
        int checksum = 0;
        for (int i = 4; i < payload.length; i++) {
            checksum += payload[i] & 0xFF;
        }
        return new byte[]{(byte) ((checksum >> 8) & 0x7F), (byte) (checksum & 0xFF)};
    }
}
