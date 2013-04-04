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

import java.util.*;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;

/**
 * An interface specific to Cyni algorithms.
 * 
 * @CyAPI.Spi.Interface
 */
public interface CyCyniAlgorithm {
	/**
	 * Creates a task iterator containing the cyni tasks.
	 * @param context The cyni context for this cyni algorithm.
	 * @param networkFactory The network factory to create a new network
	 * @param networkViewFactory The network view factory to create a new network view
	 * @param networkManager The network manager
	 * @param netTableMgr The network table manager
	 * @param rootNetMgr The root network manager
	 * @param networkViewManager The network view manager
	 * @param layoutManager The layout manager to apply a layout if a new network is created
	 * @param tunableSetter the setter if cyni is not called through the GUI
	 * @param metricsManager The metrics manager to access to all available metrics
	 * @param mparams The map to the input paramters to be used if cyni is not called through the GUI
	 * @return taskIterator contains cyni tasks.
	 */
	TaskIterator createTaskIterator(Object context, CyTable table, CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory,
			CyNetworkManager networkManager, CyNetworkTableManager netTableMgr, CyRootNetworkManager rootNetMgr, VisualMappingManager vmMgr,
			CyNetworkViewManager networkViewManager, CyLayoutAlgorithmManager layoutManager, CyCyniMetricsManager metricsManager);

	/**
	 * Returns true if the task factory is ready to produce a task iterator.
	 * @param Context The layout context for this cyni algorithm.
	 * @return true if the task factory is ready to produce a task iterator.
	 */
	boolean isReady(Object Context);

	/**
	 * Returns a new cyni context object. This method can be used to create
	 * custom configurations for cyni algorithms.
	 * @param table The table where to get the data.
	 * @param metricsManager The cyni metrics manager.
	 * @param tunableSetter The tunable setter to set the parameters if not GUI is used.
	 * @param mparams The map of each one of the parameters with the value of the parameter.
	 * @return a new cyni context object.
	 */
	Object createCyniContext(CyTable table, CyCyniMetricsManager metricsManager, TunableSetter tunableSetter,Map<String, Object> mparams);

	/**
	 * Tests to see if this Cyni supports doing a Cyni Algorithm on a subset of
	 * the nodes.
	 * 
	 * @return true if cyni algorithm supports only applying the algorithm on a subset of the nodes
	 */
	public boolean supportsSelectedOnly();
	

	/**
	 * Returns the computer-readable name of the Cyni Algorithm. To get a human
	 * readable name, use toString().
	 * 
	 * @return The computer-readable name of the Cyni Algorithm.
	 */
	public String getName();
	
	
	/**
	 * A method to get the category of the algorithm
	 * 
	 * @return the Cyni category for the algorithm
	 */
	public CyniCategory getCategory();
}
