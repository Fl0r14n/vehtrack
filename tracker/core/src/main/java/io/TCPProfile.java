package io;

/**
 * Profile class for TCPConnection
 * @author Florian Chis
 */
public class TCPProfile implements ConnectionProfile {

    /**
     * Get connection profile
     * @return connection profile string
     */
    public String getProfile() {
        if (ssl) {
            return profile("ssl");
        }
        return profile("socket");
    }

    protected String profile(String type) {
        if (!optional_params) {
            return type + "://" + host + ":" + port;
        }
        return type + "://" + host + ":" + port + ";bearer_type=" + bearer + ";access_point=" + apn + ";username=" + username + ";password=" + password;
    }

    /**
     * Set host. Both hostname or ip accepted
     * @param host host
     */
    public void setHost(String host) {
        this.host = host;
    }
    protected String host = "localhost";

    /**
     * Get host
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the TCP port (ex. http port = 80)
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }
    protected int port = 80;

    /**
     * Get port
     * @return TCP port
     */
    public int getPort() {
        return port;
    }

    /**
     * Activate usage of exta paramaters such as apn, username, password, dns
     * @param choice
     */
    public void useOptionalParameters(boolean choice) {
        optional_params = choice;
    }
    private boolean optional_params = false;

    /**
     * Get if this profile has optional parameters
     * @return true if optional parameters are active
     */
    public boolean isOptionalParameters() {
        return optional_params;
    }
    public static final String BEARER_GPRS = "GPRS";
    public static final String BEARER_CSD = "CSD";

    /**
     * Set bearer type. Posible values are GPRS or CSD
     * @param bearer
     */
    public void setBearer(String bearer) {
        this.bearer = bearer;
    }
    private String bearer = BEARER_GPRS;

    /**
     * Get bearer
     * @return bearer
     */
    public String getBearer() {
        return bearer;
    }

    /**
     * Set access point name
     * @param apn access point name
     */
    public void setAccesPointName(String apn) {
        this.apn = apn;
    }
    private String apn;

    /**
     * Get Access Point (APN)
     * @return access point name
     */
    public String getAccesPointName() {
        return apn;
    }

    /**
     * Set acces point username
     * @param username
     */
    public void setAccesPointUsername(String username) {
        this.username = username;
    }
    private String username;

    /**
     * Get access point username
     * @return username
     */
    public String getAccesPointUsername() {
        return username;
    }

    /**
     * Set access point password
     * @param password
     */
    public void setAccesPointPassword(String password) {
        this.password = password;
    }
    private String password;

    /**
     * Get access point password
     * @return password
     */
    public String getAccesPointPassword() {
        return password;
    }

    /**
     * Enable SSL connection
     * @param choice
     */
    public void setSSL(boolean choice) {
        ssl = choice;
    }
    private boolean ssl;

    /**
     * Get if SSL is activated
     * @return true if connection is SSL
     */
    public boolean isSSL() {
        return ssl;
    }
}
