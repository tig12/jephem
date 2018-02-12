//*********************************************************************************
// class jephem.MainFrame
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************

package jephem.gui;

// useless imports from tthe same package
import jephem.gui.NewEphemerisDialog;
import jephem.gui.PreferencesDialog;
import jephem.gui.NewCurveDialog;
import jephem.gui.SkyMapViewer;
import jephem.gui.CurveViewer;

import jephem.GlobalVar;
import jephem.util.Debug;

import tig.GeneralConstants;
import tig.TigBundle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.io.*;
import java.net.*;
import java.util.*;

/******************************************************************
Main frame of the application. An instance of it is loaded at application startup, and accessible via
<CODE>GlobalVar.getMainFrame()</CODE>.

@author : Thierry Graff

@history feb 28 2002 : Creation from jephem.JEphem

@todo Handle the dimension (handle OS's task bar hide automatically)
@todo : remove hard coded base URL for HTML pane - build url from GlobalVar.getDirectory(DIR_DATA)
*****************************************************************/
public class MainFrame extends JFrame implements GeneralConstants{

  //=================================================================================
  //                            INSTANCE VARIABLES
  //=================================================================================
  Container _contentPane;
  JPanel _centralPanel;
  //=================================================================================
  //                                      CONSTRUCTORS
  //=================================================================================

  /** Unique constructor. */
  public MainFrame(){
    // Internationalization
    TigBundle generalBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL);
    TigBundle menusBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_MENUS);

    // Initializes the main frame
    this.setTitle(generalBundle.getString("application.title"));
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.addWindowListener(new FrameListener());
    _contentPane = this.getContentPane();

    // ******************************
    // Menus building
    // ******************************
    JMenuBar menuBar = new JMenuBar();
    JMenu menu, submenu;
    JMenuItem menuItem;
    this.setJMenuBar(menuBar);

    menu = new JMenu(menusBundle.getString("menu.File"));
    menu.setMnemonic('F');
      menuItem = new JMenuItem(menusBundle.getString("menu.Quit"), KeyEvent.VK_Q);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(new QuitListener());
    menu.add(menuItem);
    menuBar.add(menu);
    // Event listeners

    menu = new JMenu(menusBundle.getString("menu.Astro"));
    menu.setMnemonic('A');
      menuItem = new JMenuItem(menusBundle.getString("menu.Ephemeris"), KeyEvent.VK_E);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(new EphemerisListener());
    menu.add(menuItem);
      menuItem = new JMenuItem(menusBundle.getString("menu.SkyMap"), KeyEvent.VK_C);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(new SkyMapViewerListener());
    menu.add(menuItem);
      menuItem = new JMenuItem(menusBundle.getString("menu.Curve"), KeyEvent.VK_U);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(new CurveListener());
    menu.add(menuItem);
    menuBar.add(menu);

    menu = new JMenu(menusBundle.getString("menu.Tools"));
    menu.setMnemonic('S');
      menuItem = new JMenuItem(menusBundle.getString("menu.Preferences"), KeyEvent.VK_O);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(new PreferencesListener());
    menu.add(menuItem);
    menuBar.add(menu);

    menu = new JMenu(menusBundle.getString("menu.Help"));
    menu.setMnemonic('H');
      menuItem = new JMenuItem(menusBundle.getString("menu.Help") + "...", KeyEvent.VK_H);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(0,KeyEvent.VK_F1));
      menuItem.addActionListener(new HelpListener());
    menu.add(menuItem);
      menuItem = new JMenuItem(menusBundle.getString("menu.About"), KeyEvent.VK_A);
      menuItem.addActionListener(new AboutListener());
    menu.add(menuItem);
    menuBar.add(menu);

    // ******************************
    // GUI building
    // ******************************
    // TODO : handle OS's task bar
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    //this.setSize(screen);
    this.getRootPane().setPreferredSize(new Dimension(screen.width-10,screen.height-menuBar.getHeight()-20));
    Dimension dim = _contentPane.getSize();
    //System.out.println("_contentPane.width = " + dim.width);
    //System.out.println("_contentPane.height = " + dim.height);

    // StatusBar
    JLabel status =  GlobalVar.getStatus();
    JPanel southPanel = new JPanel(new BorderLayout()); // contains status bar ; just created for the border
    southPanel.setBorder(BorderFactory.createEtchedBorder());
    southPanel.add(status, BorderLayout.CENTER);

    // Central panel
    _centralPanel = new JPanel(new BorderLayout());

    // startupPanel, which will be replaced by other panels
    JPanel startupPanel = new JPanel();
    startupPanel.setBackground(Color.WHITE);
    _centralPanel.add(startupPanel, BorderLayout.CENTER);

    _contentPane.add(_centralPanel, BorderLayout.CENTER);
    _contentPane.add(southPanel, BorderLayout.SOUTH);

