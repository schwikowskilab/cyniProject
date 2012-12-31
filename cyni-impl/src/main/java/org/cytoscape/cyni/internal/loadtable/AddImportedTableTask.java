package org.cytoscape.cyni.internal.loadtable;

import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

 class AddImportedTableTask extends AbstractTask {

	
	private final CyTableManager tableMgr;
	private final CyTableReader reader;
	
	AddImportedTableTask(	final CyTableManager tableMgr, final CyTableReader reader){
		this.tableMgr = tableMgr;
		this.reader = reader;
		System.out.println("AddImportedTableTask 1");

	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		System.out.println("AddImportedTableTask 2");
		if( this.reader != null && this.reader.getTables() != null)
			for (CyTable table : reader.getTables())
				tableMgr.addTable(table);
		else{
			
		}

	}

}
