//*********************************************************************************
// abstract class jephem.astro.spacetime.Units
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;

import jephem.astro.spacetime.UnitsConstants;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.Body;
import jephem.astro.solarsystem.SolarSystemConstants;

import tig.Formats;
import tig.GeneralConstants;
import tig.maths.Maths;
import tig.maths.Vector3;

/*********************************************************************************
Contains methods to format and transform units and coordinates.
<BR>Realted constants are located in {@link UnitsConstants}
<BR>Methods of this class permit to get labels associated with coordinates. They are normally
retrieved from 'properties' files, for internationalization ; if a problem occurs, hard coded English
labels are used.
@author Thierry Graff
@history aug 08 2001 : Creation
@history jan 09 2002 : Changed unitGroups to int[] ; removed unused methods
@history jan 22 2002 : introduced unit types and reorganized consequently
@history jan 24 2002 : Wrote conversion mechanism

@todo try - catch in getLabel methods (if ArrayOutOfBoundsException, the message would be more explicit)
**********************************************************************************/
public abstract class Units implements GeneralConstants, SolarSystemConstants, SpaceConstants, UnitsConstants{

  //=================================================================================
  //                             PUBLIC CONSTANTS
  //=================================================================================
  //=================================================================================
  //                             PRIVATE CONSTANTS
  //=================================================================================

  /** Array containing unit labels. */
  private static final String[][] UNIT_LABELS = {
    // Distance units - Order in this array must correspond to DISTANCE_UNIT_XXX constants.
    { "a.u.",
      "km",
      "m"
    },
    // Linear speed units - Order in this array must correspond to LINEAR_SPEED_UNIT_XXX constants.
    { "a.u./d",
      "km/d",
      "km/h",
      "m/s"
    },
    // Angular units - Order in this array must correspond to ANGULAR_UNIT_XXX constants.
    { "arcsec",
      "deg",
      "rad"
    },
    // Angular speed units - Order in this array must correspond to ANGULAR_SPEED_UNIT_XXX constants.
    { "arcsec/s",
      "deg/s",
      "arcsec/d",
      "deg/d",
      "rad/d"
    }
  };

  /** Conversion tables
  <BR>Used to convert a quantity expressed from unit1 to unit2.
  <BR>To use only if unit1 and unit2 belong to the same unitType.
  <BR>To convert unit1 to unit2, use CONV_XXX[unitType][unit1][unit2]
  <BR>usage : coord2 = coord1 * CONV_XXX[unitType][unit1][unit2]
  <BR>There are redundancies in the tables as table[unitType][i][j] = 1/table[unitType][j][i]
  */
  private static final double[][][] CONVERSIONS = {
    // Conversions for distance units
    {
      {1.0,             KM_PER_AU, KM_PER_AU*1000.0},
      {1.0/KM_PER_AU,   1.0,       1000.0},
      {0.001/KM_PER_AU, 0.001,     1.0}
    },
    // Conversions for linear speed units
    {
      {1.0,            KM_PER_AU, KM_PER_AU/24, KM_PER_AU/86.4},
      {1.0/KM_PER_AU,  1.0,       1.0/24.0,     1.0/86.4},
      {24.0/KM_PER_AU, 24.0,      1.0,          1.0/3.6},
      {86.4/KM_PER_AU, 86.4,      3.6,          1.0}
    },
    // Conversions for angular units
    {
      {1.0,                 1.0/3600.0,    Maths.ARCSEC_TO_RAD},
      {3600.0,              1.0,           Math.PI/180.0},
      {Maths.RAD_TO_ARCSEC, 180.0/Math.PI, 1.0}
    },
    // Conversions for angular speed units
    {
      {1.0,                         1.0/3600.0,            86400.0,             24.0,          Maths.ARCSEC_TO_RAD*86400.0},
      {3600.0,                      1.0,                   3600.0*86400.0,      86400.0,       Math.PI/180.0*86400.0},
      {1.0/86400.0,                 1.0/(3600.0*86400.0),  1.0,                 1.0/3600.0,    Maths.ARCSEC_TO_RAD},
      {1.0/24.0,                    1.0/86400.0,           3600.0,              1.0,           Math.PI/180.0},
      {Maths.RAD_TO_ARCSEC/86400.0, 180.0/Math.PI/86400.0, Maths.RAD_TO_ARCSEC, 180.0/Math.PI, 1.0}
    }
  };

