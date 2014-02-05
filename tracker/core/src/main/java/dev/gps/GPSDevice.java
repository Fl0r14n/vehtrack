package dev.gps;

import dev.Device;

/**
 * Interface describing a GPS device
 */
public interface GPSDevice extends Device {
    
    public static final int MODE_TRICKLE_POWER = 0;
    public static final int MODE_PUSH_TO_FIX = 1;
    public static final int MODE_CONTINUOUS = 2;
    
    public static final int START_HOT = 1; //All data valid
    public static final int START_WARM = 2; //Ephemeris cleared
    public static final int START_WARM_WITH_INITIALIZATION = 3; //Ephemeris cleared, initialization data loaded
    public static final int START_COLD = 4; //Clears all data in memory
    public static final int START_CLEAR_MEMORY = 8; //Clears all data in memory and resets the receiver back to factory defaults
    
    public void initializeNavigation(double X, double Y, double Z, int startMode);
}
