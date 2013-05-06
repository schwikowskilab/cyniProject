/*
 * #%L
 * Cyni API (cyni-api)
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
package org.cytoscape.cyni;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.VirtualColumnInfo;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;


import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


/**
 * This is a basic implementation of a CyniAlgorithm Task that does some
 * bookkeeping, but primarily delegates to the doCyniTask() method.
 * 
 * @CyAPI.Abstract.Class
 */
public abstract class AbstractCyniTask extends AbstractTask {


	private final String name;
	private final CyNetworkTableManager netTableMgr;
	private final CyRootNetworkManager rootNetMgr;
	protected CyNetworkFactory netFactory;
	protected CyNetworkViewFactory viewFactory;
	protected CyNetworkManager netMgr;
	protected CyNetworkViewManager viewMgr;
	protected VisualMappingManager vmMgr;
	

	/**
	 * Indicates whether to apply the algorithm to all rows or only the selected
	 * rows.
	 */
	protected final boolean selectedOnly;
	
	/**
	 * Indicates the maximum number of threads that this task can use
	 */
	protected int nThreads;

	/**
	 * Constructor.
	 * 
	 * @param name The name of the algorithm. 
	 * @param context The context with the parameters to apply the algorithm
	 * @param networkFactory The network factory to create a new network
	 * @param networkViewFactory The network view factory to create a new network view
	 * @param networkManager The network manager
	 * @param networkViewManager The network view manager
	 * @param netTableMgr The network table manager to generate a new network and its table
	 * @param rootNetMgr The root network manager to generate a new root network
	 * @param vmMgr The Visual mapping manager that allows changing the visual style of a network
	 */
	public AbstractCyniTask(String name, final CyniAlgorithmContext context, CyNetworkFactory networkFactory, 
			CyNetworkViewFactory networkViewFactory,CyNetworkManager networkManager,CyNetworkViewManager networkViewManager,
			final CyNetworkTableManager netTableMgr,final CyRootNetworkManager rootNetMgr, final VisualMappingManager visualMapperManager) {
		super();

		this.name = name;
		this.selectedOnly = context.useSelectedOnly();
		this.netFactory = networkFactory;
		this.viewFactory = networkViewFactory;
		this.viewMgr = networkViewManager;
		this.netMgr = networkManager;
		this.netTableMgr = netTableMgr;
		this.rootNetMgr = rootNetMgr;
		this.vmMgr = visualMapperManager;
		nThreads = Runtime.getRuntime().availableProcessors()-1;

	}

	@Override
	public final void run(final TaskMonitor taskMonitor) {
		
		// this is overridden by children and does the actual cyni algorithm
		doCyniTask(taskMonitor);

	}
	
	
	/**
	 * This method is designed to actually encapsulate the cyni algorithm. It
	 * will be called from within the run() method of the task.
	 * 
	 * @param taskMonitor
	 *            Provided to allow updates to the task status.
	 */
	protected abstract void doCyniTask(final TaskMonitor taskMonitor);
}