  //=================================================================================
  //                                 PUBLIC METHODS
  //=================================================================================

  //***************************** getUnits() ************************************
  /** Returns the constants expressing the units of a certain type.
  <BR>The returned array matches the array returned by {@link #getUnitLabels(int)}.
  @param type The type of units, expressed using the TYPE_XXX constants of this class.
  */
  public static int[] getUnits(int unitType){
    // retrieve the nb of units of this type through UNIT_LABELS
    int len = UNIT_LABELS[unitType].length;
    int[] res = new int[len];
    for(int i = 0; i < len; i++){
      res[i] = unitType*DELTA_BETWEEN_BASES + i;
    }
    return res;
  }//end getUnit

  //***************************** getUnitType ************************************
  /** Returns the type of unit depending on coordinate expression (spherical / cartesian)
  and the concerned coordinate (X0, X1, X2, V0, V1, V2).
  <BR>Ex : <CODE>getUnitType(SPHERICAL, COORD_X0)</CODE> returns <CODE>TYPE_DISTANCE</CODE>.
  @param coordExpr Coordinate expression (use {@link SpaceConstants#SPHERICAL} or {@link SpaceConstants#CARTESIAN}).
  @param whichCoord The concerned coordinate (use {@link SpaceConstants}.<CODE>COORD_XX</CODE> constants).
  */
  public static int getUnitType(int coordExpr, int whichCoord){
    if(coordExpr == CARTESIAN){
      switch(whichCoord){
        case COORD_X0: case COORD_X1: case COORD_X2: return TYPE_DISTANCE;
        case COORD_V0: case COORD_V1: case COORD_V2: return TYPE_LINEAR_SPEED;
        default: throw new IllegalArgumentException("Parameter 'whichCoord' must be 'X0', 'X1', 'X2', 'V0', 'V1' or 'V2'.");
      }
    }
    else if(coordExpr == SPHERICAL){
      switch(whichCoord){
        case COORD_X0: return TYPE_DISTANCE;
        case COORD_X1: case COORD_X2: return TYPE_ANGULAR;
        case COORD_V0: return TYPE_LINEAR_SPEED;
        case COORD_V1: case COORD_V2: return TYPE_ANGULAR_SPEED;
        default: throw new IllegalArgumentException("Parameter 'whichCoord' must be 'X0', 'X1', 'X2', 'V0', 'V1' or 'V2'.");
      }
    }
    else
      throw new IllegalArgumentException("Parameter 'coordExpr' must be 'CARTESIAN' or 'SPHERICAL'.");
  }//end getUnitType

  //***************************** getUnitLabels() ************************************
  /** Returns the English labels of units of a certain type.
  @param type The type of units, expressed using the TYPE_XXX constants of this class.
  */
  public static String[] getUnitLabels(int unitType){
    return UNIT_LABELS[unitType];
  }//end getUnitLabels


  //***************************** getUnitLabel() ************************************
  /** Returns the English label of a unit.
  <BR>The returned array matches the array returned by {@link #getUnits(int)}.
  @param type The unit, expressed using the COORD_XXX constants of this class.
  */
  public static String getUnitLabel(int unit){
    int type = getUnitType(unit);
    return UNIT_LABELS[type][unit - DELTA_BETWEEN_BASES*type];
  }// end getUnitLabel

