package org.cytoscape.interference.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JPanel;

import org.cytoscape.interference.internal.betweenness.BetweennessMethods;
import org.cytoscape.interference.internal.betweenness.DirectedEdgeBetweenness;
import org.cytoscape.interference.internal.betweenness.FinalResultBetweenness;
import org.cytoscape.interference.internal.centralities.NodeCentrality;
import org.cytoscape.interference.internal.centroid.CentroidMethods;
import org.cytoscape.interference.internal.centroid.FinalResultCentroid;
import org.cytoscape.interference.internal.closeness.DirectedCloseness;
import org.cytoscape.interference.internal.eccentricity.DirectedEccentricity;
import org.cytoscape.interference.internal.eigenVector.CalculateEigenVector;
import org.cytoscape.interference.internal.radiality.DirectedRadiality;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

/**
 * @author faizaan.shaik
 */
public class DirectedAlgorithm {

    public CyNetwork network;
    public boolean[] checkedCentralities;
    public String[] directedCentralities;
    public JPanel menu;
    public static boolean stop;
    public boolean openResultsPanel;
    public boolean displayBetweennessResults = false;
    private SortedMap<Long, Double> stressMap = new TreeMap<Long, Double>();
    private SortedMap<Long, Double> stressMapInHiddenNetwork = new TreeMap<Long, Double>();
    public double Diameter = 0.0;
    public double Diameter2 = 0.0;
    public double totalDist;
    //dataStructures
    public Map<CyNode, CyNode> nodeMap = new HashMap<CyNode, CyNode>();
    public Map<CyNode, List<ShortestPathList>> singleShortestPaths = new HashMap<CyNode, List<ShortestPathList>>();
    public Map<CyNode, List<ShortestPathList>> singleShortestPathsInHiddenNetwork = new HashMap<CyNode, List<ShortestPathList>>();
    ;
    public Map<CyNode, Double> directedOutDegreeValues;
    public Map<CyNode, Double> directedInDegreeValues;
    private Map<CyNode, Double> directedClosenessValues2;
    private Map<CyNode, Double> directedClosenessValues3;
    public Map<CyNode, Double> directedClosenessValues;
    public Map<CyNode, Double> directedEccentrityValues;
    public Map<CyNode, Double> directedEccentrityValues2;
    public Map<CyNode, Double> directedRadialityValues;
    public Map<CyNode, Double> directedRadialityValues2;
    public Map<CyNode, Double> directedRadialityValues3;
    public Map<CyNode, Double> directedStressValues;
    public Map<CyNode, Double> directedBridgingValues;
    public DirectedEdgeBetweenness edgeBetweenness;
    public DirectedEdgeBetweenness edgeBetweenness2;
    private static Vector BetweennessVectorResults;
    private static Vector BetweennessVectorResults2;
    private static Vector CentroidVectorResults;
    private static Vector CentroidVectorResults2;
    private static Vector CentroidVectorofNodes;
    private static Vector CentroidVectorofNodes2;
    public double[][] adjacencyMatrixOfNetwork;
    public double[][] adjacencyMatrixOfHiddenNetwork;
    public Core interferencecore;
    public Vector centralities = new Vector();

    public static void stopAlgo() {
        stop = true;
    }

