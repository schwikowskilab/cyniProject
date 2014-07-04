package fr.systemsbiology.cyni.internal.task;

/*
 * #%L
 * Cytoscape Core Task Impl (core-task-impl)
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


import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import fr.systemsbiology.cyni.*;


public class CyniTaskFactoryWrapper extends AbstractTaskFactory {
	private final CyNetworkFactory netFactory;
	private final CyNetworkViewFactory viewFactory;
	private final CyNetworkManager netMgr;
	private final CyNetworkViewManager viewMgr;
	private final VisualMappingManager vmMgr;
	private final CyCyniMetricsManager metricsManager;
	private final CyNetworkTableManager netTableMgr;
	private final CyRootNetworkManager rootNetMgr;
	private final CyLayoutAlgorithmManager layoutManager;
	private final CyCyniAlgorithm alg;

	public CyniTaskFactoryWrapper(CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory,
			CyNetworkManager networkManager, CyNetworkTableManager netTableMgr, CyRootNetworkManager rootNetMgr, VisualMappingManager vmMgr,
			CyNetworkViewManager networkViewManager, CyLayoutAlgorithmManager layoutManager, CyCyniMetricsManager metricsManager, CyCyniAlgorithm alg) {
		this.layoutManager = layoutManager;
		this.netFactory = networkFactory;
		this.netMgr = networkManager;
		this.viewFactory = networkViewFactory;
		this.viewMgr = networkViewManager;
		this.netTableMgr = netTableMgr;
		this.rootNetMgr = rootNetMgr;
		this.vmMgr = vmMgr;
		this.metricsManager = metricsManager;
		this.alg = alg;
	}

	public TaskIterator createTaskIterator() {
		CyniWrapperTask cyniTask = new CyniWrapperTask(netFactory, viewFactory,netMgr,netTableMgr,rootNetMgr,vmMgr, viewMgr,layoutManager,metricsManager,alg) ;
		return new TaskIterator(new CyniGetTableTask(cyniTask),cyniTask);
	}
}
