/*
  File: InductionSettingsDialog.java

  Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  Dout of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.cyni.internal;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.task.DynamicTaskFactoryProvisioner;
import org.cytoscape.property.CyProperty;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.cyni.*;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;


/**
 *
 * The InductionDialog is a dialog that provides an interface into all of the
 * various settings for Induction algorithms.  Each CyInductionAlgorithm must return a single
 * JPanel that provides all of its settings.
 */
public class CyniDialog extends JDialog implements ActionListener {
	private final static long serialVersionUID = 1202339874277105L;
	private TaskFactory currentCyni = null;

	// Dialog components
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JComboBox algorithmSelector; // Which algorithm we're using
	private JComboBox tableSelector; // Which algorithm we're using
	private JPanel algorithmPanel; // The panel this algorithm uses
	private JButton executeButton;

	private CyCyniAlgorithmManager cyCyniAlgorithmManager;
	private CyTableManager tableManager;
	private CySwingApplication desktop;
	private CyApplicationManager appMgr;
	private PanelTaskManager taskManager;
	private CyLayoutAlgorithmManager layoutManager;
	private CyProperty cytoscapePropertiesServiceRef;
	private DynamicTaskFactoryProvisioner factoryProvisioner;
	private boolean initialized;
	private Integer availableAlgorithms;
	private Integer availableTables;
	private CyCyniAlgorithm newCyni;
	private CyTable oldTable;
	private CyNetworkFactory netFactory;
	private CyNetworkViewFactory viewFactory;
	private CyNetworkManager netMgr;
	private CyNetworkViewManager viewMgr;
	private VisualMappingManager vmMgr;
	private CyCyniMetricsManager metricsManager;
	private CyNetworkTableManager netTableMgr;
	private CyRootNetworkManager rootNetMgr;
	private CyniCategory category;
	private String executeButtonName;
	private String selectPanelName;
	/**
	 *  Store the cyni context.
	 */
	private Map<CyCyniAlgorithm, Object> contextMap;

	/**
	 * Creates a new CyniDialog object.
	 */
	public CyniDialog(final String PanelName,
								final CyniCategory category,
								final CyCyniAlgorithmManager cyCyniAlgorithmManager, 
								final CyNetworkFactory netFactory,
								final  CyNetworkViewFactory networkViewFactory,
								final CyNetworkManager networkManager,
								final CyNetworkTableManager netTableMgr,
								final CyRootNetworkManager rootNetMgr,
								final CyNetworkViewManager networkViewManager,
								final CyTableManager tableManager,
	                            final CySwingApplication desktop,
	                            final CyApplicationManager appMgr,
	                            final PanelTaskManager taskManager,
	                            final CyLayoutAlgorithmManager layoutManager,
	                            final CyCyniMetricsManager metricsManager,
	                            final VisualMappingManager vmMgr,
	                            final CyProperty cytoscapePropertiesServiceRef,
	                            DynamicTaskFactoryProvisioner factoryProvisioner)
	{
		super(desktop.getJFrame(), PanelName, false);
		
		this.cyCyniAlgorithmManager = cyCyniAlgorithmManager;
		this.desktop = desktop;
		this.appMgr = appMgr;
		this.tableManager = tableManager;
		this.taskManager = taskManager;
		this.layoutManager = layoutManager;
		this.netFactory = netFactory;
		this.netMgr = networkManager;
		this.viewFactory = networkViewFactory;
		this.viewMgr = networkViewManager;
		this.netTableMgr = netTableMgr;
		this.rootNetMgr = rootNetMgr;
		this.vmMgr = vmMgr;
		this.metricsManager = metricsManager;
		this.cytoscapePropertiesServiceRef = cytoscapePropertiesServiceRef;
		this.factoryProvisioner = factoryProvisioner;
		this.contextMap = new HashMap<CyCyniAlgorithm, Object>();
		this.category = category;
		oldTable = null;
		availableAlgorithms = 0;
		availableTables = 0;
		switch(this.category){
		case  INDUCTION:
			executeButtonName = "Execute Induction";
			selectPanelName = "Induction Algorithm";
			break;
		case IMPUTATION:
			executeButtonName = "Execute Imputation";
			selectPanelName = "Imputation Algorithm";
			break;
		case DISCRETIZATION:
			executeButtonName = "Execute Discretization";
			selectPanelName = "Discretization Algorithm";
			break;
		default:
			executeButtonName = "Execute";
			selectPanelName = "CyNi Algorithm";
		}
		initializeOnce(); // Initialize the components we only do once

		initComponents();
		
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		String command = e.getActionCommand();
		
		System.out.println("induction action performed: " + command);

		if (command.equals("done"))
			setVisible(false);
		else if (command.equals("execute")) {
			if(currentCyni.isReady())
			{
				taskManager.execute(currentCyni.createTaskIterator());
			}
		} else {
			// OK, initialize and display
			if (isVisible()) {
				requestFocus();
			} else {
				if(getNumberTablesAvailable() != availableTables || cyCyniAlgorithmManager.getAllCyniAlgorithms(category).size() != availableAlgorithms)
				{
					initialized = false;
				}
				if (!initialized) {
					initialize();
					setLocationRelativeTo(desktop.getJFrame());
					pack();
				}
				
				setVisible(true);
				initialized = true;
			}
		}
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;


        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(this.mainPanel, gridBagConstraints);

        pack();
    }// </editor-fold>
    
