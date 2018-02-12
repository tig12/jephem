//*********************************************************************************
// class jephem.gui.NewEphemerisDialog
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.gui;

import jephem.gui.UtilsGUI;

import jephem.gui.SaveListener;
import jephem.GlobalVar;
import jephem.tools.AstroPrefs;
import jephem.tools.Ephemeris;
import jephem.astro.AstroEngine;
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
import tig.Strings;
import tig.Integers;
import tig.Dates;
import tig.TigBundle;
import tig.TigProperties;
import tig.DateFormatException;
import tig.HourFormatException;
import tig.swing.JTextField2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Vector;
import java.util.Calendar;

/******************************************************************
Modal dialog displayed when a new Ephemeris is asked by the user.
<BR>The purpose of this class is to handle the GUI to construct a new {@link jephem.tools.Ephemeris}
object and order its display.

@see jephem.tools.Ephemeris

@author Thierry Graff
@history sep 16 2001 : Creation

@todo handle date computation with Calendar
@todo WARNING : _chkPlanets[i] and planet index (from SolarSystemConstants) are associated in two places
      in the code (in initFields(), and ...). This wouldn't work if not all planets were represented.
@todo handle message if initFields couldn't load data from .last file
@todo handle message if ressource bundles can't be loded
@todo initialize default value for start date with current date.
@todo factorize checking code in OKListener, when other classes need it.
*****************************************************************/
public class NewEphemerisDialog extends JDialog
             implements GeneralConstants, SolarSystemConstants, SpaceConstants, UnitsConstants{

  //=================================================================================
  //                            INSTANCE VARIABLES
  //=================================================================================

  // Useful variables
  private int[]         _availableFrames;
  private int[]         _curBodies;

  // GUI Variables.
  private JTabbedPane _tabPane;

  // general tab
  private JTextField2    _txtPrecision;
  private JCheckBox[]   _chkPlanets;
  private JComboBox     _cmbAstroEngine;
  private JCheckBox     _chkSymbols;
  private JRadioButton  _btnInstantsInRows;
  private JRadioButton  _btnInstantsInCols;
  private JCheckBox     _chkDisplayErrorMsg;
  // date tab
  private JTextField2    _txtBeginDate;
  private JTextField2    _txtBeginHour;
  private JTextField2    _txtDisplayEvery;
  private JComboBox     _cmbDisplayEvery;
  private JRadioButton  _btnDisplayDates;
  private JRadioButton  _btnDisplayJDs;
  private JComboBox     _cmbTimeFrame;
  private JRadioButton  _btnSpecifyNbDates;
  private JRadioButton  _btnSpecifyEndDate;
  private JTextField2    _txtEndDate;
  private JTextField2    _txtEndHour;
  private JTextField2    _txtNbDates;
  // coordinate tab
  private JList         _lstFrames;
  private JCheckBox[]   _chkCoord = new JCheckBox[6];
  private JComboBox[]   _cmbUnit = new JComboBox[6];
  private JRadioButton  _btnSphere;
  private JRadioButton  _btnCart;
  private JRadioButton  _btnDMS;
  private JRadioButton  _btnDecDeg;

  //=================================================================================
  //                          RESSOURCE BUNDLES (STATIC VARIABLES)
  //=================================================================================
  // ***** Ressource bundles. ******
  private static TigBundle _myBundle, _galBundle, _astroBundle, _timeBundle;
  static{
    _galBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL);
    _astroBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_ASTRO);
    try{
      _myBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "NewEphemerisDialog.lang",
                                                             GlobalVar.getLocale());
      _timeBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "Time.lang",
                                                              GlobalVar.getLocale());
    }
    catch (IOException ioe){
      Debug.traceError(ioe);
    }
  };

  //=================================================================================
  //                            VARIABLES FOR LAST VALUES (static + constants)
  //=================================================================================
  /* Variable containing data of last values. */
  private static TigProperties _lastValues;

  /** Indicates if the last values have already been lodaed*/
  private static boolean  _lastValuesLoaded = false;

  /** File in which last values are stored. */
  private static final File LAST_VALUES_FILE =
      new File(GlobalVar.getDirectory(GlobalVar.DIR_PREFS) + FS + "jephem" + FS + "gui" + FS + "NewEphemerisDialog.last");

  /** Comment line at the beginning of last values file. */
  private static final String LAST_VALUES_HEADER =
                        "Last values entered by the user in NewEphemerisDialog";

  private static final String LAST_SELECTED_TAB_KEY             = "lastSelectedTab";
  private static final int LAST_SELECTED_TAB_DEFAULT            = 0;

  private static final String LAST_PLANETS_KEY                  = "lastPlanets";
  private static final int[] LAST_PLANETS_DEFAULT = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
  private static final String LAST_PRECISION_KEY                = "lastPrecision";
  private static final String LAST_PRECISION_DEFAULT            = "1";
  private static final String LAST_ASTROENGINE_KEY              = "lastAstroEngine";
  private static final String LAST_ASTROENGINE_DEFAULT          = AstroEngine.JEPHEM;
  private static final String LAST_USE_SYMBOLS_KEY              = "lastUseSymbols";
  private static final boolean LAST_USE_SYMBOLS_DEFAULT         = false;
  private static final String LAST_INSTANTS_IN_ROWCOL_KEY       = "lastInstantsInRowCol";
  private static final int LAST_INSTANTS_IN_ROWCOL_DEFAULT      = Ephemeris.INSTANTS_IN_ROWS;
  private static final String LAST_DISPLAY_ERROR_MESSAGE_KEY       = "lastDisplayErrorMsg";
  private static final boolean LAST_DISPLAY_ERROR_MESSAGE_DEFAULT  = false;
  private static final String LAST_BEGIN_DATE_KEY               = "lastBeginDate";
//  private static final String LAST_BEGIN_DATE_DEFAULT           = "01/01/2000";
  private static final String LAST_BEGIN_HOUR_KEY               = "lastBeginHour";
