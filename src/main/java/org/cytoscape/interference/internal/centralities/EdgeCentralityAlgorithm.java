package org.cytoscape.interference.internal.centralities;

import org.cytoscape.model.CyEdge;

public interface EdgeCentralityAlgorithm {
	public Iterable<CyEdge> getEdges();
	public double computeAt(CyEdge edge);
}