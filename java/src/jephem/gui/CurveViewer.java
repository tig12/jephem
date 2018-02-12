//*********************************************************************************
// class jephem.gui.CurveViewer
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.gui;

import jephem.tools.Curve;
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
GUI component surrounding a {@link jephem.tools.Curve}.
It provides the buttons, the status bar etc...

@author Thierry Graff
@history apr may 10 : creation.

@todo
*********************************************************************************/
public class CurveViewer extends JPanel implements GeneralConstants{

  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================
  /** field */
  private Curve _curve;

  //=================================================================================
  //                                      STATIC VARIABLES
  //=================================================================================
  // ***** Ressource bundles. ******
  private static TigBundle _myBundle;
  static{
    try{
      _myBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "CurveViewer.lang",
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
  /** Unique constructor. 'data' is used to build the corresponding {@link jephem.tools.Curve}.
  See {@link jephem.tools.Curve#Curve(double[][])} for more details. */
  public CurveViewer(double[][] data){
    try {
      this.setLayout(new BorderLayout());

      _curve = new Curve(data);

//      // *** Right panel construction
//      JPanel rightPanel = new JPanel(new GridLayout(0, 1, 2, 2));
//
//      // Zoom In button
//      JButton btnZoomIn = new JButton(new ImageIcon(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS + "iconZoomIn16.gif"));
//      btnZoomIn.addActionListener(new ZoomInListener());
//      btnZoomIn.setToolTipText(_myBundle.getString("TTTZoomIn"));
//
//      // right panel layout
//      rightPanel.add(btnZoomIn);
//
//      JPanel rightPanel2 = new JPanel(new BorderLayout());
//      rightPanel2.add(rightPanel, BorderLayout.NORTH);
//      JPanel rightPanel3 = new JPanel(new BorderLayout());
//      rightPanel3.add(rightPanel2, BorderLayout.EAST);
//
//      // General Layout
//      this.add(rightPanel3, BorderLayout.EAST);
      this.add(_curve, BorderLayout.CENTER);
    }
    catch(Exception e) {
      Debug.traceError(e);
    }
  }// end constructor


  //=================================================================================
  //                                      PUBLIC METHODS
  //=================================================================================

  //*************** get / set curve ***************
  /** Returns the {@link jephem.tools.Curve} associated with this <CODE>CurveViewer</CODE>. */
  public Curve getCurve(){ return _curve; }
  /** Sets the {@link jephem.tools.Curve} associated with this <CODE>CurveViewer</CODE>. */
  public void setCurve(Curve curve){ _curve = curve; } // also repaint ???

  //=================================================================================
  //                                      INNER CLASSES
  //=================================================================================

//  //*****************************************************************
//  class ZoomInListener implements ActionListener{
//    public void actionPerformed(ActionEvent e){
//      if(_zoom < 0.5) CurveViewer.this._zoom = CurveViewer.this._zoom*2;
//      else if(_zoom < 1) CurveViewer.this._zoom = 1;
//      else CurveViewer.this._zoom ++;
//      CurveViewer.this._sm.setZoom(_zoom);
//    }// end ActionPerformed
//  }// end class ZoomInListener

}// end class CurveViewer