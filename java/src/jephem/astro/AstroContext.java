//*********************************************************************************
// class jephem.astro.SolarSystem
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.astro;

import jephem.astro.AstroEngine;
import jephem.astro.AstroException;
import jephem.astro.Nutation;
import jephem.astro.solarsystem.SolarSystemConstants;
import jephem.astro.solarsystem.ComputationException;
import jephem.astro.solarsystem.vsop87.VSOP87;
import jephem.astro.solarsystem.Pluto99;
import jephem.astro.solarsystem.ELP82;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.spacetime.Time;
import jephem.astro.spacetime.TimeConstants;
import jephem.astro.spacetime.Units;
import jephem.astro.spacetime.UnitsConstants;

import tig.maths.Maths;
import tig.maths.Matrix3;
import tig.maths.Vector3;
import tig.GeneralConstants;

/******************************************************************************
Conductor of ephemeris calculation.
<BR>Each time a calculation needs to be done, a <CODE>AstroContext</CODE> object must be
created ; this AstroContext is then in charge of the coherence of calculations.

<BR><BR>An AstroContext is characterized by :
<LI>its <B>julian day</B></LI>
<LI>its <B>bodies</B>, an array of instances of class {@link Body}.</LI>

<BR><BR>The main method is <CODE>calcBodyCoords()</CODE> whose role is :
<LI>For each body, call the appropriate low-level computation classes depending on the required precision.</LI>
<LI>Perform the necessary coordinate transformations to express the coordinates in the right frame.</LI>
<LI>Perform the conversions to get the right units for positions and velocities.</LI>

@author Thierry Graff
@history dec 13 2000 : creation from a former version.
@history may 05 2001 : Changed to AstroContext (previously SolarSystem).
@history Aug 15 2001 : Changed to new API terminology (frame, sphereCart...),
                       Adapted calcCoord to new API.
@history jan 22 2002 : Implementation of precession and nutation
                       FRAME_EC_HELIO_GEOMETRIC returns FK5 coords

@todo Should coherence of internal data be insured (when get / set are done) ?
@todo Each time a transformation is done, call body.setCoordinateExpression()
@todo remove _moonIndex (ugly coding)
@todo parameter checking in calcBodyCoords ?
@todo parameter checking in constructor?
*********************************************************************************/
public class AstroContext implements SolarSystemConstants, SpaceConstants, TimeConstants, UnitsConstants{

  //=================================================================================
  //                                 CLASS VARIABLES
  //=================================================================================
  /** Implementation used to perform the astro computations. */
  private static String _astroEngine;

  //=================================================================================
  //                                 INSTANCE VARIABLES
  //=================================================================================

  /** Number of julian days elapsed since jan 0.5 4712 BC. */
  private double _jd;

  /**   Bodies this AstroContext has to handle. */
  private Body[] _bodies;

  /** for internal use, if different from GeneralConstants.NO_SPECIF,
  means that the moon must be handled. */
  private int _moonIndex;

  //=================================================================================
  //                                 CONSTRUCTORS
  //=================================================================================

  //***************** AstroContext(jd, bodyIndexes) *******************************
  /** The only constructor.
  <BR>Example of use, in a class implementing {@link SolarSystemConstants} (for constant names) :
  <BR>&nbsp;&nbsp;<CODE>int[] bodyIndexes = {MERCURY, VENUS};</CODE>
  <BR>&nbsp;&nbsp;<CODE>AstroContext ac = new AstroContext(jd, bodyIndexes);</CODE>

  @param jd Number of Julian Days elapsed since jan 0.5 4712 BC.
  @param timeFrame The time frame used to express 'jds'.
         Use {@link jephem.astro.spacetime.TimeConstants} constants.
  @param bodyIndexes Array containing indexes of bodies this AstroContext must handle ;
  use constants from {@link SolarSystemConstants}.
  */
  public AstroContext(double jd, int timeFrame, int[] bodyIndexes){
    // convert jd to TDB.
    if(timeFrame == TimeConstants.UTC)
      _jd = Time.getTT(jd);
    else
    _jd = jd;

    _bodies = new Body[bodyIndexes.length]; // allocate space

    _moonIndex = GeneralConstants.NO_SPECIF;
    for (int i=0; i < bodyIndexes.length; i++){
      _bodies[i] = new Body(bodyIndexes[i]); // initialize each body with its index
      if (bodyIndexes[i] == MOON) _moonIndex = i;
    }
  }// end AstroContext(jd, bodiesToCalc)

