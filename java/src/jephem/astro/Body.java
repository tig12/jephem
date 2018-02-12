//*********************************************************************************
// class:		jephem.astro.Body
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro;

import jephem.astro.AstroException;
import jephem.astro.solarsystem.SolarSystemConstants;
import jephem.astro.solarsystem.SolarSystem;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.spacetime.Units;
import jephem.astro.spacetime.UnitsConstants;
import jephem.astro.solarsystem.ComputationException;

import tig.GeneralConstants;
import tig.maths.Maths;
import tig.maths.Vector3;

/******************************************************************************
An object of this class represents a heavenly body.
<BR>A <CODE>Body</CODE> characterized by :
<LI>its <B><CODE>index</CODE></B>, corresponding to a <CODE>SolarSystemConstants</CODE> constant -
accessible via <CODE>getIndex()</CODE> ;
</LI>
<LI>its coordinates, <B><CODE>x0, x1, x2, v0, v1, v2</CODE></B>, for its position and velocity -
accessible via <CODE>get/setX0() ... get/setV2(), get/setCoords(), get/setPositionCoords(), get/setVelocityCoords()</CODE> ;
</LI>
<LI>its <B><CODE>frame</CODE></B>, the reference frame in which its coordinates are expressed -
accessible via <CODE>get/setFrame()</CODE>;</LI>
<LI>its <B><CODE>coordinateExpression</CODE></B>, the expression mode of its coordinates (spherical / cartesian) -
accessible via <CODE>get/setCoordinateExpression()</CODE>;</LI>
<LI>The <B>units</B> used to express its coordinates : <CODE>positionUnits</CODE> and <CODE>velocityUnits</CODE> -
accessible via <CODE>get/setPositionUnits()</CODE> and <CODE>get/setVelocityUnits()</CODE>.</LI>

<BR><BR><LI>This class is also used to store the {@link jephem.astro.solarsystem.ComputationException} which may occur during the
computation of its coordinates.</LI>

<BR><BR>For spherical coordinates, we have :
<LI><CODE>x0 = <FONT FACE="Symbol">r</FONT></CODE>,</LI>
<LI><CODE>x1 = <FONT FACE="Symbol">q</FONT></CODE>, belongs to [0, 2<FONT FACE="Symbol">p</FONT>[</LI>
<LI><CODE>x2 = <FONT FACE="Symbol">j</FONT></CODE>, belongs to [-<FONT FACE="Symbol">p</FONT>/2, <FONT FACE="Symbol">p</FONT>/2].</LI>

<BR><BR>Methods of this class (like <CODE>cartToSphere</CODE>) assume that this order is respected.

<BR><BR><B>WARNING</B> : internal coherence of data is not ensured. For example, a call to <CODE>setFrame</CODE>
won't internally generate coordinate transformations.

@author Thierry Graff
@history dec 17 2000 : creation from JAstre, an old class.
@history dec 28 2000 : creation from a former version.
@history dec 25 2000 : added methods and member variables of Coord.java.
@history may 23 2001 : adopted new conventions (all instance variables private)
@history aug 15 2001 : replaced coordSyst by frame ; added pos and velUnitGroup.
@history jan 09 2002 : changed pos and velUnitGroups to int[] posUnits and velUnits.
@history jan 29 2002 : replaced _x0 ... _v2 by _coords.

@todo doClone is not a clean way to handle the copy.
@todo internationalize getName()
@todo WARNING : handle coherence for set/get frame and coordExpr?
@todo is empty constructor really useful?
@todo write constructor with Vectors for coords?
@todo find a better way for doClone() (ugly coding).
*********************************************************************************/
public class Body implements GeneralConstants, UnitsConstants, SolarSystemConstants{

  //=================================================================================
  //                                 INSTANCE VARIABLES
  //=================================================================================

  /** Index of this body, using {@link SolarSystemConstants} constants. */
  private int _index;

  /** Body's coordinates ; _coords[0, 1, 2] : position ; _coords[3, 4, 5] : velocity.
  <BR>Designated as x0, x1, x2, v1, v2, v3 for public. */
  private double[] _coords = new double[6];

