/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.centroid;

import org.cytoscape.model.CyNode;

public class FinalResultCentroid {
    private final int[] distances;
    private int centroid;
    private final CyNode node;
    
    public FinalResultCentroid(CyNode node, int nodesCount) {
        this.node = node;
        this.centroid = 0;
        this.distances = new int[nodesCount];
        for(int i=0 ; i<nodesCount ; i++)
            distances[i] = Integer.MAX_VALUE;
    }
    
    public void updatevector(int index, int distance) {
        this.distances[index] = distance;
    }
    
    public int getDistanceAt(int index) {
        return distances[index];
    }
    
    public void update(int value) {
        centroid = value;
    }

    @Override
    public String toString(){
        return "node SUID = " + node.getSUID() + " centroid = " + centroid;
    }

    public long getSUID() {
        return node.getSUID();
    }
    
    public int getCentroid() {
        return centroid;
    }
    
    public CyNode getNode() {
    	return node;
    }
}