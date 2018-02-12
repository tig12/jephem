//*********************************************************************************
// class jephem.astro.spacetime.Axis
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;


/*********************************************************************************
An instance of this class permits to specify an axis to be drawn on a 2D figure.
Related constants are located in interface {@link AxisConstants}.

<LI>An axis can be a X or Y axis.</LI>
<LI>It can be defined as inside or outside the rest of the figure.</LI>

@author Thierry Graff
@history jun 06 2002 : Creation

@todo :
**********************************************************************************/
public class Axis implements AxisConstants{

  //=================================================================================
  //                                INSTANCE VARIABLES
  //=================================================================================$
  private int _xOrY;
  private int _inOrOut;
  private double _place;

  //=================================================================================
  //                                CONSTRUCTORS
  //=================================================================================$

  //***********************************************************
  /** Unique constructor.
  @param xOrY indicates if it is an X or an Y axis ;
  use <CODE>X</CODE> or <CODE>Y</CODE> constants of {@link AxisConstants}.
  @param inOrOut Indicates if the axis should be drawn in the area of the curve or not ;
  use <CODE>INSIDE</CODE> or <CODE>OUTSIDE</CODE> constants of {@link AxisConstants}.
  @param place Permits to specify the position of the axis. If 'inOrOut' is <CODE>OUTSIDE</CODE>, 'place'
  should be TOP, BOTTOM or TOP + BOTTOM for an X axis (resp. LEFT, RIGHT or LEFT + RIGHT for an Y axis).
  <BR>If 'inOrOut' is <CODE>INSIDE</CODE>, 'place' can take any value. This value must be coherent and
  understood by the class which will use the axis.
  */
  public Axis(int xOrY, int inOrOut, double place){
    if(xOrY == X || xOrY == Y) _xOrY = xOrY;
    else throw new IllegalArgumentException("incorrect value of parameter 'xOrY'");

    if(inOrOut == INSIDE || inOrOut == OUTSIDE) _inOrOut = inOrOut;
    else throw new IllegalArgumentException("incorrect value of parameter 'inOrOut'");

    if(inOrOut == OUTSIDE){
      if(xOrY == X){
        if(place == TOP || place == BOTTOM || place == TOP + BOTTOM) _place = place;
        else throw new IllegalArgumentException("Incorrect value of parameter 'place' : the place of an OUTSIDE X axis " +
        "must be TOP, BOTTOM or TOP + BOTTOM");
      }
      else{
        if(place == LEFT || place == RIGHT || place == LEFT + RIGHT) _place = place;
        else throw new IllegalArgumentException("Incorrect value of parameter 'place' : the place of an OUTSIDE Y axis " +
        "must be LEFT, RIGHT or LEFT + RIGHT");
      }
    }
    else{
      _place = place;
    }
  }// end constructor

  //=================================================================================
  //                                PUBLIC METHODS
  //=================================================================================$

  //************************* definePlace **********************************
  /**
   */

}//end class Axis

