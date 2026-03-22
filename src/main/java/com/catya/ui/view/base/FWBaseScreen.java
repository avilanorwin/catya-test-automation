/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.view.base;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.catya.ui.config.CommonConfig;
import com.catya.ui.config.ScreenConfig;

/**
 * Screen Framework
 * @author Norwin
 *
 */
public abstract class FWBaseScreen implements WindowListener{
	
	public static enum WindowType {
		WINDOW_TYPE_FRAME,
		WINDOW_TYPE_DIALOG
	};
	Object locked = new Object();
	protected boolean isStarted = false;
	protected JFrame parent;
	protected Window window;
	private WindowType windowType;
	
	//------------------------------------------
	//
	//  Toolbars
	//
	//------------------------------------------
	private JPanel		topPanel;
	private JPanel		toolbarPanel;
	private JToolBar	toolbar;
	
	//------------------------------------------
	//
	// Main body panel
	//
	//------------------------------------------
	JPanel				bodyPanel;
	
	//------------------------------------------
	//
	//  Property File
	//
	//------------------------------------------
	private ResourceBundle resourceBundle;
	protected String propertyFile;
	
	/**
	 * Constructor
	 */
	public FWBaseScreen() {
		this(CommonConfig.PROPERTY_FILE, 
				WindowType.WINDOW_TYPE_FRAME);
	}
	
	/**
	 * Constructor with custom property file
	 */
	public FWBaseScreen (String propertyFile, WindowType windowType) {
		this.propertyFile = propertyFile;
		this.windowType = windowType;
		resourceBundle = ResourceBundle.getBundle(this.propertyFile);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					createUI();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public abstract void createMenuBar();
	
	/**
	 * Derived class adds toolbar buttons in this function
	 * @return list of toolbar buttons.
	 */
	public abstract List<JButton> createToolbar();
	
	/**
	 * Derived class creates body UI in this function
	 * @return top panel of derived class
	 */
	public abstract JPanel createBody();
	
	public abstract void closeWindow();
	
	public void setWindowType(WindowType type) {
		windowType = type;
	}
	
	/**
	 * Create UI Components and screen startup
	 */
	public void createUI() {
		if (windowType == WindowType.WINDOW_TYPE_FRAME) {
			window = new JFrame();
			((JFrame)window).setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			window = new JDialog();	
			((JDialog)window).setModal(true);
		}
		window.addWindowListener(this);
		//------------------------------------------
		//
		// Setup top panel
		//
		//------------------------------------------
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		
		//------------------------------------------
		//
		// Create and add menu panel
		//
		//------------------------------------------
		createMenuBar();
		
		//------------------------------------------
		//
		// Create and add toolbar panel
		//
		//------------------------------------------
		toolbarPanel = createToolbarPanel();
		if (toolbarPanel != null) {
			topPanel.add(toolbarPanel);
		}
		
		//------------------------------------------
		//
		// Create and add body panel
		//
		//------------------------------------------
		bodyPanel = createBody();
		if (bodyPanel != null) {
			bodyPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.GRAY),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));
			topPanel.add(bodyPanel);
		}
		
		//------------------------------------------
		//
		// Add top Panel to screen panel
		//
		//------------------------------------------
		String windowTitle = resourceBundle.getString(
				ScreenConfig.WINDOW_TITLE);
		if (windowType == WindowType.WINDOW_TYPE_FRAME) {
			getFrame().setTitle(windowTitle);
			getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} else {
			getDialog().setTitle(windowTitle);
			getDialog().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			getDialog().setResizable(false);
		}
		//------------------------------------------
		//
		// Display window
		//
		//------------------------------------------
		window.add(topPanel);
		window.pack();
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
		window.setLocation(x, y);
		window.setVisible(true);
	}
	
	/**
	 * Toolbar panel
	 * @return Toolbar panel
	 */
	private JPanel createToolbarPanel() {
		JPanel toolbarContainer = new JPanel();
		toolbarContainer.setLayout(new BoxLayout(toolbarContainer, 
				BoxLayout.X_AXIS));
		toolbarContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		//------------------------------------------
		//
		//  Create Toolbar
		//
		//------------------------------------------
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		
		//------------------------------------------
		//
		// Create Toolbar buttons
		//
		//------------------------------------------
		List<JButton> toolbarButtonList = createToolbar();
		
		if (toolbarButtonList == null) {
			return null;
		}
		
		for (JButton toolbarButton: toolbarButtonList) {
			toolbar.add(toolbarButton);
			toolbar.add(Box.createRigidArea(new Dimension(5,5)));
		}
		
		//------------------------------------------
		//
		// Add the toolbar buttons
		//
		//------------------------------------------
		toolbarContainer.add(toolbar);
		toolbarContainer.add(Box.createHorizontalGlue());
		return toolbarContainer;
	}
	
	/**
	 * Helper function to create the toolbar buttons.
	 * @param image
	 * @param actionCommand
	 * @param tooltip
	 * @param altText
	 * @return
	 */
	public JButton createToolbarButton(String image, 
			String actionCommand,
			String tooltip,
			String altText) {
		
		URL imageUrl = FWBaseScreen.class.getResource(image);
		
		JButton toolbarButton= new JButton();
		
		if (imageUrl != null) {
			toolbarButton.setIcon(new ImageIcon(imageUrl, altText));
		} else {
			System.err.println("Image not found...");
		}
		
		toolbarButton.setToolTipText(tooltip);
		toolbarButton.setActionCommand(actionCommand);
		return toolbarButton;
		
	}
	
	public String getPropertyFileString(String key) {
		return resourceBundle.getString(key);
	}
	
	public JFrame getFrame() {
		if (window instanceof JFrame)
			return (JFrame)window;
		return null;
	}
	
	public JDialog getDialog() {
		if (window instanceof JDialog)
			return (JDialog)window;
		return null;
	}
	
	public boolean isStarted() {
		return isStarted == true;
	}
	
//	public void start() {
//		window.setVisible(true);
//	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		closeWindow();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}
	
	public void updateUI(Runnable r) {
		SwingUtilities.invokeLater(r);
	}
}
