
public class Event {

    public static final String HEADER = "$E,";
    protected long timestamp;
    protected int type; //to be defined
    protected String msg;

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(HEADER);
        buf.append(timestamp).append(separator);
        buf.append(type).append(separator);
        buf.append(msg);
        return buf.toString();
    }
    private static final char separator = ',';
}
