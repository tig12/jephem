//*********************************************************************************
// class jephem.JEphemPrefs
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem;

import jephem.GlobalVar;
import tig.GeneralConstants;
import tig.TigProperties;
import tig.Strings;
import java.util.Locale;
import java.util.Properties;
import java.io.*;

/******************************************************************************
Class permitting to access to the general preferences of JEphem.
<BR>At program startup, an instance is loaded, and can be accessed through <CODE>jephem.GlobalVar.getJEphemPrefs()</CODE>.
<BR><B>Example of use</B> :
<PRE>
JEphemPrefs prefs = new JEphemPrefs();
String lang = (String)prefs.getJEphemPrefs().get(JEphemPrefs.KEY_LANG);
</PRE>

@author Thierry Graff
@history sep 18 2001 : creation.
@history oct 13 2001 : removed XML storage to simple properties management.

@todo send better message when values are retrieved from defaults
*********************************************************************************/
public class JEphemPrefs extends TigProperties implements GeneralConstants{

  //=================================================================================
  //                            CONSTANTS
  //=================================================================================

  /** Path and file name of the file containing the data. */
  private final static String PREFS_PATH = GlobalVar.getDirectory(GlobalVar.DIR_PREFS) + FS + "jephem" + FS + "JEphem.prefs";

  // ******* Access to the different preferences. *******

  /** Constant to use as a key to access to the 'language' property of this preference - {@value lang}. */
  public static final String KEY_LANG = "lang";
  /** Constant to use as a key to access to the 'country' property of this preference. */
  public static final String KEY_COUNTRY = "country";
  /** Constant to use as a key to access to the 'look and feel' property of this preference. */
  public static final String KEY_LAF = "lookAndFeel";

  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================
  /** Unique constructor. */
  public JEphemPrefs(){
    super();
    try{
      //System.out.println("loading " + PREFS_PATH);
      this.load(new FileInputStream(new File(PREFS_PATH)));

      // Retrieving from storage may be only partially done
      // This would not throw an Exception, but need to set to default.
      Object o;

      String lang = this.getProperty(KEY_LANG);
      if (lang == null){
        o = this.setProperty(KEY_LANG, "en");
        System.out.println("JEphemPrefs constructor - Lang not loaded - set to default value");
      }
      else if(lang.equals("default")){
        // check if "default" correspond to an available language
        Properties p = new Properties();
        p.load(new FileInputStream(new File(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "availableLanguages.txt")));
        String[] availableLanguages = Strings.stringToStringArray(p.getProperty("availableLanguages"));
        Locale loc = Locale.getDefault();
        String defaultLang = loc.getLanguage();
        boolean defaultSupported = false;
        for (int i = 0; i < availableLanguages.length; i++){
          if(availableLanguages[i].equals(defaultLang)) defaultSupported = true;
        }
        if(defaultSupported){
          o = this.setProperty(KEY_LANG, defaultLang);
          o = this.setProperty(KEY_COUNTRY, loc.getCountry());
        }
        else{
          o = this.setProperty(KEY_LANG, "en");
          o = this.setProperty(KEY_COUNTRY, BLANK);
          System.out.println("JEphemPrefs constructor - System language not supported - set 'lang' and 'country' to default value");
        }
      }

      if (this.getProperty(KEY_COUNTRY) == null){
        o = this.setProperty(KEY_COUNTRY, BLANK);
        System.out.println("JEphemPrefs constructor - Country not loaded - set to default value");
      }

      if (this.getProperty(KEY_LAF) == null){
        System.out.println("JEphemPrefs constructor - LAF not loaded - set to default value");
        o = this.setProperty(KEY_LAF, "LAF_SYSTEM");
      }
    }
    catch(IOException ioe){
      // load default values
      this.setToDefault();
      // text in status bar ("prefs couldn't be loaded - default values used").
      // save a new prefs file with the default ones.
    }
  }// end constructor

  //=================================================================================
  //                            PUBLIC METHODS
  //=================================================================================
  /** Stores these preferences in the appropriate file with the appropriate header. */
  public void store(){
    String header = " ********************************************************************" + LS
                  + "# JEphem preferences file - JEphem.prefs" + LS
                  + "# ********************************************************************" + LS;
    try {
      this.store(new FileOutputStream(new File(PREFS_PATH)), header);
    }
    catch (Exception ex) {
      System.out.println("JEphemPrefs.store() - storage couldn't be done correctly");
    }
  }// end store

  //=================================================================================
  //                                 PRIVATE METHODS
  //=================================================================================

  /** Sets the properties to their default values. */
  private void setToDefault(){
    System.out.println("JEphemPrefs.setToDefaults()");
    Object o;
    o = this.setProperty(KEY_LANG, "en");
    o = this.setProperty(KEY_COUNTRY, BLANK);
    o = this.setProperty(KEY_LAF, "LAF_SYSTEM");
  }// end setToDefault

} // end class JEphemPrefs
