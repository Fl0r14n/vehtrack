package lib.xml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A XML Node representation model
 * @author Florian Chis
 */
public class XMLNode {

    private final Vector childs;
    private final Hashtable attributes;
    private String name;
    private String value;

    /**
     * Constructor
     * @param name node name else null
     */
    public XMLNode(String name) {
        super();
        attributes = new Hashtable(1);
        childs = new Vector(1);
        this.name = name;
    }

    //--------------Node-Name------------------
    /**
     * Get this node name
     * @return node name
     */
    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    /**
     * Assign a name to this node
     * @param name node name
     */
    public void setName(String name) {
        this.name = name;
    }

    //--------------Node-Value------------------
    /**
     * @return value as node value
     */
    public String getValue() {
        if (value == null) {
            return "";
        }
        return value;
    }

    /**
     * Assign a node value to this node
     * @param value node value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Search this node childs value field for value
     * @param value node value to search
     * @return true if value is found
     */
    public boolean hasValueOfChild(String value) {
        for (int i = 0; i < childs.size(); i++) {
            XMLNode x = (XMLNode) childs.elementAt(i);
            if (x.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    //--------------Node-Attributes------------------
    /**
     * Get node attribute
     * @param key attribute name
     * @return this node attribute
     */
    public String getAttr(String key) {
        String val = (String) attributes.get(key);
        if (val == null) {
            return "";
        }
        return val;
    }

    /**
     * Set a node attribute
     * @param key attribute name
     * @param val value to write
     */
    public void setAttr(String key, String val) {
        if (key == null || val == null) {
            return;
        }
        attributes.put(key, val);
    }

    /**
     * Add a attribute to this node
     * @param key attribute name
     * @param val attribute value
     */
    public void addAttr(String key, String val) {
        setAttr(key, val);
    }

    /**
     * Remove an attribute
     * @param key attribute name
     */
    public void delAttr(String key) {
        attributes.remove(key);
    }

    //--------------Node-Childs------------------
    /**
     * Checks to se if node contains child by name
     * @param name node name to check
     * @return seached node index. -1 is returned if not found
     */
    public int hasChild(String name) {
        for (int i = 0; i < childs.size(); i++) {
            XMLNode x = (XMLNode) childs.elementAt(i);
            if (x.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get child node by name
     * @param name name of the child node
     * @return child node requested or empty node if not found
     */
    public XMLNode getChild(String name) {
        int pos;
        if ((pos = hasChild(name)) != -1) {
            return (XMLNode) childs.elementAt(pos);
        }
        return new XMLNode(null);
    }

    /**
     * Get child node at index
     * @param idx child index
     * @return child node at index
     */
    public XMLNode getChild(int idx) {
        return (XMLNode) childs.elementAt(idx);
    }

    /**
     * Get total number of childs this node has
     * @return child count
     */
    public int getChildsCount() {
        return childs.size();
    }

    /**
     * Add a new child node to this node
     * @param n child
     */
    public void addChild(XMLNode n) {
        if (!(n instanceof XMLNode)) {
            return;
        }
        childs.addElement(n);
    }

    /**
     * Erase child by name if found
     * @param name child name
     */
    public void delChild(String name) {
        int pos;
        if ((pos = hasChild(name)) != -1) {
            childs.removeElementAt(pos);
        }
    }

    //-----------------------SUB-NODES------------------
    /**
     * Searches node tree for node with name
     * @param name node name to be searched
     * @param root root node
     * @return empty node or whole node if found
     */
    public XMLNode getSubNode(String name, XMLNode root) {
        if (root.getName().equals(name)) {
            return root;
        }
        int i = 0;
        while (i < root.getChildsCount()) {
            XMLNode x = getSubNode(name, root.getChild(i));
            if (x == null) {
                i++;
            } else {
                return x;
            }
        }
        return null;
    }

    //-------------------------OTHER--------------------
    /**
     * @return XML String representation of this node
     */
    public String toString() {
        return toString2(0);
    }

    /**
     * @return formated XML String representation of this node
     */
    public String toStringFormated() {
        return toString(0);
    }

    private String toString(int level) {
        StringBuffer buf = new StringBuffer();
        String temp, tab = "";
        for (int i = 0; i < level; i++) {
            tab += '\t';
        }
        buf.append("<").append(getName());
        for (Enumeration e = attributes.keys(); e.hasMoreElements();) {
            temp = (String) e.nextElement();
            buf.append(" ").append(temp).append("=\"").append(attributes.get(temp)).append("\"");
        }
        buf.append(">").append(getValue());
        for (int i = 0; i < childs.size(); i++) {
            buf.append("\n").append(tab).append("\t").append(((XMLNode) childs.elementAt(i)).toString(level + 1));
        }
        buf.append("\n").append(tab).append("</").append(getName()).append(">");
        return buf.toString();
    }

    private String toString2(int level) {
        StringBuffer buf = new StringBuffer();
        String temp;
        buf.append("<").append(getName());
        for (Enumeration e = attributes.keys(); e.hasMoreElements();) {
            temp = (String) e.nextElement();
            buf.append(" ").append(temp).append("=\"").append(attributes.get(temp)).append("\"");
        }
        buf.append(">").append(getValue());
        for (int i = 0; i < childs.size(); i++) {
            buf.append(((XMLNode) childs.elementAt(i)).toString2(level + 1));
        }
        buf.append("</").append(getName()).append(">");
        return buf.toString();
    }
}
