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
