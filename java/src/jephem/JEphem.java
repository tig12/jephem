//*********************************************************************************
// class jephem.JEphem
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem;

import jephem.gui.MainFrame;
import jephem.util.Debug;
import tig.TigProperties;
import tig.swing.SwingUtils;

//import org.xml.sax.*;
//import javax.xml.parsers.*;

/******************************************************************
Conductor of JEphem application ; contains the <CODE>main()</CODE> method, which :
<LI>initializes JEphem global variables,</LI>
<LI>loads internationalized data useful at startup,</LI>
<LI>builds the main frame for GUI (Graphical User Interface),</LI>

<BR><BR>JEphem needs to have 'tig' package in the classpath to run.

@author : Thierry Graff

@history sep 15 2001 : Creation
@history feb 28 2002 : Split the class - GUI stuff done by MainFrame

*****************************************************************/
public class JEphem{

  //=================================================================================
  //                            INSTANCE VARIABLES
  //=================================================================================
  private static MainFrame _mainFrame;

  //=================================================================================
  //                                      METHODS
  //=================================================================================

  //********************************** main() *********************************
  /** Entry point of JEphem application.
    <LI>Orders the construction of the main frame.</LI>
    <LI>General error handling.</LI>
  */
  public static void main(String[] args){
    try{
      JEphem jephem = new JEphem();
      jephem.init();
      jephem.start();
    }
    catch(Exception e){
      Debug.traceError(e);
    }// end try-catch
  }// end main()

  //***************** init() ********************************
  /** Handles all inintializations.
  <LI>look'n'feel</LI>,
  <LI>internationalization</LI>,
  <LI>GUI</LI>.
  */
  private void init() throws Exception{
    // *** Look 'n' feel choice. ***
    JEphemPrefs prefs = GlobalVar.getJEphemPrefs();
    // set the LAF corresponding to preferences
    SwingUtils.setLookAndFeel(TigProperties.getIntConstant(prefs.getProperty(JEphemPrefs.KEY_LAF),
                              "tig.swing.SwingUtils"));
    // Initializes the main frame
    _mainFrame =  new MainFrame();
    GlobalVar.setMainFrame(_mainFrame);
  }// end init()

  //***************** start() ********************************
  /** Sets the main frame visible. */
  public void start(){
    _mainFrame.setVisible(true);
  }// end start()

}// end class JEphem
