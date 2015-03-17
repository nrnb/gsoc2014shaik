/**
 * @author scardoni
 */

package org.cytoscape.interference.internal.closeness;

import java.util.List;

import org.cytoscape.interference.internal.ShortestPathList;
import org.cytoscape.model.CyNode;

public class ClosenessMethods {

	public static FinalResultCloseness CalculateCloseness(List<ShortestPathList> shortestPaths, CyNode root ) {
		FinalResultCloseness closeness = new FinalResultCloseness(root, 0);

		int sum = 0;
		for (ShortestPathList path: shortestPaths)
			sum += path.getCost();

		//TODO cosa succede se sum == 0 e shortestPaths non è vuoto?
		double value = shortestPaths.isEmpty() ? 0.0 : (1.0 / sum);

		closeness.update(value);

		return closeness;
	} 
}