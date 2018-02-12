//********************************************************************
// class jephem.astro.solarsystem.vsop87.DataVSOP87A_JEphem_Jupiter
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//********************************************************************
package jephem.astro.solarsystem.vsop87;

/********************************************************************
Data for calculation of Jupiter coordinates using VSOP87 theory (version A).
********************************************************************/
class DataVSOP87A_JEphem_Jupiter{

  /** Array indicating the number of terms for a coordinate and a power of time.
  <BR><CODE>nbTerms[i][j]</CODE> = number of terms for coordinate i (X, Y or Z),
  for t<SUP>j</SUP>.
  */
  protected static final int nbTerms[][] = {
    {57, 48, 32, 13, 8, 4},
    {56, 47, 32, 13, 8, 4},
    {18, 11, 6, 3, 0, 0}
  }; // end nbTerms[][]

  /** Array containing the terms for the summation.
  <BR><CODE>data[n][0]</CODE> represents term A.
  <BR><CODE>data[n][1]</CODE> represents term B.
  <BR><CODE>data[n][2]</CODE> represents term C.
  */
  protected static final double data[][] = {
    // Jupiter X, T**0
    { 5.19663470114, 0.59945082355, 529.69096509460 },
    { 0.36662642320, 3.14159265359, 0.00000000000 },
    { 0.12593937922, 0.94911583701, 1059.38193018920 },
    { 0.01500672056, 0.73175134610, 522.57741809380 },
    { 0.01476224578, 3.61736921122, 536.80451209540 },
    { 0.00457752736, 1.29883700755, 1589.07289528380 },
    { 0.00301689798, 5.17372551148, 7.11354700080 },
    { 0.00385975375, 2.01229910687, 103.09277421860 },
    { 0.00194025405, 5.02580363996, 426.59819087600 },
    { 0.00150678793, 6.12003027739, 110.20632121940 },
    { 0.00144867641, 5.55980577080, 632.78373931320 },
    { 0.00134226996, 0.87648567011, 213.29909543800 },
    { 0.00103494641, 6.19324769120, 1052.26838318840 },
    { 0.00114201562, 0.01567084269, 1162.47470440780 },
    { 0.00072095575, 3.96117430643, 1066.49547719000 },
    { 0.00059486083, 4.45769374358, 949.17560896980 },
    { 0.00068284021, 3.44051122631, 846.08283475120 },
    { 0.00047092251, 1.44612636451, 419.48464387520 },
    { 0.00030623417, 2.99132321427, 206.18554843720 },
    { 0.00026613459, 4.85169906494, 323.50541665740 },
    { 0.00019727457, 1.64891626213, 2118.76386037840 },
    { 0.00016481594, 1.95150056568, 316.39186965660 },
    { 0.00016101974, 0.87973155980, 515.46387109300 },
    { 0.00014209487, 2.07769621413, 742.99006053260 },
    { 0.00015192516, 6.25820127906, 735.87651353180 },
    { 0.00011423199, 3.48146108929, 543.91805909620 },
    { 0.00012155285, 3.75229924999, 525.75881183150 },
    { 0.00011996271, 0.58568573729, 533.62311835770 },
    { 0.00008468556, 3.47248751739, 639.89728631400 },
    { 0.00008223302, 5.56680447143, 1478.86657406440 },
    { 0.00008694124, 0.38262009411, 1692.16566950240 },
    { 0.00007427517, 5.98380751196, 956.28915597060 },
    { 0.00007516470, 0.92896448412, 1265.56747862640 },
    { 0.00007655867, 0.14178789086, 1581.95934828300 },
    { 0.00005318791, 1.10494016349, 526.50957135690 },
    { 0.00005218492, 3.23235129224, 532.87235883230 },
    { 0.00005777311, 5.03726165628, 14.22709400160 },
    { 0.00004622685, 3.75817086099, 1375.77379984580 },
    { 0.00003939864, 4.30892687511, 1596.18644228460 },
    { 0.00004569444, 2.15087281710, 95.97922721780 },
    { 0.00002952712, 3.85988483947, 309.27832265580 },
    { 0.00002857935, 6.01118473739, 117.31986822020 },
    { 0.00002440094, 4.23995765702, 433.71173787680 },
    { 0.00002438257, 3.88808463822, 220.41264243880 },
    { 0.00002675112, 3.18723449094, 1169.58825140860 },
    { 0.00002386425, 5.96354994324, 1045.15483618760 },
    { 0.00001870097, 0.52019313301, 1155.36115740700 },
    { 0.00001939060, 5.91883412864, 625.67019231240 },
    { 0.00001631500, 4.41910383466, 942.06206196900 },
    { 0.00001451667, 5.76112706040, 853.19638175200 },
    { 0.00001361286, 1.34792748837, 1368.66025284500 },
    { 0.00001663331, 1.94010629194, 838.96928775040 },
    { 0.00001611229, 5.49324974845, 74.78159856730 },
    { 0.00001033570, 0.08907208789, 1795.25844372100 },
    { 0.00000991481, 3.08609505814, 1272.68102562720 },
    { 0.00001088284, 1.13406104190, 527.24328453980 },
    { 0.00001080643, 3.20528362573, 532.13864564940 },
    // Originally 1055 terms, 57 terms retained, 998 terms dropped.

    // Jupiter X, T**1
    { 0.00882389251, 3.14159265359, 0.00000000000 },
    { 0.00635297172, 0.10662156868, 1059.38193018920 },
    { 0.00599720482, 2.42996678275, 522.57741809380 },
    { 0.00589157060, 1.91556314637, 536.80451209540 },
    { 0.00081697204, 3.46668108797, 7.11354700080 },
    { 0.00046201898, 0.45714214032, 1589.07289528380 },
    { 0.00032508590, 1.74648849928, 1052.26838318840 },
    { 0.00033891193, 4.10113482752, 529.69096509460 },
    { 0.00031234303, 2.34698051502, 1066.49547719000 },
    { 0.00021244363, 4.36576178953, 110.20632121940 },
    { 0.00018156701, 4.00572238779, 426.59819087600 },
    { 0.00013577576, 0.30008010246, 632.78373931320 },
    { 0.00012889505, 2.57489294062, 515.46387109300 },
    { 0.00009125875, 1.78082469962, 543.91805909620 },
    { 0.00008085991, 6.16136518902, 949.17560896980 },
    { 0.00007142547, 3.17267801203, 323.50541665740 },
    { 0.00004292240, 4.74970626655, 206.18554843720 },
    { 0.00004393977, 1.14770788063, 735.87651353180 },
    { 0.00003399164, 2.90091450747, 526.50957135690 },
    { 0.00003333344, 1.43691652967, 532.87235883230 },
    { 0.00003873467, 3.33648870101, 14.22709400160 },
    { 0.00003044408, 1.65428048669, 525.75881183150 },
    { 0.00003001874, 2.68376982746, 533.62311835770 },
    { 0.00002933359, 2.61899855005, 419.48464387520 },
    { 0.00002438199, 3.60655644537, 316.39186965660 },
    { 0.00002804218, 4.89742591320, 103.09277421860 },
    { 0.00002990245, 0.80692155639, 2118.76386037840 },
    { 0.00001977572, 5.08915489088, 956.28915597060 },
    { 0.00001853679, 2.76941001747, 1596.18644228460 },
    { 0.00001772800, 0.72631739446, 742.99006053260 },
    { 0.00001812965, 3.84602148747, 95.97922721780 },
    { 0.00001532945, 4.31556714501, 117.31986822020 },
    { 0.00001904067, 1.85937873703, 1581.95934828300 },
    { 0.00001539212, 1.47899172821, 639.89728631400 },
    { 0.00001632362, 1.41504212408, 1045.15483618760 },
    { 0.00001023812, 2.57182697715, 433.71173787680 },
    { 0.00001055422, 2.50844222977, 1265.56747862640 },
    { 0.00000981775, 2.18800022614, 220.41264243880 },
    { 0.00000940094, 1.34873014473, 625.67019231240 },
    { 0.00000839712, 6.20534871612, 942.06206196900 },
    { 0.00000985733, 1.42746834265, 1169.58825140860 },
    { 0.00000778939, 5.49323533683, 309.27832265580 },
    { 0.00000765192, 1.96892067856, 1155.36115740700 },
    { 0.00000643975, 4.25838784988, 213.29909543800 },
    { 0.00000734378, 0.11449859192, 1162.47470440780 },
    { 0.00000538315, 4.24575280150, 853.19638175200 },
    { 0.00000501903, 4.81386721508, 199.07200143640 },
    { 0.00000499873, 3.02041735659, 330.61896365820 },
    // Originally 488 terms, 48 terms retained, 440 terms dropped.

    // Jupiter X, T**2
    { 0.00123864644, 4.13563277513, 522.57741809380 },
    { 0.00121521296, 0.21155109275, 536.80451209540 },
    { 0.00085355503, 0.00000000000, 0.00000000000 },
    { 0.00077685547, 5.29776154458, 529.69096509460 },
    { 0.00041410887, 5.12291589939, 1059.38193018920 },
    { 0.00011423070, 1.72917878238, 7.11354700080 },
    { 0.00007051587, 0.74163703419, 1066.49547719000 },
    { 0.00005711029, 3.63172846494, 1052.26838318840 },
    { 0.00005242644, 4.27482379441, 515.46387109300 },
    { 0.00004039540, 5.58417732117, 1589.07289528380 },
    { 0.00003706457, 0.07769981349, 543.91805909620 },
    { 0.00001698817, 2.44284418066, 110.20632121940 },
    { 0.00001134598, 2.35807061809, 426.59819087600 },
    { 0.00001322673, 1.63142549980, 14.22709400160 },
    { 0.00000888203, 4.66627290244, 526.50957135690 },
    { 0.00000865547, 5.95596888539, 532.87235883230 },
    { 0.00000822579, 1.96473995078, 632.78373931320 },
    { 0.00000994008, 1.46985522253, 323.50541665740 },
    { 0.00000574066, 1.66926588148, 949.17560896980 },
    { 0.00000733386, 0.37132887987, 103.09277421860 },
    { 0.00000571711, 3.16912095909, 1045.15483618760 },
    { 0.00000514256, 5.97103330686, 525.75881183150 },
    { 0.00000512225, 4.65535000010, 533.62311835770 },
    { 0.00000595930, 2.85993171505, 735.87651353180 },
    { 0.00000458533, 1.24450068286, 1596.18644228460 },
    { 0.00000419126, 2.61042238424, 117.31986822020 },
    { 0.00000374840, 5.55821526471, 95.97922721780 },
    { 0.00000341765, 0.39491407125, 206.18554843720 },
    { 0.00000332926, 6.00008752152, 2118.76386037840 },
    { 0.00000294743, 4.41871274898, 419.48464387520 },
    { 0.00000282018, 3.71098262370, 1581.95934828300 },
    { 0.00000264464, 3.68007673744, 956.28915597060 },
    // Originally 255 terms, 32 terms retained, 223 terms dropped.

    // Jupiter X, T**3
    { 0.00017071323, 5.86133022278, 522.57741809380 },
    { 0.00016713548, 4.77458794485, 536.80451209540 },
    { 0.00003348610, 0.00000000000, 0.00000000000 },
    { 0.00001787838, 3.56550298031, 1059.38193018920 },
    { 0.00001435449, 5.98502036587, 515.46387109300 },
    { 0.00001080194, 5.42530305914, 1066.49547719000 },
    { 0.00001014206, 4.64773902077, 543.91805909620 },
    { 0.00001073175, 6.22314467964, 7.11354700080 },
    { 0.00000711065, 5.50680515205, 1052.26838318840 },
    { 0.00000261089, 4.28269834394, 1589.07289528380 },
    { 0.00000301054, 6.19841321090, 14.22709400160 },
    { 0.00000134738, 4.94746197927, 1045.15483618760 },
    { 0.00000124290, 0.37523072266, 110.20632121940 },
    // Originally 140 terms, 13 terms retained, 127 terms dropped.

    // Jupiter X, T**4
    { 0.00001762402, 1.32863039757, 522.57741809380 },
    { 0.00001717846, 3.03331531843, 536.80451209540 },
    { 0.00000304063, 1.43144096257, 515.46387109300 },
    { 0.00000216508, 2.91205595526, 543.91805909620 },
    { 0.00000128193, 3.83022265336, 1066.49547719000 },
    { 0.00000160571, 3.14159265359, 0.00000000000 },
    { 0.00000081343, 4.47957999274, 7.11354700080 },
    { 0.00000068446, 1.06498404827, 1052.26838318840 },
    // Originally 58 terms, 8 terms retained, 50 terms dropped.

    // Jupiter X, T**5
    { 0.00000131471, 3.21284928867, 522.57741809380 },
    { 0.00000126748, 1.16307002134, 536.80451209540 },
    { 0.00000048324, 3.19657723128, 515.46387109300 },
    { 0.00000034034, 1.12801913258, 543.91805909620 },
    // Originally 11 terms, 4 terms retained, 7 terms dropped.

    // Jupiter Y, T**0
    { 5.19520046589, 5.31203162731, 529.69096509460 },
    { 0.12592862602, 5.66160227728, 1059.38193018920 },
    { 0.09363670616, 3.14159265359, 0.00000000000 },
    { 0.01508275299, 5.43934968102, 522.57741809380 },
    { 0.01475809370, 2.04679566495, 536.80451209540 },
    { 0.00457750806, 6.01129093501, 1589.07289528380 },
    { 0.00300686679, 3.60948050740, 7.11354700080 },
    { 0.00378285578, 3.53006782383, 103.09277421860 },
    { 0.00192333128, 3.45690564771, 426.59819087600 },
    { 0.00146104656, 4.62267224431, 110.20632121940 },
    { 0.00139480058, 4.00075307706, 632.78373931320 },
    { 0.00132696764, 5.62184581859, 213.29909543800 },
    { 0.00101999807, 4.57594598884, 1052.26838318840 },
    { 0.00114043110, 4.72982262969, 1162.47470440780 },
    { 0.00072091178, 2.39048659148, 1066.49547719000 },
    { 0.00059051769, 2.89529070968, 949.17560896980 },
    { 0.00068374489, 1.86537074374, 846.08283475120 },
    { 0.00029807369, 4.52105772740, 206.18554843720 },
    { 0.00026933579, 3.86233956827, 419.48464387520 },
    { 0.00026619714, 3.28203174951, 323.50541665740 },
    { 0.00020873780, 3.79369881757, 735.87651353180 },
    { 0.00019727397, 0.07818534532, 2118.76386037840 },
    { 0.00018639846, 0.38751972138, 316.39186965660 },
    { 0.00016355726, 5.56997881604, 515.46387109300 },
    { 0.00014606858, 0.47759399145, 742.99006053260 },
    { 0.00011419853, 1.91089341468, 543.91805909620 },
    { 0.00012153427, 2.18151972499, 525.75881183150 },
    { 0.00011988875, 5.29687602089, 533.62311835770 },
    { 0.00008443107, 1.91435801697, 639.89728631400 },
    { 0.00008163163, 4.00303742375, 1478.86657406440 },
    { 0.00008732789, 5.09607066097, 1692.16566950240 },
    { 0.00007414115, 4.41141990461, 956.28915597060 },
    { 0.00007619486, 5.59554151997, 1265.56747862640 },
    { 0.00007779184, 4.83346300662, 1581.95934828300 },
    { 0.00005322882, 5.81740472645, 526.50957135690 },
    { 0.00005217025, 1.66178643542, 532.87235883230 },
    { 0.00005772132, 3.46915716927, 14.22709400160 },
    { 0.00004528355, 2.18377558038, 1375.77379984580 },
    { 0.00003939875, 2.73830531054, 1596.18644228460 },
    { 0.00004567181, 3.71300776935, 95.97922721780 },
    { 0.00003235419, 4.76600347062, 625.67019231240 },
    { 0.00003140740, 5.59566796922, 309.27832265580 },
    { 0.00002855423, 4.44478286006, 117.31986822020 },
    { 0.00002445625, 2.67036952230, 433.71173787680 },
    { 0.00002253545, 4.28462825722, 838.96928775040 },
    { 0.00002672262, 1.61857897069, 1169.58825140860 },
    { 0.00002423639, 2.32942339839, 220.41264243880 },
    { 0.00002362662, 4.60417580207, 1155.36115740700 },
    { 0.00002409581, 4.33196301609, 1045.15483618760 },
    { 0.00001458169, 4.18761881277, 853.19638175200 },
    { 0.00001432195, 3.24824554500, 942.06206196900 },
    { 0.00001646568, 3.91965876562, 74.78159856730 },
    { 0.00001050270, 4.83706014327, 1795.25844372100 },
    { 0.00001002355, 1.50931939870, 1272.68102562720 },
    { 0.00001087727, 5.84673086939, 527.24328453980 },
    { 0.00001079512, 1.63448507346, 532.13864564940 },
    // Originally 1037 terms, 56 terms retained, 981 terms dropped.

    // Jupiter Y, T**1
    { 0.01694798253, 3.14159265359, 0.00000000000 },
    { 0.00634859798, 4.81903199650, 1059.38193018920 },
    { 0.00601160431, 0.85811249940, 522.57741809380 },
    { 0.00588928504, 0.34491576890, 536.80451209540 },
    { 0.00081187145, 1.90914316532, 7.11354700080 },
    { 0.00046888090, 1.91294535618, 529.69096509460 },
    { 0.00046194129, 5.16955994561, 1589.07289528380 },
    { 0.00032503453, 0.17640743623, 1052.26838318840 },
    { 0.00031231694, 0.77623645597, 1066.49547719000 },
    { 0.00019462096, 3.00957119470, 110.20632121940 },
    { 0.00017738615, 2.46531787101, 426.59819087600 },
    { 0.00013701692, 5.02070197804, 632.78373931320 },
    { 0.00013034616, 0.98979834442, 515.46387109300 },
    { 0.00009122660, 0.21022587969, 543.91805909620 },
    { 0.00008109050, 4.58123811601, 949.17560896980 },
    { 0.00007145229, 1.60381236094, 323.50541665740 },
    { 0.00003957592, 6.18550697817, 206.18554843720 },
    { 0.00004347346, 5.85522835488, 735.87651353180 },
    { 0.00003401735, 1.33033225252, 526.50957135690 },
    { 0.00003331887, 6.14951835712, 532.87235883230 },
    { 0.00003866147, 1.76877582038, 14.22709400160 },
    { 0.00003094257, 1.00670454701, 419.48464387520 },
    { 0.00003044205, 0.08329779827, 525.75881183150 },
    { 0.00003001484, 1.11280606283, 533.62311835770 },
    { 0.00002977284, 3.35507028507, 103.09277421860 },
    { 0.00002347100, 2.06781775390, 316.39186965660 },
    { 0.00002990192, 5.51944830506, 2118.76386037840 },
    { 0.00001875464, 5.32657356489, 742.99006053260 },
    { 0.00001854067, 1.19908734197, 1596.18644228460 },
    { 0.00001968401, 3.51896739844, 956.28915597060 },
    { 0.00001808627, 5.40287543026, 95.97922721780 },
    { 0.00001530472, 2.75094722237, 117.31986822020 },
    { 0.00001885393, 0.29905973710, 1581.95934828300 },
    { 0.00001516541, 6.21684203571, 639.89728631400 },
    { 0.00001636913, 6.09270756447, 1045.15483618760 },
    { 0.00001260123, 0.07143173954, 625.67019231240 },
    { 0.00001028165, 1.00301485824, 433.71173787680 },
    { 0.00001035933, 0.98273794152, 1265.56747862640 },
    { 0.00000972507, 0.63832646360, 220.41264243880 },
    { 0.00000983542, 6.14294208089, 1169.58825140860 },
    { 0.00000778705, 4.83558543631, 942.06206196900 },
    { 0.00000886143, 1.10269264426, 309.27832265580 },
    { 0.00000841776, 0.18391927728, 1155.36115740700 },
    { 0.00000767993, 4.84778769533, 1162.47470440780 },
    { 0.00000541536, 2.66914118638, 853.19638175200 },
    { 0.00000551952, 5.72755176773, 213.29909543800 },
    { 0.00000499533, 1.45057427365, 330.61896365820 },
    // Originally 499 terms, 47 terms retained, 452 terms dropped.

    // Jupiter Y, T**2
    { 0.00124032509, 2.56495576833, 522.57741809380 },
    { 0.00121455991, 4.92398766380, 536.80451209540 },
    { 0.00076523263, 3.75913371793, 529.69096509460 },
    { 0.00076943042, 3.14159265359, 0.00000000000 },
    { 0.00041357600, 3.55228440457, 1059.38193018920 },
    { 0.00011277667, 0.18559902389, 7.11354700080 },
    { 0.00007051103, 5.45404368570, 1066.49547719000 },
    { 0.00005719440, 2.05970000230, 1052.26838318840 },
    { 0.00005286157, 2.69490465064, 515.46387109300 },
    { 0.00004039038, 4.01341034637, 1589.07289528380 },
    { 0.00003704528, 4.79029292271, 543.91805909620 },
    { 0.00001280283, 1.47574006861, 110.20632121940 },
    { 0.00001059783, 0.89610748176, 426.59819087600 },
    { 0.00001320627, 0.05786048417, 14.22709400160 },
    { 0.00000888144, 3.09675195621, 526.50957135690 },
    { 0.00000864544, 4.38537588795, 532.87235883230 },
    { 0.00000820223, 0.37911850134, 632.78373931320 },
    { 0.00000993728, 6.18613980226, 323.50541665740 },
    { 0.00000573001, 0.10744491970, 949.17560896980 },
    { 0.00000571480, 1.57855126864, 1045.15483618760 },
    { 0.00000624115, 1.29414272655, 735.87651353180 },
    { 0.00000513863, 4.40000698225, 525.75881183150 },
    { 0.00000511927, 3.08494935962, 533.62311835770 },
    { 0.00000458314, 5.95712671606, 1596.18644228460 },
    { 0.00000417651, 1.04909922555, 117.31986822020 },
    { 0.00000357612, 2.57817679198, 419.48464387520 },
    { 0.00000372789, 0.82429067684, 95.97922721780 },
    { 0.00000332599, 4.43064686875, 2118.76386037840 },
    { 0.00000263411, 1.67577905079, 625.67019231240 },
    { 0.00000261838, 1.57658925499, 206.18554843720 },
    { 0.00000283280, 2.13607070848, 1581.95934828300 },
    { 0.00000261886, 2.11384561317, 956.28915597060 },
    // Originally 259 terms, 32 terms retained, 227 terms dropped.

    // Jupiter Y, T**3
    { 0.00017085516, 4.29096904063, 522.57741809380 },
    { 0.00016701353, 3.20365737109, 536.80451209540 },
    { 0.00004006038, 0.00000000000, 0.00000000000 },
    { 0.00001782451, 1.99283071153, 1059.38193018920 },
    { 0.00001443816, 4.40866555269, 515.46387109300 },
    { 0.00001079405, 3.85450799252, 1066.49547719000 },
    { 0.00001013157, 3.07729621279, 543.91805909620 },
    { 0.00001055565, 4.70184773789, 7.11354700080 },
    { 0.00000710385, 3.93734062697, 1052.26838318840 },
    { 0.00000259601, 2.71566478390, 1589.07289528380 },
    { 0.00000300599, 4.62156117661, 14.22709400160 },
    { 0.00000134826, 3.36277253898, 1045.15483618760 },
    { 0.00000142837, 5.28814307330, 529.69096509460 },
    // Originally 136 terms, 13 terms retained, 123 terms dropped.

    // Jupiter Y, T**4
    { 0.00001762645, 6.04159386554, 522.57741809380 },
    { 0.00001716045, 1.46206285710, 536.80451209540 },
    { 0.00000305036, 6.14052786819, 515.46387109300 },
    { 0.00000216203, 1.34301856666, 543.91805909620 },
    { 0.00000127895, 2.25941664796, 1066.49547719000 },
    { 0.00000081740, 2.84766415879, 7.11354700080 },
    { 0.00000068446, 5.77736913573, 1052.26838318840 },
    { 0.00000070306, 0.00000000000, 0.00000000000 },
    // Originally 60 terms, 8 terms retained, 52 terms dropped.

    // Jupiter Y, T**5
    { 0.00000131471, 1.64205554066, 522.57741809380 },
    { 0.00000126634, 5.87372673584, 536.80451209540 },
    { 0.00000048269, 1.62788936723, 515.46387109300 },
    { 0.00000034034, 5.84040207007, 543.91805909620 },
    // Originally 11 terms, 4 terms retained, 7 terms dropped.

    // Jupiter Z, T**0
    { 0.11823100489, 3.55844646343, 529.69096509460 },
    { 0.00859031952, 0.00000000000, 0.00000000000 },
    { 0.00286562094, 3.90812238338, 1059.38193018920 },
    { 0.00042388592, 3.60144191032, 522.57741809380 },
    { 0.00033295491, 0.30297050585, 536.80451209540 },
    { 0.00010416160, 4.25764593061, 1589.07289528380 },
    { 0.00007449294, 5.24213104150, 103.09277421860 },
    { 0.00006910102, 1.75032945752, 7.11354700080 },
    { 0.00005292012, 1.68231447192, 426.59819087600 },
    { 0.00004313598, 3.70673689841, 213.29909543800 },
    { 0.00003784265, 2.71522544491, 110.20632121940 },
    { 0.00003798016, 2.16715743175, 632.78373931320 },
    { 0.00002455385, 2.96904135659, 1052.26838318840 },
    { 0.00002461547, 2.99889460411, 1162.47470440780 },
    { 0.00002001451, 2.68535838309, 419.48464387520 },
    { 0.00002163471, 6.26718259854, 846.08283475120 },
    { 0.00001633653, 0.64194743493, 1066.49547719000 },
    { 0.00001450672, 1.17108416193, 949.17560896980 },
    // Originally 216 terms, 18 terms retained, 198 terms dropped.

    // Jupiter Z, T**1
    { 0.00407072175, 1.52699353482, 529.69096509460 },
    { 0.00020307341, 2.59878269248, 1059.38193018920 },
    { 0.00014424953, 4.85400155025, 536.80451209540 },
    { 0.00015474611, 0.00000000000, 0.00000000000 },
    { 0.00012730364, 5.45536715732, 522.57741809380 },
    { 0.00002100882, 0.09538864287, 7.11354700080 },
    { 0.00001230425, 3.14222500244, 1589.07289528380 },
    { 0.00000760633, 5.27867348162, 1066.49547719000 },
    { 0.00000678832, 4.74895422783, 1052.26838318840 },
    { 0.00000597018, 1.04748050782, 110.20632121940 },
    { 0.00000570024, 1.09418619361, 103.09277421860 },
    // Originally 104 terms, 11 terms retained, 93 terms dropped.

    // Jupiter Z, T**2
    { 0.00028635326, 3.01374166973, 529.69096509460 },
    { 0.00003114752, 3.13228646176, 536.80451209540 },
    { 0.00002379765, 0.95574345340, 522.57741809380 },
    { 0.00001310111, 2.05263704913, 1059.38193018920 },
    { 0.00000898757, 0.00000000000, 0.00000000000 },
    { 0.00000305635, 4.64213318439, 7.11354700080 },
    // Originally 65 terms, 6 terms retained, 59 terms dropped.

    // Jupiter Z, T**3
    { 0.00000964355, 4.79228412032, 529.69096509460 },
    { 0.00000443244, 1.39969952998, 536.80451209540 },
    { 0.00000295600, 2.81281406373, 522.57741809380 }
    // Originally 27 terms, 3 terms retained, 24 terms dropped.

    // Jupiter Z, T**4
    // Originally 10 terms, 0 terms retained, 10 terms dropped.

    // Jupiter Z, T**5
    // Originally 3 terms, 0 terms retained, 3 terms dropped.

  }; // end data[][]

} // end class DataVSOP87A_JEphem_Jupiter