package fr.systemsbiology.cyni.internal;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import fr.systemsbiology.cyni.CyCyniAlgorithmManager;
import fr.systemsbiology.cyni.CyCyniMetricsManager;
import fr.systemsbiology.cyni.CyniCategory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.swing.PanelTaskManager;

import javax.swing.JLabel;
import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

public class CyniControlPanel extends JPanel implements CytoPanelComponent2 {
	
	private static final String ID = "fr.systemsbiology.cyni";
	
	String panelName;


	public CyniControlPanel(final String PanelName,
			final CyCyniAlgorithmManager cyCyniAlgorithmManager, 
			final CyNetworkFactory netFactory,
			final CyNetworkViewFactory networkViewFactory,
			final CyNetworkManager networkManager,
			final CyNetworkTableManager netTableMgr,
			final CyRootNetworkManager rootNetMgr,
			final CyNetworkViewManager networkViewManager,
			final CyTableManager tableManager,
            final CySwingApplication desktop,
            final CyApplicationManager appMgr,
            final PanelTaskManager taskManager,
            final CyLayoutAlgorithmManager layoutManager,
            final CyCyniMetricsManager metricsManager,
            final VisualMappingManager vmMgr,
            final CyServiceRegistrar serviceRegistrar) {
		
		CyniPanel inferPanel = new CyniPanel(CyniCategory.INDUCTION,cyCyniAlgorithmManager,netFactory,networkViewFactory,networkManager,netTableMgr,
				rootNetMgr,networkViewManager,tableManager,desktop,appMgr,taskManager,layoutManager,metricsManager,vmMgr,serviceRegistrar);
		CyniPanel imputePanel = new CyniPanel(CyniCategory.IMPUTATION,cyCyniAlgorithmManager,netFactory,networkViewFactory,networkManager,netTableMgr,
				rootNetMgr,networkViewManager,tableManager,desktop,appMgr,taskManager,layoutManager,metricsManager,vmMgr,serviceRegistrar);
		CyniPanel discretePanel = new CyniPanel(CyniCategory.DISCRETIZATION,cyCyniAlgorithmManager,netFactory,networkViewFactory,networkManager,netTableMgr,
				rootNetMgr,networkViewManager,tableManager,desktop,appMgr,taskManager,layoutManager,metricsManager,vmMgr,serviceRegistrar);
		
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabPane.addTab(imputePanel.getName(), null, imputePanel, imputePanel.getToolTipText());
		tabPane.addTab(discretePanel.getName(), null, discretePanel, discretePanel.getToolTipText());
		tabPane.addTab(inferPanel.getName(), null, inferPanel,inferPanel.getToolTipText());
		
		tabPane.setSelectedIndex(2);
		
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
		
		panelName = PanelName;
		
		setOpaque(false);
	}


	public Component getComponent() {
		return this;
	}


	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}


	public String getTitle() {
		return panelName;
	}


	public Icon getIcon() {
		return null;
	}
	
	@Override
	public String getIdentifier() {
		return ID;
	}
}