    public void executeCentralities(CyNetwork n, boolean[] c, String[] d, IntStartMenu menu, Core core) {
        stop = false;
        openResultsPanel = false;
        network = n;
        checkedCentralities = c;
        directedCentralities = d;
        this.interferencecore = core;

        CyNetwork hiddenNetwork = CreateHiddenNetwork.create(n, menu);
        if (hiddenNetwork == null) {
            return;
        }

        int totalnodecount = network.getNodeCount();
        int totalEdgeCount = network.getEdgeCount();
        int nodeworked = 0;
        CyTable nodeTable = network.getDefaultNodeTable();
        CyTable networkTable = network.getDefaultNetworkTable();
        List<CyNode> hiddenNodeList = hiddenNetwork.getNodeList();
        List<CyNode> nodeList = network.getNodeList();
        List<ShortestPathList> shortestPaths = null;
        List<ShortestPathList> shortestPathsInHiddenNetwork = null;
        displayBetweennessResults = checkedCentralities[7];

        if (checkedCentralities[10] && !checkedCentralities[7]) {
            checkedCentralities[7] = true;
        }

        //instantiate data stuctures
        //average distance checkbox
        if (checkedCentralities[1]) {
            totalDist = 0.0;
        }

        if (checkedCentralities[2]) {
            //Degree checkbox is selected
            directedOutDegreeValues = new HashMap<CyNode, Double>(totalnodecount);
            directedInDegreeValues = new HashMap<CyNode, Double>(totalnodecount);
        }
        if (checkedCentralities[3]) {
            directedEccentrityValues = new HashMap<CyNode, Double>(totalnodecount);
            directedEccentrityValues2 = new HashMap<CyNode, Double>();
        }

        if (checkedCentralities[4]) {
            directedRadialityValues = new HashMap<CyNode, Double>(totalnodecount);
            directedRadialityValues2 = new HashMap<CyNode, Double>(totalnodecount);
            directedRadialityValues3 = new HashMap<CyNode, Double>(totalnodecount);
        }
        if (checkedCentralities[5]) {
            directedClosenessValues2 = new HashMap<CyNode, Double>(totalnodecount);
            directedClosenessValues3 = new HashMap<CyNode, Double>(totalnodecount);
            directedClosenessValues = new HashMap<CyNode, Double>(totalnodecount);
        }
        if (checkedCentralities[6]) {
            directedStressValues = new HashMap<CyNode, Double>(totalnodecount);
        }

        if (checkedCentralities[7]) {
            BetweennessVectorResults = new Vector();
            BetweennessVectorResults2 = new Vector();
            for (Iterator i = nodeList.iterator(); i.hasNext();) {
                if (stop) {
                    return;
                }
                CyNode root = (CyNode) i.next();
                BetweennessVectorResults.add(new FinalResultBetweenness(root, 0));
            }
            for (Iterator i = hiddenNodeList.iterator(); i.hasNext();) {
                if (stop) {
                    return;
                }
                CyNode root = (CyNode) i.next();
                BetweennessVectorResults2.add(new FinalResultBetweenness(root, 0));
            }
        }
        if (checkedCentralities[8]) {
            CentroidVectorResults = new Vector();
            CentroidVectorResults2 = new Vector();
            CentroidVectorofNodes = new Vector();
            CentroidVectorofNodes2 = new Vector();
            for (Iterator i = nodeList.iterator(); i.hasNext();) {
                if (stop) {
                    return;
                }
                CyNode root = (CyNode) i.next();
                CentroidVectorofNodes.addElement(root.getSUID());
            }
            for (Iterator i = hiddenNodeList.iterator(); i.hasNext();) {
                if (stop) {
                    return;
                }
                CyNode root2 = (CyNode) i.next();
                CentroidVectorofNodes2.addElement(root2.getSUID());
            }
        }
        if (checkedCentralities[9]) {
            adjacencyMatrixOfNetwork = new double[totalnodecount][totalnodecount];
            adjacencyMatrixOfHiddenNetwork = new double[hiddenNetwork.getNodeCount()][hiddenNetwork.getNodeCount()];
        }

        if (checkedCentralities[10]) {
            directedBridgingValues = new HashMap<CyNode, Double>(totalnodecount);
        }

        if (checkedCentralities[11]) {
            edgeBetweenness = new DirectedEdgeBetweenness(network);
            edgeBetweenness2 = new DirectedEdgeBetweenness(hiddenNetwork);
        }

        boolean StressisOn = true;
        stressMap.clear();
        for (CyNode root : nodeList) {
            stressMap.put(root.getSUID(), new Double(0));
        }
        stressMapInHiddenNetwork.clear();
        for (CyNode root2 : hiddenNodeList) {
            stressMapInHiddenNetwork.put(root2.getSUID(), new Double(0));
        }
        // Create Node map from current network to hidden network
        CyTable hiddenNodeTable = hiddenNetwork.getDefaultNodeTable();
        String root2Name, root1Name;
        for (CyNode root2 : hiddenNodeList) {
            if (stop) {
                return;
            }
            CyNode root1 = null;
            root2Name = hiddenNodeTable.getRow(root2.getSUID()).get(CyNetwork.NAME, String.class);
            for (CyNode cn : nodeList) {
                root1Name = nodeTable.getRow(cn.getSUID()).get(CyNetwork.NAME, String.class);
                if (root2Name.equals(root1Name)) {
                    root1 = cn;
                    break;
                }
            }
            nodeMap.put(root2, root1);
        }
        // Create shortest paths for hidden network
        for (CyNode root2 : hiddenNodeList) {
            if (stop) {
                return;
            }
            shortestPathsInHiddenNetwork = new ShortestPathsPerNode(hiddenNetwork, root2, stressMapInHiddenNetwork, StressisOn, true, menu.isWeighted).getPaths();
            // create a single shortest path list
            List<ShortestPathList> shortestPaths2 = new ArrayList<ShortestPathList>();
            Set<String> seen = new HashSet<String>();
            for (ShortestPathList path : shortestPathsInHiddenNetwork) {
                if (seen.add(path.getLast().getNodeName())) {
                    shortestPaths2.add(path);
                }
            }
            singleShortestPathsInHiddenNetwork.put(root2, shortestPaths2);
            double currentdiametervalue;
            currentdiametervalue = CalculateDiameter(shortestPaths2);
            if (Diameter2 < currentdiametervalue) {
                Diameter2 = currentdiametervalue;
            }
            if (checkedCentralities[7]) {
                BetweennessMethods.updateBetweenness(shortestPathsInHiddenNetwork, BetweennessVectorResults2);
            }
            if (checkedCentralities[11]) {
                edgeBetweenness2.updateEdgeBetweenness(root2, shortestPathsInHiddenNetwork);
            }
        }
        // Create shortest paths for actual network
        for (CyNode root1 : nodeList) {
            if (stop) {
                return;
            }
            shortestPaths = new ShortestPathsPerNode(network, root1, stressMap, StressisOn, true, menu.isWeighted).getPaths();
            // create a single shortest path list
            List<ShortestPathList> shortestPaths2 = new ArrayList<ShortestPathList>();
            Set<String> seen = new HashSet<String>();
            for (ShortestPathList path : shortestPaths) {
                if (seen.add(path.getLast().getNodeName())) {
                    shortestPaths2.add(path);
                }
            }
            double currentdiametervalue;
            currentdiametervalue = CalculateDiameter(shortestPaths2);
            if (Diameter < currentdiametervalue) {
                Diameter = currentdiametervalue;
            }

            if (checkedCentralities[7]) {
                BetweennessMethods.updateBetweenness(shortestPaths, BetweennessVectorResults);
            }

            if (checkedCentralities[11]) {
                edgeBetweenness.updateEdgeBetweenness(root1, shortestPaths);
            }

            singleShortestPaths.put(root1, shortestPaths2);
        }
        Double closenessSum1 = 0.0;
        Double radialitySum1 = 0.0;
        //execute each centrality for actual network
        int k = 0;
        for (Map.Entry<CyNode, List<ShortestPathList>> element : singleShortestPaths.entrySet()) {
            if (stop) {
                return;
            }

            CyNode root = element.getKey();
            List<ShortestPathList> CentiScaPeSingleShortestPathVector = element.getValue();
            if (checkedCentralities[1]) {
                ShortestPathList currentlist;
                for (int j = 0; j < CentiScaPeSingleShortestPathVector.size(); j++) {
                    currentlist = CentiScaPeSingleShortestPathVector.get(j);
                    totalDist = totalDist + currentlist.getCost();
                }
            }
            if (checkedCentralities[2]) {
                directedOutDegreeValues.put(root, (double) (network.getNeighborList(root, CyEdge.Type.OUTGOING).size()));
                directedInDegreeValues.put(root, (double) (network.getNeighborList(root, CyEdge.Type.INCOMING).size()));
            }
            if (checkedCentralities[3]) {
                directedEccentrityValues.put(root, DirectedEccentricity.execute(CentiScaPeSingleShortestPathVector));
            }
            if (checkedCentralities[4]) {
                double value = DirectedRadiality.execute(Diameter, totalnodecount, CentiScaPeSingleShortestPathVector);
                radialitySum1 += value;
                directedRadialityValues.put(root, value);
            }
            if (checkedCentralities[5]) {
                double value = DirectedCloseness.execute(CentiScaPeSingleShortestPathVector);
                closenessSum1 += value;
                directedClosenessValues.put(root, value);
            }
            if (checkedCentralities[8]) {
                CentroidMethods.updateCentroid(CentiScaPeSingleShortestPathVector, root, nodeList, CentroidVectorResults);
            }
            if (checkedCentralities[9]) {
                List<CyNode> neighbors = network.getNeighborList(root, CyEdge.Type.OUTGOING);
                for (CyNode neighbor : neighbors) {
                    adjacencyMatrixOfNetwork[k][nodeList.indexOf(neighbor)] = 1.0;
                }
                k++;
            }

            menu.message("Computing shortest paths for node " + ++nodeworked + " of " + totalnodecount);
        }
        // calcululate each centrality for hidden network
        nodeworked = 0;
        Double closenessSum2 = 0.0;
        Double radialitySum2 = 0.0;
        k = 0;
        for (Map.Entry<CyNode, List<ShortestPathList>> element : singleShortestPathsInHiddenNetwork.entrySet()) {
            if (stop) {
                return;
            }
            CyNode root2 = element.getKey();
            List<ShortestPathList> CentiScaPeSingleShortestPathVector2 = element.getValue();
            if (checkedCentralities[3]) {
                directedEccentrityValues2.put(root2, DirectedEccentricity.execute(CentiScaPeSingleShortestPathVector2));
            }
            if (checkedCentralities[4]) {
                double value = DirectedRadiality.execute(Diameter2, hiddenNetwork.getNodeCount(), CentiScaPeSingleShortestPathVector2);
                radialitySum2 += value;
                directedRadialityValues.put(root2, value);
            }
            if (checkedCentralities[5]) {
                double value = DirectedCloseness.execute(CentiScaPeSingleShortestPathVector2);
                closenessSum2 += value;
                directedClosenessValues2.put(root2, value);
            }
            if (checkedCentralities[8]) {
                CentroidMethods.updateCentroid(CentiScaPeSingleShortestPathVector2, root2, hiddenNodeList, CentroidVectorResults2);
            }
            if (checkedCentralities[9]) {
                List<CyNode> neighbors = hiddenNetwork.getNeighborList(root2, CyEdge.Type.OUTGOING);
                for (CyNode neighbor : neighbors) {
                    adjacencyMatrixOfHiddenNetwork[k][hiddenNodeList.indexOf(neighbor)] = 1.0;
                }
                k++;
            }
        }
        // calculate interference
        CyNode root1;
        for (CyNode root2 : hiddenNodeList) {
            root1 = nodeMap.get(root2);
            if (checkedCentralities[5]) {
                directedClosenessValues3.put(root1, calculateInterference(directedClosenessValues.get(root1), closenessSum1, directedClosenessValues2.get(root2), closenessSum2));
            }
            if (checkedCentralities[4]) {
                directedRadialityValues3.put(root1, calculateInterference(directedRadialityValues.get(root1), radialitySum1, directedRadialityValues2.get(root2), radialitySum2));
            }
            menu.message("Computing Interference for node " + ++nodeworked + " of " + hiddenNetwork.getNodeCount());
        }
        if (checkedCentralities[8]) {
            CentroidMethods.computeCentroid(CentroidVectorResults, totalnodecount, CentroidVectorofNodes);
            CentroidMethods.computeCentroid(CentroidVectorResults2, hiddenNetwork.getNodeCount(), CentroidVectorofNodes2);

        }
        // end of computations
        menu.endOfComputation(totalnodecount);
        centralities.clear();

        //put in table
        if (checkedCentralities[0]) {
            putValuesinTable(network, directedCentralities[0], Diameter);
        }

        if (checkedCentralities[1]) {
            double average = totalDist / (totalnodecount * (totalnodecount - 1));
            putValuesinTable(network, directedCentralities[1], average);
        }
        if (checkedCentralities[2]) {
            putValuesinTable(network, directedCentralities[2], directedOutDegreeValues, centralities);
            putValuesinTable(network, "InDegree", directedInDegreeValues, centralities);
        }
        if (checkedCentralities[3]) {
            Map< CyNode, Double> values = calculateInterferenceFromMap(directedEccentrityValues, directedEccentrityValues2, nodeMap);
            putValuesinTable(network, directedCentralities[3], values, centralities);
        }

        if (checkedCentralities[4]) {
            putValuesinTable(network, directedCentralities[4], directedRadialityValues3, centralities);
        }

        if (checkedCentralities[5]) {
            putValuesinTable(network, directedCentralities[5], directedClosenessValues3, centralities);
        }

        if (checkedCentralities[6]) {
            Set stressSet = stressMap.entrySet();
            for (Iterator i = stressSet.iterator(); i.hasNext();) {
                Map.Entry currentmapentry = (Map.Entry) i.next();
                long currentnodeSUID = (Long) currentmapentry.getKey();
                CyNode currentnode = network.getNode(currentnodeSUID);
                double currentstress = (double) (Double) (currentmapentry.getValue());
                directedStressValues.put(currentnode, currentstress);
            }
            putValuesinTable(network, directedCentralities[6], directedStressValues, centralities);
        }
        if (checkedCentralities[7] && displayBetweennessResults) {
            Map<CyNode, Double> directedBetweennessValues = new HashMap<CyNode, Double>();
            double sum1 = 0.0, sum2 = 0.0;
            for (Iterator i = BetweennessVectorResults2.iterator(); i.hasNext();) {
                FinalResultBetweenness currentnodebetweenness = (FinalResultBetweenness) i.next();
                sum2 += currentnodebetweenness.getBetweenness();
            }
            for (Iterator j = BetweennessVectorResults.iterator(); j.hasNext();) {
                FinalResultBetweenness currentnodebetweenness1 = (FinalResultBetweenness) j.next();
                sum1 += currentnodebetweenness1.getBetweenness();
            }
            for (Iterator i = BetweennessVectorResults2.iterator(); i.hasNext();) {
                FinalResultBetweenness currentnodebetweenness = (FinalResultBetweenness) i.next();
                CyNode node2 = currentnodebetweenness.getNode();
                CyNode node1 = nodeMap.get(node2);
                double int1 = 0.0;
                for (Iterator j = BetweennessVectorResults.iterator(); j.hasNext();) {
                    FinalResultBetweenness currentnodebetweenness1 = (FinalResultBetweenness) j.next();
                    if (currentnodebetweenness1.getNode() == node1) {
                        int1 = currentnodebetweenness1.getBetweenness();
                        break;
                    }
                }
                directedBetweennessValues.put(node1, calculateInterference(int1, sum1, currentnodebetweenness.getBetweenness(), sum2));
            }
            putValuesinTable(network, directedCentralities[7], directedBetweennessValues, centralities);
        }
        if (checkedCentralities[8]) {
            Map<CyNode, Double> directedCentroidValues = new HashMap<CyNode, Double>();
            double sum1 = 0.0, sum2 = 0.0;
            for (Iterator i = CentroidVectorResults.iterator(); i.hasNext();) {
                FinalResultCentroid currentnodeCentroid = (FinalResultCentroid) i.next();
                sum1 += currentnodeCentroid.getCentroid();
            }
            for (Iterator i = CentroidVectorResults2.iterator(); i.hasNext();) {
                FinalResultCentroid currentnodeCentroid = (FinalResultCentroid) i.next();
                sum2 += currentnodeCentroid.getCentroid();
            }
            for (Iterator i = CentroidVectorResults2.iterator(); i.hasNext();) {
                FinalResultCentroid currentnodeCentroid = (FinalResultCentroid) i.next();
                CyNode node2 = currentnodeCentroid.getNode();
                CyNode node1 = nodeMap.get(node2);
                double int1 = 0.0;
                for (Iterator j = CentroidVectorResults.iterator(); j.hasNext();) {
                    FinalResultCentroid currentnodeCentroid1 = (FinalResultCentroid) j.next();
                    if (currentnodeCentroid1.getNode() == node1) {
                        int1 = currentnodeCentroid1.getCentroid();
                        break;
                    }
                }
                directedCentroidValues.put(node1, calculateInterference(int1, sum1, currentnodeCentroid.getCentroid(), sum2));
            }
            putValuesinTable(network, directedCentralities[8], directedCentroidValues, centralities);
        }
        if (checkedCentralities[9]) {
            Map<CyNode, Double> eigenVectorValues1 = CalculateEigenVector.execute(adjacencyMatrixOfNetwork, network);
            Map<CyNode, Double> eigenVectorValues2 = CalculateEigenVector.execute(adjacencyMatrixOfHiddenNetwork, hiddenNetwork);
            Map<CyNode, Double> eigenVectorValues3 = calculateInterferenceFromMap(eigenVectorValues1, eigenVectorValues2, nodeMap);
            putValuesinTable(network, directedCentralities[9], eigenVectorValues3, centralities);
        }

        if (checkedCentralities[10]) {
            Map<CyNode, Double> bridgingValues1 = calculateBridging(network, BetweennessVectorResults);
            Map<CyNode, Double> bridgingValues2 = calculateBridging(hiddenNetwork, BetweennessVectorResults2);
            Map<CyNode, Double> bridgingValues3 = calculateInterferenceFromMap(bridgingValues1, bridgingValues2, nodeMap);
            putValuesinTable(network, directedCentralities[10], bridgingValues3, centralities);

        }
        if (checkedCentralities[11]) {
            Map<CyEdge, Double> values = edgeBetweenness.getEdgeBetweennessMap();
            Map<CyEdge, Double> values2 = edgeBetweenness2.getEdgeBetweennessMap();
            Map<CyEdge, Double> values3 = calculateInterferenceFromMap(values, values2, CreateHiddenNetwork.edgeMap);
            putValuesinEdgeTable(network, directedCentralities[11], values3, centralities);
        }
        interferencecore.createVisualizer(centralities);
    }

