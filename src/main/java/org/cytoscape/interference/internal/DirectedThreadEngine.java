package org.cytoscape.interference.internal;

import org.cytoscape.model.CyNetwork;

/**
 * @author faizi.shaik
 */

public class DirectedThreadEngine extends Thread {
    private final CyNetwork network;
    private final boolean[] checkedCentralities;
    private final String[] directedCentralities;
    private final IntStartMenu menu;
    private final DirectedAlgorithm algorithm;
    private final Core core;
    
    public DirectedThreadEngine(CyNetwork network, boolean[] checkedCentralities, String[] directedCentralities, IntStartMenu menu, Core core){
        this.network=network;
        this.checkedCentralities=checkedCentralities;
        this.directedCentralities=directedCentralities;
        this.menu = menu;
        this.core = core;
        this.algorithm = new DirectedAlgorithm();
    }

    @Override
    public void run() {
        algorithm.executeCentralities(network, checkedCentralities, directedCentralities, menu, core);
    }
    
    public void stopAlgo(){
        algorithm.stopAlgo();
    }
}