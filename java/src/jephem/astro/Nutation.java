//*********************************************************************************
// class jephem.astro.Nutation
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro;

import jephem.astro.spacetime.TimeConstants;
import tig.maths.Maths;

/******************************************************************************
Class to compute nutation quantities <FONT FACE="Symbol">Dy</FONT> and <FONT FACE="Symbol">De</FONT>.
<BR>Implementation of formulae found in BDL's book p 138.
<BR>Coefficients taken from <CODE>nutation.c</CODE>, in XEphem

@author Thierry Graff
@history jan 30 2001 : creation

@todo
*********************************************************************************/
public abstract class Nutation{

  // Constants at the end of class

  //=================================================================================
  //                                 PUBLIC METHODS
  //=================================================================================

  //***************** calcDeltaPsiEpsilon() ***************************************
  /** Calculates the nutation quantities
  <FONT FACE="Symbol">Dy</FONT> and <FONT FACE="Symbol">De</FONT>
  for a given time, in <B>arc seconds</B>.
  @param jd The time to perform the calculations, in julian days.
  @param deltaPsi Variable which takes the resulting value of deltaPsi calculation.
  @param deltaEpsilon Variable which takes the resulting value of deltaEpsilon calculation.
  */
  public static void calcDeltaPsiEpsilon(double date, double deltaPsi, double deltaEpsilon){

    double t, t2, t3, t4;
    t = (date - TimeConstants.JD2000) / TimeConstants.DAYS_PER_CENTURY;
    t2 = t*t; t3 = t2*t; t4 = t3*t;

    // Calculate Delaunay arguments,
    // Expressions found in astron. astroph. 282 p 670, based on IERS 1992 values.
    // omega comes from 3.4, formula b3, other values from 3.5, formulae b.
    // values in arc second, converted to radians.
    double[] delaunay = new double[5];
    // l, moon mean anomaly
    delaunay[0] = (485868.249036 + 1717915923.2178*t + 31.8792*t2 + 0.051635*t3 + 0.00024470*t4)
                * Maths.ARCSEC_TO_RAD;
    // l' sun mean anomaly
    delaunay[1] = (1287104.793048 + 129596581.0481*t - 0.5532*t2 + 0.000136*t3 - 0.00001149*t4)
                * Maths.ARCSEC_TO_RAD;
    // F, moon arg lat
    delaunay[2] = (335779.526232 + 1739527262.84*t - 12.7512*t2 - 0.001037*t3 + 0.00000417*t4)
                * Maths.ARCSEC_TO_RAD;
    // D, elong moon sun
    delaunay[3] = (1072260.73512 + 1602961601.209*t -6.3706*t2 + 0.006593*t3 + 0.00003169*t4)
                * Maths.ARCSEC_TO_RAD;
    // Om, moon asc node
    delaunay[4] = (450160.398036 - 6962890.5431*t + 7.4722*t2 + 0.007702*t3 + 0.00005939*t4)
                * Maths.ARCSEC_TO_RAD;

    deltaPsi = deltaEpsilon = 0;
    double  arg;
    int i, j;
    // perform the summation
    for (i=0; i < NB_TERMS; i++){
      arg = 0;
      for (j=0; j < NB_ARGS; j++)
        arg += arguments[i][j]*delaunay[j];
      deltaPsi += (amplitudes[i][0] + amplitudes[i][1]*t) * Math.sin(arg);
      deltaEpsilon += (amplitudes[i][2] + amplitudes[i][3]*t) * Math.cos(arg);
    }
    // convert to arc seconds
    deltaPsi *= 10000;
    deltaEpsilon *= 10000;
  }// end getDeltaPsiEpsilon

  //=================================================================================
  //                                 CONSTANTS
  //=================================================================================

