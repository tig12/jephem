//*********************************************************************************
// class jephem.tools.Ephemeris
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.tools;

import jephem.GlobalVar; // for symbol path, data path and internationalized strings
import jephem.util.Debug;
import jephem.astro.AstroContext;
import jephem.astro.AstroEngine;
import jephem.astro.Body;
import jephem.astro.AstroException;
import jephem.astro.solarsystem.SwissEphemeris;
import jephem.astro.solarsystem.SolarSystemConstants;
import jephem.astro.solarsystem.SolarSystem;
import jephem.astro.solarsystem.ComputationException;
import jephem.astro.solarsystem.vsop87.VSOP87;
import jephem.astro.solarsystem.ELP82;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.spacetime.Space;
import jephem.astro.spacetime.Time;
import jephem.astro.spacetime.TimeConstants;
import jephem.astro.spacetime.UnitsConstants;

import tig.GeneralConstants;
import tig.Dates;
import tig.Strings;
import tig.Formats;
import tig.TigBundle;
import tig.Integers;
import tig.html.Html;

import java.text.NumberFormat;
import java.util.*;

/******************************************************************************
<A NAME="Top"></A>
An <B>Ephemeris</B> is the representation of a coordinate at different instants.
<BR>It can be for one or several celestial bodies.

<BR><BR>An Ephemeris is defined by :
<LI>the <B>instants</B> this Ephemeris represents, expressed in JD - accessible through {@link #getJDs()}</LI>
<LI>the <B>bodies</B> for which coordinates are represented - accessible through {@link #getBodyIndexes()}</LI><LI>the
<B>precision</B> required for the calculations.</LI>

<BR><BR>Internally, the coordinates of the bodies at different instants are stored in a <CODE>double[][][]</CODE>,
accessible via {@link #getData()}.
<BR>This array is filled by the call of <CODE>Ephemeris</CODE> constructor ; <CODE>Ephemeris</CODE> uses
{@link jephem.astro.AstroContext} to fill it.
</LI>

<BR><BR>As in the rest of the non-GUI part of the API, internal representation uses "standard units".

@author Thierry Graff
@history Aug 03 2001 : creation.
@history Sep 20 2001 : changed whichCoords to an int[] (several coordinates can be handled)

@todo getErrorMessages() is generic (could be placed elsewhere)
@todo getErrorMessages could have an other parameter, to display by planet or by date.
@todo more convenient to have _data[iBody][iCoord][iJD] // convenient for Curve
@todo debug getCoords()
*********************************************************************************/
public class Ephemeris implements GeneralConstants, SolarSystemConstants{

  //=================================================================================
  //                            INSTANCE VARIABLES
  //=================================================================================

  // ***** Data characterizing the Ephemeris - provided by the client classes. *****
  private int[]     _bodyIndexes;
  private double[]  _JDs;
  private int       _timeFrame;
  private int       _frame;
  private int[]     _coordUnits;
  private int[]     _whichCoords;
  private int       _sphereCart;
  private double    _precision;
  private String    _astroEngine;
  private boolean   _displayErrorMsg;


  // ***** Internal representation *****
  /** To store coordinates of _bodies at _instants for _whichCoords. Use : _data[iJD][iBody][iCoord] */
  private double[][][] _data;

  /** variables for convenience. */
  private int _nbBodies, _nbJDs, _nbCoords;

  /** To store computation error, initialized by fillData() */
  private Vector    _computationExceptions = new Vector();

  //=================================================================================
  //                            PUBLIC CONSTANTS
  //=================================================================================

  /** Constant meaning that the generated Ephemeris must have instants in columns
  and positions in rows. */
  public static final int INSTANTS_IN_COLUMNS = 0;
  /** Constant meaning that the generated Ephemeris must have positions in columns and
  instants in rows. */
  public static final int INSTANTS_IN_ROWS = 1;

  /** Constant meaning that dates should be displayed as formatted dates. */
  public static final int DISPLAY_DATES = 0;
  /** Constant meaning that dates should be displayed as julian days. */
  public static final int DISPLAY_JDS = 1;

  // To format the double values in the table;
  private static final NumberFormat NF = NumberFormat.getNumberInstance();
  static{
    NF.setMaximumFractionDigits(6);
    NF.setMinimumFractionDigits(6);
  };


