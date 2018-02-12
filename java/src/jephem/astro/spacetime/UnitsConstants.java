//*********************************************************************************
// interface jephem.astro.spacetime.UnitsConstants
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;

/*********************************************************************************
Interface containing constants to identify units.
@author Thierry Graff
@history feb 16 2002 : Creation from Units

**********************************************************************************/
public interface UnitsConstants{

  //=================================================================================
  //                             PUBLIC CONSTANTS
  //=================================================================================

  // ***** Degree formatting ********
  /** Constant used to indicate that degrees should be formated as "degrees minutes seconds"
  (ex : <CODE>123°18'23"</CODE>) - value : 0. */
  public static final int DEGREES_DMS = 0;
  /** Constant used to indicate that degrees should be formated as decimal degrees - value : 1. */
  public static final int DEGREES_DECIMAL = 1;

  //******************************************************
  // Constants to desingate unit types.
  // Values used for array purposes ; must be 0, 1 ...
  //******************************************************
  // ***** Distance units ********
  /** Constant designating units of type "linear disance" - value : 0. */
  public static final int TYPE_DISTANCE = 0;
  /** Constant designating units of type "linear speed" - value :  1. */
  public static final int TYPE_LINEAR_SPEED = 1;
  /** Constant designating units of type "angular" - value : 2. */
  public static final int TYPE_ANGULAR = 2;
  /** Constant designating units of type "angular speed" - value : 3. */
  public static final int TYPE_ANGULAR_SPEED = 3;

  //******************************************************
  // Constants to desingate units.
  // Values used for array purposes ;
  // Each constant has a unique value ;
  // values of one type must be BASE + 0, BASE + 1, BASE + 2 ...
  //
  // For getUnitType purposes, BASEs must be correlated with TYPEs
  // If the values of the constants change, DELTA_BETWEEN_BASES must be also modified
  //
  // Some constants have a package visibility to be accessed by Units.
  //******************************************************

  static final int DELTA_BETWEEN_BASES = 100;
  // ***** Distance units (metric units) ********
  static final int BASE_DISTANCE_UNIT = 0;
  /** Constant designating the "astronomical unit" unit - value : 0. */
  public static final int DISTANCE_UNIT_AU = 0;
  /** Constant designating the "kilometer" unit - value : 1. */
  public static final int DISTANCE_UNIT_KM = 1;
  /** Constant designating the "meter"  unit - value : 2. */
  public static final int DISTANCE_UNIT_M = 2;

  // ***** Linear speed units ********
  static final int BASE_LINEAR_SPEED_UNIT = 100;
  /** Constant designating the "astronomical unit per day" unit - value : 100. */
  public static final int LINEAR_SPEED_UNIT_AU_PER_D = 100;
  /** Constant designating the "kilometer per day" unit - value : 101. */
  public static final int LINEAR_SPEED_UNIT_KM_PER_D = 101;
  /** Constant designating the "kilometer per hour" unit - value : 102. */
  public static final int LINEAR_SPEED_UNIT_KM_PER_HOUR = 102;
  /** Constant designating the "meter per second" unit - value : 103. */
  public static final int LINEAR_SPEED_UNIT_M_PER_S = 103;

  // ***** Angular units ********
  static final int BASE_ANGULAR_UNIT = 200;
  /** Constant designating the "arc second" unit - value : 200. */
  public static final int ANGULAR_UNIT_ARCSEC = 200;
  /** Constant designating the "decimal degree" unit - value : 201. */
  public static final int ANGULAR_UNIT_DEG = 201;
  /** Constant designating the "radian" unit - value : 202. */
  public static final int ANGULAR_UNIT_RAD = 202;

  // ***** Angular speed units ********
  static final int BASE_ANGULAR_SPEED_UNIT = 300;
  /** Constant designating the "arc second per time second" unit - value : 300. */
  public static final int ANGULAR_SPEED_UNIT_ARCSEC_PER_S = 300;
  /** Constant designating the "degree per second" unit - value : 301. */
  public static final int ANGULAR_SPEED_UNIT_DEG_PER_S = 301;
  /** Constant designating the "arc second per day" unit - value : 302. */
  public static final int ANGULAR_SPEED_UNIT_ARCSEC_PER_DAY = 302;
  /** Constant designating the "degree per day" unit - value : 303. */
  public static final int ANGULAR_SPEED_UNIT_DEG_PER_DAY = 303;
  /** Constant designating the "radian per day" unit - value : 304. */
  public static final int ANGULAR_SPEED_UNIT_RAD_PER_DAY = 304;

  //******************************************************
  // Constants to desingate groups of 3 units.
  //******************************************************

  /** Constant designating the "a.u., a.u., a.u." group of units (for positions, cartesian coordinates). */
  public static final int[] UNITGROUP_AU_AU_AU = { DISTANCE_UNIT_AU,
                                                   DISTANCE_UNIT_AU,
                                                   DISTANCE_UNIT_AU
                                                 };
  /** Constant designating the "a.u., a.u., a.u." group of units (for positions, cartesian coordinates). */
  public static final int[] UNITGROUP_KM_KM_KM = { DISTANCE_UNIT_KM,
                                                   DISTANCE_UNIT_KM,
                                                   DISTANCE_UNIT_KM
                                                 };
  /** Constant designating the "au/d, au/d, au/d" group of units (for velocities, cartesian coordinates). */
  public static final int[] UNITGROUP_AUD_AUD_AUD = { LINEAR_SPEED_UNIT_AU_PER_D,
                                                      LINEAR_SPEED_UNIT_AU_PER_D,
                                                      LINEAR_SPEED_UNIT_AU_PER_D
                                                    };
  /** Constant designating the "a.u., rad, rad" group of units (for positions, spherical coordinates). */
  public static final int[] UNITGROUP_AU_RAD_RAD = { DISTANCE_UNIT_AU,
                                                     ANGULAR_UNIT_RAD,
                                                     ANGULAR_UNIT_RAD
                                                   };

  /** Constant designating the "a.u., deg, deg" group of units (for positions, spherical coordinates). */
  public static final int[] UNITGROUP_AU_DEG_DEG = { DISTANCE_UNIT_AU,
                                                     ANGULAR_UNIT_DEG,
                                                     ANGULAR_UNIT_DEG
                                                   };

  //=================================================================================
  //                             PRIVATE CONSTANTS
  //=================================================================================


}//end interface UnitsConstants