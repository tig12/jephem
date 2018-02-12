//*********************************************************************************
// class jephem.tools.Curve
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.tools;

import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.spacetime.AxisConstants;
import jephem.GlobalVar;
import jephem.util.Debug;

import tig.GeneralConstants;
import tig.maths.Maths;
import tig.maths.Vector2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.io.*;

/******************************************************************************
Visual representation of curve plotting.
<BR>A <CODE>Curve</CODE> is a <CODE>JPanel</CODE>, so it can be used directly, but {@link jephem.gui.CurveViewer},
contains the controls surrounding a <CODE>Curve</CODE>.

@author Thierry Graff
@history may 10 2002 : creation.

@todo remove setData ? or warning to side effects (recompute all the necessary fields)
*********************************************************************************/
public class Curve extends JPanel implements SpaceConstants, GeneralConstants{

  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================

  //***************** Data characteristics ******************
  /** The data to display. */
  private double[][] _data;
  /** Number of values to be represented for each curve. */
  private int _nbX;
  /** Number of curves represented. */
  private int _nbY;
  /** min and max values in data coordinates. */
  private double _xMin, _xMax, _yMin, _yMax, _deltaX, _deltaY;

  //***************** Axis characteristics ******************
  /** Point were the axis intersect, in pixel coordinates.
  Define the vert. and hor. positions of the axis. */
  private Vector2 _axisCenter;
  /** Intervals for which the values are displayed on the axis. */
  private double _intervalX, _intervalY;
  /** Space taken by the axis for the display (in pixels). */
  private int _axisSpaceX, _axisSpaceY;

  //***************** Display area characteristics ******************
  /** Supplementary gaps between the axis and the border of the display area. */
  int _ug, _bg, _lg, _rg;
  /** height and width of the display area */
  int _h, _w;

  //=================================================================================
  //                               PUBLIC CONSTANTS
  //=================================================================================

  //=================================================================================
  //                               PRIVATE CONSTANTS
  //=================================================================================
  private final static BasicStroke STROKE = new BasicStroke(1.0f);
  private final static BasicStroke STROKE2 = new BasicStroke(2.0f);

  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================

  /** Constructor from an array containing at the same time X and Y values.
  <BR>Meaning of parameter 'data' :
  <BR><CODE>data[0]</CODE> contains the values of the X axis ;
  <BR><CODE>data[1]</CODE> contains the values of the first quantity on the Y axis ;
  <BR><CODE>data[2]</CODE> contains the values of the second quantity on the Y axis ;
  <BR>etc...
  @throws IllegalArgumentException if the arrays <CODE>data[0]</CODE>, <CODE>data[1]</CODE> etc...
                                   are not all of the same length (for all represented curves, a y value must
                                   correspond to a x value).
  @throws IllegalArgumentException if data.length < 2 (Ther must be at least a value for X and a value for Y).
  */
  public Curve(double[][] data){
    // Parameter checking
    if(data.length < 2)
      throw new IllegalArgumentException("Incorrect len of parameter ' data' - Must have at least two lines");
    int len = data[0].length;
    for (int i = 0; i < data.length; i++) {
      if(data[i].length != len)
        throw new IllegalArgumentException("All data[i] must be of the same length.");
    }

    try {
      //Data stuff
      _data = data;
      _nbX = len; // all data[i] have the same length.
      _nbY = _data.length - 1;
      calcMinMaxAndDeltas();

      // Layout stuff
      this.setLayout(new BorderLayout());
      this.setBackground(Color.WHITE);
      calcHW();

      // to do : consider the case _w < _lg + _rg
      // to do : consider the case _h < _ug + _bg
      _ug = 10;
      _bg = 10;
      _lg = 10;
      _rg = 10;
      _axisSpaceX = 30;
      _axisSpaceY = 30;
    }
    catch(Exception e) {
      Debug.traceError(e);
    }
  }// end constructor Curve(double[][] data)

//  /** Constructor from two arrays (one for X values, one for y values).
//  For the <CODE>IllegalArgumentException</CODE>s, see {@link #Curve(double[][])}
//  @param xData contains the values for X axis.
//  @param yData contains the values for Y axis.
//  <BR><CODE>yData[0]</CODE> contains the values of the first curve for the Y axis ;
//  <BR><CODE>yData[0]</CODE> contains the values of the second curve for the Y axis ;
//  <BR>etc...
//  */
//  public Curve(double[] xData, double[][] yData){
//    double[][] data = new double[yData.length + 1][];
//    data[0] = xData;
//    for (int i = 0; i < yData.length; i++) {
//      data[i+1] = yData[i];
//    }
//    this(data); ///////////// This instruction must be first.
//  }// end constructor Curve(double[] xData, double[][] yData)

