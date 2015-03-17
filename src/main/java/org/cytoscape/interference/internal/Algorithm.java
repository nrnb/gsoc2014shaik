/**
 * @author faizaan.shaik
 */
package org.cytoscape.interference.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import org.cytoscape.interference.internal.betweenness.BetweennessMethods;
import org.cytoscape.interference.internal.betweenness.EdgeBetweenness;
import org.cytoscape.interference.internal.betweenness.FinalResultBetweenness;
import org.cytoscape.interference.internal.centralities.Centrality;
import org.cytoscape.interference.internal.centralities.NodeCentrality;
import org.cytoscape.interference.internal.centroid.Centroid;
import org.cytoscape.interference.internal.closeness.ClosenessMethods;
import org.cytoscape.interference.internal.closeness.FinalResultCloseness;
import org.cytoscape.interference.internal.degree.Degree;
import org.cytoscape.interference.internal.eccentricity.FinalResultEccentricity;
import org.cytoscape.interference.internal.eigenVector.CalculateEigenVector;
import org.cytoscape.interference.internal.radiality.Radiality;
import org.cytoscape.interference.internal.stress.FinalResultStress;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;

public class Algorithm implements Stoppable {

    private boolean stop = false;
    private double[][] adjacencyMatrixOfNetwork;
    private double[][] adjacencyMatrixOfHiddenNetwork;
    private boolean StressisOn = false;
    private boolean ClosenessisOn = false;
    private boolean EccentricityisOn = false;
    private boolean RadialityisOn = false;
    private boolean diameterIsSelected = false;
    private boolean betweennessIsOn = false;
    private boolean CentroidisOn = false;
    private boolean DegreeisOn = false;
    private boolean AverageisOn = false;
    private boolean EigenVectorisOn = false;
    private boolean BridgingisOn = false;
    private boolean EdgeBetweennessisOn = false;
    private boolean doNotDisplayBetweenness = false;
    private SortedMap<Long, Double> stressMap = new TreeMap<Long, Double>();
    private SortedMap<Long, Double> stressMap2 = new TreeMap<Long, Double>();
    private List<String> nodeAttributes = new ArrayList<String>();
    private List<String> networkAttributes = new ArrayList<String>();
    private final Vector centralities = new Vector();
    public static Map<CyNode, CyNode> nodeMap = new HashMap<CyNode, CyNode>();
    private CyNetwork network;
    private final Core core;
    private List<CyNode> nodeList;
    private CyTable nodeTable;
    private CyTable networkTable;
    private ShortestPaths shortestPaths;
    private ShortestPaths shortestPaths2;

    public Algorithm(Core core) {
        this.core = core;
    }

