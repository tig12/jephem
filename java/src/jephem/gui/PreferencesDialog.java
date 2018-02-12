//*********************************************************************************
// class jephem.gui.PreferencesDialog
// Software released under the General Public License (version 2 or later), available at
// http://www.gnu.org/copyleft/gpl.html
//*********************************************************************************
package jephem.gui;

import jephem.GlobalVar;
import jephem.tools.AstroPrefs;
import jephem.JEphemPrefs;
import jephem.astro.AstroEngine;
import jephem.astro.solarsystem.SolarSystemConstants;
import jephem.astro.spacetime.SpaceConstants;
import jephem.astro.spacetime.UnitsConstants;
import jephem.util.Debug;

import tig.GeneralConstants;
import tig.TigBundle;
import tig.TigProperties;
import tig.Strings;
import tig.maths.Maths;
import tig.swing.SwingUtils;
import tig.swing.JTextField2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.*;

/******************************************************************************
Modal dialog which permits to the user to choose the preferences of JEphem.

@author Thierry Graff
@history feb 20 2002 : creation.

@todo handle message if ressource bundles can't be loded
@todo put getTimeBundle and getSpaceBundle in GlobalVar ?
@todo handle message if LAF can't be changed
*********************************************************************************/
public class PreferencesDialog extends JDialog
        implements GeneralConstants, SolarSystemConstants, SpaceConstants, UnitsConstants{
  //=================================================================================
  //                                      INSTANCE VARIABLES
  //=================================================================================

  /** Variables expressing the current preferences. */
  private int _lang;

  /** content pane. */
  private Container _contentPane;
  /** Preferences tree. */
  private JTree _prefTree;

  /** The different option panes */
  private JPanel _treePanel;
  private JPanel _generalPanel;
  private JPanel _astroPanel;
  private JPanel _directoriesPanel;
  private JPanel _keyboardShortcutsPanel;
  private JPanel _curSelectedPanel;

  /** Rigid areas to give the prefs panels a uniform size. */
  private Component _horRigidArea;
  private Component _verRigidArea;

  /** GUI components */
  // General panel
  private JComboBox _cmbLAF;
  private JButton _btnApplyLAF;
  private JComboBox _cmbLang;
  // Astro panel
  private JComboBox _cmbAstroEngine;
  private JTextField2 _txtPrecision;

  // Dialog buttons
  JButton _btnOK;
  JButton _btnCancel;

  //=================================================================================
  //                          RESSOURCE BUNDLES (STATIC VARIABLES)
  //=================================================================================
  // ***** Ressource bundles. ******
  private static TigBundle _myBundle, _galBundle, _astroBundle;
  static{
    _galBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_GENERAL);
    _astroBundle = GlobalVar.getBundle(GlobalVar.BUNDLE_ASTRO);
    try{
      _myBundle = new TigBundle(GlobalVar.getDirectory(GlobalVar.DIR_LANG) + FS + "PreferencesDialog.lang", GlobalVar.getLocale());
    }
    catch (IOException ioe){
      Debug.traceError(ioe);
    }
  };

  //=================================================================================
  //                                      CONSTANTS
  //=================================================================================

  // Strings displayed in the tree's node.
  private static final String STR_GENERAL       = _myBundle.getString("General");
  private static final String STR_ASTRONOMY     = _astroBundle.getString("Astronomy");
  private static final String STR_DIRECTORIES   = _myBundle.getString("Directories");
  private static final String STR_KBD_SHORTCUTS = _myBundle.getString("KeyboardShortcuts");

  // Constants useful for the tree cell renderer
  private static final String imagePath = GlobalVar.getDirectory(GlobalVar.DIR_DATA) + FS + "gui" + FS;
  private static final ImageIcon iconDirectories = new ImageIcon(imagePath + "iconSave16.gif");
  private static final ImageIcon iconAstro = new ImageIcon(imagePath + "iconAstro16.gif");
  private static final ImageIcon iconKeyboard = new ImageIcon(imagePath + "iconKeyboard16.gif");

  //=================================================================================
  //                            CONSTRUCTORS
  //=================================================================================

  /** Unique constructor */
  public PreferencesDialog() throws Exception {
    super(GlobalVar.getMainFrame(), _myBundle.getString("Dialog.Title"), true); //true because modal
    _contentPane = this.getContentPane();

    // ***** Tree pane *****
    _treePanel = new JPanel();
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(_myBundle.getString("Preferences"));
    rootNode.add(new DefaultMutableTreeNode(STR_GENERAL));
    rootNode.add(new DefaultMutableTreeNode(STR_ASTRONOMY));
    rootNode.add(new DefaultMutableTreeNode(STR_DIRECTORIES));
    rootNode.add(new DefaultMutableTreeNode(STR_KBD_SHORTCUTS));
    _prefTree = new JTree(rootNode);
    _prefTree.addTreeSelectionListener(new PrefTreeSelectionListener());
    _prefTree.setCellRenderer(new PreferencesTreeCellRenderer());
    _treePanel.add(_prefTree);


    // ***** General panel *****
    _generalPanel = new JPanel(new BorderLayout());
    _generalPanel.setBorder(BorderFactory.createEtchedBorder());

    // Look and feel
    JPanel gp1 = new JPanel();
    gp1.setLayout(new BoxLayout(gp1, BoxLayout.Y_AXIS));
    // text, combo and button
    JPanel gp1_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    gp1_1.add(new JLabel(_myBundle.getString("LookAndFeel")));
    // WARNING : order in the combo MUST correspond to tig.swing.SwingUtils.LAF_XXX constants
    _cmbLAF = new JComboBox(new String[]{_myBundle.getString("PlatformDefault"),
                                         "Kunststoff",
                                         "Macintosh",
                                         "Metal",
                                         "Motif",
                                         "Windows"
                                         });
    gp1_1.add(_cmbLAF);
    _btnApplyLAF = new JButton(_galBundle.getString("action.Apply"));
    _btnApplyLAF.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        applyLAFChange();
      }});
    gp1_1.add(_btnApplyLAF);
    // notes
    JPanel gp1_2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    gp1_2.add(new JLabel(_myBundle.getString("LAFNote1")));
    JPanel gp1_3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    gp1_3.add(new JLabel(_myBundle.getString("LAFNote2")));
    // layout for look and feel
    gp1.add(gp1_1);
    gp1.add(gp1_2);
    gp1.add(gp1_3);

    // Language
    JPanel gp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    gp2.add(new JLabel(_myBundle.getString("Language")));
    _cmbLang = new JComboBox(new String[]{_myBundle.getString("Lang.EN"),
                                         _myBundle.getString("Lang.FR")});
    gp2.add(_cmbLang);
    JPanel gp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    gp3.add(new JLabel(_myBundle.getString("LangNote")));

    // General Panel layout
    JPanel gp10 = new JPanel();
    gp10.setLayout(new BoxLayout(gp10, BoxLayout.Y_AXIS));
    gp10.add(gp1);
    gp10.add(Box.createRigidArea(new Dimension(0,5)));
    gp10.add(gp2);
    gp10.add(gp3);
    JPanel gp11 = new JPanel(new BorderLayout());
    gp11.add(gp10, BorderLayout.NORTH);
    _generalPanel.add(gp11);

    // ***** Astro panel *****
    _astroPanel = new JPanel();
    _astroPanel.setBorder(BorderFactory.createEtchedBorder());
    // Precision
    JPanel ap1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ap1.add(new JLabel(_myBundle.getString("DefaultPrecision")));
    _txtPrecision = new JTextField2(5);
    ap1.add(_txtPrecision);
    ap1.add(new JLabel(_astroBundle.getString("arcSeconds")));
    // astro engine
    JPanel ap2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ap2.add(new JLabel(_myBundle.getString("DefaultAstroEngine")));
    _cmbAstroEngine = new JComboBox(new String[]{"JEphem", "Swiss Ephemeris"});
    ap2.add(_cmbAstroEngine);
    // Astro panel layout
    // General Panel layout
    JPanel ap10 = new JPanel();
    ap10.setLayout(new BoxLayout(ap10, BoxLayout.Y_AXIS));
    ap10.add(ap1);
    ap10.add(ap2);
    JPanel ap11 = new JPanel(new BorderLayout());
    ap11.add(ap10, BorderLayout.NORTH);
    _astroPanel.add(ap11);

    // ***** Directories panel *****
    _directoriesPanel = new JPanel();
    _directoriesPanel.setBorder(BorderFactory.createEtchedBorder());
    _directoriesPanel.add(new JButton("Directories"));

    // ***** Keyboard shortcuts panel *****
    _keyboardShortcutsPanel = new JPanel();
    _keyboardShortcutsPanel.setBorder(BorderFactory.createEtchedBorder());
    _keyboardShortcutsPanel.add(new JButton("Kbd shortcuts"));

    // ***** Dialog button panel *****
    JPanel buttonPanel = new JPanel(new BorderLayout());
    JPanel bp1 = new JPanel(new GridLayout(1, 0, 5, 5));
    _btnOK = new JButton(_galBundle.getString("action.OK"));
    _btnOK.addActionListener(new OKListener());
    bp1.add(_btnOK);
    _btnCancel = new JButton(_galBundle.getString("action.Cancel"));
    _btnCancel.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        dispose();
      }});
    bp1.add(_btnCancel);
    buttonPanel.add(bp1, BorderLayout.EAST);

    // ***** General Layout *****
    _contentPane.setLayout(new BorderLayout(5, 5));
    _contentPane.add(_treePanel, BorderLayout.WEST);
    _contentPane.add(_generalPanel, BorderLayout.CENTER);
    _contentPane.add(buttonPanel, BorderLayout.SOUTH);
    _curSelectedPanel = _generalPanel;
    resizePrefsPanels();
    this.pack();

    this.initFields();

  }// end constructor

  //=================================================================================
  //                                      PRIVATE METHODS
  //=================================================================================

  //******************* setPrefspanel ********************************
  /** Called to change the prefs panel, when the tree selection changes. */
  private void setPrefsPanel(JPanel newPanel){
      if(newPanel != _curSelectedPanel){
        _contentPane.remove(_curSelectedPanel);
        _contentPane.add(newPanel, BorderLayout.CENTER);
        _curSelectedPanel = newPanel;
        resizePrefsPanels();
        this.pack();
        this.repaint();
      }
  }// end setPrefspanel

  //******************* resizePrefsPanels ********************************
  /** Resizes the prefs panels. */
  private void resizePrefsPanels(){
      if(_horRigidArea != null) _contentPane.remove(_horRigidArea);
      if(_verRigidArea != null) _contentPane.remove(_verRigidArea);
      // recompute the hor and ver rigid areas dimensions
      int w1 = _generalPanel.getPreferredSize().width;
      int w2 = _astroPanel.getPreferredSize().width;
      int w3 = _directoriesPanel.getPreferredSize().width;
      int w4 = _keyboardShortcutsPanel.getPreferredSize().width;
      int w = Maths.max(new int[]{w1, w2, w3, w4}) + _prefTree.getPreferredSize().width
            + 2 * ((BorderLayout)(_contentPane.getLayout())).getHgap() + 20; // 20 for various forgotten gaps.

      int h1 = _generalPanel.getPreferredSize().height;
      int h2 = _astroPanel.getPreferredSize().height;
      int h3 = _directoriesPanel.getPreferredSize().height;
      int h4 = _keyboardShortcutsPanel.getPreferredSize().height;
      int h = Maths.max(new int[]{h1, h2, h3, h4});
      // resize tree
      _prefTree.setPreferredSize(new Dimension(_prefTree.getPreferredSize().width, h));
      h += 2 * ((BorderLayout)(_contentPane.getLayout())).getVgap();

      _horRigidArea = Box.createRigidArea(new Dimension(w, 0));
      _verRigidArea = Box.createRigidArea(new Dimension(0, h));
      _contentPane.add(_horRigidArea, BorderLayout.NORTH);
      _contentPane.add(_verRigidArea, BorderLayout.EAST);

  }// end resizePrefsPanels

  //******************* initFields ********************************
  /** Called by the constructor to fill the fields from the previously stored preferences. */
  private void initFields(){
    JEphemPrefs jephemPrefs = GlobalVar.getJEphemPrefs();
    AstroPrefs astroPrefs = GlobalVar.getAstroPrefs();

    // init general panel fields
    if (jephemPrefs.getProperty(JEphemPrefs.KEY_LANG).equals("en"))
      _cmbLang.setSelectedIndex(0);
    else
      _cmbLang.setSelectedIndex(1);
    int laf = TigProperties.getIntConstant(jephemPrefs.getProperty(JEphemPrefs.KEY_LAF), "tig.swing.SwingUtils");
    _cmbLAF.setSelectedIndex(laf);

    // init general panel fields
    _txtPrecision.setText(astroPrefs.getProperty(AstroPrefs.KEY_PRECISION));
    if(astroPrefs.getProperty(AstroPrefs.KEY_EPHEMERIS_IMPLEMENTATION).equals(AstroEngine.JEPHEM))
      _cmbAstroEngine.setSelectedIndex(0);
    else
      _cmbAstroEngine.setSelectedIndex(1);

  }// end initFields

  //******************* storePrefs ********************************
  /** Called by OKListener to store the new values of the preferences. */
  private void storePrefs(){

    JEphemPrefs jephemPrefs = GlobalVar.getJEphemPrefs();
    AstroPrefs astroPrefs = GlobalVar.getAstroPrefs();

    // First, re-set the directories.

    // store fields coming from general panel.
    if(_cmbLang.getSelectedIndex() == 0) jephemPrefs.setProperty(JEphemPrefs.KEY_LANG, "en");
    else jephemPrefs.setProperty(JEphemPrefs.KEY_LANG, "fr");
    String[] lafConstantNames = {"LAF_SYSTEM", "LAF_KUNSTSTOFF", "LAF_MACINTOSH", "LAF_METAL", "LAF_MOTIF", "LAF_WINDOWS"};
    jephemPrefs.setProperty(JEphemPrefs.KEY_LAF, lafConstantNames[_cmbLAF.getSelectedIndex()]);

    // store fields coming from astro panel.
    astroPrefs.setProperty(AstroPrefs.KEY_PRECISION, _txtPrecision.getText()); // validity check done before.
    if(_cmbAstroEngine.getSelectedIndex() == 0) astroPrefs.setProperty(AstroPrefs.KEY_EPHEMERIS_IMPLEMENTATION, AstroEngine.JEPHEM);
    else astroPrefs.setProperty(AstroPrefs.KEY_EPHEMERIS_IMPLEMENTATION, AstroEngine.SWISS_EPHEMERIS);

    // Save the properties
    jephemPrefs.store();
    astroPrefs.store();
    GlobalVar.reloadData();

  }// end storePrefs

  //******************* applyLAFChange ********************************
  /** Called when 'ApplyLAF' or 'OK' buttons are pressed. */
  public void applyLAFChange(){
    try{
      SwingUtils.setLookAndFeel(_cmbLAF.getSelectedIndex());
      // update all the components
      SwingUtilities.updateComponentTreeUI(GlobalVar.getMainFrame());
      SwingUtilities.updateComponentTreeUI(PreferencesDialog.this); // performs general panel
      SwingUtilities.updateComponentTreeUI(PreferencesDialog.this._astroPanel);
      SwingUtilities.updateComponentTreeUI(PreferencesDialog.this._directoriesPanel);
      SwingUtilities.updateComponentTreeUI(PreferencesDialog.this._keyboardShortcutsPanel);
      PreferencesDialog.this.pack();
    }
    catch(Exception e){
      // nothing done
    }
  } // end applyLAFChange
  //=================================================================================
  //                            INNER CLASSES
  //=================================================================================

  //*****************************************************************
  /** Permits to change the displayed panel when the tree selection changes */
  class PrefTreeSelectionListener implements TreeSelectionListener{
    public void valueChanged(TreeSelectionEvent tse) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)_prefTree.getLastSelectedPathComponent();
      JPanel newPane = null;
