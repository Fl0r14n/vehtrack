
import java.util.Vector;

public class Position {

    public static final String HEADER = "$P,";
    protected long timestamp;
    protected double latitude;
    protected double longitude;
    protected double speed;

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(timestamp).append(separator);
        buf.append(latitude).append(separator);
        buf.append(longitude).append(separator);
        buf.append(speed);
        return buf.toString();
    }
    private static final char separator = ',';

    public static void addPosition(Position p) {
        v.addElement(p);
    }
    private static Vector v;

    public static int getPositionsCount() {
        return v.size();
    }

    public static String getPositions() {
        StringBuffer buf = new StringBuffer();
        buf.append(HEADER);
        for (int i = 0; i < v.size(); i++) {
            buf.append(v.elementAt(i).toString()).append(separator);
        }
        v.removeAllElements(); //clear vector
        return buf.deleteCharAt(buf.length() - 1).toString(); //delete last separator        
    }
}
