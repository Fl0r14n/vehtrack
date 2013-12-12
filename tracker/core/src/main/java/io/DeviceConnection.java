package io;

import dev.Device;

/**
 * Generic device connection
 * @author Florian Chis
 */
class DeviceConnection extends Connection {

    protected DeviceConnection(Device driver) {
        this.driver = driver;
    }
    private Device driver;

    public boolean open(ConnectionProfile cp) {
        if (driver != null) {
            c = driver.connect(cp);
            if (c != null && c.open(cp)) {
                is = c.getInputStream();
                os = c.getOutputStream();
                return true;
            }
        }
        return false;
    }
    private Connection c;

    public void close() {
        super.close();
        if (c != null) {
            c.close();
            c = null;
        }
    }
}