    public void execute(CyNetwork network, CyNetworkView view, IntStartMenu menu) throws AlgorithmInterruptedException {
        this.stop = false;
        this.network = network;
        this.nodeTable = network.getDefaultNodeTable();
        this.networkTable = network.getDefaultNetworkTable();
        this.nodeList = network.getNodeList();
        CyNetwork hiddenNetwork = CreateHiddenNetwork.create(network, menu);
        List<CyNode> hiddenNodeList = hiddenNetwork.getNodeList();
        int nodesCount = network.getNodeCount();

        removeCentralitiesFromPanel();

        List<FinalResultEccentricity> eccentricityResults = new ArrayList<FinalResultEccentricity>();
        List<FinalResultEccentricity> eccentricityResults2 = new ArrayList<FinalResultEccentricity>();
        List<FinalResultBetweenness> betweennessResults = new ArrayList<FinalResultBetweenness>();
        List<FinalResultBetweenness> betweennessResults2 = new ArrayList<FinalResultBetweenness>();
        centralities.clear();

        if (EigenVectorisOn) {
            adjacencyMatrixOfNetwork = new double[nodesCount][nodesCount];
            adjacencyMatrixOfHiddenNetwork = new double[hiddenNetwork.getNodeCount()][hiddenNetwork.getNodeCount()];
        }

        if (betweennessIsOn || StressisOn) {
            stressMap.clear();
            stressMap2.clear();
            for (CyNode root : nodeList) {
                checkForStop();

                if (StressisOn || betweennessIsOn) {
                    stressMap.put(root.getSUID(), 0.0);
                }
            }
            for (CyNode root : hiddenNodeList) {
                checkForStop();

                if (StressisOn || betweennessIsOn) {
                    stressMap2.put(root.getSUID(), 0.0);
                }
            }
        }
        // Create Node map from current network to hidden network
        CyTable hiddenNodeTable = hiddenNetwork.getDefaultNodeTable();
        for (CyNode root2 : hiddenNodeList) {
            CyNode root1 = null;
            String root2Name = hiddenNodeTable.getRow(root2.getSUID()).get(CyNetwork.NAME, String.class);
            for (CyNode cn : nodeList) {
                String root1Name = nodeTable.getRow(cn.getSUID()).get(CyNetwork.NAME, String.class);
                if (root2Name.equals(root1Name)) {
                    root1 = cn;
                    break;
                }
            }
            nodeMap.put(root2, root1);
        }
        // Create Shortest Paths for both the networks
        shortestPaths = new ShortestPaths(network, stressMap, StressisOn, false, menu, this);
        shortestPaths2 = new ShortestPaths(hiddenNetwork, stressMap2, StressisOn, false, menu, this);
        unselectAllNodes();
        // Computation Over, Now spread the values
        menu.endOfComputation(nodesCount);

        if (diameterIsSelected) {
            networkTable.createColumn("Diameter unDir", Double.class, false);
            network.getRow(network).set("Diameter unDir", (double) shortestPaths.getDiameter());

            networkAttributes.add("Diameter unDir");
        }

        if (AverageisOn) {
            int distance = 0;
            for (CyNode root : nodeList) {
                for (ShortestPathList currentlist : shortestPaths.getReducedFor(root)) {
                    distance += currentlist.getCost();
                }
            }

            double average = ((double) distance) / (nodesCount * (nodesCount - 1));
            networkTable.createColumn("Average Distance unDir", Double.class, false);
            network.getRow(network).set("Average Distance unDir", average);

            networkAttributes.add("Average Distance unDir");
        }

        if (EccentricityisOn) {
            for (CyNode root : nodeList) {
                eccentricityResults.add(new FinalResultEccentricity(root, shortestPaths.getReducedFor(root)));
            }
            for (CyNode root2 : hiddenNodeList) {
                eccentricityResults2.add(new FinalResultEccentricity(root2, shortestPaths2.getReducedFor(root2)));
            }
            Map<CyNode, Double> eccentricityValues = new HashMap<CyNode, Double>();
            double sum1 = 0.0, sum2 = 0.0;
            for (Iterator i = eccentricityResults.iterator(); i.hasNext();) {
                FinalResultEccentricity currentnodeeccentricity = (FinalResultEccentricity) i.next();
                sum1 += currentnodeeccentricity.getEccentricity();
            }
            for (Iterator j = eccentricityResults2.iterator(); j.hasNext();) {
                FinalResultEccentricity currentnodeeccentricity2 = (FinalResultEccentricity) j.next();
                sum2 += currentnodeeccentricity2.getEccentricity();
            }
            for (Iterator i = eccentricityResults2.iterator(); i.hasNext();) {
                FinalResultEccentricity currentnodeeccentricity2 = (FinalResultEccentricity) i.next();
                CyNode node2 = currentnodeeccentricity2.getNode();
                CyNode node1 = nodeMap.get(node2);
                double int1 = 0.0;
                for (Iterator j = eccentricityResults.iterator(); j.hasNext();) {
                    FinalResultEccentricity currentnodeeccentricity1 = (FinalResultEccentricity) j.next();
                    if (currentnodeeccentricity1.getNode() == node1) {
                        int1 = currentnodeeccentricity1.getEccentricity();
                        break;
                    }
                }
                eccentricityValues.put(node1, DirectedAlgorithm.calculateInterference(int1, sum1, currentnodeeccentricity2.getEccentricity(), sum2));
            }
            putValuesinTable(network, "Eccentricity unDir", eccentricityValues, centralities);
        }

        if (ClosenessisOn) {
            Map<CyNode, Double> closenessResults = new HashMap<CyNode, Double>();
            Map<CyNode, Double> closenessResults2 = new HashMap<CyNode, Double>();
            List<FinalResultCloseness> closenessResults3 = new ArrayList<FinalResultCloseness>();
            double sum1 = 0.0, sum2 = 0.0;
            for (CyNode root : nodeList) {
                double value = ClosenessMethods.CalculateCloseness(shortestPaths.getReducedFor(root), root).getCloseness();
                closenessResults.put(root, value);
                sum1 += value;
            }
            for (CyNode root : hiddenNodeList) {
                double value = ClosenessMethods.CalculateCloseness(shortestPaths2.getReducedFor(root), root).getCloseness();
                closenessResults2.put(root, value);
                sum2 += value;
            }
            for (CyNode root2 : hiddenNodeList) {
                CyNode root1 = nodeMap.get(root2);
                closenessResults3.add(new FinalResultCloseness(root1, DirectedAlgorithm.calculateInterference(closenessResults.get(root1), sum1, closenessResults2.get(root2), sum2)));
            }

            nodeTable.createColumn("Closeness unDir", Double.class, false);
            nodeAttributes.add("Closeness unDir");
            double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, totalsum = 0;
            for (FinalResultCloseness result : closenessResults3) {
                double currentcloseness = result.getCloseness();
                if (currentcloseness < min) {
                    min = currentcloseness;
                }

                if (currentcloseness > max) {
                    max = currentcloseness;
                }

                totalsum = totalsum + currentcloseness;

                CyRow row = nodeTable.getRow(result.getNode().getSUID());
                row.set("Closeness unDir", new Double(currentcloseness));
            }
            networkTable.createColumn("Closeness Max value unDir", Double.class, false);
            networkTable.createColumn("Closeness min value unDir", Double.class, false);
            double mean = totalsum / nodesCount;
            networkTable.createColumn("Closeness mean value unDir", Double.class, false);
            network.getRow(network).set("Closeness Max value unDir", new Double(max));
            network.getRow(network).set("Closeness min value unDir", new Double(min));
            network.getRow(network).set("Closeness mean value unDir", new Double(mean));
            networkAttributes.add("Closeness Max value");
            networkAttributes.add("Closeness min value");
            networkAttributes.add("Closeness mean value");
            centralities.add(new NodeCentrality("Closeness unDir", mean, min, max));
        }

        if (RadialityisOn) {
            HashMap<CyNode, Double> radialityValues = Radiality.calculateRadiality(shortestPaths);
            HashMap<CyNode, Double> radialityValues2 = Radiality.calculateRadiality(shortestPaths2);
            Radiality radiality = new Radiality(shortestPaths2, nodeMap, radialityValues, radialityValues2);
            radiality.showInPanelFor(network);
            centralities.add(radiality);
        }

        if (betweennessIsOn) {
            for (CyNode root : nodeList) {
                betweennessResults.add(new FinalResultBetweenness(root, 0));
            }
            for (CyNode root : hiddenNodeList) {
                betweennessResults2.add(new FinalResultBetweenness(root, 0));
            }
            for (CyNode root : nodeList) {
                BetweennessMethods.updateBetweenness(shortestPaths.getFor(root), betweennessResults);
            }
            for (CyNode root : hiddenNodeList) {
                BetweennessMethods.updateBetweenness(shortestPaths2.getFor(root), betweennessResults2);
            }

            if (!doNotDisplayBetweenness) {
                Map<CyNode, Double> betweennessValues = new HashMap<CyNode, Double>();
                double sum1 = 0.0, sum2 = 0.0;
                for (Iterator i = betweennessResults2.iterator(); i.hasNext();) {
                    FinalResultBetweenness currentnodebetweenness = (FinalResultBetweenness) i.next();
                    sum2 += currentnodebetweenness.getBetweenness();
                }
                for (Iterator j = betweennessResults.iterator(); j.hasNext();) {
                    FinalResultBetweenness currentnodebetweenness1 = (FinalResultBetweenness) j.next();
                    sum1 += currentnodebetweenness1.getBetweenness();
                }
                for (Iterator i = betweennessResults2.iterator(); i.hasNext();) {
                    FinalResultBetweenness currentnodebetweenness = (FinalResultBetweenness) i.next();
                    CyNode node2 = currentnodebetweenness.getNode();
                    CyNode node1 = nodeMap.get(node2);
                    double int1 = 0.0;
                    for (Iterator j = betweennessResults.iterator(); j.hasNext();) {
                        FinalResultBetweenness currentnodebetweenness1 = (FinalResultBetweenness) j.next();
                        if (currentnodebetweenness1.getNode() == node1) {
                            int1 = currentnodebetweenness1.getBetweenness();
                            break;
                        }
                    }
                    betweennessValues.put(node1, DirectedAlgorithm.calculateInterference(int1, sum1, currentnodebetweenness.getBetweenness(), sum2));
                }
                putValuesinTable(network, "Betweenness unDir", betweennessValues, centralities);
            }
        }

        if (DegreeisOn) {
            Degree degree = new Degree(network);
            degree.showInPanelFor(network);
            centralities.add(degree);
        }

        if (StressisOn) {
            nodeTable.createColumn("Stress unDir", Double.class, false);
            nodeAttributes.add("Stress unDir");

            double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, totalsum = 0;
            List<FinalResultStress> stressResults = new ArrayList<FinalResultStress>();
            for (long nodeSUID : stressMap.keySet()) {
                CyNode node = network.getNode(nodeSUID);
                double stress = stressMap.get(nodeSUID);
                stressResults.add(new FinalResultStress(node, stress));

                min = Math.min(min, stress);
                max = Math.max(max, stress);
                totalsum += stress;

                CyRow row = nodeTable.getRow(nodeSUID);
                row.set("Stress unDir", stress);
            }
            networkTable.createColumn("Stress Max value unDir", Double.class, false);
            networkTable.createColumn("Stress min value unDir", Double.class, false);
            double mean = totalsum / nodesCount;
            networkTable.createColumn("Stress mean value unDir", Double.class, false);
            network.getRow(network).set("Stress Max value unDir", new Double(max));
            network.getRow(network).set("Stress min value unDir", new Double(min));
            network.getRow(network).set("Stress mean value unDir", new Double(mean));
            networkAttributes.add("Stress Max value");
            networkAttributes.add("Stress min value");
            networkAttributes.add("Stress mean value");
            NodeCentrality stressCentrality = new NodeCentrality("Stress unDir", mean, min, max);
            centralities.add(stressCentrality);
        }
        if (CentroidisOn) {
            Centroid.CentroidAlgorithm centroid1 = new Centroid.CentroidAlgorithm(network, shortestPaths);
            Centroid.CentroidAlgorithm centroid2 = new Centroid.CentroidAlgorithm(hiddenNetwork, shortestPaths2);
            Map<CyNode, Double> centroidValues = new HashMap< CyNode, Double>();
            double sum1 = 0.0, sum2 = 0.0;
            for (CyNode root2 : centroid2.getNodes()) {
                sum2 += centroid2.computeAt(root2);
            }
            for (CyNode root1 : centroid1.getNodes()) {
                sum1 += centroid1.computeAt(root1);
            }
            for (CyNode root2 : centroid2.getNodes()) {
                CyNode root1 = nodeMap.get(root2);
                double int1 = centroid1.computeAt(root1);
                double int2 = centroid2.computeAt(root2);
                centroidValues.put(root1, DirectedAlgorithm.calculateInterference(int1, sum1, int2, sum2));
            }
            putValuesinTable(network, "Centroid unDir", centroidValues, centralities);
        }
        if (EigenVectorisOn) {
            int k = 0;
            for (CyNode root : nodeList) {
                List<CyNode> neighbors = network.getNeighborList(root, CyEdge.Type.ANY);
                for (CyNode neighbor : neighbors) {
                    adjacencyMatrixOfNetwork[k][nodeList.indexOf(neighbor)] = 1.0;
                }
                k++;
            }
            k = 0;
            for (CyNode root2 : hiddenNodeList) {
                List<CyNode> neighbors = hiddenNetwork.getNeighborList(root2, CyEdge.Type.ANY);
                for (CyNode neighbor : neighbors) {
                    adjacencyMatrixOfHiddenNetwork[k][hiddenNodeList.indexOf(neighbor)] = 1.0;
                }
                k++;
            }
            nodeAttributes.add("EigenVector unDir");
            networkAttributes.add("EigenVector Max value unDir");
            networkAttributes.add("EigenVector min value unDir");
            networkAttributes.add("EigenVector mean value unDir");
            Map<CyNode, Double> eigenVectorValues1 = CalculateEigenVector.execute(adjacencyMatrixOfNetwork, network);
            Map<CyNode, Double> eigenVectorValues2 = CalculateEigenVector.execute(adjacencyMatrixOfHiddenNetwork, hiddenNetwork);
            Map<CyNode, Double> eigenVectorValues3 = DirectedAlgorithm.calculateInterferenceFromMap(eigenVectorValues1, eigenVectorValues2, nodeMap);
            putValuesinTable(network, "EigenVector unDir", eigenVectorValues3, centralities);
        }
        if (BridgingisOn) {
            Map<CyNode, Double> bridgingValues1 = calculateBridging(network, betweennessResults);
            Map<CyNode, Double> bridgingValues2 = calculateBridging(hiddenNetwork, betweennessResults2);
            Map<CyNode, Double> bridgingValues3 = DirectedAlgorithm.calculateInterferenceFromMap(bridgingValues1, bridgingValues2, nodeMap);
            putValuesinTable(network, "Bridging unDir", bridgingValues3, centralities);
        }
        if (EdgeBetweennessisOn) {
            EdgeBetweenness.EdgeBetweennessAlgorithm edgeBetweenness = new EdgeBetweenness.EdgeBetweennessAlgorithm(network, shortestPaths);
            EdgeBetweenness.EdgeBetweennessAlgorithm edgeBetweenness2 = new EdgeBetweenness.EdgeBetweennessAlgorithm(hiddenNetwork, shortestPaths2);
            Map<CyEdge, Double> values = DirectedAlgorithm.calculateInterferenceFromMap(edgeBetweenness.edgeBetweenness, edgeBetweenness2.edgeBetweenness, CreateHiddenNetwork.edgeMap);
            DirectedAlgorithm.putValuesinEdgeTable(network, "Edge Betweenness unDir", values, centralities);
        }
        core.createVisualizer(centralities);
    }

