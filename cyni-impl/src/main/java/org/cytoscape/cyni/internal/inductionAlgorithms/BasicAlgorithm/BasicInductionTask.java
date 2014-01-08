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
package org.cytoscape.cyni.internal.inductionAlgorithms.BasicAlgorithm;



import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.cyni.*;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;



/**
 * The BasicInduction provides a very simple Induction, suitable as
 * the default Induction for Cytoscape data readers.
 */
public class BasicInductionTask extends AbstractCyniTask {
	private final double thresholdAddEdge;
	private boolean removeNodes = false;
	private boolean useAbsolut;
	private final List<String> attributeArray;
	private final CyTable table;
	
	private CyLayoutAlgorithmManager layoutManager;
	private CyCyniMetricsManager metricsManager;
	private CyCyniMetric selectedMetric;
	private CyniNetworkUtils netUtils;

	/**
	 * Creates a new BasicInduction object.
	 */
	public BasicInductionTask(final String name, final BasicInductionContext context, CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory,
			CyNetworkManager networkManager,CyNetworkTableManager netTableMgr, CyRootNetworkManager rootNetMgr, VisualMappingManager vmMgr,
			CyNetworkViewManager networkViewManager,CyLayoutAlgorithmManager layoutManager, 
			CyCyniMetricsManager metricsManager, CyTable selectedTable)
	{
		super(name, context,networkFactory,networkViewFactory,networkManager, networkViewManager,netTableMgr,rootNetMgr, vmMgr);

		this.thresholdAddEdge = context.thresholdAddEdge;
		this.layoutManager = layoutManager;
		this.metricsManager = metricsManager;
		//this.removeNodes = context.removeNodes;
		this.useAbsolut = context.useAbsolut;
		this.selectedMetric = context.measures.getSelectedValue();
		this.attributeArray = context.attributeList.getSelectedValues();
		this.table = selectedTable;
		this.netUtils = new CyniNetworkUtils(networkViewFactory,networkManager,networkViewManager,netTableMgr,rootNetMgr,vmMgr);
		
	}

	/**
	 *  Perform actual Induction task.
	 *  This creates the Cyni Induction Task
	 */
	@Override
	final protected void doCyniTask(final TaskMonitor taskMonitor) {
		Integer numNodes = 1;
		CyNode node1,node2;
		CyEdge edge;
		CyLayoutAlgorithm layout;
		Double progress = 0.0d;
		Double step;
		int nRows,threadNumber;
		ArrayList<Integer> index = new ArrayList<Integer>();
		Map<Object,CyNode> mapRowNodes;
		CyNetwork networkSelected = null;
		CyNetworkView newNetworkView ;
		double threadResults[] = new double[nThreads];
		double result;
		int threadIndex[] = new int[nThreads];
		threadNumber=0;
		Arrays.fill(threadResults, 0.0);
		newNetwork = netFactory.createNetwork();
		networkSelected = netUtils.getNetworkAssociatedToTable(table);
		
		taskMonitor.setTitle("Correlation Inference");
		taskMonitor.setStatusMessage("Generating network inference...");
		taskMonitor.setProgress(progress);
		mapRowNodes = new HashMap<Object,CyNode>();
		index.add(0);
		// Create the CyniTable
		CyniTable data = selectedMetric.getCyniTable(table,attributeArray.toArray(new String[0]), false, false, selectedOnly);
		
		nRows = data.nRows();
		step = 1.0 / nRows;
		
		threadResults = new double[nRows];
		threadIndex = new int[nRows];
		Arrays.fill(threadResults, 0.0);
		
		netUtils.setNetworkName(newNetwork, "Correlation Inference " + newNetwork.getSUID());
		
		
		//netUtils.addColumns(networkSelected,newNetwork,table,CyNode.class, CyNetwork.LOCAL_ATTRS);
		netUtils.copyNodeColumns(newNetwork, table);
	
		netUtils.createEdgeColumn(newNetwork,"Metric", String.class, false);	
		netUtils.createEdgeColumn(newNetwork,"Distance", Double.class, false);	
		
		// Create the thread pools
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);

