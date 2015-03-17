package org.cytoscape.interference.internal.radiality;

import java.util.List;

import org.cytoscape.interference.internal.ShortestPathList;

/**
 * @author faizaan.shaik
 */

public class DirectedRadiality {

	public static double execute(double diameter, int totalNodeCount, List<ShortestPathList> shortestPathsList){
		double radiality = diameter * (totalNodeCount - 1);
		for (ShortestPathList pathList: shortestPathsList)
			radiality -= 1.0 / pathList.getCost();

		return radiality == 0.0 ? 0.0 : (1.0 / radiality);
	}
}