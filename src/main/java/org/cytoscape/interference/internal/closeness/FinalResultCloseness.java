/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.closeness;

import org.cytoscape.model.CyNode;

public class FinalResultCloseness {
	private final CyNode node;
	private double closeness;

	public FinalResultCloseness(CyNode node, double clo) {
		this.node = node;
		this.closeness = clo;
	}

	public void update(double value) {
		closeness += value;
	}

	@Override
	public String toString(){
		return "node SUID = " + node.getSUID() + " closeness = " + closeness;
	}

	public double getCloseness() {
		return closeness;
	}

	public CyNode getNode() {
		return node;
	}
}