		for (int i = 0; i < nRows; i++) 
		{
			threadNumber = 0;
			if (cancelled)
				break;
			for (int j = i+1; j < nRows; j++) 
			{
				if (cancelled)
					break;
				
				index.set(0, j);
				executor.execute(new ThreadedGetMetric(data,i,index,threadNumber,threadResults));
				threadIndex[threadNumber] = j;
				threadNumber++;
			}
			executor.shutdown();
			// Wait until all threads are finish
			try {
	         	executor.awaitTermination(7, TimeUnit.DAYS);
	        } catch (Exception e) {}
			
			for(int pool = 0; pool< threadNumber;pool++)
			{
				result = threadResults[pool];
				if(useAbsolut)
					result = Math.abs(threadResults[pool]);
				if(result > thresholdAddEdge)
				{
					if(!mapRowNodes.containsKey(data.getRowLabel(i)))
					{
						node1 = newNetwork.addNode();
						netUtils.cloneNodeRow(newNetwork,table.getRow(data.getRowLabel(i)), node1);
						if(newNetwork.getRow(node1).get(CyNetwork.NAME,String.class ) == null || newNetwork.getRow(node1).get(CyNetwork.NAME,String.class ).isEmpty() == true)
						{
							if(table.getPrimaryKey().getType().equals(String.class) && networkSelected == null)
								newNetwork.getRow(node1).set(CyNetwork.NAME,table.getRow(data.getRowLabel(i)).get(table.getPrimaryKey().getName(),String.class));
							else
								newNetwork.getRow(node1).set(CyNetwork.NAME, "Node " + numNodes);
						}
						if(newNetwork.getRow(node1).get(CyNetwork.SELECTED,Boolean.class ) == true)
							newNetwork.getRow(node1).set(CyNetwork.SELECTED, false);
						mapRowNodes.put(data.getRowLabel(i),node1);
						numNodes++;
					}
					if(!mapRowNodes.containsKey(data.getRowLabel(threadIndex[pool])))
					{
						node2 = newNetwork.addNode();
						netUtils.cloneNodeRow(newNetwork,table.getRow(data.getRowLabel(threadIndex[pool])), node2);
						if(newNetwork.getRow(node2).get(CyNetwork.NAME,String.class ) == null || newNetwork.getRow(node2).get(CyNetwork.NAME,String.class ).isEmpty() == true)
						{
							if(table.getPrimaryKey().getType().equals(String.class) && networkSelected == null)
								newNetwork.getRow(node2).set(CyNetwork.NAME,table.getRow(data.getRowLabel(threadIndex[pool])).get(table.getPrimaryKey().getName(),String.class));
							else
								newNetwork.getRow(node2).set(CyNetwork.NAME, "Node " + numNodes);
						}
						if(newNetwork.getRow(node2).get(CyNetwork.SELECTED,Boolean.class ) == true)
							newNetwork.getRow(node2).set(CyNetwork.SELECTED, false);
						mapRowNodes.put(data.getRowLabel(threadIndex[pool]),node2);
						numNodes++;
					}
							
					if(!newNetwork.containsEdge(mapRowNodes.get(data.getRowLabel(i)), mapRowNodes.get(data.getRowLabel(threadIndex[pool]))))
					{
						edge = newNetwork.addEdge(mapRowNodes.get(data.getRowLabel(i)), mapRowNodes.get(data.getRowLabel(threadIndex[pool])), false);
						newNetwork.getRow(edge).set("Distance", threadResults[pool]);
						newNetwork.getRow(edge).set("Metric",selectedMetric.toString());
						newNetwork.getRow(edge).set("name", newNetwork.getRow(mapRowNodes.get(data.getRowLabel(i))).get("name", String.class)
								+ " (Basic) " + newNetwork.getRow( mapRowNodes.get(data.getRowLabel(threadIndex[pool]))).get("name", String.class));
					}
				}
			}
			threadNumber = 0;
			executor = Executors.newFixedThreadPool(nThreads);

			progress = progress + step;
			taskMonitor.setProgress(progress);
		}
		
		if (!cancelled)
		{
			if(removeNodes)
				netUtils.removeNodesWithoutEdges(newNetwork);
			newNetworkView = netUtils.displayNewNetwork(newNetwork,networkSelected, false);
			taskMonitor.setProgress(1.0d);
			layout = layoutManager.getDefaultLayout();
			Object context = layout.getDefaultLayoutContext();
			insertTasksAfterCurrentTask(layout.createTaskIterator(newNetworkView, context, CyLayoutAlgorithm.ALL_NODE_VIEWS,""));
		}
	
	}
	
	private class ThreadedGetMetric implements Runnable {
		private ArrayList<Integer> index2;
		private int index1;
		private CyniTable tableData;
		private double results[];
		private int pos;
		
		ThreadedGetMetric(CyniTable data,int index1, ArrayList<Integer> parentsToIndex,int pos, double results[])
		{
			this.index2 = new ArrayList<Integer>( parentsToIndex);
			this.index1 = index1;
			this.tableData = data;
			this.pos = pos;
			this.results = results;
		}
		
		public void run() {
			results[pos] = selectedMetric.getMetric(tableData, tableData, index1, index2);

		}
		

	}
	

}