  //=================================================================================
  //                         PUBLIC METHODS
  //=================================================================================

  //*************** getX ***************
  // OVERRIDE IN JCOMPONENT
  /** Returns the values of the X axis. */
  public double[] getXValues(){ return _data[0]; }

  //*************** getNbX ***************
  /** Returns the number of X values. */
  public double getNbX(){ return _nbX; }

  //*************** getY ***************
  /** Returns the values of the Y axis for the requested curve.
  @param whichY index of the requested curve, starting to 1.
  @throws ArrayIndexOutOfBoundsException if the whichYth curve does not exist.
  */
  public double[] getY(int whichY){ return _data[whichY]; }

  //*************** getNbY ***************
  /** Returns the number of curves held by this <CODE>Curve </CODE>. */
  public double getNbY(){ return _nbY; }

  //*************** getY(X) ***************
  /** Returns the values of the Y axis for the requested curve.
  A Lagrange polynom is used to interpolate.
  @param whichY index of the requested curve, starting to 1.
  @throws ArrayIndexOutOfBoundsException if the whichYth curve does not exist.
  */
  public double getY(int whichY, double x){
    return 0; //// to write
  }

  //*************** getAllY ***************
  /** Returns the values of the Y axis, for all the curves. */
  public double[][] getAllY(){
    double[][] data  = new double[_nbY][_nbX];
    for (int i = 0; i < _nbY; i++) {
      data[i] = _data[i+1];
    }
    return data;
  }// end getAllY

  //*************** getData ***************
  /** Returns the data displayed by this curve.
  <BR>If the returned value is called 'data' :
  <BR><CODE>data[0]</CODE> contains the values of the X axis ;
  <BR><CODE>data[1]</CODE> contains the values of the first curve on the Y axis ;
  <BR><CODE>data[2]</CODE> contains the values of the second curve on the Y axis ;
  <BR>etc...
  */
  public double[][] getData(){ return _data; }

  //*************** setData ***************
  /** Sets the data displayed by this curve.
  */
  public void setData(double[][] data){
    // Parameter checking
    if(data.length < 2)
      throw new IllegalArgumentException("Incorrect len of parameter ' data' - Must have at least two lines");
    int len = data[0].length;
    for (int i = 0; i < data.length; i++) {
      if(data[i].length != len)
        throw new IllegalArgumentException("All data[i] must be of the same length.");
    }
    _data = data;
    _nbX = len; // all data[i] have the same length.
    _nbY = _data.length - 1;
//System.out.println("_nbX = " + _nbX);
//System.out.println("_nbY = " + _nbY);
    calcMinMaxAndDeltas();
  }// end setData

  //*************** toString ***************
  /** Returns a String representation of this curve. */
  public String toString(){
    String XEQ = "X = ";
    String Y = "Y";
    String EQ = " = ";
    String SPACES = "   ";
    StringBuffer res = new StringBuffer("=== curve data===" + LS);
    for (int i = 0; i < _data[0].length; i++){ // all data[i] have the same length
      res.append(XEQ).append(_data[0][i]);
      for (int j = 1; j < _data.length; j++) {
        res.append(SPACES).append(Y).append(j).append(EQ).append(_data[j][i]);
      }
      res.append(LS);
    }
    res.append("=== end curve data ===");
    return res.toString();
  }// end toString

  //*****************************************************************************
  //****************************** paintComponent *******************************
  //*****************************************************************************
  /** Paints the curve on its display area. */
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    calcHW();
    _axisCenter = new Vector2(_lg + (double)_axisSpaceX/2, _h - (_bg + (double)_axisSpaceY/2));

    g2.setStroke(STROKE);
    drawAxis(g2);

