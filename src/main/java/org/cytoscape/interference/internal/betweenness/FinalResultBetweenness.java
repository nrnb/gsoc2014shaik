/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.betweenness;

import org.cytoscape.model.CyNode;

public class FinalResultBetweenness {
    private double betweenness;
    private final CyNode node;
    
    public FinalResultBetweenness(CyNode node, double betweenness) {
        this.node = node;
        this.betweenness = betweenness;
    }
    
    public void update(double value) {
        betweenness += value;
    }

    @Override
    public String toString() {
        return "node SUID = " + node.getSUID() + " betweenness = " + betweenness;
    }

    public long getSUID() {
        return node.getSUID();
    }
    
    public double getBetweenness() {
        return betweenness;
    }
    
    public CyNode getNode() {
        return node;
    }
}