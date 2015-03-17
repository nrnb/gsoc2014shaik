/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.stress;

import org.cytoscape.model.CyNode;

public class FinalResultStress {
    private final long nodeSUID;
    private final double stress;
    
    public FinalResultStress(CyNode node, double Stress) {
        this.nodeSUID = node.getSUID();
        this.stress = Stress;
    }
    
    @Override
    public String toString() {
    	return "node suid = " + nodeSUID + " stress = " + stress;
    }
}