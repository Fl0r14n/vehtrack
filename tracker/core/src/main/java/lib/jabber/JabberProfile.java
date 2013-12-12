package lib.jabber;

import io.TCPProfile;

/**
 * Profile class for the JabberClient
 * @author Florian Chis
 */
public class JabberProfile extends TCPProfile {

    /**
     * @return Jabber resource
     */
    public String getJabberResource() {
        return jabberResource;
    }

    /**
     * Set jabber resource. A jabber resouce identifies clients using same account
     * @param jabberResource label for resource
     */
    public void setJabberResource(String jabberResource) {
        this.jabberResource = jabberResource;
    }
    protected String jabberResource;

    /**
     * @return Jabber password
     */
    public String getJabberPassword() {
        return jabberPassword;
    }

    /**
     * Set Jabber password
     * @param jabberPassword password
     */
    public void setJabberPassword(String jabberPassword) {
        this.jabberPassword = jabberPassword;
    }
    protected String jabberPassword;

    /**
     * @return Jabber user
     */
    public String getJabberUser() {
        return jabberUser;
    }

    /**
     * set Jabber user. Full account name is used <br>
     * Must be like johndoe@vietnam.war
     * @param jabberUser jabber user
     */
    public void setJabberUser(String jabberUser) {
        this.jabberUser = jabberUser;
    }
    protected String jabberUser;

    public String toString() {
        return jabberUser + "/" + jabberResource + ":" + jabberPassword + "[" + super.getProfile() + "]";
    }

    /**
     * Enables debug
     * @param choice choice. If true debug is enabled
     */
    public void setDebugXMPP(boolean choice) {
        debug = choice;
    }
    protected boolean debug = false;

    /**
     * Try's to create new jabber account on server
     */
    public void createNewAccount() {
        new_account = true;
    }
    protected boolean new_account = false;
}
