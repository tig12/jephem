//*********************************************************************************
// class jephem.astro.MeanTrue
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro;

import jephem.astro.spacetime.TimeConstants;
import tig.maths.Maths;
import tig.maths.Matrix3;

/******************************************************************************
An object of this class holds data permitting to handle conversions between mean and true coordinates.
<BR>This class was written from a BDL theory which can be found at <A HREF="ftp://ftp.bdl.fr/pub/ephem/ref-frames/prcbld94" TARGET="_blank">ftp://ftp.bdl.fr/pub/ephem/ref-frames/prcbld94</A> ; see also "Numerical expressions for precession formulae and mean elements for the Moon and the planets" from Astron. Astrophys. 282, 663 (1994).

@author Thierry Graff
@history jan 19 2002 : creation.

@todo
*********************************************************************************/
public class MeanTrue implements TimeConstants{

  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================
  // Time powers
  private double _date;
  private double t1, t2, t3, t4, t5, t6;
  private double T1, T2, T3, T4, T5;

  /** Precession quantities */
  private static final int NB_QTIES = 14;
  private double[] _qties = new double[NB_QTIES];
  private boolean[] _qtyFlags = new boolean[NB_QTIES]; // false by default

  /** Boolean indicating if the fixed epoch is JD2000 */
  private boolean _fix2000;
  //=================================================================================
  //                                      CONSTANTS
  //=================================================================================

  // Constants designating precession or nutation quantities ;
  // used for array purposes (must be 0, 1 ...).

  /** Constant to access <CODE>sin(<FONT FACE="Symbol">p</FONT><SUB>a</SUB>)sin(<FONT
  FACE="Symbol">P</FONT><SUB>a</SUB>)</CODE> precession quantity (value = 0). */
  public final static int QTY_ss_a = 0;

  /** Constant to access <CODE>sin(<FONT FACE="Symbol">p</FONT><SUB>a</SUB>)cos(<FONT
  FACE="Symbol">P</FONT><SUB>a</SUB>)</CODE> precession quantity (value = 1). */
  public final static int QTY_sc_a = 1;

  /** Constant to access <CODE><FONT FACE="Symbol">p</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 2). */
  public final static int QTY_pi_a = 2;

  /** Constant to access <CODE><FONT FACE="Symbol">P</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 3). */
  public final static int QTY_PI_a = 3;

  /** Constant to access <CODE>P<SUB>a</SUB></CODE> precession quantity (value = 4). */
  public final static int QTY_P_a = 4;

  /** Constant to access <CODE><FONT FACE="Symbol">q</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 5). */
  public final static int QTY_theta_a = 5;

  /** Constant to access <CODE><FONT FACE="Symbol">z</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 6). */
  public final static int QTY_zeta_a = 6;

  /** Constant to access <CODE>z<SUB>a</SUB></CODE> precession quantity (value = 7). */
  public final static int QTY_z_a = 7;

  /** Constant to access <CODE><FONT FACE="Symbol">e</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 8). */
  public final static int QTY_epsilon_a = 8;

  /** Constant to access <CODE><FONT FACE="Symbol">W</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 9). */
  public final static int QTY_omega_a = 9;

  /** Constant to access <CODE><FONT FACE="Symbol">y</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 10). */
  public final static int QTY_psi_a = 10;

  /** Constant to access <CODE><FONT FACE="Symbol">c</FONT><SUB>a</SUB></CODE>
  precession quantity (value = 11). */
  public final static int QTY_chi_a = 11;

  /** Constant to access <CODE><FONT FACE="Symbol">Dy</FONT></CODE>
  nutation quantity (value = 12). */
  public final static int QTY_deltaPsi = 12;

