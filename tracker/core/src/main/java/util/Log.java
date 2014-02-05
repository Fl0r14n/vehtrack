package util;

import java.util.Date;

/**
 * Debug class
 */
public class Log {

    /**
     * Set custom Log implementation
     *
     * @param dbg
     */
    public static void setDebug(Debug dbg) {
        Log.dbg = dbg;
    }

    /**
     * Remove debug implementation if any
     */
    public static void removeDebug() {
        dbg = null;
    }

    /**
     * Add to Log
     *
     * @param stream stream
     */
    public static void add2Log(String stream) {
        add2Log(stream, "");
    }

    /**
     * Add to Log
     *
     * @param stream stream
     * @param source source
     */
    public static void add2Log(String stream, String source) {
        if (dbg != null) {
            dbg.add2Log(source, stream);
        } else {
            System.out.println(new Date().toString() + "|" + source + ": " + stream);
        }
    }

    public static abstract class Debug {

        /**
         * Add to Log
         *
         * @param source source
         * @param stream stream
         */
        public abstract void add2Log(String stream, String source);
    }
    private static Debug dbg;
}
