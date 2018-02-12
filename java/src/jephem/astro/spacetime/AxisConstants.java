//*********************************************************************************
// interface jephem.astro.spacetime.AxisConstants
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;

/*********************************************************************************
Interface containing constants related with axis drawing.

@author Thierry Graff
@history jun 06 2002 : Creation

@todo :
**********************************************************************************/
public interface AxisConstants{

  //=================================================================================
  //                                CONSTANTS
  //=================================================================================

  /** Constant to designate an X axis. */
  public static final int X = 0;
  /** Constant to designate an Y axis. */
  public static final int Y = 1;

  /** Constant to indicate that an axis should be drawn in the same area as the rest of the figure. */
  public static final int INSIDE = 0;
  /** Constant to indicate that the axis and the rest of the figure should be drawn in two distinct areas.
  This means that the axis is necessarily located on the border of the figure. */
  public static final int OUTSIDE = 1;

  /** Constant to indicate that an Y axis should be drawn on the left of the figure. */
  public static final double LEFT = 1;
  /** Constant to indicate that an Y axis should be drawn on th right of the figure. */
  public static final double RIGHT = 2;

  /** Constant to indicate that an X axis should be drawn on the top of the figure. */
  public static final double TOP = 4;
  /** Constant to indicate that an X axis should be drawn on the boottom of the figure. */
  public static final double BOTTOM = 5;

}//end interface AxisConstants