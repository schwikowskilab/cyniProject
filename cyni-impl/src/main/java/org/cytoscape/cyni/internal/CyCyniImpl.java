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
package org.cytoscape.cyni.internal;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.property.CyProperty;
import org.cytoscape.cyni.*;


/**
 * CyInductionsImpl is a singleton class that is used to register all available
 * Induction algorithms.  
 */
public class CyCyniImpl implements CyCyniAlgorithmManager {

	private final Map<String, CyCyniAlgorithm> InductionMap;
	private final Map<String, CyCyniAlgorithm> ImputationMap;
	private final Map<String, CyCyniAlgorithm> DiscretizationMap;
	private final CyProperty<Properties> cyProps;

	public CyCyniImpl(final CyProperty<Properties> p) {
		this.cyProps = p;
		InductionMap = new HashMap<String,CyCyniAlgorithm>();
		ImputationMap = new HashMap<String,CyCyniAlgorithm>();
		DiscretizationMap = new HashMap<String,CyCyniAlgorithm>();
	}

	/**
	 * Add a Induction to the Induction manager's list.  If menu is "null"
	 * it will be assigned to the "none" menu, which is not displayed.
	 * This can be used to register Inductions that are to be used for
	 * specific algorithmic purposes, but not, in general, supposed
	 * to be for direct user use.
	 *
	 * @param Induction The Induction to be added
	 * @param menu The menu that this should appear under
	 */
	public void addCyniAlgorithm(CyCyniAlgorithm Induction, Map props) {
		if ( Induction != null )
		{
			if(Induction.getCategory() == CyniCategory.INDUCTION)
			{
				InductionMap.put(Induction.getName(),Induction);
			}
			else if (Induction.getCategory() == CyniCategory.IMPUTATION)
			{
				ImputationMap.put(Induction.getName(),Induction);
			}else if (Induction.getCategory() == CyniCategory.DISCRETIZATION)
			{
				DiscretizationMap.put(Induction.getName(),Induction);
			}
		}
	}

	/**
	 * Remove a Induction from the Induction maanger's list.
	 *
	 * @param Induction The Induction to remove
	 */
	public void removeCyniAlgorithm(CyCyniAlgorithm Induction, Map props) {
		if ( Induction != null )
		{
			if(Induction.getCategory() == CyniCategory.INDUCTION)
			{
				InductionMap.remove(Induction.getName());
			}
			else if (Induction.getCategory() == CyniCategory.IMPUTATION)
			{
				ImputationMap.remove(Induction.getName());
			}else if (Induction.getCategory() == CyniCategory.DISCRETIZATION)
			{
				DiscretizationMap.remove(Induction.getName());
			}
		}
	}

	/**
	 * Get the Induction named "name".  If "name" does
	 * not exist, this will return null
	 *
	 * @param name String representing the name of the Induction
	 * @return the Induction of that name or null if it is not reigstered
	 */
	@Override
	public CyCyniAlgorithm getCyniAlgorithm(String name, CyniCategory category) {
		if (name != null)
		{
			if(category == CyniCategory.INDUCTION)
			{
				return InductionMap.get(name);
			}
			else if (category == CyniCategory.IMPUTATION)
			{
				return ImputationMap.get(name);
			}else if (category == CyniCategory.DISCRETIZATION)
			{
				return DiscretizationMap.get(name);
			} else
				return null;
		}
		return null;
	}

	/**
	 * Get all of the available Inductions.
	 *
	 * @return a Collection of all the available Inductions
	 */
	@Override
	public Collection<CyCyniAlgorithm> getAllCyniAlgorithms(CyniCategory category) {
		if(category == CyniCategory.INDUCTION)
		{
			return InductionMap.values();
		}
		else if (category == CyniCategory.IMPUTATION)
		{
			return ImputationMap.values();
		}else if (category == CyniCategory.DISCRETIZATION)
		{
			return DiscretizationMap.values();
		} else
			return null;
	}
	
	/**
	 * Get the name all available Inductions Metrics.
	 *
	 * @return a list all the available Inductions Metrics
	 */
	@Override
	public List<String> getAllCyniAlgorithmNames(CyniCategory category) {
		List<String> list;
		if(category == CyniCategory.INDUCTION)
		{
			list = new ArrayList<String>( InductionMap.keySet());
			return list;
		}
		else if (category == CyniCategory.IMPUTATION)
		{
			list = new ArrayList<String>( ImputationMap.keySet());
			return list;
		}else if (category == CyniCategory.DISCRETIZATION)
		{
			list = new ArrayList<String>( DiscretizationMap.keySet());
			return list;
		} else
			return null;
		
		
	}

	
}
