package dev;

import io.Connection;
import io.ConnectionProfile;

/**
 * Describes a generic device interface
 */
public interface Device {

    /**
     * Init device. To be called in Device impl constructor if needed
     * @return true if successful
     */
    public boolean init();

    /**
     * Read the contents of the device.
     * @param cp connection profile where device config is to be set
     * @return true if the read process was successful
     */
    public boolean readDevice(ConnectionProfile cp);

    /**
     * Write preferences to the device
     * @param cp connection profile to write to device
     * @return true if write process was successful
     */
    public boolean writeDevice(ConnectionProfile cp);

    /**
     * Connect to the device
     * @param cp connection profile
     * @return current Connection object
     */
    public Connection connect(ConnectionProfile cp);

    /**
     * Change power state of the device
     * @param powerState
     */
    public void setPowerState(int powerState);

    /**
     * @return curent power state of the device
     */
    public int getPowerState();

    /**
     * Reset device
     */
    public void reset();
}