  private static final int NB_TERMS = 106;
  private static final int NB_ARGS = 5;

/*  static final double delaunay[][] = {
    {1072260.73512, 1602961601.209, -6.3706, 0.006593, 0.00003169} // D, elong moon sun
    {335779.526232, 1739527262.84, -12.7512, -0.001037, 0.00000417} // F, moon arg lat
    {485868.249036, 1717915923.2178, 31.8792, 0.051635, 0.00024470} // l, moon mean anomaly
    {1287104.793048, 129596581.0481, -0.5532, 0.000136, -0.00001149} // l' sun mean anomaly
    {450160.398036, -6962890.5431, 7.4722, 0.007702, 0.00005939} // Om, moon l asc node
  };
*/
  /** Multipliers for Delaunay arguments (l, l', F, D, omega). */
  static final short arguments[][] = {
    {0, 0, 0, 0, 1},
    {0, 0, 0, 0, 2},
    {-2, 0, 2, 0, 1},
    {2, 0, -2, 0, 0},
    {-2, 0, 2, 0, 2},
    {1, -1, 0, -1, 0},
    {0, -2, 2, -2, 1},
    {2, 0, -2, 0, 1},
    {0, 0, 2, -2, 2},
    {0, 1, 0, 0, 0},
    {0, 1, 2, -2, 2},
    {0, -1, 2, -2, 2},
    {0, 0, 2, -2, 1},
    {2, 0, 0, -2, 0},
    {0, 0, 2, -2, 0},
    {0, 2, 0, 0, 0},
    {0, 1, 0, 0, 1},
    {0, 2, 2, -2, 2},
    {0, -1, 0, 0, 1},
    {-2, 0, 0, 2, 1},
    {0, -1, 2, -2, 1},
    {2, 0, 0, -2, 1},
    {0, 1, 2, -2, 1},
    {1, 0, 0, -1, 0},
    {2, 1, 0, -2, 0},
    {0, 0, -2, 2, 1},
    {0, 1, -2, 2, 0},
    {0, 1, 0, 0, 2},
    {-1, 0, 0, 1, 1},
    {0, 1, 2, -2, 0},
    {0, 0, 2, 0, 2},
    {1, 0, 0, 0, 0},
    {0, 0, 2, 0, 1},
    {1, 0, 2, 0, 2},
    {1, 0, 0, -2, 0},
    {-1, 0, 2, 0, 2},
    {0, 0, 0, 2, 0},
    {1, 0, 0, 0, 1},
    {-1, 0, 0, 0, 1},
    {-1, 0, 2, 2, 2},
    {1, 0, 2, 0, 1},
    {0, 0, 2, 2, 2},
    {2, 0, 0, 0, 0},
    {1, 0, 2, -2, 2},
    {2, 0, 2, 0, 2},
    {0, 0, 2, 0, 0},
    {-1, 0, 2, 0, 1},
    {-1, 0, 0, 2, 1},
    {1, 0, 0, -2, 1},
    {-1, 0, 2, 2, 1},
    {1, 1, 0, -2, 0},
    {0, 1, 2, 0, 2},
    {0, -1, 2, 0, 2},
    {1, 0, 2, 2, 2},
    {1, 0, 0, 2, 0},
    {2, 0, 2, -2, 2},
    {0, 0, 0, 2, 1},
    {0, 0, 2, 2, 1},
    {1, 0, 2, -2, 1},
    {0, 0, 0, -2, 1},
    {1, -1, 0, 0, 0},
    {2, 0, 2, 0, 1},
    {0, 1, 0, -2, 0},
    {1, 0, -2, 0, 0},
    {0, 0, 0, 1, 0},
    {1, 1, 0, 0, 0},
    {1, 0, 2, 0, 0},
    {1, -1, 2, 0, 2},
    {-1, -1, 2, 2, 2},
    {-2, 0, 0, 0, 1},
    {3, 0, 2, 0, 2},
    {0, -1, 2, 2, 2},
    {1, 1, 2, 0, 2},
    {-1, 0, 2, -2, 1},
    {2, 0, 0, 0, 1},
    {1, 0, 0, 0, 2},
    {3, 0, 0, 0, 0},
    {0, 0, 2, 1, 2},
    {-1, 0, 0, 0, 2},
    {1, 0, 0, -4, 0},
    {-2, 0, 2, 2, 2},
    {-1, 0, 2, 4, 2},
    {2, 0, 0, -4, 0},
    {1, 1, 2, -2, 2},
    {1, 0, 2, 2, 1},
    {-2, 0, 2, 4, 2},
    {-1, 0, 4, 0, 2},
    {1, -1, 0, -2, 0},
    {2, 0, 2, -2, 1},
    {2, 0, 2, 2, 2},
    {1, 0, 0, 2, 1},
    {0, 0, 4, -2, 2},
    {3, 0, 2, -2, 2},
    {1, 0, 2, -2, 0},
    {0, 1, 2, 0, 1},
    {-1, -1, 0, 2, 1},
    {0, 0, -2, 0, 1},
    {0, 0, 2, -1, 2},
    {0, 1, 0, 2, 0},
    {1, 0, -2, -2, 0},
    {0, -1, 2, 0, 1},
    {1, 1, 0, -2, 1},
    {1, 0, -2, 2, 0},
    {2, 0, 0, 2, 0},
    {0, 0, 2, 4, 2},
    {0, 1, 0, 1, 0}
  }; // end delaunay[][]