  //=================================================================================
  //                                 PUBLIC METHODS
  //=================================================================================

  //************************* get / set methods  *******************************
  /** Returns the julian day of this <CODE>AstroContext</CODE>. */
  public double getJd(){ return _jd; }

  /** Returns the bodies handled by this <CODE>AstroContext</CODE>. */
  public Body[] getBodies(){ return _bodies; }

  /** Returns the bodies handled by this <CODE>AstroContext</CODE>.
  //// ADDITION 2002.10.22 - To remove as AstroEngine will be statically retrieved
  */
  public void setAstroEngine(String astroEngine){ _astroEngine = astroEngine; }

  //***************** getBody(whichBody) *********************************************
  /** Returns the Body handled by this <CODE>AstroContext</CODE> whose index is 'whichBody'.
  @param whichBody the index charcterizing the body to retrieve, using
  {@link jephem.astro.solarsystem.SolarSystemConstants} constants.
  @return the corresponding body if it is handled by this AstroContext, <B>null</B> otherwise.
  */
  public Body getBody(int whichBody){
    for (int i=0; i < _bodies.length; i++){
      if (_bodies[i].getIndex() == whichBody)
        return _bodies[i];
    }
    return null;
  } // end getBody(whichBody)

  //******** calcBodyCoords(frame) ********
  /** Call to {@link #calcBodyCoords(int, int, double, boolean, int[])} with default values :
  <LI><CODE>sphereCart</CODE> is set to spherical,</LI>
  <LI><CODE>units</CODE> are set to ua, degrees, degrees for the positions and
  to ua/d, deg/d, deg/d for the velocities.</LI>
  */
  public void calcBodyCoords(int frame, boolean velocities) throws AstroException{
    int[] units;
    if(velocities)
      units = new int[]{DISTANCE_UNIT_AU, ANGULAR_UNIT_DEG, ANGULAR_UNIT_DEG};
    else
      units = new int[]{DISTANCE_UNIT_AU, ANGULAR_UNIT_DEG, ANGULAR_UNIT_DEG,
                   LINEAR_SPEED_UNIT_AU_PER_D, ANGULAR_SPEED_UNIT_DEG_PER_DAY, ANGULAR_SPEED_UNIT_DEG_PER_DAY};
    calcBodyCoords(frame, SPHERICAL, 0, velocities, units);
  }// end calcBodyCoords

