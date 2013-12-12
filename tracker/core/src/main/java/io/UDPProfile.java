package io;

/**
 * Profile class for UDPConnection
 * @author Florian Chis
 */
public class UDPProfile extends TCPProfile {

    /**
     * Get connection profile
     * @return connection profile string
     */
    public String getProfile() {
        return profile("datagram");
    }
}
