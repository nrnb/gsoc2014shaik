package org.cytoscape.interference.internal.degree;

import org.cytoscape.interference.internal.centralities.NodeCentrality;
import org.cytoscape.interference.internal.centralities.NodeCentralityAlgorithm;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class Degree extends NodeCentrality {

	public Degree(final CyNetwork network) {
		super("Degree", false, new NodeCentralityAlgorithm() {

			@Override
			public double computeAt(CyNode node) {
				return network.getNeighborList(node, CyEdge.Type.ANY).size();
			}

			@Override
			public Iterable<CyNode> getNodes() {
				return network.getNodeList();
			}
		});
	}
}