  //******** calcBodyCoords(frame, spherCart, precision, velocities, units) ********
  /** Main calculation method for body coordinate calculation.
  <BR>The calculation will be done for all the bodies of this AstroContext.

  @param frame Coordinate system in which the coordinates are expressed ;
  use {@link jephem.astro.spacetime.SpaceConstants} constants.
  @param sphereCart Expression mode of the coordinates (spherical, cartesian) ;
         Use {@link jephem.astro.spacetime.SpaceConstants} constants.
  @param precision Precision required for the calculations, expressed in <B>arcseconds</B>.
  @param velocities Indicates if the velocities need to be also calculated.
  @param units the desired units to express the coordinates.
         Use {@link jephem.astro.spacetime.UnitsConstants} constants.
         <BR><B>WARNING</B> : this array must contain 3 or 6 elements (depending on parameter
         'velocities'), and contain units of the right type (linear, angular etc...).
  @throws AstroException if an unrecoverable error occured during computation.
  */
  public void calcBodyCoords(int     frame,
                             int     sphereCart,
                             double  precision,
                             boolean velocities,
                             int[]   units
                            ) throws AstroException{

    // Parameter checking
    // removed to save up time.
//    if((velocities && units.length != 6) || (!velocities && units.length != 3))
//      throw new IllegalArgumentException("Incorrect length for parameter 'units'");

    //  Swiss Ephemeris computation
    //System.out.println("AstroContext.calcBodyCoords() - astroEngine = " + _astroEngine);
    if (_astroEngine.equals(AstroEngine.SWISS_EPHEMERIS)){
      calcFromSwissEphemeris(frame, sphereCart, precision, velocities, units);
      return;
    }

    // JEphem computation

    // handle moon calculation separately
    if (_moonIndex != GeneralConstants.NO_SPECIF)
      calcMoonCoord(frame, sphereCart, precision, velocities, units);

    // variables used in several places.
    Vector3 pos, vel; // position and velocity vectors.
    Matrix3 brsFk5, P, N, trueEqEc; // various matrices used in the method
    Body b;
    int i;

    try{ // general try for AstroException

      // ****** 1a - calculate Earth's geometric heliocentric ecliptic position
      // Refrence Frame : Theory
      // Center of coordinate system : Sun
      // Reference plane : Mean Ecliptic JD2000
      Body geomEarth = new Body(EARTH);
      VSOP87.calcCoord(_jd, geomEarth, precision, velocities);

      // ****** 1b - calculate BRS planets' geometric heliocentric ecliptic positions
      // Refrence Frame : Theory
      // Center of coordinate system : Sun
      // Reference plane : Mean Ecliptic JD2000
      for (i=0; i < _bodies.length; i++){
        switch(_bodies[i].getIndex()){
          case SUN:
            // Build a body with coords set to 0, and other characteristics like in VSOP87
            _bodies[i].setFrame(SpaceConstants.FRAME_THEORY);
            _bodies[i].setCoordinateExpression(SpaceConstants.CARTESIAN);
            _bodies[i].setPositionUnits(UNITGROUP_AU_AU_AU);
            if (velocities) _bodies[i].setVelocityUnits(UNITGROUP_AUD_AUD_AUD);
            _bodies[i].setPositionCoords(0, 0, 0);
            if (velocities) _bodies[i].setVelocityCoords(0, 0, 0);
          break;
          case MOON:
          break;
          case EARTH:
            _bodies[i] = geomEarth.doClone();
          break;
          case PLUTO:
            Pluto99.calcCoord(_jd, _bodies[i], precision, velocities);
          break;
          default:
            VSOP87.calcCoord(_jd, _bodies[i], precision, velocities);
          break;
        }
      }

      // If raw coordinates as given by the theory are wanted, return results
      if (frame == FRAME_THEORY){
        finalizeResults(frame, sphereCart, velocities, units);
        return;
      }

      // TO CHECK : implemented formula (from VSOP87.doc) different from BDL book p. 90
      brsFk5 = new Matrix3(1.0,             0.000000440360, -0.000000190919,
                            -0.000000479966, 0.917482137087, -0.397776982902,
                            0.0,             0.397776982902,  0.917482137087);

      // If ecliptic heliocentric geometric coords are wanted, convert to FK5 and return results
      if (frame == FRAME_EC_HELIO_GEOMETRIC){
        for (i=0; i<_bodies.length; i++){
          if(i == MOON){
            ///////////////////////// CODE TO WRITE /////////////////////////////
          }
          else{
            pos = _bodies[i].getPositionCoords();
            _bodies[i].setPositionCoords(Vector3.mul(brsFk5, pos));
            if (velocities){
              vel = _bodies[i].getVelocityCoords();
              _bodies[i].setVelocityCoords(Vector3.mul(brsFk5, vel));
            }
          }
          finalizeResults(frame, sphereCart, velocities, units);
          return;
        }
      }

      // ****** 2 - calculate apparent heliocentric ecliptic coordinates.
      // (change from geometric to apparent)
      // Does not correspond to something physically observable
      // Refrence Frame : Theory
      // Center of coordinate system : Sun
      // Reference plane : Mean Ecliptic JD2000
      //
      double rho, dt;
      Vector3 posGE = geomEarth.getPositionCoords();

      // Calculate apparent position of the Earth
      Body appEarth =  new Body(EARTH);
      // calculate distance between sun and geomEarth
      rho = Vector3.norm(posGE);
      // calculate time taken by light to go from Sun to geomEarth
      dt = rho * KM_PER_AU / LIGHT_VELOCITY; // (km used for KM_PER_AU and LIGHT_VELOCITY)
      dt /= SECONDS_PER_DAY; // convert dt in days
      // calculate position of earth at time jd - dt to get apparent position
      VSOP87.calcCoord(_jd - dt, appEarth, precision, velocities);

      for (i=0; i < _bodies.length; i++){
        b = _bodies[i];
        switch(b.getIndex()){
        case SUN: // nothing to do, coordinates already set to 0 when computing geometrical coord.
          break;
        case MOON: ///////////////////////// CODE TO WRITE /////////////////////////////
          break;
        case EARTH:
          _bodies[i] = appEarth.doClone();
          break;
        default:
          // calculate distance between geomEarth and planet
          rho = Vector3.norm(Vector3.sub(posGE, b.getPositionCoords()));
          // calculate time taken by light to go from planet to Earth
          dt = rho * KM_PER_AU / LIGHT_VELOCITY; // (rho converted in meters)
          dt /= SECONDS_PER_DAY; // convert dt in days
          // calculate position of planet at time jd - dt to get apparent position
          if (b.getIndex() == PLUTO){
            Pluto99.calcCoord(_jd - dt, _bodies[i], precision, velocities);
          }
          else{
            VSOP87.calcCoord(_jd - dt, _bodies[i], precision, velocities);
          }
          break;
        }
      }

      // ****** 3 - calculate (apparent) geocentric ecliptic coordinates.
      // (change center from Sun to Earth)
      // Refrence Frame : Theory
      // Center of coordinate system : Earth
      // Reference plane : Mean Ecliptic JD2000
      for (i=0; i < _bodies.length; i++){
        b = _bodies[i];
        switch(b.getIndex()){
          case SUN: // compute from apparent Earth
            _bodies[i].setPositionCoords(Vector3.negate(appEarth.getPositionCoords()));
            if (velocities){
              _bodies[i].setVelocityCoords(Vector3.negate(appEarth.getVelocityCoords()));
            }
            break;
          case MOON: ///////////////////////// CODE TO WRITE /////////////////////////////
            break;
          case EARTH:
            _bodies[i] = new Body(EARTH); // ensure coords are set to 0.
          default:
            // For the transformation, we use geomEarth and apparent planets
            _bodies[i].setPositionCoords(Vector3.sub(b.getPositionCoords(), posGE));
            if (velocities){
              _bodies[i].setVelocityCoords(
                        Vector3.sub(b.getVelocityCoords(), geomEarth.getVelocityCoords()));
            }
        }// end switch
      }// end for

      // ****** 4 - calculate FK5 (apparent) mean geocentric equatorial coordinates JD2000.
      // (change from BRS to FK5)
      // Refrence Frame : FK5
      // Center of coordinate system : Earth
      // Reference plane : Mean Equator JD2000
      for (i=0; i<_bodies.length; i++){
        if(i == MOON){
          /////////////////////// CODE TO WRITE /////////////////
        }
        else{
          pos = _bodies[i].getPositionCoords();
          _bodies[i].setPositionCoords(Vector3.mul(brsFk5, pos));
          if (velocities){
            vel = _bodies[i].getVelocityCoords();
            _bodies[i].setVelocityCoords(Vector3.mul(brsFk5, vel));
          }
        }
      }

      // ****** 5 - calculate FK5 (apparent) mean geocentric equatorial coordinates of date.
      // (apply precession)
      // Refrence Frame : FK5
      // Center of coordinate system : Earth
      // Reference plane : Mean Equator of the date

      MeanTrue mt = new MeanTrue(_jd, JD2000);
      P = mt.getEqPrecessionMatrix();
      for (i=0; i<_bodies.length; i++){
        if(i == MOON){
          /////////////////////// CODE TO WRITE /////////////////
        }
        else{
          pos = _bodies[i].getPositionCoords();
          _bodies[i].setPositionCoords(Vector3.mul(P, pos));
          if (velocities){
            vel = _bodies[i].getVelocityCoords();
            _bodies[i].setVelocityCoords(Vector3.mul(P, vel));
          }
        }
      }

      // ****** 6 - calculate FK5 (apparent) true geocentric equatorial coordinates of date.
      // (apply nutation)
      // Reference Frame : FK5
      // Center of coordinate system : Earth
      // Reference plane : True Equator of the date
      N = mt.getEqNutationMatrix();
      for (i=0; i<_bodies.length; i++){
        if(i == MOON){
          /////////////////////// CODE TO WRITE /////////////////
        }
        else{
          pos = _bodies[i].getPositionCoords();
          _bodies[i].setPositionCoords(Vector3.mul(N, pos));
          if (velocities){
            vel = _bodies[i].getVelocityCoords();
            _bodies[i].setVelocityCoords(Vector3.mul(N, vel));
          }
        }
      }

      // if geocentric apparent true equatorial coordinates are wanted, return results
      if (frame == FRAME_EQUATORIAL){
        finalizeResults(frame, sphereCart, velocities, units);
        return;
      }

      // ****** 7 - calculate FK5 (apparent) true geocentric ecliptic coordinates of date.
      // Refrence Frame : FK5
      // Center of coordinate system : Earth
      // Reference plane : Mean Ecliptic of the date
      // Reference axis : true equinox of date
      trueEqEc = mt.getTrueEqToEcMatrix();
      for (i=0; i<_bodies.length; i++){
        pos = _bodies[i].getPositionCoords();
        _bodies[i].setPositionCoords(Vector3.mul(trueEqEc, pos));
        if (velocities){
          vel = _bodies[i].getVelocityCoords();
          _bodies[i].setVelocityCoords(Vector3.mul(trueEqEc, vel));
        }
      }

      // if geocentric apparent true equatorial coordinates are wanted, return results
      if (frame == FRAME_ECLIPTIC){
        finalizeResults(frame, sphereCart, velocities, units);
        return;
      }

    }// end of general try
    catch(AstroException e){
      throw e;
    }

    // Should never execute this code
    throw new AstroException("CalcBodyCoords called with an unvalid frame");

  }// end calcBodyCoords()