  //=================================================================================
  //                            PRIVATE CONSTANTS
  //=================================================================================
  /** Path to the images of the planet symbols. */
  private static final String SYMBOL_PATH = GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS
                                          + "planetSymbols" + FS + "18" + FS;

  private static final String[] PLANET_SYMBOL_TAGS = { "<IMG SRC=\"file://" + SYMBOL_PATH + "01-sun_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "02a-moon_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "03-mercury_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "04-venus_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "05-earth_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "06-mars_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "07-jupiter_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "08-saturn_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "09-uranus_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "10-neptune_18.gif" + "\">",
                                                       "<IMG SRC=\"file://" + SYMBOL_PATH + "11-pluto_18.gif" + "\">"
                                                     };
  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================

  //***************** Constructor *******************************
  /** Unique constructor, which orders the computations to be done.
  <BR>Warning : 'whichCoords' and 'coordUnits' parameters must be of the same length.

  @param bodyIndexes The indexes of the bodies for which ephemeris is represented, expressed with constants of
         {@link jephem.astro.solarsystem.SolarSystemConstants}.
  @param JDs The instants this Ephemeris represents, expressed in <B>Julian Days</B>.
  @param timeFrame The time frame used to express 'jds'.
         use {@link jephem.astro.spacetime.TimeConstants} constants.
  @param frame The reference frame in which the required coordinate must be expressed.
         use {@link jephem.astro.spacetime.SpaceConstants} constants.
  @param whichCoord Type of coordinate (x1, x2, ... v3) ;
         use {@link jephem.astro.spacetime.SpaceConstants}.<CODE>COORD_XXX</CODE> constants.
         <BR>You can specify from <B>one to six</B> coordinates to be displayed (positions and velocities).
  @param coordUnits Specifies which units must be used to express the coordinate;
         use {@link jephem.astro.spacetime.UnitsConstants}.<CODE>UNIT_XXX</CODE> constants.
  @param sphereCart Specifies if coordinate must be expressed in cartesian or spherical ;
         use {@link jephem.astro.spacetime.SpaceConstants} constants.
  @param precision Precision required for the calculation, expressed in <B>arcseconds</B>.
  @param astroEngine : the astro engine used for the computations.
         use {@link jephem.astro.AstroContext} constants to specify it.
  @param displayErrorMsg Indicates if the error messages should be memorized for further display.
  */
  public Ephemeris(int[]      bodyIndexes,
                   double[]   JDs,
                   int        timeFrame,
                   int[]      whichCoords,
                   int[]      coordUnits,
                   int        frame,
                   int        sphereCart,
                   double     precision,
                   String     astroEngine,
                   boolean    displayErrorMsg
                   ){
    // Parameters checking
    if (bodyIndexes.length < 1)
      throw new IllegalArgumentException("Invalid 'bodyIndexes' parameter.");
    if (JDs.length < 1 )
      throw new IllegalArgumentException("Invalid 'JDs' parameter.");
    if (whichCoords.length < 1 || whichCoords.length > 6)
      throw new IllegalArgumentException("Invalid 'whichCoords' parameter - "
                          + "One to six coordinates can be represented by an Ephemeris");
//    if (whichCoords.length != coordUnits.length)
//      throw new IllegalArgumentException("'whichCoords' and 'coordUnits must be of the same length");


    // fill instance variables
    _bodyIndexes = Integers.copyFrom(bodyIndexes);
    _JDs = JDs;
    _timeFrame = timeFrame;
    _frame = frame;
    _whichCoords = whichCoords;
    _coordUnits = coordUnits;
    _sphereCart = sphereCart;
    _precision = precision;
    _astroEngine = astroEngine;
    _displayErrorMsg = displayErrorMsg;

    _nbBodies = bodyIndexes.length;
    _nbJDs = _JDs.length;
    _nbCoords = _whichCoords.length;
    _data = new double[_nbJDs][_nbBodies][_nbCoords];

//    System.out.println(this.toString());
    this.fillData();
  }// end Ephemeris constructor

  //=================================================================================
  //                            PUBLIC METHODS
  //=================================================================================

