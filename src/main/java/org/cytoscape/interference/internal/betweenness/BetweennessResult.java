/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.betweenness;

import java.util.ArrayList;
import java.util.List;

public class BetweennessResult {
    private final String source;
    private final String target;
    private final List<BetweennessElement> elements = new ArrayList<BetweennessElement>();
    private int SPcount;
    
    public BetweennessResult(String source, String target) {
    	this.source = source;
    	this.target = target;
    	this.SPcount = 1;
    }

    public boolean exist(String source, String target) {
    	return source.equals(this.source) && target.equals(this.target);
    }

    public void incrementSPcount() {
    	SPcount++;
    }

    public List<BetweennessElement> getElements() {
    	return elements;
    }

    public void update(long nodeSUID) {
    	boolean found = false;
    	for (BetweennessElement element: elements)
    		if (nodeSUID == element.getSUID()) {
    			element.incrementSPcount();
    			found = true;
    		}

    	if (!found)
    		elements.add(new BetweennessElement(nodeSUID));
    }

    public void computeBetweennessCount() {
    	for (BetweennessElement element: elements)
    		element.computeBetweenessCount(SPcount);
    }

    @Override
    public String toString() {
    	String result = "source = " + source + " target = " + target + " SP number = " + SPcount ;

    	for (BetweennessElement element: elements)
    		result += " " + element.getSUID() + 
   				" spcount = " + element.getSPCount() + " btwcount = " + element.getBetweennessCount();

    	return result;
    }
}