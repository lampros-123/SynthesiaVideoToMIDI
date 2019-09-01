
package com.matthias.synthesiavideotomidi.gui;

import com.matthias.synthesiavideotomidi.bl.NoteListener;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

public class ConfirmNotelistenerBaseColors extends javax.swing.JDialog {

    private List<NoteListener> listeners;
    private List<Color> colors;
    
    private int selected = -1;
    
    public ConfirmNotelistenerBaseColors(java.awt.Frame parent, boolean modal, List<NoteListener> listeners) {
        super(parent, modal);
        initComponents();
        setSize(500, 300);
        this.listeners = listeners;
        colors = new ArrayList<>();
        for (NoteListener listener : listeners) {
            colors.add(listener.getDefCol());
        }
        
        resetButtons();
    }
    
    private void resetButtons() {
        pnGrid.removeAll();
        pnGrid.setLayout(new GridLayout(0, 12));
        for (int i = 0; i < colors.size(); i++) {
            JButton button = new JButton();
            button.setBackground(colors.get(i));
            
            final int idx = i;
            button.addActionListener((e) -> {
                if(selected < 0) {
                    selected = idx;
                    btCancel.setText("Cancel Selected");
                    if(button.getBackground().getBlue() > 127) {
                        button.setBackground(button.getBackground().darker().darker());
                    } else {
                        button.setBackground(button.getBackground().brighter().brighter());
                    }
                } else {
                    colors.set(selected, colors.get(idx));
                    selected = -1;
                    btCancel.setText("Cancel");
                    resetButtons();
                }
            });
            pnGrid.add(button);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnGrid = new javax.swing.JPanel();
        btCancel = new javax.swing.JButton();
        btOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnGrid.setLayout(new java.awt.GridLayout(1, 10, 10, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnGrid, gridBagConstraints);

        btCancel.setText("Cancel");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(btCancel, gridBagConstraints);

        btOk.setText("Ok");
        btOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(btOk, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOkActionPerformed
        for (int i = 0; i < colors.size(); i++) {
            listeners.get(i).setDefCol(colors.get(i));
        }
        this.dispose();
    }//GEN-LAST:event_btOkActionPerformed

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        if(selected < 0) {
            this.dispose();
        } else {
            selected = -1;
            btCancel.setText("Cancel");
            resetButtons();
        }
    }//GEN-LAST:event_btCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOk;
    private javax.swing.JPanel pnGrid;
    // End of variables declaration//GEN-END:variables

}
