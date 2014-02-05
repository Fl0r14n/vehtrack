package io;

import com.siemens.icm.io.file.FileConnection;
import java.io.IOException;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import util.Log;

/**
 * Class for accesing unit's flash file system
 */
public class FILEConnection extends Connection {

    private static final String NAME = "FILEConnection";
    
    /**
     * Open a file connection o a file or directory<br>
     * If the path needs to be changed (ex when moving from file to file) the open() can be called again without first call close()
     * @param cp this connection profile
     * @return true if file is opened
     */
    public boolean open(ConnectionProfile cp) {
        if (cp instanceof FILEProfile) {
            fp = (FILEProfile) cp;
        } else {
            return false;
        }
        try {
            fc = (FileConnection) Connector.open(fp.getProfile(), Connector.READ_WRITE);
            if (fp.mkdir) {
                fc.mkdir();
                fp.mkdir = false;
                return true;
            }
            if (!fc.exists()) {
                fc.create();
            }
            if (fp.delete) {
                fc.delete();
                fp.delete = false;
                //avoid exceptions exceptions from accessing a connection to a non-existent file or directory
                fc.close();
                return true;
            }
            if (fp.toRename) {
                fc.rename(fp.newName);
                fp.toRename = false;
                return true;
            }
            if (fp.hidden) {
                fc.setHidden(true);
            } else {
                fc.setHidden(false);
            }
            is = fc.openInputStream();
            if (fp.append) {
                os = fc.openOutputStream(fc.fileSize());
                fp.append = false;
            } else {
                os = fc.openOutputStream();
            }
            return true;
        } catch (IOException ex) {
            Log.add2Log(ex.getMessage(), NAME);
        }
        return false;
    }
    private FileConnection fc;
    private FILEProfile fp;

    /**
     * Close this connection
     */
    public void close() {
        super.close();
        try {
            if (this.fc != null) {
                this.fc.close();
                this.fc = null;
            }
        } catch (Exception _) {
        }
    }

    //--------------------------------------------------------------------------
    /**
     * List files present on media from path secified in this connection
     * @param regex search according to regex. Wildcards may be used
     * @param hidden true if hidden files should be included
     * @return Enumeration with file names present
     */
    public Enumeration listFiles(String regex, boolean hidden) {
        try {
            return fc.list(regex, hidden);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Get file/directory size
     * @return file/directory size
     */
    public long getSize() {
        try {
            return fc.fileSize();
        } catch (IOException ex) {
            return -1;
        }
    }

    /**
     * Get available space on media
     * @return available space on media
     */
    public long getAvailable() {
        return fc.totalSize() - fc.usedSize();
    }
}
