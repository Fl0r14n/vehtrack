package lib.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import util.Log;

/**
 * XML Transport implementation <br>
 * Supports both XMPP and SOAP
 * @author Florian Chis
 */
public class XMLTransportImpl implements XMLTransport {

    /**
     * Main Constructor
     * @param is InputStream from which the XML stanzas will be read
     * @param os OutputStream to which XML stanzas will be sent
     */
    public XMLTransportImpl(InputStream is, OutputStream os) {
        try {
            if (is != null) {
                this.is = new InputStreamReader(is, "UTF-8");
            }
            if (os != null) {
                //this.os = new OutputStreamWriter(os, "UTF-8");
                //Serious latency bug in Cinterion OutputStreamWriter implementation
                this.os = os;
            }
        } catch (Exception e) {
            Log.add2Log(e.getMessage(), this.getClass().getName());
        }
    }
    private InputStreamReader is;
    //private OutputStreamWriter os;
    private OutputStream os;

    /**
     * Send a XML stanza
     * @param node XML node to send
     */
    public void sendXML(XMLNode node) throws IOException {
        //os.write(node.toString().getBytes());
        os.write(toUTF(node.toString()).getBytes());
        os.flush();
        if (debug && !node.getName().equals("")) {
            Log.add2Log(node.toString(), this.getClass().getName());
        }
    }

    /**
     * Try to read a XML stanza
     * @return XML node
     */
    public XMLNode receiveXML() throws IOException {
        XMLNode root = new XMLNode(null);
        state = STATE_INIT;
        parseXML(root);
        if (debug && !root.getName().equals("")) {
            Log.add2Log(root.toString(), this.getClass().getName());
        }
        return root;
    }

    private void parseXML(XMLNode n) throws IOException {
        while (is.ready()) {
            switch (state) {
                case STATE_INIT: {
                    getStartTag();
                    break;
                }
                case STATE_START: { // <
                    ch = is.read(); //read the first char after
                    if (ch == TAG_COMMENT || ch == TAG_FORMAT) {
                        getStartTag(); //read all unnecesary stuff
                        break;
                    }
                    //read name
                    StringBuffer buf = new StringBuffer();
                    buf.append((char) ch);
                    while ((ch = is.read()) != TAG_ATTRIBUTE && ch != TAG_END && ch != TAG_END_NODE) {//read name
                        buf.append((char) ch);
                    }
                    n.setName(n.getName() + buf.toString());
                    //System.out.println("Node name: " + n.getName());
                    if (ch == TAG_ATTRIBUTE) {
                        state = STATE_ATTRIBUTE;
                    } else if (ch == TAG_END_NODE || ch == TAG_END) {
                        state = STATE_STOP;
                    }
                    break;
                }
                case STATE_ATTRIBUTE: {
                    StringBuffer buf = new StringBuffer();
                    while ((ch = is.read()) != TAG_ATTRIBUTE_VALUE) {
                        if (ch != TAG_ATTRIBUTE) {
                            buf.append((char) ch);
                        }
                    }
                    String attribute_name = buf.toString();
                    buf = new StringBuffer();
                    while ((ch = is.read()) != TAG_ATTRIBUTE_END1 && ch != TAG_ATTRIBUTE_END2) {
                    } //read ' or "
                    while ((ch = is.read()) != TAG_ATTRIBUTE_END1 && ch != TAG_ATTRIBUTE_END2) {
                        buf.append((char) ch);
                    }
                    n.addAttr(attribute_name, buf.toString());
                    //System.out.println("Attribute: " + attribute_name+"="+buf.toString());
                    if ((ch = is.read()) == TAG_END_NODE || ch == TAG_END) {
                        state = STATE_STOP;
                    }
                    break;
                }
                case STATE_STOP: { // /
                    if (ch == TAG_END) {
                        while ((ch = is.read()) == '\n' || ch == '\t' || ch == ' ') {
                        }
                        if (ch != TAG_START) {//we have a value
                            StringBuffer buf = new StringBuffer();
                            buf.append((char) ch);
                            while ((ch = is.read()) != TAG_START) {
                                buf.append((char) ch);
                            }
                            n.setValue(buf.toString().trim());
                            do {
                                ch = is.read();
                            } while (ch != TAG_END);
                            return;
                        } else { // start tag
                            state = STATE_NEW_NODE;
                        }
                        break;
                    } else {
                        //state = STATE_INIT;
                        getStartTag();
                        if (ch == TAG_START) {
                            state = STATE_NEW_NODE;
                        }
                        return;
                    }
                }
                case STATE_NEW_NODE: {
                    if ((ch = is.read()) == TAG_COMMENT || ch == TAG_FORMAT) {
                        getStartTag();
                        ch = is.read();
                    }
                    if (ch == TAG_END_NODE) {
                        state = STATE_STOP;
                        break;
                    }
                    state = STATE_START;
                    XMLNode x = new XMLNode("" + (char) ch);
                    parseXML(x);
                    n.addChild(x);
                    //System.out.println(n.getName() + " add child:" + x.toString());
                    break;
                }
            }
        }
        return;
    }
    private int ch;
    private int state = STATE_INIT;
    private static final int TAG_START = '<';
    private static final int TAG_END = '>';
    private static final int TAG_COMMENT = '!';
    private static final int TAG_FORMAT = '?';
    private static final int TAG_END_NODE = '/';
    private static final int TAG_ATTRIBUTE = ' ';
    private static final int TAG_ATTRIBUTE_VALUE = '=';
    private static final int TAG_ATTRIBUTE_END1 = '\'';
    private static final int TAG_ATTRIBUTE_END2 = '"';
    private static final int STATE_INIT = 0;
    private static final int STATE_START = 1;
    private static final int STATE_STOP = 2;
    private static final int STATE_ATTRIBUTE = 3;
    private static final int STATE_NEW_NODE = 4;

    private void getStartTag() throws IOException {
        while ((ch != TAG_START) && is.ready()) {
            ch = is.read();
        }
        state = STATE_START;
    }

    /**
     * Activates debug. 
     * @param choice If true, XML streams will be shown
     */
    public void setDebug(boolean choice) {
        debug = choice;
    }
    private boolean debug = false;

    //fix for not using OutputStreamWriter
    private static String toUTF(String s) {
        int i = 0;
        StringBuffer stringbuffer = new StringBuffer();
        for (int j = s.length(); i < j; i++) {
            int c = (int) s.charAt(i);
            if ((c >= 1) && (c <= 0x7f)) {
                stringbuffer.append((char) c);
            }
            if (((c >= 0x80) && (c <= 0x7ff)) || (c == 0)) {
                stringbuffer.append((char) (0xc0 | (0x1f & (c >> 6))));
                stringbuffer.append((char) (0x80 | (0x3f & c)));
            }
            if ((c >= 0x800) && (c <= 0xffff)) {
                stringbuffer.append(((char) (0xe0 | (0x0f & (c >> 12)))));
                stringbuffer.append((char) (0x80 | (0x3f & (c >> 6))));
                stringbuffer.append(((char) (0x80 | (0x3f & c))));
            }
        }
        return stringbuffer.toString();
    }
}
