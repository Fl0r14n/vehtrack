
/*
 * 
 * Copyright (C) Cinterion Wireless Modules GmbH 2008. All Rights reserved.
 *
 * Cinterion Wireless Modules GmbH ("Cinterion") grants Licensee a non-exclusive,
 * non-transferable, limited license to transmit, reproduce, disseminate, utilize
 * and/or edit the source code of this Software (IMlet, LIBlet, batch files,
 * project files) for the sole purpose of designing, developing and testing
 * Licensee's applications only in connection with a Cinterion Wireless Module. 
 *
 * THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * CINTERION, ITS LEGAL REPRESENTATIVES AND VICARIOUS AGENTS SHALL - IRRESPECTIVE
 * OF THE LEGAL GROUND - ONLY BE LIABLE FOR DAMAGES IF THE DAMAGE WAS CAUSED
 * THROUGH CULPABLE BREACH OF A MAJOR CONTRACTUAL OBLIGATION (CARDINAL DUTY),
 * I.E. A DUTY THE FULFILMENT OF WHICH ALLOWS THE PROPER EXECUTION OF THE
 * RESPECTIVE AGREEMENT IN THE FIRST PLACE OR THE BREACH OF WHICH PUTS THE
 * ACHIEVEMENT OF THE PURPOSE OF THE AGREEMENT AT STAKE, RESPECTIVELY, AND ON THE
 * FULFILMENT OF WHICH THE RECIPIENT THEREFORE MAY RELY ON OR WAS CAUSED BY GROSS
 * NEGLIGENCE OR INTENTIONALLY. ANY FURTHER LIABILITY FOR DAMAGES SHALL -
 * IRRESPECTIVE OF THE LEGAL GROUND - BE EXCLUDED. IN THE EVENT THAT CINTERION IS
 * LIABLE FOR THE VIOLATION OF A MAJOR CONTRACTUAL OBLIGATION IN THE ABSENCE OF
 * GROSS NEGLIGENCE OR WILFUL CONDUCT, SUCH LIABILITY FOR DAMAGE SHALL BE LIMITED
 * TO AN EXTENT WHICH, AT THE TIME WHEN THE RESPECTIVE AGREEMENT IS CONCLUDED,
 * CINTERION SHOULD NORMALLY EXPECT TO ARISE DUE TO CIRCUMSTANCES THAT THE PARTIES
 * HAD KNOWLEDGE OF AT SUCH POINT IN TIME. CINTERION SHALL IN NO EVENT BE LIABLE
 * FOR INDIRECT AND CONSEQUENTIAL DAMAGES OR LOSS OF PROFIT. CINTERION SHALL IN NO
 * EVENT BE LIABLE FOR AN AMOUNT EXCEEDING € 20,000.00 PER EVENT OF DAMAGE. WITHIN
 * THE BUSINESS RELATIONSHIP THE OVERALL LIABILITY SHALL BE LIMITED TO A TOTAL
 * OF € 100,000.00. CLAIMS FOR DAMAGES SHALL BECOME TIME-BARRED AFTER ONE YEAR AS
 * OF THE BEGINNING OF THE STATUTORY LIMITATION PERIOD. IRRESPECTIVE OF THE
 * LICENSEE'S KNOWLEDGE OR GROSS NEGLIGENT LACK OF KNOWLEDGE OF THE CIRCUMSTANCES
 * GIVING RISE FOR A LIABILITY ANY CLAIMS SHALL BECOME TIME-BARRED AFTER FIVE
 * YEARS AS OF THE LIABILITY AROSE. THE AFOREMENTIONED LIMITATION OR EXCLUSION
 * OF LIABILITY SHALL NOT APPLY IN THE CASE OF CULPABLE INJURY TO LIFE, BODY OR
 * HEALTH, IN CASE OF INTENTIONAL ACTS, UNDER THE LIABILITY PROVISIONS OF THE
 * GERMAN PRODUCT LIABILITY ACT (PRODUKTHAFTUNGSGESETZ) OR IN CASE OF A
 * CONTRACTUALLY AGREED OBLIGATION TO ASSUME LIABILITY IRRESPECTIVE OF ANY
 * FAULT (GUARANTEE).
 *
 * IN THE EVENT OF A CONFLICT BETWEEN THE PROVISIONS OF THIS AGREEMENT AND
 * ANOTHER AGREEMENT REGARDING THE SOURCE CODE  OF THIS SOFTWARE (IMLET, LIBLET,
 * BATCH FILES, PROJECT FILES) (EXCEPT THE GENERAL TERMS AND CONDITIONS OF
 * CINTERION) THE OTHER AGREEMENT SHALL PREVAIL.
 *
 */

