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
 * @(#)NetDemo.java 0.1 02/18/05
 */

package example.netdemo;

import javax.microedition.midlet.*;
import java.io.*;
import javax.microedition.io.*;


public class NetDemo extends MIDlet {
    
    static String destHost = "192.168.1.2"; 
    static String destPort = "80";
    static String connProfile = "bearer_type=gprs;access_point=internet.t-d1.de;username=anyone;password=something";

    /**
    * NetDemo - default constructor
    */
    public NetDemo() 
    {
        System.out.println("NetDemo: Constructor"); 
    }

    /**
    * startApp()
    */
    public void startApp() throws MIDletStateChangeException 
    {  
        SocketConnection    sc  = null;
        InputStream         is  = null;
        OutputStream        os  = null;

        System.out.println("NetDemo: startApp");

        try
        {       
            /* Open all */
            String openParm = "socket://" + destHost + ":" + destPort+ ";" + connProfile;            
            System.out.println("NetDemo: Connector open: " + openParm);             
            sc = (SocketConnection) Connector.open(openParm);

            is = sc.openInputStream();
            os = sc.openOutputStream();

            /* Write Data */
            String outTxt = "GET /somefile.txt HTTP/1.0\r\n\r\n";
            System.out.println("NetDemo: sending: " + outTxt);                
            os.write(outTxt.getBytes());

            /* Read Data */
            StringBuffer    str = new StringBuffer();
            int             ch;
           
            while ((ch = is.read()) != -1) 
            {
                str.append((char)ch);
            }

            System.out.println("NetDemo: received: " + str );

            /* Close all */
            is.close();
            os.close();
            sc.close();

        }
        catch (Exception e)
        {
            System.out.println("NetDemo: " + e.getMessage()); 
        }    

        destroyApp(true);
    }

    /**
    * pauseApp()
    */
    public void pauseApp() {
        System.out.println("NetDemo: pauseApp()");
    }

    /**
    * destroyApp()
    *
    * This is important.  It closes the app's RecordStore
    * @param cond true if this is an unconditional destroy
    *             false if it is not
    *             currently ignored and treated as true
    */
    public void destroyApp(boolean cond) {
        System.out.println("NetDemo: destroyApp(" + cond + ")");

        notifyDestroyed();
    }
}