  /** Constant to access <CODE><FONT FACE="Symbol">De</FONT></CODE>
  precession quantity (value = 13). */
  public final static int QTY_deltaEpsilon = 13;

  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================
  /** Unique constructor.
  @param date the date expressed in <B>julian days</B>.
  @param fixedEpoch the fixed epoch expressed in <B>julian days</B>.
  */
  public MeanTrue(double date, double fixedEpoch){
    _date = date;
    t1 = (date - fixedEpoch)/DAYS_PER_MILLENIUM;
    t2 = t1*t1; t3 = t2*t1; t4 = t3*t1; t5 = t4*t1; t6 = t5*t1;
    if (fixedEpoch == JD2000)
      _fix2000 = true;
    else{
      _fix2000 = false;
      T1 = (fixedEpoch - JD2000)/DAYS_PER_MILLENIUM;
      T2 = T1*T1; T3 = T2*T1; T4 = T3*T1; T5 = T4*T1;
    }
  }// end MeanTrue(double, double)

  //=================================================================================
  //                                 PUBLIC METHODS
  //=================================================================================

  //******************************* getEqPrecessionMatrix() ******************************
  /** Returns the precession matrix permitting to transform from <B>mean equatorial coordinates
  of the fixedEpoch</B> to <B>mean equatorial coordinates of the date</B>.
  */
  public Matrix3 getEqPrecessionMatrix(){
    double theta_a = this.getQuantity(QTY_theta_a) * Maths.ARCSEC_TO_RAD;
    double zeta_a = this.getQuantity(QTY_zeta_a) * Maths.ARCSEC_TO_RAD;
    double z_a = this.getQuantity(QTY_z_a) * Maths.ARCSEC_TO_RAD;

    // intermediate variables
    double cZZeta = Math.cos(z_a + zeta_a);
    double sZZeta = Math.sin(z_a + zeta_a);
    double cZ = Math.cos(z_a);
    double sZ = Math.sin(z_a);
    double cZeta = Math.cos(zeta_a);
    double sZeta = Math.sin(zeta_a);
    double cTheta = Math.cos(theta_a);
    double sTheta = Math.sin(theta_a);
    double s2Theta2 = Math.sin(theta_a/2) * Math.sin(theta_a/2);

    // returned matrix.
    return new Matrix3(cZZeta - 2*s2Theta2*cZ*cZeta,
                        -sZZeta + 2*s2Theta2*cZ*sZeta,
                        -cZ*sTheta,
                        sZZeta - 2*s2Theta2*sZ*cZeta,
                        cZZeta + 2*s2Theta2*sZ*sZeta,
                        -sZ*sTheta,
                        cZeta*sTheta,
                        -sZeta*sTheta,
                        cTheta);
  }// end getEqPrecessionMatrix()

  //******************************* getEqNutationMatrix() ******************************
  /** Returns the nutation matrix permitting to transform from <B>mean equatorial coordinates
  of a date</B> to <B>true equatorial coordinates of a date</B>.
  */
  public Matrix3 getEqNutationMatrix(){
    double deltaPsi     = this.getQuantity(QTY_deltaPsi) * Maths.ARCSEC_TO_RAD;
    double deltaEpsilon = this.getQuantity(QTY_deltaEpsilon) * Maths.ARCSEC_TO_RAD;
    double epsilon_a    = this.getQuantity(QTY_epsilon_a) * Maths.ARCSEC_TO_RAD;
    double epsilonP_a = epsilon_a + deltaEpsilon; // epsilonP means epsilon_a'

    // intermediate variables
    double se = Math.sin(epsilon_a);
    double ce = Math.cos(epsilon_a);
    double sep = Math.sin(epsilonP_a);
    double cep = Math.cos(epsilonP_a);
    double sde = Math.sin(deltaEpsilon);
    double cde = Math.cos(deltaEpsilon);
    double sdp = Math.sin(deltaPsi);
    double cdp = Math.cos(deltaPsi);
    double s2dp2 = Math.sin(deltaPsi/2) * Math.sin(deltaPsi/2);

    // returned matrix.
    return new Matrix3(cdp,
                        -sdp*ce,
                        -sdp*se,
                        cep*sdp,
                        cde - 2*s2dp2*ce*cep,
                        -sde - 2*s2dp2*se*cep,
                        sep*sdp,
                        sde - 2*s2dp2*ce*sep,
                        cde - 2*s2dp2*se*sep);
  }// end getEqNutationMatrix

