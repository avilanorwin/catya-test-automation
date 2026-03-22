/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.view.components;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;
import com.catya.ui.view.base.FWBaseScreen;

public class WaitDialog extends JDialog{
	private JLabel progressLabel;
	private String progressDisplay;
	private static final long serialVersionUID = -3756913985496127237L;
	private volatile boolean done;
	private int progressIndex = 0;
	private String progress[] = {
    		".", ". .", ". . .", ". . . .", ". . . . ."
    };
    private Window parent;
    
    private static Logger LOG = Logger.getLogger(WaitDialog.class.getName());
    
    public WaitDialog(FWBaseScreen pParent, String pProgressDisplay) {
    	progressDisplay = pProgressDisplay;
    	
    	if (pParent.getFrame() == null) {
    		LOG.info("Parent is a Dialog.");
	    	while(!pParent.getDialog().isVisible()) {
	    		try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					LOG.severe(e.getMessage());
				}
	    	}
	    	parent = pParent.getDialog();
	    	setTitle(pParent.getDialog().getTitle());    	
    	} else {
    		LOG.info("Parent is a Frame.");
    		while(!pParent.getFrame().isVisible()) {
	    		try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					LOG.severe(e.getMessage());
				}
	    	}
    		parent = pParent.getFrame();
    		setTitle(pParent.getFrame().getTitle());
    	}
    	
    	done = false;
    	setModal(true);

    	JPanel progressPane = new JPanel();
    	progressLabel = new JLabel(progress[0]);
    	progressPane.setLayout(new BoxLayout(progressPane, BoxLayout.X_AXIS));
    	progressPane.add(Box.createRigidArea(new Dimension(40, 20)));
    	progressPane.add(new JLabel(progressDisplay));
    	progressPane.add(progressLabel);
    	add(progressPane);
    	int width = (progressPane.getPreferredSize().width + 90);
    	setSize(new Dimension(width, 100));
    	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    	if (pParent.getFrame() == null) {
    		setLocationRelativeTo(pParent.getDialog());
    	} else {
    		setLocationRelativeTo(pParent.getFrame());
    	}
	
	
    	addWindowListener(new WindowAdapter() {
    		//--------------------------------------------
    		//
    		// Show progress when window is made visible
    		//
    		//--------------------------------------------
    		public void windowOpened(WindowEvent e) {
    			new Thread( new Runnable() {
    				@Override
    				public void run() {
    					while (!done) {
    						try {
    							// Delay for each progress
    							Thread.sleep(100);
    							SwingUtilities.invokeLater(new Runnable() {
    								@Override
    								public void run() {
    									if (!parent.isVisible()) {
    										parent.setVisible(true);
    										parent.toFront();
    									}
    									progressLabel.setText(progress[progressIndex++]);
    									if (progressIndex == 5) progressIndex=0;
    								}
    							});
    						} catch (InterruptedException e) {
    							LOG.severe(e.getMessage());
    						}
    					}
    					dispose();
    				}
    			}).start();
    		}
    		
		});
    	
    	new Thread(new Runnable() {
			@Override
			public void run() {
		    	setVisible(true);	
			}
		}).start();
    }
    
    public void close() {
    	done = true;
    }

	public void setProgressDisplay(String progressDisplay) {
		this.progressDisplay = progressDisplay;
	}

	public String getProgressDisplay() {
		return progressDisplay;
	}
	
	public void executeTask(IWaitDialogTask task) {
		task.execute();
		close();
	}
}
