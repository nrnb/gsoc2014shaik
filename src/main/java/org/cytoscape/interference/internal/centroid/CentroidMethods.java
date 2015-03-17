/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.centroid;

import java.util.List;

import org.cytoscape.interference.internal.ShortestPathList;
import org.cytoscape.model.CyNode;

public class CentroidMethods {

	private static int numberOfCloseNodes(FinalResultCentroid node1, FinalResultCentroid node2, List<CyNode> nodes) {
		int node1count = 0;
		int node2count = 0;
		int node1index = nodes.indexOf(node1);
		int node2index = nodes.indexOf(node2);

		for (int i = 0; i < nodes.size(); i++)
				if (node1.getDistanceAt(i) < node2.getDistanceAt(i))
					node1count++;
				else if (node1.getDistanceAt(i) > node2.getDistanceAt(i))
					node2count++;

		return node1count - node2count;
	}

	public static void updateCentroid(List<ShortestPathList> shortestPaths, CyNode root, List<CyNode> nodes, List<FinalResultCentroid> results) {
		FinalResultCentroid centroid = new FinalResultCentroid(root, nodes.size());
                int indexOfRoot = nodes.indexOf(root);
                centroid.updatevector(indexOfRoot, 0);
		for (ShortestPathList path: shortestPaths) {
			// prendo la distanza
			int cost = path.getCost();
			// guardo a che indice e' il nodo nel vettore
			int index = nodes.indexOf(path.getLast().getNode());
			// aggiorno l'elemento centroide di root con la distanza
                        if(index < nodes.size() && index >= 0)
                            centroid.updatevector(index, cost);
		}

		// da rivedere
		results.add(centroid);
	}

	public static void computeCentroid(List<FinalResultCentroid> results, int nodesCount, List<CyNode> nodes) {
		for (FinalResultCentroid node1: results) {
			int min = nodesCount + 1;

			for (FinalResultCentroid node2: results) 
				if (node1.getSUID() != node2.getSUID())
					min = Math.min(min, numberOfCloseNodes(node1, node2, nodes));

			node1.update(min);
		}
	}
}