package org.cytoscape.interference.internal;

/**
 * @author scardoni
 */

import java.util.List;
import java.util.ArrayList;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class MultiSPath {
    private final CyNode node;
    private final String nodeName;
    private int cost;
    private final List<MultiSPath> predecessors = new ArrayList<MultiSPath>();

    public MultiSPath(CyNode node, int cost, CyNetwork network) {
        this.node = node;
        this.nodeName = network.getRow(node).get("name", String.class);
        this.cost = cost;
    }
    
    public void setCost(int cost) {
    	this.cost = cost;
    }

    public void addPredecessor(MultiSPath newPredecessor){
    	predecessors.add(newPredecessor);
    }

    public MultiSPath getPredecessor(int i) {
    	return predecessors.get(i);
    }

    public void removeAllPredecessors() {
    	predecessors.clear();
    }

    public int getNumberOfPredecessors() {
    	return predecessors.size();
    }

    public Iterable<MultiSPath> getPredecessors() {
    	return predecessors;
    }

    public CyNode getNode() {
    	return node;  
    }

    public int getCost()  {
    	return cost;
    }

    public String getNodeName() {
    	return nodeName;
    }

    public long getSUID() {
    	return node.getSUID();
    }

    private String predecessorsAsString() {
    	if (predecessors.isEmpty())
    		return " no predecessors ";

    	String result = "";
    	for (MultiSPath path: predecessors)
   			result += " " + path.nodeName;

    	return result;
    }

    @Override
    public String toString() {
    	String result = "origine = " + nodeName + " costo = " + cost;

    	if (cost == 0)
    		result += " Root and Target are the same ";
    	else if (!predecessors.isEmpty())
    		result += "predecessori= " + predecessorsAsString();

    	return result;
    }
}