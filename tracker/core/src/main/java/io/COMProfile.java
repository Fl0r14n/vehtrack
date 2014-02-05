package io;

/**
 * Descibes a serial com connection and parameters
 */
public class COMProfile implements ConnectionProfile {

    public static final int BAUDRATE_300 = 300;
    public static final int BAUDRATE_600 = 600;
    public static final int BAUDRATE_1200 = 1200;
    public static final int BAUDRATE_2400 = 2400;
    public static final int BAUDRATE_4800 = 4800;
    public static final int BAUDRATE_9600 = 9600;
    public static final int BAUDRATE_14400 = 14400;
    public static final int BAUDRATE_19200 = 19200;
    public static final int BAUDRATE_28800 = 28800;
    public static final int BAUDRATE_38400 = 38400;
    public static final int BAUDRATE_57600 = 57600;
    public static final int BAUDRATE_115200 = 115200;
    public static final int BAUDRATE_230400 = 230400;
    public static final int BAUDRATE_460800 = 460800;
    public static final int DATABITS_7 = 7;
    public static final int DATABITS_8 = 8;
    public static final int FLOWCONTROL_NONE = 0;
    public static final int FLOWCONTROL_HARDWARE = 3;
    public static final int PARITY_NONE = 0;
    public static final int PARITY_ODD = 1;
    public static final int PARITY_EVEN = 2;
    public static final int STOPBITS_1 = 1;
    private int baudrate = BAUDRATE_115200, databits = DATABITS_8, stopbits = STOPBITS_1;
    private String comm = "COM1";
    private String parity = "none", auto_cts = "off", auto_rts = "off";

    /**
     * Get connection profile
     * @return this profile
     */
    public String getProfile() {
        return "comm:" + comm + ";baudrate=" + baudrate + ";bitsperchar=" + databits + ";parity=" + parity + ";blocking=on;autorts=" + auto_rts + ";autocts=" + auto_cts;
    }

    /**
     * Set COM port
     * @param asc_port serial com port
     */
    public void setCOMPort(String asc_port) {
        this.comm = asc_port;
    }

    /**
     * Get COM port
     * @return com port
     */
    public String getCOMPort() {
        return comm;
    }

    /**
     * Set baudrate. Use class constants
     * @param baudrate baudrate
     */
    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    /**
     * Get baudrate
     * @return baudrate
     */
    public int getBaudrate() {
        return baudrate;
    }

    /**
     * Set databits. Use class constants
     * @param databits databits
     */
    public void setDatabits(int databits) {
        this.databits = databits;
    }

    /**
     * Get databits
     * @return databits
     */
    public int getDatabits() {
        return databits;
    }

    /**
     * Set parity. Use class constants
     * @param parity parity
     */
    public void setParity(int parity) {
        switch (parity) {
            case PARITY_NONE: {
                this.parity = "none";
                break;
            }
            case PARITY_ODD: {
                this.parity = "odd";
                break;
            }
            case PARITY_EVEN: {
                this.parity = "even";
                break;
            }
        }
    }

    /**
     * Get parity
     * @return parity
     */
    public int getParity() {
        if (parity.equals("odd")) {
            return PARITY_ODD;
        }
        if (parity.equals("even")) {
            return PARITY_EVEN;
        }
        return PARITY_NONE;
    }

    /**
     * Set total number of stop bits. Use class constants
     * @param stopbits stopbits
     */
    public void setStopBits(int stopbits) {
        this.stopbits = stopbits;
    }

    /**
     * Get stop bits
     * @return stopbits
     */
    public int getStopBits() {
        return stopbits;
    }

    /**
     * Set flow control. Use class constants
     * @param flowcontrol flow control
     */
    public void setFlowControl(int flowcontrol) {
        if (flowcontrol == FLOWCONTROL_HARDWARE) {
            auto_cts = "on";
            auto_rts = "on";
        } else {
            auto_cts = "off";
            auto_rts = "off";
        }
    }

    /**
     * Get flow control
     * @return flowcontrol
     */
    public int getFlowControl() {
        return auto_cts.equals("on") ? 3 : 0;
    }
}
