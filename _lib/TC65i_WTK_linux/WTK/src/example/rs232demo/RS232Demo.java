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
 * @(#)RS232Demo.java 0.1 02/07/02
 */

package example.rs232demo;

import javax.microedition.midlet.*;
import java.io.*;
import javax.microedition.io.*;
import com.siemens.icm.io.*;


public class RS232Demo extends MIDlet {

  CommConnection  commConn;
  InputStream     inStream;
  OutputStream    outStream;

  /**
   * RS232Demo - default constructor
   */
  public RS232Demo() {
    System.out.println("RS232Demo: Constructor");
    System.out.println("Available COM-Ports: " + System.getProperty("microedition.commports"));
    try {
      String strCOM = "comm:com0;blocking=on;baudrate=115200";
      commConn = (CommConnection)Connector.open(strCOM);
      System.out.println("CommConnection(" + strCOM + ") opened");
      System.out.println("Real baud rate: " + commConn.getBaudRate());
      inStream  = commConn.openInputStream();
      outStream = commConn.openOutputStream();
      System.out.println("InputStream and OutputStream opened");
    } catch(IOException e) {
      System.out.println(e);
      notifyDestroyed();
    }
  }

  /**
   * startApp()
   */
  public void startApp() throws MIDletStateChangeException {
    System.out.println("RS232Demo: startApp");
    System.out.println("Looping back received data, leave with 'Q'...");
    try {
      int ch = -1;
      while(ch != 'Q') {
        ch = inStream.read();
        if (ch >= 0) {
          outStream.write(ch);
          System.out.print((char)ch);
        }
      }
    } catch(IOException e) {
      System.out.println(e);
    }
    System.out.println();
    destroyApp(true);
  }

  /**
   * pauseApp()
   */
  public void pauseApp() {
    System.out.println("RS232Demo: pauseApp()");
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
    System.out.println("RS232Demo: destroyApp(" + cond + ")");
    try {
      inStream.close();
      outStream.close();
      commConn.close();
      System.out.println("Streams and connection closed");
    } catch(IOException e) {
      System.out.println(e);
    }

    notifyDestroyed();
  }
}
