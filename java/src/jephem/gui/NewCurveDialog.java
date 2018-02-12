//*********************************************************************************
// class jephem.gui.NewCurveDialog
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.gui;

import jephem.gui.UtilsGUI;

import jephem.gui.SaveListener;
import jephem.GlobalVar;
import jephem.tools.Ephemeris;
//import jephem.tools.AstroPrefs;
import jephem.astro.solarsystem.SolarSystemConstants;
import jephem.astro.spacetime.Space;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.spacetime.Time;
import jephem.astro.spacetime.TimeConstants;
import jephem.astro.spacetime.Units;
import jephem.astro.spacetime.UnitsConstants;
import jephem.astro.AstroException;
import jephem.util.Debug;

import tig.GeneralConstants;
import tig.Dates;
import tig.TigBundle;
import tig.TigProperties;
import tig.swing.JTextField2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
/******************************************************************
Modal dialog displayed when a new Curve plotting is asked by the user.

@see jephem.tools.Curve
@see jephem.gui.CurveViewer

@author Thierry Graff
@history sep 10 2002 : Creation

@todo handle
*****************************************************************/
public class NewCurveDialog extends JDialog
             implements GeneralConstants, SolarSystemConstants, SpaceConstants, UnitsConstants{

  //=================================================================================
  //                            INSTANCE VARIABLES
  //=================================================================================


  //=================================================================================
  //                          RESSOURCE BUNDLES (STATIC VARIABLES)
  //=================================================================================
  // ***** Ressource bundles. ******
  private static TigBundle _myBundle, _galBundle, _astroBundle, _timeBundle;
  static{
    _galBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL);
    _astroBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_ASTRO);
    try{
      _myBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "NewCurveDialog.lang",
                                                             GlobalVar.getLocale());
      _timeBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "Time.lang",
                                                              GlobalVar.getLocale());
    }
    catch (IOException ioe){
      Debug.traceError(ioe);
    }
  };

  //=================================================================================
  //                            CONSTANTS
  //=================================================================================

  //=================================================================================
  //                                      CONSTRUCT0RS
  //=================================================================================
  /** Unique constructor */
  public NewCurveDialog() throws Exception {
    super(GlobalVar.getMainFrame(), _myBundle.getString("Dialog.Title"), true);
//System.out.println("NewCuveDialog.constructor()");

    // GUI is initialized with last values in initFields()

    try{
        Container contentPane = this.getContentPane();

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        JButton btnTest1 = new JButton("Uranus Neptune 1993");
        btnTest1.addActionListener(new TestListener(1));
        buttonPanel.add(btnTest1);
        JButton btnTest2 = new JButton("Retrogradation Venus 2002");
        btnTest2.addActionListener(new TestListener(2));
        buttonPanel.add(btnTest2);
        JButton btnTest3 = new JButton("Comparison JEphem / Swiss Ephemeris");
        btnTest3.addActionListener(new TestListener(3));
        buttonPanel.add(btnTest3);
        JButton btnTest4 = new JButton("V M J S 2002");
        btnTest4.addActionListener(new TestListener(4));
        buttonPanel.add(btnTest4);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
        // center on its parent
        Dimension d1 = this.getParent().getSize();
        Dimension d2 = this.getSize();
        int x = Math.max(0, ((d1.width - d2.width) / 2));
        int y = Math.max(0, ((d1.height - d2.height) / 2));
        this.setLocation(x, y);

        //this.initFields();
    }
    catch(Exception e){
      throw e;
    }
  }//end NewCurveDialog()

  //=================================================================================
  //                               AUXILIARY METHODS
  //=================================================================================

  //=================================================================================
  //                            INNER CLASSES
  //=================================================================================

  //*****************************************************************
  //	      							EVENT LISTENNERS
  //*****************************************************************

  //*****************************************************************
  /** Contains validity checking and building of an Ephemeris object.*/
  class TestListener implements ActionListener {
    private int _whichTest;
    public TestListener(int whichTest){
      _whichTest = whichTest;
    }
    public void actionPerformed(ActionEvent e) {
      try{

        // 1 - Build a curve
        // prepare variables
        double[][] curveData;
        double[][][] ephData;
        int[] bodyIndexes;
        double jd0, jdF, interval, deltaT;
        double[] jds;
        int nbValues;
        int[] whichCoords, coordUnits;
        jephem.tools.Ephemeris eph;

        switch(_whichTest){
          case 0 : // TEST x, x2, sqrt(x)
            curveData = new double[4][20];
            for (int i = 0; i < 20; i++) {
              double d = (double)i;
              curveData[0][i] = d;
              curveData[1][i] = d; // y = x
              curveData[2][i] = d*d;
              curveData[3][i] = Math.sqrt(d);
            }
          break;

          // TEST conj Uranus Neptune
          case 1 :
            bodyIndexes = new int[]{URANUS, NEPTUNE};
            nbValues = 100;
            jd0 = 2448988.5; // jan 01 1993
            interval = 3.65;
            jds = new double[nbValues];
            for(int i = 0; i < jds.length; i++){
              jds[i] = jd0 + (double)i*interval;
            }
            whichCoords = new int[]{COORD_X1};
            coordUnits = UNITGROUP_AU_DEG_DEG; // obliged because of AstroContext implementation
            eph = new jephem.tools.Ephemeris(bodyIndexes,
                                             jds,
                                             TimeConstants.UTC,
                                             whichCoords,
                                             coordUnits,
                                             FRAME_ECLIPTIC,
                                             SPHERICAL,
                                             1,
                                             jephem.astro.AstroEngine.SWISS_EPHEMERIS,
                                             false);
            curveData = new double[bodyIndexes.length + 1][nbValues];
            curveData[0] = jds;
            ephData = eph.getData();
            for (int i = 0; i < ephData.length; i++) {
              curveData[1][i] = ephData[i][0][0];
              curveData[2][i] = ephData[i][1][0];
            }
          break;

          // TEST Retro VENUS
          case 2 :
            bodyIndexes = new int[]{VENUS};
//            jd0 = 2452527.5; // sep 10 2002
//            jdF = 2452588.5; // nov 10 2002
            jd0 = 2439686.5; // jul 15 1967
            jdF = 2439793.5; // oct 30 1967
            deltaT = jdF - jd0;
            nbValues = 100;
            interval = deltaT/(double)nbValues;
            jds = new double[nbValues];
            for(int i = 0; i < jds.length; i++){
              jds[i] = jd0 + (double)i*interval;
            }
            whichCoords = new int[]{COORD_X1, COORD_X2};
            coordUnits = UNITGROUP_AU_DEG_DEG; // obliged because of AstroContext implementation
            eph = new jephem.tools.Ephemeris(bodyIndexes,
                                                                    jds,
                                                                    TimeConstants.UTC,
                                                                    whichCoords,
                                                                    coordUnits,
                                                                    FRAME_ECLIPTIC,
                                                                    SPHERICAL,
                                                                    1,
                                                                    jephem.astro.AstroEngine.SWISS_EPHEMERIS,
                                                                    false);
            curveData = new double[whichCoords.length][nbValues];
            ephData = eph.getData();
            for (int i = 0; i < ephData.length; i++){
              curveData[0][i] = ephData[i][0][0];
              curveData[1][i] = ephData[i][0][1];
            }
          break;

          // TEST Comparison JEphem - SwissEphemeris
          case 3 :
            bodyIndexes = new int[]{VENUS};
            jd0 = 2452419.6; // in may 2002
            jdF = 2439686.7; // a little later
            deltaT = jdF - jd0;
            nbValues = 100;
            interval = deltaT/(double)nbValues;
            jds = new double[nbValues];
            for(int i = 0; i < jds.length; i++){
              jds[i] = jd0 + (double)i*interval;
            }
            whichCoords = new int[]{COORD_X1};
            coordUnits = UNITGROUP_AU_DEG_DEG; // obliged because of AstroContext implementation
            jephem.tools.Ephemeris eph1 = new jephem.tools.Ephemeris(bodyIndexes,
                                                                    jds,
                                                                    TimeConstants.UTC,
                                                                    whichCoords,
                                                                    coordUnits,
                                                                    FRAME_ECLIPTIC,
                                                                    SPHERICAL,
                                                                    1,
                                                                    jephem.astro.AstroEngine.SWISS_EPHEMERIS,
                                                                    false);
            jephem.tools.Ephemeris eph2 = new jephem.tools.Ephemeris(bodyIndexes,
                                                                    jds,
                                                                    TimeConstants.UTC,
                                                                    whichCoords,
                                                                    coordUnits,
                                                                    FRAME_ECLIPTIC,
                                                                    SPHERICAL,
                                                                    1,
                                                                    jephem.astro.AstroEngine.JEPHEM,
                                                                    false);
            //double[][] curveData = new double[3][nbValues];
            curveData = new double[3][nbValues];
            curveData[0] = jds;
            double[][][] ephData1 = eph1.getData();
            double[][][] ephData2 = eph2.getData();
            for (int i = 0; i < ephData1.length; i++){
              //curveData[1][i] = ephData1[i][0][0];
              //curveData[2][i] = ephData2[i][0][0];
              curveData[1][i] = ephData1[i][0][0] - ephData2[i][0][0];
            }
          break;

          // TEST Venus Mars Jupiter Saturn 2002
          case 4 :
            bodyIndexes = new int[]{VENUS, MARS, JUPITER, SATURN};
            nbValues = 100;
            jd0 = 2452395.5; // may 01 2002
            jdF = 2452470.5; // july 15 2002
            deltaT = jdF - jd0;
            interval = deltaT/(double)nbValues;
            jds = new double[nbValues];
            for(int i = 0; i < jds.length; i++){
              jds[i] = jd0 + (double)i*interval;
            }
            whichCoords = new int[]{COORD_X1};
            coordUnits = UNITGROUP_AU_DEG_DEG; // obliged because of AstroContext implementation
            eph = new jephem.tools.Ephemeris(bodyIndexes,
                                             jds,
                                             TimeConstants.UTC,
                                             whichCoords,
                                             coordUnits,
                                             FRAME_ECLIPTIC,
                                             SPHERICAL,
                                             1,
                                             jephem.astro.AstroEngine.SWISS_EPHEMERIS,
                                             false);
            curveData = new double[bodyIndexes.length + 1][nbValues];
            curveData[0] = jds;
            ephData = eph.getData();
            for (int i = 0; i < ephData.length; i++) {
              curveData[1][i] = ephData[i][0][0];
              curveData[2][i] = ephData[i][1][0];
              curveData[3][i] = ephData[i][2][0];
              curveData[4][i] = ephData[i][3][0];
            }
          break;

          default :
            throw new IllegalArgumentException("Bad parameter in TestListener constructor");
        }// end switch(_whichTest)

        // Add a 'Modify' menuItem

        // Save current state of the dialog for restoration

        NewCurveDialog.this.dispose();

        JPanel centralPanel = GlobalVar.getMainFrame().getCentralPanel();
        CurveViewer cv = new CurveViewer(curveData);
//System.out.println(cv.getCurve().toString());
        centralPanel.removeAll();
        centralPanel.add(cv, BorderLayout.CENTER);
        centralPanel.invalidate();
        centralPanel.repaint();
        centralPanel.validate();
      }
      catch(Exception je){
        Debug.traceError(je);
      }

    } // end actionPerformed
  } // end class TestListener

  //*****************************************************************
  class CancelListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      dispose();
    } // end actionPerformed
  } // end class CancelListener

}// end class NewCurveDialog
