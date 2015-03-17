/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.eccentricity;

import java.util.List;

import org.cytoscape.interference.internal.ShortestPathList;
import org.cytoscape.model.CyNode;

public class FinalResultEccentricity {
    private final double eccentricity;
    private final CyNode node;
    
    public FinalResultEccentricity(CyNode node, List<ShortestPathList> shortestPaths) {
        this.node = node;
        this.eccentricity = DirectedEccentricity.execute(shortestPaths);
    }

    @Override
    public String toString(){
    	return "node suid = " + node.getSUID() + " eccentricity = " + eccentricity;
    }
    
    public double getEccentricity() {
        return eccentricity;
    }

    public CyNode getNode() {
    	return node;
    }
}