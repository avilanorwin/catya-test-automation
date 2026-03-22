/**
 * @author Norwin T. Avila
 * @author Eldon Leuterio - Added Menu, Load and Save script file
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.view.screens;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import java.util.logging.Logger;
import com.catya.ui.config.CommonConfig;
import com.catya.ui.config.ScreenConfig;
import com.catya.ui.controller.CATYAController;
import com.catya.ui.model.CATYAModel;
import com.catya.ui.view.base.FWBaseScreen;
import com.catya.ui.view.components.FWTableCellRenderer;
import com.catya.ui.view.components.FWTableModel;
import com.catya.ui.view.components.IWaitDialogTask;
import com.catya.ui.view.components.WaitDialog;

/**
 * Main Application screen
 * @author Norwin
 *
 */
public class CATYAScreen extends FWBaseScreen implements ActionListener {
	
	//------------------------------------------
	//
	//  Toolbar buttons
	//
	//------------------------------------------
	private JMenu fileMenu;
	private JMenuItem loadFileMenuItem;
	private JMenuItem saveFileMenuItem;
	private JMenuItem closeFileMenuItem;
	private JMenuItem quitMenuItem;
	private JMenu helpMenu;
	
	//------------------------------------------
	//
	//  Toolbar buttons
	//
	//------------------------------------------
	private JButton		toolbarStartButton;
	private JButton		toolbarStopButton;
	private JButton 	toolbarPauseButton;
	
	//------------------------------------------
	//
	//  Toggle send icon
	//
	//------------------------------------------
	private ImageIcon toggleSendIcon [];
	
	//------------------------------------------
	//
	// Main body panel
	//
	//------------------------------------------
	private JPanel				bodyPanel;
	
	//------------------------------------------
	//
	// Tabbed Pane
	//
	//------------------------------------------
	JTabbedPane tabbedPane;

	//------------------------------------------
	//
	//  Table
	//
	//------------------------------------------
	private JScrollPane tableView;
	private JTable table;

	//------------------------------------------
	//
	// Controller
	//
	//------------------------------------------
	CATYAController controller;
	
	//------------------------------------------
	//
	// Model
	//
	//------------------------------------------
	CATYAModel model;
	
	private static Logger LOG = Logger.getLogger(CATYAScreen.class.getName());
	
	public CATYAScreen(CATYAModel model) {
		this(CommonConfig.PROPERTY_FILE);
		this.model = model;
		this.model.setModel((FWTableModel)table.getModel());
	}
	
	public CATYAScreen(String propertyFile) {
		super(propertyFile, WindowType.WINDOW_TYPE_FRAME);
		
		URL sendURL = CATYAScreen.class.getResource(getPropertyFileString(
				ScreenConfig.ICO_START));
		URL stopURL = CATYAScreen.class.getResource(getPropertyFileString(
				ScreenConfig.ICO_STOP));
		toggleSendIcon = new ImageIcon[2];
		toggleSendIcon[0] = new ImageIcon(sendURL, getPropertyFileString(
				ScreenConfig.TOOLTIP_START));
		toggleSendIcon[1] = new ImageIcon(stopURL, getPropertyFileString(
				ScreenConfig.TOOLTIP_STOP));
	}
	
	@Override
	public void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		fileMenu.add(loadFileMenuItem = generate(loadFileMenuItem, "Load Script", "Load an Excel File", ScreenConfig.COMMAND_LOAD));
		fileMenu.add(saveFileMenuItem = generate(saveFileMenuItem, "Save As", "Save an Excel File", ScreenConfig.COMMAND_SAVE));
		fileMenu.add(closeFileMenuItem = generate(closeFileMenuItem, "Close Script", "Close an Excel File", ScreenConfig.COMMAND_CLOSE));
		fileMenu.add(quitMenuItem = generate(quitMenuItem, "Quit", "Quit Application", ScreenConfig.COMMAND_QUIT));
		