  //=================================================================================
  //                                 PRIVATE METHODS
  //=================================================================================

  //******************************* finalizeResults() ******************************
  /** Always called by calcBodyCoords before returning results.
  <LI>Calls handleSphereCart() ;</LI>
  <LI>Convert units.</LI>
  @throws AstroException If sphereToCart or cartToSphere throw one.
  */
  private void finalizeResults(int frame, int sphereCart, boolean velocities, int[] units) throws AstroException{
    // No parameter checking (for units.length), relying on a good call from calcBodyCoords()
    for (int i=0; i < _bodies.length; i++){

//System.out.println("========= AstroContext.finalizeResults() - Body index = " + i);

//// trace coords
//System.out.println("position coords = " + _bodies[i].getPositionCoords().toString());
//System.out.println("velocity coords = " + _bodies[i].getVelocityCoords().toString());

//// trace position units
//String strTmp = "";
//for(int j = 0; j < 3; j++) strTmp += units[j] + " ";
//System.out.println("position units = " + strTmp);
//// trace body.posUnits
//int[] tmpUnits = _bodies[i].getPositionUnits();
//strTmp = "";
//for(int j = 0; j < 3; j++) strTmp += tmpUnits[j] + " ";
//System.out.println("Before transformation, body.posUnits = " + strTmp);

      // set Frame
      _bodies[i].setFrame(frame);
      // change to spherical if necessary
      if(sphereCart == SPHERICAL && _bodies[i].getCoordinateExpression() == CARTESIAN){
//        System.out.println("AstroContext.finalizeResults() - calling cartToSphere");
        _bodies[i].cartToSphere(velocities);
      }
      if(sphereCart == CARTESIAN && _bodies[i].getCoordinateExpression() == SPHERICAL)
         _bodies[i].sphereToCart(velocities);

//tmpUnits = _bodies[i].getPositionUnits();
//strTmp = "";
//for(int j = 0; j < 3; j++) strTmp += tmpUnits[j] + " ";
//System.out.println("After sphere/cart, body.posUnits = " + strTmp);

      //convert units
      _bodies[i].setPositionCoords(Units.convertUnits(
              _bodies[i].getPositionCoords(),
              _bodies[i].getPositionUnits(),
              new int[]{units[0], units[1], units[2]}
      ));
      _bodies[i].setPositionUnits(new int[]{units[0], units[1], units[2]});

//tmpUnits = _bodies[i].getPositionUnits();
//strTmp = "";
//for(int j = 0; j < 3; j++) strTmp += tmpUnits[j] + " ";
//System.out.println("After last transformation, body.posUnits = " + strTmp);

      if (velocities){
//// trace velocity units
//strTmp = "";
//for(int j = 3; j < 5; j++) strTmp += units[j] + " ";
//System.out.println("velocity units = " + strTmp);
//// trace body.velUnits
//tmpUnits = _bodies[i].getVelocityUnits();
//strTmp = "";
//for(int j = 0; j < 3; j++) strTmp += tmpUnits[j] + " ";
//System.out.println("Body.velUnits = " + strTmp);
        _bodies[i].setVelocityCoords(Units.convertUnits(
                _bodies[i].getVelocityCoords(),
                _bodies[i].getVelocityUnits(),
                new int[]{units[3], units[4], units[5]}
        ));
      _bodies[i].setVelocityUnits(new int[]{units[3], units[4], units[5]});
      }
    }// end for
    return;
  }// end finalizeResults

