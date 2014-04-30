package org.cytoscape.cyniTest;

/*
 * #%L
 * Cytoscape Session Impl Integration Test (session-impl-integration-test)
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

import static org.cytoscape.model.CyNetwork.DEFAULT_ATTRS;
import static org.cytoscape.model.CyNetwork.HIDDEN_ATTRS;
import static org.cytoscape.model.CyNetwork.LOCAL_ATTRS;
import static org.cytoscape.model.CyNetwork.NAME;
import static org.cytoscape.model.CyNetwork.SELECTED;
import static org.cytoscape.model.subnetwork.CyRootNetwork.SHARED_ATTRS;
import static org.cytoscape.model.subnetwork.CyRootNetwork.SHARED_DEFAULT_ATTRS;
import static org.junit.Assert.*;

import java.io.File;
import java.util.*;


import org.cytoscape.group.CyGroup;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.Task;
import org.cytoscape.cyni.CyCyniAlgorithm;
import org.cytoscape.cyni.CyCyniMetricsManager;
import org.cytoscape.cyni.CyniAlgorithmContext;
import org.cytoscape.cyni.CyniCategory;
import org.cytoscape.cyni.CyCyniMetric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class PearsonTest extends BasicIntegrationTest {
	
	CyniAlgorithmContext cyniContext;
	CyCyniAlgorithm cyniAlgorithm;
	CyTable globalTable;
	CyNetwork oldNetwork;
	@Before
	public void setup() throws Exception {
		
		sessionFile = new File("./src/test/resources/", "pearsonTestSession.cys");
		checkBasicConfiguration();
		
	}

	@Test
	public void testLoadSession() throws Exception {
		ListMultipleSelection<String> attributeList;
		TaskIterator ti = openSessionTF.createTaskIterator(sessionFile);
		tm.execute(ti);
		
		oldNetwork =  applicationManager.getCurrentNetwork();
		cyniAlgorithm = cyniManager.getCyniAlgorithm("basic",CyniCategory.INDUCTION);
		assertEquals(1, tableManager.getGlobalTables().size());
		globalTable = tableManager.getGlobalTables().iterator().next();
		assertNotNull(cyniAlgorithm);
		assertNotNull(globalTable);
		assertNotNull(oldNetwork);
		Map<String, Object> mparams = new HashMap<String, Object> ();
		mparams.put("thresholdAddEdge",0.1);
		ListSingleSelection<String> type = new ListSingleSelection<String>("Absolute value");
		type.setSelectedValue("Absolute value");
		mparams.put("type",type);
		ListSingleSelection metrics = new ListSingleSelection<CyCyniMetric> (metricsManager.getCyniMetric("Correlation.cyni"));
		metrics.setSelectedValue (metricsManager.getCyniMetric("Correlation.cyni"));
		mparams.put("Measures",metrics);
		cyniContext = cyniAlgorithm.createCyniContext(globalTable,metricsManager,tunableSetter,mparams);
		assertNotNull(cyniContext);
		ti = cyniAlgorithm.createTaskIterator(cyniContext, globalTable, networkFactory, netViewFactory, networkManager, networkTableManager,
				rootNetManager,vmm, viewManager,  layoutManager, metricsManager);
		CheckTask observer = new CheckTask();
		tm.execute(ti,observer);
	}

	
	
	private void checkResults(CyNetwork newNetwork) {
		int correctEdges = 0;
		System.out.println("New network: " + newNetwork.getRow(newNetwork).get(CyNetwork.NAME, String.class));
		assertEquals(4, newNetwork.getEdgeCount());
		assertEquals(4, newNetwork.getNodeCount());
		CyTable oldEdgeTable = oldNetwork.getDefaultEdgeTable();
		CyTable newEdgeTable = newNetwork.getDefaultEdgeTable();
		
		for(CyRow row1 : newEdgeTable.getAllRows())
		{
			String name1 = row1.get(CyNetwork.NAME,String.class);
			Double value1 = row1.get("Distance",Double.class);
			System.out.println("New edge name: " + name1 + " dis: " + value1);
			for(CyRow row2 : oldEdgeTable.getAllRows())
			{
				String name2 = row2.get(CyNetwork.NAME,String.class);
				Double value2 = row2.get("Distance",Double.class);
				System.out.println("Old edge name: " + name2 + " dis: " + value2);
				if((name1.equals(name2)) && (value1.equals(value2)))
				{
					correctEdges++;
					break;
				}
			}
		}
		assertEquals(4,correctEdges);
	}
	
	public class CheckTask implements TaskObserver {
		public void taskFinished(ObservableTask t) {
			Object res = t.getResults(CyNetwork.class);
			assertNotNull(res);
			checkResults((CyNetwork) res);
			
		}

		public void allFinished(FinishStatus status) {
			
		}
	}
}
