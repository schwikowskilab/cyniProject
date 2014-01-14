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
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.Task;
import org.cytoscape.cyni.CyCyniAlgorithm;
import org.cytoscape.cyni.CyCyniMetricsManager;
import org.cytoscape.cyni.CyniAlgorithmContext;
import org.cytoscape.cyni.CyniCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class ManualDiscretizationTest extends BasicIntegrationTest {
	
	CyniAlgorithmContext cyniContext;
	CyCyniAlgorithm cyniAlgorithm;
	CyTable globalTable;
	CyNetwork oldNetwork;
	@Before
	public void setup() throws Exception {
		
		sessionFile = new File("./src/test/resources/", "kendallTestSession.cys");
		checkBasicConfiguration();
		
	}

	@Test
	public void testLoadSession() throws Exception {
		ListMultipleSelection<String> attributeList;
		TaskIterator ti = openSessionTF.createTaskIterator(sessionFile);
		tm.execute(ti);
		
		oldNetwork =  applicationManager.getCurrentNetwork();
		cyniAlgorithm = cyniManager.getCyniAlgorithm("manual.cyni",CyniCategory.DISCRETIZATION);
		assertEquals(1, tableManager.getGlobalTables().size());
		globalTable = tableManager.getGlobalTables().iterator().next();
		assertNotNull(cyniAlgorithm);
		assertNotNull(globalTable);
		assertNotNull(oldNetwork);
		Map<String, Object> mparams = new HashMap<String, Object> ();
		String[] tab1 = {"Column 10","Column 20","Column 30","Column 40"};
		ArrayList cols = new ArrayList(Arrays.asList(tab1));
		attributeList = new  ListMultipleSelection<String>(cols);
		attributeList.setSelectedValues(attributeList.getPossibleValues());
		mparams.put("attributeList",attributeList);
		ListSingleSelection interval = new ListSingleSelection<String> ("5");
		interval.setSelectedValue ("5");
		mparams.put("interval",interval);
		mparams.put("th41",new BoundedDouble(-100.0,1.0,100.0,false,false));
		mparams.put("th42",new BoundedDouble(-200.0,2.0,200.0,false,false));
		mparams.put("th43",new BoundedDouble(-300.0,3.0,300.0,false,false));
		mparams.put("th44",new BoundedDouble(-400.0,4.0,400.0,false,false));
		cyniContext = cyniAlgorithm.createCyniContext(globalTable,metricsManager,tunableSetter,mparams);
		assertNotNull(cyniContext);
		ti = cyniAlgorithm.createTaskIterator(cyniContext, globalTable, networkFactory, netViewFactory, networkManager, networkTableManager,
				rootNetManager,vmm, viewManager,  layoutManager, metricsManager);
		CheckTask observer = new CheckTask();
		tm.execute(ti,observer);
	}

	
	
	private void checkResults(List<String> columns) {
		Set tempSet = new HashSet<String>();
		ArrayList<String> strings = new ArrayList<String>();
		System.out.println("New columns: " + columns.toString());
		assertEquals(4, columns.size());
		
		for(String colName : columns)
		{
			assertTrue(globalTable.getColumn(colName) != null);
			assertTrue(globalTable.getColumn(colName).getType() == String.class);
			strings.clear();
			for(CyRow row : globalTable.getAllRows())
			{
				strings.add(row.get(colName,String.class));
				
			}
			tempSet.clear();
			tempSet.addAll(strings);
			assertTrue(tempSet.size() <= 5);
		}
	}
	
	public class CheckTask implements TaskObserver {
		public void taskFinished(ObservableTask t) {
			Object res = t.getResults(List.class);
			assertNotNull(res);
			checkResults( (List<String>)res);
			
		}

		public void allFinished(FinishStatus status) {
			
		}
	}
}
