package org.cytoscape.interference.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * @author scardoni
 */

public class ShortestPaths {
	private final ConcurrentMap<CyNode, List<ShortestPathList>> shortestPathsPerNode = new ConcurrentHashMap<CyNode, List<ShortestPathList>>();
	private final ConcurrentMap<CyNode, List<ShortestPathList>> reducedShortestPathsPerNode = new ConcurrentHashMap<CyNode, List<ShortestPathList>>();
	private final int nodesCount;
	private final int diameter;

	public ShortestPaths(final CyNetwork network, final SortedMap<Long, Double> stressMap, final boolean stressIsOn, final boolean directed, final IntStartMenu menu, final Stoppable algorithm) throws AlgorithmInterruptedException {
		final AtomicInteger nodesAlreadyProcessed = new AtomicInteger();
        final int nodesCount = network.getNodeCount();
        final Iterator<CyNode> iterator = network.getNodeList().iterator();

        class Worker extends Thread {

        	@Override
        	public void run() {
        		CyNode root;

        		while (true) {
        			if (algorithm.isStopped())
        				return;

        			synchronized (iterator) {
	        			if (iterator.hasNext())
	        				root = iterator.next();
	        			else
	        				return;
        			}

        			menu.message("Computing shortest paths for node " + nodesAlreadyProcessed.incrementAndGet() + " of " + nodesCount);

        	       	List<ShortestPathList> shortestPaths = new ShortestPathsPerNode(network, root, stressMap, stressIsOn, directed, menu.isWeighted).getPaths();

                    List<ShortestPathList> reducedShortestPaths = new ArrayList<ShortestPathList>();
                    Set<String> seen = new HashSet<String>();
                    for (ShortestPathList path: shortestPaths)
                    	if (seen.add(path.getLast().getNodeName()))
                            reducedShortestPaths.add(path);

                    shortestPathsPerNode.put(root, shortestPaths);
                    reducedShortestPathsPerNode.put(root, reducedShortestPaths);
        		}
        	}
        }

        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        Worker[] workers = new Worker[numberOfProcessors];
        for (int pos = 0; pos < numberOfProcessors; pos++)
        	(workers[pos] = new Worker()).start();

        for (Worker worker: workers)
        	try {
        		worker.join();
        	}
        	catch (InterruptedException e) {}

        if (algorithm.isStopped())
        	throw new AlgorithmInterruptedException();

        this.nodesCount = nodesAlreadyProcessed.get();
        this.diameter = computeDiameter();
	}

	public Iterable<CyNode> getNodes() {
		return shortestPathsPerNode.keySet();
	}

	public int getNodesCount() {
		return nodesCount;
	}

	public List<ShortestPathList> getFor(CyNode node) {
		return shortestPathsPerNode.get(node);
	}

	public List<ShortestPathList> getReducedFor(CyNode node) {
		return reducedShortestPathsPerNode.get(node);
	}

	public int getDiameter() {
		return diameter;
	}

	private int computeDiameter() {
		int diameter = 0;
       	for (Iterable<ShortestPathList> paths: reducedShortestPathsPerNode.values())
       		diameter = Math.max(diameter, computeDiameter(paths));

        return diameter;
	}

	private static int computeDiameter(Iterable<ShortestPathList> paths) {
        int max = 0;
        for (ShortestPathList path: paths)
            max = Math.max(max, path.getCost());

        return max;
    }
}