  /** Frame (equatorial, ecliptic...) in which its coordinates are expressed.
  Use {@link jephem.astro.spacetime.SpaceConstants} constants for it. */
  private int _frame;

  /** Way its coordinates are expressed (spherical, cartesian).
  Use {@link jephem.astro.spacetime.SpaceConstants} constants for it. */
  private int _coordExpr;

  /** Units used to express the positions ; constants of {@link jephem.astro.spacetime.UnitsConstants}.
  designating units or unit groups can be used. */
  private int[] _posUnits;

  /** Units used to express the velocities ; constants of {@link jephem.astro.spacetime.UnitsConstants}
  designating units or unit groups can be used. */
  private int[] _velUnits;

  /** Field which holds a computation exception that may occur when the body is computed. */
  private ComputationException _ce;

  //=================================================================================
  //                                 CONSTRUCTORS
  //=================================================================================

  private static final int[] noSpecifArray = {NO_SPECIF, NO_SPECIF, NO_SPECIF};
  //***************************** Body(index) *************************************
  /** Constructor to use in general. Coordinates are initialized to 0.0 ; <CODE>frame</CODE>,
  <CODE>coordinateExpression</CODE>, <CODE>positionUnits</CODE>, <CODE>velocityUnits</CODE>
  are initialized to <B><CODE>GeneralConstants.NO_SPECIF</CODE></B>.

  @param index index of Body to create, using {@link SolarSystemConstants} constants.
  ********************************************************************************/
  public Body(int index){
    this(index, NO_SPECIF, NO_SPECIF, noSpecifArray, noSpecifArray,
         0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }//end Body(index)

  //********** Body(index, frame, coordExpr, posUnits, velUnits, x0, x1, x2, v0, v1, v2) ***********
  /** Complete constructor.
  @param index index of Body to create ; use {@link SolarSystemConstants} constants.
  @param frame Coordinate system in which body coordinates must be expressed ;
         use {@link jephem.astro.spacetime.SpaceConstants} constants.
  @param coordExpr Way coordinates must be expressed (cartesian, spherical) ;
         use {@link jephem.astro.spacetime.SpaceConstants} constants.
  @param posUnits Units used for the positions (use {@link jephem.astro.spacetime.UnitsConstants} constants).
  @param velUnits Units used for the velocities (use {@link jephem.astro.spacetime.UnitsConstants} constants).
  @param x0 Value of the first position coordinate.
  @param x1 Value of the second position coordinate.
  @param x2 Value of the third position coordinate.
  @param v0 Value of the first velocity coordinate.
  @param v1 Value of the second velocity coordinate.
  @param v2 Value of the third velocity coordinate.

  @throws IllegalArgumentException if <CODE>posUnits.lenght</CODE> or
  <CODE>velUnits.lenght != 3</CODE>.
  *******************************************************************************/
  public Body(int index, int frame, int coordExpr,
              int[] posUnits, int[] velUnits,
              double x0, double x1, double x2,
              double v0, double v1, double v2){
    if(posUnits.length != 3 || velUnits.length != 3)
      throw new IllegalArgumentException("Position and velocity units must have 3 elements.");
    _index = index;
    _frame = frame;
    _coordExpr = coordExpr;
    _posUnits = posUnits;
    _velUnits = velUnits;
    _coords[0] = x0;
    _coords[1] = x1;
    _coords[2] = x2;
    _coords[3] = v0;
    _coords[4] = v1;
    _coords[5] = v2;
  }//end Body(index, frame, coordExpr, posUnits, velUnits, x0, x1, x2, v0, v1, v2)

  //=================================================================================
  //                                 GET / SET METHODS
  //=================================================================================

  //***************** get / setIndex() *********************************************
  /** Returns the index of this body, using {@link SolarSystemConstants} constants. */
  public int getIndex() {return _index;}
  /** Sets the index of this body, using SolarSystemConstants constants. */
  public void setIndex(int index) {_index = index;}

  //***************** getName() *********************************************
  /** Returns the English name of this body.
  <BR>Equivalent to <CODE>SolarSystem.getBodyName(this.getIndex())</CODE>
  */
  public String getName() {return SolarSystem.getBodyName(_index);}

  //***************** get / setFrame() ****************************
  /** Returns the reference frame in which the body's coordinates are expressed,
  using {@link jephem.astro.spacetime.SpaceConstants}.FRAME_XXX constants. */
  public int getFrame() {return _frame;}
  /** Sets the reference frame in which the body's coordinates are expressed ;
  <BR>use {@link jephem.astro.spacetime.SpaceConstants}.FRAME_XXX constants. */
  public void setFrame(int frame) {_frame = frame;}

  //***************** get / setCoordinateExpression() **************************
  /** Returns the way body's coordinates are expressed (spherical, cartesian),
  using {@link jephem.astro.spacetime.SpaceConstants} constants for it. */
  public int getCoordinateExpression() {return _coordExpr;}
  /** Sets the way body's coordinates are expressed ;
  use {@link jephem.astro.spacetime.SpaceConstants}.<CODE>SPHERICAL</CODE> or
  <CODE>CARTESIAN</CODE> constants for it. */
  public void setCoordinateExpression(int coordExpr) {_coordExpr = coordExpr;}

  //**************** get / set position units *******************
  /** Returns the units used to express the positions, using constants of
  {@link jephem.astro.spacetime.UnitsConstants}. */
  public int[] getPositionUnits(){ return _posUnits; }
  /** Sets the units used to express the positions, using constants of
  {@link jephem.astro.spacetime.UnitsConstants}. */
  public void setPositionUnits(int[] positionUnits){
    if(positionUnits.length != 3)
      throw new IllegalArgumentException("'positionUnits' must have 3 elements.");
    _posUnits = positionUnits;
  }// end setPositionUnits

  //**************** get / set velocity units *******************
  /** Returns the units used to express the velocities, using constants of
  {@link jephem.astro.spacetime.UnitsConstants}. */
  public int[] getVelocityUnits() { return _velUnits; }
  /** sets the units used to express the velocities, using constants of
  {@link jephem.astro.spacetime.UnitsConstants}. */
  public void setVelocityUnits(int[] velocityUnits){
    if(velocityUnits.length != 3)
      throw new IllegalArgumentException("'velocityUnits' must have 3 elements.");
    _velUnits = velocityUnits;
  }// end setVelocityUnits


  //***************** get / setCoord ******************************
  /** Returns the coordinate designated by 'iCoord' .
  <BR><B>WARNING</B> : if parameter 'iCoord' not valid, just returns 0.
  @param iCoord : index of coordinate to get (0 : x0, ... , 5 : v2).
  {@link jephem.astro.spacetime.SpaceConstants}.COORD_XX constants can
  be used to express index.
  */
  public double getCoord(int iCoord) {
    if(iCoord > -1 && iCoord < 6)
      return _coords[iCoord];
    else
      throw new IllegalArgumentException("Parameter 'iCoord' must be between 0 and 5");
  }// end getCoord(iCoord)

  /** Sets a coordinate using an index to designate the coordinate.
  @param iCoord : index of coordinate to get (0 : x0, ... , 5 : v2). */
  public void setCoord(int iCoord, double coord) {
    if(iCoord > -1 && iCoord < 6)
      _coords[iCoord] = coord;
    else
      throw new IllegalArgumentException("Parameter 'iCoord' must be between 0 and 5");
  } // end setCoord

  //***************** get / set Position coordinates ******************************
  /** Returns a vector containing the position coordinates. */
  public Vector3 getPositionCoords(){
    return new Vector3(_coords[0], _coords[1], _coords[2]);
  }
  /** Sets the position coordinates from a vector. */
  public void setPositionCoords(Vector3 pos){
    _coords[0] = pos.x0; _coords[1] = pos.x1; _coords[2] = pos.x2;
  }
  /** Sets the position coordinates from 3 <CODE>doubles</CODE>. */
  public void setPositionCoords(double x0, double x1, double x2){
    _coords[0] = x0; _coords[1] = x1; _coords[2] = x2;
  }

  //***************** get / set Velocity coordinates ******************************
  /** Returns a vector containing the position coordinates. */
  public Vector3 getVelocityCoords(){
    return new Vector3(_coords[3], _coords[4], _coords[5]);
  }
  /** Sets the velocity coordinates from a vector. */
  public void setVelocityCoords(Vector3 vel){
    _coords[3] = vel.x0; _coords[4] = vel.x1; _coords[5] = vel.x2;
  }
  /** Sets the velocity coordinates from 3 <CODE>doubles</CODE>. */
  public void setVelocityCoords(double x0, double x1, double x2){
    _coords[3] = x0; _coords[4] = x1; _coords[5] = x2;
  }

  //***************** Computation exceptions ******************************
  /** To store a computation exception, if one is thrown while computing the coordinates of this body. */
  public void setComputationException(ComputationException ce){ _ce = ce;}
  /** Returns a computation exception, if one was thrown while computing the coordinates of this body. */
  public ComputationException getComputationException(){ return _ce;}

  //=================================================================================
  //                                 OTHER METHODS
  //=================================================================================

  //***************** doClone *************************************************
  /** Returns a copy of this body.
  @param source <CODE>Body</CODE> from all fields are copied.
  */
  public Body doClone(){
    return new Body(_index, _frame, _coordExpr, _posUnits, _velUnits,
                    _coords[0], _coords[1], _coords[2], _coords[3], _coords[4], _coords[5]);
  }// end doClone()

  //***************** sphereToCart() *********************************************
  /** Transformation of coordinates (positions and velocities), from spherical to cartesian.
  @pre this.getCoordinateExpression() = SpaceConstants.SPHERICAL
  @post this.getCoordinateExpression() = SpaceConstants.CARTESIAN
  @param velocities Indicates if the velocities should be also transformed.
  @throws IllegalArgumentException If units have not been assigned before trying to transform
  */
  public void sphereToCart(boolean velocities) throws AstroException{
    if (_coordExpr != SpaceConstants.SPHERICAL) return;

    // Take care of units for positions
    if(_posUnits[0] == NO_SPECIF || _posUnits[1] == NO_SPECIF || _posUnits[2] == NO_SPECIF)
      throw new IllegalArgumentException("Spherical to cartesian conversion can't be done : position " +
                                "units of coord to transform are not specified");
    // Convert to radians.
    this.setPositionCoords(Units.convertUnits(this.getPositionCoords(), _posUnits,
      new int[]{_posUnits[0], ANGULAR_UNIT_RAD, ANGULAR_UNIT_RAD}));

    // Now perform the transformation

    // variables to remember initial values.
    double rho = _coords[0];
    double theta = _coords[1];
    double phi = _coords[2];

    double cTheta = Math.cos(theta);
    double sTheta = Math.sin(theta);
    double cPhi = Math.cos(phi);
    double sPhi = Math.sin(phi);

    _coords[0] = rho * cPhi * cTheta; // X
    _coords[1] = rho * cPhi * sTheta; // Y
    _coords[2] = rho * sPhi;  // Z

    // Re-assign units (X, Y, Z are in the unit of rho).
    _posUnits[1] = _posUnits[0];
    _posUnits[2] = _posUnits[0];

    // Case of velocities
    if (velocities){
      if(_velUnits[0] == NO_SPECIF || _velUnits[1] == NO_SPECIF || _velUnits[2] == NO_SPECIF)
        throw new IllegalArgumentException("Spherical to cartesian conversion can't be done : velocity " +
                                  "units of coord to transform are not specified");
      // First, force unit conversion.
      this.setVelocityCoords(Units.convertUnits(
              this.getVelocityCoords(),
              this.getVelocityUnits(),
              new int[]{LINEAR_SPEED_UNIT_AU_PER_D,
                        ANGULAR_SPEED_UNIT_RAD_PER_DAY,
                        ANGULAR_SPEED_UNIT_RAD_PER_DAY})
      );
      // variables to remember initial values (rhoP stands for "rho point" etc...).
      double rhoP = _coords[3];
      double thetaP = _coords[4];
      double phiP = _coords[5];

      _coords[3] = cPhi*cTheta*rhoP + rho*cPhi*sTheta*thetaP + rho*sPhi*cTheta*phiP;
      _coords[4] = cPhi*sTheta*rhoP - rho*cPhi*cTheta*thetaP + rho*sPhi*sTheta*phiP;
      _coords[5] = sPhi*rhoP                                 - rho*cPhi*phiP;
      // Re-assign units
      this.setVelocityUnits(UNITGROUP_AUD_AUD_AUD);
    }
    _coordExpr = SpaceConstants.SPHERICAL;
  }// end sphereToCart

  //***************************** cartToSphere ************************************
  /** Transformation of coordinates (positions and velocities), from cartesian to spherical.
  @pre _coordExpr = SpaceConstants.CARTESIAN
  @post _coordExpr = SpaceConstants.SPHERICAL
  @post _posUnits = {xxx, RADIANS, RADIANS}
  @post _velUnits = {xxx, RADIANS_PER_DAY, RADIANS_PER_DAY}
  @param velocities Indicates if the velocities should be also transformed.
  @throws IllegalArgumentException If units have not been assigned before trying to transform
  */
  public void cartToSphere(boolean velocities){
    if (_coordExpr != SpaceConstants.CARTESIAN) return;

    // ***** 1 - positions
    // Take care of units for positions
    if(_posUnits[0] == NO_SPECIF || _posUnits[1] == NO_SPECIF || _posUnits[2] == NO_SPECIF)
      throw new IllegalArgumentException("Cartesian to spherical conversion can't be done : position " +
                                "units of coord to transform are not specified");
    int posUnit = NO_SPECIF;
    if(_posUnits[0] == _posUnits[1] && _posUnits[0] == _posUnits[2]){
      // no problem, the 3 units are the same
      posUnit = _posUnits[0];
    }
    else{ // all 3 must have the same unit - so convert to km
      posUnit = DISTANCE_UNIT_KM;
      this.setPositionCoords(Units.convertUnits(
              this.getPositionCoords(),
              this.getPositionUnits(),
              new int[]{posUnit, posUnit, posUnit})
      );
      this.setPositionUnits(new int[]{posUnit, posUnit, posUnit});
    }

    // Now perform the transformation

    // variables to remember initial values.
    double X = _coords[0];
    double Y = _coords[1];
    double Z = _coords[2];
    double rho2 = X*X + Y*Y + Z*Z;

    // rho
    _coords[0] = Math.sqrt(rho2);
    // theta
    _coords[1] = Maths.atan3(Y, X);
    // phi
    _coords[2] = Math.asin(Z / _coords[0]);

    // Re-assign units
    this.setPositionUnits(new int[]{posUnit, ANGULAR_UNIT_RAD, ANGULAR_UNIT_RAD});

    // ***** 2 - velocities

    if (velocities){
      // take care of units
      if(_velUnits[0] == NO_SPECIF || _velUnits[1] == NO_SPECIF || _velUnits[2] == NO_SPECIF)
        throw new IllegalArgumentException("Cartesian to spherical conversion can't be done : velocity " +
                                  "units of coords to transform are not all specified");
      int velUnit = NO_SPECIF;
      if(_velUnits[0] == _velUnits[1] && _velUnits[0] == _velUnits[2]){
        // no problem, the 3 units are the same
        velUnit = _velUnits[0];
      }
      else{ // all 3 must have the same unit - so convert to arbitrary unit
        velUnit = LINEAR_SPEED_UNIT_M_PER_S ;
        this.setVelocityCoords(Units.convertUnits(
                this.getVelocityCoords(),
                this.getVelocityUnits(),
                new int[]{velUnit, velUnit, velUnit})
        );
      }

      // Formulae imply X, Y, Z and Xp, Yp, Zp, so we must be careful with units
      // We know that X, Y, Z have the same unit : posUnit
      // We know that Xp, Yp, Zp have the same unit : velUnit
      // We must first ensure that distance units of Xp, Yp, Zp equals to the unit of X, Y, Z.
      switch(velUnit){
        case LINEAR_SPEED_UNIT_AU_PER_D :
          if(posUnit == DISTANCE_UNIT_AU) break;
          if(posUnit == DISTANCE_UNIT_KM){ //convert to au
            X /= KM_PER_AU; Y /= KM_PER_AU; Z /= KM_PER_AU;
            break;
          }
          if(posUnit == DISTANCE_UNIT_M){ //convert to au
            X /= (KM_PER_AU * 1000); Y /= (KM_PER_AU * 1000); Z /= (KM_PER_AU * 1000);
            break;
          }
        case LINEAR_SPEED_UNIT_KM_PER_HOUR :
        case LINEAR_SPEED_UNIT_KM_PER_D :
          if(posUnit == DISTANCE_UNIT_KM) break;
          if(posUnit == DISTANCE_UNIT_AU){ //convert to km
            X *= KM_PER_AU; Y *= KM_PER_AU; Z *= KM_PER_AU;
            break;
          }
          if(posUnit == DISTANCE_UNIT_M){ //convert to km
            X /= 1000; Y /= 1000; Z /= 1000;
            break;
          }
        case LINEAR_SPEED_UNIT_M_PER_S :
          if(posUnit == DISTANCE_UNIT_M) break;
          if(posUnit == DISTANCE_UNIT_AU){ //convert to m
            X *= (KM_PER_AU * 1000); Y *= (KM_PER_AU * 1000); Z *= (KM_PER_AU * 1000);
            break;
          }
          if(posUnit == DISTANCE_UNIT_KM){ //convert to m
            X *= 1000; Y *= 1000; Z *= 1000;
            break;
          }
      }// end switch(velUnit)
      // Now the coords are coherent , and the formulae can be applied

      // variables to remember initial values (Xp, Yp, Zp stand for "X point" etc...).
      double Xp = _coords[3];
      double Yp = _coords[4];
      double Zp = _coords[5];
      double r2 = rho2 - Z*Z; // = X*X + Y*Y

      // d(rho)/dt
      _coords[3] = (X*Xp + Y*Yp + Z*Zp) / Math.sqrt(rho2);
      // d(theta)/dt
      _coords[4] = (Xp*Y - X*Yp) / r2;
      // d(phi)/dt
      _coords[5] = (Z * (X*Xp + Y*Yp) - r2*Zp) / (rho2 * Math.sqrt(r2));

      // Re-assign units
      // The present unit are : angular units = radians
      // Time units of d(theta)/dt and d(phi)/dt depend on the time unit of Xp, Yp, Zp
      // This can take the values : au/d, km/d, km/h, km/s
      // A honnest conversion would lead to rad/s, rad/h or rad/d
      // To avoid adding new constants in class UnitConstants, we impose rad/day, which already exists.
      switch(_velUnits[0]){ // Xp unit
        case LINEAR_SPEED_UNIT_AU_PER_D : break;
        case LINEAR_SPEED_UNIT_KM_PER_HOUR :
          _coords[3] /= 24.0; _coords[4] /= 24.0; _coords[5] /= 24.0; break;
        case LINEAR_SPEED_UNIT_M_PER_S :
          _coords[3] /= 86400.0; _coords[4] /= 86400.0; _coords[5] /= 86400.0; break;
      }
      this.setVelocityUnits(new int[]{velUnit,
                                      ANGULAR_SPEED_UNIT_RAD_PER_DAY,
                                      ANGULAR_SPEED_UNIT_RAD_PER_DAY});
    }// end if (velocities)

    _coordExpr = SpaceConstants.SPHERICAL;
  }// end cartToSphere


  //=================================================================================
  //=================================================================================
  //                                      TESTS
  //=================================================================================
  //=================================================================================

  //***********************************************
  public String toString(){
    String res = "========== Body.toString() ====================" + LS;
    res += "index : " + this._index;
    res += "    frame : " + this._frame;
    res += "    coordExpr : " + this._coordExpr;
    res += LS;
    System.out.println("coords : ");
    for (int i = 0; i < _coords.length; i++) {
      res += "  " + _coords[i];
    }
    res += LS;
    res += "position units : ";
    for (int i = 0; i < _posUnits.length; i++) {
      System.out.println("    " + _posUnits[i]);
    }
    res += LS;
    res += "velocity units : ";
    for (int i = 0; i < _velUnits.length; i++) {
      res += "    " + _velUnits[i];
    }
    return res;
  }//end traceBody

}//end class Body