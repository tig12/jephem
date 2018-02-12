//*********************************************************************************
// class jephem.gui.SkyMapViewer
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.gui;

import jephem.tools.SkyMap;
import jephem.GlobalVar;
import jephem.util.Debug;
import jephem.astro.spacetime.SpaceConstants;

import tig.GeneralConstants;
import tig.TigBundle;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

/******************************************************************************
GUI component surrounding a {@link jephem.tools.SkyMap}.
It provides the buttons, the status bar etc...

@author Thierry Graff
@history apr 27 2002 : creation.

@todo
*********************************************************************************/
public class SkyMapViewer extends JPanel implements GeneralConstants{

  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================
  /** field */
  private SkyMap _sm;
  private double _zoom;
  private double _increment; // the nb of degrees that a movement generates

  //=================================================================================
  //                                      STATIC VARIABLES
  //=================================================================================
  // ***** Ressource bundles. ******
  private static TigBundle _myBundle;
  static{
    try{
      _myBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "SkyMapViewer.lang",
                                                             GlobalVar.getLocale());
    }
    catch (IOException ioe){
      Debug.traceError(ioe);
    }
  };

  //=================================================================================
  //                                      CONSTANTS
  //=================================================================================
  // For ConstellationListener


  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================
  public SkyMapViewer(){
    super();
    try {
      this.setLayout(new BorderLayout());
      _sm = new SkyMap();
      _zoom = _sm.getZoom();
      _increment = 10;

      // *** Right panel construction
      JPanel rightPanel = new JPanel(new GridLayout(0, 1, 2, 2));

      // Zoom In button
      JButton btnZoomIn = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconZoomIn16.gif"));
      btnZoomIn.addActionListener(new ZoomInListener());
      btnZoomIn.setToolTipText(_myBundle.getString("TTTZoomIn"));
      // Zoom Out button
      JButton btnZoomOut = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconZoomOut16.gif"));
      btnZoomOut.addActionListener(new ZoomOutListener());
      btnZoomOut.setToolTipText(_myBundle.getString("TTTZoomOut"));

      // Left button
      JButton btnLeft = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconArrowLeft16.gif"));
      btnLeft.addActionListener(new MoveListener(SkyMap.Y, SkyMap.PLUS));
      btnLeft.setToolTipText(_myBundle.getString("TTTMoveLeft"));
      // Right button
      JButton btnRight = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconArrowRight16.gif"));
      btnRight.addActionListener(new MoveListener(SkyMap.Y, SkyMap.MINUS));
      btnRight.setToolTipText(_myBundle.getString("TTTMoveRight"));
      // Up button
      JButton btnUp = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconArrowUp16.gif"));
      btnUp.addActionListener(new MoveListener(SkyMap.X, SkyMap.MINUS));
      btnUp.setToolTipText(_myBundle.getString("TTTMoveUp"));
      // Down button
      JButton btnDown = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconArrowDown16.gif"));
      btnDown.addActionListener(new MoveListener(SkyMap.X, SkyMap.PLUS));
      btnDown.setToolTipText(_myBundle.getString("TTTMoveDown"));

      // Rotate clockwise button
      JButton btnRotateClockwise = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconRotateClockwise16.gif"));
      btnRotateClockwise.addActionListener(new MoveListener(SkyMap.Z,SkyMap.MINUS));
      btnRotateClockwise.setToolTipText(_myBundle.getString("TTTRotateClockwise"));
      // Rotate anti-clockwise button
      JButton btnRotateAntiClockwise = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconRotateAntiClockwise16.gif"));
      btnRotateAntiClockwise.addActionListener(new MoveListener(SkyMap.Z, SkyMap.PLUS));
      btnRotateAntiClockwise.setToolTipText(_myBundle.getString("TTTRotateAntiClockwise"));

      // Horizon grid button
      JButton btnHorGrid = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconGridHor16.gif"));
      btnHorGrid.addActionListener(new GridListener(SpaceConstants.FRAME_HORIZONTAL_TOPOCENTRIC));
      btnHorGrid.setToolTipText(_myBundle.getString("TTTHorGrid"));
      // Equatorial grid button
      JButton btnEqGrid = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconGridEq16.gif"));
      btnEqGrid.addActionListener(new GridListener(SpaceConstants.FRAME_EQUATORIAL));
      btnEqGrid.setToolTipText(_myBundle.getString("TTTEqGrid"));
      // Ecliptic grid button
      JButton btnEcGrid = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconGridEc16.gif"));
      btnEcGrid.addActionListener(new GridListener(SpaceConstants.FRAME_ECLIPTIC));
      btnEcGrid.setToolTipText(_myBundle.getString("TTTEcGrid"));
      // Galactic grid button
      JButton btnGalGrid = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconGridGal16.gif"));
      btnGalGrid.addActionListener(new GridListener(SpaceConstants.FRAME_GALACTIC));
      btnGalGrid.setToolTipText(_myBundle.getString("TTTGalGrid"));

      // Constellation abbreviation button
      JButton btnConstAbbreviations = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "IconConstellationsAbbreviations16.gif"));
      btnConstAbbreviations.addActionListener(new ConstellationListener(ConstellationListener.ABBREVIATIONS));
      btnConstAbbreviations.setToolTipText(_myBundle.getString("TTTConstAbbreviations"));
      // Constellation lines button
      JButton btnConstLines = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "IconConstellationsLines16.gif"));
      btnConstLines.addActionListener(new ConstellationListener(ConstellationListener.LINES));
      btnConstLines.setToolTipText(_myBundle.getString("TTTConstLines"));
      // Constellation abbreviation button
      JButton btnConstBoundaries = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "IconConstellationsBoundaries16.gif"));
      btnConstBoundaries.addActionListener(new ConstellationListener(ConstellationListener.BOUNDARIES));
      btnConstBoundaries.setToolTipText(_myBundle.getString("TTTConstBoundaries"));

      // right panel layout
      rightPanel.add(btnZoomIn);
      rightPanel.add(btnZoomOut);
      rightPanel.add(btnLeft);
      rightPanel.add(btnRight);
      rightPanel.add(btnUp);
      rightPanel.add(btnDown);
      rightPanel.add(btnRotateClockwise);
      rightPanel.add(btnRotateAntiClockwise);
      rightPanel.add(btnHorGrid);
      rightPanel.add(btnEqGrid);
      rightPanel.add(btnEcGrid);
      rightPanel.add(btnGalGrid);
      rightPanel.add(btnConstAbbreviations);
      rightPanel.add(btnConstLines);
      rightPanel.add(btnConstBoundaries);

      JPanel rightPanel2 = new JPanel(new BorderLayout());
      rightPanel2.add(rightPanel, BorderLayout.NORTH);
      JPanel rightPanel3 = new JPanel(new BorderLayout());
      rightPanel3.add(rightPanel2, BorderLayout.EAST);

      // General Layout
      this.add(rightPanel3, BorderLayout.EAST);
      this.add(_sm, BorderLayout.CENTER);
    }
    catch(Exception e) {
      Debug.traceError(e);
    }
  }// end constructor


  //=================================================================================
  //                                      PUBLIC METHODS
  //=================================================================================

  //=================================================================================
  //                                      INNER CLASSES
  //=================================================================================

  //*****************************************************************
  class ZoomInListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if(_zoom < 0.5) SkyMapViewer.this._zoom = SkyMapViewer.this._zoom*2;
      else if(_zoom < 1) SkyMapViewer.this._zoom = 1;
      else SkyMapViewer.this._zoom ++;
      SkyMapViewer.this._sm.setZoom(_zoom);
    }// end ActionPerformed
  }// end class ZoomInListener

  //*****************************************************************
  class ZoomOutListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if(_zoom > 2) SkyMapViewer.this._zoom --;
      else if(_zoom > 1) SkyMapViewer.this._zoom = 1;
      else SkyMapViewer.this._zoom = SkyMapViewer.this._zoom/2;
      SkyMapViewer.this._sm.setZoom(_zoom);
    }// end ActionPerformed
  }// end class ZoomOutListener

  //*****************************************************************
  class MoveListener implements ActionListener{
    int _direction; // indicates the direction of the movement
    double _sign;
    public MoveListener(int direction, int sign){
      super();
      _direction = direction;
      if(sign == SkyMap.PLUS) _sign = 1;
      else _sign = -1;
    }
    public void actionPerformed(ActionEvent e){
      SkyMapViewer.this._sm.rotateEye(_direction, _sign * SkyMapViewer.this._increment);
    }// end ActionPerformed
  }// end class ZoomOutListener

  //*****************************************************************
  class ConstellationListener implements ActionListener{
    static final int ABBREVIATIONS = 0;
    static final int LINES = 1;
    static final int BOUNDARIES = 2;
    int _whichFlag;
    public ConstellationListener(int whichFlag){
      super();
      _whichFlag = whichFlag;
    }
    public void actionPerformed(ActionEvent e){
      switch (_whichFlag) {
        case ABBREVIATIONS : SkyMapViewer.this._sm.changeDisplayConstellationAbbreviations(); break;
        case LINES : SkyMapViewer.this._sm.changeDisplayConstellationLines(); break;
        case BOUNDARIES : SkyMapViewer.this._sm.changeDisplayConstellationBoundaries(); break;
      }
    }// end ActionPerformed
  }// end class ConstellationListener

  //*****************************************************************
  class GridListener implements ActionListener{
    int _frame;
    public GridListener(int frame){
      super();
      _frame = frame;
    }
    public void actionPerformed(ActionEvent e){
      SkyMapViewer.this._sm.changeGridDisplay(_frame);
    }// end ActionPerformed
  }// end class GridListener

}// end class SkyMapViewer