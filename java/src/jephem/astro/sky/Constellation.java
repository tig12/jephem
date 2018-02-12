//*********************************************************************************
// class jephem.astro.sky.Constellation
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro.sky;

import jephem.util.Debug;
import tig.GeneralConstants;

/******************************************************************************
Represents a constellation, and hold its assoiated data. Uses class {Constellations}, which contains
the data arrays.
@author Thierry Graff.
@history apr 30 2002 : Creation

@todo
*********************************************************************************/
public class Constellation implements GeneralConstants{

  //=================================================================================
  //                   INSTANCE VARIABLES
  //=================================================================================
  private String _abbr; // latin abbreviation
  private String _latinName;
  private double raMin, raMax, decMin, decMax;
  private double _surface; // in square degrees
  private double[][] _boundaries;

  //=================================================================================
  //                     PRIVATE CONSTANTS
  //=================================================================================

} //end class Constellation