    private Integer getNumberTablesAvailable() {
    	Integer number = 0;
    	
    	for ( CyTable table : tableManager.getGlobalTables()) 
		{
			if(table.isPublic())
			{
				number++;
			}
		}
    	
    	if(category == CyniCategory.INDUCTION)
    	{
	    	number = number + netMgr.getNetworkSet().size();
    	}
    	else
    	{
    		number = number + netMgr.getNetworkSet().size()*3;
    	}
		
		return number;
    }
	
	private void initializeOnce() {
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		// Create a panel for the list of algorithms
		JPanel algorithmSelectorPanel = new JPanel();
		
		algorithmSelector = new JComboBox();
		algorithmSelector.addActionListener(new AlgorithmActionListener());
		
		algorithmSelectorPanel.add(algorithmSelector);

		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder, selectPanelName);
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		algorithmSelectorPanel.setBorder(titleBorder);
		
		mainPanel.add(algorithmSelectorPanel);
		
		JPanel tableSelectorPanel = new JPanel();
		tableSelector = new JComboBox();
		tableSelector.addActionListener(new AlgorithmActionListener());
		
		tableSelectorPanel.add(tableSelector);

		selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		titleBorder = BorderFactory.createTitledBorder(selBorder, "Table Data");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		tableSelectorPanel.setBorder(titleBorder);
		
		mainPanel.add(tableSelectorPanel);

		// Create a panel for algorithm's content
		this.algorithmPanel = new JPanel();
		
		mainPanel.add(algorithmPanel);

		// Create a panel for our button box
		this.buttonBox = new JPanel();

		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		executeButton = new JButton(executeButtonName);
		executeButton.setActionCommand("execute");
		executeButton.addActionListener(this);
		executeButton.setEnabled(false);

		buttonBox.add(executeButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		mainPanel.add(buttonBox);
//		setContentPane(mainPanel);
	}

