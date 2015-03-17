package org.cytoscape.interference.internal.centroid;

import java.util.HashMap;
import java.util.Map;
import org.cytoscape.interference.internal.ShortestPathList;
import org.cytoscape.interference.internal.ShortestPaths;
import org.cytoscape.interference.internal.centralities.NodeCentrality;
import org.cytoscape.interference.internal.centralities.NodeCentralityAlgorithm;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class Centroid extends NodeCentrality {
 
    public Centroid(CyNetwork network, ShortestPaths shortestPaths) {
    	super("Centroid", false, new CentroidAlgorithm(network, shortestPaths));
    }

    public static class CentroidAlgorithm implements NodeCentralityAlgorithm {
    	private final Map<CyNode, Integer> centroid = new HashMap<CyNode, Integer>();
    	private final CyNetwork network;

    	public CentroidAlgorithm(CyNetwork network, ShortestPaths shortestPaths) {    		
    		this.network = network;

    		Iterable<CyNode> nodes = shortestPaths.getNodes();
    		int nodesCount = shortestPaths.getNodesCount();

    		// the distance from each node to all other nodes
    		Map<CyNode, int[]> distances = new HashMap<CyNode, int[]>();

    		for (CyNode root: nodes) {
    			int[] distancesForRoot = new int[nodesCount];
    			distances.put(root, distancesForRoot);

    			for (ShortestPathList path: shortestPaths.getReducedFor(root))
    				// guardo a che indice e' il nodo nella sequenza e aggiorno con la distanza
    				distancesForRoot[indexOf(nodes, path.getLast().getNode())] = path.getCost();
    		}

    		for (CyNode node1: nodes) {
    			int min = nodesCount + 1;
    			int[] distancesForNode1 = distances.get(node1);

    			for (CyNode node2: nodes){
    				if (!node1.equals(node2)) {
    					int[] distancesForNode2 = distances.get(node2);

    					int node3index = 0, numberOfCloseNodes = 0;
    					for (CyNode node3: nodes) {
    						if (!node1.equals(node3) && !node2.equals(node3)) {
    							int diff = distancesForNode1[node3index] - distancesForNode2[node3index];
    							if (diff < 0)
    								numberOfCloseNodes++;
    							else if (diff > 0)
    								numberOfCloseNodes--;
    						}

    						node3index++;
    					}

    					min = Math.min(min, numberOfCloseNodes);
    				}
                        }
    			centroid.put(node1, min);
    		}
    	}

		private int indexOf(Iterable<CyNode> nodes, CyNode node) {
			int index = 0;
			for (CyNode cursor: nodes)
				if (cursor.equals(node))
					return index;
				else
					index++;

			return -1;
		}

		@Override
		public double computeAt(CyNode node) {
			return centroid.get(node);
		}

		@Override
		public Iterable<CyNode> getNodes() {
			return network.getNodeList();
		}
    }
}