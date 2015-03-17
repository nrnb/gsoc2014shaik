package org.cytoscape.interference.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.interference.internal.visualizer.IntVisualizer;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.interference.internal.centralities.Centrality;

/**
 *
 * @author faizaan.shaik
 */
public class Core {

    private final CyApplicationManager cyApplicationManager;
    private final CySwingApplication cyDesktopService;
    private final CyServiceRegistrar cyServiceRegistrar;
    private final IntStartMenu menu;
    private final List<IntVisualizer> visualizers;

    public Core(CyActivator cyActivator) {
        this.cyApplicationManager = cyActivator.getApplicationManager();
        this.cyDesktopService = cyActivator.getDesktopService();
        this.cyServiceRegistrar = cyActivator.getServiceRegistrar();
        this.menu = createStartMenu(cyActivator);
        this.visualizers = new ArrayList<IntVisualizer>();
    }

    private IntStartMenu createStartMenu(CyActivator cyActivator) {
        IntStartMenu menu = new IntStartMenu(cyActivator, this);
        cyServiceRegistrar.registerService(menu, CytoPanelComponent.class, new Properties());
        CytoPanel panelWest = cyDesktopService.getCytoPanel(CytoPanelName.WEST);
        panelWest.setSelectedIndex(panelWest.indexOfComponent(menu));

        return menu;
    }

    public void close() {
        for (IntVisualizer visualizer : visualizers) {
            cyServiceRegistrar.unregisterService(visualizer, CytoPanelComponent.class);
        }

        cyServiceRegistrar.unregisterService(menu, CytoPanelComponent.class);
    }

    public void createVisualizer(List<Centrality> centralities) {
        IntVisualizer visualizer = new IntVisualizer(cyApplicationManager, this);
        cyServiceRegistrar.registerService(visualizer, CytoPanelComponent.class, new Properties());
        CytoPanel panelEast = cyDesktopService.getCytoPanel(CytoPanelName.EAST);
        panelEast.setState(CytoPanelState.DOCK);
        panelEast.setSelectedIndex(panelEast.indexOfComponent(visualizer));
        visualizers.add(visualizer);
        visualizer.setEnabled(centralities);
    }

    public CyApplicationManager getCyApplicationManager() {
        return cyApplicationManager;
    }

    public CySwingApplication getCyDesktopService() {
        return cyDesktopService;
    }

    public void closeCurrentResultPanel(IntVisualizer resultPanel) {
        cyServiceRegistrar.unregisterService(resultPanel, CytoPanelComponent.class);
    }
}