//  private static final String LAST_BEGIN_HOUR_DEFAULT           = "00h00m00s";
  private static final String LAST_TXT_DISPLAY_EVERY_KEY        = "lastTxtDisplayEvery";
  private static final String LAST_TXT_DISPLAY_EVERY_DEFAULT    = "1";
  private static final String LAST_LST_DISPLAY_EVERY_KEY        = "lastLstDisplayEvery";
  private static final int LAST_LST_DISPLAY_EVERY_DEFAULT       = 3;
  private static final String LAST_DISPLAY_DATE_JDS_KEY         = "lastDisplayDateJDs";
  private static final int LAST_DISPLAY_DATE_JDS_DEFAULT        = Ephemeris.DISPLAY_DATES;
  private static final String LAST_TIME_FRAME_KEY               = "lastTimeFrame";
  private static final int LAST_TIME_FRAME_DEFAULT              = TimeConstants.UTC;
  private static final String LAST_SPECIFY_NB_DATES_KEY         = "lastSpecifyNbDates";
  private static final boolean LAST_SPECIFY_NB_DATES_DEFAULT    = true;
  private static final String LAST_NB_DATES_KEY                 = "lastNbDates";
  private static final String LAST_NB_DATES_DEFAULT             = "10";
  private static final String LAST_END_DATE_KEY                 = "lastEndDate";
  private static final String LAST_END_DATE_DEFAULT             = BLANK;
  private static final String LAST_END_HOUR_KEY                 = "lastEndHour";
  private static final String LAST_END_HOUR_DEFAULT             = BLANK;
  private static final String LAST_FRAME_KEY                    = "lastFrame";
  private static final int LAST_FRAME_DEFAULT                   = FRAME_THEORY ;
  private static final String LAST_SELECTED_COORDS_KEY          = "lastSelectedCoords";
  private static final int[] LAST_SELECTED_COORDS_DEFAULT       = new int[]{0, 1, 2};
  private static final String LAST_UNITS_KEY                    = "lastUnits";
  private static final int[] LAST_UNITS_DEFAULT = new int[]{0, 0, 0, 0, 0, 0};
  private static final String LAST_COORD_EXPR_KEY               = "lastCoordExpr";
  private static final int LAST_COORD_EXPR_DEFAULT              = SPHERICAL;
  private static final String LAST_DEGREE_EXPR_KEY              = "lastDegreeExpr";
  private static final int LAST_DEGREE_EXPR_DEFAULT             = Units.DEGREES_DECIMAL;

  //=================================================================================
  //                            CONSTANTS
  //=================================================================================
  // Index of panels within the tabbed pane.
  private static final int GENERAL_PANEL_INDEX = 0;
  private static final int DATE_PANEL_INDEX = 1;
  private static final int COORD_PANEL_INDEX = 2;

  // just used for more readable code
  private static final int NB_COORD = 3;

  //=================================================================================
  //                                      CONSTRUCT0RS
  //=================================================================================
  /** Unique constructor */
  public NewEphemerisDialog() throws Exception {
    super(GlobalVar.getMainFrame(), _myBundle.getString("Dialog.Title"), true);
    //System.out.println("NewEphemerisDialog.constructor()");

    // GUI is initialized with last values in initFields()

    try{
        // ****************************
        // ***** GUI construction *****
        // ****************************
        Container contentPane = this.getContentPane();
        //********************************************************
        // 1 - General pane : planets, precision, astroEngine and layout
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.X_AXIS));
        generalPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // 1.1 - List containing planets
        // 1.1 - Check boxes containing planets
        // Build the array of names of current bodies.
        _curBodies = GlobalVar.getAstroPrefs().getIntArrayProperty(AstroPrefs.KEY_CURRENT_BODIES);
        String[] allPlanetNames = Strings.stringToStringArray(_astroBundle.getString("planetNames"));
        String[] planetNames = new String[_curBodies.length];
        for (int i = 0; i < planetNames.length; i++){
          if(_curBodies[i] == SUN || _curBodies[i] == EARTH)
            planetNames[i] = allPlanetNames[SUN] + " / " + allPlanetNames[EARTH];
          else
          planetNames[i] = allPlanetNames[_curBodies[i]];
        }
        allPlanetNames = null;
        JPanel planetsSubPanel = new JPanel();
        planetsSubPanel.setLayout(new BoxLayout(planetsSubPanel, BoxLayout.Y_AXIS));
        planetsSubPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
        _chkPlanets = new JCheckBox[planetNames.length];
        JPanel chkPlanetsPanel = new JPanel(new GridLayout(0,1,0,0)); // also sets gaps
        chkPlanetsPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
        for (int i = 0; i < planetNames.length; i++){
          _chkPlanets[i] = new JCheckBox(planetNames[i], true);
          chkPlanetsPanel.add(_chkPlanets[i]);
        }
        JScrollPane planetScrollPane = new JScrollPane(chkPlanetsPanel);
        JPanel p11 = new JPanel(new FlowLayout(FlowLayout.LEFT)); // to have 'Planets' label aligned left
        p11.add(new JLabel(_astroBundle.getString("Planets")));
        planetsSubPanel.add(p11);
        planetsSubPanel.add(planetScrollPane);

        // 1.2 - Precision
        JPanel precisionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblPrecision = new JLabel(_astroBundle.getString("Precision") + " :");
        lblPrecision.setAlignmentX(0.0f);
        precisionPanel.add(lblPrecision);
        _txtPrecision = new JTextField2(GlobalVar.getAstroPrefs().getProperty(AstroPrefs.KEY_PRECISION), 5);
        precisionPanel.add(_txtPrecision);
        precisionPanel.add(new JLabel(_astroBundle.getString("arcSeconds")));

        //1.2 bis - Astro Engine
        JPanel astroEnginePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        astroEnginePanel.add(new JLabel(_myBundle.getString("AstroEngine")));
        _cmbAstroEngine = new JComboBox(new String[]{"JEphem", "SwissEphemeris"});
        astroEnginePanel.add(_cmbAstroEngine);

        //1.3 - Appearence
        JPanel appearencePanel = new JPanel();
        appearencePanel.setLayout(new BoxLayout(appearencePanel, BoxLayout.Y_AXIS));
        appearencePanel.setBorder(BorderFactory.createTitledBorder(_myBundle.getString("Appearence")));
        // use symbols check box
        JPanel symbolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        _chkSymbols = new JCheckBox(_myBundle.getString("UseSymbols"));
        symbolPanel.add(_chkSymbols);
        appearencePanel.add(symbolPanel);
        //instants in rows or cols
//        JPanel rowColPanel = new JPanel(new GridLayout(2, 0));
        JPanel rowColPanel = new JPanel();
        rowColPanel.setLayout(new BoxLayout(rowColPanel, BoxLayout.Y_AXIS));
        _btnInstantsInRows = new JRadioButton(_myBundle.getString("InstantsInRows"));
        _btnInstantsInRows.setSelected(true);
        rowColPanel.add(_btnInstantsInRows);
        _btnInstantsInCols = new JRadioButton(_myBundle.getString("InstantsInCols"));
        rowColPanel.add(_btnInstantsInCols);
        ButtonGroup rowColButtonGroup = new ButtonGroup();
        rowColButtonGroup.add(_btnInstantsInRows);
        rowColButtonGroup.add(_btnInstantsInCols);
//        rowColPanel.setMaximumSize(rowColPanel.getPreferredSize());
        JPanel rowColPanel2 = new JPanel(new BorderLayout());
        rowColPanel2.add(rowColPanel, BorderLayout.WEST);
        appearencePanel.add(rowColPanel2);
        // Display error messages
        _chkDisplayErrorMsg = new JCheckBox(_myBundle.getString("DisplayErrorMessages"));
        JPanel displayErrorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        displayErrorPanel.add(_chkDisplayErrorMsg);

        // 1.4 - GeneralPanel layout
        generalPanel.add(planetsSubPanel);

        JPanel miscPanel = new JPanel();
        miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));
        miscPanel.add(precisionPanel);
        miscPanel.add(astroEnginePanel);
        miscPanel.add(appearencePanel);
        miscPanel.add(displayErrorPanel);
        JPanel miscPanel2 = new JPanel(new BorderLayout());
        miscPanel2.add(miscPanel, BorderLayout.NORTH);
