package org.cytoscape.interference.internal.betweenness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.interference.internal.MultiSPath;
import org.cytoscape.interference.internal.ShortestPathList;
import org.cytoscape.interference.internal.ShortestPaths;
import org.cytoscape.interference.internal.centralities.EdgeCentrality;
import org.cytoscape.interference.internal.centralities.EdgeCentralityAlgorithm;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/** 
 * The edge betweenness centrality is defined as the number of shortest 
 * paths that go through an edge in a graph or network. If there is more
 * than one shortest path between a pair of nodes, each path is assigned
 * equal weight such that the total weight of all of the paths is equal
 * to unity. (Girvan and Newman 2002)
 * 
 * @author faizaan.shaik
 */

public class EdgeBetweenness extends EdgeCentrality {
 
    public EdgeBetweenness(CyNetwork network, ShortestPaths shortestPaths) {
    	super("Edge Betweenness", false, new EdgeBetweennessAlgorithm(network, shortestPaths));
    }

    public static class EdgeBetweennessAlgorithm implements EdgeCentralityAlgorithm {
    	public final Map<CyEdge, Double> edgeBetweenness = new HashMap<CyEdge, Double>();
    	private final Set<CyNode> seen = new HashSet<CyNode>();
    	private final CyNetwork network;
    	private final ShortestPaths shortestPaths;

    	public EdgeBetweennessAlgorithm(CyNetwork network, ShortestPaths shortestPaths) {
    		this.network = network;
    		this.shortestPaths = shortestPaths;

    		Double zero = 0.0;
    		for (CyEdge edge: network.getEdgeList())
    			edgeBetweenness.put(edge, zero);

    		for (CyNode root: shortestPaths.getNodes())
            	computeEdgeBetweennessAt(root);
    	}

    	private void computeEdgeBetweennessAt(CyNode root){
		    seen.add(root);

		    Iterable<ShortestPathList> shortestPaths = this.shortestPaths.getFor(root);

		    for (ShortestPathList paths1: shortestPaths) {
		    	CyNode sink = paths1.getLast().getNode();
		        if (!seen.contains(sink)) {
		            int factor = 1;
		            for (ShortestPathList paths2: shortestPaths)
		                if (paths1 != paths2 && paths2.getLast().getNode().equals(sink))
		                    factor++;
		            
		            MultiSPath previousPath = null;
		            for (MultiSPath path: paths1) {
		            	if (previousPath != null) {
		            		List<CyEdge> edges = network.getConnectingEdgeList(previousPath.getNode(), path.getNode(), CyEdge.Type.ANY);
		            		CyEdge firstEdge = edges.get(0);
		            		edgeBetweenness.put(firstEdge, edgeBetweenness.get(firstEdge) + (1.0 / factor));
		            	}
		
		            	previousPath = path;
		            }
		        }
		   }
		}

		@Override
		public double computeAt(CyEdge edge) {
			return edgeBetweenness.get(edge);
		}

		@Override
		public Iterable<CyEdge> getEdges() {
			return network.getEdgeList();
		}
    }
}