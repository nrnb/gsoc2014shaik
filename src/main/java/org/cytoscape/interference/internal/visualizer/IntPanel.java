/*
 * IntPanel.java
 *
 * Created on 18 dicembre 2007, 12.15
 */

package org.cytoscape.interference.internal.visualizer;

import java.util.Observable;
import java.util.Observer;

import org.cytoscape.interference.internal.centralities.Centrality;



/**
 *
 * @author  admini
 */
public class IntPanel extends javax.swing.JPanel implements Cloneable {

    public class MyObservable extends Observable {

        public void doIt(IntPanel cp) {
            this.setChanged();
            notifyObservers(cp);
        }
    }
    public Centrality cent;
    // variable updated by slider movement (contains the threshold)
    public double threshold;
    // minor (true) or major (false)
    public boolean minor;
    //private Vector<CyNode> nodeSelectState;
    public MyObservable notifier;
    public static String newline = System.getProperty("line.separator");
    // threshold value for this centrality panel
    private int val;
    public boolean isActive;
    // when key updating, don't consider slider's data.
    private boolean dontUpdate;
        
    public IntPanel(Observer observer, Centrality c) {
        cent = c;
        minor = true;
        isActive = true;
        initComponents();
        less.setSelected(true);
        notifier = new MyObservable();
        this.setCentralityRanges(c.getMinValue(), c.getMaxValue(), c.getMeanValue());
        registerTheObserver(observer);
        dontUpdate = false;
        this.setToolTip();
    }

    private void setToolTip() {
        String tt = "<html>min value=" +
                cent.getMinValue() +
                "<br>" +
                " max value=" +
                cent.getMaxValue() +
                "<br>" +
                " average value=" +
                cent.getMeanValue() +
                "</html>";
        super.setToolTipText(tt);
    }

    private void registerTheObserver(Observer obs) {
        // when a slider is moved, update the selected list in cytoscape.currentnetworkview
        //System.out.println("registrazione observer");
        notifier.addObserver(obs);
    }

    /** slider parameters setup*/
    private void setCentralityRanges(double min, double max, double init) {
        CentalitySlider.setMinimum(0);
        this.CentalitySlider.setMaximum(100);
        int i = (int) Math.floor(init * 100 / (max - min));
        this.CentalitySlider.setValue(i);
        this.CentralityValue.setText(String.valueOf(Double.toString(init)));
    }

    private void updatedByTextField() {
        try {
            threshold = Double.parseDouble(CentralityValue.getText());
        } catch (NumberFormatException ex) {
        }
        notifier.doIt(this);
        setSlider(threshold);
    }

    //** slider automatic setup */   
    public  void setSlider(double thr) {
        int pos = (int) (thr * 100 / cent.getMaxValue());
        dontUpdate = true;
        CentalitySlider.setValue(pos);
    }

    /** Creates new form CentralityPanel */
    public IntPanel() {
        //System.out.println("IntPanel.centralityPanel");
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        moreLess = new javax.swing.ButtonGroup();
        CentalitySlider = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        less = new javax.swing.JRadioButton();
        more = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        CentralityValue = new javax.swing.JTextField();
        updateValue = new javax.swing.JButton();
        restoreValue = new javax.swing.JButton();
        active = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createTitledBorder("CentralityPanel"));
        setMaximumSize(new java.awt.Dimension(500, 170));

        CentalitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CentalitySliderStateChanged(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Centrality value is"));

        moreLess.add(less);
        less.setSelected(true);
        less.setText("less/equals threshold");
        less.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lessActionPerformed(evt);
            }
        });

        moreLess.add(more);
        more.setText("more/equals threshold");
        more.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(less)
                    .add(more))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(less)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(more)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Threshold"));

        CentralityValue.setText("centralityVal");
        CentralityValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CentralityValueActionPerformed(evt);
            }
        });

        updateValue.setText("update");
        updateValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateValueActionPerformed(evt);
            }
        });

        restoreValue.setText("restore");
        restoreValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreValueActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, CentralityValue)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                        .add(updateValue)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(restoreValue)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(CentralityValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(updateValue)
                    .add(restoreValue))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        active.setSelected(true);
        active.setText("active");
        active.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(CentalitySlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 308, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(active))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(CentalitySlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(active))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void CentralityValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CentralityValueActionPerformed
        // TODO add your handling code here:
        updatedByTextField();
    }//GEN-LAST:event_CentralityValueActionPerformed

    private void updateValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateValueActionPerformed
        // TODO add your handling code here
        updatedByTextField();
}//GEN-LAST:event_updateValueActionPerformed

    private void CentalitySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CentalitySliderStateChanged
        // TODO add your handling code here:
        //System.out.println("stateChange");
        if (dontUpdate) {
            //System.out.println("don't update");
            dontUpdate = false;
            return;
        }
        val = this.CentalitySlider.getValue();
       // threshold = ((double) (val)) / 100 * (cent.getMaxValue() - cent.getMinValue());
//        if (val == 0) {
//            threshold = cent.getMinValue();
//        } else if (val == 100) {
//            threshold = cent.getMaxValue();
//        } else {
//            threshold = (((double) (val))  * (cent.getMaxValue() - cent.getMinValue()))/ 100;
//        }
        threshold=cent.getMinValue()+((double)val/100)*(cent.getMaxValue()-cent.getMinValue());
        //System.out.println("slider="+val+"threshold="+threshold);
        //nodeSelectState=new Vector();
        this.CentralityValue.setText(Double.toString(threshold));
        //System.out.println("pre notify");
        notifier.doIt(this);
    }//GEN-LAST:event_CentalitySliderStateChanged

    private void lessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lessActionPerformed
        // TODO add your handling code here:
        //System.out.println("less");
        minor = true;
        notifier.doIt(this);
        
    }//GEN-LAST:event_lessActionPerformed

    private void moreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreActionPerformed
        // TODO add your handling code here:
        //System.out.println("more");
        minor = false;
        notifier.doIt(this);
    }//GEN-LAST:event_moreActionPerformed

    private void restoreValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreValueActionPerformed
        // TODO add your handling code here:
        threshold = cent.getMeanValue();
        this.CentralityValue.setText(Double.toString(threshold));
        setSlider(threshold);
        notifier.doIt(this);
    }//GEN-LAST:event_restoreValueActionPerformed

    private void activeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activeActionPerformed
        // TODO add your handling code here:
        isActive = active.isSelected();
        setPanelEnableState(isActive);
        notifier.doIt(this);
}//GEN-LAST:event_activeActionPerformed

    private void setPanelEnableState(boolean active) {
        CentalitySlider.setEnabled(active);
        jPanel1.setEnabled(active);
        jPanel2.setEnabled(active);
        less.setEnabled(active);
        more.setEnabled(active);
        CentralityValue.setEnabled(active);
        updateValue.setEnabled(active);
        restoreValue.setEnabled(active);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider CentalitySlider;
    private javax.swing.JTextField CentralityValue;
    private javax.swing.JCheckBox active;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton less;
    private javax.swing.JRadioButton more;
    private javax.swing.ButtonGroup moreLess;
    private javax.swing.JButton restoreValue;
    private javax.swing.JButton updateValue;
    // End of variables declaration//GEN-END:variables
}
