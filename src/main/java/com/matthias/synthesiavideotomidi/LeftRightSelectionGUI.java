package com.matthias.synthesiavideotomidi;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Matthias
 */
public class LeftRightSelectionGUI extends javax.swing.JDialog {
    
    private List<Voice> voices = new ArrayList<>();
    public int balance;
    
    
    public LeftRightSelectionGUI(java.awt.Frame parent, boolean modal, List<Voice> voices) {
        super(parent, modal);
        initComponents();
        balance = sliderBalance.getValue();
        this.voices = voices;
        updateVoices();
    }
    
    public void updateVoices() {
        pnLH.removeAll();
        pnRH.removeAll();
        
        pnLH.setLayout(new GridLayout(5, 5));
        pnRH.setLayout(new GridLayout(5, 5));
        for (Voice voice : voices) {
            JPanel p  = new JPanel();
            p.setBackground(voice.getColor());
            JLabel firstOccurence = new JLabel();
            firstOccurence.setText(String.format("%.0f", voice.getNotes().get(0).getStartBeat()));
            p.add(firstOccurence);
            
            if(voice.getAverageNote() < balance) {
                pnLH.add(p);
            } else {
                pnRH.add(p);
            }
        }
        pnLH.revalidate();
        pnRH.revalidate();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btOK = new javax.swing.JButton();
        sliderBalance = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        pnLH = new javax.swing.JPanel();
        pnRH = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btOK.setText("OK");
        btOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOKActionPerformed(evt);
            }
        });

        sliderBalance.setValue(60);
        sliderBalance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBalanceStateChanged(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        pnLH.setBorder(javax.swing.BorderFactory.createTitledBorder("Left hand"));
        pnLH.setLayout(new java.awt.GridLayout(1, 5));
        jPanel1.add(pnLH);

        pnRH.setBorder(javax.swing.BorderFactory.createTitledBorder("Right Hand"));
        pnRH.setLayout(new java.awt.GridLayout(1, 5));
        jPanel1.add(pnRH);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliderBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btOK, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btOK)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sliderBalanceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBalanceStateChanged
        balance = sliderBalance.getValue();
        updateVoices();
    }//GEN-LAST:event_sliderBalanceStateChanged

    private void btOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOKActionPerformed
        dispose();
    }//GEN-LAST:event_btOKActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btOK;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnLH;
    private javax.swing.JPanel pnRH;
    private javax.swing.JSlider sliderBalance;
    // End of variables declaration//GEN-END:variables
}
