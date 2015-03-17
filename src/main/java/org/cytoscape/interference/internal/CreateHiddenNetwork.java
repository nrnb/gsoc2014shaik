package org.cytoscape.interference.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;

/**
 *
 * @author faizaan.shaik
 */
public class CreateHiddenNetwork {

    public static Map<CyEdge, CyEdge> edgeMap = new HashMap<CyEdge, CyEdge>();

    public static CyNetwork create(CyNetwork n, final IntStartMenu menu) {
        //Get the selected nodes
        List<CyNode> selectedNodes = CyTableUtil.getNodesInState(n, "selected", true);
        if (selectedNodes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please Select atleast 1 node");
            menu.stopcalculus();
            return null;
        }
        //Get the unselected Nodes
        List<CyNode> unSelectedNodes = CyTableUtil.getNodesInState(n, "selected", false);
        CyNetwork hiddenNetwork;
        List<CyNode> nodeList = n.getNodeList();
        CyTable nodeTable = n.getDefaultNodeTable();
        int totalNodeCount = nodeList.size();
        // To get a reference of CyNetworkFactory at CyActivator class of the App
        CyNetworkFactory networkFactory = CyActivator.getNetworkFactory();
        // Create a new network
        hiddenNetwork = networkFactory.createNetwork();
        // Set name for network
        hiddenNetwork.getRow(hiddenNetwork).set(CyNetwork.NAME, "Network with selected Nodes removed");

        // Add Node (Dont add selected nodes)
        List<CyNode> nodesInNewNetwork = new ArrayList<CyNode>(unSelectedNodes.size());
        for (int i = 0; i < unSelectedNodes.size(); i++) {
            nodesInNewNetwork.add(hiddenNetwork.addNode());
        }
        // Set name for new nodes
        for (int i = 0; i < unSelectedNodes.size(); i++) {
            hiddenNetwork.getRow(nodesInNewNetwork.get(i)).set(CyNetwork.NAME, nodeTable.getRow(unSelectedNodes.get(i).getSUID()).get(CyNetwork.NAME, String.class));
        }
        //Create a new column in Edge Table of hidden network with name "IntStartMenu.edgeWeightAttribute"
        if (menu.isWeighted) {
            String edgeAttr = IntStartMenu.edgeWeightAttribute;
            hiddenNetwork.getDefaultEdgeTable().createColumn(edgeAttr, IntStartMenu.attrtype, false);
        }
        //add edges
        List<CyEdge> edgeList = n.getEdgeList();
        List<CyNode> hiddenNodeList = hiddenNetwork.getNodeList();
        for (CyEdge edge : edgeList) {
            CyNode source = edge.getSource();

            CyNode target = edge.getTarget();
            boolean isDirected = edge.isDirected();
            if (unSelectedNodes.contains(source) && unSelectedNodes.contains(target)) {
                String sourceNodeName = nodeTable.getRow(source.getSUID()).get(CyNetwork.NAME, String.class);
                String targetNodeName = nodeTable.getRow(target.getSUID()).get(CyNetwork.NAME, String.class);
                CyNode newSource = null, newTarget = null;
                for (CyNode root : hiddenNodeList) {
                    String rootName = hiddenNetwork.getDefaultNodeTable().getRow(root.getSUID()).get(CyNetwork.NAME, String.class);
                    if (rootName.equals(sourceNodeName)) {
                        newSource = root;
                    }
                    if (rootName.equals(targetNodeName)) {
                        newTarget = root;
                    }
                }
                if (newSource != null && newTarget != null) {
                    CyEdge e = hiddenNetwork.addEdge(newSource, newTarget, isDirected);
                    edgeMap.put(e, edge);
                    if (menu.isWeighted) {
                        CyRow row = hiddenNetwork.getDefaultEdgeTable().getRow(e.getSUID());
                        row.set(IntStartMenu.edgeWeightAttribute, getDistance(edge, n.getDefaultEdgeTable(), menu.isWeighted));
                    }
                } else {
                    System.out.println("There is a problem in creating hidden network, please check!");
                }
            }
        }
        // Used for Testing Hidden Network : unselected nodes, nodelist, hidden network
//        for(CyNode root: nodeList){
//            System.out.println("Node name : "+nodeTable.getRow(root.getSUID()).get(CyNetwork.NAME, String.class)+" - "+root.getSUID());
//        }
//        for(CyNode root: unSelectedNodes){
//            System.out.println("Node name : "+nodeTable.getRow(root.getSUID()).get(CyNetwork.NAME, String.class)+" - "+root.getSUID());
//        }
//        List<CyNode> hiddenNodeList = hiddenNetwork.getNodeList();
//        for(CyNode root: hiddenNodeList){
//            System.out.println("Node name : "+hiddenNetwork.getDefaultNodeTable().getRow(root.getSUID()).get(CyNetwork.NAME, String.class)+" - "+root.getSUID());
//        }
//        // Add the network to Cytoscape
//        CyNetworkManager networkManager = CyActivator.getNetworkManager();
//        networkManager.addNetwork(hiddenNetwork);

        //Add view to cyto
//        CyNetworkView myView = CyActivator.getNetworkViewFactory().createNetworkView(hiddenNetwork);
//        CyActivator.getNetworkViewManager().addNetworkView(myView);
        return hiddenNetwork;
    }

    public static int getDistance(CyEdge currentEdge, CyTable edgeTable, boolean isWeighted) {
        if (isWeighted) {
            Number num = (Number) edgeTable.getRow(currentEdge.getSUID()).get(IntStartMenu.edgeWeightAttribute, IntStartMenu.attrtype);
            if (num != null) {
                return (int) Math.round(num.doubleValue());
            }
        }
        return 1;
    }
}
