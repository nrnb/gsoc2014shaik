/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.betweenness;

public class BetweennessElement {
    private final long nodeSUID;
    private int SPCount;
    private double betweennessCount;

    public BetweennessElement(long nodeSUID) {
    	this.nodeSUID = nodeSUID;
    	this.SPCount = 1;
    }

    public void incrementSPcount() {
    	SPCount++;
    }

    public int getSPCount() {
    	return SPCount;
    }

    public double getBetweennessCount() {
    	return betweennessCount;
    }

    public void computeBetweenessCount(double totalSP) {
    	betweennessCount = SPCount / totalSP; 
    }

    public long getSUID() {
    	return nodeSUID;
    }

    @Override
    public String toString() {
    	return "nodoname = " + nodeSUID + " SPCount = " + SPCount + " betweennessCount = " + betweennessCount;
    }
}