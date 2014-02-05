package io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines basic io operations
 */
public abstract class Connection {

    /**
     * Generic open method
     * @param cp this connection profile with desired parameters
     * @return true if the connection has been established<br>
     * <b>Should check this and not open the streams unless true is returned</b>
     */
    public abstract boolean open(ConnectionProfile cp);

    /**
     * Generic close method
     */
    public void close() {
        try {
            if (this.is != null) {
                this.is.close();
                this.is = null;
            }
            if (this.os != null) {
                this.os.close();
                this.os = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     * Get InputStream object
     * @return InputStream of this process<br>
     * <b>The method doesen't throw anything so watch for null pointer exception</b>
     */
    public InputStream getInputStream() {
        return is;
    }
    protected InputStream is;

    /**
     * Get OutputStream object
     * @return OutputStream of this process<br>
     * <b>The method doesen't throw anything so watch for null pointer exception</b>
     */
    public OutputStream getOutputStream() {
        return os;
    }
    protected OutputStream os;
}
