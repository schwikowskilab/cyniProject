package org.cytoscape.cyni.internal.loadtable;

import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TunableValidator;
import static org.cytoscape.work.TunableValidator.ValidationState.OK;

public class ReaderTableTask extends AbstractTask implements TunableValidator {

	@ProvidesTitle
	public String getTitle() {
		return "Import Attribute From Table";
	}

	@ContainsTunables
	public CyTableReader readerTask;

	
	public ReaderTableTask(CyTableReader readerTask , CyNetworkManager networkManager,  final CyRootNetworkManager rootNetMgr){
		System.out.println("ReaderTableTask 1");
		this.readerTask = readerTask;
		System.out.println("ReaderTableTask 2");
	}

	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		if ( readerTask instanceof TunableValidator ) {
			ValidationState readVS = ((TunableValidator)readerTask).getValidationState(errMsg);

			if ( readVS != OK )
				return readVS;
		}
		
		// If MapTableToNetworkTablesTask implemented TunableValidator, then
		// this is what we'd do:
		// return mappingTask.getValidationState(errMsg);

		return OK;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		System.out.println("ReaderTableTask 3");

		readerTask.run(taskMonitor);
	}

}
