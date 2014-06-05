/*
 * #%L
 * Cyni Implementation (cyni-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.cytoscape.cyni.internal;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;import java.awt.Font;
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
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
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
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.ColumnNameChangedListener;
import org.cytoscape.model.events.ColumnNameChangedEvent;
import org.cytoscape.model.events.TableAddedEvent;
import org.cytoscape.model.events.TableAddedListener;
import org.cytoscape.model.events.TableAboutToBeDeletedEvent;
import org.cytoscape.model.events.TableAboutToBeDeletedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
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
public class CyniPanel extends JPanel implements ColumnCreatedListener, ColumnDeletedListener,ColumnNameChangedListener, 
										ActionListener, TableAddedListener, TableAboutToBeDeletedListener,NetworkAddedListener,
										CyniAlgorithmDeletedListener,CyniAlgorithmAddedListener, NetworkAboutToBeDestroyedListener{
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
	private boolean initialized;
	private CyCyniAlgorithm newCyni;
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
	private Map<CyCyniAlgorithm, Map<CyTable, CyniAlgorithmContext>> contextMap;
	
	private static final Dimension DEF_COMBOBOX_SIZE = new Dimension(3000, 30);

	/**
	 * Creates a new CyniDialog object.
	 */
	public CyniPanel(final CyniCategory category,
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
	                    final CyServiceRegistrar serviceRegistrar)
	{
		//super(desktop.getJFrame(), PanelName, false);
		
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
		this.contextMap = new HashMap<CyCyniAlgorithm, Map<CyTable, CyniAlgorithmContext>>();
		this.category = category;
		switch(this.category){
		case  INDUCTION:
			executeButtonName = "Infer Network";
			selectPanelName = "Inference Algorithm";
			setName("Infer Network");
			break;
		case IMPUTATION:
			executeButtonName = "Impute Missing Data";
			selectPanelName = "Imputation Algorithm";
			setName("Impute Data");
			break;
		case DISCRETIZATION:
			executeButtonName = "Discretize Data";
			selectPanelName = "Discretization Algorithm";
			setName("Discretize Data");
			break;
		default:
			executeButtonName = "Execute";
			selectPanelName = "CyNi Algorithm";
			setName("Cyni Algorithm");
		}
		initializeOnce(); // Initialize the components we only do once
		serviceRegistrar.registerService(this, ColumnCreatedListener.class, new Properties());
		serviceRegistrar.registerService(this, ColumnDeletedListener.class, new Properties());
		serviceRegistrar.registerService(this, ColumnNameChangedListener.class, new Properties());
		serviceRegistrar.registerService(this, TableAddedListener.class, new Properties());
		serviceRegistrar.registerService(this, TableAboutToBeDeletedListener.class, new Properties());
		serviceRegistrar.registerService(this, NetworkAddedListener.class, new Properties());
		serviceRegistrar.registerService(this, CyniAlgorithmDeletedListener.class, new Properties());
		serviceRegistrar.registerService(this, CyniAlgorithmAddedListener.class, new Properties());
		serviceRegistrar.registerService(this, NetworkAboutToBeDestroyedListener.class, new Properties());
		
		
		initialize();

		//initComponents();
		
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		String command = e.getActionCommand();
		
		if (command.equals("execute")) {
			CyniAlgorithmContext context = contextMap.get(newCyni).get((CyTable)tableSelector.getSelectedItem());
			if(context.contextHasOwnSwingPanel())
			{
				if(context.contextContentValid())
					taskManager.execute(currentCyni.createTaskIterator());
			}else
			{
				if (taskManager.validateAndApplyTunables(context)) {
						taskManager.execute(currentCyni.createTaskIterator());
				}
			}
		} 
		
	}
	
	@Override
	public void handleEvent(final ColumnCreatedEvent e) {
		for( Map<CyTable, CyniAlgorithmContext> mapTable : contextMap.values())
		{
			if(mapTable.containsKey(e.getSource()))
			{
				if(category == CyniCategory.DISCRETIZATION && e.getSource().getColumn(e.getColumnName()).getType() == String.class)
					return;
				mapTable.remove(e.getSource());
				if((tableSelector.getSelectedItem() instanceof CyTable) && e.getSource().equals(tableSelector.getSelectedItem()))
					initialize();
			}
		}
	}

	@Override
	public void handleEvent(final ColumnDeletedEvent e) {
		for( Map<CyTable, CyniAlgorithmContext> mapTable : contextMap.values())
		{
			if(mapTable.containsKey(e.getSource()))
			{
				if(category == CyniCategory.DISCRETIZATION && e.getSource().getColumn(e.getColumnName()).getType() == String.class)
					return;
				mapTable.remove(e.getSource());
				if((tableSelector.getSelectedItem() instanceof CyTable) && e.getSource().equals(tableSelector.getSelectedItem()))
					initialize();
			}
		}
	}
	
	@Override
	public void handleEvent(final ColumnNameChangedEvent e) {
		for( Map<CyTable, CyniAlgorithmContext> mapTable : contextMap.values())
		{
			if(mapTable.containsKey(e.getSource()))
			{
				if(category == CyniCategory.DISCRETIZATION && e.getSource().getColumn(e.getNewColumnName()).getType() == String.class)
					return;
				mapTable.remove(e.getSource());
				if((tableSelector.getSelectedItem() instanceof CyTable) && e.getSource().equals(tableSelector.getSelectedItem()))
					initialize();
			}
		}
	}
	
	@Override
	public void handleEvent(final TableAddedEvent e) {
		
		if(isTableGlobal( e.getTable()))
		{
			tableSelector.addItem(e.getTable());
			//repaint();
		}
		
	}
	
	@Override
	public void handleEvent(final TableAboutToBeDeletedEvent e) {
		if(!e.getTable().isPublic())
			return;
		
		for( Map<CyTable, CyniAlgorithmContext> mapTable : contextMap.values())
		{
			if(mapTable.containsKey(e.getTable()))
				mapTable.remove(e.getTable());
			
		}
		
		if((tableSelector.getSelectedItem() instanceof CyTable) && e.getTable().equals(tableSelector.getSelectedItem()))
			initialize();
		else
		{
			
			//repaint();
		}
		tableSelector.removeItem(e.getTable());
	}
	
	@Override
	public void handleEvent(final NetworkAddedEvent e) {
		
		tableSelector.addItem(e.getNetwork().getDefaultNodeTable());
		if(category != CyniCategory.INDUCTION)
			tableSelector.addItem(e.getNetwork().getDefaultEdgeTable());
		//repaint();
	}
	
	@Override
	public void handleEvent(final NetworkAboutToBeDestroyedEvent e) {
		
		for( Map<CyTable, CyniAlgorithmContext> mapTable : contextMap.values())
		{
			if(mapTable.containsKey(e.getNetwork().getDefaultNodeTable()))
				mapTable.remove(e.getNetwork().getDefaultNodeTable());
			
			if(mapTable.containsKey(e.getNetwork().getDefaultEdgeTable()))
				mapTable.remove(e.getNetwork().getDefaultEdgeTable());
			
		}
		
		if(e.getNetwork().getDefaultEdgeTable().equals(tableSelector.getSelectedItem()) || e.getNetwork().getDefaultNodeTable().equals(tableSelector.getSelectedItem()))
			initialize();
		else
		{
			tableSelector.removeItem(e.getNetwork().getDefaultNodeTable());
			if(category != CyniCategory.INDUCTION)
				tableSelector.removeItem(e.getNetwork().getDefaultEdgeTable());
			//repaint();
		}
	}
	
	@Override
	public void handleEvent(final CyniAlgorithmAddedEvent e) {
		
		if(e.getCyniAlgorithm().getCategory() != category)
			return;
		
		if(contextMap.containsKey(e.getCyniAlgorithm()))
		{
			contextMap.remove(e.getCyniAlgorithm());
		}
		
		for (int index = 0; index < algorithmSelector.getItemCount() ; index++) 
		{
			if (!(algorithmSelector.getItemAt(index) instanceof CyCyniAlgorithm))
				continue;
			if (e.getCyniAlgorithm().getName().matches(((CyCyniAlgorithm)algorithmSelector.getItemAt(index)).getName())) 
			{
				if((algorithmSelector.getSelectedItem() instanceof CyCyniAlgorithm) && e.getCyniAlgorithm().getName().matches(((CyCyniAlgorithm)algorithmSelector.getSelectedItem()).getName()))
				{
					initialize();
					return;
				}
				else
					algorithmSelector.removeItemAt(index);
					
				break;
			}
		}
		algorithmSelector.addItem(e.getCyniAlgorithm());
		
	}
	
	@Override
	public void handleEvent(final CyniAlgorithmDeletedEvent e) {
		
		if(e.getCyniAlgorithm().getCategory() != category)
			return;
		
		if(contextMap.containsKey(e.getCyniAlgorithm()))
		{
			contextMap.remove(e.getCyniAlgorithm());
		}
		
		for (int index = 0; index < algorithmSelector.getItemCount() ; index++) 
		{
			if (e.getCyniAlgorithm().equals(algorithmSelector.getItemAt(index))) 
			{
				if((algorithmSelector.getSelectedItem() instanceof CyCyniAlgorithm) && e.getCyniAlgorithm().equals((CyCyniAlgorithm)algorithmSelector.getSelectedItem()))
					initialize();
				else
					algorithmSelector.removeItemAt(index);
					//revalidate();
				return;
			}
		}
	}
    
    private boolean isTableGlobal(CyTable table)
    {
    	
		if(table.isPublic() && tableManager.getGlobalTables().contains(table))
		{
			return true;
		}
		return false;
    }
	
	private void initializeOnce() {
		

		setPreferredSize(new Dimension(420, getMinimumSize().height));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// Create a panel for the list of algorithms
		JPanel algorithmSelectorPanel = new JPanel();
		
		algorithmSelectorPanel.setLayout( new BoxLayout(algorithmSelectorPanel, BoxLayout.PAGE_AXIS));
		algorithmSelector = new JComboBox();
		algorithmSelector.addActionListener(new AlgorithmActionListener());
		algorithmSelector.setMaximumSize(DEF_COMBOBOX_SIZE);
		
		algorithmSelectorPanel.add(algorithmSelector);

		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder, selectPanelName);
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		algorithmSelectorPanel.setBorder(titleBorder);
		
		//algorithmSelectorPanel.setMaximumSize(DEF_COMBOBOX_SIZE);
		
		add(algorithmSelectorPanel);
		
		JPanel tableSelectorPanel = new JPanel();
		
		tableSelectorPanel.setLayout(new BoxLayout(tableSelectorPanel, BoxLayout.PAGE_AXIS));
		tableSelector = new JComboBox();
		tableSelector.addActionListener(new AlgorithmActionListener());
		tableSelector.setMaximumSize(DEF_COMBOBOX_SIZE);
		
		tableSelectorPanel.add(tableSelector);

		selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		titleBorder = BorderFactory.createTitledBorder(selBorder, "Table Data");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		tableSelectorPanel.setBorder(titleBorder);
		
		//tableSelectorPanel.setMaximumSize(DEF_COMBOBOX_SIZE);
		
		add(tableSelectorPanel);

		// Create a panel for algorithm's content
		this.algorithmPanel = new JPanel();
		
		//algorithmPanel.setLayout( new GridLayout(0,1));
		selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		titleBorder = BorderFactory.createTitledBorder(selBorder, "Cyni Algorithm Settings");
		titleBorder.setTitleJustification(TitledBorder.CENTER);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		algorithmPanel.setBorder(titleBorder);
		JScrollPane scrollPane = new JScrollPane(algorithmPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add(scrollPane);

		// Create a panel for our button box
		this.buttonBox = new JPanel();

		buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
		executeButton = new JButton("Apply");
		executeButton.setActionCommand("execute");
		executeButton.addActionListener(this);
		executeButton.setEnabled(false);
		executeButton.setAlignmentX(CENTER_ALIGNMENT);

		buttonBox.add(executeButton);
		//buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		add(buttonBox);
//		setContentPane(mainPanel);
	}

	private void initialize() {
		
		// Populate the algorithm selector
		algorithmPanel.removeAll();
		algorithmSelector.removeAllItems();
		executeButton.setEnabled(false);
		
		// Add the "instructions"
		algorithmSelector.setRenderer(new MyItemRenderer());
		algorithmSelector.addItem("Select algorithm to view settings");

		for ( CyCyniAlgorithm algo : cyCyniAlgorithmManager.getAllCyniAlgorithms(category)) 
			algorithmSelector.addItem(algo);

		
		// Populate the table selector
		tableSelector.removeAllItems();

		// Add the "instructions"
		tableSelector.setRenderer(new MyItemRenderer());
		tableSelector.addItem("Select table data to apply algorithm");
		
		for ( CyTable table : tableManager.getGlobalTables()) 
		{
			if(table.isPublic())
				tableSelector.addItem(table);
		}
		
		
		for ( CyNetwork network : netMgr.getNetworkSet()) 
		{
			tableSelector.addItem(network.getDefaultNodeTable());
			if(category != CyniCategory.INDUCTION)
				tableSelector.addItem(network.getDefaultEdgeTable());
			
		}
		
		repaint();
	}

	private class AlgorithmActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Component tunablePanel;
			boolean firstTime = false;
			Object o = algorithmSelector.getSelectedItem();
			
			Object table = tableSelector.getSelectedItem();
			// if it's a string, that means it's the instructions
			if (!(o instanceof String) && !(table instanceof String) && o != null) {
				CyniAlgorithmContext context = null;
				newCyni = (CyCyniAlgorithm)o;
				CyTable newTable = (CyTable) table;
				
				//Checking if the context has already been charged, if so there is no need to do it again
				if(contextMap.get(newCyni) == null)
				{
					firstTime = true;
				}
				else
				{
					context = contextMap.get(newCyni).get(newTable);
					if(context ==null)
						firstTime = true;
						
				}
				
				if (firstTime)
				{
					
					context =  newCyni.createCyniContext(newTable, metricsManager,null,null);
					if(context != null)
						context.setParentSwingComponent(getParent());
					if(contextMap.get(newCyni) == null)
						contextMap.put(newCyni, new HashMap<CyTable, CyniAlgorithmContext>());
					contextMap.get(newCyni).put(newTable, context);
				}
				executeButton.setEnabled(true);
				TaskFactory factory = wrapWithContext(newCyni, newTable,context);

				if(context.contextHasOwnSwingPanel())
					tunablePanel = context.getContextSwingPanel();
				else
					tunablePanel = taskManager.getConfiguration(factory, context);

				if (tunablePanel == null){
					JOptionPane.showMessageDialog(CyniPanel.this, "Can not change setting for this algorithm, because context info is not available!", "Warning", JOptionPane.WARNING_MESSAGE);
					algorithmPanel.removeAll();
				
				}
				else {
					algorithmPanel.removeAll();
					algorithmPanel.add(tunablePanel);	
				}
				currentCyni = factory;
				//CyniPanel.this.pack();
				
				validate();
				repaint();
			}
			else
			{
				executeButton.setEnabled(false);
				algorithmPanel.removeAll();
				repaint();
			}
		}
	}

	private  TaskFactory wrapWithContext(final CyCyniAlgorithm cyniAlgorithm,final CyTable table, final CyniAlgorithmContext cyniContext) {
		return new TaskFactory() {
			@Override
			public boolean isReady() {
				return cyniAlgorithm.isReady(cyniContext);
			}
			
			@Override
			public TaskIterator createTaskIterator() {
				return cyniAlgorithm.createTaskIterator(cyniContext,table, netFactory,viewFactory,netMgr,netTableMgr, rootNetMgr,vmMgr,viewMgr, layoutManager, metricsManager);
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
			if(value == null)
				return this;

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
