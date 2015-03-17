package org.cytoscape.interference.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.osgi.framework.BundleContext;

/**
 *
 * @author faizaan.shaik
 */
public class CyActivator extends AbstractCyActivator { 
    private static final String APP_NAME = "Interference";
    private static final String VERSION = "2.0";
    private static CyNetworkFactory networkFactory;
    private static CyNetworkManager networkManager;
    private static CyNetworkViewFactory networkViewFactory;
    private static CyNetworkViewManager networkViewManager;
    private CyApplicationManager applicationManager;
    private CySwingApplication desktopService;
    private CyServiceRegistrar serviceRegistrar;
    private IntMenuAction menuAction;

    @Override
    public void start(BundleContext context) throws Exception {
        this.applicationManager = getService(context, CyApplicationManager.class);
        this.desktopService = getService(context, CySwingApplication.class);
        networkFactory = getService(context, CyNetworkFactory.class);
        networkManager = getService(context, CyNetworkManager.class);
        networkViewFactory = getService(context,CyNetworkViewFactory.class);
        networkViewManager = getService(context, CyNetworkViewManager.class);
        this.serviceRegistrar = getService(context, CyServiceRegistrar.class);
        this.menuAction = new IntMenuAction(applicationManager, APP_NAME + VERSION, this);
        registerAllServices(context, menuAction, new Properties());
    }
    
    public CyServiceRegistrar getServiceRegistrar() {
        return serviceRegistrar;
    }

    public CyApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public CySwingApplication getDesktopService() {
    	return desktopService;
    }

    public IntMenuAction getMenuAction() {
        return menuAction;
    }
    
    public static CyNetworkFactory getNetworkFactory(){
        return networkFactory;
    }
    
    public static CyNetworkManager getNetworkManager(){
        return networkManager;
    }
    
    public static CyNetworkViewFactory getNetworkViewFactory(){
        return networkViewFactory;
    }
    
    public static CyNetworkViewManager getNetworkViewManager(){
        return networkViewManager;
    }
    
}