  //***************** getData() *********************************************
  /** Returns the data of this Ephemeris.
  <BR>The use is :
  <BR><CODE>double[][][] x = myEphemeris.getData();
  <BR>double myValue = x[iJD][iBody][iCoord];</CODE>.
  <BR>But Warning : 'iBody' corresponds here to the ith body of this ephemeris, and is not related with SolarSystem constants.
  */
  public double[][][] getData() { return _data; }

//  //***************** getCoords(body, coord) *********************************************
//  /** Returns the coordinates of the sppecified body and coord for all the julian days of this Ephemeris.
//  @param bodyIndex body index, using {@link jephem.astro.SolarSystemConstants} constants.
//  @param coordIndex coordinate index, using {@link jephem.astro.spacetime.SpaceConstants}<CODE>.COORD_XXX</CODE> constants.
//  */
//  public double[] getCoords(int bodyIndex, int coordIndex){
//    // find bodyIndex in _bodyIndexes
//    int iBody;
//    for(iBody = 0; iBody < _bodyIndexes.length; iBody++){
//      if(_bodyIndexes[iBody] == bodyIndex) break;
//    }
//    int iCoord;
//    for(iCoord = 0; iCoord < _bodyIndexes.length; iCoord++){
//      if(_whichCoords[iCoord] == bodyIndex) break;
//    }
//    // build the res
//    double[] res = new double[_data.length];
//    for(int i = 0; i < _data.length; i++){
//      res[i] = _data[i][iCoord][iBody];
//    }
//
//    return res;
//  }// end getCoords

  //***************** getJDs() *********************************************
  /** Returns the instants of this Ephemeris, expressed in <B>julian days</B>. */
  public double[] getJDs() { return _JDs; }

  //***************** getBodyIndexes() *********************************************
  /** Returns the indexes of the bodies handled by this Ephemeris. */
  public int[] getBodyIndexes() { return _bodyIndexes; }

  //***************** getHtmlString *********************************************
  /** Returns a HTML String representation of the Ephemeris.
  <BR>Build only a <CODE>TABLE</CODE>. Calling methods must incorporate the ephemeris in a HTML page

  @param orientation Specifies if the instants must be displayed in column or in rows ;
  use constants of this class.
  @param useSymbols Indicates if planet names should be replaced by pictures containing
         symbols of the planets.
  @param degreeFormat Indicates how degrees should be formatted ;
         use {@link jephem.astro.spacetime.UnitsConstants}<CODE>.DEGREE_XXX</CODE> constants.
  @param dateDisplay indicates if dates should be displayed as formatted dates or julian days ;
         use <CODE>DISPLAY_XXX</CODE> constants of this class.
  */
  public String getHtmlString(int orientation, boolean useSymbols, int degreeFormat, int dateDisplay){

    StringBuffer strRes = new StringBuffer();

    TigBundle astroBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_ASTRO);

    int i; // used several times
    int nbRow, nbCol; // nb of row, col - the real number of rows is (nbRows * _nbCoord) + 1
    int iRow, iCol, iCoord; // indexes to build the table
    String[] rowHeaders, colHeaders; // contain titles of row, col.
    double data; // represents the data to display in current row

    // Build coord labels
    /////////////// CODE TO MOVE ESLEWHERE /////////////////
    String[] coordLabels = new String[_nbCoords]; // Labels of coords to be displayed
    String[] allLabels = new String[3];
    if (_sphereCart == SpaceConstants.CARTESIAN)
      allLabels = Space.getCoordGroupLabels(SpaceConstants.COORDGROUP_XYZ);
    else
      allLabels = Space.getCoordGroupLabels(Space.getCoordGroup(_frame));
    for (i = 0; i < _nbCoords; i++){
      if (_whichCoords[i] < 3)
        coordLabels[i] = allLabels[_whichCoords[i]];
      else
        coordLabels[i] = allLabels[_whichCoords[i] - 3] + "'";
    }
    allLabels = null;

    // Build planetNames
    /////////////// Code common with NewEphemerisDialog - to factorize /////////////////
    String[] allPlanetNames = Strings.stringToStringArray(astroBundle.getString("planetNames"));
    String[] planetNames = new String[_bodyIndexes.length];
    for (i = 0; i < planetNames.length; i++){
      planetNames[i] = allPlanetNames[_bodyIndexes[i]];
    }
    allPlanetNames = null;