    // Draw the curves
    g2.setStroke(STROKE2);
    g2.setPaint(Color.BLACK);
    Vector2 v0D, v1D, v0P, v1P; // P and D for Pixel and Data coordinates
    for(int i = 1; i <= _nbY; i++){
      v0D = new Vector2(_data[0][0], _data[i][0]); // first point to draw
      for(int j = 0; j < _nbX - 1; j++){
        v1D = new Vector2(_data[0][j+1], _data[i][j+1]); // second point to draw
        // change to pixel coords
        v0P = dataToPixel(v0D);
        v1P = dataToPixel(v1D);
        g2.draw(new Line2D.Double((int)v0P.x0, (int)v0P.x1, (int)v1P.x0, (int)v1P.x1));
        // v0D takes the value of v1D
        v0D = v1D;
      }// end for j
    }// end for i

  }// end paintComponent

  //=================================================================================
  //                                      PRIVATE METHODS
  //=================================================================================

  //********************** dataToPixel **********************
  /** Converts data coordinates to pixel coordinates. */
  private Vector2 dataToPixel(Vector2 vD){
    double a, b;
    double w = (double)_w;
    double h = (double)_h;
    double ug = (double)_ug;
    double bg = (double)_bg;
    double rg = (double)_rg;
    double lg = (double)_lg;
    // conversion for x
    a = (w - rg - lg)/_deltaX;
    b = (lg*_xMax + (rg - w)*_xMin) / _deltaX;
    double xP = a*vD.x0 + b;
    // conversion for y
    a = (ug + bg - h) / _deltaY;
    b = (-ug*_yMin + (h - bg)*_yMax ) / _deltaY;
    double yP = a*vD.x1 + b;
    return new Vector2(xP, yP);
  }// end dataToPixel

  //********************** pixelToData **********************
  /** Converts pixel coordinates to data coordinates. */
  private Vector2 pixelToData(Vector2 vP){
    double a, b;
    double w = (double)_w;
    double h = (double)_h;
    double ug = (double)_ug;
    double bg = (double)_bg;
    double rg = (double)_rg;
    double lg = (double)_lg;
    // conversion for x
    a = _deltaX / (w - rg - lg);
    b = (lg*_xMax + (rg - w)*_xMin) / (-w + rg + lg);
    double xD = a*vP.x0 + b;
    // conversion for y
    a = _deltaY / (ug + bg - h);
    b = (-ug*_yMin + (h - bg)*_yMax ) / (h - ug - bg);
    double yD = a*vP.x1 + b;
    return new Vector2(xD, yD);
  }// end pixelToData

  //********************** calcMinMaxAndDeltas **********************
  /** Computes from _data the min and max and deltas of the x and y axis.
  @post _xMin, _xMax, _yMin, _yMax, _deltaX, _deltaY are computed.
  */
  private void calcMinMaxAndDeltas(){
    _xMin = Maths.min(_data[0]);
    _xMax = Maths.max(_data[0]);

    _yMin = Maths.min(_data[1]);
    _yMax = Maths.max(_data[1]);
    double yMin, yMax;
    for (int i = 2; i <= _nbY; i++) {
      yMin = Maths.min(_data[i]);
      yMax = Maths.max(_data[i]);
      if(yMin < _yMin) _yMin = yMin;
      if(yMax > _yMax) _yMax = yMax
      ;
    }
    _deltaX = _xMax - _xMin;
    _deltaY = _yMax - _yMin;
//System.out.println("_xMin = " + _xMin);
//System.out.println("_xMax = " + _xMax);
//System.out.println("_yMin = " + _yMin);
//System.out.println("_yMax = " + _yMax);
  }// end calcMinMaxAndDeltas

  //*************** calcRHW ***************
  /** Computes the height and width of the panel. */
  private void calcHW(){
    // height, width of the panel
    _w = this.getSize().width;
    _h = this.getSize().height;
  }// end calcHW

  //*************** drawAxis ***************
  /** Computes the height and width of the panel. */
  private void drawAxis(Graphics2D g2){
    int posX = (int)_axisCenter.x0; // _axisCenter computed in paintComponent (after calcHW())
    int posY = (int)_axisCenter.x1;
//System.out.println("posX = " + posX + " posY = " + posY);

    // Draw the axis lines
    g2.draw(new Line2D.Double(_lg, posY, _w - _rg, posY)); // X axis
    g2.draw(new Line2D.Double(posX, _h - _bg, posX, _ug)); // Y axis

    int as = 15; // as = arrow size = size of the arrow
    double alpha = Math.toRadians(20); // angle of the arrow
    double ca = Math.cos(alpha);
    double sa = Math.sin(alpha);
    // Draw the arrow for X axis
    g2.draw(new Line2D.Double(_w - _rg, posY, _w - _rg - as*ca, posY - as*sa));
    g2.draw(new Line2D.Double(_w - _rg, posY, _w - _rg - as*ca, posY + as*sa));
    // Draw the arrow for Y axis
    g2.draw(new Line2D.Double(posX, _ug, posX - as*sa, _ug + as*ca));
    g2.draw(new Line2D.Double(posX, _ug, posX + as*sa, _ug + as*ca));


  }// end drawAxis

}// end class SkyMap