/*
 * @(#)FwUpDemo.java 1.0 01/06/06
 */

package example.fwupdemo;

import javax.microedition.midlet.*;
import java.io.*;
import javax.microedition.io.*;

import com.siemens.icm.io.*;

import com.siemens.icm.io.file.FileConnection;

/**
 * MIDlet to demonstrate firmware update over the air (FOTA)
 * It downloads a firmware image from a HTTP server, stores it in an external storage
 * connected via SPI and then triggers firmware flashing procedure.
 */
public class FwUpDemo extends MIDlet {

    /* address or name of the HTTP server */
    static String destHost = "192.168.1.3"; 
    /* port of the HTTP server */
    static String destPort = "80";   
    /* local file name */
    static String fileName = "kathy_all_222an.usf";
    /* drive name */
    static String driveName = "3:";
    /* connection profile */
    static String connProfile = "bearer_type=gprs;access_point=internet.t-d1.de;username=anyone;password=something";
    
    private ATCommand  ATCmd = null;
    private ATListener lt = null;
    private HttpConnection  hc  = null;
    private InputStream     is  = null;
    private DataOutputStream    os  = null;
    private FileConnection fconn = null;

    /**
    * AT listener
    */
    class ATListener implements ATCommandListener {
        
        public void ATEvent(String Event) {
            System.out.println("URC-Event: " + Event);
        }
        
        public void RINGChanged(boolean SignalState) {
            System.out.println("RING-Event: " + SignalState);
        }
        
        public void DCDChanged(boolean SignalState) {
            System.out.println("DCD-Event: " + SignalState);
        }
        
        public void DSRChanged(boolean SignalState) {
            System.out.println("DSR-Event: " + SignalState);
        }

        public void CONNChanged(boolean SignalState) {
            System.out.println("CONN-Event: " + SignalState);
        }
    }
    
    /**
    * default constructor
    */
    public FwUpDemo() 
    {
        System.out.println("FwUpDemo: Constructor");

        /* create AT command instance */
        try {
            lt = new ATListener();
            ATCmd = new ATCommand(false);
            ATCmd.addListener(lt);
        } catch (ATCommandFailedException e) {
            System.out.println(e);
            destroyApp(true);
            notifyDestroyed();
        }
    }

