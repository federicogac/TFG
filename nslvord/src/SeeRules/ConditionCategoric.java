/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SeeRules;

import java.awt.Color;

/**
 *
 * @author kete1
 */
public class ConditionCategoric extends javax.swing.JPanel {

    /**
     * Creates new form ConditionCategoric
     */
    public ConditionCategoric() {
        initComponents();
        _num_labels = 0;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _variable = new javax.swing.JLabel();
        _is = new javax.swing.JLabel();
        _label = new javax.swing.JPanel();
        _parenthesis = new javax.swing.JLabel();

        _variable.setForeground(new java.awt.Color(0, 0, 204));
        _variable.setText("NameVariable");

        _is.setText("IS");

        _label.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        _parenthesis.setText("(");
        _parenthesis.setVisible(false);
        _label.add(_parenthesis);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(_variable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(_is)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(_variable)
                        .addComponent(_is))
                    .addComponent(_label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void setVariable(String variable) {
        _variable.setText(variable);
    }
    
    public void addLabel(String label) {
        javax.swing.JLabel new_label = new javax.swing.JLabel(label);
        if(_num_labels > 1) {
            _label.remove(_label.getComponentCount() - 1);
        }
        if(_num_labels > 0) {
            _label.add(new javax.swing.JLabel("  OR  "));
            _parenthesis.setVisible(true);
        }
        new_label.setForeground(Color.BLUE);
        _label.add(new_label);
        if(_num_labels > 0) {
            _label.add(new javax.swing.JLabel(")"));
        }
        _num_labels++;
    }
    
    int _num_labels;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel _is;
    private javax.swing.JPanel _label;
    private javax.swing.JLabel _parenthesis;
    private javax.swing.JLabel _variable;
    // End of variables declaration//GEN-END:variables
}