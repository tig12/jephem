//*********************************************************************************
// class jephem.gui.UtilsGUI
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.gui;

import jephem.GlobalVar;
import javax.swing.*;

/**********************************************************************************
Class containing utility static methods and constants for JEphem's GUI.
@author Thierry Graff
@history oct 05 2001 : Creation.
**********************************************************************************/
public abstract class UtilsGUI{

  //=================================================================================
  //                                CONSTANTS
  //=================================================================================

  //=================================================================================
  //                                STATIC METHODS
  //=================================================================================

  //*****************************************************
  /** Convenient method to display an error message */
  public static void showErrorMessage(Throwable t){
    String message = GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL).getString("error.Internal") + t.toString();
    JOptionPane.showMessageDialog(GlobalVar.getMainFrame(),
                                  message,
                                  GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL).getString("Error"),
                                  JOptionPane.WARNING_MESSAGE);
  }// end showErrorMessage

  //*****************************************************
  /** Convenient method to display a warning message */
  public static void showWarningMessage(String message){
    JOptionPane.showMessageDialog(GlobalVar.getMainFrame(),
                                  message,
                                  GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL).getString("Warning"),
                                  JOptionPane.WARNING_MESSAGE);
  }// end showWarningMessage

}//end abstract class UtilsGUI
