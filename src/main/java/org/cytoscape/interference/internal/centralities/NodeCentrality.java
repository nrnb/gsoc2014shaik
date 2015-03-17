package org.cytoscape.interference.internal.centralities;

import java.util.HashMap;
import java.util.Map;
import org.cytoscape.interference.internal.Algorithm;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

/**
 * @author scardoni
 */
public class NodeCentrality extends Centrality {

    private final Map<CyNode, Double> values = new HashMap<CyNode, Double>();
    private static final Map<CyNode, CyNode> nodeMap = Algorithm.nodeMap;
    
    public NodeCentrality(String name, double mean, double min, double max) {
        super(name, false);

        initMeanMinMax(mean, min, max);
    }

    protected NodeCentrality(String name, boolean directed, NodeCentralityAlgorithm algorithm) {
        super(name, directed);

        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY, sum = 0.0;

        int nodesCount = 0;
        for (CyNode root : algorithm.getNodes()) {
            double value = algorithm.computeAt(root);

            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
            nodesCount++;
            
            values.put(nodeMap.get(root), value);
        }

        initMeanMinMax(sum / nodesCount, min, max);
    }

    @Override
    public void showInPanelFor(CyNetwork network) {
        super.showInPanelFor(network);

        CyTable nodeTable = network.getDefaultNodeTable();
        String columnName = getName();

        if (nodeTable.getColumn(columnName) != null) {
            nodeTable.deleteColumn(columnName);
        }

        nodeTable.createColumn(columnName, Double.class, false);
        for (CyNode root : values.keySet()) {
            CyRow row = nodeTable.getRow(root.getSUID());
            row.set(columnName, values.get(root));
        }
    }

    @Override
    public void removeFromPanel(CyNetwork network) {
        super.removeFromPanel(network);

        CyTable nodeTable = network.getDefaultNodeTable();
        String columnName = getName();

        if (nodeTable.getColumn(columnName) != null) {
            nodeTable.deleteColumn(columnName);
        }
    }
}