  //******************************* calcMoonCoord ******************************
  /** Computation of Moon coordinates - handled separately as changes of frames are different.
  @throws AstroExcetion if ELP82 throws one.
  */
  private void calcMoonCoord(int     frame,
                             int     sphereCart,
                             double  precision,
                             boolean velocities,
                             int[]   units
                            ) throws AstroException{

    ELP82.calcCoord(_jd, _bodies[_moonIndex], precision, velocities);
  }// end calcMoonCoord

  //******************************* calcFromSwissEphemeris ******************************
  /** Use SwissEphemeris to compute.
  @throws AstroExcetion if SwissEphemeris throws one.
  */
  private void calcFromSwissEphemeris(int     frame,
                                      int     sphereCart,
                                      double  precision,
                                      boolean velocities,
                                      int[]   units
                                      ) throws AstroException{
    for(int i = 0; i < _bodies.length; i++){
      // WARNING : when SwissEphemeris throws AstroException,
      // put try catch here and fill strAstroException
      jephem.astro.solarsystem.SwissEphemeris.calcBodyCoords(_jd,
                                                             _bodies,
                                                             frame,
                                                             sphereCart,
                                                             precision,
                                                             velocities,
                                                             units);
    }// end for
    return;

  }// end calcFromSwissEphemeris()

}//end class AstroContext