  //******************************* getTrueEqToEcMatrix() ******************************
  /** Returns the nutation matrix permitting to transform from <B>mean equatorial coordinates
  of a date</B> to <B>true equatorial coordinates of a date</B>.
  */
  public Matrix3 getTrueEqToEcMatrix(){
    double deltaPsi = getQuantity(QTY_deltaPsi);
    double deltaEpsilon = getQuantity(QTY_deltaEpsilon);
    double epsilon_a = getQuantity(QTY_epsilon_a);
    double epsilonP_a = epsilon_a + deltaEpsilon; // epsilonP means epsilon_a'
    // intermediate variables
    double cep = Math.cos(epsilonP_a);
    double sep = Math.sin(epsilonP_a);

    // returned matrix.
    return new Matrix3(1, 0, 0,
                        0, cep, sep,
                        0, -sep, cep);
  }// end getTrueEqToEcMatrix()

  /** Returns a quantity (precession or nutation), expressed in <B>arc seconds</B>.
  <BR>The precession quantities are computed using the 1992 IERS planetary masses.
  @param index index used to specify which quantity to return ;
  use the <CODE>QTY_xxx</CODE> constants of this class to express 'index'
  */
  public double getQuantity(int index){

    int indexMax = NB_QTIES -1;
    if (index < 0 || index > indexMax)
      throw new IllegalArgumentException("'index' must be between 0 and " + indexMax);

    // Test if the quantity has already been calculated
    if (_qtyFlags[index]) return _qties[index];

    switch(index){
      // **************************************************************************
      // Precession quantities returned in arc seconds
      // **************************************************************************
      case QTY_ss_a:
        if (_fix2000){
          _qties[QTY_ss_a] = 41.9971*t1 + 19.3971*t2 - 0.2235*t3 - 0.0104*t4 + 0.0002*t5
          // corrections to fit to 1992 IERS planetary masses
          - 0.0010*t1;
        }
        else{
          _qties[QTY_ss_a] =
            (+41.9971 - 75.3286*T1 + 0.3179*T2 + 0.3178*T3 + 0.0007*T4 - 0.0004*T5)*t1
          + (+19.3971 + 0.5740*T1 - 0.2541*T2 - 0.0005*T3 + 0.0006*T4)*t2
          + (-0.2235 + 0.0859*T1 + 0.0033*T2 - 0.0003*T3)*t3
          + (-0.0104 - 0.0004*T1 + 0.0002*T2)*t4
          + (+0.0002)*t5
          // corrections to fit to 1992 IERS planetary masses
          + (-0.0010 - 0.0008*T1)*t1;
        }
        _qtyFlags[QTY_ss_a] = true;
      break;
      // *************************************
      case QTY_sc_a:
        if (_fix2000){
          _qties[QTY_sc_a] = - 468.0927*t1 + 5.1043*t2 + 0.5223*t3 - 0.0057*t4 - 0.0001*t5
          // corrections to fit to 1992 IERS planetary masses
          - 0.0029*t1;
        }
        else{
          _qties[QTY_sc_a] =
            (-468.0927 - 0.0305*T1  + 5.9967*T2 - 0.0205*T3 - 0.0125*T4 - 0.0002*T5)*t1
          + (+5.1043 - 3.1633*T1 - 0.0326*T2 + 0.0138*T3 - 0.0002*T4)*t2
          + (+0.5223 + 0.0318*T1 - 0.0066*T2 - 0.0004*T3)*t3
          + (-0.0057 + 0.0019*T1 - 0.0001*T2)*t4
          + (-0.0001)*t5
          // corrections to fit to 1992 IERS planetary masses
          + (-0.0029 + 0.0002*T1)*t1;
        }
        _qtyFlags[QTY_sc_a] = true;
      break;
      // *************************************
      case QTY_pi_a:
        if (_fix2000){
          _qties[QTY_pi_a] = 469.9729*t1 - 3.3505*t2 - 0.1237*t3 + 0.0003*t4
          // corrections to fit to 1992 IERS planetary masses
          + 0.0027*t1;
        }
        else{
          _qties[QTY_pi_a] =
            (+469.9729 -6.7011*T1 + 0.0448*T2 - 0.0019*T3 - 0.0001*T4)*t1
          + (-3.3505 + 0.0448*T1 - 0.0028*T2 - 0.0002*T3 + 0.0001*T4)*t2
          + (-0.1237 - 0.0004*T1 - 0.0002*T2 + 0.0001*T3)*t3
          + (+0.0003 - 0.0001*T1 + 0.0001*T2)*t4
          // corrections to fit to 1992 IERS planetary masses
          + 0.0027*t1;
        }
        _qtyFlags[QTY_pi_a] = true;
      break;
      // *************************************
      case QTY_PI_a:
        if (_fix2000){
          _qties[QTY_PI_a] = + 629543.433 - 8679.270*t1 + 15.342*t2 + 0.005*t3 - 0.037*t4 - 0.001*t5
          // corrections to fit to 1992 IERS planetary masses
          + 0.555 + 0.052*t1;
        }
        else{
          _qties[QTY_PI_a] =
            (+629543.433 + 32929.659*T1 + 95.352*T2 - 0.005*T3 - 0.459*T4 - 0.010*T5)
          + (-8679.270 - 15.851*T1 - 0.113*T2 - 0.448*T3 - 0.019*T4)*t1
          + (+15.342 - 0.019 *T1 - 0.432 *T2 - 0.023*T3)*t2
          + (+0.005 -0.208*T1 - 0.015*T2)*t3
          + (-0.037 - 0.005*T1)*t4
          + (-0.001)*t5
          // corrections to fit to 1992 IERS planetary masses
          + 0.555 + 0.104*T1 + 0.052*t1;
        }
        _qtyFlags[QTY_PI_a] = true;
      break;
      // *************************************
      case QTY_P_a:
        if (_fix2000){
          _qties[QTY_P_a] =
          50288.200*t1 + 111.2022*t2 + 0.0773*t3 - 0.2353*t4 - 0.0018*t5 + 0.0002*t6
          // corrections to fit to 1992 IERS planetary masses
          + 0.0011*t2;
        }
        else{
          _qties[QTY_P_a] =
            (50288.200 + 222.4045*T1 + 0.2095*T2 - 0.9408*T3 - 0.0090*T4 + 0.0010*T5)*t1
          + (+111.2022 + 0.2095*T1 - 1.4111*T2 - 0.0180*T3 + 0.0026*T4)*t2
          + (+0.0773 - 0.9410*T1 - 0.0180*T2 + 0.0035*T3)*t3
          + (-0.2353 - 0.0090*T1 + 0.0026*T2)*t4
          + (-0.0018 + 0.0010*T1)*t5
          + (+0.0002)*t6
          // corrections to fit to 1992 IERS planetary masses
          + 0.0019*t1*T1 + 0.0011*t2;
        }
        _qtyFlags[QTY_P_a] = true;
      break;
      // *************************************
      case QTY_theta_a:
        if (_fix2000){
          _qties[QTY_theta_a] =
          20042.0207*t1 - 42.6566*t2 - 41.8238*t3 - 0.0731*t4 - 0.0127*t5 + 0.0004*t6
          // corrections to fit to 1992 IERS planetary masses
          - 0.0009*t1 - 0.0002*t2;
        }
        else{
          _qties[QTY_theta_a] =
            (+20042.0207 - 85.3131*T1  -0.2111*T2 +0.3642*T3 +0.0008*T4 -0.0005*T5)*t1
          + (-42.6566 - 0.2111*T1 + 0.5463*T2 + 0.0017*T3 - 0.0012*T4)*t2
          + (-41.8238 + 0.0359*T1 + 0.0027*T2 - 0.0001*T3)*t3
          + (-0.0731 + 0.0019*T1 + 0.0009*T2)*t4
          + (-0.0127 + 0.0011*T1)*t5
          + (+0.0004)*t6
          // corrections to fit to 1992 IERS planetary masses
          + (-0.0009 - 0.0006*T1)*t1 - 0.0002*t2;
        }
        _qtyFlags[QTY_theta_a] = true;
      break;
      // *************************************
      case QTY_zeta_a:
        if (_fix2000){
          _qties[QTY_zeta_a] =
          23060.9097*t1 + 30.2226*t2 + 18.0183*t3 - 0.0583*t4 - 0.0285*t5 - 0.0002*t6
          // corrections to fit to 1992 IERS planetary masses
          + 0.0002*t1 + 0.0002*t2;
        }
        else{
          _qties[QTY_zeta_a] =
            (+23060.9097 + 139.7495*T1  - 0.0038*T2 - 0.5918*T3 - 0.0037*T4 + 0.0007*T5)*t1
          + (+30.2226 - 0.2523*T1 - 0.3840*T2 - 0.0014*T3 + 0.0007*T4)*t2
          + (+18.0183 - 0.1326*T1 + 0.0006*T2 + 0.0005*T3)*t3
          + (-0.0583 - 0.0001*T1 + 0.0007*T2)*t4
          + (-0.0285)*t5
          + (-0.0002)*t6
          // corrections to fit to 1992 IERS planetary masses
          + (0.0002 + 0.0013*T1)*t1 + 0.0002*t2;
        }
        _qtyFlags[QTY_zeta_a] = true;
      break;
      // *************************************
      case QTY_z_a:
        if (_fix2000){
          _qties[QTY_z_a] =
          23060.9097*t1 + 109.5270*t2 + 18.2667*t3 - 0.2821*t4 - 0.0301*t5 - 0.0001*t6
          // corrections to fit to 1992 IERS planetary masses
          + 0.0002*t1 + 0.0010*t2;
        }
        else{
          _qties[QTY_z_a] =
            (+23060.9097 + 139.7495*T1 - 0.0038*T2 - 0.5918*T3 - 0.0037*T4 + 0.0007*T5)*t1
          + (+109.5270 + 0.2446*T1  -1.3913*T2 -0.0134*T3 +0.0026*T4)*t2
          + (+18.2667     -1.1400*T1  -0.0173*T2 +0.0044*T3)*t3
          + (-0.2821     -0.0093*T1  +0.0032*T2)*t4
          + (-0.0301     +0.0006*T1)*t5
          + (-0.0001)*t6
          // corrections to fit to 1992 IERS planetary masses
          + (0.0002 + 0.0013*T1)*t1 + 0.0010*t2;
        }
        _qtyFlags[QTY_z_a] = true;
      break;
      // *************************************
      case QTY_epsilon_a:
        if (_fix2000){
          _qties[QTY_epsilon_a] =
          84381.412 - 468.0927*t1 - 0.0152*t2 + 1.9989*t3 - 0.0051*t4 - 0.0025*t5
          // corrections to fit to 1992 IERS planetary masses
          - 0.0029*t1;
        }
        else{
          _qties[QTY_epsilon_a] =
            (+84381.412 - 468.0927*T1  - 0.0152*T2 + 1.9989*T3 - 0.0051*T4 - 0.0025*T5)
          + (-468.0927 - 0.0305*T1 + 5.9967*T2 - 0.0205*T3 - 0.0125*T4 - 0.0002*T5) *t1
          + (-0.0152 + 5.9967*T1 - 0.0308*T2 - 0.0250*T3 - 0.0006*T4) *t2
          + (+1.9989 - 0.0205*T1 - 0.0250*T2 - 0.0008*T3) *t3
          + (-0.0051 - 0.0125*T1 - 0.0006*T2) *t4
          + (-0.0025 - 0.0002*T1) *t5
          // corrections to fit to 1992 IERS planetary masses
          - 0.0029*T1 + (-0.0029 + 0.0002*T1)*t1;
        }
        _qtyFlags[QTY_epsilon_a] = true;
      break;
      // *************************************
      case QTY_omega_a:
        if (_fix2000){
          _qties[QTY_omega_a] =
            84381.412 + 5.1294*t2 - 7.7276*t3 - 0.0048*t4 + 0.0333*t5 - 0.0003*t6;
          // no corrections to fit to 1992 IERS planetary masses
        }
        else{
          _qties[QTY_omega_a] =
            (84381.412 - 468.0927*T1 - 0.0152*T2 + 1.9989*T3 - 0.0051*T4 - 0.0025*T5)
          + (+5.1294 - 9.1954*T1 + 0.0298*T2 + 0.0389*T3 + 0.0002*T4)*t2
          + (-7.7276 + 0.0235*T1 + 0.0987*T2 - 0.0001*T3)*t3
          + (-0.0048 + 0.0954*T1 - 0.0007*T2)*t4
          + (+0.0333 - 0.0009*T1)*t5
          + (-0.0003)*t6
          // corrections to fit to 1992 IERS planetary masses
          - 0.0029*T1;
        }
        _qtyFlags[QTY_omega_a] = true;
      break;
      // *************************************
      case QTY_psi_a:
        if (_fix2000){
          _qties[QTY_psi_a] =
            50385.0672*t1 - 107.2374*t2 - 1.1424*t3 + 1.3279*t4 - 0.0094*t5 - 0.0035*t6
          // corrections to fit to 1992 IERS planetary masses
          - 0.0023*t1 - 0.0007*t2;
        }
        else{
          _qties[QTY_psi_a] =
            (+50385.0672 + 49.2595*T1 - 0.1344*T2 - 0.2115*T3 + 0.0017*T4 + 0.0003*T5)*t1
          + (-107.2374 - 1.0919*T1 + 1.3673*T2 + 0.0137*T3 - 0.0028*T4)*t2
          + (-1.1424 + 2.6425*T1 + 0.0087*T2 - 0.0111*T3)*t3
          + (+1.3279 - 0.0110*T1 - 0.0170*T2)*t4
          + (-0.0094 - 0.0123*T1)*t5
          + (-0.0035)*t6
          // corrections to fit to 1992 IERS planetary masses
          + (-0.0023 + 0.0002*T1)*t1 - 0.0007*t2;
        }
        _qtyFlags[QTY_psi_a] = true;
      break;
      // *************************************
      case QTY_chi_a:
        if (_fix2000){
          _qties[QTY_chi_a] =
            105.5794*t1 - 238.1379*t2 - 1.2117*t3 + 1.7024*t4 - 0.0077*t5 - 0.0040*t6
          // corrections to fit to 1992 IERS planetary masses
          - 0.0025*t1 - 0.0017*t2;
        }
        else{
          _qties[QTY_chi_a] =
            (+105.5794 - 188.8214*T1 - 0.1888*T2 + 0.7950*T3 + 0.0101*T4 - 0.0009*T5)*t1
          + (-238.1379 - 1.0910*T1 + 3.0291*T2 + 0.0290*T3 - 0.0059*T4)*t2
          + (-1.2117 + 3.9055*T1 + 0.0229*T2 - 0.0159*T3)*t3
          + (+1.7024 - 0.0038*T1 -0.0214*T2)*t4
          + (-0.0077 - 0.0145*T1)*t5
          + (-0.0040)*t6
          // corrections to fit to 1992 IERS planetary masses
          + (-0.0025 - 0.0017*T1)*t1 - 0.0017*t2;
        }
        _qtyFlags[QTY_chi_a] = true;
      break;
      // **************************************************************************
      // Nutation quantities returned in arc seconds
      // **************************************************************************
      case QTY_deltaPsi:
      case QTY_deltaEpsilon:
        // deltaPsi and deltaEpsilon are always computed together
        // so _qtyFlags[QTY_deltaEpsilon] is not used.
        double deltaPsi = 0, deltaEpsilon = 0;
        Nutation.calcDeltaPsiEpsilon(_date, deltaPsi, deltaEpsilon);
        _qties[QTY_deltaPsi] = deltaPsi;
        _qties[QTY_deltaEpsilon] = deltaEpsilon;
        _qtyFlags[QTY_deltaPsi] = true;
        _qtyFlags[QTY_deltaEpsilon] = true;
      break;
    }// end switch

    return _qties[index];

  }// end calcQuantity

