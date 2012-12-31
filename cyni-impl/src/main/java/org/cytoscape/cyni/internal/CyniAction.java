/*
 File: CyniInductionAction.java

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
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.cyni.internal;


import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.property.CyProperty;
import org.cytoscape.cyni.*;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
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
			CyProperty cytoscapePropertiesServiceRef, DynamicTaskFactoryProvisioner factoryProvisioner, CyniCategory category)
	{
		super(MenuName,appMgr,"", networkViewManager);
		setPreferredMenu("Tools.Cyni Tools");
		if(category == CyniCategory.INDUCTION)
		{
			setMenuGravity(8.0f);
			PanelName = "Network Induction";
		}
		else if (category == CyniCategory.IMPUTATION)
		{
			setMenuGravity(9.0f);
			PanelName = "Data Imputation";
		}
		else
		{
			setMenuGravity(10.0f);
			PanelName = "Data Discretization";
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
		
		System.out.println("induction action");
		
		dialog = new CyniDialog(PanelName, category,cyi,netFactory,viewFactory,netMgr,netTableMgr, rootNetMgr,viewMgr,table, desk, appMgr, tm, layoutManager,metricsManager,vmMgr,this.cytoscapePropertiesServiceRef, factoryProvisioner);
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("action performed induction action");
		dialog.actionPerformed(e);
	}
}

