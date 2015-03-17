package org.cytoscape.interference.internal.eigenVector;

import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.util.HashMap;
import java.util.Map;

/**
 * @author faizaan.shaik
 */

public class CalculateEigenVector {

	public static Map<CyNode, Double> execute(double[][] adjacencyMatrixOfNetwork, CyNetwork network){
                
		Matrix A = new Matrix(adjacencyMatrixOfNetwork);
		EigenvalueDecomposition e = A.eig();
		Matrix V = e.getV();
		double[][] eigenVectors = V.getArray();
                
                List<CyNode> nodeList = network.getNodeList();
                int numberOfNodes = nodeList.size();
                Map<CyNode, Double> eigenVectorValues = new HashMap<CyNode, Double>();
                for (int j = 0; j < numberOfNodes; j++) {
			double value = eigenVectors[j][numberOfNodes - 1];
                        eigenVectorValues.put(nodeList.get(j), value);
                }
                
                return eigenVectorValues;
	}
}