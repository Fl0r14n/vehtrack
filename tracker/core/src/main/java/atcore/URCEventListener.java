package atcore;

import java.util.Vector;

/**
 * Terminal's event URC codes
 * @author Florian Chis
 */
public abstract class URCEventListener {

    public static final String AT_RING = "RING";
    //configuration
    public static final String AT_CFUN = "^SYSSTART";
    public static final String AT_SMSO = "^SHUTDOWN";
    public static final String AT_SCFG = "^SCFG:";
    //status control commands
    public static final String AT_CMER = "+CIEV:";
    public static final String AT_SIND = "+CIEV:";
    //call related commands
    public static final String AT_SLCC = "^SLCC:";
    public static final String AT_CRC = "+CRING:";
    //network service commands
    public static final String AT_CREG = "+CREG:";
    public static final String AT_SALS = "^SALS:";
    //supplementary service commands
    public static final String AT_SACM = "+CCCM:";
    public static final String AT_CCWA = "+CCCWA:";
    public static final String AT_CLIP = "+CLIP:";
    public static final String AT_COLP = "+COLP:";
    public static final String AT_CSSN_1 = "+CSSI:";
    public static final String AT_CLIP_2 = "+CSSU:";
    public static final String AT_CUSD = "+CUSD:";
    //internet service commands
    public static final String AT_SISR = "^SISR:";
    public static final String AT_SISW = "^SISW:";
    public static final String SIS = "^SIS:";
    //gprs commands
    public static final String AT_CGREG = "+CGREG:";
    //sms commands
    public static final String AT_CNMI_CMTI = "+CMTI:";
    public static final String AT_CNMI_CMT = "+CMT:";
    public static final String AT_CNMI_CBM = "+CBM:";
    public static final String AT_CNMI_CDS = "+CDS:";
    public static final String AT_CLIP_CDSI = "+CDSI:";
    public static final String AT_SMGO = "^SMGO:";
    //sim commands
    public static final String AT_SCKS = "^SCKS:";
    public static final String AT_SSET = "^SSIM:";
    //remote sim access
    public static final String AT_SRSA = "^SRSA:";
    public static final String AT_SRSM = "^SRSM:";
    //sim application toolkit
    public static final String AT_SSTN = "^SSTN:";
    //hardware related commands
    public static final String AT_CALA = "+CALA:";
    public static final String AT_SBC = "^SBC:";
    public static final String AT_SCTM_A = "^SCTM_A:";
    public static final String AT_SCTM_B = "^SCTM_B:";
    public static final String AT_SRACD = "^SRADC:";
    //GPIO commands
    public static final String AT_SCPOL = "^SCPOL:";
    public static final String AT_SCCNT = "^SCCNT:";
    public static final String AT_SSCNT = "^SSCNT:";

    /**
     * Default Constructor
     */
    public URCEventListener() {
        event_headers = new Vector(1);
    }

    /**
     * Constructor
     * @param event_type Event type to listen to. Use class constants
     */
    public URCEventListener(String event_type) {
        event_headers = new Vector(1);
        event_headers.addElement(event_type);
    }
    protected Vector event_headers;

    /**
     * Register for events
     * @param event_type Event for which notification is received
     */
    public void addEventType(String event_type) {
        event_headers.addElement(event_type);
    }

    public abstract void event(String event); //here the event will be broadcast
}
