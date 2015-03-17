package org.cytoscape.interference.internal;

/**
 * @author scardoni
 */

import java.util.Iterator;
import java.util.LinkedList;

public class ShortestPathList implements Iterable<MultiSPath> {
	private final LinkedList<MultiSPath> paths = new LinkedList<MultiSPath>();

	public ShortestPathList() {}

	public ShortestPathList(ShortestPathList parent) {
		paths.addAll(parent.paths);
	}

	public void addFirst(MultiSPath first) {
		paths.addFirst(first);
	}

	public MultiSPath getFirst() {
		return paths.getFirst();
	}

	public MultiSPath getLast() {
		return paths.getLast();
	}

	@Override
	public String toString() {
		String result = " ";
		for (MultiSPath path: this)
			result += " " + path.getNodeName();

		return result + " : distance: " + getCost();
	}

	public int getCost() {
		return paths.getLast().getCost();
	}

	@Override
	public Iterator<MultiSPath> iterator() {
		return paths.iterator();
	}
}