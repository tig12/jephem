//*********************************************************************************
// class jephem.GlobalVar
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem;

import jephem.gui.MainFrame;
import jephem.astro.AstroEngine;
import jephem.tools.AstroPrefs;
import jephem.util.Debug;

import tig.GeneralConstants;
import tig.TigBundle;

import java.util.Locale;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.text.html.*;

/******************************************************************
Contains static methods to get / set the global variables of JEphem application.
<BR>'Global variables' are variables that can be needed by several classes, and loaded once for all.

@author Thierry Graff
@history sep 16 2001 : Creation

@todo check if appDir works on all OSs (potential pb of FS ?)
@todo all paths depend on userDir. CHANGE (retrieve the value from a textFile) Change in initPaths
*****************************************************************/
public class GlobalVar implements GeneralConstants{

  //=================================================================================
  //                            PUBLIC CONSTANTS
  //=================================================================================

  // Constants for directories
  /** Constant designating the application directory (myPath/JEphem). */
  public static final int DIR_APP = 0;
  /** Constant designating the directory where application data are stored (myPath/JEphem/data). */
  public static final int DIR_DATA = 1;
  /** Constant designating the directory where internationalized strings are stored (myPath/JEphem/data/lang). */
  public static final int DIR_LANG = 2;
  /** Constant designating the directory where user preferences are stored (myPath/JEphem/data/prefs). */
  public static final int DIR_PREFS = 3;
  /** Constant designating the directory where sources are stored (myPath/JEphem/java/src). */
  public static final int DIR_SRC = 4;

  // Constants for Bundles
  /** Constant designating the bundle where the general internationalized strings are stored. */
  public static final int BUNDLE_GENERAL = 0;
  /** Constant designating the bundle where the menu internationalized strings are stored. */
  public static final int BUNDLE_MENUS = 1;
  /** Constant designating the bundle where the astronomy internationalized strings are stored. */
  public static final int BUNDLE_ASTRO = 2;

  //=================================================================================
  //                            PRIVATE CONSTANTS
  // Not directly declared final, because initialization done in separate methods
  //=================================================================================

  // ************ Paths ************
  // see getDir method for meaning of these variables
  private static String _appDir;
  private static String _dataDir;
  private static String _langDir;
  private static String _prefsDir;
  private static String _srcDir;
  // Initialization of path variables
  static { initPaths(); };

  // ************ Preferences ************
  private static JEphemPrefs _jephemPrefs;
  private static AstroPrefs _astroPrefs;
  // Initialization of preferences variables
  static { initPrefs(); };

  // ************ GUI ************
  private static MainFrame _mainFrame;
  private static JEditorPane _htmlPane;
//	private JEditorPane helpPane;
  private static JLabel _status;

  // ************ Internationalization ************
  private static Locale _locale;
  private static TigBundle _generalBundle;
  private static TigBundle _astroBundle;
  private static TigBundle _menusBundle;
  // Initialization of GUI variables
  static {
    try {
      initGUI();
    }
    catch (Exception ex) { Debug.traceError(ex);
    }
  };

  //=================================================================================
  //                            GET SET METHODS
  //=================================================================================

  //************************ Paths *****************************
  /** Returns one of the application directories.
  <BR>Warning : directory names are returned WITHOUT the trailing path separator.
  @param whichDir One of the available directories, expressed with one of the <CODE>DIR_XXX</CODE> constants.
  */
  public static String getDirectory(int whichDir){
    switch(whichDir){
      case DIR_APP :      return _appDir;
      case DIR_DATA :     return _dataDir;
      case DIR_LANG :     return _langDir;
      case DIR_PREFS :    return _prefsDir;
      case DIR_SRC :      return _srcDir;
      default : throw new IllegalArgumentException(whichDir + " doe not correspond to a valid directory.");
    }
  }// end getDirectory

  //************************ Preferences *****************************
  /** Returns the general preferences of JEphem. */
  public static JEphemPrefs getJEphemPrefs(){ return _jephemPrefs; }
  /** Returns the astronomical preferences of JEphem. */
  public static AstroPrefs getAstroPrefs(){ return _astroPrefs; }

  /** Returns the current <CODE>Locale</CODE>.
  <BR>Facility method (current Locale can be accessed through {@link #getJEphemPrefs()}). */
  public static Locale getLocale(){ return _locale; }