//        p14.add(Box.createRigidArea(new Dimension(0, 75)));
        generalPanel.add(miscPanel2);

        //********************************************************
        // 2 - Date panel
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
        datePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // 2.1 - left date panel (left panel of date TabStrip)
        JPanel leftDatePanel = new JPanel();
        leftDatePanel.setLayout(new BoxLayout(leftDatePanel, BoxLayout.Y_AXIS));

        // 2.1.1 beginDateHour sub panel
        JPanel beginDateHourSubPanel = new JPanel();
        beginDateHourSubPanel.setLayout(new BoxLayout(beginDateHourSubPanel, BoxLayout.Y_AXIS));
        beginDateHourSubPanel.setBorder(BorderFactory.createEtchedBorder());
        // 2.1.1.1 begin date label and text fields
        JPanel p2111 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2111.add(new JLabel(_myBundle.getString("BeginDate")));
        _txtBeginDate = new JTextField2(10);
        p2111.add(_txtBeginDate);
        // 2.1.1.2 beginHour label and text fields
        JPanel p2112 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2112.add(new JLabel(_myBundle.getString("BeginHour")));
        _txtBeginHour = new JTextField2(8);
        p2112.add(_txtBeginHour);
        // 2.1.1.3 Buttons 'Now' and 'Today'
        JPanel p2113 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNow = new JButton(_timeBundle.getString("Now"));
        btnNow.setMargin(new Insets(0, 0, 0, 0));
        btnNow.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent ae){
            Calendar cal = Calendar.getInstance();
            _txtBeginDate.setText(Dates.formatDate(new int[]{
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)},
                GlobalVar.getLang()));
            _txtBeginHour.setText(Dates.formatHour(new int[]{
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)},
                GlobalVar.getLang()));
          }
        });
        JButton btnToday = new JButton(_timeBundle.getString("Today"));
        btnToday.setMargin(new Insets(0, 0, 0, 0));
        btnToday.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent ae){
            Calendar cal = Calendar.getInstance();
            _txtBeginDate.setText(Dates.formatDate(new int[]{
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)},
                GlobalVar.getLang()));
            _txtBeginHour.setText("00:00:00");
          }
        });
        p2113.add(btnToday);
        p2113.add(btnNow);
        // 2.1.1.4 beginDateHourSubPanel layout
        beginDateHourSubPanel.add(p2111);
        beginDateHourSubPanel.add(p2112);
        beginDateHourSubPanel.add(p2113);

        // 2.2 - end date panel
        JPanel endDatePanel = new JPanel();
        endDatePanel.setLayout(new BoxLayout(endDatePanel, BoxLayout.Y_AXIS));
        endDatePanel.setBorder(BorderFactory.createEtchedBorder());

        // 2.2.0 create radio buttons and listener
        NumberOrEndDateActionListener noedListener = new NumberOrEndDateActionListener();
        _btnSpecifyNbDates = new JRadioButton(_myBundle.getString("SpecifyNbDates"));
        _btnSpecifyNbDates.setSelected(true);
        _btnSpecifyNbDates.addActionListener(noedListener);
        _btnSpecifyEndDate = new JRadioButton(_myBundle.getString("SpecifyEndDate"));
        _btnSpecifyEndDate.addActionListener(noedListener);
        ButtonGroup specifyButtonGroup = new ButtonGroup();
        specifyButtonGroup.add(_btnSpecifyNbDates);
        specifyButtonGroup.add(_btnSpecifyEndDate);

        // 2.2.1 specify nb of date sub panel
        JPanel specifyNbDateSubPanel = new JPanel();
        specifyNbDateSubPanel.setLayout(new BoxLayout(specifyNbDateSubPanel, BoxLayout.Y_AXIS));
        // 2.2.1.1 Button, label and text for nb dates.
        JPanel p2211_0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2211_0.add(_btnSpecifyNbDates);
        JPanel p2211_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblNbDates = new JLabel(_myBundle.getString("NbDates"));
        p2211_1.add(lblNbDates);
        _txtNbDates = new JTextField2(4);
        lblNbDates.setLabelFor(_txtNbDates);
        p2211_1.add(_txtNbDates);
        // 2.2.1.2 specifyNbDateSubPanel layout
        specifyNbDateSubPanel.add(p2211_0);
        specifyNbDateSubPanel.add(p2211_1);

        // 2.2.2 specify end date sub panel
        JPanel specifyEndDateSubPanel = new JPanel();
        specifyEndDateSubPanel.setLayout(new BoxLayout(specifyEndDateSubPanel, BoxLayout.Y_AXIS));
        // button, label and text for end date
        JPanel p222_0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p222_0.add(_btnSpecifyEndDate);
        JPanel p222_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p222_1.add(new JLabel(_myBundle.getString("EndDate")));
        _txtEndDate = new JTextField2(10);
        p222_1.add(_txtEndDate);
        JPanel p222_2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p222_2.add(new JLabel(_myBundle.getString("EndHour")));
        _txtEndHour = new JTextField2(8);
        p222_2.add(_txtEndHour);
        // specifyEndDateSubPanel layout
        specifyEndDateSubPanel.add(p222_0);
        specifyEndDateSubPanel.add(p222_1);
        specifyEndDateSubPanel.add(p222_2);

        // call to setEditable of the right JTextfields
        nbOrEndDateChoosen();

        // 2.2.3 end date panel layout
        endDatePanel.add(specifyEndDateSubPanel);
        endDatePanel.add(specifyNbDateSubPanel);

        // 2.2.4 left date panel layout
        leftDatePanel.add(beginDateHourSubPanel);
        JPanel endDatePanel2 = new JPanel(new BorderLayout());
        endDatePanel2.add(endDatePanel);
        leftDatePanel.add(endDatePanel2);

        // 2.3 - right date panel
        // 2.3.1 displayEvery sub panel
        JPanel displayEverySubPanel = new JPanel();
        displayEverySubPanel.setLayout(new BoxLayout(displayEverySubPanel, BoxLayout.Y_AXIS));
        // label "display every"
        JPanel p2121 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2121.add(new JLabel(_myBundle.getString("DisplayEvery")));
        // text field and list
        JPanel p2122 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        _txtDisplayEvery = new JTextField2(5);
        p2122.add(_txtDisplayEvery);
        String[] timeUnits = {
                              _timeBundle.getString("years"),
                              _timeBundle.getString("months"),
                              _timeBundle.getString("weeks"),
                              _timeBundle.getString("days"),
                              _timeBundle.getString("hours"),
                              _timeBundle.getString("minuts"),
                              _timeBundle.getString("seconds"),
                             };
        _cmbDisplayEvery = new JComboBox(timeUnits);
        _cmbDisplayEvery.setSelectedIndex(3); // default is days
        p2122.add(_cmbDisplayEvery);
        // displayEverySubPanel layout
        displayEverySubPanel.add(p2121);
        displayEverySubPanel.add(p2122);

        // 2.3.2 Time frame sub panel
        JPanel timeFrameSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeFrameSubPanel.add(new JLabel(_myBundle.getString("TimeFrame") + " : "));
        _cmbTimeFrame = new JComboBox(new String[]{_timeBundle.getString("UTC"), _timeBundle.getString("TT-TDB")});
        timeFrameSubPanel.add(_cmbTimeFrame);

        // 2.3.4 displayDateOrJD sub panel
        JPanel displayDateOrJDSubPanel = new JPanel(new GridLayout(0,1));
        displayDateOrJDSubPanel.setBorder(BorderFactory.createTitledBorder(
                                          _myBundle.getString("DateDisplay")));
        _btnDisplayDates = new JRadioButton(_myBundle.getString("Dates"));
        _btnDisplayJDs = new JRadioButton(_myBundle.getString("JulianDays"));
        ButtonGroup dateOrJdButtonGroup = new ButtonGroup();
        dateOrJdButtonGroup.add(_btnDisplayDates);
        dateOrJdButtonGroup.add(_btnDisplayJDs);
        displayDateOrJDSubPanel.add(_btnDisplayDates);
        displayDateOrJDSubPanel.add(_btnDisplayJDs);
        _btnDisplayDates.setSelected(true);

        // 2.3.5 right date panel layout
        JPanel rightDatePanel = new JPanel();
        rightDatePanel.setLayout(new BoxLayout(rightDatePanel, BoxLayout.Y_AXIS));
        rightDatePanel.add(displayEverySubPanel);
        rightDatePanel.add(timeFrameSubPanel);
        rightDatePanel.add(displayDateOrJDSubPanel);

        // 2.4 - Date panel layout
        JPanel leftDatePanel2 = new JPanel(new BorderLayout());
        leftDatePanel2.add(leftDatePanel, BorderLayout.NORTH);
        datePanel.add(leftDatePanel2);

        datePanel.add(Box.createRigidArea(new Dimension(10,0)));

        JPanel rightDatePanel2 = new JPanel(new BorderLayout());
        rightDatePanel2.add(rightDatePanel, BorderLayout.NORTH);
        datePanel.add(rightDatePanel2);

        //********************************************************
        // 3 - Coordinates panel
        JPanel coordPanel = new JPanel();
        coordPanel.setLayout(new BoxLayout(coordPanel, BoxLayout.X_AXIS));
        coordPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // 3.1 Frames sub panel
        JPanel framesSubPanel = new JPanel(new FlowLayout());
        framesSubPanel.setBorder(BorderFactory.createTitledBorder(_astroBundle.getString("Frame")));
        // get the names of available frames
        _availableFrames = GlobalVar.getAstroPrefs().getIntArrayProperty(AstroPrefs.KEY_AVAILABLE_FRAMES);
        String[] frameNames = Strings.stringToStringArray(_astroBundle.getString("frameLabels"));
        _lstFrames = new JList(frameNames);
        _lstFrames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _lstFrames.setSelectedIndex(0);
        _lstFrames.ensureIndexIsVisible(0);
        _lstFrames.setVisibleRowCount(4);
        _lstFrames.addListSelectionListener(new FrameListSelectionListener());
        framesSubPanel.add(new JScrollPane(_lstFrames));

        // 3.2 Coordinates sub panel
        JPanel coordSubPanel = new JPanel(new GridLayout(0,2,4,0));
        coordSubPanel.setBorder(BorderFactory.createTitledBorder(
                                                  _myBundle.getString("RepresentedCoordinates")));
        // add components
        coordSubPanel.add(new JLabel(_astroBundle.getString("Coordinate")));
        coordSubPanel.add(new JLabel(_astroBundle.getString("Unit")));
        // build coord check boxes and unit combos
        for (int i = 0; i < 6; i++){
          _chkCoord[i] = new JCheckBox();
          coordSubPanel.add(_chkCoord[i]);
          _cmbUnit[i] = new JComboBox();
          coordSubPanel.add(_cmbUnit[i]);
        }

        // 3.3 Miscelaneous sub panel
        JPanel miscSubPanel = new JPanel();
        miscSubPanel.setLayout(new BoxLayout(miscSubPanel, BoxLayout.Y_AXIS));
        miscSubPanel.setBorder(BorderFactory.createEtchedBorder());
        // 3.3.1 Spherical / cartesian
        miscSubPanel.add(new JLabel(_myBundle.getString("CoordinateExpression")));
        SphereCartActionListener scListener = new SphereCartActionListener();
        _btnSphere = new JRadioButton(_astroBundle.getString("Spherical"));
        _btnSphere.addActionListener(scListener);
        _btnSphere.setSelected(true);
        _btnCart = new JRadioButton(_astroBundle.getString("Cartesian"));
        _btnCart.addActionListener(scListener);
        ButtonGroup sphereCartButtonGroup = new ButtonGroup();
        sphereCartButtonGroup.add(_btnSphere);
        sphereCartButtonGroup.add(_btnCart);
        miscSubPanel.add(_btnSphere);
        miscSubPanel.add(_btnCart);

        //setCoordLabels now done in initFields.
        //this.setCoordLabels(); // Call to coord Labels depending on the selected frame

        // 3.3.2 Dec deg / DMS
        miscSubPanel.add(Box.createRigidArea(new Dimension(0,10)));
        miscSubPanel.add(new JLabel(_myBundle.getString("DegreesExpression")));
        _btnDMS = new JRadioButton(_myBundle.getString("DMS"));
        _btnDMS.setSelected(true);
        _btnDecDeg = new JRadioButton(_myBundle.getString("DecDeg"));
        ButtonGroup degExprButtonGroup = new ButtonGroup();
        degExprButtonGroup.add(_btnDMS);
        degExprButtonGroup.add(_btnDecDeg);
        miscSubPanel.add(_btnDMS);
        miscSubPanel.add(_btnDecDeg);

        // 3.4 Coordinates panel layout
        JPanel panel34 = new JPanel();
        panel34.setLayout(new BoxLayout(panel34, BoxLayout.Y_AXIS));
        framesSubPanel.setAlignmentX(LEFT_ALIGNMENT);
        miscSubPanel.setAlignmentX(LEFT_ALIGNMENT);
        panel34.add(framesSubPanel);
        panel34.add(miscSubPanel);
        coordPanel.add(panel34);

        JPanel coordSubPanel2 = new JPanel(new BorderLayout());
        coordSubPanel2.add(coordSubPanel, BorderLayout.NORTH);
        coordPanel.add(coordSubPanel2);

        //******************************************
        // 4 - Buttons
        JButton btnOK = new JButton(_galBundle.getString("action.OK"));
        btnOK.addActionListener(new OKListener());
        JButton btnCancel = new JButton(_galBundle.getString("action.Cancel"));
        btnCancel.addActionListener(new CancelListener());
        // layout

        JPanel buttonPanel = new JPanel(new BorderLayout());//contains buttons
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2,0,0,0));
        JPanel buttonPanel2 = new JPanel(new GridLayout(0, 2, 5, 0));
        buttonPanel2.add(btnCancel);
        buttonPanel2.add(btnOK);
        buttonPanel.add(buttonPanel2, BorderLayout.EAST);

        // general packing
        _tabPane = new JTabbedPane();
        _tabPane.addTab(_myBundle.getString("General"), generalPanel);
        _tabPane.addTab(_myBundle.getString("Dates"), datePanel);
        _tabPane.addTab(_myBundle.getString("Coordinates"), coordPanel);

        contentPane.add(_tabPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
        // center on its parent
        Dimension d1 = this.getParent().getSize();
        Dimension d2 = this.getSize();
        int x = Math.max(0, ((d1.width - d2.width) / 2));
        int y = Math.max(0, ((d1.height - d2.height) / 2));
//        int x = (d1.width - d2.width) / 2;
//        int y = (d1.height - d2.height) / 2;
        this.setLocation(x, y);

        this.initFields();
    }
    catch(Exception e){
      throw e;
    }
  }//end NewEphemerisDialog()

  //=================================================================================
  //                               AUXILIARY METHODS
  //=================================================================================

  //******************* setCoordLabels ********************************
  /** Depending on the selected frame and expression mode (spherical / cartesian), sets the labels
  of check boxes and combos used to select the coordinates and units.
  */
  private void setCoordLabels(){
    int i,j;
    // 1 - Check boxes (coordinates)
    String[] coordLabels = new String[NB_COORD];
    int coordGroup;
    if (_btnCart.isSelected()){
      coordGroup = COORDGROUP_XYZ;
    }
    else{ // spherical, so need to see which frame is selected
      // we use the fact that indices of _lstFrames correspond to indices of  _availableFrames
      int frame = _availableFrames[_lstFrames.getSelectedIndex()];
      coordGroup = Space.getCoordGroup(frame);
    }
    coordLabels = Space.getCoordGroupLabels(coordGroup);
    // Set checkBox texts and empty combo boxes;
    for (i = 0; i < 3; i++){
      _chkCoord[i].setText(coordLabels[i]);
      _cmbUnit[i].removeAllItems();
    }
    for (i = 0; i < 3; i++){
      _chkCoord[i+3].setText(coordLabels[i] + "'");
      _cmbUnit[i+3].removeAllItems();
    }
    coordLabels = null;

    // 2 - combo boxes (units)
    String[] unitLabels;
    if (coordGroup == COORDGROUP_XYZ){
      unitLabels = Units.getUnitLabels(TYPE_DISTANCE);
      for (i = 0; i < NB_COORD; i++){
        for(j = 0; j < unitLabels.length; j++){
          _cmbUnit[i].addItem(unitLabels[j]);
        }
      }
      unitLabels=null;
      unitLabels = Units.getUnitLabels(TYPE_LINEAR_SPEED);
      for (i = 0; i < NB_COORD; i++){
        for(j = 0; j < unitLabels.length; j++){
          _cmbUnit[i+NB_COORD].addItem(unitLabels[j]);
        }
      }
      unitLabels=null;
    }
    else{ // spherical coordinates
      unitLabels = Units.getUnitLabels(TYPE_DISTANCE);
      for(j = 0; j < unitLabels.length; j++){
        _cmbUnit[0].addItem(unitLabels[j]);
      }
      unitLabels=null;
      unitLabels = Units.getUnitLabels(TYPE_ANGULAR);
      for(j = 0; j < unitLabels.length; j++){
        _cmbUnit[1].addItem(unitLabels[j]);
        _cmbUnit[2].addItem(unitLabels[j]);
      }
      unitLabels = Units.getUnitLabels(TYPE_LINEAR_SPEED);
      for(j = 0; j < unitLabels.length; j++){
        _cmbUnit[3].addItem(unitLabels[j]);
      }
      unitLabels=null;
      unitLabels = Units.getUnitLabels(TYPE_ANGULAR_SPEED);
      for(j = 0; j < unitLabels.length; j++){
        _cmbUnit[4].addItem(unitLabels[j]);
        _cmbUnit[5].addItem(unitLabels[j]);
      }
    }
  }// end setCoordLabels

  //*****************************************************************
  /** Method called when a radio button 'Specify end date' or 'Specify nb of dates' is selected. */
  private void nbOrEndDateChoosen(){
    if (_btnSpecifyEndDate.isSelected()){
      _txtEndDate.setEditable(true);
      _txtEndDate.setEnabled(true);
      _txtEndHour.setEditable(true);
      _txtEndHour.setEnabled(true);
      _txtNbDates.setEditable(false);
      _txtNbDates.setEnabled(false);
    }
    else{
      _txtNbDates.setEditable(true);
      _txtNbDates.setEnabled(true);
      _txtEndDate.setEditable(false);
      _txtEndDate.setEnabled(false);
      _txtEndHour.setEditable(false);
      _txtEndHour.setEnabled(false);
    }
  }// end nbOrEndDateChoosen

  //=================================================================================
  //                    METHODS TO HANDLE LAST VALUES
  //=================================================================================

  //******************* initFields ********************************
  /** Called by the constructor to fill the fields from values preceedingly entered
  <LI>if '_lastValuesLoaded'=true lastXxx variables are loaded from file 'NewEphemerisDialog.last';</LI>
  <LI>Gui components are initialized from lastXxx variables</LI>
  */
  private void initFields(){
    //System.out.println("entering initFields - _lastValuesLoaded : " + _lastValuesLoaded);
    int i;

    // 1 - Retrieve the values from .last file if necessary
    if (!_lastValuesLoaded){
      _lastValues = new TigProperties();
      try{ // Load data from 'NewEphemerisDialog.last'
        _lastValues.load(new FileInputStream(LAST_VALUES_FILE));
        _lastValuesLoaded = true;
      }
      catch(Exception e){ // Load process failed - hard coded default values will be used
        // TO DO : handle this message properly.
        System.out.println("Load process failed - hard coded default values will be used");
      }
    }// end if (!_lastValuesLoaded)

    // 2 - Initialize gui components from _lastValues

    // ***** 2.0 - selct the right tab
    _tabPane.setSelectedIndex(_lastValues.getIntProperty(LAST_SELECTED_TAB_KEY, LAST_SELECTED_TAB_DEFAULT));

    // ***** 2.1 - General tab
    int[] lastPlanets = _lastValues.getIntArrayProperty(LAST_PLANETS_KEY, LAST_PLANETS_DEFAULT);
    for(i = 0; i < _chkPlanets.length; i++){
      if(Integers.contains(lastPlanets, i))
        _chkPlanets[i].setSelected(true);
      else
        _chkPlanets[i].setSelected(false);
    }

    _txtPrecision.setText(_lastValues.getProperty(LAST_PRECISION_KEY, LAST_PRECISION_DEFAULT));

    String strTmp = (_lastValues.getProperty(LAST_ASTROENGINE_KEY, LAST_ASTROENGINE_DEFAULT));
    if(strTmp.equals(AstroEngine.JEPHEM)) _cmbAstroEngine.setSelectedIndex(0);
    else _cmbAstroEngine.setSelectedIndex(1);

    if(_lastValues.getBooleanProperty(LAST_USE_SYMBOLS_KEY, LAST_USE_SYMBOLS_DEFAULT))
      _chkSymbols.setSelected(true);

    if(_lastValues.getIntProperty(LAST_INSTANTS_IN_ROWCOL_KEY, LAST_INSTANTS_IN_ROWCOL_DEFAULT)
           == Ephemeris.INSTANTS_IN_ROWS){
      _btnInstantsInRows.setSelected(true);
      _btnInstantsInCols.setSelected(false);
    }
    else{
      _btnInstantsInRows.setSelected(false);
      _btnInstantsInCols.setSelected(true);
    }

    if(_lastValues.getBooleanProperty(LAST_DISPLAY_ERROR_MESSAGE_KEY, LAST_DISPLAY_ERROR_MESSAGE_DEFAULT))
      _chkDisplayErrorMsg.setSelected(true);

    // ***** 2.2 - Date tab
    // default set to current date and hour (not to predefined default values)
    Calendar cal = Calendar.getInstance();
//    _txtBeginDate.setText(_lastValues.getProperty(LAST_BEGIN_DATE_KEY, LAST_BEGIN_DATE_DEFAULT));
    _txtBeginDate.setText(_lastValues.getProperty(LAST_BEGIN_DATE_KEY,
                                                  Dates.formatDate(new int[]{cal.get(Calendar.YEAR),
                                                                             cal.get(Calendar.MONTH)+1,
                                                                             cal.get(Calendar.DAY_OF_MONTH)},
                                                                   GlobalVar.getLang())));
//    _txtBeginHour.setText(_lastValues.getProperty(LAST_BEGIN_HOUR_KEY, LAST_BEGIN_HOUR_DEFAULT));
    _txtBeginHour.setText(_lastValues.getProperty(LAST_BEGIN_HOUR_KEY,
                                                  Dates.formatHour(new int[]{cal.get(Calendar.HOUR_OF_DAY),
                                                                             cal.get(Calendar.MINUTE),
                                                                             cal.get(Calendar.SECOND)},
                                                                   GlobalVar.getLang())));


    _txtDisplayEvery.setText(_lastValues.getProperty(LAST_TXT_DISPLAY_EVERY_KEY,
                                                     LAST_TXT_DISPLAY_EVERY_DEFAULT));
    _cmbDisplayEvery.setSelectedIndex(_lastValues.getIntProperty(LAST_LST_DISPLAY_EVERY_KEY,
                                                                 LAST_LST_DISPLAY_EVERY_DEFAULT));
    if(_lastValues.getIntProperty(LAST_DISPLAY_DATE_JDS_KEY, LAST_DISPLAY_DATE_JDS_DEFAULT)
           == Ephemeris.DISPLAY_DATES){
      _btnDisplayDates.setSelected(true);
      _btnDisplayJDs.setSelected(false);
    }
    else{
      _btnDisplayDates.setSelected(false);
      _btnDisplayJDs.setSelected(true);
    }

    _cmbTimeFrame.setSelectedIndex(_lastValues.getIntProperty(LAST_TIME_FRAME_KEY,
                                                                 LAST_TIME_FRAME_DEFAULT));

    if (_lastValues.getBooleanProperty(LAST_SPECIFY_NB_DATES_KEY, LAST_SPECIFY_NB_DATES_DEFAULT)){
      _btnSpecifyNbDates.setSelected(true);
      _btnSpecifyEndDate.setSelected(false);
      _txtNbDates.setText(_lastValues.getProperty(LAST_NB_DATES_KEY, LAST_NB_DATES_DEFAULT));
      _txtEndDate.setText(BLANK);
      _txtEndHour.setText(BLANK);
    }
    else{
      _btnSpecifyNbDates.setSelected(false);
      _btnSpecifyEndDate.setSelected(true);
      _txtNbDates.setText(BLANK);
      _txtEndDate.setText(_lastValues.getProperty(LAST_END_DATE_KEY, LAST_END_DATE_DEFAULT));
      _txtEndHour.setText(_lastValues.getProperty(LAST_END_HOUR_KEY, LAST_END_HOUR_DEFAULT));
    }
    nbOrEndDateChoosen();

    // ***** 2.3 - Coordinate tab
    _lstFrames.setSelectedIndex(_lastValues.getIntProperty(LAST_FRAME_KEY, LAST_FRAME_DEFAULT));

    int[] lastSelectedCoords = _lastValues.getIntArrayProperty(LAST_SELECTED_COORDS_KEY,
                                                               LAST_SELECTED_COORDS_DEFAULT);
    for(i = 0; i < lastSelectedCoords.length; i++){
      _chkCoord[lastSelectedCoords[i]].setSelected(true);
    }

    if(_lastValues.getIntProperty(LAST_COORD_EXPR_KEY, LAST_COORD_EXPR_DEFAULT) == SPHERICAL){
      _btnSphere.setSelected(true);
      _btnCart.setSelected(false);
    }
    else{
      _btnSphere.setSelected(false);
      _btnCart.setSelected(true);
    }
    if(_lastValues.getIntProperty(LAST_DEGREE_EXPR_KEY, LAST_DEGREE_EXPR_DEFAULT) == DEGREES_DECIMAL){
      _btnDMS.setSelected(false);
      _btnDecDeg.setSelected(true);
    }
    else{
      _btnDMS.setSelected(true);
      _btnDecDeg.setSelected(false);
    }

    this.setCoordLabels(); // Call to coord Labels depending on the selected frame

    int[] lastUnits = _lastValues.getIntArrayProperty(LAST_UNITS_KEY, LAST_UNITS_DEFAULT);
    for(i = 0; i < lastUnits.length; i++){
      _cmbUnit[i].setSelectedIndex(lastUnits[i]);
    }

  }// end initFields

  //******************* storeLastValues ********************************
  /** - Fills the lastXxx variables from GUI component fields.
      - Store the lastXXX values in the '.last' file.
   */
  private void storeLastValues(){
//System.out.println("entering storeLastValues");
    String strTmp;
    int i, j, len = 0;


    // ***** 2.0 - Selected Pane
    _lastValues.setIntProperty(LAST_SELECTED_TAB_KEY, _tabPane.getSelectedIndex());

    // ***** 2.1 - General tab
    for(i = 0; i < _chkPlanets.length; i++) if(_chkPlanets[i].isSelected()) len ++;
    int[] lastPlanets = new int[len];
    j = 0;
    for(i = 0; i < _chkPlanets.length; i++){
      if(_chkPlanets[i].isSelected()){
        lastPlanets[j] = i;
        j++;
      }
    }
    _lastValues.setIntArrayProperty(LAST_PLANETS_KEY, lastPlanets);

    _lastValues.setProperty(LAST_PRECISION_KEY, _txtPrecision.getText());

    if(_cmbAstroEngine.getSelectedIndex() == 0)
      _lastValues.setProperty(LAST_ASTROENGINE_KEY, AstroEngine.JEPHEM);
    else
      _lastValues.setProperty(LAST_ASTROENGINE_KEY, AstroEngine.SWISS_EPHEMERIS);

    _lastValues.setBooleanProperty(LAST_USE_SYMBOLS_KEY, _chkSymbols.isSelected());

    if (_btnInstantsInRows.isSelected())
      _lastValues.setIntProperty(LAST_INSTANTS_IN_ROWCOL_KEY, Ephemeris.INSTANTS_IN_ROWS);
    else
      _lastValues.setIntProperty(LAST_INSTANTS_IN_ROWCOL_KEY, Ephemeris.INSTANTS_IN_COLUMNS);

    _lastValues.setBooleanProperty(LAST_DISPLAY_ERROR_MESSAGE_KEY, _chkDisplayErrorMsg.isSelected());

    // ***** 2.2 - Date tab
    try{ _lastValues.setProperty(LAST_BEGIN_DATE_KEY, Dates.formatDate(_txtBeginDate.getText(),
                                                                      GlobalVar.getLang())); }
    catch (Exception e){ _lastValues.setProperty(LAST_BEGIN_DATE_KEY, _txtBeginDate.getText()); }
    try{ _lastValues.setProperty(LAST_BEGIN_HOUR_KEY, Dates.formatHour(_txtBeginHour.getText(),
                                                                      GlobalVar.getLang())); }
    catch (Exception e){ _lastValues.setProperty(LAST_BEGIN_HOUR_KEY, _txtBeginHour.getText()); }
    _lastValues.setProperty(LAST_TXT_DISPLAY_EVERY_KEY, _txtDisplayEvery.getText());
    _lastValues.setIntProperty(LAST_LST_DISPLAY_EVERY_KEY, _cmbDisplayEvery.getSelectedIndex());

    if(_btnDisplayDates.isSelected())
      _lastValues.setIntProperty(LAST_DISPLAY_DATE_JDS_KEY, Ephemeris.DISPLAY_DATES);
    else
      _lastValues.setIntProperty(LAST_DISPLAY_DATE_JDS_KEY, Ephemeris.DISPLAY_JDS);

    _lastValues.setIntProperty(LAST_TIME_FRAME_KEY, _cmbTimeFrame.getSelectedIndex());

    if (_btnSpecifyNbDates.isSelected()){
      _lastValues.setBooleanProperty(LAST_SPECIFY_NB_DATES_KEY, true);
      _lastValues.setProperty(LAST_NB_DATES_KEY, _txtNbDates.getText());
      _lastValues.setProperty(LAST_END_DATE_KEY, BLANK);
      _lastValues.setProperty(LAST_END_HOUR_KEY, BLANK);
    }
    else{
      _lastValues.setBooleanProperty(LAST_SPECIFY_NB_DATES_KEY, false);
      _lastValues.setProperty(LAST_NB_DATES_KEY, BLANK);
      try{ _lastValues.setProperty(LAST_END_DATE_KEY, Dates.formatDate(_txtEndDate.getText(),
                                                                      GlobalVar.getLang())); }
      catch (Exception e){ _lastValues.setProperty(LAST_END_DATE_KEY, _txtEndDate.getText()); }
      try{ _lastValues.setProperty(LAST_END_HOUR_KEY, Dates.formatHour(_txtEndHour.getText(),
                                                                      GlobalVar.getLang())); }
      catch (Exception e){ _lastValues.setProperty(LAST_END_HOUR_KEY, _txtEndHour.getText()); }
    }

    // ***** 2.3 - Coordinate tab
    _lastValues.setIntProperty(LAST_FRAME_KEY, _lstFrames.getSelectedIndex());

    // rebuild lastSelectedCoords
    len = 0;
    for(i = 0; i < _chkCoord.length; i++) if(_chkCoord[i].isSelected()) len ++;
    int[] lastSelectedCoords = new int[len];
    j = 0;
    for(i = 0; i < _chkCoord.length; i++){
      if(_chkCoord[i].isSelected()){
        lastSelectedCoords[j] = i;
        j++;
      }
    }
    _lastValues.setIntArrayProperty(LAST_SELECTED_COORDS_KEY, lastSelectedCoords);

    int[] lastUnits = new int[_cmbUnit.length];
    for(i = 0; i < _cmbUnit.length; i++){
      lastUnits[i] = _cmbUnit[i].getSelectedIndex();
    }
    _lastValues.setIntArrayProperty(LAST_UNITS_KEY, lastUnits);

    if(_btnSphere.isSelected())
      _lastValues.setIntProperty(LAST_COORD_EXPR_KEY, SPHERICAL);
    else
      _lastValues.setIntProperty(LAST_COORD_EXPR_KEY, CARTESIAN);

    if(_btnDecDeg.isSelected())
      _lastValues.setIntProperty(LAST_DEGREE_EXPR_KEY, DEGREES_DECIMAL);
    else
      _lastValues.setIntProperty(LAST_DEGREE_EXPR_KEY, DEGREES_DMS);

    // Now store the values
    try{
      _lastValues.store(new FileOutputStream(LAST_VALUES_FILE), LAST_VALUES_HEADER);
    }
    catch (IOException ioe){
      // do nothing (temp code)
      System.out.println("NewEphemerisDialog.storeLastValues() - can't store last values");
    }
  }// end storeLastValues

  //=================================================================================
  //                            INNER CLASSES
  //=================================================================================

  //*****************************************************************
  //	      							EVENT LISTENNERS
  //*****************************************************************

  //*****************************************************************
  /** Contains validity checking and building of an Ephemeris object.*/
  class OKListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      String warningMsg;

      // Variables used to build the Ephemeris.
      int[]     bodyIndexes;
      double[]  jds;
      int       frame;
      int[]     whichCoords;
      int[]     coordUnits;
      int       sphereCart;
      double    precision;
      int       planetsInRowCol;
      boolean   useSymbols = false;
      int       degreeFormat;
      int       dateDisplay; // JDs or Dates
      int       timeFrame; // UTC or TT/TDB

      // 1 -  Build bodyIndexes
      /////// TEMP. PARSING - it supposes that elements of the list correspond to
      // SolarSystemConstants constants.
      // This is the case only if the proposed planets (retrieved from Astro.prefs, currentBodies)
      // contain all the planets
      // Then find a mechanism to associate an elt of the list and a body.

      // ***** 1.1 - Build 'frame'
      // should have been done in 3, but needed here
      frame = _availableFrames[_lstFrames.getSelectedIndex()];
