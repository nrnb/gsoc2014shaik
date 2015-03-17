package org.cytoscape.interference.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

/**
 * @author faizaan
 */

public class ShortestPathsPerNode {
	private final List<ShortestPathList> result = new ArrayList<ShortestPathList>();

	public List<ShortestPathList> getPaths() {
		return result;
	}

	public ShortestPathsPerNode(final CyNetwork network, final CyNode root, final SortedMap<Long, Double> stressMap, final boolean stressIsOn, final boolean directed, final boolean isWeighted) {

		class Builder {
			private final LinkedList<MultiSPath> queue = new LinkedList<MultiSPath>();
			private final Set<MultiSPath> temporaryPaths = new HashSet<MultiSPath>();
			private CyNode target;

			private Builder() {
				initialize();

				for (MultiSPath path: main()) {
					ShortestPathList prova = new ShortestPathList();
					result.add(prova);
					target = path.getNode();

					createShortestPaths(prova, path);
				}
			}

			private void initialize() {
				// shortest path for the root
				queue.add(new MultiSPath(root, 0, network));

				// and for the other nodes
				int nodesCountPlusOne = network.getNodeCount() + 1;
				for (CyNode node: network.getNodeList())
					if (!node.equals(root))
						temporaryPaths.add(new MultiSPath(node, nodesCountPlusOne, network));
			}

			private Set<MultiSPath> main() {
				Set<MultiSPath> paths = new HashSet<MultiSPath>();

				while (!queue.isEmpty()) {
					MultiSPath path = queue.pop();
					if (!path.getNode().equals(root))
						paths.add(path);

					Iterable<CyNode> neighbors;
					if (directed)
						//TODO perché nel caso diretto non rimuove i duplicati?
						neighbors = network.getNeighborList(path.getNode(), CyEdge.Type.OUTGOING);
					else
						neighbors = new HashSet<CyNode>(network.getNeighborList(path.getNode(), CyEdge.Type.ANY));

					for (CyNode neighbor: neighbors)
						relax(path, neighbor);
				}

				return paths;
			}

			private void relax(MultiSPath path, CyNode neighbor) {
				MultiSPath neighborPath = findSPath(neighbor, temporaryPaths);
				if (neighborPath != null) {
					int distance = getDistance(path.getNode(), neighbor);
					neighborPath.setCost(path.getCost() + distance);
					neighborPath.addPredecessor(path);
					queue.addLast(neighborPath);
					temporaryPaths.remove(neighborPath);
				}
				else {
					// if Neighbor is not in TempSet verify if it is in Queue
					neighborPath = findSPath(neighbor, queue);
					// if yes put it in neighborPath
					if (neighborPath != null) {
						// then verify if its cost is greater then that of the current path
						int distance = getDistance(path.getNode(), neighbor);
						if (neighborPath.getCost() >= path.getCost() + distance) {
							// if yes we have found a new minimum shortest path so we have another predecessor
							// for the neighbor; we update the cost (useless) and add the new predecessor
							if (neighborPath.getCost() > path.getCost() + distance)
								neighborPath.removeAllPredecessors();

							neighborPath.setCost(path.getCost() + distance);
							neighborPath.addPredecessor(path);
						}
					}
				}
			}

			private MultiSPath findSPath(CyNode node, Iterable<MultiSPath> where) {
				for (MultiSPath path: where)
					if (node.equals(path.getNode()))
						return path;

				return null;
			}

			private int createShortestPaths(ShortestPathList pathList, MultiSPath path) {
				pathList.addFirst(path);
				// if the node is not the root, we enter to recursive calls
				if (path.getNumberOfPredecessors() > 0) {
					ShortestPathList originalPathList = new ShortestPathList(pathList);
					int stress = 0;
					final CyNode parent = path.getNode();
					long parentNodeSUID = parent.getSUID();
					boolean firstIteration = true;
					for (MultiSPath predecessor: path.getPredecessors()) {
						if (firstIteration) {
							firstIteration = false;

							if (parent.equals(target))
								createShortestPaths(pathList, predecessor);
							else
								stress += createShortestPaths(pathList, predecessor);
						}
						else {
							// if there are more predecessors we have to build a new element for each predecessor
							// but the first, since each predecessor contributes with a distinct shortest path
							ShortestPathList newlist = new ShortestPathList(originalPathList);
							result.add(newlist);
							if (parent.equals(target))
								createShortestPaths(newlist, predecessor);
							else
								stress += createShortestPaths(newlist, predecessor);
						}
					}
					if (stressIsOn)
						synchronized (stressMap) {
							stressMap.put(parentNodeSUID, stressMap.get(parentNodeSUID) + stress);
						}

					return stress;
				}
				else
					return 1;
			}

			private int getDistance(CyNode sourceNode, CyNode targetNode) {
				if (isWeighted) {
					CyTable edgeTable = network.getDefaultEdgeTable();
					List<CyEdge> edges = network.getConnectingEdgeList(sourceNode, targetNode, CyEdge.Type.ANY);
					CyEdge currentEdge = edges.get(0);

					Number num = (Number) edgeTable.getRow(currentEdge.getSUID()).get(IntStartMenu.edgeWeightAttribute, IntStartMenu.attrtype);
					if (num != null)
						return (int) Math.round(num.doubleValue());
				}

				return 1;
			}
		}

		new Builder();
	}
}