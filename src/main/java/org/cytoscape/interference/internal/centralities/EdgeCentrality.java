package org.cytoscape.interference.internal.centralities;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

/**
 * @author scardoni
 */

public class EdgeCentrality extends Centrality {
	private final Map<CyEdge, Double> values = new HashMap<CyEdge, Double>();

	public EdgeCentrality(String name, double mean, double min, double max) {
		super(name, false);

		initMeanMinMax(mean, min, max);
	}

	protected EdgeCentrality(String name, boolean directed, EdgeCentralityAlgorithm algorithm) {
		super(name, directed);

		double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY, sum = 0;

		int edgesCount = 0;
		for (CyEdge root: algorithm.getEdges()) {
            double value = algorithm.computeAt(root);

            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
            edgesCount++;

            values.put(root, value);
        }

		initMeanMinMax(sum / edgesCount, min, max);
	}

	@Override
	public void showInPanelFor(CyNetwork network) {
		super.showInPanelFor(network);

		CyTable edgeTable = network.getDefaultEdgeTable();
	    String columnName = getName();

	    if (edgeTable.getColumn(columnName) != null)
	    	edgeTable.deleteColumn(columnName);

	    edgeTable.createColumn(columnName, Double.class, false);
	    for (CyEdge root: values.keySet()) {
	        CyRow row = edgeTable.getRow(root.getSUID());
	        row.set(columnName, values.get(root));
	    }
	}

	@Override
	public void removeFromPanel(CyNetwork network) {
		super.removeFromPanel(network);

		CyTable edgeTable = network.getDefaultEdgeTable();
	    String columnName = getName();

	    if (edgeTable.getColumn(columnName) != null)
	    	edgeTable.deleteColumn(columnName);
	}
}