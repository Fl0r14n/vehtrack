package lib.jabber;

/**
 * Jabber Watchdog interface
 * @author Florian Chis
 */
public interface JabberWatchdogListener {

    /**
     * jabber client is connecting
     */
    public void connecting();

    /**
     * jabber client is online
     */
    public void online();

    /**
     * can't connect no more. bad jabber profile or server down?
     */
    public void connectionNotPossible();

    /**
     * exception occured in the process
     * @param ex the Exception
     */
    public void exceptionOcured(Exception ex);

    /**
     * error occured in the process
     * @param er the Error
     */
    public void errorOccured(Error er);
}