  /** Represent terms Ai, A'i, Bi, B'i */
  static final int amplitudes[][] = {
    {-171996, -1742, 92025, 89},
    {2062, 2, -895, 5},
    {46, 0, -24, 0},
    {11, 0, 0, 0},
    {-3, 0, 1, 0},
    {-3, 0, 0, 0},
    {-2, 0, 1, 0},
    {1, 0, 0, 0},
    {-13187, -16, 5736, -31},
    {1426, -34, 54, -1},
    {-517, 12, 224, -6},
    {217, -5, -95, 3},
    {129, 1, -70, 0},
    {48, 0, 1, 0},
    {-22, 0, 0, 0},
    {15, 17, -1, 0, 0},
    {-15, 0, 9, 0},
    {-16, 1, 7, 0},
    {-12, 0, 6, 0},
    {-6, 0, 3, 0},
    {-5, 0, 3, 0},
    {4, 0, -2, 0},
    {4, 0, -2, 0},
    {-4, 0, 0, 0},
    {1, 0, 0, 0},
    {1, 0, 0, 0},
    {-1, 0, 0, 0},
    {1, 0, 0, 0},
    {1, 0, 0, 0},
    {-1, 0, 0, 0},
    {-2274, -2, 977, -5},
    {712, 1, -7, 0},
    {-386, -4, 200, 0},
    {-301, 0, 129, -1},
    {-158, 0, -1, 0},
    {123, 0, -53, 0},
    {63, 0, -2, 0},
    {63, 1, -33, 0},
    {-58, -1, 32, 0},
    {-59, 0, 26, 0},
    {-51, 0, 27, 0},
    {-38, 0, 16, 0},
    {29, 0, -1, 0},
    {29, 0, -12, 0},
    {-31, 0, 13, 0},
    {26, 0, -1, 0},
    {21, 0, -10, 0},
    {16, 0, -8, 0},
    {-13, 0, 7, 0},
    {-10, 0, 5, 0},
    {-7, 0, 0, 0},
    {7, 0, -3, 0},
    {-7, 0, 3, 0},
    {-8, 0, 3, 0},
    {6, 0, 0, 0},
    {6, 0, -3, 0},
    {-6, 0, 3, 0},
    {-7, 0, 3, 0},
    {6, 0, -3, 0},
    {-5, 0, 3, 0},
    {5, 0, 0, 0},
    {-5, 0, 3, 0},
    {-4, 0, 0, 0},
    {4, 0, 0, 0},
    {-4, 0, 0, 0},
    {-3, 0, 0, 0},
    {3, 0, 0, 0},
    {-3, 0, 1, 0},
    {-3, 0, 1, 0},
    {-2, 0, 1, 0},
    {-3, 0, 1, 0},
    {-3, 0, 1, 0},
    {2, 0, -1, 0},
    {-2, 0, 1, 0},
    {2, 0, -1, 0},
    {-2, 0, 1, 0},
    {2, 0, 0, 0},
    {2, 0, -1, 0},
    {1, 0, -1, 0},
    {-1, 0, 0, 0},
    {1, 0, -1, 0},
    {-2, 0, 1, 0},
    {-1, 0, 0, 0},
    {1, 0, -1, 0},
    {-1, 0, 1, 0},
    {-1, 0, 1, 0},
    {1, 0, 0, 0},
    {1, 0, 0, 0},
    {1, 0, -1, 0},
    {-1, 0, 0, 0},
    {-1, 0, 0, 0},
    {1, 0, 0, 0},
    {1, 0, 0, 0},
    {-1, 0, 0, 0},
    {1, 0, 0, 0},
    {1, 0, 0, 0},
    {-1, 0, 0, 0},
    {-1, 0, 0, 0},
    {-1, 0, 0, 0},
    {-1, 0, 0, 0},
    {-1, 0, 0, 0},
    {-1, 0, 0, 0},
    {-1, 0, 0, 0},
    {1, 0, 0, 0},
    {-1, 0, 0, 0},
    {1, 0, 0, 0}
  }; // end amplitudes[][]

}//end class Nutation