//*********************************************************************************
// class jephem.tools.SkyMapBase
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.tools;

import jephem.astro.sky.BSC5;
import jephem.astro.sky.Constellations;
import jephem.astro.sky.ConstellationConstants;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.solarsystem.SolarSystemConstants;
import jephem.GlobalVar; // only used to retrieve BSC5 directory
import jephem.util.Debug;

import tig.GeneralConstants;
import tig.maths.Maths;
import tig.maths.Matrix3;
import tig.maths.Vector3;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.io.*;

/******************************************************************************
Contains the geometrical functionalities of a sky map ; do not use this class directly,
use instead {@link SkyMap}.
@author Thierry Graff
@history may 09 2002 : creation from SkyMap

@todo when rotation, center of rotation must be the center of the screen.
*********************************************************************************/
public class SkyMapBase extends JPanel implements ConstellationConstants, SpaceConstants, GeneralConstants{

  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================

  /** base frame. */
  private int _skyFrame;

  /** zoom factor. */
  private double _zoom;

  /** Eye matrix. */
  private Matrix3 _em;

  /** Eye orientation. */
  double _theta0, _phi0;

  // Global geometric characteristics
  int _h, _w; // height, width of this panel
  /** Radius of the map, depending on the zoom factor. */
  int _r;

  //=================================================================================
  //                               PUBLIC CONSTANTS
  //=================================================================================

  // Orientation constants
  /** Constant to indicate X axis. */
  public static final int X = 0;
  /** Constant to indicate Y axis. */
  public static final int Y = 1;
  /** Constant to indicate Z axis. */
  public static final int Z = 2;
  /** Constant to indicate direct sens of rotation (anti-clockwise). */
  public static final int PLUS = 3;
  /** Constant to indicate indirect sens of rotation (clockwise). */
  public static final int MINUS = 4;


  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================
  /** Unique constructor. The base frame is set to equatorial frame. */
  public SkyMapBase(){
    super();
    try {
      this._zoom = 1;
      this._skyFrame = FRAME_EQUATORIAL;
      _em = Matrix3.getIdentityMatrix();// at the beginning, eye matrix is the identity matrix
rotateEye(X, -90);
_theta0 = 0;
_phi0 = 0;
    }
    catch(Exception e) {
      Debug.traceError(e);
    }
  }// end constructor

  //=================================================================================
  //                         GEOMETRICAL PUBLIC METHODS
  //=================================================================================

  //*************** get / set eye matrix ***************
  /** Returns the eye matrix, used to define the viewer's orientation. */
  public Matrix3 getEyeMatrix(){ return _em; }
  /** Sets the eye matrix, used to define the viewer's orientation. */
  public void setEyeMatrix(Matrix3 em){ _em = em; } // also repaint ???

  //*************** get / set zoom ***************
  /** Returns the zoom of this sky map. */
  public double getZoom(){ return _zoom; }
  /** Sets the zoom of this sky map, and repaints. */
  public void setZoom(double zoom){
    _zoom = zoom;
    this.repaint();
  }// end setZoom

  //*************** rotateEye ***************
  /** Changes the orientation of the viewer's eye and repaints.
  @param direction Use constants of this class to expree it.
  @param increment The value of the rotation angle, <B>in degres</B>.
  */
  public void rotateEye(int direction, double increment){

    Matrix3 rot;
    increment = Math.toRadians(increment);
    switch(direction){
      case X : // up / down
        rot = Matrix3.getRotX(increment); break;
      case Y : // right / left
        rot = Matrix3.getRotY(increment); break;
      case Z : // anti-clockwise / clockwise
        rot = Matrix3.getRotZ(increment); break;
      default:
        throw new IllegalArgumentException("invalid 'direction' parameter");
    }
    // modify the eye matrix :
    _em = Matrix3.mul(rot, _em);
    this.repaint();
  }// end rotateEye

  //*************** setEyeOrientation ***************
  /** Sets the orientation of the viewer's eye and repaints.
  <BR>The parameters designate the direction to which the eye should look ;
  they are expressed in degrees, in sky coordinates.
  */
  public void setEyeOrientation(double theta, double phi){
    _theta0 = Math.toRadians(theta);
    _phi0 = Math.toRadians(phi);
    this.repaint();
  }// end setEyeOrientation

