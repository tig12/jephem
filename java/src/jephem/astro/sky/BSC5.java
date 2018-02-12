//*********************************************************************************
// class jephem.astro.sky.BSC5
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.astro.sky;

import jephem.util.Debug;

import tig.GeneralConstants;
import tig.Exceptions;

import java.io.*;
/******************************************************************************
Low-level class to access to BSC5 (Bright Star Catalog, version 5) data.
<BR>Header of readMe of the present version :
<PRE>
================================================================================
The Bright Star Catalogue,  5th Revised Ed. (Preliminary Version)
     Hoffleit E.D., Warren Jr. W.H.
================================================================================
</PRE>

@author Thierry Graff.
@history apr 21 2002 : Creation

@todo WARNING when implementing new data - no parameter checking in setStringData() and setDoubleData()
*********************************************************************************/
public abstract class BSC5 implements GeneralConstants{

  //=================================================================================
  //                   STATIC VARIABLES
  //=================================================================================

  /** Path to the directory where BSC5 files are stored. */
  private static String _dataPath = null;
  /** Indicates if the data are already loaded. */
  private static boolean _dataLoaded = false;

  // Arrays to store the variables.
  private static double[][] _doubleData;
  private static String[][] _stringData;

  //=================================================================================
  //                     PRIVATE CONSTANTS
  //=================================================================================

  /** Name of the file containing the data. */
  private static final String DATA_FILENAME = "BSC5.txt";

  //=================================================================================
  //                     PUBLIC CONSTANTS
  //=================================================================================
  /** Number of stars in the catalog. */
  public static final int NB_STARS = 9096;


  /************** Constant group ***********************
  <constGroup title="Data codes"
              desc="Codes identifying the different fields given by BSC5">
    <comment value="Correspond to their indexes in the different arrays."/>
  */

    /** <subGroup name="String data">*/
  private static final int STRING_DATA_BASE = 0;
  private static final int STRING_DATA_MAX = 3;
  /** Constant to access to BSC number (= Harvard Revised Number). */
  public static final int BSC = 0;
  /** Constant to access to HD catalog number. */
  public static final int HD = 1;
  /** Constant to access to SAO catalog number. */
  public static final int SAO = 2;
  /** Constant to access to spectral type. */
  public static final int TYP = 3;
  /** </subGroup>*/

  /**   <subGroup name="double data">*/
  private static final int DOUBLE_DATA_BASE = 100;
  private static final int DOUBLE_DATA_MAX = 103;
  /** Constant to access to r.a. 2000. */
  public static final int RA = 100;
  /** Constant to access to dec. 2000. */
  public static final int DEC = 101;
  /** Constant to access to visual magnitude. */
  public static final int MAG = 102;
  /** Constant to access to parallax. */
  public static final int PAR = 103;
  /**   </subGroup>*/

  private final static String[] DATA_LABELS = { "BSC number", "HD number", "SAO number", "Sectral type",
                                                "ra", "dec", "magnitude", "parallax" };
  /** </constGroup> <!-- end Data codes -->*/

  //=================================================================================
  //                                 GET / SET METHODS
  //=================================================================================

  //******************* setDataPath *************
  /** Sets the path where the BSC5 data are located ; MUST be called before calling any other
  method of this class. */
  public static void setDataPath(String dataPath){
    _dataPath = dataPath;
  }// end setDataPath

  //******************* getDataLabel *************
  /** Returns the English label of the specified data.
  @param dataCode constant of this class designating one of the BSC5 data.
  */
  public static String getDataLabel(int dataCode){
    if(dataCode < DOUBLE_DATA_BASE) // if String data
      return DATA_LABELS[dataCode - STRING_DATA_BASE];
    else
      return DATA_LABELS[dataCode - DOUBLE_DATA_BASE];
  } // end getDataLabel

  //******************* getStringData *************
  /** Returns a String data for a particular star.
  @param dataCode constant of this class designating one of the BSC5 String data.
  */
  public static String getStringData(int starIdx, int dataCode){
    if(!_dataLoaded) loadData();
    if(dataCode < STRING_DATA_BASE || dataCode > STRING_DATA_MAX)
      throw new IllegalArgumentException("dataCode " + dataCode + " does not correspond to a String data.");
    if(starIdx < 0 || starIdx >= NB_STARS)
      throw new IllegalArgumentException("starIdx " + starIdx + " does not correspond to a valid star.");
    return _stringData[starIdx][dataCode - STRING_DATA_BASE];
  } // end getStringData

