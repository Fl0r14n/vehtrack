package lib.jabber;

import lib.xml.XMLNode;
import lib.xml.XMLTransport;

/**
 * Abstract class describes common jabber events
 * @author Florian Chis
 */
public abstract class JabberEventListener {

    /**
     * A new message is received
     * @param message message
     * @param from who sent the message
     */
    public abstract void eventMessage(String message, String from);

    /**
     * Iq type message received from server
     * @param node The full XML node
     */
    public abstract void eventIq(XMLNode node);

    /**
     * Someone sent a presence message.
     * @param node The full XML node
     */
    public abstract void eventPresence(XMLNode node);

    /**
     * We are online. XMLTransport is thrown
     * @param xmlt JWriter instance
     */
    protected void eventXMLTransport(XMLTransport xmlt) {
        this.xmlt = xmlt;
    }
    private XMLTransport xmlt;

    public void close() {
        xmlt = null;
    }

    /**
     * Implementation to send a message
     * @param body What is the message to be send
     * @param to to who is the message addressed.
     * @throws Exception
     */
    public void sendMessage(String body, String to) throws Exception {
        XMLNode x = new XMLNode("message");
        x.addAttr("to", to);
        x.addAttr("type", "chat");
        x.addAttr("id", getSessionId());
        XMLNode bdy = new XMLNode("body");
        bdy.setValue(denormalizeMessage(body));
        x.addChild(bdy);
        xmlt.sendXML(x);
    }

    /**
     * Send a presence to server. Announce our status
     * @param status Status type
     * @param show Custom message
     * @throws Exception 
     */
    public void sendPresence(String status, String show) throws Exception {
        XMLNode x = new XMLNode("presence");
        XMLNode st = new XMLNode("status");
        st.setValue(status);
        XMLNode sh = new XMLNode("show");
        sh.setValue(show);
        x.addChild(st);
        x.addChild(sh);
        xmlt.sendXML(x);
    }

    /**
     * Send a iq to server
     * @param type iq type
     * @param xmlns xmlns
     * @param to to who is the iq addressed; the domain in most cases
     * @throws Exception 
     */
    public void sendIq(String type, String xmlns, String to) throws Exception {
        XMLNode x = new XMLNode("iq");
        x.addAttr("type", type);
        x.addAttr("to", to);
        x.addAttr("id", getSessionId());
        XMLNode query = new XMLNode("query");
        query.addAttr("xmlns", xmlns);
        x.addChild(query);
        xmlt.sendXML(x);
    }

    /**
     * send raw xml to server
     * @param xml message to send
     * @throws Exception
     */
    public void sendXML(XMLNode xml) throws Exception {
        xmlt.sendXML(xml);
    }

    /**
     * generates a sesion id used in jabber transactions
     * @return session id
     */
    protected String getSessionId() {
        sesion++;
        return "jcl_" + sesion;
    }
    private long sesion = 1;

    protected String denormalizeMessage(String str) {
        StringBuffer nbuf = new StringBuffer();
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            switch (c) {
                case '<':
                    nbuf.append("&lt;"); /* System.out.print("ESC(<)"); */
                    break;
                case '>':
                    nbuf.append("&gt;"); /* System.out.print("ESC(>)"); */
                    break;
                case '&':
                    nbuf.append("&amp;"); /* System.out.print("ESC(&)"); */
                    break;
                case '\'':
                    nbuf.append("&apos;"); /* System.out.print("ESC('\')"); */
                    break;
                case '"':
                    nbuf.append("&quot;"); /* System.out.print("ESC(\""); */
                    break;
                default:
                    nbuf.append(c); /* System.out.print(c); */
                    break;
            }
        }
        return nbuf.toString();
    }
}