	private void initialize() {
		
		// Populate the algorithm selector
		algorithmPanel.removeAll();
		algorithmSelector.removeAllItems();
		availableAlgorithms = 0;
		availableTables = 0;
		
		// Add the "instructions"
		algorithmSelector.setRenderer(new MyItemRenderer());
		algorithmSelector.addItem("Select algorithm to view settings");

		for ( CyCyniAlgorithm algo : cyCyniAlgorithmManager.getAllCyniAlgorithms(category)) 
		{
			algorithmSelector.addItem(algo);
			availableAlgorithms++;
		}
		
		// Populate the table selector
		tableSelector.removeAllItems();

		// Add the "instructions"
		tableSelector.setRenderer(new MyItemRenderer());
		tableSelector.addItem("Select table data to apply algorithm");
		
		for ( CyTable table : tableManager.getGlobalTables()) 
		{
			if(table.isPublic())
			{
				tableSelector.addItem(table);
				availableTables++;
			}
		}
		
		if(category == CyniCategory.INDUCTION)
    	{
			for ( CyNetwork network : netMgr.getNetworkSet()) 
			{
				tableSelector.addItem(network.getDefaultNodeTable());
				availableTables++;
			}
    	}
		else
		{
			for ( CyNetwork network : netMgr.getNetworkSet()) 
			{
				tableSelector.addItem(network.getDefaultNodeTable());
				tableSelector.addItem(network.getDefaultEdgeTable());
				tableSelector.addItem(network.getDefaultNetworkTable());
				availableTables =+ 3;
			}
		}
		
	}

	private class AlgorithmActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object o = algorithmSelector.getSelectedItem();
			
			Object table = tableSelector.getSelectedItem();
			// if it's a string, that means it's the instructions
			if (!(o instanceof String) && !(table instanceof String) && o != null) {
				newCyni = (CyCyniAlgorithm)o;
				CyTable newTable = (CyTable) table;
				
				//Checking if the context has already been charged, if so there is no need to do it again
				Object context = contextMap.get(newCyni);
				executeButton.setEnabled(true);
				if (context == null || !oldTable.equals(table))
				{
					if(contextMap.containsKey(newCyni))
					{
						contextMap.remove(newCyni);
					}
					context =  newCyni.createCyniContext(newTable, metricsManager,null,null);
					contextMap.put(newCyni, context);
					oldTable = (CyTable) table;
				}

				TaskFactory factory = wrapWithContext(newCyni, context);

				JPanel tunablePanel = taskManager.getConfiguration(factory, context);

				if (tunablePanel == null){
					JOptionPane.showMessageDialog(CyniDialog.this, "Can not change setting for this algorithm, because tunable info is not available!", "Warning", JOptionPane.WARNING_MESSAGE);
					algorithmPanel.removeAll();
				
				}
				else {
					algorithmPanel.removeAll();
					algorithmPanel.add(tunablePanel);	
					
				}
				currentCyni = factory;
				CyniDialog.this.pack();
			}
		}
	}

	private  TaskFactory wrapWithContext(final CyCyniAlgorithm cyniAlgorithm, final Object tunableContext) {
		return new TaskFactory() {
			@Override
			public boolean isReady() {
				return cyniAlgorithm.isReady(tunableContext);
			}
			
			@Override
			public TaskIterator createTaskIterator() {
				return cyniAlgorithm.createTaskIterator(tunableContext, netFactory,viewFactory,netMgr,netTableMgr, rootNetMgr,vmMgr,viewMgr, layoutManager, metricsManager);
			}
		};
	}
	
	private class MyItemRenderer extends JLabel implements ListCellRenderer {
		private final static long serialVersionUID = 1202339874266209L;
		public MyItemRenderer() {
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
		                                              boolean isSelected, boolean cellHasFocus) {
			// If this is a String, we don't want to allow selection.  If this is
			// index 0, we want to set the font 
			Font f = getFont();

			if (value.getClass() == String.class) {
				setFont(f.deriveFont(Font.PLAIN));
				setText((String) value);
				setHorizontalAlignment(CENTER);
				setForeground(Color.GRAY);
				setEnabled(false);
			} else {
				setForeground(list.getForeground());
				setHorizontalAlignment(LEFT);
				setEnabled(true);

				if (isSelected) {
					setFont(f.deriveFont(Font.BOLD));
				} else {
					setFont(f.deriveFont(Font.PLAIN));
				}

				setText(value.toString());
			}

			return this;
		}
	}
}
