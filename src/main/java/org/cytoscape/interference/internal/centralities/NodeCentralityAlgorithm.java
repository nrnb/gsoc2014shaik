package org.cytoscape.interference.internal.centralities;

import org.cytoscape.model.CyNode;

public interface NodeCentralityAlgorithm {
	public Iterable<CyNode> getNodes();
	public double computeAt(CyNode node);
}