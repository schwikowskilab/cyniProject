/*
  File: CyCyniAlgorithmManager.java

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

import java.util.Collection;
import java.util.List;

/**
 * This class provides access to the available Cyni algorithms.
 * 
 * @CyAPI.Api.Interface
 */
public interface CyCyniAlgorithmManager {


	/**
	 * Returns a Cyni algorithm of the specified name and category. Returns null if no
	 * algorithm exists with that name and category.
	 * 
	 * @param name
	 *            The name of the algorithm.
	 * @param category
	 *            The category of the algorithm.
	 * @return a Cyni algorithm of the specified name and category null if no
	 *         algorithm exists with that name.
	 */
	CyCyniAlgorithm getCyniAlgorithm(String name, CyniCategory category);

	/**
	 * Returns a collection of all available Cyni algorithms for the specified category.
	 * 	 @param category
	 *            The category of  algorithms.
	 * 
	 * @return a collection of all available Cyni algorithms.
	 */
	Collection<CyCyniAlgorithm> getAllCyniAlgorithms(CyniCategory category);
	
	/**
	 * Returns the list of names of cyni algorithms for a specified category.
	 * @param category
	 *            The category of  algorithms.
	 * @return the list of names of cyni algorithms.
	 */
	List<String> getAllCyniAlgorithmNames(CyniCategory category);


}