  //***************************** convertUnits(Vector3) ************************************
  /** Method equivalent to {@link #convertUnits(double[],int[],int[])},
  using <CODE>Vector3</CODE> instead of <CODE>double</CODE>.
  */
  public static Vector3 convertUnits(Vector3 coords, int[] units1, int[] units2){
    double[] array = {coords.x0, coords.x1, coords.x2};
    return new Vector3(convertUnits(array, units1, units2));
  }// end convertUnits(Vector3)

  //***************************** convertUnits(double) ************************************
  /** Converts coordinates expressed with 'units1' to coordinates expressed with 'units2'.
  @param coords The coordinates to convert, expressed with 'units1'.
  @param units1 The units used to express 'coords'.
  @param units2 The units used to express the returned coordinates.
  @return The coordinates expressed with 'units2'.
  @throws IllegalArgumentException if :
  <BR>1 - the sizes of arrays 'units1' and 'units2' are different from the size of 'coords'.
  <BR>2 - an attempt is made to perform a transformation between units of different types
  (if each elements of 'units1' is not coherent with the corresponding element of 'units2'
  - for example converting from an angular unit to a linear unit).
  */
  public static double[] convertUnits(double[] coords, int[] units1, int[] units2){
    // Parameters checking
    int len = coords.length;
    int i;
    if (units1.length != len)
      throw new IllegalArgumentException("Parameters 'units1' not coherent with parameter 'coords'");
    if (units2.length != len)
      throw new IllegalArgumentException("Parameters 'units2' not coherent with parameter 'coords'");
    // Now we are sure that coords, units1 and units2 have the same length.
    for (i = 0; i < len; i++){
      if (units1[i] == NO_SPECIF || units2[i] == NO_SPECIF) continue;
      if (getUnitType(units1[i]) != getUnitType(units2[i])){
        String tmp = "Conversion can be done only between units of the same type (ex : angular to angular)" + LS;
        tmp += "Attempt : ";
        for (int j = 0; j < len; j++) tmp += units1[j] + " -> " + units2[j] + "  ";
        throw new IllegalArgumentException(tmp);
      }
    }
    // Now we are sure that the conversion between units1[i] and units2[i] is coherent
    double[] res = new double[len];
    int a, b, type;
    for (i = 0; i < len; i++){
      // if one of the unit is not specified, the coordinate is set to NaN
      if (units1[i] == NO_SPECIF || units2[i] == NO_SPECIF){
        res[i] = Double.NaN;
      }
      else{
        type = getUnitType(units1[i]); // = getUnitType(units2[i])
        a = units1[i] - type*DELTA_BETWEEN_BASES;
        b = units2[i] - type*DELTA_BETWEEN_BASES;
        res[i] = coords[i]*CONVERSIONS[type][a][b];
      }
    }
    return res;
  }// end convertUnits

  //=================================================================================
  //                                 PRIVATE METHODS
  //=================================================================================
  // Warning : the code to find the correspondance supposes a correlation between
  // the values of UNIT_XXX and TYPE_XXX constants.
  private static int getUnitType(int unit){
    return (int)(Math.floor((double)unit/(double)DELTA_BETWEEN_BASES));
  } // getUnitType

