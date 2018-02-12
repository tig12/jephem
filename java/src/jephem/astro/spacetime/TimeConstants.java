//*********************************************************************************
// interface jephem.astro.spacetime.TimeConstants
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;

/*********************************************************************************
Interface containing constants related with space.
Related methods can be found in class {@link Time}.

@author Thierry Graff
@history feb 15 2002 : Creation from tig.Time.

@todo : remove the labels from coordGroup comments
@todo see if some constants should go to tig API.
**********************************************************************************/
public interface TimeConstants{

  //=================================================================================
  //                                PUBLIC CONSTANTS
  //=================================================================================

  /** Constant used to characterize that a date is expressed in UTC (Universal Coordinated Time). */
  public static final int UTC = 0;
  /** Constant used to characterize that a date is expressed in TT (Terrestrial Time), which
  is considered as equal to TDB (Temps Dynamique Barycentrique) in JEphem. */
  public static final int TT_TDB = 1;

  /** Number of seconds in a day (24 x 3600 = 86400). */
  public static final double SECONDS_PER_DAY = 86400.0;
  /** Number of days per millenium ( = 365250). */
  public static final double DAYS_PER_MILLENIUM	= 365250.0;
  /** Number of days per century ( = 36525). */
  public static final double DAYS_PER_CENTURY	= 36525.0;
  /** Number of days per year ( = 365.25). */
  public static final double DAYS_PER_YEAR	= 365.25;

  /** Julian date of 01/01/1900, 12h00m00s TU ( = 2415020.0). */
  public static final double JD1900 = 2415020.0;
  /** Julian date of 01/01/2000, 12h00m00s TU ( = 2451545.0). */
  public static final double JD2000 = 2451545.0;
  /** Julian date of 01/01/2100, 12h00m00s TU ( = 2488070.0). */
  public static final double JD2100 = 2488070.0;

}//end interface TimeConstants