		helpMenu = new JMenu("Help");
		helpMenu.add(generate(loadFileMenuItem, "About", "About CATYA Application", ScreenConfig.COMMAND_ABOUT));
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		
		((JFrame)window).setJMenuBar(menuBar);
	}
	
	public JMenuItem generate(JMenuItem menuItem, String title, String tip, String command){
		menuItem = new JMenuItem(title);
		menuItem.setToolTipText(tip);
		menuItem.setActionCommand(command);
		menuItem.addActionListener(this);
		return menuItem;
	}

	@Override
	public List<JButton> createToolbar() {
		List<JButton> toolbarButtons = new ArrayList<JButton>();
		
		toolbarStartButton = createToolbarButton(
				getPropertyFileString(ScreenConfig.ICO_START), 
				ScreenConfig.COMMAND_START, 
				getPropertyFileString(ScreenConfig.TOOLTIP_START), 
				getPropertyFileString(ScreenConfig.TOOLTIP_START));
		
		toolbarStartButton.addActionListener(this);
		
		toolbarPauseButton = createToolbarButton(
				getPropertyFileString(ScreenConfig.ICO_PAUSE), 
				ScreenConfig.COMMAND_PAUSE, 
				getPropertyFileString(ScreenConfig.TOOLTIP_PAUSE), 
				getPropertyFileString(ScreenConfig.TOOLTIP_PAUSE));
		
		toolbarPauseButton.addActionListener(this);
		
		toolbarStopButton = createToolbarButton(
				getPropertyFileString(ScreenConfig.ICO_STOP), 
				ScreenConfig.COMMAND_STOP, 
				getPropertyFileString(ScreenConfig.TOOLTIP_STOP), 
				getPropertyFileString(ScreenConfig.TOOLTIP_STOP));
		
		toolbarStopButton.addActionListener(this);
		
		toolbarButtons.add(toolbarStartButton);
		toolbarButtons.add(toolbarPauseButton);
		toolbarButtons.add(toolbarStopButton);
		
		return toolbarButtons;
	}

	@Override
	public JPanel createBody() {
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.X_AXIS));
		
		FWTableModel tableModel = new FWTableModel();
		
		String columnString = getPropertyFileString(
				ScreenConfig.TABLE_COLUMNS);
		String columns[] = columnString.split(",");
		int columnNum = columns.length/2;
		String columnNames [] = new String [columnNum];
		tableModel.setColumnNames(columnNames);
		table = new JTable(tableModel);
		table.setDefaultRenderer(Object.class, new FWTableCellRenderer());
		int i=0;
		int colIndex = 0;
		for (String column: columns) {
			column = column.trim();
			if (((i%2) == 0) &&
				(colIndex<columnNum)) {
				table.getTableHeader().getColumnModel().
						getColumn(colIndex++).setHeaderValue(column);
			} else {
				table.getTableHeader().getColumnModel().
						getColumn(colIndex-1).setPreferredWidth(
													Integer.parseInt(column));
				table.getTableHeader().getColumnModel().
						getColumn(colIndex-1).setMinWidth(40);
			}
			i++;
		}

		tableView = new JScrollPane(table);
		tableView.setMinimumSize(new Dimension(300, 100));
		tableView.setPreferredSize(new Dimension(700,500));
		
		///
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Sheet1", tableView);
		
		JPanel panel = new JPanel(false);
        JLabel filler = new JLabel("Not yet supported");
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
		
		tabbedPane.addTab("Sheet2", panel);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		
		bodyPanel.add(tabbedPane);
		
		
		
		ImageIcon img = new ImageIcon(getClass().getResource(
				getPropertyFileString(ScreenConfig.ICO_APP)));
		getFrame().setIconImage(img.getImage());
		
		return bodyPanel;
	}
	
	/**
	 * Generates a table view (like an excel sheet)
	 * @return
	 */
	public JScrollPane tableViewGenerator(String sheetName){
		JScrollPane scrlPane = new JScrollPane(tableGenerator());
		scrlPane.setMinimumSize(new Dimension(300, 100));
		scrlPane.setPreferredSize(new Dimension(700,500));
		return scrlPane;
	}
	
	public JTable tableGenerator(){
		FWTableModel tableModel = new FWTableModel();
		
		String columnString = getPropertyFileString(
				ScreenConfig.TABLE_COLUMNS);
		String columns[] = columnString.split(",");
		int columnNum = columns.length/2;
		String columnNames [] = new String [columnNum];
		tableModel.setColumnNames(columnNames);
		JTable table = new JTable(tableModel);
		table.setDefaultRenderer(Object.class, new FWTableCellRenderer());
		int i=0;
		int colIndex = 0;
		for (String column: columns) {
			column = column.trim();
			if (((i%2) == 0) &&
				(colIndex<columnNum)) {
				table.getTableHeader().getColumnModel().
						getColumn(colIndex++).setHeaderValue(column);
			} else {
				table.getTableHeader().getColumnModel().
						getColumn(colIndex-1).setPreferredWidth(
													Integer.parseInt(column));
				table.getTableHeader().getColumnModel().
						getColumn(colIndex-1).setMinWidth(40);
			}
			i++;
		}
		return table;
	}

	@Override
	public void closeWindow() {
		controller.closeWindow();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals(ScreenConfig.COMMAND_START)) {
			new Thread(()->{controller.execute();}).start();
		} else if (command.equals(ScreenConfig.COMMAND_STOP)) {
			new Thread(()->{controller.stopExecute();}).start();
		} else if (command.equals(ScreenConfig.COMMAND_PAUSE)) {
			new Thread(()->{controller.pauseExecute();}).start();
		} else if (command.equals(ScreenConfig.COMMAND_LOAD)) {
			LOG.info("Start Open Dialog.");
			String file = controller.getFile(ScreenConfig.COMMAND_LOAD);
			new Thread(()->{
				if(null != file){
					controller.reset();
					controller.loadFile(file);	
				}
			}).start();
		} else if (ScreenConfig.COMMAND_SAVE.equals(command)){
			LOG.info("Start Save Dialog");
			String file = controller.getFile(ScreenConfig.COMMAND_SAVE);
			new Thread(()->{
				controller.saveFile(file);
			}).start();
		} else if (ScreenConfig.COMMAND_CLOSE.equals(command)) {
			new Thread(()-> {
				controller.closeFile();
			}).start();
		} else if (ScreenConfig.COMMAND_QUIT.equals(command)) {
			controller.quit();
		} else if (ScreenConfig.COMMAND_ABOUT.equals(command)) {
			controller.about();
		}
	}
	
	public void initScreen() {
		
		WaitDialog waitDialog = new WaitDialog(this, "Initializing screen");
		
		IWaitDialogTask task = ()-> {
			int numTableRows = Integer.parseInt(getPropertyFileString(ScreenConfig.TABLE_ROWS));
			model.initModel(numTableRows);	
		};
		
		waitDialog.executeTask(task);
	}

	public void setController(CATYAController controller) {
		this.controller = controller;
	}

	public CATYAController getController() {
		return controller;
	}
	
	public void setModel(CATYAModel model) {
		this.model = model;
	}

	public CATYAModel getModel() {
		return model;
	}

	public void setBodyPanel(JPanel bodyPanel) {
		this.bodyPanel = bodyPanel;
	}

	public JPanel getBodyPanel() {
		return bodyPanel;
	}
	
	public void updateSendState(boolean sendStart) {
		isStarted = sendStart;
	}
	
	public ImageIcon[] getImageIcon() {
		return toggleSendIcon;
	}
	
	public JTable getTable() {
		return table;
	}
	
	public JButton getStartButton() {
		return toolbarStartButton;
	}
	
	public JButton getStopButton() {
		return toolbarStopButton;
	}
	
	public JButton getPauseButton() {
		return toolbarPauseButton;
	}
	
	public JMenu getFileMenu() {
		return fileMenu;
	}
	
	public JMenuItem getLoadMenuItem() {
		return loadFileMenuItem;
	}
	
	public JMenuItem getSaveFileMenuItem() {
		return saveFileMenuItem;
	}
	
	public JMenuItem getCloseFileMenuItem() {
		return closeFileMenuItem;
	}
	
	public JMenuItem getQuitMenuItem() {
		return quitMenuItem;
	}
}
