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
package org.cytoscape.cyni;

import java.util.*;
import javax.swing.JPanel;
import java.awt.Component;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

/**
 * This is a class that contains several methods that are needed to display the context of a Cyni algorithm
 * 
 */
public class CyniAlgorithmContext {


	/**
	 * Indicates that only selected rows should be used.
	 */
	protected boolean selectedOnly;

	/**
	 * Indicates that there is the possibility to use only selected rows.
	 */
	private boolean supportsSelectedOnly;
	
	/**
	 * Indicates the parent of the context UI if a panel has been created
	 */
	private Component parent;


	/**
	 * The constructor
	 */
	public CyniAlgorithmContext(boolean supportsSelectedOnly) {
		this.supportsSelectedOnly = supportsSelectedOnly;
		parent = null;
		
	}
	
	/**
	 * Sets the parent component of this context so the context knows whether
	 * a swing UI has been created for this context
	 * 
	 * @param parent The swing component parent for this context 
	 */
	public void setParentSwingComponent(Component parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Returns the swing parent component of this context 
	 * 
	 * @return The swing parent component, if null means the context
	 * does not have a corresponding UI
	 */
	public Component getParentSwingComponent()
	{
		return parent;
	}
	
	/**
	 * This method tells cytoscape if this algorithm provides its own swing Panel to display 
	 * the context parameters. If so, it will not use Cytoscape tunables.
	 * 
	 * @return True if the panel is provided by the algorithm
	 */
	public boolean contextHasOwnSwingPanel()
	{
		return false;
	}
	
	/**
	 * Returns the swing Panel that will be used instead of the tunables panel
	 * 
	 * @return The swing Panel that will be added to the dialog used to 
	 * 			select the context parameters 
	 */
	public JPanel getContextSwingPanel()
	{
		return null;
	}
	
	/**
	 * This method checks the context values to validate them. It is only used if 
	 * tunables are not used. Otherwise tunables already has a validation procedure.
	 * 
	 * @return True if the context content is correct
	 */
	public boolean contextContentValid()
	{
		return true;
	}

	/**
	 * Set the flag that indicates that this algorithm should only operate on
	 * the currently selected rows.
	 * 
	 * @param selectedOnly
	 *            set to "true" if the algorithm should only apply to selected
	 *            rows only
	 */
	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	/**
	 * Returns whether the algorithm allows working with only selected rows
	 * @return True if the algorithm allows working with only selected rows
	 */
	public boolean useSelectedOnly() {
		return selectedOnly;
	}
	
	/**
	 * Fills a list with names of columns that has the same type that the one requested
	 * 
	 * @param attributeList The list of names of columns.
	 * @param table The CyTable to get the names of attributes.
	 * @param prefix The prefix that could be added to the name of columns
	 * @param type The type of the columns
	 */
	public void getListOfAttributes(List<String> attributeList, CyTable table, String prefix, Object type) {
		if(table == null)
			return;
		String[] names = new String[table.getColumns().size()];
		int i = 0;
		for (final CyColumn column : table.getColumns())
		{
			names[i] = column.getName();
			i++;
		}
		for (i = 0; i < names.length; i++) {
			if(names[i].equals(""))
				continue;
			if (type == String.class || type == List.class)
			{
				if (table.getColumn(names[i]).getType() == type)
				{
					attributeList.add(prefix+names[i]);
				}
			}
			else
			{
				if (table.getColumn(names[i]).getType() == Double.class || table.getColumn(names[i]).getType() == Float.class ||
						table.getColumn(names[i]).getType() == Integer.class) {
					attributeList.add(prefix+names[i]);
				}
			}
		}
	}
	
	/**
	 * Returns a list of the names of the columns that contains strings
	 * 
	 * @param table The CyTable to get the names of attributes.
	 * @return The list of column names
	 */
	public List<String> getAllAttributesStrings(CyTable table) {
		List<String> attributeList = new ArrayList<String>();
		if(table != null)
		{
			getListOfAttributes(attributeList, table,"", String.class);
			//Collections.sort(attributeList);
		}
		return attributeList;
	}
	
	/**
	 * Returns a list of the names of the columns that contains list of strings
	 * 
	 * @param table The CyTable to get the names of attributes.
	 * @return The list of column names
	 */
	public List<String> getAllAttributesLists(CyTable table) {
		List<String> attributeList = new ArrayList<String>();
		if(table != null)
		{
			getListOfAttributes(attributeList, table,"", List.class);
			//Collections.sort(attributeList);
		}
		return attributeList;
	}
	
	/**
	 * Returns a list of the names of the columns that contains numbers(Float,Integer,Double)
	 * 
	 * @param table The CyTable to get the names of attributes.
	 * @return The list of column names
	 */
	public List<String> getAllAttributesNumbers(CyTable table) {
		List<String> attributeList = new ArrayList<String>();
		if(table != null)
		{
			getListOfAttributes(attributeList, table,"", null);
			//Collections.sort(attributeList);
		}
		return attributeList;
	}

	
}
