package lib.jabber;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import util.Log;
import util.MD5;

/**
 * does GOOGLE-TOKEN-AUTH witch means gmail auth base from MGtalk client
 *
 * @author Florian Chis
 */
public class GoogleToken {

    /**
     * @param userName username
     * @param passwd password
     * @return Google TOKEN authentification
     * @throws java.lang.Exception
     */
    public String getGoogleToken(String userName, String passwd) throws Exception {
        String str = "Email=" + userName + "&Passwd=" + passwd
                + "&PersistentCookie=false&source=googletalk";
        try {
            Log.add2Log("Connecting to www.google.com", this.getClass().getName());
            c = (HttpConnection) Connector.open("https://www.google.com:443/accounts/ClientAuth?"
                    + str);
            dis = c.openDataInputStream();
            str = readStanza(dis);
            //Log.add2Log(str);
            //TC65 bug: close() method doesen't release the resources
            dis.close();
            dis = null;
            c.close();
            c = null;
            buf = null;
            System.gc();
            Thread.sleep(3000); //time 2 clean the waste https connections take alot of heap space
            //the retuned str contains 2 lines SID=...\nLSID=...
            buf = new StringBuffer();
            if (str.startsWith("SID=")) {
                buf.append(str.substring(0, str.indexOf("LSID") - 1));
                buf.append('&');
                buf.append(str.substring(str.indexOf("LSID"), str.length() - 1));
                buf.append("&service=mail&Session=true");
                //Log.add2Log(buf.toString());
                Log.add2Log("Next www.google.com connection", this.getClass().getName());
                //next gmail connection as follows
                c = (HttpConnection) Connector.open("https://www.google.com:443/accounts/IssueAuthToken?"
                        + buf.toString());
                buf = null;                
                dis = c.openInputStream();
                str = readStanza(dis);
                buf = null;
                dis.close();
                c.close();
                dis = null;
                c = null;
                System.gc();
                //processing response
                String token = MD5.toBase64(("\0" + userName + "\0" + str).getBytes());
                return token;
            } else {
                throw new Exception("Invalid response");
            }
        } catch (Exception ex) {
            if (dis != null) {
                dis.close();
            }
            if (c != null) {
                c.close();
            }
            c = null;
            dis = null;
            buf = null;
            System.gc();
            throw new Exception(this.getClass().getName() + ex.getMessage());
        }
    }
    private HttpConnection c = null;
    private InputStream dis = null;

    private String readStanza(InputStream dis) {
        String stanza = "";
        do {
            stanza = getStream(dis);
        } while (stanza.equals(""));
        return stanza;
    }
    StringBuffer buf;

    private String getStream(InputStream is) {
        buf = new StringBuffer();
        try {
            while (is.available() > 0) {
                buf.append((char) is.read());
            }
        } catch (IOException e) {
            Log.add2Log(e.getMessage(), this.getClass().getName());
        }
        return buf.toString();
    }
}