    public void end() {
        stop = true;
    }

    public void setChecked(boolean[] ison) {
        diameterIsSelected = ison[0];
        AverageisOn = ison[1];
        DegreeisOn = ison[2];
        EccentricityisOn = ison[3];
        RadialityisOn = ison[4];
        ClosenessisOn = ison[5];
        StressisOn = ison[6];
        betweennessIsOn = ison[7];
        CentroidisOn = ison[8];
        EigenVectorisOn = ison[9];
        BridgingisOn = ison[10];
        EdgeBetweennessisOn = ison[11];
        if (BridgingisOn && !betweennessIsOn) {
            betweennessIsOn = true;
            doNotDisplayBetweenness = true;
        }
        if (ison[7]) {
            doNotDisplayBetweenness = false;
        }
    }

    private void checkForStop() throws AlgorithmInterruptedException {
        if (stop) {
            unselectAllNodes();
            throw new AlgorithmInterruptedException();
        }
    }

    private void unselectAllNodes() {
        //TODO lo chiamerei select all nodes!
        for (CyNode node : nodeList) {
            network.getRow(node).set("selected", true);
        }
    }

    private void removeCentralitiesFromPanel() {
        for (Object centrality : centralities) {
            Centrality centrality1 = (Centrality) centrality;
            centrality1.removeFromPanel(network);
        }

        for (String attribute : nodeAttributes) {
            if (nodeTable.getColumn(attribute) != null) {
                nodeTable.deleteColumn(attribute);
            }
        }

        for (String attribute : networkAttributes) {
            if (networkTable.getColumn(attribute) != null) {
                networkTable.deleteColumn(attribute);
            }
        }
    }

