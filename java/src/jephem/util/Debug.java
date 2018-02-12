//*********************************************************************************
// class jephem.util.Debug
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.util;

import jephem.astro.AstroException;
import jephem.GlobalVar;
import tig.GeneralConstants;
import tig.TigBundle;
import tig.Exceptions;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/******************************************************************************
Contains methods report exceptions - can be used in debug and production contexts.
@author Thierry Graff
@history oct 02 2001 : creation.
@history mar 11 2002 : added trace modes.
@history apr 25 2002 : used tig.Exceptions

@todo
*********************************************************************************/
public abstract class Debug implements GeneralConstants{

  //=================================================================================
  //                              CONSTANTS
  //=================================================================================
  /** Constant to indicate that the way to trace exceptions is equivalent to <CODE>printStackTrace()</CODE> - default mode. */
  public static final int STACK_TRACE = 0;
  /** Constant to indicate that the way to trace exceptions is done through a dialog box,
  using internationalization. */
  public static final int GUI_TRACE = 1;

  //=================================================================================
  //                              STATIC VARIABLES
  //=================================================================================

  private static int _traceMode = STACK_TRACE;

  //=================================================================================
  //                              CONSTANTS
  //=================================================================================
  private static TigBundle _myBundle = null;
  static{
    try {
      _myBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "lang" + FS + "Debug.lang", GlobalVar.getLocale());
    }
    catch (Exception ex) {
    }
  };

  //=================================================================================
  //                              METHODS
  //=================================================================================

  //*************** traceError ***************
  /** Traces an exception in a text area of an autonom frame.
  @param e The exception being thrown.
  */
  public static void sendError(Exception e){
    Exceptions.printShortTrace(e);
  }// end sendError

  //*************** traceError ***************
  private static File errorFile = new File("errors.txt");
  /** Traces an exception in a text area of an autonom frame.
  @param e The exception being thrown.
  */
  public static void traceError(Exception e){
    // extract eventual embedded exception from AstroExceptions
    if(e.getClass().getName().equals("jephem.astro.AstroException")){
      e = ((AstroException)e).getEmbeddedException();
    }

    if (_traceMode == STACK_TRACE){
      e.printStackTrace();
      return;
    }

    if (_traceMode == GUI_TRACE){
      try{
  /*
        PrintStream ps = new PrintStream(new FileOutputStream(errorFile));
        System.setErr(ps);
        e.printStackTrace();
        String strError;
        LineNumberReader lnr = new LineNumberReader(new FileReader(errorFile));
        String message = "";
        String line;
        while((line = lnr.readLine()) != null) message += line + LS;
        lnr.close();
        ps.close();
        boolean useless = errorFile.delete();
        //System.out.println(useless);
        //System.out.println(message);

        //Build an autonom frame.
        final JFrame f = new JFrame("Error");
        Container contentPane = f.getContentPane();
        contentPane.add(new JScrollPane(new JTextArea(message, 30, 45)), BorderLayout.CENTER);
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent ae){
            f.setVisible(false);
          }
        });
        contentPane.add(btnOK, BorderLayout.SOUTH);
        f.pack();
        f.setVisible(true);
  */
      }
      catch(Exception e2){
        System.out.println("*** Debug.traceError - Failed to trace an exception ***");
      }
    }// end if (_traceMode == STACK_TRACE)
  }// end traceError

}// end class Debug