    // Compute headers of rows and cols
    if(orientation == INSTANTS_IN_ROWS){
      nbRow = _nbJDs;
      nbCol = _nbBodies;
      rowHeaders = new String[nbRow];
      colHeaders = new String[nbCol];
      // Dates
      for (iRow = 0; iRow < nbRow; iRow++){
        if (dateDisplay == DISPLAY_DATES)
          rowHeaders[iRow] = Dates.formatHourDate(Dates.jdToDate(_JDs[iRow]), GlobalVar.getLang());
        else
          rowHeaders[iRow] = Double.toString(_JDs[iRow]);
      }
      // Body names
      for (iCol = 0; iCol < nbCol; iCol++){
        if(useSymbols)
          colHeaders[iCol] = PLANET_SYMBOL_TAGS[_bodyIndexes[iCol]];
        else
          colHeaders[iCol] = SolarSystem.getBodyName(_bodyIndexes[iCol]);
      }
    }
    else if(orientation == INSTANTS_IN_COLUMNS){
      nbRow = _nbBodies;
      nbCol = _nbJDs;
      rowHeaders = new String[nbRow];
      colHeaders = new String[nbCol];
      for (iRow = 0; iRow < nbRow; iRow++){
        rowHeaders[iRow] = SolarSystem.getBodyName(_bodyIndexes[iRow]);
      }
      for (iCol = 0; iCol < nbCol; iCol++){
        colHeaders[iCol] = Dates.formatHourDate(Dates.jdToDate(_JDs[iCol]), GlobalVar.getLang());
      }
    }
    else throw new IllegalArgumentException("bad 'orientation' parameter");

    strRes.append("<CENTER>" + LS);
    // Write title
    strRes.append("<H1>" + astroBundle.getString("Ephemeris") + "</H1>" + LS);

    // Write the ephemeris table
    strRes.append("<TABLE BORDER=\"1\">" + LS);
    // write headers for columns
    if (_nbCoords == 1) // in this case, col with coord label is not written
      strRes.append("<TR><TD></TD>" + LS);
    else
      strRes.append("<TR><TD COLSPAN=\"2\"></TD>" + LS);
    for (iCol = 0; iCol < nbCol; iCol++){
      strRes.append("<TH>" + colHeaders[iCol] + "</TH>" + LS);
    }
    strRes.append("</TR>" + LS);

    // write data lines
    for (iRow = 0; iRow < nbRow; iRow++){
     // write first col header
      strRes.append("<TR><TD ROWSPAN = \"" + _nbCoords + "\">" + rowHeaders[iRow] + "</TD>" + LS);
      for (iCoord = 0; iCoord < _nbCoords; iCoord++){
        // write second col header, containing coord label
        if(iCoord != 0) strRes.append("<TR>" + LS);
        if (_nbCoords != 1) // write second header only if more than one coord are displayed
          strRes.append("<TD>" + coordLabels[iCoord] + "</TD>" + LS);
        for (iCol = 0; iCol < nbCol; iCol++){
          if (orientation == INSTANTS_IN_ROWS)
            data = _data[iRow][iCol][iCoord];
          else
            data = _data[iCol][iRow][iCoord];
          // Now format the coordinate
          if ((_coordUnits[iCoord] == UnitsConstants.ANGULAR_UNIT_DEG ||
               _coordUnits[iCoord] == UnitsConstants.ANGULAR_SPEED_UNIT_DEG_PER_S ||
               _coordUnits[iCoord] == UnitsConstants.ANGULAR_SPEED_UNIT_DEG_PER_DAY)
               && degreeFormat == UnitsConstants.DEGREES_DMS){
            strRes.append("<TD>" + Formats.doubleToDMS(data) + "</TD>" + LS);
          }
          else{
            strRes.append("<TD>" + NF.format(data) + "</TD>" + LS);
          }
        }// end for iCol
      }// end for iCoord
      strRes.append("</TR>" + LS);
    }// end for iRow

    // end writing the output.
    strRes.append("</TABLE></CENTER>" + LS);