  //=================================================================================
  //=================================================================================
  //                                 TESTS
  //=================================================================================
  //=================================================================================
/*
  // **************** For tests only ****************
  public static void main(String[] args){
    // no complete argument checking
    if(args[0].equalsIgnoreCase("displayQties"))
      displayQties(args[1], args[2]);
    else if(args[0].equalsIgnoreCase("testPerf"))
      testPerf(args[1]);
    else
      System.out.println("first argument must be 'displayQties' or 'testPerf'");
  }// end main

  // **************** For tests only ****************
  private static void displayQties(String strDate, String strFixed){
    double date = Double.parseDouble(strDate); //2351545.0;
    double fixedEpoch = Double.parseDouble(strFixed);
    // for fixed epoch : 2451545.0 == JD2000;  2251545.0 = test value
    String[] varNames = {"sin(pi_a)*sin(PI_a)", "sin(pi_a)*cos(PI_a)", "pi_a",
                         "PI_a",  "P_a", "theta_a", "zeta_a", "z_a", "epsilon_a",
                         "omega_a", "psi_a", "chi_a"};
    MeanTrue mt = new MeanTrue(date, fixedEpoch);
    System.out.println("t = " + mt.t1);
    System.out.println("T = " + mt.T1);
    for (int i = 0; i < 12; i++){
      System.out.println(varNames[i] + " = " + mt.getQuantity(i));
    }
  }// end displayQties

  // **************** For tests only ****************
  // The purpose of this method is to test if implementation of short formulae (when T = 0) is worth.
  private static void testPerf(String strNbTests){
    int NB_TESTS = Integer.parseInt(strNbTests);
    int STEP = 1; // interval of tests (in days)
    double baseDate = 2351545.0; // any arbitrary date
    double curDate = baseDate;
    double fixedEpoch1 = Time.JD2000;
    double fixedEpoch2 = 2251545.0;

    // test with JD2000
    java.util.Date startDate1 = new java.util.Date();
    for (int i = 0; i < NB_TESTS; i++){
      curDate += STEP;
      MeanTrue mt = new MeanTrue(curDate, fixedEpoch1);
      mt.getQuantity(QTY_epsilon_a);
    }
    java.util.Date endDate1 = new java.util.Date();

    // test with other date
    java.util.Date startDate2 = new java.util.Date();
    for (int i = 0; i < NB_TESTS; i++){
      curDate += STEP;
      MeanTrue mt = new MeanTrue(curDate, fixedEpoch2);
      mt.getQuantity(QTY_epsilon_a);
    }
    java.util.Date endDate2 = new java.util.Date();

    // Display results
    long d1 = endDate1.getTime() - startDate1.getTime();
    long d2 = endDate2.getTime() - startDate2.getTime();
    System.out.println("With JD2000 : " + d1);
    System.out.println("With " + fixedEpoch2 + " : " + d2);
    long gain = 100 - (d2 * 100 / d1);
    System.out.println("gain : " + gain + " %");

  }// end testPerf
*/

/*  From prctable.doc :
 For the date :        JD2351545.0 (3-18-1726 12h)
 and the fixed epoch : JD2251545.0 (5-24-1452 12h),

 we have the time parameters :
 T = (2251545.0-2451545.0)/365250 = -0.54757016
 t = (2351545.0-2251545.0)/365250 = +0.27378508

 and the precession quantities :
 sin(pi_a)*sin(PI_a) =      24.2221 "
 sin(pi_a)*cos(PI_a) =    -127.1375 "
 pi_a =                    129.4244 "
 PI_a =                 609167.960  "
 p_a =                   13743.1821 "
 theta_a =                5495.9267 "
 zeta_a =                 6295.4465 "
 z_a =                    6301.3632 "
 epsilon_a =             84509.5266 "
 omega_a =               84637.9961 "
 psi_a =                 13779.2847 "
 chi_a =                    39.3656 "
*/
}// end class Precession