//System.out.println("frame = " + frame);
      // first pass to get the size of body indexes
      int tmp = 0;
      for (int i=0; i<_chkPlanets.length; i++){
        if (_chkPlanets[i].isSelected()) tmp++;
      }
      bodyIndexes = new int[tmp];
      tmp = 0;
      // second pass, to fill bodyIndexes
      for (int i=0; i<_chkPlanets.length; i++){
        if (_chkPlanets[i].isSelected()){
          if(_curBodies[i] == SUN || _curBodies[i] == EARTH){
            if(frame == FRAME_THEORY || frame == FRAME_EC_HELIO_GEOMETRIC)
              bodyIndexes[tmp] = EARTH;
            else
              bodyIndexes[tmp] = SUN;
          }// end if SUN EARTH
          else{
            bodyIndexes[tmp] = _curBodies[i];
          }
          tmp++;
        }
      }
//for (int i = 0; i < bodyIndexes.length; i++) System.out.println("bodyIndexes : " + bodyIndexes[i]);

      // ***** 2 - Build jds
      double jdI = 0, jdF = 0; // jd initial and final.
      int nbJds = 0; // nb of julian days to compute.

      // ***** 2.1 - check begin date and hour
      try{
        int[] beginDateFields = Dates.parseHourDate(_txtBeginDate.getText(), _txtBeginHour.getText(), GlobalVar.getLang());
        jdI = Dates.dateToJd(beginDateFields);
      }
      catch(DateFormatException ex){
        warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                     + _myBundle.getString("BeginDate") + "'";
        UtilsGUI.showWarningMessage(warningMsg);
        _tabPane.setSelectedIndex(DATE_PANEL_INDEX);
        _txtBeginDate.requestFocus();
        return;
      }
      catch(HourFormatException ex){
        warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                     + _myBundle.getString("BeginHour") + "'";
        UtilsGUI.showWarningMessage(warningMsg);
        _tabPane.setSelectedIndex(DATE_PANEL_INDEX);
        _txtBeginHour.requestFocus();
        return;
      }

      // ***** 2.2 - check 'display every'
      double interval=0; // value, in seconds, of 'display every'
      try{
        interval = Integer.parseInt(_txtDisplayEvery.getText());
        if (interval == 0){
          warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                       + _myBundle.getString("DisplayEvery") + "' " + _galBundle.getString("error.FieldCannotBeZero");
          UtilsGUI.showWarningMessage(warningMsg);
          return;
        }
      }
      catch(NumberFormatException nfe){
        warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                     + _myBundle.getString("DisplayEvery") + "'";
        UtilsGUI.showWarningMessage(warningMsg);
        return;
      }
      // convert interval to a common unit (days)
      switch(_cmbDisplayEvery.getSelectedIndex()){
        case 6 : interval /= 86400.0; break; // seconds
        case 5 : interval /= 1440.0; break; // minutes
        case 4 : interval /= 24.0; break; // hours
        //case 3 : break; // days
        case 2 : interval *= 7.0; break; // weeks
        case 1 : interval *= 30.0; break; // months
        case 0 : interval *= 365.0; break; // years
      }

      // ***** 2.2bis - Build 'dateDisplay'
      if(_btnDisplayJDs.isSelected())
        dateDisplay = Ephemeris.DISPLAY_JDS;
      else
        dateDisplay = Ephemeris.DISPLAY_DATES;

      // ***** 2.2ter - Build 'timeFrame'
      if(_cmbTimeFrame.getSelectedIndex() == 0)
        timeFrame = TimeConstants.UTC;
      else
        timeFrame = TimeConstants.TT_TDB;

      // **** 2.3 - compute nbJDs
      if (_btnSpecifyNbDates.isSelected()){
        // ***** 2.3.1 'specify nb dates' is selected
        try{
         nbJds = Integer.parseInt(_txtNbDates.getText());
        }
        catch(NumberFormatException nfe){
          warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                       + _myBundle.getString("NbDates") + "'";
          UtilsGUI.showWarningMessage(warningMsg);
          _tabPane.setSelectedIndex(DATE_PANEL_INDEX);
          _txtNbDates.requestFocus();
          return;
        }
      }
      else{
        // **** 2.3 - 'specify end date' is selected
        try{
          int[] endDateFields = Dates.parseHourDate(_txtEndDate.getText(), _txtEndHour.getText(),
                                                   GlobalVar.getLang());
          jdF = Dates.dateToJd(endDateFields);
          // Check that end date must be posterior to begin date
          if(jdF <= jdI){
            UtilsGUI.showWarningMessage(_myBundle.getString("error.EndDatePosterior"));
            _tabPane.setSelectedIndex(DATE_PANEL_INDEX);
            return;
          }
          // compute nbJds
          nbJds = (int)Math.ceil((jdF - jdI)/interval);
        }
        catch(DateFormatException ex){
          warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                       + _myBundle.getString("EndDate") + "'";
          UtilsGUI.showWarningMessage(warningMsg);
          _tabPane.setSelectedIndex(DATE_PANEL_INDEX);
          _txtEndDate.requestFocus();
          return;
        }
        catch(HourFormatException ex){
          warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                       + _myBundle.getString("EndHour") + "'";
          UtilsGUI.showWarningMessage(warningMsg);
          _tabPane.setSelectedIndex(DATE_PANEL_INDEX);
          _txtEndHour.requestFocus();
          return;
        }
      }// end else of if (_btnSpecifyNbDates.isSelected())

      // ***** 2.4 - compute jds
      if(nbJds == 0) nbJds = 1;
      jds = new double[nbJds];
      for (int i = 0; i < nbJds; i++){
        jds[i] = jdI + interval * i ;
      }

      // ***** 3 - Build 'frame'
      // frame was needed to compute bodyIndexes - see in 1 - build bodyIndeses

      // ***** 4 - Build 'sphereCart'
      if(_btnCart.isSelected()) sphereCart = CARTESIAN;
      else sphereCart = SPHERICAL;

      // ***** 5 and 6 - Build 'whichCoords' and 'coordUnit'
      // 1st pass to to check that at least one coord is wanted.
      int nbCoord = 0;
      for(int i = 0; i < NB_COORD*2; i++){
        if (_chkCoord[i].isSelected())
          nbCoord ++;
      }
      if (nbCoord == 0){
        UtilsGUI.showWarningMessage(_myBundle.getString("error.selectCoord"));
        _tabPane.setSelectedIndex(COORD_PANEL_INDEX);
        return;
      }
      whichCoords = new int[nbCoord];
      coordUnits = new int[NB_COORD*2];
      // 2nd pass to fill whichCoords and coordUnits
      int iCoord = 0;
      int[] tmpUnits;
      for(int i = 0; i < NB_COORD*2; i++){
        if (_chkCoord[i].isSelected()){
          whichCoords[iCoord] = Space.getCoord(i); // supposes that there are 6 checkBoxes
          // find the units corresponding to _cmbUnits.
          tmpUnits = Units.getUnits(Units.getUnitType(sphereCart, whichCoords[iCoord]));
//System.out.println("NewEphemerisDialog.OKListener - i = " + i +
//" - iCoord = " + iCoord + " - whichCoords[iCoord] = " + whichCoords[iCoord] +
//" - tmpUnits = " + tig.Strings.intArrayToString(tmpUnits));
          coordUnits[i] = tmpUnits[_cmbUnit[i].getSelectedIndex()];
          iCoord ++;
        }
        else
          coordUnits[i] = NO_SPECIF;
      }

      // ***** 7 - Build 'precision'
      try{
         precision = Strings.parseDouble(_txtPrecision.getText());
      }
      catch(NumberFormatException nfe){
        warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                     + _myBundle.getString("Precision") + "'";
        UtilsGUI.showWarningMessage(warningMsg);
        _tabPane.setSelectedIndex(GENERAL_PANEL_INDEX);
        _txtPrecision.requestFocus();
        return;
      }

      // ***** 8 - Build 'planetsInRowCol'
      if (_btnInstantsInRows.isSelected())
        planetsInRowCol = Ephemeris.INSTANTS_IN_ROWS;
      else
        planetsInRowCol = Ephemeris.INSTANTS_IN_COLUMNS;

      // ***** 9 - Build 'useSymbols'
      if (_chkSymbols.isSelected())
        useSymbols = true;

      // ***** 10 - Build 'degreeFormat'
      if (_btnDecDeg.isSelected())
        degreeFormat = DEGREES_DECIMAL;
      else
        degreeFormat = DEGREES_DMS;

      // ***** 11 - Build 'astroEngine'
      String astroEngine = null;
      if(_cmbAstroEngine.getSelectedIndex()==0) astroEngine = AstroEngine.JEPHEM;
      if(_cmbAstroEngine.getSelectedIndex()==1) astroEngine = AstroEngine.SWISS_EPHEMERIS;

      // ***** 11 - 'displayErrorMsg'
      boolean displayErrorMsg;
      if (_chkDisplayErrorMsg.isSelected()) displayErrorMsg = true;
      else displayErrorMsg = false;

      // Logs