    /**
    * startApp()
    */
    public void startApp() throws MIDletStateChangeException 
    {  
        System.out.println("FwUpDemo: startApp");

        try
        {    
            int rc;
            int len;
            String strRcv;

            /* Open HTTP connector */
            String openParm = "http://" + destHost + ":" + destPort + "/datafiles/" + fileName + ";" + connProfile;            

            System.out.println("FwUpDemo: Connector open: " + openParm);
                                  
            hc = (HttpConnection) Connector.open(openParm);

            /* evaluate HTTP response code */
            rc = hc.getResponseCode();
            System.out.println("FwUpDemo: HTTP response: " + rc);
            if (rc != HttpConnection.HTTP_OK) 
            {
                 throw new IOException("HTTP response code: " + rc);
            }

            /* get length */
            len = (int) hc.getLength();
            System.out.println("FwUpDemo: HTTP Length: " + len);

            /* open input stream for reading file via http  */
            is = hc.openInputStream();

            try
            {
                int buffsize = 2048;
                byte[] buff = new byte[buffsize];
                int actual = -1;
                long spaceAvailable;

                String pathParm = driveName + "/" + fileName;
                FileConnection fconndel = null;
                
                System.out.println("FwUpDemo: open file: " + pathParm);

                /* open connector for writing data to SPI memory */
                fconn = (FileConnection)Connector.open("file:///"+pathParm);

                if (!fconn.exists()) {
                    try {
                     fconn.create();  // create the file if it doesn't exist
                     System.out.println("FwUpDemo: after create");
                    }
                    catch (IOException e)
                    {
                      // creating of file fails, because drive is not empty: clean it
                      System.out.println("FwUpDemo: IOexception catched, open dir to delete file");
                      fconndel = (FileConnection)Connector.open("file:///"+ driveName +"/");
                      System.out.println("FwUpDemo: try to delete file...");
                      fconndel.delete();
                      System.out.println("FwUpDemo: file deleted");
                      fconndel.close();
                      // now create file
                      fconn.create();  // create the file if it doesn't exist
                    }
                }

                spaceAvailable = fconn.availableSize();

                System.out.println("FwUpDemo: availableSize: " + spaceAvailable);

                System.out.println("FwUpDemo: totalSize: " + fconn.totalSize());

                /* open output stream for writing to ffs  */
                os = fconn.openDataOutputStream();

                if ( len > spaceAvailable) {
                  throw new Exception("Not enough storage on device available!");
                }

                /* read from HTTP connection and save to file */
                if (len > 0) 
                {
                    int received = 0;

                    do 
                    {
                        actual = is.read(buff, 0, buffsize);

                        if (actual != -1) 
                        {
                            received += actual;
                            System.out.println("\r\nreceived " + received + " of " +len);                        

                            os.write(buff, 0, actual);
                        }
                    } while (actual != -1);
                }
                
                System.out.println("FwUpDemo: close all");

                /* close file */
                os.close();
                fconn.close();
                
                /* Close http */
                is.close();
                hc.close();
   
   
                /* close GPRS connection */
                System.out.println("close GPRS connection...");
                strRcv = ATCmd.send("\rAT+CGATT=0\r");
                System.out.println("received: " + strRcv);
                if (strRcv.indexOf("OK") < 0) throw new ATCommandFailedException("Wrong answer from module");                    
      
            
                /* start firmware update */
                System.out.println("starting firmware update: check data consistency + trigger update");
                strRcv = ATCmd.send("\rAT^sfdl=1\r");
                System.out.println("received: " + strRcv);
                if (strRcv.indexOf("OK") < 0) throw new ATCommandFailedException("Wrong answer from module");                    

            }
            catch (Exception e)
            {
                System.out.println("FwUpDemo: " + e.getMessage());
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            System.out.println("FwUpDemo: " + e.getMessage());
            e.printStackTrace(); 
        }

        destroyApp(true);
    }

    /**
    * pauseApp()
    */
    public void pauseApp() {
        System.out.println("FwUpDemo: pauseApp()");
    }

    /**
    * destroyApp()
    *
    * close everything
    * @param cond true if this is an unconditional destroy
    *             false if it is not
    *             currently ignored and treated as true
    */
    public void destroyApp(boolean cond) {
        System.out.println("FwUpDemo: destroyApp(" + cond + ")");

        try {
            if (ATCmd != null) {
                ATCmd.release();
                ATCmd = null;
            }
        } catch (ATCommandFailedException e) {
            System.out.println(e);
        }
        
        try {
            if (os != null) {
                os.close();
                os = null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
        try {
            if (fconn != null) {
                fconn.close();
                fconn = null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            if (is != null) {
                is.close();
                is = null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
        try {
            if (hc != null) {
                hc.close();
                hc = null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        notifyDestroyed();
    }

}
