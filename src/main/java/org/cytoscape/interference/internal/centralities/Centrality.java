package org.cytoscape.interference.internal.centralities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

/**
 * @author admini
 */

public abstract class Centrality {
	private final String name;
	private final boolean directed;
	private double mean;
	private double min;
	private double max;

	protected Centrality(String name, boolean directed) {
		this.name = name;  
		this.directed = directed;
	}

	protected void initMeanMinMax(double mean, double min, double max) {
		this.mean = mean;
		this.min = min;
		this.max = max;
	}

	public final String getName() {
		if (name.endsWith("Dir"))
			return name;
		else
			return name + ' ' + direction();
	}

	protected final String getSimpleName() {
		return name;
	}

	protected final String direction() {
		return directed ? "Dir" : "unDir";
	}

	public final double getMeanValue() {
		return mean;
	}

	public final double getMinValue() {
		return min;
	}

	public final double getMaxValue() {
		return max;
	}

    @Override
    public final String toString() {
    	return getName();
    }

	public void showInPanelFor(CyNetwork network) {
	    CyTable networkTable = network.getDefaultNetworkTable();

	    if (networkTable.getColumn(maxColumnName()) != null)
	    	networkTable.deleteColumn(maxColumnName());
	    networkTable.createColumn(maxColumnName(), Double.class, false);
	    if (networkTable.getColumn(minColumnName()) != null)
	    	networkTable.deleteColumn(minColumnName());
	    networkTable.createColumn(minColumnName(), Double.class, false);
	    if (networkTable.getColumn(meanColumnName()) != null)
	    	networkTable.deleteColumn(meanColumnName());
	    networkTable.createColumn(meanColumnName(), Double.class, false);
	    network.getRow(network).set(maxColumnName(), max);
	    network.getRow(network).set(minColumnName(), min);
	    network.getRow(network).set(meanColumnName(), mean);
	}

	public void removeFromPanel(CyNetwork network) {
		CyTable networkTable = network.getDefaultNetworkTable();
		String columnName = maxColumnName();
	    if (networkTable.getColumn(columnName) != null)
	    	networkTable.deleteColumn(columnName);
	
	    columnName = minColumnName();
	    if (networkTable.getColumn(columnName) != null)
	    	networkTable.deleteColumn(columnName);
	
	    columnName = meanColumnName();
	    if (networkTable.getColumn(columnName) != null)
	    	networkTable.deleteColumn(columnName);
	}

	private String meanColumnName() {
		return getSimpleName() + " mean value " + direction();
	}

	private String minColumnName() {
		return getSimpleName() + " min value " + direction();
	}

	private String maxColumnName() {
		return getSimpleName() + " max value " + direction();
	}
}