/*
  File: CyCyniImpl.java

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
