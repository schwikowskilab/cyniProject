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
package fr.systemsbiology.cyni;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;



/**
 * This is a class that contains several utilities that will help creating a new bayesian network
 * 
 */
public class CyniBayesianUtils  {

	
	/**
	 * This variable maps an integer representing a node/row with a list
	 * of integers that represents a list of parents for that node/row
	 * In oder to use it correctly, this variable needs to be initialized and a previous
	 * map between a node and its corresponding integer is also required
	 */
	protected Map<Integer, ArrayList<Integer>> nodeParents;


	/**
	 * Constructor.
	 * 
	 * @param nodeParents The map used to know at any time the list of parents a node has. 
	 */
	public CyniBayesianUtils( Map<Integer, ArrayList<Integer>> nodeParents) {

		this.nodeParents = nodeParents;
		
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
	
	
}
