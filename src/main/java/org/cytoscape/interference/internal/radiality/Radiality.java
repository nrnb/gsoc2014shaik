package org.cytoscape.interference.internal.radiality;

import java.util.HashMap;
import java.util.Map;
import org.cytoscape.interference.internal.DirectedAlgorithm;
import org.cytoscape.interference.internal.ShortestPathList;
import org.cytoscape.interference.internal.ShortestPaths;
import org.cytoscape.interference.internal.centralities.NodeCentrality;
import org.cytoscape.interference.internal.centralities.NodeCentralityAlgorithm;
import org.cytoscape.model.CyNode;

public class Radiality extends NodeCentrality {

    public Radiality(final ShortestPaths shortestPaths, final Map<CyNode, CyNode> nodeMap, final HashMap<CyNode, Double> radialityValues1, final HashMap<CyNode, Double> radialityValues2) {
        super("Radiality", false, new NodeCentralityAlgorithm() {
            @Override
            public double computeAt(CyNode node) {
                CyNode node1 = nodeMap.get(node);
                double sum1 = 0.0, sum2 = 0.0;
                for (Double value : radialityValues1.values()) {
                    sum1 += value;
                }
                for (Double value : radialityValues2.values()) {
                    sum2 += value;
                }
                return DirectedAlgorithm.calculateInterference(radialityValues1.get(node1), sum1, radialityValues2.get(node), sum2);
            }

            @Override
            public Iterable<CyNode> getNodes() {
                return shortestPaths.getNodes();
            }
        });
    }

    public static HashMap<CyNode, Double> calculateRadiality(final ShortestPaths shortestPaths) {
        HashMap<CyNode, Double> radialityValues = new HashMap<CyNode, Double>();
        Iterable<CyNode> nodeList = shortestPaths.getNodes();
        for (CyNode root : nodeList) {
            radialityValues.put(root, computeRadialityAtNode(root, shortestPaths));
        }
        return radialityValues;
    }

    public static double computeRadialityAtNode(CyNode node, final ShortestPaths shortestPaths) {
        int radiality = 0;
        int diameter = shortestPaths.getDiameter();

        for (ShortestPathList path : shortestPaths.getReducedFor(node)) {
            int distance = path.getCost();
            if (distance != 0) {
                radiality += diameter + 1 - distance;
            }
        }

        // TODO: and what if nodesCount == 1?
        return radiality / (shortestPaths.getNodesCount() - 1.0);
    }
}