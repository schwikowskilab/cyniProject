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
package org.cytoscape.cyni.internal.inductionAlgorithms.K2Algorithm;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.VirtualColumnInfo;
//import org.cytoscape.induction.internal.metrics.*;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.cyni.*;
import org.cytoscape.cyni.internal.inductionAlgorithms.BasicAlgorithm.BasicInductionContext;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;




/**
 * The K2InductionTask performs the actual network inference algorithm
 */
public class K2InductionTask extends AbstractCyniTask {
	private final int maxNumParents;
	private final List<String> attributeArray;
	private final CyTable table;
	private CyLayoutAlgorithmManager layoutManager;
	private CyCyniMetricsManager metricsManager;
	private CyCyniMetric selectedMetric;
	private String selectedOrder;
	private String selectedCol;
	private boolean removeNodes;
	private boolean changeSign;
	private CyniNetworkUtils netUtils;

	/**
	 * Creates a new K2InductionTask object.
	 */
	public K2InductionTask(final String name, final K2InductionContext context, CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory,
			CyNetworkManager networkManager,CyNetworkTableManager netTableMgr, CyRootNetworkManager rootNetMgr, VisualMappingManager vmMgr,
			CyNetworkViewManager networkViewManager,CyLayoutAlgorithmManager layoutManager, 
			CyCyniMetricsManager metricsManager, CyTable selectedTable)
	{
		super(name, context,networkFactory,networkViewFactory,networkManager, networkViewManager,netTableMgr,rootNetMgr, vmMgr);

		this.maxNumParents = context.maxNumParents;
		this.layoutManager = layoutManager;
		this.metricsManager = metricsManager;
		this.attributeArray = context.attributeList.getSelectedValues();
		this.selectedOrder = context.ordering.getSelectedValue();
		this.selectedCol = context.selectedColumn.getSelectedValue();
		this.table = selectedTable;
		this.changeSign = false;
		this.removeNodes = context.removeNodes;
		this.selectedMetric = context.measures.getSelectedValue();
		this.netUtils = new CyniNetworkUtils(networkViewFactory,networkManager,networkViewManager,netTableMgr,rootNetMgr,vmMgr);
		
	}