    public static void putValuesinTable(CyNetwork network, String heading, double result) {
        CyTable networkTable = network.getDefaultNetworkTable();
        networkTable.createColumn(heading, Double.class, false);
        network.getRow(network).set(heading, new Double(result));
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
        networkTable.createColumn(networkattheading + " Max value Dir", Double.class, false);
        networkTable.createColumn(networkattheading + " min value Dir", Double.class, false);
        double mean = totalsum / totalnodecount;
        networkTable.createColumn(networkattheading + " mean value Dir", Double.class, false);
        network.getRow(network).set(networkattheading + " Max value Dir", new Double(max));
        network.getRow(network).set(networkattheading + " min value Dir", new Double(min));
        network.getRow(network).set(networkattheading + " mean value Dir", new Double(mean));

        centralities.add(new NodeCentrality(heading, mean, min, max));
    }

    public static void putValuesinEdgeTable(CyNetwork network, String heading, Map<CyEdge, Double> values, Vector centralities) {
        CyTable edgeTable = network.getDefaultEdgeTable();
        edgeTable.createColumn(heading, Double.class, false);
        Set<CyEdge> edges = values.keySet();
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, totalsum = 0, currentvalue;
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            CyEdge root = (CyEdge) it.next();
            currentvalue = values.get(root);
            if (currentvalue < min) {
                min = currentvalue;
            }

            if (currentvalue > max) {
                max = currentvalue;
            }

            totalsum = totalsum + currentvalue;
            CyRow row = edgeTable.getRow(root.getSUID());
            row.set(heading, new Double(currentvalue));
        }
        // remove last word of the heading
        String firstWords = heading.substring(0, heading.lastIndexOf(" "));
        String lastWord = heading.substring(heading.lastIndexOf(" ") + 1);
        network.getDefaultNetworkTable().createColumn(firstWords + " Max value "+lastWord, Double.class, false);
        network.getDefaultNetworkTable().createColumn(firstWords + " min value "+lastWord, Double.class, false);
        double mean = totalsum / edges.size();
        network.getDefaultNetworkTable().createColumn(firstWords + " mean value "+lastWord, Double.class, false);
        network.getRow(network).set(firstWords + " Max value "+lastWord, new Double(max));
        network.getRow(network).set(firstWords + " min value "+lastWord, new Double(min));
        network.getRow(network).set(firstWords + " mean value "+lastWord, new Double(mean));