// General packing
    this.pack();

  }// end constructor

  //=================================================================================
  //                                      PUBLIC METHODS
  //=================================================================================
  /** Returns the display area of the application (the central panel). */
  public JPanel getCentralPanel(){ return _centralPanel; }

  //=================================================================================
  //                                      PRIVATE METHODS
  //=================================================================================

  //***************** closeApplication() *************************
  /* To put the end code in a single place.
  ActionEvents, WindowEvents then perform then same operations. */
  private void closeApplication(){
    this.setVisible(false);
    System.exit(0);
  } // end closeApplication

  //***************** centerOnFrame() *************************
  /* Returns the Point to permitting to center 'dialog' on the main frame. */
  private Point centerOnFrame(JDialog dialog){
    Dimension d1 = this.getSize();
    Dimension d2 = dialog.getSize();
    int x = (d1.width - d2.width) / 2;
    int y = (d1.height - d2.height) / 2;

    return new Point((x > 0 ? x : 0), (y > 0 ? y : 0));
  } // end centerOnFrame

  //=================================================================================
  //                            INNER CLASSES
  //=================================================================================

  //*****************************************************************
  //	      							EVENT LISTENNERS
  //*****************************************************************

  //**************************************
  class FrameListener extends WindowAdapter {
    public void windowClosing(WindowEvent e){
      closeApplication();
    } // end windowClosing
  } // end class FrameListener

  //**************************************
  class QuitListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      closeApplication();
    } //end actionPerformed
  } //end class QuitListener

  //**************************************
  class EphemerisListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      TigBundle generalBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL);
      // Build Boutons
      JButton btnQuit = new JButton(generalBundle.getString("action.Quit"));
      btnQuit.setToolTipText(generalBundle.getString("action.QuitTTT"));
      btnQuit.setMnemonic('q');
      btnQuit.addActionListener(new QuitListener());

      JButton btnHelp = new JButton(generalBundle.getString("action.Help"));
      btnHelp.setToolTipText(generalBundle.getString("action.HelpTTT"));
      btnHelp.setMnemonic('h');
      btnHelp.addActionListener(new HelpListener());

      JPanel p3 = new JPanel();//contains buttons
      p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
      p3.setBorder(BorderFactory.createEmptyBorder(0,10,5,5));
      p3.add(Box.createHorizontalGlue());
      p3.add(btnHelp);
      p3.add(Box.createRigidArea(new Dimension(5,0)));
      p3.add(btnQuit);

      // Build htmlPane
      JEditorPane htmlPane = new JEditorPane("text/html",BLANK);
      htmlPane.setEditable(false);
      try{
        URL url = new URL("file://C:/b_dvpt/JEphem/data");
        ((HTMLDocument)(htmlPane.getDocument())).setBase(url);
      }
      catch(MalformedURLException mue){
        Debug.traceError(mue);
      }
      GlobalVar.setHTMLPane(htmlPane);
      JScrollPane htmlScrPane = new JScrollPane(htmlPane,
                                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      // Add buttons and htmlPane to central panel
      _centralPanel.removeAll();
      _centralPanel.add(htmlScrPane, BorderLayout.CENTER);
      _centralPanel.add(p3, BorderLayout.SOUTH);
      _centralPanel.invalidate();
      _centralPanel.repaint();
      _centralPanel.validate();

      //Build the ephemeris dialog
      try{
        NewEphemerisDialog ned = new NewEphemerisDialog();
        ned.setLocation(centerOnFrame(ned));
        ned.show();
      }
      catch (Exception e){
        Debug.traceError(e);
      }
    } // end actionPerformed
  } // end class EphemerisListener

  //**************************************
  class SkyMapViewerListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      try{
        SkyMapViewer sv = new SkyMapViewer();
        _centralPanel.removeAll();
        _centralPanel.add(sv, BorderLayout.CENTER);
        _centralPanel.invalidate();
        _centralPanel.repaint();
        _centralPanel.validate();
      }
      catch (Exception e){
        Debug.traceError(e);
      }
    } // end actionPerformed
  } // end class SkyMapViewerListener

  //**************************************
  class CurveListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      try{
        NewCurveDialog ncd = new NewCurveDialog();
        ncd.setLocation(centerOnFrame(ncd));
        ncd.show();
      }
      catch (Exception e){
        Debug.traceError(e);
      }
    } // end actionPerformed
  } // end class CurveListener

  //**************************************
  class PreferencesListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      try{
        PreferencesDialog prefDialog = new PreferencesDialog();
        prefDialog.setLocation(centerOnFrame(prefDialog));
        prefDialog.show();
      }
      catch (Exception e){
      Debug.traceError(e);
      }
    } // end actionPerformed
  } // end class PreferencesListener

  //**************************************
  class HelpListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      //BrowserControl.displayURL("ReadMe.htm");
    } // end actionPerformed
  } // end class HelpListener

  //**************************************
  class AboutListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      //AboutDlg.show();
    } // end actionPerformed
  } // end class AboutListener

}// end class MainFrame
