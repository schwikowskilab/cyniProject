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

import java.util.HashMap;
import java.util.Properties;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.DynamicTaskFactoryProvisioner;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.task.read.LoadTableFileTaskFactory;
import org.cytoscape.task.read.LoadTableURLTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cyni.*;
import org.cytoscape.cyni.internal.metrics.*;
import org.cytoscape.cyni.internal.inductionAlgorithms.BasicAlgorithm.*;
import org.cytoscape.cyni.internal.imputationAlgorithms.BPCAFillAlgorithm.*;
import org.cytoscape.cyni.internal.imputationAlgorithms.RAVGFillAlgorithm.*;
import org.cytoscape.cyni.internal.imputationAlgorithms.ZeroFillAlgorithm.*;
import org.cytoscape.cyni.internal.inductionAlgorithms.K2Algorithm.*;
import org.cytoscape.cyni.internal.inductionAlgorithms.HillClimbingAlgorithm.*;
import org.cytoscape.cyni.internal.discretizationAlgorithms.EqualWidthFreqDiscretization.*;
import org.cytoscape.cyni.internal.loadtable.*;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.TITLE;
import org.cytoscape.io.read.CyTableReaderManager;
import org.osgi.framework.BundleContext;



public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {

		CyProperty cyPropertyServiceRef = getService(bc,CyProperty.class,"(cyPropertyName=cytoscape3.props)");
		CyNetworkFactory cyNetworkFactoryRef = getService(bc,CyNetworkFactory.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		CyTableManager cyTableServiceRef = getService(bc, CyTableManager.class);
		CySwingApplication cySwingApplicationServiceRef = getService(bc,CySwingApplication.class);	
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc, CyNetworkManager.class);
		PanelTaskManager panelTaskManagerServiceRef = getService(bc, PanelTaskManager.class);
		CyLayoutAlgorithmManager cyLayoutsServiceRef = getService(bc, CyLayoutAlgorithmManager.class);
		DynamicTaskFactoryProvisioner dynamicTaskFactoryProvisionerServiceRef = getService(bc, DynamicTaskFactoryProvisioner.class);
		CyTableReaderManager cyDataTableReaderManagerServiceRef = getService(bc,CyTableReaderManager.class);
		TunableSetter tunableSetterServiceRef = getService(bc,TunableSetter.class);
		CyRootNetworkManager rootNetworkManagerServiceRef  = getService(bc, CyRootNetworkManager.class);
		CyNetworkTableManager cyNetworkTableManagerServiceRef  = getService(bc, CyNetworkTableManager.class);
		VisualMappingManager visualMappingManagerServiceRef = getService(bc, VisualMappingManager.class);

		
		BasicInduction basicInduction = new BasicInduction();
		K2Induction k2Induction = new K2Induction();
		HillClimbingInduction HCInduction = new HillClimbingInduction();
		
		BPCAImputation bpca = new BPCAImputation();
		ZeroImputation zero = new ZeroImputation();
		RAVGImputation ravg = new RAVGImputation();
		
		EqualDiscretization equal = new EqualDiscretization();
		
		CorrelationMetric  correlationMetric = new CorrelationMetric();
		RankCorrelationMetric  rankCorrelationMetric = new RankCorrelationMetric();
		KendallTauCorrelationMetric kendallCorrelationMetric = new KendallTauCorrelationMetric();
		BayesianMetric  bayesMetric = new BayesianMetric();
		EntropyMetric  entropyMetric = new EntropyMetric();
		MDLMetric  mdlMetric = new MDLMetric();
		AICMetric  aicMetric = new AICMetric();
		BayesDirichletEquivalentMetric  bdeMetric = new BayesDirichletEquivalentMetric();
		
		CyCyniImpl cyInduction = new CyCyniImpl(cyPropertyServiceRef);
		
		CyCyniMetricsImpl cyCyniMetrics = new CyCyniMetricsImpl(cyPropertyServiceRef);
		
		/******************************************************************************************************************************************/
		/**This code is temporal and it will be removed when cy3 allows loading tables without mapping them to other elements as nodes or networks*/
		
		LoadAttributesFileTaskFactoryImpl loadAttrsFileTaskFactory = new LoadAttributesFileTaskFactoryImpl(cyDataTableReaderManagerServiceRef, 
				tunableSetterServiceRef,cyNetworkManagerServiceRef, cyTableServiceRef, rootNetworkManagerServiceRef );
		LoadAttributesURLTaskFactoryImpl loadAttrsURLTaskFactory = new LoadAttributesURLTaskFactoryImpl(cyDataTableReaderManagerServiceRef, 
				tunableSetterServiceRef, cyNetworkManagerServiceRef, cyTableServiceRef, rootNetworkManagerServiceRef);
		
		Properties loadAttrsURLTaskFactoryProps = new Properties();
		loadAttrsURLTaskFactoryProps.setProperty(PREFERRED_MENU,"Tools.Cyni Tools.Add Table");
		loadAttrsURLTaskFactoryProps.setProperty(MENU_GRAVITY,"8.0f");
		loadAttrsURLTaskFactoryProps.setProperty(TITLE,"URL...");
		
		Properties loadAttrsFileTaskFactoryProps = new Properties();
		loadAttrsFileTaskFactoryProps.setProperty(PREFERRED_MENU,"Tools.Cyni Tools.Add Table");
		loadAttrsFileTaskFactoryProps.setProperty(MENU_GRAVITY,"7.0f");
		loadAttrsFileTaskFactoryProps.setProperty(TITLE,"File...");
		
		registerService(bc,loadAttrsFileTaskFactory,TaskFactory.class, loadAttrsFileTaskFactoryProps);
		registerService(bc,loadAttrsURLTaskFactory,TaskFactory.class, loadAttrsURLTaskFactoryProps);
		
		/******************************************************************************************************************************************/
		
		registerService(bc,cyInduction,CyCyniAlgorithmManager.class, new Properties());
		registerServiceListener(bc,cyInduction,"addCyniAlgorithm","removeCyniAlgorithm",CyCyniAlgorithm.class);
		
		registerService(bc,cyCyniMetrics,CyCyniMetricsManager.class, new Properties());
		registerServiceListener(bc,cyCyniMetrics,"addCyniMetric","removeCyniMetric",CyCyniMetric.class);
		
		registerService(bc,basicInduction,CyCyniAlgorithm.class, new Properties());
		registerService(bc,k2Induction,CyCyniAlgorithm.class, new Properties());
		registerService(bc,HCInduction,CyCyniAlgorithm.class, new Properties());
		registerService(bc,zero,CyCyniAlgorithm.class, new Properties());
		registerService(bc,bpca,CyCyniAlgorithm.class, new Properties());
		registerService(bc,ravg,CyCyniAlgorithm.class, new Properties());
		registerService(bc,equal,CyCyniAlgorithm.class, new Properties());
		
		registerService(bc,correlationMetric,CyCyniMetric.class, new Properties());
		registerService(bc,rankCorrelationMetric,CyCyniMetric.class, new Properties());
		registerService(bc,kendallCorrelationMetric,CyCyniMetric.class, new Properties());
		registerService(bc,bayesMetric,CyCyniMetric.class, new Properties());
		registerService(bc,entropyMetric,CyCyniMetric.class, new Properties());
		registerService(bc,mdlMetric,CyCyniMetric.class, new Properties());
		registerService(bc,aicMetric,CyCyniMetric.class, new Properties());
		registerService(bc,bdeMetric,CyCyniMetric.class, new Properties());
		
		CyniAction inductionAction = new CyniAction("Infer Network...",cyInduction,cyNetworkFactoryRef,cyNetworkViewFactoryServiceRef,cyTableServiceRef, cySwingApplicationServiceRef,
                cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef,
                cyNetworkManagerServiceRef,cyNetworkTableManagerServiceRef,rootNetworkManagerServiceRef,panelTaskManagerServiceRef,
                cyLayoutsServiceRef,cyCyniMetrics,visualMappingManagerServiceRef,cyPropertyServiceRef, dynamicTaskFactoryProvisionerServiceRef,CyniCategory.INDUCTION);
		
		CyniAction imputationAction = new CyniAction("Impute Missing Data...",cyInduction,cyNetworkFactoryRef,cyNetworkViewFactoryServiceRef,cyTableServiceRef, cySwingApplicationServiceRef,
				cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef,
                cyNetworkManagerServiceRef,cyNetworkTableManagerServiceRef,rootNetworkManagerServiceRef,panelTaskManagerServiceRef,
                cyLayoutsServiceRef,cyCyniMetrics,visualMappingManagerServiceRef,cyPropertyServiceRef, dynamicTaskFactoryProvisionerServiceRef,CyniCategory.IMPUTATION);
		
		CyniAction discretizationAction = new CyniAction("Discretize Data...",cyInduction,cyNetworkFactoryRef,cyNetworkViewFactoryServiceRef,cyTableServiceRef, cySwingApplicationServiceRef,
				cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef,
                cyNetworkManagerServiceRef,cyNetworkTableManagerServiceRef,rootNetworkManagerServiceRef,panelTaskManagerServiceRef,
                cyLayoutsServiceRef,cyCyniMetrics,visualMappingManagerServiceRef,cyPropertyServiceRef, dynamicTaskFactoryProvisionerServiceRef,CyniCategory.DISCRETIZATION);
		
		registerService(bc, inductionAction, CyAction.class, new Properties());
		registerService(bc,imputationAction,CyAction.class, new Properties());
		registerService(bc,discretizationAction,CyAction.class, new Properties());
		
		

	}
}

