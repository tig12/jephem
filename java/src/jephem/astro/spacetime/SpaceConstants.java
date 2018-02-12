//*********************************************************************************
// interface jephem.astro.spacetime.SpaceConstants
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;

/*********************************************************************************
Interface containing constants related with space.
Related methods can be found in class {@link Space}.

@author Thierry Graff
@history 2001.xx.xx : Creation from a part of jephem.astro.Body.java.

@todo : remove the labels from coordGroup comments
**********************************************************************************/
public interface SpaceConstants{

  //=================================================================================
  //                                CONSTANTS
  //=================================================================================

  /** Value of light velocity, (value UAI 1976 : 299792458 km.s<SUP>-1</SUP>). */
  public static final double LIGHT_VELOCITY = 299792458;

  //****************************************************
  // Constants to desingate coordinate expressions.
  // Values used for array purposes ; must be 0, 1, 2 ...
  //****************************************************
  /** Constant designating the cartesian way to express coordinates. */
  public static final int CARTESIAN = 0;
  /** Constant designating the spherical way to express coordinates. */
  public static final int SPHERICAL = 1;
  /** Array containing labels of ways to express coordinates.
  <BR>Use <CODE>SpaceConstants.CARTESIAN</CODE> or <CODE>SPHERICAL</CODE> constants to access to
  its elements. */
//  public static final String[] coordExprLabels = {"Cartesian", "Spherical"};

  //****************************************************
  // Constants to desingate frames.
  // Values used for array purposes ; must be 0, 1, 2 ...
  //****************************************************
  /** Constant indicating that a coordinate is expressed in the frame
  of the theory used to compute it. */
  public static final int FRAME_THEORY = 0;

  /** Constant designating heliocentric ecliptic reference frame,
  for geometric coordinates. */
  public static final int FRAME_EC_HELIO_GEOMETRIC = 1;

  /** Constant designating geocentric ecliptic reference frame ;
  coordinates expressed in this frame are <B>true apparent coordinates</B>.
  <BR>Reference system : FK5.
  <BR>Reference plane = mean ecliptic of date.
  <BR>Equinox = true equinox of date.
  */
  public static final int FRAME_ECLIPTIC = 2;

  /** Constant designating geocentric equatorial reference frame ;
  coordinates expressed in this frame are <B>true apparent coordinates</B>.
  <BR>Reference system : FK5.
  <BR>Reference plane = true equator of date.
  <BR>Equinox = true equinox of date.
  */
  public static final int FRAME_EQUATORIAL = 3;

  /** Constant designating equatorial topocentric reference frame. */
//  public static final int FRAME_EQUATORIAL_TOPOCENTRIC = 4;

  /** Constant designating horizontal topocentric reference frame. */
  public static final int FRAME_HORIZONTAL_TOPOCENTRIC = 5;

  /** Constant designating galactic reference frame. */
  public static final int FRAME_GALACTIC = 6;

  //******************************************************
  // Constants to desingate coordinates.
  // Values used for array purposes ; must be 0, 1, 2 ... 5
  //******************************************************

  /** Usual number of coordinates - value= 3 ; just for cleaner code. */
  public static final int NB_COORDS = 3;

  /** Constant designating the first spatial coordinate (<B>X</B> in cartesian, <B><FONT FACE="Symbol">r</FONT></B> in spherical). */
  public static final int COORD_X0 = 0;
  /** Constant designating the second spatial coordinate (<B>Y</B> in cartesian, <B><FONT FACE="Symbol">q</FONT></B> in spherical). */
  public static final int COORD_X1 = 1;
  /** Constant designating the third spatial coordinate (<B>Z</B> in cartesian, <B><FONT FACE="Symbol">j</FONT></B> in spherical). */
  public static final int COORD_X2 = 2;
  /** Constant designating the first velocity coordinate (<B>dX/dt</B> in cartesian, <B>d<FONT FACE="Symbol">r</FONT>/dt</B> in spherical). */
  public static final int COORD_V0 = 3;
  /** Constant designating the second velocity coordinate (<B>dY/dt</B> in cartesian, <B>d<FONT FACE="Symbol">q</FONT>/dt</B> in spherical). */
  public static final int COORD_V1 = 4;
  /** Constant designating the third velocity coordinate (<B>dZ/dt</B> in cartesian, <B>d<FONT FACE="Symbol">j</FONT>/dt</B> in spherical). */
  public static final int COORD_V2 = 5;

  //******************************************************
  // Constants to desingate groups of 3 coordinates.
  // Values used for array purposes ; must be 0, 1, 2 ...
  //******************************************************

  /** Constant designating the "X, Y, Z" group of coordinates.
  <BR><CODE>labels = {"X", "Y", "Z"}</CODE>. */
  public static final int COORDGROUP_XYZ = 0;

  /** Constant designating the "delta, beta, lambda" group of coordinates.
  <BR><CODE>labels = {"del", "bet", "lam"}</CODE>. */
  public static final int COORDGROUP_DELTA_BETA_LAMBDA = 1;

  /** Constant designating the "r, l, b" group of coordinates.
  <BR><CODE>labels = {"r", "l", "b"}</CODE>. */
  public static final int COORDGROUP_RLB = 2;

  /** Constant designating the "distance alpha, delta" group of coordinates.
  <BR><CODE>labels = {"dist.", "alpha", "beta"}</CODE>. */
  public static final int COORDGROUP_DIST_ALPHA_DELTA = 3;

}//end interface SpaceConstants