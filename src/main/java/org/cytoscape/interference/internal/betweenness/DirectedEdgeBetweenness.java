package org.cytoscape.interference.internal.betweenness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.interference.internal.MultiSPath;
import org.cytoscape.interference.internal.ShortestPathList;
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

public class DirectedEdgeBetweenness {
	private final CyNetwork network ;
    private final Map<CyEdge,Double> edgeBetweennessValues ;
    private final List<CyNode> nodesVisited ;
    
    public DirectedEdgeBetweenness(CyNetwork network){
        this.network = network;
        this.edgeBetweennessValues = new HashMap<CyEdge,Double>();
        this.nodesVisited = new ArrayList<CyNode>();

        for (CyEdge edge: network.getEdgeList())
            edgeBetweennessValues.put(edge, 0.0);
    }
    
    public void updateEdgeBetweenness(CyNode root, Iterable<ShortestPathList> shortestPaths){
        nodesVisited.add(root);
        
        for (ShortestPathList paths1: shortestPaths) {
        	CyNode sink = paths1.getLast().getNode();
            if (!nodesVisited.contains(sink)) {
	            int factor = 1;
	            for (ShortestPathList paths2: shortestPaths)
	                if (paths1 != paths2 && paths2.getLast().getNode().equals(sink))
	                    factor++;
	            
	            MultiSPath previousPath = null;
	            for (MultiSPath path: paths1) {
	            	if (previousPath != null) {
	            		List<CyEdge> edges = network.getConnectingEdgeList(previousPath.getNode(), path.getNode(), CyEdge.Type.ANY);
	            		CyEdge firstEdge = edges.get(0);
	                    edgeBetweennessValues.put(firstEdge, edgeBetweennessValues.get(firstEdge) + (1.0 / factor));
	            	}
	
	            	previousPath = path;
	            }
            }
       }
    }
    
    public Map<CyEdge,Double> getEdgeBetweennessMap() {
        return edgeBetweennessValues;
    }
}