	/**
	 *  Perform actual Induction task.
	 *  This creates the default square Induction.
	 */
	@Override
	final protected void doCyniTask(final TaskMonitor taskMonitor) {
		
		Integer numNodes = 1;
		String networkName;
		CyTable nodeTable, edgeTable;
		CyNode node1;
		CyEdge edge;
		CyLayoutAlgorithm layout;
		Double progress = 0.0d;
		Double step = 0.0;
		int bestAttribute,row,i,threadNumber;
		int nRows;
		Map<Object,CyNode> mapRowNodes;
		CyNetwork newNetwork = netFactory.createNetwork();
		CyNetworkView newNetworkView = null ;
		CyNetwork networkSelected = null;
		boolean okToProceed = false;
		ArrayList<Integer> parents = new ArrayList<Integer>();
		double pOld,pNew;
		ArrayList<Integer> parentsToIndex;
		double threadResults[] ;
		int threadIndex[] ;
		threadNumber=0;
		networkSelected = netUtils.getNetworkAssociatedToTable(table);
		
		taskMonitor.setTitle("K2 Bayesian Inference");
		taskMonitor.setStatusMessage("Generating K2 network bayesian inference...");
		taskMonitor.setProgress(progress);
		mapRowNodes = new HashMap<Object,CyNode>();
		parentsToIndex = new ArrayList<Integer>();

		// Create the CyniTable
		CyniTable data = selectedMetric.getCyniTable(table,attributeArray.toArray(new String[0]), false, false, selectedOnly);
		selectedMetric.resetParameters();
		
		if(selectedMetric.getName() == "Entropy.cyni")
		{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("Conditional", true);
			params.put("LogBase", "log10");
			selectedMetric.setParameters(params);
		}
		if(selectedMetric.getName() == "Entropy.cyni" || selectedMetric.getName() == "AIC.cyni" || selectedMetric.getName() == "MDL.cyni")
			changeSign = true;
		
		if(data.hasAnyMissingValue())
		{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "The data selected contains missing values.\n " +
							"Therefore, this algorithm can not proceed with these conditions.", "Warning", JOptionPane.WARNING_MESSAGE);
				}
			});
			newNetwork.dispose();
			return;
		}

		nRows = data.nRows();
		step = 1.0 / nRows;
		
		threadResults = new double[nRows];
		threadIndex = new int[nRows];
		Arrays.fill(threadResults, 0.0);
		
		if(selectedOrder.equals("Random Order"))
		{
			data.changeOrderRowsToRandom();
			
		}
		else if(selectedOrder.equals("Use Column"))
		{
			if(selectedCol != null)
			{
				ArrayList<Object>  tempList = new ArrayList<Object>(nRows);
				for(CyRow tempRow : table.getAllRows())
				{
					tempList.add(tempRow.get(selectedCol, table.getColumn(selectedCol).getType()));
				}
				data.changeOrderRowsByColumnValuesOrder(tempList);
			}
		}
		
		netUtils.setNetworkName(newNetwork, "K2 Inference " + newNetwork.getSUID());
		
		
		//netUtils.addColumns(networkSelected,newNetwork,table,CyNode.class, CyNetwork.LOCAL_ATTRS);
		netUtils.copyNodeColumns(newNetwork, table);
				
		netUtils.createEdgeColumn(newNetwork,"Metric", String.class, false);	
		netUtils.createEdgeColumn(newNetwork,"Score", Double.class, false);	
		
		// Create the thread pools
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);

		for (row = 0; row < nRows; row++) {
			if (cancelled)
				break;
			taskMonitor.setStatusMessage("K2 network bayesian induction(Nodes " + nRows + "). Searching parents for node " + (row+1) );
			parents.clear();
			pOld = 0.0;
			pNew = 0.0;
			node1 = newNetwork.addNode();
			netUtils.cloneNodeRow(newNetwork,table.getRow(data.getRowLabel(row)), node1);
			if(!table.getRow(data.getRowLabel(row)).isSet(CyNetwork.NAME))
				newNetwork.getRow(node1).set(CyNetwork.NAME, "Node " + numNodes);
			mapRowNodes.put(data.getRowLabel(row),node1);
			
			okToProceed = true;
			
			if(data.rowHasMissingValue(row))
			{
				numNodes++;
				continue;
			}
			parentsToIndex.clear();
			parentsToIndex.add(row);
			pOld = selectedMetric.getMetric(data, data, row, parentsToIndex);
			if(changeSign)
				pOld = -1.0*pOld;
			pNew = pOld;
			threadNumber = 0;
						
			while (okToProceed && (parents.size() < maxNumParents))
			{
				bestAttribute = -1;
				
				for(int possParent = 0; possParent < row ; possParent++)
				{
					if (cancelled)
					{
						okToProceed = false;
						break;
					}
					if(!parents.contains(possParent))
					{
						parentsToIndex.clear();
						parentsToIndex.add(possParent);
						parentsToIndex.addAll(parents);
						
						executor.execute(new ThreadedGetMetric(data,row,parentsToIndex,threadNumber,threadResults));
						threadIndex[threadNumber] = possParent;
						threadNumber++;
					}

				}
				
				executor.shutdown();
				// Wait until all threads are finish
				try {
		         	executor.awaitTermination(7, TimeUnit.DAYS);
		        } catch (Exception e) {}
				 
				for(int pool = 0; pool< threadNumber;pool++)
				{
					if(threadResults[pool] > pNew)
					{
						pNew = threadResults[pool];
						bestAttribute = threadIndex[pool];
					}
				}
				threadNumber = 0;
				executor = Executors.newFixedThreadPool(nThreads);
				if(bestAttribute != -1)
				{
					parents.add(bestAttribute);
				}
				else
					okToProceed = false;
			}
			
			if(parents.size() > 0)
			{
				if(changeSign && pNew != 0.0)
					pNew = -1.0*pNew;
				for(int parent : parents)
				{
					edge = newNetwork.addEdge( mapRowNodes.get(data.getRowLabel(parent)),mapRowNodes.get(data.getRowLabel(row)), true);
					newNetwork.getRow(edge).set("Score", pNew);
					newNetwork.getRow(edge).set("Metric",selectedMetric.toString());
					newNetwork.getRow(edge).set("name", newNetwork.getRow( mapRowNodes.get(data.getRowLabel(parent))).get("name", String.class)
							+ " (k2) " + newNetwork.getRow( mapRowNodes.get(data.getRowLabel(row))).get("name", String.class));
				}
			}

			numNodes++;
			progress = progress + step;
			taskMonitor.setProgress(progress);
		}
		
		
		if (!cancelled)
		{
			if(removeNodes)
				netUtils.removeNodesWithoutEdges(newNetwork);
			newNetworkView = netUtils.displayNewNetwork(newNetwork, networkSelected,true);
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
			if(changeSign)
				results[pos] = -1.0*results[pos];

		}
		

	}
	

}
