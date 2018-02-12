//*********************************************************************************
// class jephem.astro.AstroEngine
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro;

/******************************************************************************
Contains global variables used by classes of <CODE>jephem.astro</CODE> package.

@author Thierry Graff
@history jun 10 2002 : Creation
*********************************************************************************/
public abstract class AstroEngine{

  //=================================================================================
  //                                 CONSTANTS
  //=================================================================================
  /** Constant used to designate JEphem as the astro engine. */
  public static final String JEPHEM = "JEphem";
  /** Constant used to designate Swiss Ephemeris as the astro engine. */
  public static final String SWISS_EPHEMERIS = "SwissEphemeris";

  //=================================================================================
  //                            STATIC VARIABLES
  //=================================================================================

  /** Implementation used for the ephemeris computations. */
  private static String _ephemerisImplementation;
  private static double _prec;
  private static int[] _posUnits;
  private static int[] _velUnits;
  private static int _sphereCart;
  private static int defaultFrame;

  // TEMP CODE
  static{

  };
  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================
  /** Not used as this class is abstract. */
  public AstroEngine(){}

  //=================================================================================
  //                                          METHODS
  //=================================================================================

  /** Permits to indicate which implementation to use for the computations.
  <BR>If not set, JEphem is used.
  @param astroEngine Use constants of this class to specify it.
  */
  public static void setAstroEngine(String ephemerisImplementation){ _ephemerisImplementation = ephemerisImplementation; }


}//end class AstroEngine