package org.cytoscape.interference.internal.eccentricity;

import java.util.List;

import org.cytoscape.interference.internal.ShortestPathList;

/**
 * @author faizaan.shaik
 */

public class DirectedEccentricity {
	public static double execute(List<ShortestPathList> shortestPaths){
		if (shortestPaths.isEmpty())
			return 0.0;

		int max = 0;
		for (ShortestPathList path: shortestPaths)
			max = Math.max(max, path.getCost());

		return 1.0 / max;
	}
}