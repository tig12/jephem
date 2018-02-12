//*********************************************************************************
// class jephem.tools.SkyMap
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.tools;

import jephem.tools.SkyMapBase;

import jephem.astro.sky.BSC5;
import jephem.astro.sky.Constellations;
import jephem.astro.sky.ConstellationConstants;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.solarsystem.SolarSystemConstants;
import jephem.GlobalVar; // only used to retrieve BSC5 directory
import jephem.util.Debug;

import tig.GeneralConstants;
import tig.Formats;
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
A visual representation of a sky map. This class permits to specify the visual gadgets of a sky map : grid display; constellation display...
<BR>The core functionalities of a sky map are implemented in its super class, {@link SkyMapBase}.
@author Thierry Graff
@history apr 23 2002 : creation.
@history apr 29 2002 : rotations of the view.
@history may 09 2002 : Split to give SkyMapBase and SkyMap (for clearer code).

@todo maybe put get / set GridInterval in radians directly
*********************************************************************************/
public class SkyMap extends SkyMapBase{

  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================

  // Grid diplay variables
  // WARNING : these variables should be accessed only through get / set, even by the code of this class
  // This way, the class can't be affected by eventual changes in SpaceConstants' constant values.
  private int[] _gridDisplay; // for each frame, can be NONE, FULL or PLANE
  private double[] _gridInterval; // in radians

  // Constellation display variables
  private boolean _displayConstellationAbbreviations;
  private boolean _displayConstellationBoundaries;
  private boolean _displayConstellationLines;

  //=================================================================================
  //                               PUBLIC CONSTANTS
  //=================================================================================

  // Display coordinate grid constants
  /** Constant to indicate that no grid must be displayed. */
  public static final int GRID_NONE = 0;
  /** Constant to indicate that only the reference plane must be displayed. */
  public static final int GRID_PLANE = 1;
  /** Constant to indicate that the whole grid must be displayed. */
  public static final int GRID_FULL = 2;

  //=================================================================================
  //                               PRIVATE CONSTANTS
  //=================================================================================
  private final static BasicStroke STROKE = new BasicStroke(1.0f);
  private final static BasicStroke STROKE2 = new BasicStroke(2.0f);
  private final static BasicStroke DASHED = new BasicStroke(1.0f,
                                                            BasicStroke.CAP_BUTT,
                                                            BasicStroke.JOIN_MITER,
                                                            10.0f, new float[]{5.0f}, 0.0f);
  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================
  /** Unique constructor. The base frame is set to equatorial frame. */
  public SkyMap(){
    super();
    try {
      BSC5.setDataPath(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "astro" + FS + "sky" + FS + "BSC5");

      _gridDisplay = new int[4];
      _gridInterval = new double[4];
      setGridDisplay(FRAME_HORIZONTAL_TOPOCENTRIC,  GRID_NONE);
      setGridDisplay(FRAME_EQUATORIAL,              GRID_FULL);
      setGridDisplay(FRAME_ECLIPTIC,                GRID_PLANE);
      setGridDisplay(FRAME_GALACTIC,                GRID_NONE);
      setGridInterval(FRAME_HORIZONTAL_TOPOCENTRIC, 30);
      setGridInterval(FRAME_EQUATORIAL,             10);
      setGridInterval(FRAME_ECLIPTIC,               10);
      setGridInterval(FRAME_GALACTIC,               30);

      _displayConstellationAbbreviations = true;
      _displayConstellationLines = false;
      _displayConstellationBoundaries = true;

      this.setBackground(Color.WHITE);

      this.addMouseListener(new SkyChartMouseListener());
    }
    catch(Exception e) {
      Debug.traceError(e);
    }
  }// end constructor

  //=================================================================================
  //                         GEOMETRICAL PUBLIC METHODS
  //=================================================================================

  //=================================================================================
  //                           GET / SET METHODS for visualization characteristics
  //=================================================================================