//      System.out.println("bodyIndexes = " + bodyIndexes);

      // Now be can build the Ephemeris
      try{
        Ephemeris eph = null;
        eph = new Ephemeris(bodyIndexes,
                            jds,
                            timeFrame,
                            whichCoords,
                            coordUnits,
                            frame,
                            sphereCart,
                            precision,
                            astroEngine,
                            displayErrorMsg);
        String strRes = "<HTML>" + LS;
        strRes += "<HEAD>" + LS;
        strRes += "<STYLE> TABLE {text-align:center} </STYLE>" + LS;
        strRes += "</HEAD>" + LS;
//        strRes += "<BODY BGCOLOR = \"#FFCC99\">" + LS;
        strRes += "<BODY>" + LS;
        strRes += eph.getHtmlString(planetsInRowCol,
                                    useSymbols,
                                    degreeFormat,
                                    dateDisplay);
        strRes += "</BODY></HTML>" + LS;
//System.out.println("NewEphemerisDialog.OKListener - returned from Ephemeris");
        // Add a 'save' menuItem
        String strSave = GlobalVar.getBundle(GlobalVar.BUNDLE_MENUS).getString("menu.Save");
        JMenuItem saveItem = new JMenuItem(strSave, KeyEvent.VK_S);
        saveItem.addActionListener(new SaveListener((JComponent)GlobalVar.getHTMLPane(), strRes));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
// WARNING : INDICES OF MENUS SHOULD BE PUT IN STATIC VARIABLES
        // Test if File menu already contains a save item
        // THIS CODE MUST BE PUT SOMEWHERE ELSE. (maybe use a subclass of JMenuBar containing a method boolean contains(menuIndex, String)
        JMenu mnu = GlobalVar.getMainFrame().getJMenuBar().getMenu(0);
        boolean addSave = true;
        for (int ttt = 0; ttt < mnu.getItemCount(); ttt++){
          if(mnu.getItem(ttt).getLabel() != null && mnu.getItem(ttt).getLabel().equals(strSave)) addSave = false;
        }
        if(addSave) GlobalVar.getMainFrame().getJMenuBar().getMenu(0).add(saveItem, 0);

        // Add a 'Modify' menuItem

        // Save current state of the dialog for restoration
        NewEphemerisDialog.this.storeLastValues(); // TO DO thread this operation

        NewEphemerisDialog.this.dispose();
        GlobalVar.getHTMLPane().setText(strRes);
      }
      catch(Exception je){
        Debug.traceError(je);
      }

    } // end actionPerformed
  } // end class OKListener

  //*****************************************************************
  class CancelListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      dispose();
    } // end actionPerformed
  } // end class CancelListener

  //*****************************************************************
  /** For radio buttons spherical / cartesian */
  class SphereCartActionListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      setCoordLabels();
      // Set default values for units
      if (_btnSphere.isSelected()){
        // select degrees for angles
        _cmbUnit[1].setSelectedIndex(1);
        _cmbUnit[2].setSelectedIndex(1);
        // select deg/day for angular speeds
        _cmbUnit[4].setSelectedIndex(3);
        _cmbUnit[5].setSelectedIndex(3);
      }
      //else
        // nothing to do, as a.u. and a.u/d are at index 0 in cmbUnits
        // and this is convenient
    }// end ActionPerformed
  }// end class SphereCartActionListener

  //*****************************************************************
  /** For Specify nb of dates / choose end date */
  class NumberOrEndDateActionListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      nbOrEndDateChoosen();
    }// end ActionPerformed
  }// end class NumberOrEndDateActionListener

  //*****************************************************************
  class FrameListSelectionListener implements ListSelectionListener{
    public void valueChanged(ListSelectionEvent e){
      // Remember selected units
      int[] tmpUnits = new int[6];
      for (int i = 0; i < 6; i++)
        tmpUnits[i] = _cmbUnit[i].getSelectedIndex();

      setCoordLabels();

      // Restore values
      for (int i = 0; i < 6; i++)
        _cmbUnit[i].setSelectedIndex(tmpUnits[i]);
    }// end ActionPerformed
  }// end class FrameListSelectionListener

}// end class NewEphemerisDialog