  //*************** drawCircle ***************
  /** Draws a circle on the given Graphics2D, taking into account the eye matrix.
  <BR>The angles specify the circle orientation :
  <BR><IMG SRC="doc-files/drawCircle.jpg" ALT="" HEIGHT="312" BORDER="0">
  @param alpha0 <B>in degrees</B>.
  @param epsilon0 <B>in degrees</B>.
  @param delta0 <B>in degrees</B>.
  */
  public void drawCircle(Graphics2D g2, double alpha0, double epsilon0, double delta0){
    alpha0 = Math.toRadians(alpha0);
    epsilon0 = Math.toRadians(epsilon0);
    delta0 = Math.toRadians(delta0);

    double step = Math.toRadians(10); // step used to draw a circle
    int nbIterations = (int)Math.floor(2*Math.PI/step);
    double alpha = 0;
    Vector3 v0, v1, v0P, v1P;
    double x0, y0, x1, y1;
    for(int i = 0; i < nbIterations; i++){
      v0 = getCartesianCoords(alpha + epsilon0, delta0);// The equation of the circle in R1 is : delta = delta0
      // convert v0 in the sky frame by two rotations
      v0 = Vector3.mul(Matrix3.getRotX(-epsilon0), v0);
      v0 = Vector3.mul(Matrix3.getRotZ(-alpha0), v0);
      v0 = Vector3.mul(_em, v0); // convert v0 in the eye frame
      v0P = screenToPixel(v0);// Transform to pixel coordinates

      alpha+= step; // Increment alpha

      // perform the same operations for v1
      v1 = getCartesianCoords(alpha + epsilon0, delta0);
      v1 = Vector3.mul(Matrix3.getRotX(-epsilon0), v1);
      v1 = Vector3.mul(Matrix3.getRotZ(-alpha0), v1);
      v1 = Vector3.mul(_em, v1);
      v1P = screenToPixel(v1);// Transform to pixel coordinates

      if(v0.x2 > 0 && v1.x2 > 0){
        g2.draw(new Line2D.Double((int)v0P.x0, (int)v0P.x1, (int)v1P.x0, (int)v1P.x1));
      }
    }// end for i

  }// end drawCircle

  //=================================================================================
  //                                      PROTECTED METHODS
  //=================================================================================

  //*************** calcRHW ***************
  /** Computes the height, width of the panel, and the radius of the circle containing the chart,
  in pixels ; stores these data in instance variables.
  <BR>Written to avoid computing these values several times.
  */
  void calcRHW(){
    // height, width of the panel
    _w = this.getSize().width;
    _h = this.getSize().height;
    int m = 0; // margin
    int r0 = Math.min(_h/2 - 2*m, _w/2 - 2*m); // r0 = radius of the disk (in pixels) when _zoom = 1.
    if(r0 < 0) r0 = 0;
    _r = (int)_zoom*r0; // r = radius of the black disk, in pixels.
  }// end calcRHW


  //********************************************************************************
  //                         Coordinate transformations
  //********************************************************************************

  //*************** getCartesianCoords ***************
  /** Computes cartesian coordinates assuming that 'theta' and 'phi' are in radians, and that rho = 1.*/
  Vector3 getCartesianCoords(double theta, double phi){
//    theta = theta - Math.toRadians(_theta0);
//    phi = phi - Math.PI/2 - Math.toRadians(_phi0);
    return new Vector3( Math.cos(phi)*Math.cos(theta),
                         Math.cos(phi)*Math.sin(theta),
                         Math.sin(phi)
                       );
  }// end getCartesianCoords

  //*************** getSphericalCoords ***************
  /** Computes spherical coordinates of a vector.*/
  Vector3 getSphericalCoords(Vector3 v){
    return new Vector3( 1,
                        Maths.atan3(v.x1, v.x0),
                        Math.asin(v.x2/Vector3.norm(v))
                       );
  }// end getSphericalCoords

  //*************** skyToEye ***************
  /** Transforms sky coordinates to eye coordinates.
  Transformation done between cartesian coordinates.
  */
  Vector3 skyToEye(Vector3 vC){
    return Vector3.mul(_em, vC);
  }// end skyToEye

  //*************** eyeToSky ***************
  /** Transforms eye coordinates to sky coordinates.
  Transformation done between cartesian coordinates.
  */
  Vector3 eyeToSky(Vector3 vE){
    return Vector3.mul(Matrix3.invert(_em), vE);
  }// end eyeToSky

  //*************** screenToEye ***************
  /** Transforms screen coordinates to eye coordinates.
  @param vS a vector containing screen <B>cartesian</B> coordinates.
  @return a vector containing <B>spherical</B> coordinates.
  */
  Vector3 screenToEye(Vector3 vS){
    return new Vector3( Maths.atan3(vS.x0, vS.x1),
                        Math.acos(Vector3.norm(vS)),
                        0);
  }// end screenToEye

  //*************** eyeToScreen ***************
  /** Transforms eye coordinates to screen coordinates (implements the projection). */
  // NOT implemented, useless for orthogonal projection.
//  Vector3 eyeToScreen(Vector3 vE){
//  }// end eyeToScreen


  //*************** screenToPixel ***************
  /** Transforms screen coordinates to pixel coordinates.
  <BR>Assumes that <CODE>calcRHW()</CODE> was called before.
  */
  Vector3 screenToPixel(Vector3 vS){
    return new Vector3( -vS.x0*_r + _w/2,
                        -vS.x1*_r + _h/2,
                        0);
  }// end screenToPixel

  //*************** pixelToScreen ***************
  /** Transforms pixel coordinates to screen coordinates.
  <BR>Assumes that <CODE>calcRHW()</CODE> was called before.
  */
  Vector3 pixelToScreen(Vector3 vP){
    return new Vector3( (-vP.x0 + (double)_w/2)/(double)_r,
                        (-vP.x1 + (double)_h/2)/(double)_r,
                        0);
  }// end pixelToScreen

}// end class SkyMap