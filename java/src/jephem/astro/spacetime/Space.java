//*********************************************************************************
// class jephem.astro.spacetime.Space
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;

import tig.GeneralConstants;

/******************************************************************************
Contains static methods used when dealing with space.
<BR>Interface {@link SpaceConstants} contains related constants.

@author Thierry Graff
@history sep 21 2001 : creation.

@todo internationalize getCoordGroupLabels
@tosee getCoord only used once in NewEphemerisDialog - try to remove
*********************************************************************************/
public abstract class Space implements SpaceConstants, GeneralConstants{

  //=================================================================================
  //                                      METHODS
  //=================================================================================

  /** Contains the english names of frames ; use <CODE>FRAME_XXX</CODE> constants of this interface
  to access to the names. */
  private static final String[] ENGLISH_FRAME_NAMES = {"Frame of theory",
                                                       "Heliocentric geometric",
                                                       "Ecliptic geocentric",
                                                       "Equatorial geocentric"
                                                      };

  /** Contains the english names of frames ; use <CODE>FRAME_XXX</CODE> constants of this interface
  to access to the names. */
  private static final String[] ENGLISH_COORDEXPR_NAMES = {"cartesian",
                                                           "spherical"};

  //=================================================================================
  //                                      METHODS
  //=================================================================================

  /** Returns the English label of a body. Use {@link jephem.astro.solarsystem.SolarSystemConstants}
  constants to designate the body. */
  public static String getFrameLabel(int frameIndex){
    return ENGLISH_FRAME_NAMES[frameIndex];
  }// end getFrameLabel

  /** Returns the English label of a coordinate expression (returns "cartesian" or "spherical").
  Use {@link jephem.astro.solarsystem.SolarSystemConstants}.<CODE>CARTESIAN</CODE> or <CODE>SPHERICAL</CODE>
  to designate the coordinate expression. */
  public static String getCoordinateExpressionLabel(int coordExpr){
    return ENGLISH_COORDEXPR_NAMES[coordExpr];
  }// end getFrameLabel

  //*************************************************
  /** Returns the labels of <B>position</B> coordinates of a group of coordinates.
  Client classes must build themselves the names of velocity coordinates.
  @param coordGroup The concerned group of coordinates ;
  use {@link SpaceConstants}.COORDGROUP_XXX</CODE> constants
  */
  public static String[] getCoordGroupLabels(int coordGroup){
    String[][] coordGroupLabels = {
                                    {"X1", "X2", "X3"}, // for NO_SPECIF
                                    {"X", "Y", "Z"},
                                    {"del", "bet", "lam"},
                                    {"r", "l", "b"},
                                    {"dist.", "alpha", "beta"}
                                  };
    if (coordGroup == NO_SPECIF)
      return coordGroupLabels[0];
    else
      return coordGroupLabels[coordGroup + 1];

  }// end getCoordGroupLabels

  /** Returns the group of coordinates associated with a frame.
  @param frame Concerned frame ; use {@link SpaceConstants}<CODE>.FRAME_XXX</CODE> constants.
  @return The group of coordinates associated with <CODE>frame</CODE> ;
  {@link SpaceConstants}.COORDGROUP_XXX</CODE> constants are used ;
  {@link tig.GeneralConstants#NO_SPECIF} is returned if 'frame' does not correspond
  to a valid frame.
  */
  public static int getCoordGroup(int frame){
    switch(frame){
      case FRAME_EC_HELIO_GEOMETRIC:
        return COORDGROUP_DELTA_BETA_LAMBDA;
      case FRAME_ECLIPTIC:
        return COORDGROUP_RLB;
      case FRAME_EQUATORIAL:
        return COORDGROUP_DIST_ALPHA_DELTA;
      //case FRAME_EQ_TOPO:
      //case FRAME_HOR_TOPO:
      default:
        return NO_SPECIF;
    }
  }// end getCoordGroup

  //*************************************************
  /** Returns the {@link SpaceConstants}'s <CODE>COORD_XXX</CODE> constant corresponding to
  <CODE>coordIndex</CODE>.
  <BR>If <CODE>coordIndex</CODE> does not correspond to a coordinate,
  <CODE>GeneralConstants.NO_SPECIF</CODE> is returned.
  <BR><CODE>getCoord(0)</CODE> returns <CODE>SpaceConstants.X0</CODE> ... etc ...
  <CODE>getCoord(5)</CODE> returns <CODE>SpaceConstants.V2</CODE>
  @param coordIndex An integer between 0 and 5 corresponding to a position or velocity coordinate.
  */
  public static int getCoord(int coordIndex){
    switch (coordIndex){
      case 0: return COORD_X0;
      case 1: return COORD_X1;
      case 2: return COORD_X2;
      case 3: return COORD_V0;
      case 4: return COORD_V1;
      case 5: return COORD_V2;
      default : return NO_SPECIF;
    }
  }// end getCoord

  //*************************************************
  /** Tests if the coordinates passed in parameter contain one or more velocity coordinate.
  @param coords An array of coordinates expressed with <CODE>SpaceConstants.COORD_XXX</CODE>
  constants.
  */
  public static boolean containsVelocityCoord(int[] coords){
    for(int i=0; i < coords.length; i++){
      if(coords[i] == COORD_V0 || coords[i] == COORD_V1 || coords[i] == COORD_V2)
        return true;
    }
    return false;
  }// end containsVelocityCoord

  //=================================================================================
  //=================================================================================
  //                                      TESTS
  //=================================================================================
  //=================================================================================
/*
  // **************** For tests only ****************
  public static void main(String[] args){
    // no complete argument checking
    if(args[0].equalsIgnoreCase("testGetCoordGroupLabels"))
      testGetCoordGroupLabels();
    if(args[0].equalsIgnoreCase("testContainsVelocityCoord"))
      testContainsVelocityCoord(args[1]);
    else{
      String possibleArgs = "'testGetCoordGroupLabels' or 'testContainsVelocityCoord'";
      System.out.println("first argument must be " + possibleArgs);
    }
  }// end main

  // **************** For tests only ****************
  private static void testGetCoordGroupLabels(){
    int frame, coordGroup;
    frame = FRAME_THEORY;
    coordGroup = getCoordGroup(frame);
    String[] labels = getCoordGroupLabels(coordGroup);
    String res="";
    for(int i = 0; i < labels.length; i++){
      res += labels[i] + " ";
    }
    System.out.println("FRAME_THEORY : " + res);
  }// end testGetCoordGroupLabels

  // **************** For tests only ****************
  // call : jephem.astro.spacetime.Space testContainsVelocityCoord 1,2,5
  private static void testContainsVelocityCoord(String strCoords){
    int[] coords = tig.Strings.stringToIntArray(strCoords);
    System.out.println("res : " + containsVelocityCoord(coords));
  }// end testContainsVelocityCoord
*/
}// end class Space