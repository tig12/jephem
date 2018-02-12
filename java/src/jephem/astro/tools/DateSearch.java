//*********************************************************************************
// class jephem.astro.tools.DateSearch
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.tools;

import jephem.astro.AstroEngine;
import tig.GeneralConstants;

/******************************************************************************
Class implementing date search methods.
<BR>
@author Thierry Graff
@history jun 15 2002 : creation.

@todo
*********************************************************************************/
public class DateSearch implements GeneralConstants{

  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================
  /** field */
  double _beginDate, _endDate;

  /** field */
  int _coord;

  //=================================================================================
  //                                      CONSTANTS
  //=================================================================================

  /**  */

  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================

  /** Unique constructor, from the begin and end dates for which the search must be done.
  <BR>The dates must be expressed in <B>julian days</B>.
  */
  public DateSearch(double beginDate, double endDate){
    _beginDate = beginDate;
    _endDate = endDate;
  }// end constructor

  //=================================================================================
  //                                      METHODS
  //=================================================================================

  //*************** setBeginDate ***************
  /** Sets the begin date for which the search must be done.
  <BR>The dates must be expressed in <B>julian days</B>.
  */
  public double[] setBeginDate(double beginDate){ _beginDate = beginDate; }

  //*************** setBeginDate ***************
  /** Sets the end date for which the search must be done.
  <BR>The dates must be expressed in <B>julian days</B>.
  */
  public double[] setEndDate(double endDate){ _endDate = endDate; }

  //*************** setCoord ***************
  /** Sets the coordinate for which the search must be done.
  @coord use <CODE>COORD_XX</CODE> constants of interface {@link jephem.astro.spacetime.SpaceConstants}.
  */
  public double[] setCoord(int coord){ _coord = coord; }


  //*************** search(body, position) ***************
  /** Searchs the dates for which 'body' occupies a certain position.
  <BR>The dates are searched between the begin and end dates characterizing this <CODE>DateSearch</CODE> object.
  @param body The celestial body ; use {@link jephem.astro.solarsystem.SolarSystemConstants}'s constants.
  @param position The position for which the search must be done, expressed in the default unit of <CODE>AstroEngine</CODE>.
  @return The dates corresponding to the search, expressed in julian days.
  */
  public double[] search(int body, double position){
    double[] res;

    return res;
  }// end search(body, position)

  //*************** search(body1, body2, angle) ***************
  /** Searchs the dates for which 'body1' and 'body2' form the given 'angle', seen from the center
  of the current frame.
  <BR>If 'body1' is B1, 'body2' is B2, and the frame center is O, 'angle' is the angle (B1 0 B2)
  <BR>The dates are searched between the begin and end dates characterizing this <CODE>DateSearch</CODE> object.
  @param angle expressed in the default unit of <CODE>AstroEngine</CODE>.
  */
  public double[] search(int body1, int body2, double angle){
    double[] res;

    return res;
  }// end search(body1, body2, angle)

}// end class DateSearch