  //*************** get / set / change displayConstellationAbbreviations ***************
  /** Returns the flag indicating if the constellation abbreviations must be displayed on the map. */
  //public boolean getDisplayConstellationAbbreviations(){ return _displayConstellationAbbreviations; }
  /** Sets the flag indicating if the constellation abbreviations must be displayed on the map. */
  public void setDisplayConstellationAbbreviations(boolean displayConstellationAbbreviations){
    _displayConstellationAbbreviations = displayConstellationAbbreviations;
    this.repaint();
  }// end setDisplayConstellationAbbreviations
  /** Inverts the flag indicating if the constellation abbreviations must be displayed on the map. */
  public void changeDisplayConstellationAbbreviations(){
    _displayConstellationAbbreviations = !_displayConstellationAbbreviations;
    this.repaint();
  }// end changeDisplayConstellationAbbreviations

  //*************** get / set / change displayConstellationLines ***************
  /** Returns the flag indicating if the constellation lines must be displayed on the map. */
//  public boolean getDisplayConstellationLines(){ return _displayConstellationLines; }
  /** Sets the flag indicating if the constellation lines must be displayed on the map. */
  public void setDisplayConstellationLines(boolean displayConstellationLines){
    _displayConstellationLines = displayConstellationLines;
    this.repaint();
  }// end setDisplayConstellationLines
  /** Inverts the flag indicating if the constellation lines must be displayed on the map. */
  public void changeDisplayConstellationLines(){
    _displayConstellationLines = !_displayConstellationLines;
    this.repaint();
  }// end changeDisplayConstellationLines

  //*************** get / set / change displayConstellationBoundaries ***************
  /** Returns the flag indicating if the constellation boundaries must be displayed on the map. */
  //public boolean getDisplayConstellationBoundaries(){ return _displayConstellationBoundaries; }
  /** Sets the flag indicating if the constellation boundaries must be displayed on the map. */
  public void setDisplayConstellationBoundaries(boolean displayConstellationBoundaries){
    _displayConstellationBoundaries = displayConstellationBoundaries;
    this.repaint();
  }// end setDisplayConstellationBoundaries
  /** Inverts the flag indicating if the constellation boundaries must be displayed on the map. */
  public void changeDisplayConstellationBoundaries(){
    _displayConstellationBoundaries = !_displayConstellationBoundaries;
    this.repaint();
  }// end changeDisplayConstellationBoundaries

  //*************** getGridDisplay ***************
  /** Returns the "grid display" of this sky map.
  @param whichFrame One of the supported frames, using
                    {@link jephem.astro.spacetime.SpaceConstants}.<CODE>FRAME_XXX</CODE> constants.
  @return the grid display, expressed with <CODE>GRID_XXX</CODE> constants of this class.
  */
  public int getGridDisplay(int whichFrame){
    switch (whichFrame) {
      case FRAME_HORIZONTAL_TOPOCENTRIC : return _gridDisplay[0];
      case FRAME_EQUATORIAL :   return _gridDisplay[1];
      case FRAME_ECLIPTIC :   return _gridDisplay[2];
      case FRAME_GALACTIC : return _gridDisplay[3];
      default : throw new IllegalArgumentException("invalid 'whichFrame' parameter");
    }
  }// end getGridDisplay

  //*************** setGridDisplay ***************
  /** Sets the "grid display" of this sky map.
  @param whichFrame One of the supported frames, using
         {@link jephem.astro.spacetime.SpaceConstants}.<CODE>FRAME_XXX</CODE> constants.
  @param gridDisplay Expressed with <CODE>GRID_XXX</CODE> constants of this class.
  */
  public void setGridDisplay(int whichFrame, int gridDisplay){
    switch (whichFrame) {
      case FRAME_HORIZONTAL_TOPOCENTRIC : _gridDisplay[0] = gridDisplay; break;
      case FRAME_EQUATORIAL :   _gridDisplay[1] = gridDisplay; break;
      case FRAME_ECLIPTIC :   _gridDisplay[2] = gridDisplay; break;
      case FRAME_GALACTIC : _gridDisplay[3] = gridDisplay; break;
      default : throw new IllegalArgumentException("invalid 'whichFrame' parameter");
    }
  }// end setGridDisplay

  //*************** changeGridDisplay ***************
  /** Changes the grid display for the specified frame and repaints. */
  public void changeGridDisplay(int frame){
    int gd = getGridDisplay(frame);
    switch (gd) {
      case GRID_NONE : setGridDisplay(frame, GRID_PLANE); break;
      case GRID_PLANE : setGridDisplay(frame, GRID_FULL); break;
      case GRID_FULL : setGridDisplay(frame, GRID_NONE); break;
    }
    repaint();
  }// end changeGridDisplay

