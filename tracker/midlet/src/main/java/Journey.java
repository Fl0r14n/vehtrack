
public class Journey {

    public static final String HEADER = "$J,";
    protected long startTimestamp;
    protected double startLatitude;
    protected double startLongitude;
    protected long finishTimestamp;
    protected double finishLatitude;
    protected double finishLongitude;
    protected double distance;
    protected double avgSpeed;
    protected double maxSpeed;

    //duration is calcultated by server
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(HEADER);
        buf.append(startTimestamp).append(separator);
        buf.append(startLatitude).append(separator);
        buf.append(startLongitude).append(separator);
        buf.append(finishTimestamp).append(separator);
        buf.append(finishLatitude).append(separator);
        buf.append(finishLongitude).append(separator);
        buf.append(distance).append(separator);
        buf.append(maxSpeed);
        return buf.toString();
    }
    private static final char separator = ',';
}
