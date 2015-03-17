/*
 * IntPanelFactory.java
 *
 * Created on 27 novembre 2007, 16.16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.cytoscape.interference.internal.visualizer;

import java.util.Observer;

import org.cytoscape.interference.internal.centralities.Centrality;


/**
 *
 * @author admini
 */
public abstract class IntPanelFactory {
    
    private static Centrality cent;
    
    public static IntPanel allocateCentralityPanel(Observer obs,Centrality c){
        IntPanel cp=new IntPanel(obs,c);
//        System.out.println("centrality "+c.getName()+
//                " min="+c.getMinValue()+
//                " max="+c.getMaxValue()+
//                " def="+c.getDefaultValue());
        cp.setBorder(javax.swing.BorderFactory.createTitledBorder(c.getName()));
        cp.setSlider(c.getMeanValue());
        return(cp);
    }
}
