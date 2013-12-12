package io;

/**
 * Profile class for FILEConnection
 * @author Florian Chis
 */
public class FILEProfile implements ConnectionProfile{

    /**
     * Get connection profile
     * @return profile
     */
    public String getProfile() {
        return "file:///"+path;
    }

    /**
     * Set Connection path.<br>
     * ex: a:/directory/myfile.txt where a:/ is the volume
     * @param path
     */
    public void setPATH(String path) {
        this.path=path;
    }
    private String path;

    /**
     * Get current path
     * @return current set path
     */
    public String getPATH() {
        return path;
    }

    //-------------------------------------------------

    /**
     * Set if the file/directory specified in the path should be hidden
     * @param choice choice
     */
    public void setHidden(boolean choice) {
        hidden = choice;
    }
    protected boolean hidden=false;

    /**
     * Get if path is to be hidden
     * @return if this path is to be hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Append to file
     * @param choice tru to append at the end of file
     */
    public void setAppend(boolean choice) {
        append = choice;
    }
    protected boolean append=false;

    /**
     * Get if append to file is set
     * @return true if Append is enabled
     */
    public boolean isAppend() {
        return append;
    }

    /**
     * Delete file/directory<br>
     * As a result the file/directory secified in the path will be deleted on connection open
     */
    public void delete() {
        delete=true;
    }
    protected boolean delete=false;

    /**
     * Create directory<br>
     * As a result the directory secified in the path will be created on connection open
     */
    public void mkir() {
        mkdir=true;
    }
    protected boolean mkdir=false;

    /**
     * Rename to files/folders <br>
     * As a result the name of the file/directory specified by the path will be changed on connection open
     * @param newName new name
     */
    public void rename(String newName) {
        this.newName=newName;
        toRename=true;
    }
    protected String newName;
    protected boolean toRename=false;
}