    // Write the frame
    String[] frameNames = Strings.stringToStringArray(astroBundle.getString("frameLabels"));
    strRes.append("<BR>" + LS);
    strRes.append("<B>" + astroBundle.getString("Frame") + "</B> : " + frameNames[_frame] + LS);

    // Write the error messages
    if (_displayErrorMsg){
      strRes.append("<BR>" + LS);
      strRes.append("<BR>" + LS);
      strRes.append(getErrorMessages(_computationExceptions, dateDisplay, _timeFrame));
    }

    return strRes.toString();
  }// end getHtmlString

  //=================================================================================
  //                            PRIVATE METHODS
  //=================================================================================

  //***************** fillData() *********************************************
  /** Fills <CODE>_data</CODE>, the double[][][] containing the data, calling AstroContext.
  @pre _instants and _body are not empty.
  */
  private void fillData(){
    int iJD, iBody, iCoord; // indexes
    boolean velocities = Space.containsVelocityCoord(_whichCoords);

    // Set the path to data used for the computations.
    AstroEngine.setAstroEngine(_astroEngine);
    if(_astroEngine.equals(AstroEngine.JEPHEM)){
      VSOP87.setDataPath(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "astro" + FS + "planets" + FS
                  + "vsop87" + FS + "VSOP87A" + FS);
      ELP82.setDataPath(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "astro" + FS + "planets" + FS
                  + "elp82" + FS);
    }
    else{
      SwissEphemeris.setDataPath(GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "astro" + FS + "swissEphem" + FS);
    }

    try{
      double[] jds = new double[_nbJDs];
      AstroContext[] ac = new AstroContext[_nbJDs];
//_computationExceptions.clear();
      for (iJD = 0; iJD < _nbJDs; iJD++){
        ac[iJD] = new AstroContext(_JDs[iJD], _timeFrame, _bodyIndexes);
        ac[iJD].setAstroEngine(_astroEngine); // ADDITION 2002.10.22 to remove
        ac[iJD].calcBodyCoords(_frame, _sphereCart, _precision, velocities, _coordUnits);
        for (iBody = 0; iBody < _nbBodies; iBody++){
          for (iCoord = 0; iCoord < _nbCoords; iCoord++){
            // fill _data and error messages
            _data[iJD][iBody][iCoord] = ac[iJD].getBody(_bodyIndexes[iBody]).getCoord(_whichCoords[iCoord]);
          }// end for iCoord
          if(_displayErrorMsg){
            if (ac[iJD].getBody(_bodyIndexes[iBody]).getComputationException() != null){
              _computationExceptions.add(ac[iJD].getBody(_bodyIndexes[iBody]).getComputationException());
            }
          }// end if(_displayErrorMsg)
        }// end for iBody
      }// end for iJD

      return;
      }
      catch(AstroException ae){ // for ac.calcBodyCoords() and getErrorMessages
        Debug.traceError(ae);
      }
  }// end fillData()

  //***************** getErrorMessages *********************************************
  /** Returns the {@link jephem.astro.planets.ComputationExceptions} contained in a Vector
  {@link jephem.astro.planets.ComputationException}s.
  <BR>The language used to build the resulting string come from {@link jephem.GlobaVar}
  @param computationExceptions the <CODE>ComputationException</CODE>s to display.
  @param dateDisplay Permits to specify if the dates in the errors messages should be displayed as
         formatted dates or julian days. Use {@link jephem.tools.Ephemeris} constants to express it.
  @param timeFrame : The time frame in which dates must be displayed (UTC or TT/TDB) must be expressed.
                     Use {@link jephem.astro.spacetime.TimeConstants} constants for it.
  @return an HTML formatted string containing a description of the exceptions.
  */
  private static String getErrorMessages(Vector computationExceptions, int dateDisplay, int timeFrame){
    StringBuffer strRes = new StringBuffer(BLANK);
    if(computationExceptions.size()==0) return strRes.toString();

    Comparator comp = new Comparator() {
      public int compare(Object o1, Object o2) {
        ComputationException c1 = (ComputationException) o1;
        ComputationException c2 = (ComputationException) o2;
        // first compare the body indexes
        if(c1.getBodyIndex() < c2.getBodyIndex()) return -1;
        else if (c1.getBodyIndex() > c2.getBodyIndex()) return 1;
        else{ // same bodies, so compare the error types
          if(c1.getErrorType() < c2.getErrorType()) return -1;
          else if(c1.getErrorType() > c2.getErrorType()) return 1;
          else{ // same error types, so compare the dates
            if(c1.getJulianDay(TimeConstants.TT_TDB) < c2.getJulianDay(TimeConstants.TT_TDB)) return -1;
            else return 1;
          }
        }
      }
    }; // end comparator

    // Order the exceptions by body, error type, and jd
    Collections.sort(computationExceptions, comp);

    // Prepare the strings to display
    String BRLS = "<BR>" + LS;
    String SPACES = "&nbsp;&nbsp;&nbsp;&nbsp;";
    TigBundle astroBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_ASTRO);
    String[] planetNames = Strings.stringToStringArray(astroBundle.getString("planetNames"));
    String[] strError = new String[]{SPACES + astroBundle.getString("PrecisionNotHandledForDates") + BRLS,
                                     SPACES + astroBundle.getString("DateLimitForTheory") + BRLS};
    // Prepare variables
    int lastBody = ((ComputationException)computationExceptions.get(0)).getBodyIndex();
    int lastErrorType = ((ComputationException)computationExceptions.get(0)).getErrorType();
    int curBody, curErrorType;
    ComputationException curCE;

    // write display beginning (we know the vector is not empty).
    strRes.append(Html.tag(astroBundle.getString("NotGuaranteedResults"), "B")).append(BRLS);
    strRes.append(planetNames[((ComputationException)computationExceptions.get(0)).getBodyIndex()]).append(BRLS);
    strRes.append(strError[((ComputationException)computationExceptions.get(0)).getErrorType()]);

    for(Iterator i = computationExceptions.iterator(); i.hasNext(); ){
      curCE = (ComputationException)i.next();
      curBody = curCE.getBodyIndex();
      curErrorType = curCE.getErrorType();
      if(curBody != lastBody)
        strRes.append(planetNames[curBody]).append(BRLS);
      if(curErrorType != lastErrorType || curBody != lastBody)
        strRes.append(strError[curErrorType]).append(BRLS);

      // Write the date of the error
      if(dateDisplay == Ephemeris.DISPLAY_DATES)
        strRes.append(SPACES).append(SPACES).append(Dates.formatHourDate(Dates.jdToDate(curCE.getJulianDay(timeFrame)), GlobalVar.getLang()));
      else
        strRes.append(SPACES).append(SPACES).append(curCE.getJulianDay(timeFrame));
      strRes.append(BRLS);

      // Swap for next
      lastBody = curBody;
      lastErrorType = curErrorType;
    }// end for iterator

    return strRes.toString();
  }// end getErrorMessages

  //***************** toString *********************************************
  /** Returns a basic String representation of this Ephemeris (useful for debug). */
  public String toString(){
    StringBuffer strRes = new StringBuffer();
    strRes.append("========= Ephemeris.toString() ==========").append(LS);
    String strTmp;
    int i;

    strRes.append("frame : ").append(_frame).append(LS);
    strRes.append("timeFrame : ").append(_timeFrame).append(LS);
    strRes.append("sphereCart : ").append(_sphereCart).append(LS);
    strRes.append("precision : ").append(_precision).append(LS);

    strTmp = BLANK;
    for (i = 0; i < _bodyIndexes.length; i++)
      strTmp += _bodyIndexes[i] + " ";
    strRes.append("bodyIndexes : ").append(strTmp).append(LS);

    strTmp = BLANK;
    for (i = 0; i < _whichCoords.length; i++)
      strTmp += _whichCoords[i] + " ";
    strRes.append("whichCoords : ").append(strTmp).append(LS);

    strTmp = BLANK;
    for (i = 0; i < _coordUnits.length; i++)
      strTmp += _coordUnits[i] + " ";
    strRes.append("coordUnits : ").append(strTmp).append(LS);

    strRes.append("========= end Ephemeris.toString() ==========");
    return strRes.toString();
  }// end traceInstanceVariables

  //=================================================================================
  //=================================================================================
  //                            TESTS
  //=================================================================================
  //=================================================================================

}//end class Ephemeris
