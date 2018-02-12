//*********************************************************************************
// class jephem.gui.SaveListener
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.gui;

import jephem.GlobalVar;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
  
/**********************************************************************************
Generic listener for 'Save' events, when a String needs to be saved in a file.
@author Thierry Graff
@history oct 23 2001 : Creation.
**********************************************************************************/

public class SaveListener implements ActionListener{

  //=================================================================================
  //                                INSTANCE VARIABLES
  //=================================================================================
  private JComponent _parent;
  private String _strToSave;
   
  //=================================================================================
  //                                CONSTRUCTORS
  //=================================================================================
  public SaveListener(JComponent parent, String strToSave){
    super();
    _parent = parent;
    _strToSave = strToSave;
  }// end constructor
  //=================================================================================
  //                                STATIC METHODS
  //=================================================================================
	public void actionPerformed(ActionEvent e){
    final JFileChooser fc = new JFileChooser();
    int returnVal = fc.showOpenDialog(_parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      try{
        FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());
        fos.write(_strToSave.getBytes());
        fos.close();
      }
      catch (IOException ioe){
        // send message
      }
    }// end if
  } // end actionPerformed

}//end class SaveListener
