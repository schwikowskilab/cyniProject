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
package org.cytoscape.cyni.internal.discretizationAlgorithms.ManualDiscretization;


import java.util.Map;

import org.cytoscape.cyni.*;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;


/**
 * The BasicInduction provides a very simple Induction, suitable as
 * the default Induction for Cytoscape data readers.
 */
public class ManualDiscretization extends AbstractCyniAlgorithm {
	
	
	private CyTable selectedTable;
	/**
	 * Creates a new EqualDiscretization object.
	 */
	public ManualDiscretization() {
		super("manual_discrete","Manual Discretization",true,CyniCategory.DISCRETIZATION);
	
	}

	public TaskIterator createTaskIterator(CyniAlgorithmContext context,CyTable table, CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory,
			CyNetworkManager networkManager,CyNetworkTableManager netTableMgr, CyRootNetworkManager rootNetMgr,VisualMappingManager vmMgr,
			CyNetworkViewManager networkViewManager, CyLayoutAlgorithmManager layoutManager, CyCyniMetricsManager metricsManager) {
		selectedTable = table;
		return new TaskIterator(new ManualDiscretizationTask(getName(),(ManualDiscretizationContext) context,selectedTable));
	}
	
	public CyniAlgorithmContext createCyniContext(CyTable table, CyCyniMetricsManager metricsManager, TunableSetter tunableSetter,Map<String, Object> mparams) {
		CyniAlgorithmContext context;
		selectedTable = table;
		context = new ManualDiscretizationContext(table);
		if(mparams != null && !mparams.isEmpty())
		{
			tunableSetter.applyTunables(context, mparams);
			((ManualDiscretizationContext)context).updateMapThresholds();
		}
		return context;
	}
	
}
