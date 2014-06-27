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
package fr.systemsbiology.cyni.internal.loadtable;

import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

 class AddImportedTableTask extends AbstractTask {

	
	private final CyTableManager tableMgr;
	private final CyTableReader reader;
	
	AddImportedTableTask(	final CyTableManager tableMgr, final CyTableReader reader){
		this.tableMgr = tableMgr;
		this.reader = reader;

	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if( this.reader != null && this.reader.getTables() != null)
			for (CyTable table : reader.getTables())
			{
				if(table.getColumn(CyTable.SUID) == null)
				{
					table.createColumn(CyTable.SUID, Long.class,true);
					for ( CyRow row :  table.getAllRows()) 
					{
						row.set(CyTable.SUID, SUIDFactory.getNextSUID());
					}
				}
				tableMgr.addTable(table);
			}
		else{
			
		}

	}

}
