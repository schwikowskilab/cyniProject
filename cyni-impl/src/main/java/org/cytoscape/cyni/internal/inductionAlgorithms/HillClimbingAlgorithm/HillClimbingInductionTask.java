/*
  File: HillClimbingInductionTask.java

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
package org.cytoscape.cyni.internal.inductionAlgorithms.HillClimbingAlgorithm;



import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
public class HillClimbingInductionTask extends AbstractCyniTask {
	private final int maxNumParents;
	private final List<String> attributeArray;
	private final CyTable table;
	private CyLayoutAlgorithmManager layoutManager;
	private CyCyniMetricsManager metricsManager;
	private Map<Integer, CyNode>  mapIndexNode;
	private Map<CyNode, Integer> mapNodeIndex;
	private boolean useNetworkAsInitialSearch;
	private boolean edgesBlocked;
	private boolean reversalOption;
	private Map<CyNode, CyNode> orig2NewNodeMap;
	private Map<CyNode, CyNode> new2OrigNodeMap;
	private double [] [] scoreAdd;
	private double [] [] scoreDel;
	private boolean [] [] edgeBlocked;
	private CyCyniMetric selectedMetric;
	private boolean removeNodes;


	/**
	 * Creates a new BasicInduction object.
	 */
	public HillClimbingInductionTask(final String name, final HillClimbingInductionContext context, CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory,
			CyNetworkManager networkManager,CyNetworkTableManager netTableMgr, CyRootNetworkManager rootNetMgr, VisualMappingManager vmMgr,
			CyNetworkViewManager networkViewManager,CyLayoutAlgorithmManager layoutManager, 
			CyCyniMetricsManager metricsManager, CyTable selectedTable)
	{
		super(name, context,networkFactory,networkViewFactory,networkManager, networkViewManager,netTableMgr,rootNetMgr,vmMgr);

		this.maxNumParents = context.maxNumParents;
		this.layoutManager = layoutManager;
		this.metricsManager = metricsManager;
		this.attributeArray = context.attributeList.getSelectedValues();
		this.table = selectedTable;
		this.selectedMetric = context.measures.getSelectedValue();
		this.useNetworkAsInitialSearch = context.useNetworkAsInitialSearch;
		this.reversalOption = context.reversalOption;
		this.removeNodes = context.removeNodes;
		this.edgesBlocked = context.edgesBlocked;
		mapNodeIndex =  new HashMap< CyNode, Integer>();
		mapIndexNode =  new HashMap<Integer, CyNode>();
		orig2NewNodeMap = new WeakHashMap<CyNode, CyNode>();
		new2OrigNodeMap = new WeakHashMap<CyNode, CyNode>();
		nodeParents = new HashMap<Integer, ArrayList<Integer>>();
		
	}

	/**
	 *  Perform actual Induction task.
	 *  This creates the default square Induction.
	 */
	@Override
	final protected void doCyniTask(final TaskMonitor taskMonitor) {
		
		String networkName;
		Integer numNodes = 1;
		CyTable nodeTable, edgeTable;
		CyEdge edge;
		CyNode newNode;
		CyLayoutAlgorithm layout;
		Double progress = 0.0d;
		Double step = 0.0;
		int i=0;
		int nRows,added,removed,reversed;
		CyNetwork newNetwork = netFactory.createNetwork();
		CyNetworkView newNetworkView ;
		CyNetwork networkSelected = null;
		boolean okToProceed = true;
		Operation operationAdd = new Operation("Add");
		Operation operationDelete = new Operation("Delete");
		Operation operationReverse = new Operation("Reverse");
		Operation chosenOperation;
		
		networkSelected = getNetworkAssociatedToTable(table);
		
		taskMonitor.setTitle("Hill Climbing induction");
		taskMonitor.setStatusMessage("Generating Hill Climbing induction...");
		taskMonitor.setProgress(progress);
		
		networkName = "HC Induction " + newNetwork.getSUID();
		if (newNetwork != null && networkName != null) {
			CyRow netRow = newNetwork.getRow(newNetwork);
			netRow.set(CyNetwork.NAME, networkName);
		}
		
		addColumns(networkSelected,newNetwork,table,CyNode.class, CyNetwork.LOCAL_ATTRS);
		
		for (CyRow origRow : table.getAllRows()) {
			if(selectedOnly)
			{
				if(networkSelected != null && !origRow.get(CyNetwork.SELECTED, Boolean.class))
					continue;
			}
			newNode = newNetwork.addNode();
			if(networkSelected != null)
			{
				orig2NewNodeMap.put(networkSelected.getNode(origRow.get(CyNetwork.SUID,Long.class)), newNode);
				new2OrigNodeMap.put(newNode, networkSelected.getNode(origRow.get(CyNetwork.SUID,Long.class)));
			}
			cloneRow(newNetwork, CyNode.class,origRow, newNetwork.getRow(newNode, CyNetwork.LOCAL_ATTRS));
			if(!origRow.isSet(CyNetwork.NAME))
				newNetwork.getRow(newNode).set(CyNetwork.NAME, "Node " + numNodes);
			numNodes++;
		}
		
		nodeTable = newNetwork.getDefaultNodeTable();
		edgeTable = newNetwork.getDefaultEdgeTable();
		
		// Create the CyniTable
		CyniTable data = new CyniTable(nodeTable,attributeArray.toArray(new String[0]), false, false, selectedOnly);
		
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
		
		progress = progress + step;
		taskMonitor.setProgress(progress);
		
		for (i = 0; i< nRows;i++){//( CyNode origNode : newNetwork.getNodeList()) {
			nodeParents.put(i, new ArrayList<Integer>());
			/*if(!data.rowHasMissingValue(i))
			{*/
				mapNodeIndex.put( newNetwork.getNode(nodeTable.getRow(data.getRowLabel(i)).get(CyNetwork.SUID, Long.class)) ,i);
				mapIndexNode.put(  i, newNetwork.getNode(nodeTable.getRow(data.getRowLabel(i)).get(CyNetwork.SUID, Long.class)));
			//}
		}
		
		edgeBlocked = new boolean [nRows][nRows];
		
		if(networkSelected != null && useNetworkAsInitialSearch)
		{
			addColumns(networkSelected,newNetwork,table,CyEdge.class, CyNetwork.LOCAL_ATTRS);
			for (final CyEdge origEdge : networkSelected.getEdgeList()) {
				
				final CyNode newSource = orig2NewNodeMap.get(origEdge.getSource());
				final CyNode newTarget = orig2NewNodeMap.get(origEdge.getTarget());
				if(newSource != null && newTarget != null)
				{
					final boolean newDirected = origEdge.isDirected();
					final CyEdge newEdge = newNetwork.addEdge(newSource, newTarget, newDirected);
					cloneRow(newNetwork, CyEdge.class, networkSelected.getRow(origEdge, CyNetwork.LOCAL_ATTRS), newNetwork.getRow(newEdge, CyNetwork.LOCAL_ATTRS));
					if(edgesBlocked)
					{
						if(networkSelected.getRow(origEdge, CyNetwork.LOCAL_ATTRS).get(CyNetwork.SELECTED, Boolean.class))
							edgeBlocked[mapNodeIndex.get(newSource)][mapNodeIndex.get(newTarget)] = true;
					}
					if(!newDirected )
					{
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(null, "The data selected belongs to a network that is not directed.\n " +
										"Therefore, this algorithm is not able to proceed with parameters requested", "Warning", JOptionPane.WARNING_MESSAGE);
							}
						});
						newNetwork.dispose();
						return;
					}
				}
			}
				
			initParentsMap(newNetwork);
				
			for ( i = 0; i< nRows; i++) {		
				if(isGraphCyclic( i))
				{
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, "The data selected belongs to a network that is not acyclic.\n " +
									"This algorithm is a bayesian network algorithm and requires a Directed Acyclic Graph(DAG) to perform", "Warning", JOptionPane.WARNING_MESSAGE);
						}
					});
					newNetwork.dispose();
					return;
				}
			}
			
		}

		scoreAdd = new double [nRows][nRows];
		scoreDel = new double [nRows][nRows];
		
		selectedMetric.resetParameters();
		
		taskMonitor.setStatusMessage("Initializing Cache..." );
		initCache(data, selectedMetric, taskMonitor);
		taskMonitor.setStatusMessage("Cache Initialized\n Looking for optimal solution..." );
		edgeTable.createColumn("Probability", Double.class, false);	
		progress += 0.5; 
		added = 0;
		removed = 0;
		reversed = 0;
		
		while(okToProceed)
		{
			operationAdd.resetParameters();
			operationDelete.resetParameters();
			operationReverse.resetParameters();
			taskMonitor.setStatusMessage("Cache Initialized\nLooking for optimal solution by performing the following operations:\n" +
					"Added edges: " + added + "\nRemoved edges: " + removed + "\nReversed edges: " + reversed  );
			
			findBestAddEdge(data, operationAdd);
			findBestDeleteEdge(data, operationDelete);
			if(reversalOption)
				findBestReverseEdge(data, operationReverse);
						
			if (cancelled)
				break;
			
			if(operationAdd.score >= operationDelete.score)
			{
				chosenOperation = operationAdd;
			}
			else
			{
				chosenOperation = operationDelete;
			}
			
			if(reversalOption)
			{
				if(operationReverse.score > chosenOperation.score)
				{
					chosenOperation = operationReverse;
				}
			}
			
			if(chosenOperation.score > 0.0)
			{
				if(chosenOperation.type == "Add")
				{
					edge = newNetwork.addEdge( mapIndexNode.get(chosenOperation.nodeParent), mapIndexNode.get(chosenOperation.nodeChild), true);
					newNetwork.getRow(edge).set("name", newNetwork.getRow( mapIndexNode.get(chosenOperation.nodeParent)).get("name", String.class)
							+ " (HC) " + newNetwork.getRow( mapIndexNode.get(chosenOperation.nodeChild)).get("name", String.class));
					nodeParents.get(chosenOperation.nodeChild).add(Integer.valueOf(chosenOperation.nodeParent));
					updateCache(data, selectedMetric,chosenOperation.nodeChild);
					added++;
				}
				if(chosenOperation.type == "Delete")
				{
					newNetwork.removeEdges(newNetwork.getConnectingEdgeList(mapIndexNode.get(chosenOperation.nodeParent), mapIndexNode.get(chosenOperation.nodeChild), CyEdge.Type.DIRECTED));
					nodeParents.get(chosenOperation.nodeChild).remove(Integer.valueOf(chosenOperation.nodeParent));
					updateCache(data, selectedMetric,chosenOperation.nodeChild);
					removed++;
				}
				if(chosenOperation.type == "Reverse")
				{
					newNetwork.removeEdges(newNetwork.getConnectingEdgeList(mapIndexNode.get(chosenOperation.nodeParent), mapIndexNode.get(chosenOperation.nodeChild), CyEdge.Type.DIRECTED));
					edge = newNetwork.addEdge( mapIndexNode.get(chosenOperation.nodeChild), mapIndexNode.get(chosenOperation.nodeParent), true);
					newNetwork.getRow(edge).set("name", newNetwork.getRow( mapIndexNode.get(chosenOperation.nodeChild)).get("name", String.class)
							+ " (HC) " + newNetwork.getRow( mapIndexNode.get(chosenOperation.nodeParent)).get("name", String.class));
					nodeParents.get(chosenOperation.nodeChild).remove(Integer.valueOf(chosenOperation.nodeParent));
					updateCache(data, selectedMetric,chosenOperation.nodeChild);
					nodeParents.get(chosenOperation.nodeParent).add(Integer.valueOf(chosenOperation.nodeChild));
					updateCache(data, selectedMetric,chosenOperation.nodeParent);
					reversed++;
				}
			}
			else
				okToProceed = false;
			
			progress = progress + step;
			taskMonitor.setProgress(progress);
		}
		
		
		if (!cancelled)
		{
			if(removeNodes)
				removeNodesWithoutEdges(newNetwork);
			newNetworkView = displayNewNetwork(newNetwork,networkSelected, true);
			taskMonitor.setProgress(1.0d);
			layout = layoutManager.getDefaultLayout();
			Object context = layout.getDefaultLayoutContext();
			insertTasksAfterCurrentTask(layout.createTaskIterator(newNetworkView, context, CyLayoutAlgorithm.ALL_NODE_VIEWS,""));
		}
	
	}
	
	
	private void initParentsMap(CyNetwork network)
	{
		for ( CyEdge edge : network.getEdgeList()) {
			nodeParents.get(mapNodeIndex.get(edge.getTarget())).add(Integer.valueOf(mapNodeIndex.get(edge.getSource())));
		}
	}
	
	private void initCache(CyniTable data, CyCyniMetric metric, TaskMonitor taskMonitor)
	{
		double[] baseScores = new double[data.nRows()];
        int nRows = data.nRows();
        int nodeIndex,i;
        Double progress = 0.0d;
		Double step = 0.0;
        ArrayList<Integer> parents = new ArrayList<Integer>();
	    // Create the thread pools
	    ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        step = 1.0 / (nRows*2.0);
        
        for(i = 0;i<nRows;i++) {
			nodeIndex =i;
			parents.clear();
			if(nodeParents.get(nodeIndex).size() > 0)
				parents.addAll(nodeParents.get(nodeIndex));
			else
				parents.add(nodeIndex);
			baseScores[nodeIndex] = metric.getMetric(data, data, nodeIndex,parents);
		}

        for (int nodeStart = 0; nodeStart < nRows; nodeStart++) {
            for (int nodeEnd = 0; nodeEnd < nRows; nodeEnd++) 
            {
                if (nodeStart != nodeEnd) 
                {
                	executor.execute(new ThreadedGetMetric(data,nodeStart,nodeEnd,baseScores[nodeEnd]));
				}
            }
            executor.shutdown();
    	    // Wait until all threads are finish
            try {
           	    executor.awaitTermination(7, TimeUnit.DAYS);
            } catch (Exception e) {}
            executor = Executors.newFixedThreadPool(nThreads);
            progress = progress + step;
    	    taskMonitor.setProgress(progress);
        }
       
	}
	
	private void updateCache(CyniTable data, CyCyniMetric metric, int nodeEnd)
	{
	    int nRows = data.nRows();
	    ArrayList<Integer> parents = new ArrayList<Integer>();
	    double baseScore ;
	    // Create the thread pools
	    ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		
	    if(nodeParents.get(nodeEnd).size() > 0)
			parents.addAll(nodeParents.get(nodeEnd));
		else
			parents.add(nodeEnd);
	    baseScore = metric.getMetric(data, data, nodeEnd, parents);
		 
		for (int nodeStart = 0; nodeStart < nRows; nodeStart++) {
            if (nodeStart != nodeEnd) {
				executor.execute(new ThreadedGetMetric(data,nodeStart,nodeEnd,baseScore));
			}
        }
		executor.shutdown();
		// Wait until all threads are finish
		 try {
         	executor.awaitTermination(7, TimeUnit.DAYS);
         } catch (Exception e) {}
	}
	
	private void findBestOperation(CyniTable data, Operation operationAdd, Operation operationDel, Operation operationRev)
	{
		int nRows = data.nRows();
		
		for (int nodeChild = 0; nodeChild < nRows; nodeChild++) {
			for (int nodeParent = 0; nodeParent < nRows; nodeParent++) {
					
				if(!nodeParents.get(nodeChild).contains(nodeParent))
				{
					if (nodeParents.get(nodeChild).size() < maxNumParents) 
					{
						nodeParents.get(nodeChild).add(Integer.valueOf(nodeParent));
						if(scoreAdd[nodeParent][nodeChild] > operationAdd.score)
						{
							if(!isGraphCyclic( nodeChild) )
							{
								operationAdd.score = scoreAdd[nodeParent][nodeChild];
								operationAdd.nodeParent = nodeParent;
								operationAdd.nodeChild = nodeChild;
							}
						}
						nodeParents.get(nodeChild).remove(Integer.valueOf(nodeParent));
					}
				}
				else
				{
					if(reversalOption)
					{
						nodeParents.get(nodeChild).remove(Integer.valueOf(nodeParent));
						nodeParents.get(nodeParent).add(Integer.valueOf(nodeChild));
						if((scoreDel[nodeParent][nodeChild] + scoreAdd[nodeChild][nodeParent]) > operationRev.score)
						{	
							if(!isGraphCyclic( nodeParent) )
							{
								operationRev.score = scoreDel[nodeParent][nodeChild] + scoreAdd[nodeChild][nodeParent];
								operationRev.nodeParent = nodeParent;
								operationRev.nodeChild = nodeChild;
							}
						}
						nodeParents.get(nodeParent).remove(Integer.valueOf(nodeChild));
						nodeParents.get(nodeChild).add(Integer.valueOf(nodeParent));		
					}
				}
				if(scoreDel[nodeParent][nodeChild] > operationDel.score)
				{
					operationDel.score = scoreDel[nodeParent][nodeChild];
					operationDel.nodeParent = nodeParent;
					operationDel.nodeChild = nodeChild;
				}
			}
		}
				
	}
	
	private void findBestAddEdge(CyniTable data, Operation operation)
	{
		int nRows = data.nRows();
		
		for (int nodeChild = 0; nodeChild < nRows; nodeChild++) {
			if (nodeParents.get(nodeChild).size() < maxNumParents) {
				for (int nodeParent = 0; nodeParent < nRows; nodeParent++) {
					if(!nodeParents.get(nodeChild).contains(nodeParent))
					{
						//System.out.println("nodeP " + nodeChild);
						if(scoreAdd[nodeParent][nodeChild] > operation.score)
						{
							nodeParents.get(nodeChild).add(Integer.valueOf(nodeParent));
							//System.out.println("nodesAd " + nodeParent + " " + nodeChild + " " + scoreAdd[nodeParent][nodeChild]);
							if(!isGraphCyclic( nodeChild) )
							{
								//System.out.println("nodes " + nodeParent + " " + nodeChild + " " + scoreAdd[nodeParent][nodeChild] + " " + operation.score);
								operation.score = scoreAdd[nodeParent][nodeChild];
								operation.nodeParent = nodeParent;
								operation.nodeChild = nodeChild;
							}
							nodeParents.get(nodeChild).remove(Integer.valueOf(nodeParent));
						}
					}
				}
			}
		}
	}
	
	private void findBestDeleteEdge(CyniTable data, Operation operation)
	{
		int nRows = data.nRows();
		
		for (int nodeChild = 0; nodeChild < nRows; nodeChild++) {
			for (int nodeParent : nodeParents.get(nodeChild)) {
					if(scoreDel[nodeParent][nodeChild] > operation.score && !edgeBlocked[nodeParent][nodeChild])
					{
						//System.out.println("nodes " + nodeParent + " " + nodeChild + " " + scoreDel[nodeParent][nodeChild] + " " + operation.score);
						operation.score = scoreDel[nodeParent][nodeChild];
						operation.nodeParent = nodeParent;
						operation.nodeChild = nodeChild;
					}
			}
		}
		
	}
	
	private void findBestReverseEdge(CyniTable data, Operation operation)
	{
		int nRows = data.nRows();
		
		for (int nodeChild = 0; nodeChild < nRows; nodeChild++) {
			for (int nodeParent = 0; nodeParent < nRows; nodeParent++) {
				if(nodeParents.get(nodeChild).contains(nodeParent))
				{
					if((scoreDel[nodeParent][nodeChild] + scoreAdd[nodeChild][nodeParent]) > operation.score)
					{	
						nodeParents.get(nodeChild).remove(Integer.valueOf(nodeParent));
						nodeParents.get(nodeParent).add(Integer.valueOf(nodeChild));
						if(!isGraphCyclic( nodeParent) )
						{
							operation.score = scoreDel[nodeParent][nodeChild] + scoreAdd[nodeChild][nodeParent];
							operation.nodeParent = nodeParent;
							operation.nodeChild = nodeChild;
						}
						nodeParents.get(nodeParent).remove(Integer.valueOf(nodeChild));
						nodeParents.get(nodeChild).add(Integer.valueOf(nodeParent));
					}
				}
			}
		}
	}
	
	class Operation{
		
		public Operation(String type)
		{
			nodeParent = -1;
			nodeChild = -1;	
			score =  -1E100;
			this.type = type;
		}
		
		public int nodeParent;
		
		public int nodeChild;
		
		public double score;
		
		public String type;
		
		public void resetParameters()
		{
			nodeParent = -1;		
			nodeChild = -1;
			score =  -1E100;
		}
	}
	
	private class ThreadedGetMetric implements Runnable {
		private int nodeStart, nodeEnd;
		private double baseScore;
		private CyniTable tableData;
		private  ArrayList<Integer> parents = new ArrayList<Integer>();
		
		ThreadedGetMetric(CyniTable data,int nodeStart, int nodeEnd, double baseScore)
		{
			this.nodeStart = nodeStart;
			this.nodeEnd = nodeEnd;
			this.tableData = data;
			this.baseScore = baseScore;
			
		}
		
		public void run() {
			parents.clear();
            if(!nodeParents.get(nodeEnd).contains(nodeStart))
            {
            	if (nodeParents.get(nodeEnd).size() < maxNumParents) 
            	{
            		parents.addAll(nodeParents.get(nodeEnd));
            		parents.add(nodeStart);
            		
            		scoreAdd[nodeStart][nodeEnd] = selectedMetric.getMetric(tableData, tableData, nodeEnd, parents) - baseScore;
            	}
            }
            else
            {
            	if(nodeParents.get(nodeEnd).size() > 0)
            	{
            		parents.addAll(nodeParents.get(nodeEnd));
            		parents.remove(Integer.valueOf(nodeStart));
            	}
            	if(parents.size() == 0)
            		parents.add(nodeEnd);
            		
            	
            	scoreDel[nodeStart][nodeEnd] = selectedMetric.getMetric(tableData, tableData, nodeEnd, parents) - baseScore;
            		
            }

		}
		

	}

}