        // for embending centrality in Results Panel -- These two lines are enough
        centralities.add(new NodeCentrality(heading, mean, min, max));
    }

    private double CalculateDiameter(List<ShortestPathList> paths) {
        int max = 0;
        for (ShortestPathList path : paths) {
            max = Math.max(max, path.getCost());
        }

        return max;
    }

    public static Map<CyNode, Double> calculateBridging(CyNetwork network, Vector BetweennessVectorResults) {
        Map<CyNode, Double> bridgingValues = new HashMap<CyNode, Double>();
        for (Iterator i = BetweennessVectorResults.iterator(); i.hasNext();) {
            FinalResultBetweenness currentnodebetweenness = (FinalResultBetweenness) i.next();
            double currentbetweenness = currentnodebetweenness.getBetweenness();
            CyNode root = currentnodebetweenness.getNode();
            List<CyNode> bridgingNeighborList = network.getNeighborList(root, CyEdge.Type.ANY);
            double bridgingCoefficient = 0;
            if (!bridgingNeighborList.isEmpty()) {
                double BCNumerator = (1 / (double) (bridgingNeighborList.size()));
                double BCDenominator = 0;
                for (CyNode bridgingNeighbor : bridgingNeighborList) {
                    if (!network.getNeighborList(bridgingNeighbor, CyEdge.Type.ANY).isEmpty()) {
                        BCDenominator = BCDenominator + 1 / (double) (network.getNeighborList(bridgingNeighbor, CyEdge.Type.ANY).size());
                    }
                }
                if (BCDenominator != 0) {
                    bridgingCoefficient = BCNumerator / BCDenominator;
                } else {
                    bridgingCoefficient = 0.0;
                }
            }
            double bridgingCentrality = bridgingCoefficient * currentbetweenness;
            bridgingValues.put(root, bridgingCentrality);
        }
        return bridgingValues;
    }

    public static double calculateInterference(double int1, double sum1, double int2, double sum2) {
        double value = 0.0;
        value = ((double) (int1 / sum1) - (double) (int2 / sum2)) * 100;
        System.out.println(int1 + "/" + sum1 + "-" + int2 + "/" + sum2 + "*100 = " + value);
        return value;
    }

    public static <K extends CyIdentifiable> Map<K, Double> calculateInterferenceFromMap(Map<K, Double> map1, Map<K, Double> map2, Map<K, K> nodeMap) {
        Map< K, Double> InterferenceValues = new HashMap< K, Double>();
        Double sum1 = 0.0, sum2 = 0.0;
        for (Map.Entry<K, Double> e : map1.entrySet()) {
            sum1 += e.getValue();
        }
        for (Map.Entry<K, Double> e : map2.entrySet()) {
            sum2 += e.getValue();
        }
        for (Map.Entry<K, Double> e : map2.entrySet()) {
            K root2 = e.getKey();
            K root1 = nodeMap.get(root2);
            Double int1 = map1.get(root1);
            Double int2 = e.getValue();
            InterferenceValues.put(root1, calculateInterference(int1, sum1, int2, sum2));
        }
        return InterferenceValues;
    }
    // Used for testing/ printing a Map<CyNode, Double>

    public static void printValues(CyNetwork n, Map<CyNode, Double> values) {
        CyTable table = n.getDefaultNodeTable();
        for (Map.Entry<CyNode, Double> element : values.entrySet()) {
            CyNode root = element.getKey();
            System.out.print(root.toString() + table.getRow(root.getSUID()).get(CyNetwork.NAME, String.class));
            System.out.println(" : " + element.getValue());
        }
    }
}