    public static Map<CyNode, Double> calculateBridging(CyNetwork network, List<FinalResultBetweenness> betweennessResults) {
        Map<CyNode, Double> bridgingValues = new HashMap<CyNode, Double>();
        for (FinalResultBetweenness result : betweennessResults) {
            double currentbetweenness = result.getBetweenness();
            CyNode root = result.getNode();
            List<CyNode> bridgingNeighborList = network.getNeighborList(root, CyEdge.Type.ANY);
            double bridgingCoefficient = 0;
            if (!bridgingNeighborList.isEmpty()) {
                double BCNumerator = 1.0 / bridgingNeighborList.size();
                double BCDenominator = 0.0;
                for (CyNode bridgingNeighbor : bridgingNeighborList) {
                    BCDenominator = BCDenominator + 1 / (double) (network.getNeighborList(bridgingNeighbor, CyEdge.Type.ANY).size());
                }

                bridgingCoefficient = BCNumerator / BCDenominator;
            }
            double bridgingCentrality = bridgingCoefficient * currentbetweenness;
            bridgingValues.put(root, bridgingCentrality);
        }
        return bridgingValues;
    }

    public static void putValuesinTable(CyNetwork network, String heading, Map<CyNode, Double> values, Vector centralities) {
        CyTable nodeTable = network.getDefaultNodeTable();
        nodeTable.createColumn(heading, Double.class, false);

        Set<CyNode> nodes = values.keySet();
        String networkattheading = heading;
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, totalsum = 0, currentvalue;
        Iterator it = nodes.iterator();

        while (it.hasNext()) {
            CyNode root = (CyNode) it.next();
            currentvalue = values.get(root);
            if (currentvalue < min) {
                min = currentvalue;
            }

            if (currentvalue > max) {
                max = currentvalue;
            }

            totalsum = totalsum + currentvalue;
            CyRow row = nodeTable.getRow(root.getSUID());
            row.set(heading, new Double(currentvalue));
        }

        if (!heading.equals("InDegree") && !heading.equals("OutDegree")) {
            networkattheading = heading.split(" ")[0];
        }

        CyTable networkTable = network.getDefaultNetworkTable();
        int totalnodecount = network.getNodeCount();
        networkTable.createColumn(networkattheading + " Max value unDir", Double.class, false);
        networkTable.createColumn(networkattheading + " min value unDir", Double.class, false);
        double mean = totalsum / totalnodecount;
        networkTable.createColumn(networkattheading + " mean value unDir", Double.class, false);
        network.getRow(network).set(networkattheading + " Max value unDir", new Double(max));
        network.getRow(network).set(networkattheading + " min value unDir", new Double(min));
        network.getRow(network).set(networkattheading + " mean value unDir", new Double(mean));

        centralities.add(new NodeCentrality(heading, mean, min, max));
    }

    @Override
    public boolean isStopped() {
        return stop;
    }
}
