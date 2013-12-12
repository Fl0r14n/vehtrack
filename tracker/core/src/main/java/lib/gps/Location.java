package lib.gps;

/**
 * Location info model bases on GGA & RMC sentences
 * @author Chis Florian
 */
public class Location {

    /**
     * Default constructor
     */
    public Location() {
    }

    /**
     * Set a Location object constructor.<br> To be used in geofence points
     * @param name This proximity point name
     * @param latitude Latitude in degrees like gg.gggg
     * @param longitude Longitude in degrees like gg.gggg
     * @param radius Radius in km
     */
    public Location(String name, double latitude, double longitude, double radius) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }
    //all variables
    protected double latitude, longitude, altitude, speed, azimuth;
    protected long utc_timestamp;
    protected int fix, satellites;
    protected float hdop, separation;
    protected String name;
    protected double radius;
    protected int status_geofence = -1; //internal use

    /**
     * @return Latitude in degrees
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set latitude in degrees
     * @param latitude Latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return Longitude in degrees
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set longitude in degrees
     * @param longitude Longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * This Location object name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set this Location object name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the radius in km used in geofence
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Set proximity radius of this point
     * @param radius Radius in km
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * @return Altitude in meters
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @return Bearing in degrees. True North is 0
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * @return GPS current fix status. Compare with class constants
     */
    public int getFix() {
        return fix;
    }
    public static int FIX_NA = -1;
    public static int FIX_INVALID = 0; //no fix
    public static int FIX_STANDARD = 1; //2D || 3D
    public static int FIX_DIFFERENTIAL = 2; //DGPS
    public static int FIX_ESTIMATED = 6; //DR

    /**
     * @return Horizontal Dilution of Precision
     */
    public float getHdop() {
        return hdop;
    }

    /**
     * @return the number of satellites used
     */
    public int getSatellites() {
        return satellites;
    }

    /**
     * @return Geoid Separation in meters
     */
    public float getSeparation() {
        return separation;
    }

    /**
     * @return Speed in km/h
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @return Unix time stamp as millis from 01.01.1970
     */
    public long getUNIXTimestamp() {
        return utc_timestamp;
    }

    /**
     * @return String representation of this object
     */
    public String toString() {
        return utc_timestamp + ",LAT:" + latitude + ",LNG:" + longitude + ",ALT:" + altitude + ",SPD:" + speed + ",AZI:" + azimuth + "FIX:" + fix;
    }
}