  //******************* getDoubleData *************
  /** Returns a String data for a particular star.
  @param dataCode constant of this class designating one of the BSC5 double data.
  */
  public static double getDoubleData(int starIdx, int dataCode){
    if(!_dataLoaded) loadData();
    if(dataCode < DOUBLE_DATA_BASE || dataCode > DOUBLE_DATA_MAX)
      throw new IllegalArgumentException("dataCode " + dataCode + " does not correspond to a double data.");
    if(starIdx < 0 || starIdx >= NB_STARS)
      throw new IllegalArgumentException("starIdx " + starIdx + " does not correspond to a valid star.");
    return _doubleData[starIdx][dataCode - DOUBLE_DATA_BASE];
  } // end getDoubleData

  //=================================================================================
  //                                 PRIVATE METHODS
  //=================================================================================

  //******************* setStringData *************
  private static void setStringData(int starIdx, int dataCode, String value){
    // No parameter checking - dataCode between 0 and 3
    _stringData[starIdx][dataCode - STRING_DATA_BASE] = value;
  } // end setStringData

  //******************* setDoubleData *************
  private static void setDoubleData(int starIdx, int dataCode, double value){
    // No parameter checking - dataCode between 0 and 3
    _doubleData[starIdx][dataCode - DOUBLE_DATA_BASE] = value;
  } // end setdoubleData

  //******************* loadData() *************
  /**
  @pre _dataPath != null
  */
  private static void loadData(){
    if(_dataPath == null){
      System.out.println("setDataPath must be called before accessing to BSC5 data.");
      System.exit(0);
    }
    _dataLoaded = true;
    int i = 0;
    try{
      File f = new File(_dataPath + FS + DATA_FILENAME);
      LineNumberReader lnr = new LineNumberReader(new FileReader(f));

      _stringData = new String[NB_STARS][4];
      _doubleData = new double[NB_STARS][4];

      String line, strTmp;

      for (i = 0; i < NB_STARS; i++){
        line = lnr.readLine(); //System.out.println("i = " + i + " - line = " + line);
        setStringData(i, BSC, line.substring(0, 4)); //System.out.println("BSC = " + getStringData(i, BSC));
        setStringData(i, HD, line.substring(4, 10)); //System.out.println("HD = " + getStringData(i, HD));
        setStringData(i, SAO, line.substring(10, 16)); //System.out.println("SAO = " + getStringData(i, SAO));
        setStringData(i, TYP, line.substring(38, 58)); //System.out.println("TYP = " + getStringData(i, TYP));
//BHSRDMTP
        setDoubleData(i, RA, Double.parseDouble(line.substring(16, 24).trim())); //System.out.println("RA = " + getDoubleData(i, RA));
        setDoubleData(i, DEC, Double.parseDouble(line.substring(24, 33).trim())); //System.out.println("DEC = " + getDoubleData(i, DEC));
        setDoubleData(i, MAG, Double.parseDouble(line.substring(33, 38).trim())); //System.out.println("MAG = " + getDoubleData(i, MAG));
        strTmp = line.substring(58).trim();
        setDoubleData(i, PAR, (strTmp.equals(BLANK) ? 0 : Double.parseDouble(strTmp))); //System.out.println("PAR = " + getDoubleData(i, PAR));
      }
    }
    catch(NumberFormatException nfe){
      System.out.println("============== BSC5.loadData() ========= recoverable.");
      Debug.sendError(nfe);
      System.exit(0);
    }
    catch(Exception e){
      System.out.println("Problem when trying to load BSC5 data.");
      Debug.sendError(e);
      System.exit(0);
    }

  }// end loadData

  //******************* traceStaticVariables() *************
  /** Traces the data of the specified stars.
  @param beg The begin star index to trace (0 based).
  @param end The end star index to trace (0 based).
  */
  private static void traceStaticVariables(int beg, int end){
    String display;
    System.out.println("  ====  ");
    int j;
    for(int i = beg; i < end; i++) {
      for(j = STRING_DATA_BASE; j < STRING_DATA_MAX; j++){
        System.out.println(getDataLabel(j) + " : " + getStringData(j, i));
      }
      for(j = DOUBLE_DATA_BASE; j < DOUBLE_DATA_MAX; j++){
        System.out.println(getDataLabel(j) + " : " + getDoubleData(j, i));
      }
    }
  }// end traceStaticVariables

} //end class BSC5