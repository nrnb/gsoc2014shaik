package org.cytoscape.interference.internal;

/**
 * @author scardoni
 */
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

public class ThreadEngine extends Thread {

    private final Algorithm algorithm;
    private final CyNetwork network;
    private final CyNetworkView networkView;
    private final IntStartMenu menu;

    public ThreadEngine(Algorithm algorithm, CyNetwork network, CyNetworkView networkView, IntStartMenu menu) {
        this.network = network;
        this.networkView = networkView;
        this.algorithm = algorithm;
        this.menu = menu;
    }

    @Override
    public void run() {
        try {
            algorithm.execute(network, networkView, menu);
        } catch (AlgorithmInterruptedException e) {
        }
        menu.setEnabled(true);
    }

    public void end() {
        algorithm.end();
    }
}
