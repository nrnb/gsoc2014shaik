package org.cytoscape.interference.internal.betweenness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.interference.internal.MultiSPath;
import org.cytoscape.interference.internal.ShortestPathList;

/**
 * @author scardoni 
 */

public class BetweennessMethods {

	public static void updateBetweenness(List<ShortestPathList> shortestPaths, List<FinalResultBetweenness> results) {
		List<BetweennessResult> betweenness = new ArrayList<BetweennessResult>(); 
		List<BetweennessElement> newResults = new ArrayList<BetweennessElement>();

		for (ShortestPathList path: shortestPaths) {
			String sourceName = path.getFirst().getNodeName();
			String targetName = path.getLast().getNodeName();
			int index = indexOf(sourceName, targetName, betweenness);
			BetweennessResult result;

			if (index == -1) {
				result = new BetweennessResult(sourceName, targetName); 
				betweenness.add(result);
			}
			else {
				result = betweenness.get(index);
				result.incrementSPcount();
			}

			Iterator<MultiSPath> iterator = path.iterator();
			if (iterator.hasNext()) {
				iterator.next(); // discard the first

				if (iterator.hasNext())
					do {
						MultiSPath currentmultispath = iterator.next();
						if (iterator.hasNext()) // also discard the last
							result.update(currentmultispath.getSUID());
					}
					while (iterator.hasNext());
			}
		}

		for (BetweennessResult currentresult: betweenness) {
			currentresult.computeBetweennessCount();
			newResults.addAll(currentresult.getElements());
		}
		// adesso devo aggiungere gli elementi 
		updateResults(newResults, results);
	}

	private static int indexOf(String sourceName, String targetName, Iterable<BetweennessResult> betweenness) {
		int i = 0;
		for (BetweennessResult result: betweenness)
			if (result.exist(sourceName, targetName))
				return i;
			else
				i++;

		return -1;
	}

	private static void updateResults(Iterable<BetweennessElement> newElements, Iterable<FinalResultBetweenness> results) {
		for (BetweennessElement newElement: newElements)
			insertNewValue(newElement, results);
	}

	private static void insertNewValue(BetweennessElement newElement, Iterable<FinalResultBetweenness> results) {
		for (FinalResultBetweenness result: results)
			if (newElement.getSUID() == result.getSUID())
				result.update(newElement.getBetweennessCount());
	}
}