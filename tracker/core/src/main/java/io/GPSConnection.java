package io;

import dev.gps.GPSDevice;

/**
 * Class that works with external GPS devices
 */
public class GPSConnection extends DeviceConnection {
    
    /**
     * Constructor
     * @param driver GPS driver implementation
     */
    public GPSConnection(GPSDevice driver) {
        super(driver);
    }

    /**
     * Open a GPS NMEA based connection
     * @param cp GPS profile
     * @return true if success
     */
    public boolean open(ConnectionProfile cp) {
        if (cp instanceof GPSProfile) {
            GPSProfile gpsp = (GPSProfile) cp;
            return super.open(gpsp);
        }
        return false;
    }        
}
