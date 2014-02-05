package io;

/**
 * Profile class for SMSConnection
 */
public class SMSProfile implements ConnectionProfile {

    /**
     * Get connection profile
     * @return connection profile string
     */
    public String getProfile() {
        return "";
    }
    public static final String MESAGE_STORAGE_SIM = "SM";
    public static final String MESAGE_STORAGE_TERMINAL = "ME";
    public static final String MESAGE_STORAGE_BOTH = "MT";

    /**
     * Set message storage. It can be the SIM, the terminal it self or both<br>
     * Use class constants
     * @param storage
     */
    public void setMessageStorage(String storage) {
        this.storage = storage;
    }
    protected String storage = MESAGE_STORAGE_BOTH;

    /**
     * Get message storage medium
     * @return storage medium
     */
    public String getMessageStorage() {
        return storage;
    }

    /**
     * Choose which SMS from storage
     * @param index
     */
    public void setMessageIndex(int index) {
        this.index = index;
    }
    protected int index = 1;

    /**
     * Get message index id
     * @return message index in storage
     */
    public int getMessageIndex() {
        return index;
    }

    /**
     * Get message date
     * @return SMS date after opening connection on valid SMS at index
     */
    public String getMessageDate() {
        return date;
    }
    protected String date;

    /**
     * Change phone number, redirecting the output stream to that specific number
     * @param phone phone number
     */
    public void setPhoneNumber(String phone) {
        this.phone = phone;
    }
    protected String phone;

    /**
     * Get current phone number
     * @return current phone number used by the output stream
     */
    public String getPhoneNumber() {
        return phone;
    }

    /**
     * Set true if incoming messages will be deleted after interpretation
     * @param choice choice. Deletes the SMS from location after reading
     */
    public void setDeleteSMSAfterRead(boolean choice) {
        this.delete = choice;
    }
    protected boolean delete = false;

    /**
     * Get if messages are to be deleted after reading them
     * @return true if this option is enabled
     */
    public boolean isDeleteSMSAfterRead() {
        return delete;
    }
}
