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
package fr.systemsbiology.cyni.internal.inductionAlgorithms.HillClimbingAlgorithm;



import java.awt.Component;
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
import fr.systemsbiology.cyni.*;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;



/**
 * The HillClimbingInductionTask performs the actual network inference algorithm
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
	private Operation [] [] scoreOperations;
	private boolean [] [] nodeAscendantsReach;
	private boolean [] [] nodeParentsMatrix;
	private boolean [] [] edgeBlocked;
	private CyCyniMetric selectedMetric;
	private boolean removeNodes;
	private TreeSet<Operation> scoreTree;
	private CyniNetworkUtils netUtils;
	private boolean changeSign;
	private CyniBayesianUtils bayesUtils;
	private Map<Integer, ArrayList<Integer>> nodeParents;
	private Component parent;
	private static int iteration = 0;


	/**
	 * Creates a new HillClimbingInductionTask object.
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
		this.changeSign = false;
		mapNodeIndex =  new HashMap< CyNode, Integer>();
		mapIndexNode =  new HashMap<Integer, CyNode>();
		orig2NewNodeMap = new WeakHashMap<CyNode, CyNode>();
		new2OrigNodeMap = new WeakHashMap<CyNode, CyNode>();
		nodeParents = new HashMap<Integer, ArrayList<Integer>>();
		parent = context.getParentSwingComponent();
		this.netUtils = new CyniNetworkUtils(networkViewFactory,networkManager,networkViewManager,netTableMgr,rootNetMgr,vmMgr);
		this.bayesUtils = new CyniBayesianUtils(nodeParents);
		iteration++;
		
	}

	/**
	 *  Perform actual Induction task.
	 *  This creates the default square Induction.
	 */
	@Override
	final protected void doCyniTask(final TaskMonitor taskMonitor) {
		
		Integer numNodes = 1;
		CyTable nodeTable, netTable;
		CyEdge edge;
		CyNode newNode;
		CyLayoutAlgorithm layout;
		Double progress = 0.0d;
		Double step = 0.0;
		int i=0;
		boolean loopFound = false;
		int nRows,added,removed,reversed;
		CyNetworkView newNetworkView ;
		CyNetwork networkSelected = null;
		boolean okToProceed = true;
		Operation operationAdd = new Operation("Add");
		Operation operationReverse = new Operation("Reverse");
		Operation chosenOperation;
		Operation lastOperation = new Operation("");
		
		newNetwork = netFactory.createNetwork();
		
		networkSelected = netUtils.getNetworkAssociatedToTable(table);
		
		taskMonitor.setTitle("Cyni - Hill Climbing Inference Algorithm");
		taskMonitor.setStatusMessage("Generating Hill Climbing Inference...");
		taskMonitor.setProgress(progress);
		
		netUtils.setNetworkName(newNetwork, "HC Inference " + iteration);
		
		if(selectedMetric.getName() == "Entropy.cyni" || selectedMetric.getName() == "AIC.cyni" || selectedMetric.getName() == "MDL.cyni")
			changeSign = true;
		
		if(selectedMetric.getTagsList().contains(CyniMetricTags.LOW_METRIC.toString()))
			changeSign = true;
		//netUtils.addColumns(networkSelected,newNetwork,table,CyNode.class, CyNetwork.LOCAL_ATTRS);
		
		netUtils.copyNodeColumns(newNetwork, table);
		
		for (CyRow origRow : table.getAllRows()) 
		{
			if(selectedOnly)
			{
				if(networkSelected != null && !origRow.get(CyNetwork.SELECTED, Boolean.class))
					continue;
			}
			newNode = newNetwork.addNode();
			netUtils.cloneNodeRow(newNetwork, origRow, newNode);
			if(networkSelected != null)
			{
				orig2NewNodeMap.put(networkSelected.getNode(origRow.get(CyNetwork.SUID,Long.class)), newNode);
				new2OrigNodeMap.put(newNode, networkSelected.getNode(origRow.get(CyNetwork.SUID,Long.class)));
				if(newNetwork.getRow(newNode).get(CyNetwork.SELECTED,Boolean.class ) == true)
					newNetwork.getRow(newNode).set(CyNetwork.SELECTED, false);
			}
			
			if(!origRow.isSet(CyNetwork.NAME))
			{
				if(table.getPrimaryKey().getType().equals(String.class) && networkSelected == null)
					newNetwork.getRow(newNode).set(CyNetwork.NAME,origRow.get(table.getPrimaryKey().getName(),String.class));
				else
					newNetwork.getRow(newNode).set(CyNetwork.NAME, "Node " + numNodes);
			}
			numNodes++;
		}
		
		nodeTable = newNetwork.getDefaultNodeTable();
		netTable = newNetwork.getDefaultNetworkTable();
		
		// Create the CyniTable
		CyniTable data = selectedMetric.getCyniTable(nodeTable,attributeArray.toArray(new String[0]), false, false, false);
		
		if(data.hasAnyMissingValue())
		{
			outputMessage = "The data selected contains missing values.\n " +
					"Therefore, this algorithm can not proceed with these conditions.";
			if(parent != null)
			{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(parent,outputMessage , "Warning", JOptionPane.WARNING_MESSAGE);
					}
				});
			}
			newNetwork.dispose();
			return;
		}
		
		nRows = data.nRows();
		step = 1.0 / nRows;
		
		progress = progress + step;
		taskMonitor.setProgress(progress);
		
		for (i = 0; i< nRows;i++)
		{
			nodeParents.put(i, new ArrayList<Integer>());
			
			mapNodeIndex.put( newNetwork.getNode(nodeTable.getRow(data.getRowLabel(i)).get(CyNetwork.SUID, Long.class)) ,i);
			mapIndexNode.put(  i, newNetwork.getNode(nodeTable.getRow(data.getRowLabel(i)).get(CyNetwork.SUID, Long.class)));
			
		}
		
		scoreTree = new TreeSet<Operation>( new Comparator<Operation>() {
			 public int compare(Operation op1, Operation op2) {
				 if(op1.score > op2.score)
					 return -1;
				 if(op1.score < op2.score)
					 return 1;
				 if(op1.nodeChild > op2.nodeChild)
					 return -1;
				 if(op1.nodeChild < op2.nodeChild)
					 return 1;
				 if(op1.nodeParent > op2.nodeParent)
					 return -1;
				 if(op1.nodeParent < op2.nodeParent)
					 return 1;
				 return 0;
			 }
		});
		
		edgeBlocked = new boolean [nRows][nRows];
		nodeParentsMatrix = new boolean [nRows][nRows];
		nodeAscendantsReach = new boolean [nRows][nRows];
		
		if(networkSelected != null && useNetworkAsInitialSearch)
		{
			netUtils.copyEdgeColumns(newNetwork,networkSelected.getDefaultEdgeTable());
			for (final CyEdge origEdge : networkSelected.getEdgeList()) 
			{
				
				final CyNode newSource = orig2NewNodeMap.get(origEdge.getSource());
				final CyNode newTarget = orig2NewNodeMap.get(origEdge.getTarget());
				if(mapNodeIndex.get(newTarget) == null || mapNodeIndex.get(newSource) == null)
					continue;
				if(selectedOnly)
				{
					if(!networkSelected.getRow(origEdge).get(CyNetwork.SELECTED, Boolean.class))
						continue;
				}
				if(newSource != null && newTarget != null)
				{
					final boolean newDirected = origEdge.isDirected();
					final CyEdge newEdge = newNetwork.addEdge(newSource, newTarget, newDirected);
					netUtils.cloneEdgeRow(newNetwork, networkSelected.getRow(origEdge, CyNetwork.LOCAL_ATTRS), newEdge);
					if(edgesBlocked)
					{
						if(networkSelected.getRow(origEdge, CyNetwork.LOCAL_ATTRS).get(CyNetwork.SELECTED, Boolean.class))
							edgeBlocked[mapNodeIndex.get(newSource)][mapNodeIndex.get(newTarget)] = true;
					}
					if(newNetwork.getRow(newEdge).get(CyNetwork.SELECTED,Boolean.class ) == true)
						newNetwork.getRow(newEdge).set(CyNetwork.SELECTED, false);
					if(!newDirected )
					{
						outputMessage = "The data selected belongs to a network that is not directed.\n " +
								"Therefore, this algorithm is not able to proceed with parameters requested";
						if(parent != null)
						{
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(parent, outputMessage, "Warning", JOptionPane.WARNING_MESSAGE);
								}
							});
						}
						newNetwork.dispose();
						return;
					}
				}
			}
				
			initParentsMap(newNetwork);
			for ( i=0;i<nRows;i++) 
			{
				updateAscendantsOfNode(i);
			}
			for ( i = 0; i< nRows; i++) {	
				if(bayesUtils.isGraphCyclic( i))
				{
					outputMessage = "The data selected belongs to a network that is not acyclic.\n " +
							"This algorithm is a bayesian network algorithm and requires a Directed Acyclic Graph(DAG) to perform";
					if(parent != null)
					{
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(parent,outputMessage , "Warning", JOptionPane.WARNING_MESSAGE);
							}
						});
					}
					newNetwork.dispose();
					return;
				}
			}
			
		}

		if(newNetwork.getNodeCount() == 0)
		{
			outputMessage = "There are no nodes selected in the associated network. Therefore, the algorithm can not proceed.";
			if(parent != null)
			{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(parent,outputMessage , "Warning", JOptionPane.WARNING_MESSAGE);
					}
				});
			}
			newNetwork.dispose();
			return;
		}
		scoreOperations = new Operation [nRows][nRows];
		
		if(selectedMetric.getName() == "Entropy.cyni")
		{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("Conditional", true);
			params.put("LogBase", "log10");
			selectedMetric.setParameters(params);
		}
		
		selectedMetric.initMetric();
		
		taskMonitor.setStatusMessage("Initializing Cache..." );
		initCache(data, selectedMetric, taskMonitor);
		taskMonitor.setStatusMessage("Cache Initialized" );
		if(newNetwork.getDefaultEdgeTable().getColumn("Metric") == null)
			netUtils.createEdgeColumn(newNetwork,"Metric", String.class, false);	
		if(newNetwork.getDefaultEdgeTable().getColumn("Score") == null)
			netUtils.createEdgeColumn(newNetwork,"Score", Double.class, false);	
		netUtils.createNetworkColumn(newNetwork,"Added Edges", Integer.class, false);	
		netUtils.createNetworkColumn(newNetwork,"Removed Edges", Integer.class, false);	
		netUtils.createNetworkColumn(newNetwork,"Reversed Edges", Integer.class, false);	
		
		progress += 0.5; 
		added = 0;
		removed = 0;
		reversed = 0;
		
		while(okToProceed)
		{
			if (cancelled)
				break;
			operationAdd.resetParameters();
			operationReverse.resetParameters();
			taskMonitor.setStatusMessage("Optimal solution search. Added edges: " + added + " Removed edges: " + removed + " Reversed edges: " + reversed  );
			
			chosenOperation=findBestOperation(data, operationAdd);
			
			if(reversalOption)
			{
				findBestReverseEdge(data, operationReverse);
				
				if(operationReverse.score > chosenOperation.score)
				{
					//Only accept reverse option if it is not producing a loop
					if(lastOperation.type == "Reverse")
					{
						if(lastOperation.nodeChild != operationReverse.nodeParent ||  lastOperation.nodeParent != operationReverse.nodeChild)
							chosenOperation = operationReverse;
					}
					else
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
					nodeParentsMatrix[chosenOperation.nodeChild][chosenOperation.nodeParent] = true;
					updateCache(data, selectedMetric,chosenOperation.nodeChild);
					updateAscendantsAfterAdd(chosenOperation.nodeParent,chosenOperation.nodeChild);
					added++;
					newNetwork.getRow(edge).set("Score", chosenOperation.score);
					newNetwork.getRow(edge).set("Metric",selectedMetric.toString());
				}
				if(chosenOperation.type == "Delete")
				{
					newNetwork.removeEdges(newNetwork.getConnectingEdgeList(mapIndexNode.get(chosenOperation.nodeParent), mapIndexNode.get(chosenOperation.nodeChild), CyEdge.Type.DIRECTED));
					nodeParents.get(chosenOperation.nodeChild).remove(Integer.valueOf(chosenOperation.nodeParent));
					nodeParentsMatrix[chosenOperation.nodeChild][chosenOperation.nodeParent] = false;
					updateCache(data, selectedMetric,chosenOperation.nodeChild);
					updateAscendantsAfterDelete(chosenOperation.nodeParent,chosenOperation.nodeChild);
					removed++;
				}
				if(chosenOperation.type == "Reverse")
				{
					newNetwork.removeEdges(newNetwork.getConnectingEdgeList(mapIndexNode.get(chosenOperation.nodeParent), mapIndexNode.get(chosenOperation.nodeChild), CyEdge.Type.DIRECTED));
					edge = newNetwork.addEdge( mapIndexNode.get(chosenOperation.nodeChild), mapIndexNode.get(chosenOperation.nodeParent), true);
					newNetwork.getRow(edge).set("name", newNetwork.getRow( mapIndexNode.get(chosenOperation.nodeChild)).get("name", String.class)
							+ " (HC) " + newNetwork.getRow( mapIndexNode.get(chosenOperation.nodeParent)).get("name", String.class));
					nodeParents.get(chosenOperation.nodeChild).remove(Integer.valueOf(chosenOperation.nodeParent));
					nodeParentsMatrix[chosenOperation.nodeChild][chosenOperation.nodeParent] = false;
					updateAscendantsAfterDelete(chosenOperation.nodeParent,chosenOperation.nodeChild);
					updateCache(data, selectedMetric,chosenOperation.nodeChild);
					nodeParents.get(chosenOperation.nodeParent).add(Integer.valueOf(chosenOperation.nodeChild));
					nodeParentsMatrix[chosenOperation.nodeParent][chosenOperation.nodeChild] = true;
					updateCache(data, selectedMetric,chosenOperation.nodeParent);
					updateAscendantsAfterAdd(chosenOperation.nodeChild,chosenOperation.nodeParent);
					reversed++;
					newNetwork.getRow(edge).set("Score", chosenOperation.score);
					newNetwork.getRow(edge).set("Metric",selectedMetric.toString());
				}			
				lastOperation.type = chosenOperation.type;
				lastOperation.nodeParent = chosenOperation.nodeParent;
				lastOperation.nodeChild = chosenOperation.nodeChild;
				
				
			}
			else
				okToProceed = false;
			
			progress = progress + step;
			taskMonitor.setProgress(progress);
		}
		scoreTree.clear();
		
		if (!cancelled)
		{
			netTable.getRow(newNetwork.getSUID()).set("Added Edges", added);
			netTable.getRow(newNetwork.getSUID()).set("Removed Edges", removed);
			netTable.getRow(newNetwork.getSUID()).set("Reversed Edges", reversed);
			if(removeNodes)
				netUtils.removeNodesWithoutEdges(newNetwork);
			taskMonitor.setStatusMessage("New network " +  newNetwork.getRow(newNetwork).get(CyNetwork.NAME, String.class)+ " created");
			newNetworkView = netUtils.displayNewNetwork(newNetwork,networkSelected, true);
			taskMonitor.setProgress(1.0d);
			layout = layoutManager.getDefaultLayout();
			Object context = layout.getDefaultLayoutContext();
			insertTasksAfterCurrentTask(layout.createTaskIterator(newNetworkView, context, CyLayoutAlgorithm.ALL_NODE_VIEWS,""));
		}
	
	}
	
	/*
	 * Initialize the list of parents and the parents matrix for each node. The parents matrix is the fast way to know if
	 * a node is a parent of another node
	 */
	private void initParentsMap(CyNetwork network)
	{
		for ( CyEdge edge : network.getEdgeList()) {
			if(mapNodeIndex.get(edge.getTarget()) == null || mapNodeIndex.get(edge.getSource()) == null)
				continue;
			nodeParents.get(mapNodeIndex.get(edge.getTarget())).add(Integer.valueOf(mapNodeIndex.get(edge.getSource())));
			nodeParentsMatrix[mapNodeIndex.get(edge.getTarget())][mapNodeIndex.get(edge.getSource())] = true;
		}
	}
	
	/*
	 * Update the matrix that allows to know for each node which other nodes are its ascendants
	 */
	private void updateAscendantsOfNode(int nodeToUpdate )
	{
		ArrayList<Integer> ascendantsList = new ArrayList<Integer>();
		int pos = -1;
		boolean [] nodeCheckList = new boolean [nodeParents.size()];
		int nodeToCheck = nodeToUpdate;
		
		Arrays.fill(nodeAscendantsReach[nodeToUpdate], false);
		nodeCheckList[nodeToUpdate] = true;
		while(pos != ascendantsList.size())
		{
			for(int node : nodeParents.get(nodeToCheck))
			{
				nodeAscendantsReach[nodeToUpdate][node] = true;
				if(!nodeCheckList[node])
				{
					ascendantsList.add(node);
					nodeCheckList[node] = true;
				}
			
			}
			pos++;
			if(pos < ascendantsList.size())
				nodeToCheck = ascendantsList.get(pos);
		}
		
	}
	
	/*
	 * Update the ascendants matrix after adding a new edge
	 */
	private void updateAscendantsAfterAdd(int parent , int child)
	{
		ArrayList<Integer> parentsList = new ArrayList<Integer>();
		ArrayList<Integer> childsList = new ArrayList<Integer>();
		int i;
		for(i=0;i<nodeParents.size();i++)
		{
			if(nodeAscendantsReach[i][child])
				childsList.add(i);
			if(nodeAscendantsReach[parent][i])
				parentsList.add(i);
		}
		parentsList.add(parent);
		childsList.add(child);		
		for(int c: childsList)
		{
			for(int p: parentsList)
			{
				nodeAscendantsReach[c][p] = true;
			}
		}
	}
	
	/*
	 * Update the ascendants matrix after deleting an existing edge
	 */
	private void updateAscendantsAfterDelete(int parent , int child)
	{
		ArrayList<Integer> childsList = new ArrayList<Integer>();
		int i;
		
		for(i=0;i<nodeParents.size();i++)
		{
			if(nodeAscendantsReach[i][child])
				childsList.add(i);
		}
		childsList.add(child);
		for(int c: childsList)
		{
			updateAscendantsOfNode(c);
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
        
        for(i = 0;i<nRows;i++) 
        {
			nodeIndex =i;
			parents.clear();
			if(nodeParents.get(nodeIndex).size() > 0)
				parents.addAll(nodeParents.get(nodeIndex));
			else
				parents.add(nodeIndex);
			baseScores[nodeIndex] = metric.getMetric(data, data, nodeIndex,parents);
		}

        for (int nodeStart = 0; nodeStart < nRows; nodeStart++) 
        {
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
    	    if (cancelled)
    	    	return;
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
		
	    removeElements(nodeEnd,nRows);
		for (int nodeStart = 0; nodeStart < nRows; nodeStart++) {
            if (nodeStart != nodeEnd) {
            	
				executor.execute(new ThreadedGetMetric(data,nodeStart,nodeEnd,baseScore));
			}
            if (cancelled)
				break;
        }
		executor.shutdown();
		// Wait until all threads are finish
		 try {
         	executor.awaitTermination(7, TimeUnit.DAYS);
         } catch (Exception e) {}
	}
	
	
	private Operation findBestOperation(CyniTable data, Operation operation)
	{
		boolean notFound = true;
		ArrayList<Operation> opList = new ArrayList<Operation>();
		Operation opTemp = new Operation();
		while(notFound)
		{
			opTemp = scoreTree.pollFirst();
			opList.add(opTemp);
			if(opTemp.type == "Add")
			{
				if ((nodeParents.get(opTemp.nodeChild).size() < maxNumParents) && !nodeAscendantsReach[opTemp.nodeParent][opTemp.nodeChild]) 
				{
					operation = opTemp;
					notFound = false;
				}
			}
			if(opTemp.type == "Delete")
			{
				if(!edgeBlocked[opTemp.nodeParent][opTemp.nodeChild])
				{
					operation = opTemp;
					notFound = false;
				}
			}
		}
		
		scoreTree.addAll(opList);
		return opTemp;
	}
	
	private void removeElements(int nodeEnd, int nRows)
	{
		for (int nodeStart = 0; nodeStart < nRows; nodeStart++) {
			if (nodeStart != nodeEnd)
				scoreTree.remove(scoreOperations[nodeStart][nodeEnd]);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void findBestReverseEdge(CyniTable data, Operation operation)
	{
		int nRows = data.nRows();
		int nodeParent;
		ArrayList<Integer> tempList;
		
		for (int nodeChild = 0; nodeChild < nRows; nodeChild++) {
			tempList = (ArrayList<Integer>)nodeParents.get(nodeChild).clone();
			for (Iterator<Integer> it = tempList.iterator(); it.hasNext();) {
				nodeParent = it.next();
				if(scoreOperations[nodeParent][nodeChild] == null || scoreOperations[nodeChild][nodeParent] == null)
					continue;//The option to add an edge is not possible because maximum reached
				if(nodeParents.get(nodeParent).size() >= maxNumParents || edgeBlocked[nodeParent][nodeChild])
					continue;
				if((scoreOperations[nodeParent][nodeChild].score + scoreOperations[nodeChild][nodeParent].score) > operation.score)
				{	
					nodeParents.get(nodeChild).remove(Integer.valueOf(nodeParent));
					nodeParents.get(nodeParent).add(Integer.valueOf(nodeChild));
					if(!bayesUtils.isGraphCyclic( nodeParent) )
					{
						operation.score = scoreOperations[nodeParent][nodeChild].score + scoreOperations[nodeChild][nodeParent].score;
						operation.nodeParent = nodeParent;
						operation.nodeChild = nodeChild;
					}
					nodeParents.get(nodeParent).remove(Integer.valueOf(nodeChild));
					nodeParents.get(nodeChild).add(Integer.valueOf(nodeParent));
				}
			}
		}
	}
	
	class Operation{
		
		public Operation()
		{
			nodeParent = -1;
			nodeChild = -1;	
			score =  -1E100;
		}
		
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
		
            if(!nodeParentsMatrix[nodeEnd][nodeStart])
            {
            	if (nodeParents.get(nodeEnd).size() < maxNumParents) 
            	{
            		Operation op = new Operation ("Add");
            		parents.addAll(nodeParents.get(nodeEnd));
            		parents.add(nodeStart);
            		
            		op.score = selectedMetric.getMetric(tableData, tableData, nodeEnd, parents) - baseScore;
            		if(changeSign)
            			op.score = -1.0*op.score;
            		op.nodeChild = nodeEnd;
            		op.nodeParent = nodeStart;
            		scoreOperations[nodeStart][nodeEnd] = op;
            		synchronized (scoreTree){
            			scoreTree.add(op);
            		}
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
            		
            	Operation op = new Operation ("Delete");
            	op.score = selectedMetric.getMetric(tableData, tableData, nodeEnd, parents) - baseScore;
            	if(changeSign)
        			op.score = -1.0*op.score;
        		op.nodeChild = nodeEnd;
        		op.nodeParent = nodeStart;
        		scoreOperations[nodeStart][nodeEnd] = op;
            	synchronized (scoreTree){
            		scoreTree.add(op);
            	}
            		
            }
            parents.clear();

		}
		

	}

}

