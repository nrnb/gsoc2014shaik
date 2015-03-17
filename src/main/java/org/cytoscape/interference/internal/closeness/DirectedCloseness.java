package org.cytoscape.interference.internal.closeness;

import java.util.List;
import org.cytoscape.interference.internal.ShortestPathList;

/**
 * @author faizaan.shaik
 */
public class DirectedCloseness {

    public static double execute(List<ShortestPathList> shortestPaths) {
        double closeness = 0;
        int cost = 0;
        for (ShortestPathList path : shortestPaths) {
            cost = path.getCost();
            if(cost != 0 )
                closeness += 1.0 / cost;
        }

        return closeness;
    }
}