  //*************** getGridInterval ***************
  /** Returns the "grid interval" of this sky map, <B>in degrees</B>.
  @param whichFrame One of the supported frames, using
         {@link jephem.astro.spacetime.SpaceConstants}.<CODE>FRAME_XXX</CODE> constants.
  */
  public double getGridInterval(int whichFrame){
    switch (whichFrame) {
      case FRAME_HORIZONTAL_TOPOCENTRIC : return Math.toDegrees(_gridInterval[0]);
      case FRAME_EQUATORIAL :   return Math.toDegrees(_gridInterval[1]);
      case FRAME_ECLIPTIC :   return Math.toDegrees(_gridInterval[2]);
      case FRAME_GALACTIC : return Math.toDegrees(_gridInterval[3]);
      default : throw new IllegalArgumentException("invalid 'whichFrame' parameter");
    }
  }// end getGridInterval

  //**************** setGridInterval ***************
  /** Sets the "grid interval" of this sky map, <B>in degrees</B>.
  @param whichFrame One of the supported frames, using
                    {@link jephem.astro.spacetime.SpaceConstants}.<CODE>FRAME_XXX</CODE> constants.
  @param gridDisplay Expressed with <CODE>GRID_XXX</CODE> constants of this class.
  */
  public void setGridInterval(int whichFrame, double gridInterval){
    switch (whichFrame) {
      case FRAME_HORIZONTAL_TOPOCENTRIC : _gridInterval[0] = Math.toRadians(gridInterval); break;
      case FRAME_EQUATORIAL :   _gridInterval[1] = Math.toRadians(gridInterval); break;
      case FRAME_ECLIPTIC :   _gridInterval[2] = Math.toRadians(gridInterval); break;
      case FRAME_GALACTIC : _gridInterval[3] = Math.toRadians(gridInterval); break;
      default : throw new IllegalArgumentException("invalid 'whichFrame' parameter");
    }
  }// end setGridInterval

  //*****************************************************************************
  //****************************** paintComponent *******************************
  //*****************************************************************************
  /** Paints the sky map on its display area. */
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    calcRHW(); // actualisation of _r, _h, _w

    // Draw the colored disk representing the sky background
    g2.setPaint(new Color(3, 6, 113));
    g2.fill(new Ellipse2D.Double(_w/2 - _r, _h/2 - _r, 2*_r, 2*_r));

    int i;

    // ***** Star display *****
    Vector3 vC, vE, vP; // vectors containing cartesian coords
    int sd; // star diameter
    double ra, dec; // alpha, delta
    // variables for conversion between magnitude and star diameter in pixels
    double a1, b1;
    double d0 = 7, d7 = 1; // d0 : diameter of star of mag 0 ; d7 : diameter of star of mag 7
    a1 = (d7 - d0)/7; b1 = d0;

    g2.setPaint(Color.WHITE);

    // Loop on the stars
    for (i = 0; i < BSC5.NB_STARS; i++){
      // get the star coordinates
      dec = BSC5.getDoubleData(i, BSC5.DEC);
      ra = BSC5.getDoubleData(i, BSC5.RA);
      vC = getCartesianCoords(ra, dec); // get cartesian coords In the sky frame.
      // change from sky frame to the eye frame (use eye matrix).
      vE = Vector3.mul(getEyeMatrix(), vC);
      if(vE.x2 > 0){
        // normally : vS = eyeToScreen(vE); vP = screenToPixel(vS);
        // here, directly from vE to vP as an orthogonal projection is used
        vP = screenToPixel(vE);
        // Draw the star
        sd = (int)(a1*BSC5.getDoubleData(i, BSC5.MAG) + b1);
        g2.fill(new Ellipse2D.Double(vP.x0 - sd/2, vP.x1 - sd/2, sd, sd));
      }
    }// end loop on the stars

    // ***** Constellation display *****
    if(_displayConstellationBoundaries){
      g2.setStroke(DASHED);
      g2.setPaint(Color.YELLOW);
      for (i = 0; i < NB_CONSTELLATIONS; i++) {
        drawConstellationBoundaries(g2, i);
      }
      g2.setStroke(STROKE);
    }

