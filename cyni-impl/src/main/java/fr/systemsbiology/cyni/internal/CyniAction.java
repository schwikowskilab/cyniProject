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
package fr.systemsbiology.cyni.internal;


import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.property.CyProperty;
import fr.systemsbiology.cyni.*;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.ColumnNameChangedListener;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.DynamicTaskFactoryProvisioner;



public class CyniAction extends AbstractCyAction {
	private final static long serialVersionUID = 1202339874289358L;

	private CyCyniAlgorithmManager cyi;
	private CyTableManager table;
	private CySwingApplication desk;
	private PanelTaskManager tm;
	private CyProperty cytoscapePropertiesServiceRef;
	private CyApplicationManager appMgr;
	private CyLayoutAlgorithmManager layoutManager;
	private CyNetworkFactory netFactory;
	private CyNetworkViewFactory viewFactory;
	private CyNetworkManager netMgr;
	private CyNetworkViewManager viewMgr;
	private CyCyniMetricsManager metricsManager;
	private CyNetworkTableManager netTableMgr;
	private CyRootNetworkManager rootNetMgr;
	private VisualMappingManager vmMgr;
	private String PanelName;

	private CyniDialog dialog;

	public CyniAction(final String MenuName,final CyCyniAlgorithmManager cyi, final CyNetworkFactory networkFactory,final CyNetworkViewFactory networkViewFactory,final CyTableManager table, final CySwingApplication desk, final CyApplicationManager appMgr, final CyNetworkViewManager networkViewManager,
			final CyNetworkManager networkManager, final CyNetworkTableManager netTableMgr,final CyRootNetworkManager rootNetMgr,
			PanelTaskManager tm, CyLayoutAlgorithmManager layoutManager,CyCyniMetricsManager metricsManager, final VisualMappingManager vmMgr,
			CyProperty cytoscapePropertiesServiceRef, DynamicTaskFactoryProvisioner factoryProvisioner,CyServiceRegistrar serviceRegistrar, CyniCategory category)
	{
		super(MenuName,appMgr,"", networkViewManager);
		setPreferredMenu("Tools.Cyni Tools");
		if(category == CyniCategory.INDUCTION)
		{
			setMenuGravity(8.0f);
			PanelName = "Infer Network";
		}
		else if (category == CyniCategory.IMPUTATION)
		{
			setMenuGravity(9.0f);
			PanelName = "Impute Missing Data";
		}
		else
		{
			setMenuGravity(10.0f);
			PanelName = "Discretize Data";
		}
		this.appMgr = appMgr;
		this.cyi = cyi;
		this.desk = desk;
		this.tm = tm;
		this.layoutManager = layoutManager;
		this.table = table;
		this.netFactory = networkFactory;
		this.netMgr = networkManager;
		this.viewFactory = networkViewFactory;
		this.viewMgr = networkViewManager;
		this.metricsManager = metricsManager;
		this.cytoscapePropertiesServiceRef = cytoscapePropertiesServiceRef;
		this.netTableMgr = netTableMgr;
		this.rootNetMgr = rootNetMgr;
		this.vmMgr = vmMgr;
		
		
		dialog = new CyniDialog(PanelName, category,cyi,netFactory,viewFactory,netMgr,netTableMgr, rootNetMgr,viewMgr,table, desk, appMgr, tm, layoutManager,metricsManager,vmMgr,this.cytoscapePropertiesServiceRef, factoryProvisioner);
		serviceRegistrar.registerService(dialog, ColumnCreatedListener.class, new Properties());
		serviceRegistrar.registerService(dialog, ColumnDeletedListener.class, new Properties());
		serviceRegistrar.registerService(dialog, ColumnNameChangedListener.class, new Properties());
	}

	public void actionPerformed(ActionEvent e) {
		dialog.actionPerformed(e);
	}
}

