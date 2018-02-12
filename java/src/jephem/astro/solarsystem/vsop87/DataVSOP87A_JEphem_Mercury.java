//********************************************************************
// class jephem.astro.solarsystem.vsop87.DataVSOP87A_JEphem_Mercury
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//********************************************************************
package jephem.astro.solarsystem.vsop87;

/********************************************************************
Data for calculation of Mercury coordinates using VSOP87 theory (version A).
********************************************************************/
class DataVSOP87A_JEphem_Mercury{

  /** Array indicating the number of terms for a coordinate and a power of time.
  <BR><CODE>nbTerms[i][j]</CODE> = number of terms for coordinate i (X, Y or Z),
  for t<SUP>j</SUP>.
  */
  protected static final int nbTerms[][] = {
    {25, 11, 8, 7, 1, 0},
    {26, 11, 8, 7, 2, 0},
    {10, 7, 6, 4, 2, 0}
  }; // end nbTerms[][]

  /** Array containing the terms for the summation.
  <BR><CODE>data[n][0]</CODE> represents term A.
  <BR><CODE>data[n][1]</CODE> represents term B.
  <BR><CODE>data[n][2]</CODE> represents term C.
  */
  protected static final double data[][] = {
    // Mercury X, T**0
    { 0.37546291728, 4.39651506942, 26087.90314157420 },
    { 0.03825746672, 1.16485604339, 52175.80628314840 },
    { 0.02625615963, 3.14159265359, 0.00000000000 },
    { 0.00584261333, 4.21599394757, 78263.70942472259 },
    { 0.00105716695, 0.98379033182, 104351.61256629678 },
    { 0.00021011730, 4.03469353923, 130439.51570787099 },
    { 0.00004433373, 0.80236674527, 156527.41884944518 },
    { 0.00000974967, 3.85319674536, 182615.32199101939 },
    { 0.00000700327, 4.45478725367, 24978.52458948080 },
    { 0.00000626468, 1.18563492001, 27197.28169366760 },
    { 0.00000446989, 2.97507181503, 1059.38193018920 },
    { 0.00000398401, 1.86487895049, 20426.57109242200 },
    { 0.00000277216, 3.77909548342, 31749.23519072640 },
    { 0.00000190657, 4.27201801941, 53285.18483524180 },
    { 0.00000181790, 4.94857138217, 1109.37855209340 },
    { 0.00000194418, 0.67806013045, 4551.95349705880 },
    { 0.00000221028, 0.62082250658, 208703.22513259358 },
    { 0.00000190713, 1.17385212686, 5661.33204915220 },
    { 0.00000138492, 1.22446421973, 51066.42773105500 },
    { 0.00000151693, 2.67604566886, 51116.42435295920 },
    { 0.00000095481, 0.16753795386, 529.69096509460 },
    { 0.00000114338, 0.56002737806, 57837.13833230060 },
    { 0.00000075179, 6.06954012703, 27147.28507176339 },
    { 0.00000074528, 4.92454709213, 46514.47423399620 },
    { 0.00000083764, 3.18666883018, 10213.28554621100 },
    // Originally 1449 terms, 25 terms retained, 1424 terms dropped.

    // Mercury X, T**1
    { 0.00318848034, 0.00000000000, 0.00000000000 },
    { 0.00105289019, 5.91600475006, 52175.80628314840 },
    { 0.00032316001, 2.68247273347, 78263.70942472259 },
    { 0.00011992889, 5.81575112963, 26087.90314157420 },
    { 0.00008783200, 5.73285747425, 104351.61256629678 },
    { 0.00002329042, 2.50023793407, 130439.51570787099 },
    { 0.00000614473, 5.55087602844, 156527.41884944518 },
    { 0.00000162192, 2.31836529248, 182615.32199101939 },
    { 0.00000069028, 2.91494938058, 24978.52458948080 },
    { 0.00000062918, 2.73196853262, 27197.28169366760 },
    { 0.00000042904, 5.36906063918, 208703.22513259358 },
    // Originally 792 terms, 11 terms retained, 781 terms dropped.

    // Mercury X, T**2
    { 0.00001484185, 4.35401210269, 52175.80628314840 },
    { 0.00000907467, 1.13216343018, 78263.70942472259 },
    { 0.00001214995, 0.00000000000, 0.00000000000 },
    { 0.00000368809, 4.18705944126, 104351.61256629678 },
    { 0.00000254306, 4.12817377140, 26087.90314157420 },
    { 0.00000130149, 0.95681684789, 130439.51570787099 },
    { 0.00000042868, 4.00892196726, 156527.41884944518 },
    { 0.00000013566, 0.77740665693, 182615.32199101939 },
    // Originally 299 terms, 8 terms retained, 291 terms dropped.

    // Mercury X, T**3
    { 0.00000046252, 3.14159265359, 0.00000000000 },
    { 0.00000017461, 5.90570575226, 78263.70942472259 },
    { 0.00000014855, 2.98081270061, 52175.80628314840 },
    { 0.00000010499, 2.65555597352, 104351.61256629678 },
    { 0.00000006474, 3.10442611992, 26087.90314157420 },
    { 0.00000004906, 5.70235297811, 130439.51570787099 },
    { 0.00000002012, 2.46917870918, 156527.41884944518 },
    // Originally 54 terms, 7 terms retained, 47 terms dropped.

    // Mercury X, T**4
    { 0.00000000396, 3.14159265359, 0.00000000000 },
    // Originally 15 terms, 1 terms retained, 14 terms dropped.

    // Mercury X, T**5
    // Originally 10 terms, 0 terms retained, 10 terms dropped.

    // Mercury Y, T**0
    { 0.37953642888, 2.83780617820, 26087.90314157420 },
    { 0.11626131831, 3.14159265359, 0.00000000000 },
    { 0.03854668215, 5.88780608966, 52175.80628314840 },
    { 0.00587711268, 2.65498896201, 78263.70942472259 },
    { 0.00106235493, 5.70550616735, 104351.61256629678 },
    { 0.00021100828, 2.47291315849, 130439.51570787099 },
    { 0.00004450056, 5.52354907071, 156527.41884944518 },
    { 0.00000978286, 2.29102643026, 182615.32199101939 },
    { 0.00000707500, 2.89516591531, 24978.52458948080 },
    { 0.00000654742, 5.92892123881, 27197.28169366760 },
    { 0.00000448561, 1.40595042211, 1059.38193018920 },
    { 0.00000402168, 0.30317998006, 20426.57109242200 },
    { 0.00000290604, 2.23645868392, 31749.23519072640 },
    { 0.00000191358, 2.70792842547, 53285.18483524180 },
    { 0.00000181119, 0.23941291054, 1109.37855209340 },
    { 0.00000193372, 5.38698781997, 4551.95349705880 },
    { 0.00000221718, 5.34170676570, 208703.22513259358 },
    { 0.00000139514, 5.94698662319, 51066.42773105500 },
    { 0.00000154924, 1.12201865761, 51116.42435295920 },
    { 0.00000177242, 2.78855813429, 5661.33204915220 },
    { 0.00000116072, 5.28608170116, 57837.13833230060 },
    { 0.00000081399, 3.50862797958, 21535.94964451540 },
    { 0.00000086595, 5.06999843254, 529.69096509460 },
    { 0.00000075457, 4.50396814445, 27147.28507176339 },
    { 0.00000075078, 3.36291170975, 46514.47423399620 },
    { 0.00000083250, 1.60127885818, 10213.28554621100 },
    // Originally 1438 terms, 26 terms retained, 1412 terms dropped.

    // Mercury Y, T**1
    { 0.00107803852, 4.34964793883, 52175.80628314840 },
    { 0.00080651544, 3.14159265359, 0.00000000000 },
    { 0.00032715354, 1.11763734425, 78263.70942472259 },
    { 0.00008858158, 4.16852401867, 104351.61256629678 },
    { 0.00011914709, 1.22139986340, 26087.90314157420 },
    { 0.00002344469, 0.93615372641, 130439.51570787099 },
    { 0.00000617838, 3.98693992284, 156527.41884944518 },
    { 0.00000162955, 0.75452718043, 182615.32199101939 },
    { 0.00000070135, 1.35447664024, 24978.52458948080 },
    { 0.00000063991, 1.18000294070, 27197.28169366760 },
    { 0.00000043082, 3.80528844384, 208703.22513259358 },
    // Originally 782 terms, 11 terms retained, 771 terms dropped.

    // Mercury Y, T**2
    { 0.00004612157, 0.00000000000, 0.00000000000 },
    { 0.00001575670, 2.81172733349, 52175.80628314840 },
    { 0.00000927896, 5.85368769122, 78263.70942472259 },
    { 0.00000670255, 0.90964509090, 26087.90314157420 },
    { 0.00000373744, 2.62279275699, 104351.61256629678 },
    { 0.00000131389, 5.67519052208, 130439.51570787099 },
    { 0.00000043188, 2.44402631830, 156527.41884944518 },
    { 0.00000013650, 5.49573569359, 182615.32199101939 },
    // Originally 299 terms, 8 terms retained, 291 terms dropped.

    // Mercury Y, T**3
    { 0.00000018231, 4.35141183918, 78263.70942472259 },
    { 0.00000017840, 1.45419068020, 52175.80628314840 },
    { 0.00000015722, 0.00000000000, 0.00000000000 },
    { 0.00000010723, 1.09353490107, 104351.61256629678 },
    { 0.00000008835, 4.62739214222, 26087.90314157420 },
    { 0.00000004973, 4.13805648872, 130439.51570787099 },
    { 0.00000002032, 0.90409186617, 156527.41884944518 },
    // Originally 59 terms, 7 terms retained, 52 terms dropped.

    // Mercury Y, T**4
    { 0.00000000448, 3.14159265359, 0.00000000000 },
    { 0.00000000299, 2.91725329579, 78263.70942472259 },
    // Originally 15 terms, 2 terms retained, 13 terms dropped.

    // Mercury Y, T**5
    // Originally 10 terms, 0 terms retained, 10 terms dropped.

    // Mercury Z, T**0
    { 0.04607665326, 1.99295081967, 26087.90314157420 },
    { 0.00708734365, 3.14159265359, 0.00000000000 },
    { 0.00469171617, 5.04215742764, 52175.80628314840 },
    { 0.00071626395, 1.80894256071, 78263.70942472259 },
    { 0.00012957446, 4.85922032010, 104351.61256629678 },
    { 0.00002575002, 1.62646731545, 130439.51570787099 },
    { 0.00000543259, 4.67698860167, 156527.41884944518 },
    { 0.00000119462, 1.44437994097, 182615.32199101939 },
    { 0.00000079477, 4.94442849343, 27197.28169366760 },
    { 0.00000082635, 2.03127961938, 24978.52458948080 },
    // Originally 598 terms, 10 terms retained, 588 terms dropped.

    // Mercury Z, T**1
    { 0.00108722177, 3.91134750825, 26087.90314157420 },
    { 0.00057826621, 3.14159265359, 0.00000000000 },
    { 0.00004297352, 2.56373047177, 52175.80628314840 },
    { 0.00002435833, 0.05112640506, 78263.70942472259 },
    { 0.00000795699, 3.20041081922, 104351.61256629678 },
    { 0.00000229251, 0.00558431110, 130439.51570787099 },
    { 0.00000063404, 3.07612843684, 156527.41884944518 },
    // Originally 351 terms, 7 terms retained, 344 terms dropped.

    // Mercury Z, T**2
    { 0.00001053118, 5.37979214357, 26087.90314157420 },
    { 0.00001185024, 0.00000000000, 0.00000000000 },
    { 0.00000087113, 0.42206932430, 52175.80628314840 },
    { 0.00000049534, 4.38054039769, 78263.70942472259 },
    { 0.00000025685, 1.45827443162, 104351.61256629678 },
    { 0.00000010457, 4.62224691737, 130439.51570787099 },
    // Originally 143 terms, 6 terms retained, 137 terms dropped.

    // Mercury Z, T**3
    { 0.00000021392, 2.12804278460, 26087.90314157420 },
    { 0.00000017872, 0.00000000000, 0.00000000000 },
    { 0.00000002919, 5.41085836184, 52175.80628314840 },
    { 0.00000001164, 2.43324966912, 78263.70942472259 },
    // Originally 28 terms, 4 terms retained, 24 terms dropped.

    // Mercury Z, T**4
    { 0.00000000587, 4.86442699315, 26087.90314157420 },
    { 0.00000000283, 3.14159265359, 0.00000000000 }
    // Originally 10 terms, 2 terms retained, 8 terms dropped.

    // Mercury Z, T**5
    // Originally 7 terms, 0 terms retained, 7 terms dropped.

  }; // end data[][]

} // end class DataVSOP87A_JEphem_Mercury