  //=================================================================================
  //=================================================================================
  //                                      TESTS
  //=================================================================================
  //=================================================================================
/*
  // **************** For tests only ****************
  public static void main(String[] args){
    // no complete argument checking
    if(args[0].equalsIgnoreCase("testUnitType"))
      testUnitType();
    else if(args[0].equalsIgnoreCase("testConversion"))
      testConversion(args[1]);
    else if(args[0].equalsIgnoreCase("testGetUnitLabel"))
      testGetUnitLabel(args[1]);
    else
      System.out.println("first argument must be 'testUnitType' or 'testConversion' " +
      "or 'testGetUnitLabel'");
  }// end main

  // **************** For tests only ****************
  private static void testGetUnitLabel(String strUnit){
    int unit = Integer.parseInt(strUnit);
    System.out.println(strUnit + " : " + getUnitLabel(unit));
  }

  // **************** For tests only ****************
  // Simulates calls to convertUnits
  private static void testConversion(String strType){
    int type = Integer.parseInt(strType);
    String[] labels = getUnitLabels(type);
    int i, j, k;
    int len = labels.length;
    int paramLen = len*len;
    // parameters passed to convertUnits
    double[] coords = new double[paramLen];
    int[] units1 = new int[paramLen];
    int[] units2 = new int[paramLen];
    // build the parameters
    for (i = 0; i < len; i++){
      for (j = 0; j < len; j++){
        k = len*i + j ;
        coords[k] = 1.0;
        units1[k]= DELTA_BETWEEN_BASES*type + i;
        units2[k]= DELTA_BETWEEN_BASES*type + j;
        //System.out.println(k + " : " + getUnitLabel(units1[k]) + " - " + getUnitLabel(units2[k]));
      }
    }
    //System.exit(0);
    // call to convertUnits
    double[] res = convertUnits(coords, units1, units2);
    // display results
    for (k = 0; k < paramLen; k++){
      System.out.println(k + " : " + getUnitLabel(units1[k]) + " ==> "
                         + getUnitLabel(units2[k]) + " : " + res[k]);
    }
  }// end testConversion

  // **************** For tests only ****************
  private static void testUnitType(){
    System.out.println("testUnitType");
    System.out.println("LINEAR_SPEED_UNIT_AU_PER_D : " + getUnitType(LINEAR_SPEED_UNIT_AU_PER_D));

    System.out.println("DISTANCE_UNIT_AU : " + getUnitType(DISTANCE_UNIT_AU));
    System.out.println("DISTANCE_UNIT_KM : " + getUnitType(DISTANCE_UNIT_KM));
    System.out.println("DISTANCE_UNIT_M : " + getUnitType(DISTANCE_UNIT_M));
    System.out.println("\n");
    System.out.println("LINEAR_SPEED_UNIT_AU_PER_D : " + getUnitType(LINEAR_SPEED_UNIT_AU_PER_D));
    System.out.println("LINEAR_SPEED_UNIT_KM_PER_D : " + getUnitType(LINEAR_SPEED_UNIT_KM_PER_D));
    System.out.println("LINEAR_SPEED_UNIT_KM_PER_HOUR : " + getUnitType(LINEAR_SPEED_UNIT_KM_PER_HOUR));
    System.out.println("LINEAR_SPEED_UNIT_M_PER_S : " + getUnitType(LINEAR_SPEED_UNIT_M_PER_S));
    System.out.println("\n");
    System.out.println("ANGULAR_UNIT_ARCSEC : " + getUnitType(ANGULAR_UNIT_ARCSEC));
    System.out.println("ANGULAR_UNIT_DEG : " + getUnitType(ANGULAR_UNIT_DEG));
    System.out.println("ANGULAR_UNIT_RAD : " + getUnitType(ANGULAR_UNIT_RAD));
    System.out.println("\n");
    System.out.println("ANGULAR_SPEED_UNIT_ARCSEC_PER_S : " + getUnitType(ANGULAR_SPEED_UNIT_ARCSEC_PER_S));
    System.out.println("ANGULAR_SPEED_UNIT_DEG_PER_S : " + getUnitType(ANGULAR_SPEED_UNIT_DEG_PER_S));
    System.out.println("ANGULAR_SPEED_UNIT_ARCSEC_PER_DAY : " + getUnitType(ANGULAR_SPEED_UNIT_ARCSEC_PER_DAY));
    System.out.println("ANGULAR_SPEED_UNIT_DEG_PER_DAY : " + getUnitType(ANGULAR_SPEED_UNIT_DEG_PER_DAY));
    System.out.println("ANGULAR_SPEED_UNIT_RAD_PER_DAY : " + getUnitType(ANGULAR_SPEED_UNIT_RAD_PER_DAY));
  }// end testUnitType
*/
}//end class Units