package org.cytoscape.interference.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

/**
 *
 * @author faizaan.shaik
 */
public class IntMenuAction extends AbstractCyAction {

    public CyApplicationManager cyApplicationManager;
    public CySwingApplication cyDesktopService;
    public CyActivator cyactivator;

    public IntMenuAction(CyApplicationManager cyApplicationManager, final String menuTitle, CyActivator cyactivator) {

        super(menuTitle, cyApplicationManager, null, null);
        setPreferredMenu("Apps");
        this.cyactivator = cyactivator;
        this.cyApplicationManager = cyApplicationManager;
        this.cyDesktopService = cyactivator.getDesktopService();

    }

    public void actionPerformed(ActionEvent e) {
        Core interferencecore = new Core(cyactivator);
    }

}
