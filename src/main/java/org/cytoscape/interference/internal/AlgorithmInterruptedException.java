package org.cytoscape.interference.internal;

/**
 * @author faizaan.shaik
 */
public class AlgorithmInterruptedException extends Exception {

    public AlgorithmInterruptedException() {
        super("The user stopped CentiScaPe while it was running");
    }
}
