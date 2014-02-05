package lib.jabber;

import io.Connection;
import io.TCPConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import lib.xml.XMLNode;
import lib.xml.XMLTransportImpl;
import util.Base64;
import util.Log;
import util.MD5;

/**
 * A Jabber client implementation. Plain, MD5 and GOOGLE_TOKEN (for gtalk) auth supported
 */
public class JabberClient implements Runnable {

    /**
     * Set a watchdog event observer.
     * @param jwl JabberWatchdgListener implementation
     */
    public void setWatchdogEventListener(JabberWatchdogListener jwl) {
        this.jwl = jwl;
    }
    private JabberWatchdogListener jwl;

    /**
     * Remove watchdog event observer
     */
    public void removeWatchdogEventListener() {
        jwl = null;
    }

    /**
     * add a Jabber event observer
     * @param jel JabberEventListener implementation
     */
    public void addJabberEventListener(JabberEventListener jel) {
        evl.addElement(jel);
    }
    private Vector evl;

    /**
     * Remove specified Jabber event observer
     * @param jel jabber event object
     */
    public void removeJabberEventListener(JabberEventListener jel) {
        evl.removeElement(jel);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param jp jabber profile
     */
    public JabberClient(JabberProfile jp) {
        this.jp = jp;
        init();
    }

    private void init() {
        evl = new Vector(1);
        new Thread(this).start();
    }
    private JabberProfile jp;
    private Connection c;
    private InputStream is;
    private OutputStream os;
    private XMLTransportImpl xmlt;
    private XMLNode x;

    /**
     * Close connection
     */
    public void close() {
        active = false;
        c.close();
        xmlt = null;
        evl.removeAllElements();
        evl = null;
        removeWatchdogEventListener();
    }

    public void run() {
        long retry = 0;
        active = true;
        c = new TCPConnection();
        while (active) {
            try {
                if (jwl != null) { //connection event
                    jwl.connecting();
                }
                if (c.open(jp)) {//connect
                    xmlt = new XMLTransportImpl(is = c.getInputStream(), os = c.getOutputStream());
                    xmlt.setDebug(jp.debug);
                } else {
                    throw new Exception("Jabber: Exception opening TCP connection");
                }
                JAuth();
                retry = 0;
                for (int i = 0; i < evl.size(); i++) { //set xml transport in listener
                    JabberEventListener jel = (JabberEventListener) evl.elementAt(i);
                    jel.eventXMLTransport(xmlt);
                }
                if (jwl != null) {
                    jwl.online();
                }
                reconnect = false;
                while (active) {
                    if (reconnect) {
                        throw new Exception("Jabber: Forced reconnect received");
                    }
                    x = xmlt.receiveXML();
                    if (x.getName().equals("")) {
                        Thread.sleep(Te);
                    } else if (x.getName().equals("message") && !x.getAttr("type").equals("error")) {
                        //if(x.getChild("body").getValue().equals("error")) throw new Exception("Jabber: Reconnect test");
                        //Log.add2Log("Jabber: Message received", this.getClass());
                        String message = normalizeMessage(x.getChild("body").getValue());
                        for (int i = 0; i < evl.size(); i++) {
                            JabberEventListener jel = (JabberEventListener) evl.elementAt(i);
                            jel.eventMessage(message, x.getAttr("from"));
                        }
                    } else if (x.getName().equals("iq")) {
                        //Log.add2Log("Jabber: Iq received", this.getClass());
                        for (int i = 0; i < evl.size(); i++) {
                            JabberEventListener jel = (JabberEventListener) evl.elementAt(i);
                            jel.eventIq(x);
                        }
                    } else if (x.getName().equals("presence")) {
                        //Log.add2Log("Jabber: Presence received", this.getClass());
                        for (int i = 0; i < evl.size(); i++) {
                            JabberEventListener jel = (JabberEventListener) evl.elementAt(i);
                            jel.eventPresence(x);
                        }
                    }
                }
            } catch (Exception ex) {
                if (jwl != null) {
                    jwl.exceptionOcured(ex);
                }
            } catch (Error er) {
                if (jwl != null) {
                    jwl.errorOccured(er);
                }
            }
            try {
                c.close();
                xmlt = null;
                if (retry < 60000) {
                    retry += 10000;
                } else {
                    if (jwl != null) {
                        jwl.connectionNotPossible();
                    }
                }
                Thread.sleep(retry);
            } catch (Exception e) {
                Log.add2Log(e.getMessage(), this.getClass().getName());
            }
        }
    }
    private static final long Te = 100; //one reading every Te ms
    private boolean active;
    private boolean reconnect;

    /**
     * Forces client to reconnect to server
     */
    public void reconnect() {
        Log.add2Log("Jabber: Forcing reconnect", JabberClient.class.getName());
        this.reconnect = true;
    }

    //do authentification-------------------------------------------
    private void JAuth() throws Exception {
        boolean isGoogle = false;
        int at_pos = jp.jabberUser.indexOf('@');
        if (at_pos == -1) {
            throw new Exception("Jabber: Bad username construction");
        }
        String username = jp.jabberUser.substring(0, at_pos);
        String domain = jp.jabberUser.substring(at_pos + 1);
        Log.add2Log("Jabber: Initialising session...", this.getClass().getName());
        writeStanza("<?xml version=\"1.0\"?><stream:stream to=\"" + domain + "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" version=\"1.0\">");
        x = readStanza();
        if (x.getName().equals("stream:error")) {
            throw new Exception("Jabber: Error opening stream");
        }
        if (jp.new_account) {
            //crating new acount
            writeStanza("<iq id=\"newaccount\" type=\"set\"><query xmlns=\"jabber:iq:register\"><username>" + username + "</username><password>" + jp.jabberPassword + "</password></query></iq>");
            XMLNode _x = readStanza();
            if (_x.getAttr("type").equals("error")) {
                //conflict error code?
                if (_x.getChild("error").getAttr("code").equals("409")) {
                    Log.add2Log(jp.jabberUser + " allready created", this.getClass().getName());
                }
            } else {
                throw new Exception("Jabber: Error creating new account");
            }
        }
        // reading auth <mechanisms>
        if ((x = x.getSubNode("mechanisms", x)) == null) {
            x = readStanza(); //try again just in case mecanisms xml is late
        }
        if ((x = x.getSubNode("mechanisms", x)) == null) {
            throw new Exception("Jabber: No auth mechanisms found");
        }
        int childs = x.getChildsCount();
        for (int i = 0; i < childs; i++) {
            Log.add2Log("Jabber: Mechanism:|" + x.getChild(i).getValue() + "|", this.getClass().getName());
        }
        if (x.hasValueOfChild("PLAIN")) {
            Log.add2Log("Jabber: PLAIN authorization", this.getClass().getName());
            String resp = "\0" + username + "\0" + jp.jabberPassword;
            writeStanza("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">" + MD5.toBase64(resp.getBytes()) + "</auth>");
            x = readStanza();
            if (x.getName().equals("failure")) {
                throw new Exception("PLAIN authorization error");
            }
        } else if (x.hasValueOfChild("DIGEST-MD5")) {
            Log.add2Log("Jabber: DIGEST-MD5 authorization", this.getClass().getName());
            writeStanza("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"DIGEST-MD5\"/>");
            x = readStanza();
            if (x.getName().equals("failure")) {
                throw new Exception("MD5 authorization error");
            }
            String dec = new String(Base64.decode(x.getValue().getBytes()));
            int ind = dec.indexOf("nonce=\"") + 7;
            String nonce = dec.substring(ind, dec.indexOf('\"', ind + 1));
            String cnonce = "00deadbeef00";
            writeStanza("<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" + generateAuthResponse(username, jp.getJabberPassword(), domain, "xmpp/" + domain, nonce, cnonce) + "</response>");
            Log.add2Log("Jabber: Waiting for response", this.getClass().getName());
            x = readStanza();
            if (x.getName().equals("failure")) {
                throw new Exception("Jabber: MD5 authentifcation error");
            } else if (x.getName().equals("challenge")) { //DIGEST-MD5 v1
                Log.add2Log("Jabber: MD5 Next authorization step", this.getClass().getName());
                writeStanza("<response xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>");
                x = readStanza();
                if (x.getName().equals("failure")) {
                    throw new Exception("Jabber: MD5 authorization error");
                }
            }
        } else if (x.hasValueOfChild("X-GOOGLE-TOKEN")) {
            isGoogle = true;
            // doing x-google-token autn
            Log.add2Log("Jabber: X-GOOGLE-TOKEN authorization", this.getClass().getName());
            String google_token = new GoogleToken().getGoogleToken(jp.getJabberUser(), jp.getJabberPassword());
            writeStanza("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"X-GOOGLE-TOKEN\">" + google_token + "</auth>");
            x = readStanza();
            if (x.getName().equals("failure")) {
                throw new Exception("Jabber: GOOGLE authorization error with error message: " + x.getChild(0).toString());
            }
        } else {
            throw new Exception("Jabber: Can't find a suitable auth mechanism");
        }
        // hello again
        Log.add2Log("Jabber: Opening next stream", this.getClass().getName());
        writeStanza("<?xml version=\"1.0\"?><stream:stream to=\"" + domain + "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" version=\"1.0\">");
        x = readStanza();
        if (x.getValue().equals("stream:error")) {
            throw new Exception("Jabber: Error opening second stream");
        }
        // resource bind
        Log.add2Log("Jabber: Binding resource", this.getClass().getName());
        writeStanza("<iq type=\"set\" id=\"bind\">" + "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" + "<resource>" + jp.jabberResource + "</resource></bind></iq>");
        x = readStanza();
        if (x.getAttr("type").equals("error")) {
            throw new Exception("Jabber: Error binding resource");
        }
        // session
        Log.add2Log("Jabber: Opening session", this.getClass().getName());
        writeStanza("<iq to=\"" + domain + "\" type=\"set\" id=\"jcl_1\">" + "<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/></iq>");
        x = readStanza();
        if (x.getAttr("type").equals("error")) {
            throw new Exception("Error opening session");
        }
        //Log.add2Log("Roster items", this.getClass());
        //writeStanza("<iq type=\"get\" id=\"roster\">" + "<query xmlns=\"jabber:iq:roster\"/></iq>");
        Log.add2Log("Jabber: Sending presence", this.getClass().getName());
        writeStanza("<presence><show></show><status></status></presence>");
        if (isGoogle) {
            writeStanza("<iq type=\"get\" id=\"23\"><query xmlns=\"google:mail:notify\" q=\"(!label:^s) (!label:^k) ((label:^u) (label:^i) (!label:^vm))\"/></iq>" + "<iq type=\"get\" to=\"" + jp.jabberUser + "\" id=\"21\"><query xmlns=\"google:shared-status\"/></iq>");
        }
    }

    private XMLNode readStanza() throws Exception {
        for (int i = 0; i < 100; i++) {//wait 10s max
            if (is.available() > 0) {
                return xmlt.receiveXML();
            }
            Thread.sleep(Te);
        }
        throw new Exception("Jabber: no answer");
    }

    private void writeStanza(String stream) throws Exception {
        os.write(stream.getBytes());
        os.flush();
        if (jp.debug) {
            Log.add2Log(stream, this.getClass().getName());
        }
    }

    private String generateAuthResponse(String user, String pass, String realm, String digest_uri, String nonce, String cnonce) {
        String val1 = user + ":" + realm + ":" + pass;
        byte bb[] = new byte[17];
        bb = md5It(val1);
        int sl = (":" + nonce + ":" + cnonce).length();
        byte cc[] = (":" + nonce + ":" + cnonce).getBytes();
        byte bc[] = new byte[99];
        System.arraycopy(bb, 0, bc, 0, 16);
        System.arraycopy(cc, 0, bc, 16, sl);
        String val2 = (MD5.toHex(md5It(bc, sl + 16)));
        String val3 = "AUTHENTICATE:" + digest_uri;
        val3 = MD5.toHex(md5It(val3));
        String val4 = val2 + ":" + nonce + ":00000001:" + cnonce + ":auth:" + val3;
        val4 = MD5.toHex(md5It(val4));
        String enc = "charset=utf-8,username=\"" + user + "\",realm=\"" + realm + "\"," + "nonce=\"" + nonce + "\",cnonce=\"" + cnonce + "\"," + "nc=00000001,qop=auth,digest-uri=\"" + digest_uri + "\"," + "response=" + val4;
        String resp = MD5.toBase64(enc.getBytes());
        return resp;
    }

    private byte[] md5It(String s) {
        byte bb[] = new byte[16];
        try {
            MD5 md2 = new MD5(s.getBytes());
            return md2.doFinal();
        } catch (Exception e) {
            Log.add2Log(e.getMessage(), this.getClass().getName());
        }
        return bb;
    }

    private byte[] md5It(byte[] s, int l) {
        byte bb[] = new byte[16];
        try {
            byte tmp[] = new byte[l];
            System.arraycopy(s, 0, tmp, 0, l);
            MD5 md2 = new MD5(tmp);
            return md2.doFinal();
        } catch (Exception e) {
            Log.add2Log(e.getMessage(), this.getClass().getName());
        }
        return bb;
    }

    private String normalizeMessage(String str) {
        StringBuffer nbuf = new StringBuffer();
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch == '&') {
                i++;
                StringBuffer buf = new StringBuffer();
                while (i < str.length() && (ch = str.charAt(i)) != ';') {
                    buf.append(ch);
                    i++;
                }
                String sc = buf.toString();
                if (sc.equalsIgnoreCase("lt")) {
                    nbuf.append('<');
                } else if (sc.equalsIgnoreCase("gt")) {
                    nbuf.append('>');
                } else if (sc.equalsIgnoreCase("amp")) {
                    nbuf.append('&');
                } else if (sc.equalsIgnoreCase("apos")) {
                    nbuf.append('\'');
                } else if (sc.equalsIgnoreCase("quot")) {
                    nbuf.append('"');
                }
            } else {
                nbuf.append(ch);
            }
        }
        return nbuf.toString();
    }
}
