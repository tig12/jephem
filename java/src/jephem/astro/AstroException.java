//*********************************************************************************
// class jephem.astro.AstroException
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.astro;

/******************************************************************************
Exception thrown by classes of package <CODE>jephem.astro</CODE> when a serious problem occurs.
<BR>It is used to propagate all the exceptions raised in the astro classes up to the calling classes.
<BR>It can contain an embedded exception.

@author Thierry Graff
@history jan 10 2002 : Creation to replace former exception classes.
@history jan 10 2002 : Modified completely the meaning and the use in the rest of the API ; added embedded exception;
*********************************************************************************/
public class AstroException extends Exception{

  //=================================================================================
  //                            INSTANCE VARIABLES
  //=================================================================================
  /** Embedded Exception. */
  private Exception _e;

  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================
  /** Default constructor */
  public AstroException(){
    super();
  }

  /** Constructor with an error message.
  @param strErr The message of this Exception
  */
  public AstroException(String strErr){
    super(strErr);
  }

  /** Constructor from an other Exception.
  @param e the embedded Exception.
  */
  public AstroException(Exception e){
    super();
    _e = e;
  }

  //=================================================================================
  //                                          METHODS
  //=================================================================================
  /** Returns the embedded exception, if any. */
  public Exception getEmbeddedException(){ return _e; }

}//end class AstroException