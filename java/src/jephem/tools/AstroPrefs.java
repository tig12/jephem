//*********************************************************************************
// class jephem.tools.AstroPrefs
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.tools;

import jephem.astro.AstroEngine;

import jephem.GlobalVar;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.solarsystem.SolarSystemConstants;

import tig.TigProperties;
import tig.GeneralConstants;
import tig.Strings;

import java.io.*;

/******************************************************************************
Handles general astronomy preferences.
@author Thierry Graff
@history Sep 22 2001 : creation.

@todo Internationalize Exception messages.
*********************************************************************************/
public class AstroPrefs extends TigProperties implements GeneralConstants, SolarSystemConstants, SpaceConstants{

  //=================================================================================
  //                            PUBLIC CONSTANTS
  //=================================================================================

  // ******* Access to the different preferences. *******
  /** Constant to use as a key to access the 'precision' property of these preferences. */
  public static final String KEY_PRECISION = "precision";
  /** Constant to use as a key to access to the 'currentBodies' property of these preferences. */
  public static final String KEY_CURRENT_BODIES = "currentBodies";
  /** Constant to use as a key to access to the 'availableFrames' property of these preferences. */
  public static final String KEY_AVAILABLE_FRAMES = "availableFrames";
  /** Constant to use as a key to access to the 'ephemImplementation' property of this preference. */
  public static final String KEY_EPHEMERIS_IMPLEMENTATION = "ephemImplementation";

  //=================================================================================
  //                            PRIVATE CONSTANTS
  //=================================================================================
  /** Path and file name of the file containing the data from <CODE>GlobalVar.getDirectory(GlobalVar.DIR_PREFS)</CODE> */
  private final static String PREFS_PATH = GlobalVar.getDirectory(GlobalVar.DIR_PREFS) + FS + "jephem" + FS + "astro" + FS + "Astro.prefs";

  /** Default String written if available frames can't be loaded. */
  private final static String  DEFAULT_AVAILABLE_FRAMES = "FRAME_EC_HELIO, FRAME_ECLIPTIC, FRAME_EQUATORIAL, FRAME_THEORY";
  /** Default String written if current bodies can't be loaded. */
  private final static String  DEFAULT_CURRENT_BODIES = "SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN, URANUS, NEPTUNE, PLUTOY";

  //=================================================================================
  //                                      CONSTRUCTORS
  //=================================================================================
  /** Unique constructor ; if the file containing the preferences can't be read, uses hard-coded default values.
  */
  public AstroPrefs(){
    super();
    try{
      this.load(new FileInputStream(new File(PREFS_PATH)));

      // Retrieving from storage may be only partially done
      // This would not throw an Exception, but need to set to default.
      Object o;
      if (this.getProperty(KEY_PRECISION) == null){
        o = this.setProperty(KEY_PRECISION, "0");
        System.out.println("AstroPrefs constructor - Precision not loaded - set to default value");
      }
      if (this.getProperty(KEY_CURRENT_BODIES) == null){
        o = this.setProperty(KEY_CURRENT_BODIES, DEFAULT_CURRENT_BODIES);
        System.out.println("AstroPrefs constructor - Current bodies not loaded - set to default value");
      }
      if (this.getProperty(KEY_EPHEMERIS_IMPLEMENTATION) == null){
        o = this.setProperty(KEY_EPHEMERIS_IMPLEMENTATION, AstroEngine.JEPHEM);
        System.out.println("AstroPrefs constructor -  not loaded - set to default value");
      }
      if (this.getProperty(KEY_AVAILABLE_FRAMES) == null){
        o = this.setProperty(KEY_AVAILABLE_FRAMES, DEFAULT_AVAILABLE_FRAMES);
        System.out.println("AstroPrefs constructor - Available frames not loaded - set to default value");
      }
    }
    catch(IOException ioe){
      // load default values
      this.setToDefault();
      // text in status bar ("prefs couldn't be loaded - default values used").
      // save a new prefs file with the default ones.
    }
  }// end EphemerisPrefs

  //=================================================================================
  //                                 PUBLIC METHODS
  //=================================================================================
  /** Stores these preferences in the appropriate file with the appropriate header. */
  public void store(){
    String header = " ********************************************************************" + LS
                  + "# JEphem preferences file - Astro.prefs" + LS
                  + "# ********************************************************************" + LS;
    try {
      this.store(new FileOutputStream(new File(PREFS_PATH)), header);
    }
    catch (Exception ex) {
      System.out.println("AstroPrefs.store() - storage couldn't be done correctly");
    }
  }// end store

  /** This method has been overriden because some properties are expressed with constant names and can
  be retrieved using reflection through <CODE>TigProperties.getIntConstant</CODE>.
  */
  public int[] getIntArrayProperty(String key){
      try{
      String[] values = Strings.stringToStringArray(this.getProperty(key));
      int[] res = new int[values.length];

      if (key.equals(KEY_CURRENT_BODIES)){
        for (int i = 0; i < values.length; i++){
          res[i] = TigProperties.getIntConstant(values[i], "jephem.astro.solarsystem.SolarSystemConstants");
        }
        return res;
      }
      if (key.equals(KEY_AVAILABLE_FRAMES)){

//for (int i = 0; i< values.length; i++) System.out.println("values[i] : " + values[i]);
        for (int i = 0; i < values.length; i++){
          res[i] = TigProperties.getIntConstant(values[i], "jephem.astro.spacetime.SpaceConstants");
        }
        return res;
      }
      else throw new RuntimeException("Property not found");
    }
    catch(Exception e){
      throw new RuntimeException(e.toString());
    }
  }// end getIntArrayProperty

  //=================================================================================
  //                                 PRIVATE METHODS
  //=================================================================================

  /** Sets the properties to their default values. */
  private void setToDefault(){
System.out.println("AstroPrefs.setToDefaults()");
    Object o;
    o = this.setProperty(KEY_PRECISION, "0");
    o = this.setProperty(KEY_CURRENT_BODIES, DEFAULT_CURRENT_BODIES);
    o = this.setProperty(KEY_AVAILABLE_FRAMES, DEFAULT_AVAILABLE_FRAMES);
    o = this.setProperty(KEY_EPHEMERIS_IMPLEMENTATION, AstroEngine.JEPHEM);
  }

}//end class EphemerisPrefs
