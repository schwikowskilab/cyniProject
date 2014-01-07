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


import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.cyni.*;
import org.cytoscape.cyni.internal.task.CyniTaskFactoryWrapper;


/**
 * CycynisImpl is a singleton class that is used to register all available
 * cyni algorithms.  
 */
public class CyCyniImpl implements CyCyniAlgorithmManager {

	private final Map<String, CyCyniAlgorithm> cyniMap;
	private final Map<String, CyCyniAlgorithm> ImputationMap;
	private final Map<String, CyCyniAlgorithm> DiscretizationMap;
	private final Map<String, TaskFactory> serviceMap;
	private final CyProperty<Properties> cyProps;
	private final CyServiceRegistrar serviceRegistrar;
	private final CyNetworkFactory netFactory;
	private final CyNetworkViewFactory viewFactory;
	private final CyNetworkManager netMgr;
	private final CyNetworkViewManager viewMgr;
	private final VisualMappingManager vmMgr;
	private final CyCyniMetricsManager metricsManager;
	private final CyNetworkTableManager netTableMgr;
	private final CyRootNetworkManager rootNetMgr;
	private final CyLayoutAlgorithmManager layoutManager;
	private final CyEventHelper eventHelper;

	public CyCyniImpl(CyServiceRegistrar serviceRegistrar,CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory,
			CyNetworkManager networkManager, CyNetworkTableManager netTableMgr, CyRootNetworkManager rootNetMgr, VisualMappingManager vmMgr,
			CyNetworkViewManager networkViewManager, CyLayoutAlgorithmManager layoutManager, CyCyniMetricsManager metricsManager,
			final CyProperty<Properties> p,CyEventHelper eventHelper) {
		this.cyProps = p;
		cyniMap = new HashMap<String,CyCyniAlgorithm>();
		ImputationMap = new HashMap<String,CyCyniAlgorithm>();
		DiscretizationMap = new HashMap<String,CyCyniAlgorithm>();
		serviceMap = new ConcurrentHashMap<String,TaskFactory>();
		this.serviceRegistrar = serviceRegistrar;
		this.layoutManager = layoutManager;
		this.netFactory = networkFactory;
		this.netMgr = networkManager;
		this.viewFactory = networkViewFactory;
		this.viewMgr = networkViewManager;
		this.netTableMgr = netTableMgr;
		this.rootNetMgr = rootNetMgr;
		this.vmMgr = vmMgr;
		this.metricsManager = metricsManager;
		this.eventHelper = eventHelper;
	}

	/**
	 * Add a cyni to the cyni manager's list.  If menu is "null"
	 * it will be assigned to the "none" menu, which is not displayed.
	 * This can be used to register cynis that are to be used for
	 * specific algorithmic purposes, but not, in general, supposed
	 * to be for direct user use.
	 *
	 * @param cyni The cyni to be added
	 * @param menu The menu that this should appear under
	 */
	public void addCyniAlgorithm(CyCyniAlgorithm cyni, Map props) {
		if ( cyni != null )
		{
			if(cyni.getCategory() == CyniCategory.INDUCTION)
			{
				cyniMap.put(cyni.getName(),cyni);
			}
			else if (cyni.getCategory() == CyniCategory.IMPUTATION)
			{
				ImputationMap.put(cyni.getName(),cyni);
			}else if (cyni.getCategory() == CyniCategory.DISCRETIZATION)
			{
				DiscretizationMap.put(cyni.getName(),cyni);
			}
			if (serviceRegistrar != null) {
				Properties cyniProps = new Properties();
				cyniProps.setProperty(COMMAND, cyni.getName());
				cyniProps.setProperty(COMMAND_NAMESPACE, "cyni");
				TaskFactory service = new CyniTaskFactoryWrapper(netFactory, viewFactory,netMgr,netTableMgr,rootNetMgr,vmMgr, viewMgr,layoutManager,metricsManager, cyni);
				// Register the service as a TaskFactory for commands
				serviceRegistrar.registerService(service, TaskFactory.class, cyniProps);
				serviceMap.put(cyni.getName(), service);
			}
			eventHelper.fireEvent(new CyniAlgorithmAddedEvent(this,cyni));
		}
	}

	/**
	 * Remove a cyni from the cyni maanger's list.
	 *
	 * @param cyni The cyni to remove
	 */
	public void removeCyniAlgorithm(CyCyniAlgorithm cyni, Map props) {
		if ( cyni != null )
		{
			if(cyni.getCategory() == CyniCategory.INDUCTION)
			{
				cyniMap.remove(cyni.getName());
			}
			else if (cyni.getCategory() == CyniCategory.IMPUTATION)
			{
				ImputationMap.remove(cyni.getName());
			}else if (cyni.getCategory() == CyniCategory.DISCRETIZATION)
			{
				DiscretizationMap.remove(cyni.getName());
			}
			if (serviceRegistrar != null && serviceMap.containsKey(cyni.getName())) {
				TaskFactory service = serviceMap.get(cyni.getName());
				serviceRegistrar.unregisterService(service,TaskFactory.class);
				serviceMap.remove(cyni.getName());
			}
			eventHelper.fireEvent(new CyniAlgorithmDeletedEvent(this,cyni));
		}
	}

	/**
	 * Get the cyni named "name".  If "name" does
	 * not exist, this will return null
	 *
	 * @param name String representing the name of the cyni
	 * @return the cyni of that name or null if it is not reigstered
	 */
	@Override
	public CyCyniAlgorithm getCyniAlgorithm(String name, CyniCategory category) {
		if (name != null)
		{
			if(category == CyniCategory.INDUCTION)
			{
				return cyniMap.get(name);
			}
			else if (category == CyniCategory.IMPUTATION)
			{
				return ImputationMap.get(name);
			}else if (category == CyniCategory.DISCRETIZATION)
			{
				return DiscretizationMap.get(name);
			} else
				return null;
		}
		return null;
	}

	/**
	 * Get all of the available cynis.
	 *
	 * @return a Collection of all the available cynis
	 */
	@Override
	public Collection<CyCyniAlgorithm> getAllCyniAlgorithms(CyniCategory category) {
		if(category == CyniCategory.INDUCTION)
		{
			return cyniMap.values();
		}
		else if (category == CyniCategory.IMPUTATION)
		{
			return ImputationMap.values();
		}else if (category == CyniCategory.DISCRETIZATION)
		{
			return DiscretizationMap.values();
		} else
			return null;
	}
	
	/**
	 * Get the name all available cynis Metrics.
	 *
	 * @return a list all the available cynis Metrics
	 */
	@Override
	public List<String> getAllCyniAlgorithmNames(CyniCategory category) {
		List<String> list;
		if(category == CyniCategory.INDUCTION)
		{
			list = new ArrayList<String>( cyniMap.keySet());
			return list;
		}
		else if (category == CyniCategory.IMPUTATION)
		{
			list = new ArrayList<String>( ImputationMap.keySet());
			return list;
		}else if (category == CyniCategory.DISCRETIZATION)
		{
			list = new ArrayList<String>( DiscretizationMap.keySet());
			return list;
		} else
			return null;
		
		
	}

	
}
