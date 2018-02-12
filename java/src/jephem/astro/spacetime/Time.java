//*********************************************************************************
// class jephem.astro.spacetime.Time
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.spacetime;

import jephem.astro.spacetime.TimeConstants;
import tig.GeneralConstants;

/******************************************************************************
Contains static methods used when dealing with time.
<BR>Related constants can be found in {@link TimeConstants}.
@author Thierry Graff
@history feb 14 2002 : creation from tig.Time - dvpt of calcDeltaT.

@todo remove calcDecDay???
@todo handle deltaT for future dates
@todo see if calcDeltaT can be also used with TT
*********************************************************************************/
public abstract class Time implements TimeConstants{

  //=================================================================================
  //                              PRIVATE CONSTANTS
  //=================================================================================

  /** Strange offset in TT / TAI formula (in seconds). */
  private static final double OFFSET = 32.184;

  /** Contains the english names of time frames ; use constants <CODE>UTC</CODE> or
  <CODE>TT_TDB</CODE> of this interface to access to the names. */
  public static final String[] ENGLISH_TIMEFRAME_NAMES = {"UTC", "TT/TDB"};

  //=================================================================================
  //                                PUBLIC STATIC METHODS
  //=================================================================================

  /** Returns the english names of time frames ; use constants <CODE>UTC</CODE> or
  <CODE>TT_TDB</CODE> of {@link TimeConstants} to access to the names. */
  public static String getTimeFrameLabel(int timeFrameIndex){
    return ENGLISH_TIMEFRAME_NAMES[timeFrameIndex];
  }// end getFrameLabel

  //*************************************************
  /** Computes TT from UTC, using the formula <B>TT = UTC + 32.184s + deltaT</B>.
  <BR>UTC stands for "Universal Coordinated Time", which is the basis of legal time.
  <BR>TT stands for "Terrestrial Time", which is considered as equal to TDB, generally used
  in planetary theories.
  @param utc A UTC date expressed in Julian Days.
  @return The date in Terrestrial Time corresponding to 'utc', in julian days.
  */
  public static double getTT(double utc){
    return utc + (OFFSET + calcDeltaT(utc))/SECONDS_PER_DAY;
  } // end getTT

  //*************************************************
  /** Computes UTC from TT, using the formula <B>UTC = TT - 32.184s - deltaT</B>.
  <BR>TT stands for "Terrestrial Time", which is considered as equal to TDB, generally used
  <BR>UTC stands for "Universal Coordinated Time", which is the basis of legal time.
  in planetary theories.
  @param utc A UTC date expressed in Julian Days.
  @return The date in Terrestrial Time corresponding to 'utc', in julian days.
  */
  public static double getUTC(double tt){
    return tt - (OFFSET + calcDeltaT(tt))/SECONDS_PER_DAY;
  } // end getUTC

  //*************************************************
  /** Returns the value <B>deltaT = TAI - UTC</B>, in seconds.
  <BR>UTC stands for "Universal Coordinated Time", which is the basis of legal time.
  <BR>TAI stands for "Temps Atomique International" (Atomic International Time).
  @param utc A UTC date expressed in Julian Days.
  @return The value of delatT, <B>in seconds</B>.
  */
  public static double calcDeltaT(double utc){

    if(utc > 2452640.5) return 33.0; // 2452640.5 = 2003 jan 01 00:00:00
    else if (utc > 2451179.5) return 32.0; // 2451179.5 = 1999 jan 01 00:00:00
    else if (utc > 2450630.5) return 31.0;
    else if (utc > 2450083.5) return 30.0;
    else if (utc > 2449534.5) return 29.0;
    else if (utc > 2449169.5) return 28.0;
    else if (utc > 2448804.5) return 27.0;
    else if (utc > 2448257.5) return 26.0;
    else if (utc > 2447892.5) return 25.0;
    else if (utc > 2447161.5) return 24.0;
    else if (utc > 2446247.5) return 23.0;
    else if (utc > 2445516.5) return 22.0;
    else if (utc > 2445151.5) return 21.0;
    else if (utc > 2444786.5) return 20.0;
    else if (utc > 2444239.5) return 19.0;
    else if (utc > 2443874.5) return 18.0;
    else if (utc > 2443509.5) return 17.0;
    else if (utc > 2443144.5) return 16.0;
    else if (utc > 2442778.5) return 15.0;
    else if (utc > 2442413.5) return 14.0;
    else if (utc > 2442048.5) return 13.0;
    else if (utc > 2441683.5) return 12.0;
    else if (utc > 2441499.5) return 11.0;
    else if (utc > 2441317.5) return 10.0;
    else if (utc > 2439887.5) return 4.2131700  + (calcMjd(utc) - 39126.0) * 0.002592;
    else if (utc > 2439126.5) return 4.3131700  + (calcMjd(utc) - 39126.0) * 0.002592;
    else if (utc > 2439004.5) return 3.8401300  + (calcMjd(utc) - 38761.0) * 0.001296;
    else if (utc > 2438942.5) return 3.7401300  + (calcMjd(utc) - 38761.0) * 0.001296;
    else if (utc > 2438820.5) return 3.6401300  + (calcMjd(utc) - 38761.0) * 0.001296;
    else if (utc > 2438761.5) return 3.5401300  + (calcMjd(utc) - 38761.0) * 0.001296;
    else if (utc > 2438639.5) return 3.4401300  + (calcMjd(utc) - 38761.0) * 0.001296;
    else if (utc > 2438486.5) return 3.3401300  + (calcMjd(utc) - 38761.0) * 0.001296;
    else if (utc > 2438395.5) return 3.2401300  + (calcMjd(utc) - 38761.0) * 0.001296;
    else if (utc > 2438334.5) return 1.9458580  + (calcMjd(utc) - 37665.0) * 0.0011232;
    else if (utc > 2437665.5) return 1.8458580  + (calcMjd(utc) - 37665.0) * 0.0011232;
    else if (utc > 2437512.5) return 1.3728180  + (calcMjd(utc) - 37300.0) * 0.001296;
    else if (utc > 2437300.5) return 1.4228180  + (calcMjd(utc) - 37300.0) * 0.001296; // 2437300.5 = 1961 jan 01 00:00:00
    else if (utc > 2331196.5){ // 2331196.5 = 1670 JUL 01
      double y = (utc - 2382149)/100;
      return -15 + 32.5*y*y; // 2382149 = 1810 jan 01, 12:00:00
    }
    else return 0.0;
  }// end getTai

  /**
   Returns the 'Modified Julian Day', number of Julian Days elapsed since November 17 1858, 00:00:00.
   <BR>MJD = JD - 2400000.5.
   @param jd A date expressed in Julian Days.
   */
    public static double calcMjd(double jd){
      return jd - 2400000.5;
    }// end calcMjd()

  //=================================================================================
  //                                      TESTS
  //=================================================================================

  // **************** For tests only ****************
//  public static void main(String[] args){
//  }// end main

  // **************** For tests only ****************

}// end class Time