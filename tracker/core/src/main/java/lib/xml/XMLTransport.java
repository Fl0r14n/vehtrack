package lib.xml;

import java.io.IOException;

/**
 * A simple trasport interface for XML stanzas
 */
public interface XMLTransport {

    /**
     * Send a XML stanza
     * @param node XML node to send
     * @throws IOException
     */
    public void sendXML(XMLNode node) throws IOException;

    /**
     * Try to read a XML stanza
     * @return XML node
     * @throws IOException
     */
    public XMLNode receiveXML() throws IOException;
}