//System.out.println("listener" + (String)node.getUserObject());
      if(node.getUserObject().equals(STR_GENERAL)){
        newPane = _generalPanel;
      }
      else if(node.getUserObject().equals(STR_ASTRONOMY)){
        newPane = _astroPanel;
      }
      else if(node.getUserObject().equals(STR_DIRECTORIES)){
        newPane = _directoriesPanel;
      }
      else if(node.getUserObject().equals(STR_KBD_SHORTCUTS)){
        newPane = _keyboardShortcutsPanel;
      }
      else{
        System.out.println("listener - no equality");
        return; // useful when click on root node
      }

      PreferencesDialog.this.setPrefsPanel(newPane);

    } // end actionPerformed
  } // end class PrefTreeSelectionListener

  /**********************************************************************************
  Cell renderer for the tree of PreferencesDialog.
  **********************************************************************************/
  private class PreferencesTreeCellRenderer extends DefaultTreeCellRenderer{
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus){
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if(leaf) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        String str = node.getUserObject().toString();
        if(str.equals(STR_DIRECTORIES)) setIcon(iconDirectories);
        else if(str.equals(STR_ASTRONOMY)) setIcon(iconAstro);
        else if(str.equals(STR_KBD_SHORTCUTS)) setIcon(iconKeyboard);
        //setToolTipText("This book is in the Tutorial series.");
//      } else {
//          //setToolTipText(null);
      }
      return this;
    }// end getTreeCellRendererComponent()
  }//end class PreferencesTreeCellRenderer

  /**********************************************************************************
  OKListener : contains all the verification and savings of the prefs.
  **********************************************************************************/
  class OKListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      // Check 'precision'
      try{
         double precision = Strings.parseDouble(_txtPrecision.getText());
      }
      catch(NumberFormatException nfe){
        String warningMsg = _galBundle.getString("error.IncorectValueOfField") + " : ' "
                     + _astroBundle.getString("Precision") + "'";
        UtilsGUI.showWarningMessage(warningMsg);
        PreferencesDialog.this.setPrefsPanel(_astroPanel);
        _txtPrecision.requestFocus();
        return;
      }

      PreferencesDialog.this.storePrefs();
      PreferencesDialog.this.applyLAFChange();
      PreferencesDialog.this.dispose();
    } // end actionPerformed
  } // end class OKListener

}// end class PreferencesDialog