  /** Returns the current language, using ISO-639 language code (lower case two-letter code).
  <BR>Facility method (current language can be accessed through {@link #getJEphemPrefs()}). */
  public static String getLang(){
    return _jephemPrefs.getProperty(JEphemPrefs.KEY_LANG);
  }

  /** Returns the implementation used to perform the astronomical computations. */
  public static String getAstroEngine(){
    if(((String)_astroPrefs.getProperty(AstroPrefs.KEY_EPHEMERIS_IMPLEMENTATION)).equals(AstroEngine.JEPHEM))
      return AstroEngine.JEPHEM;
    else return AstroEngine.SWISS_EPHEMERIS;
  }// end getAstroEngine

  /** Reloads global variables depending on the properties. */
  public static void reloadData(){
    initPaths();
    initPrefs();
  }// end reloadData

  //************************ GUI ************************
  /** Returns the top-level frame of the Application. */
  public static MainFrame getMainFrame(){ return _mainFrame; }
  /** Sets the top-level frame of the Application. */
  static void setMainFrame(MainFrame mainFrame){ _mainFrame = mainFrame; } // package acces

  /** Returns the HTML display area of the application. */
  public static JEditorPane getHTMLPane(){ return _htmlPane; }
  /** This method is public as a implementation side effect. Sets the HTML display area of the application. */
  public static void setHTMLPane(JEditorPane htmlPane){ _htmlPane = htmlPane; }

  //************************ Internationalization ************************
  /** Returns the Bundle containing internationalized general astronomical terms.
  @param whichBundle One of the available ressource bundle, expressed with one of the <CODE>BUNDLE_XXX</CODE> constants.
  */
  public static TigBundle getBundle(int whichBundle){
    switch(whichBundle){
      case BUNDLE_GENERAL :      return _generalBundle;
      case BUNDLE_MENUS :        return _menusBundle;
      case BUNDLE_ASTRO :        return _astroBundle;
      default : throw new IllegalArgumentException(whichBundle + " does not correspond to a valid bundle.");
    }
  }// end getBundle

  //************************ Status *****************************
  /**	Returns the status bar (generally on bottom of main window). */
  public static JLabel getStatus() { return _status; }
  /**	Returns the text of the status bar (generally on bottom of main window). */
  public static String getStatusText() { return _status.getText(); }
  /** Permits to modify the text of the status bar (generally on bottom of main window).
  @param newStatus New value of the status bar's text. */
  public static void setStatusText(String newStatusText){ _status.setText(newStatusText); }

  //=================================================================================
  //                            INITIALIZATION METHODS
  //=================================================================================

  /** Initializes paths */
  private static void initPaths(){
    try{
//      java.util.Properties p = new java.util.Properties();
//      p.load(new FileInputStream(new File("Directories.prefs")));
//      p.list(System.out);
      String userDir=System.getProperty("user.dir");
      _appDir = userDir.substring(0,userDir.lastIndexOf("java") - 1);
//	System.out.println("_appDir = " + _appDir );
      _dataDir = _appDir + FS + "data";
      _srcDir = _appDir + FS + "java" + FS + "src";
      _prefsDir = _appDir + FS + "data" + FS + "prefs";
      _langDir = _appDir + FS + "data" + FS + "lang";
    }
    catch(Exception e){
      System.out.println("jephem.GlobalVar : Problem during path initialization");
      e.printStackTrace();
    }
  }// end initPaths

  /** Initializes settings - must be called after initPath()*/
  private static void initPrefs(){
    _jephemPrefs = new JEphemPrefs();
    _astroPrefs = new AstroPrefs();
  }// end initPrefs

  /** Initializes GUI (components and internationalization stuff) - must be called after initPrefs() */
  private static void initGUI(){
    _status =  new JLabel(BLANK);

    // Internationalization
    try{
      _locale = new Locale(_jephemPrefs.getProperty(JEphemPrefs.KEY_LANG),
                           _jephemPrefs.getProperty(JEphemPrefs.KEY_COUNTRY));
      _astroBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "Astro.lang", GlobalVar.getLocale());
      _generalBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "General.lang", GlobalVar.getLocale());
      _menusBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "Menus.lang", GlobalVar.getLocale());
    }
    catch(IOException ioe){
      Debug.traceError(ioe);
    }

  }// end initGUI

}// end class GlobalVar