    // ***** Grid display *****
    g2.setPaint(Color.WHITE);
    int interval;
    // Equatorial grid
    if(getGridDisplay(FRAME_EQUATORIAL) == GRID_PLANE){
      g2.setStroke(STROKE2);
      drawCircle(g2, 0, 0, 0); // draw equator plane
      g2.setStroke(STROKE);
    }
    else if(getGridDisplay(FRAME_EQUATORIAL) == GRID_FULL){
      interval = (int)Math.ceil(getGridInterval(FRAME_EQUATORIAL));
      // display parallels
      g2.setStroke(STROKE2);
      drawCircle(g2, 0, 0, 0); // draw equator plane
      g2.setStroke(STROKE);
      for (i = interval; i < 90; i+=interval){
        drawCircle(g2, 0, 0, (double)i);
      }
      for (i = -interval; i > -90; i-=interval){
        drawCircle(g2, 0, 0, (double)i);
      }
      // display meridians
      for (i = 0; i < 360; i+=interval){
        drawCircle(g2, (double)i, 90, 0);
      }
    }
    // Ecliptic grid
    if(getGridDisplay(FRAME_ECLIPTIC) == GRID_PLANE){
      g2.setStroke(STROKE2);
      drawCircle(g2, 0, SolarSystemConstants.E0_2000, 0); // draw ecliptic
      g2.setStroke(STROKE);
    }
    else if(getGridDisplay(FRAME_ECLIPTIC) == GRID_FULL){
      interval = (int)Math.ceil(getGridInterval(FRAME_ECLIPTIC));
      // display parallels
      g2.setStroke(STROKE2);
      drawCircle(g2, 0, SolarSystemConstants.E0_2000, 0); // draw ecliptic
      g2.setStroke(STROKE);
      for (i = interval; i < 90; i+=interval){
        drawCircle(g2, 0, SolarSystemConstants.E0_2000, (double)i);
      }
      for (i = -interval; i > -90; i-=interval){
        drawCircle(g2, 0, SolarSystemConstants.E0_2000, (double)i);
      }
      // display meridians
      for (i = 0; i < 90; i+=interval){
        drawCircle(g2, (double)i, 90 + SolarSystemConstants.E0_2000, 0);
      }
    }

  }// end paintComponent

  //=================================================================================
  //                                      PRIVATE METHODS
  //=================================================================================

  //*************** drawConstellationBoundaries ***************
  /** Auxiliary method of paint() to draws a constellation.
  @param whichConst use ConstellationConstants.
  */
  private void drawConstellationBoundaries(Graphics2D g2, int whichConst){
    double[][] theConst = Constellations.getBoundaries(whichConst);
    Vector3 v0, v1, vP0, vP1; // P stands for Pixel
    v0 = getCartesianCoords(theConst[0][0], theConst[0][1]);
    v0 = Vector3.mul(getEyeMatrix(), v0); // v0 in the eye frame
    for(int i = 1; i < theConst.length; i++){
      v1 = getCartesianCoords(theConst[i][0], theConst[i][1]);
      v1 = Vector3.mul(getEyeMatrix(), v1); // v1 in the eye frame
      if(v0.x2 > 0 && v1.x2 > 0){
        vP0 = screenToPixel(v0);// Transform to pixel coords
        vP1 = screenToPixel(v1);// Transform to pixel coords
        g2.draw(new Line2D.Double(vP0.x0, vP0.x1, vP1.x0, vP1.x1));
      }
      v0 = Vector3.doClone(v1);
    }
  }// end drawConstellationBoundaries


  //=================================================================================
  //                                      INNER CLASSES
  //=================================================================================

  class SkyChartMouseListener extends MouseAdapter{
    public void mousePressed(MouseEvent e){
      String display;
      Vector3 vS = pixelToScreen(new Vector3(e.getX(), e.getY(), 0));
      Vector3 vE = screenToEye(vS);
      Vector3 vC = eyeToSky(vE);
      vC = getSphericalCoords(vC);
      display = "(ra, dec) = (" + Formats.doubleToDMS(vC.x1) + ", " + Formats.doubleToDMS(vC.x2) + ")";
      GlobalVar.setStatusText(display);
    }// end MousePressed
  } // end class SkyChartMouseListener

}// end class SkyMap