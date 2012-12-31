/*
  File: AbstractCyniTask.java

  Copyright (c) 2006, 2010-2012, The Cytoscape Consortium (www.cytoscape.org)

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
	 * This variable maps an integer representing a node/row with a list
	 * of integers that represents a list of parents for that node/row
	 * In oder to use it correctly, this variable needs to be initialized and a previous
	 * map between a node and its corresponding integer is also required
	 */
	protected Map<Integer, ArrayList<Integer>> nodeParents;

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
	 * @param netTableMgr The network table manager to generate a new network and its table
	 * @param rootNetMgr The root network manager to generate a new root network
	 */
	public AbstractCyniTask(String name, final AbstractCyniAlgorithmContext context, CyNetworkFactory networkFactory, 
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
		nodeParents = null;
		nThreads = Runtime.getRuntime().availableProcessors()-1;

	}

	@Override
	public final void run(final TaskMonitor taskMonitor) {
		
		// this is overridden by children and does the actual cyni algorithm
		doCyniTask(taskMonitor);

	}
	
	/**
	 * Add a column that belongs to a table associated to a network to a new table associated to a new network
	 * 
	 * @param origNet The original network
	 * @param newNet The new network
	 * @param origTable The original table
	 * @param tableType The type of the table
	 * @param namespace The namespace of the table
	 */
	protected void addColumns(final CyNetwork origNet,final CyNetwork newNet, final CyTable origTable,
		final Class<? extends CyIdentifiable> tableType,final String namespace) {
		CyTable from = origTable; 
		CyTable to = newNet.getTable(tableType, namespace); 
		CyRootNetwork origRoot = null ;
		CyRootNetwork newRoot = rootNetMgr.getRootNetwork(newNet);
		Map<String, CyTable> origRootTables  ;
			
		for (final CyColumn col : from.getColumns())
		{
			final String name = col.getName();
			
			if (to.getColumn(name) == null){
			final VirtualColumnInfo info = col.getVirtualColumnInfo();
			
				if (info.isVirtual() && origNet != null) {
					origRoot = rootNetMgr.getRootNetwork(origNet);
					origRootTables = netTableMgr.getTables(origRoot, tableType);
					if (origRootTables.containsValue(info.getSourceTable())) {
						// If the virtual column is from a root-network table, do NOT set this virtual column directly to
						// the new table:
						// Get the original column (not the virtual one!)
						final CyColumn origCol = info.getSourceTable().getColumn(info.getSourceColumn());
						// Copy the original column to the root-network's table first
						final CyTable newRootTable = newRoot.getTable(tableType, namespace);
						
						if (newRootTable.getColumn(origCol.getName()) == null)
							copyColumn(origCol, newRootTable);
					
						// Now we can add the new "root" column as a virtual one to the new network's table
						to.addVirtualColumn(name, origCol.getName(), newRootTable, CyIdentifiable.SUID, col.isImmutable());
					} else {
						// Otherwise (e.g. virtual column from a global table) just add the virtual column directly
						addVirtualColumn(col, to);
					}
				} else {
					// Not a virtual column, so just copy it to the new network's table
					copyColumn(col, to);
				}
			}
		}
	}
	
	/**
	 * Add a virtual column to a table
	 * 
	 * @param col The column to add.
	 * @param subTable The CyTable to add the column.
	 */
	protected void addVirtualColumn(CyColumn col, CyTable subTable){
		VirtualColumnInfo colInfo = col.getVirtualColumnInfo();
		CyColumn checkCol= subTable.getColumn(col.getName());
		
		if (checkCol == null)
			subTable.addVirtualColumn(col.getName(), colInfo.getSourceColumn(), colInfo.getSourceTable(), colInfo.getTargetJoinKey(), true);
		else if (!checkCol.getVirtualColumnInfo().isVirtual() ||
					!checkCol.getVirtualColumnInfo().getSourceTable().equals(colInfo.getSourceTable()) ||
					!checkCol.getVirtualColumnInfo().getSourceColumn().equals(colInfo.getSourceColumn()))
			subTable.addVirtualColumn(col.getName(), colInfo.getSourceColumn(), colInfo.getSourceTable(), colInfo.getTargetJoinKey(), true);
	}

	/**
	 * Copy a column to a table
	 * 
	 * @param col The column to copy
	 * @param subTable The CyTable to add the column.
	 */
	protected void copyColumn(CyColumn col, CyTable subTable) {
		if (List.class.isAssignableFrom(col.getType()))
			subTable.createListColumn(col.getName(), col.getListElementType(), false);
		else
			subTable.createColumn(col.getName(), col.getType(), false);	
	}
	
	/**
	 * Clone a row to be added to a new network
	 * 
	 * @param newNet The new network.
	 * @param tableType The type of the table.
	 * @param from The source row
	 * @param to The target row
	 */
	protected void cloneRow(final CyNetwork newNet, final Class<? extends CyIdentifiable> tableType, final CyRow from,
			final CyRow to) {
		final CyRootNetwork newRoot = rootNetMgr.getRootNetwork(newNet);
		Map<String, CyTable> rootTables = netTableMgr.getTables(newRoot, tableType);
		
		for (final CyColumn col : to.getTable().getColumns()){
			final String name = col.getName();
			
			if (name.equals(CyIdentifiable.SUID))
				continue;
			
			final VirtualColumnInfo info = col.getVirtualColumnInfo();
			
			// If it's a virtual column whose source table is assigned to the new root-network,
			// then we have to set the value, because the rows of the new root table may not have been copied yet
			if (!info.isVirtual() || rootTables.containsValue(info.getSourceTable()))
				to.set(name, from.getRaw(name));
		}
	}
	
	/**
	 * Checks if the table is associated to a network, if so it return the associated network otherwise it returns null
	 * 
	 * @param table The table to check
	 * @return null if no network is associated to the table or the network associated
	 */
	protected CyNetwork getNetworkAssociatedToTable(CyTable table)
	{
		CyNetwork networkFound = null;
		for(CyNetwork net : netMgr.getNetworkSet())
		{
			if(table.equals(net.getDefaultNodeTable()))
			{
				networkFound = net;
			}
		}
		return networkFound;
	}
	
	/**
	 * Remove nodes that does not have any edge
	 * 
	 * @param network The network to remove nodes
	 */
	protected void removeNodesWithoutEdges(CyNetwork network)
	{
		ArrayList<CyNode> list = new ArrayList<CyNode>();
		
		for(CyNode node : network.getNodeList())
		{
			if(network.getAdjacentEdgeList(node, CyEdge.Type.ANY).size() == 0)
				list.add(node);	
		}
		if(list.size() > 0)
			network.removeNodes(list);
	}
	
	
	/**
	 * This method displays the new network and return a network view that might be used to modify the display features such as the layout
	 * 
	 * @param newNetwork The new network
	 * @param oldNetwork The old network in case we are using a table associated to a network otherwise it is a null
	 * @param directed Tells whether the new network is a directed graph or not
	 * @return the new network view
	 */
	protected CyNetworkView displayNewNetwork(CyNetwork newNetwork,CyNetwork oldNetwork, boolean directed)
	{
		CyNetworkView newNetworkView;
		netMgr.addNetwork(newNetwork);
		newNetworkView = viewFactory.createNetworkView(newNetwork);
		if(oldNetwork != null && viewMgr.getNetworkViews(oldNetwork).size() > 0)
		{
			final VisualStyle style = vmMgr.getVisualStyle(viewMgr.getNetworkViews(oldNetwork).iterator().next());
			style.apply(newNetworkView);
		}
		if(directed)
		{
			for(View<CyEdge> edgeView : newNetworkView.getEdgeViews())
			{
				edgeView.setVisualProperty(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE, ArrowShapeVisualProperty.ARROW);
			}
		}
		newNetworkView.updateView();
		viewMgr.addNetworkView(newNetworkView);
		return newNetworkView;
	}
	
	/**
	 * This method allows checking if there is a cycle in a graph starting from a defined node. It also uses the nodeParents variable
	 * so to make a good use of this function, the nodeParents variable needs to be previously initialized
	 * 
	 * @param nodeToCheck The index corresponding to the starting node from where we want to check if there is a cycle in the graph
	 * @return True if the graph is cyclic or false if not or the nodeParents has not been initialized
	 */
	public boolean isGraphCyclic( int nodeToCheck )
	{
		if(nodeParents == null)
			return false;
		boolean[] visited = new boolean[nodeParents.size()];
		
		return isCyclic(visited,nodeToCheck, nodeToCheck);
		
		
	}
	
	private boolean isCyclic(boolean[] visited, int nodeToCheck, int start)
	{
		boolean result = false;
		int node = 0;
		
		if(visited[nodeToCheck])
		{
			if(nodeToCheck == start)
			{
				return true;
			}
			else
				return false;
		}
		
		visited[nodeToCheck] = true;
		
		for (ListIterator<Integer> it = nodeParents.get(nodeToCheck).listIterator(); it.hasNext(); )
		{
			node = it.next();
			
			if(isCyclic(visited,node, start))
			{
				result = true;
				break;
			}
		}
		
		return result;
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
