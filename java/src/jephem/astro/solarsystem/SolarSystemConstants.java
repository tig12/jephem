//*********************************************************************************
// interface jephem.astro.solarsystem.SolarSystemConstants
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.astro.solarsystem;

/******************************************************************************
Contains general constants for ephemeris calculation.
@author Thierry Graff
@history dec 16 2000 : creation from SolarSystem.java

@todo : internationalize bodyNames[].
@todo : comments in mean obliquities - indicate the source.
*********************************************************************************/
public interface SolarSystemConstants{

  /** Value of an astronomical unit, in km (149597870.61 ; value IERS 1992). */
  public static final double KM_PER_AU = 149597870.61;

  /** Mean obliquity for t = 1900.0 (23.4522944) */
  public static final double E0_1900 = 23.4522944;
  /**  Mean obliquity for  t = 1950.0 (23.4457889) */
  public static final double E0_1950 = 23.4457889;
  /** Mean obliquity for  t = 2000.0 (23.439292). */
  public static final double E0_2000 = 23.439292;

  /** Sideral rate of Earth rotation. */
  public static final double SIDERAL_RATE = .9972695677;

  /** Number of heavenly bodies known by JEphem */
  // WARNING : if planetary constants are added / removed, this parameter must change.
  public static final int NB_BODIES = 11;

  //********* Heavenly bodies *********
  // WARNING : should have value 0 ... NB_BODIES - 1 (used for array purposes).

  /** Constant designating the Sun (value = 0). */
  public static final int SUN = 0;
  /** Constant designating the Moon (value = 1). */
  public static final int MOON = 1;
  /** Constant designating Mercury (value = 2). */
  public static final int MERCURY = 2;
  /** Constant designating Venus (value = 3). */
  public static final int VENUS = 3;
  /** Constant designating the Earth (value = 4). */
  public static final int EARTH = 4;
  /** Constant designating Mars (value = 5). */
  public static final int MARS = 5;
  /** Constant designating Jupiter (value = 6). */
  public static final int JUPITER = 6;
  /** Constant designating Saturn (value = 7). */
  public static final int SATURN = 7;
  /** Constant designating Uranus (value = 8). */
  public static final int URANUS = 8;
  /** Constant designating Neptune (value = 9). */
  public static final int NEPTUNE = 9;
  /** Constant designating Pluto (value = 10). */
  public static final int PLUTO = 10  ;

}//end interface SolarSystemConstants
