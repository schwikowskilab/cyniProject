package org.cytoscape.cyni;

import org.cytoscape.event.AbstractCyEvent;

/*
 * #%L
 * Cytoscape Cyni API (Cyni-api)
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


/**
 * This event will be fired when new Cyni Algorithm is deleted from {@link CyCyniAlgorithmManager}.
 * @CyAPI.Final.Class
 * @CyAPI.InModule cyni-api
 */
public final class CyniAlgorithmDeletedEvent extends AbstractCyEvent<CyCyniAlgorithmManager> {
	
	private final CyCyniAlgorithm cyni;
	
	/**
	 * Constructs the cyni algorithm deleted event.
	 * @param cyni   the cyni algorithm deleted from the cyni algorithm manager
	 */
	public CyniAlgorithmDeletedEvent(final CyCyniAlgorithmManager source,final CyCyniAlgorithm cyni) {
		super(source, CyniAlgorithmDeletedListener.class);
		this.cyni = cyni;
	}


	/**
	 * Returns the cyni algorithm deleted from the cyni algorithm manager.
	 * @return the cyni algorithm deleted from the cyni algorithm manager.
	 */
	public final CyCyniAlgorithm getCyniAlgorithm() {
		return cyni;
	}
}
