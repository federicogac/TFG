/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SeeRules;

import java.awt.Color;
import java.awt.Component;

/**
 *
 * @author kete1
 */
public class Rule extends javax.swing.JPanel {

    /**
     * Creates new form Rules
     */
    public Rule() {
        initComponents();
        _num_rules = 0;
        _IF.setSize(_IF.getPreferredSize());
        _num_row = -1;
        _weight = 0;
        _consequent = new ConditionCategoric();
        _consequent.setLocation(46,0);
        _thenpanel.add(_consequent);
    }
    
    public void weight(float weight) {
        if(weight < 0) weight = 0;
        _weight = weight;
    }
    
    public void number(int num) {
        _num.setText("RULE " + num + ": (WEIGHT = " + _weight + ")");
    }
    
    public void regroup_components(int num) {
        if(_num_rules <= 1 || num == _num_row) return;
        _num_row = num;
        if(num < 1) num = 1;
        Component a = _ifpanel.getComponent(1);
        int h,w,line;
        w = a.getX() + a.getWidth() + 6;
        java.awt.Dimension tam = new java.awt.Dimension(w,a.getHeight());
        h = 0;
        for(int i = 2; i < _num_rules*2; i = i + 2){
            // Cambio de linea
            if ((i/2) % num == 0){
                w = 30;
                h = tam.height + 6;
            }
            
            // Recolocacion AND
            a = _ifpanel.getComponent(i);
            a.setLocation(w,h + 11);
            w += a.getWidth() + 6;
            
            // Recolocacion antecedente
            a = _ifpanel.getComponent(i+1);
            a.setLocation(w,h);
            w += a.getWidth() + 6;
            
            // Cambiar tamaño
            if(tam.height < a.getHeight() + h) tam.height = a.getHeight() + h;
            if(tam.width < w) tam.width = w;
        }
        
        // Aplicar cambios en los paneles
        update_panel(tam);        
    }
    
    private void update_panel(java.awt.Dimension tam){
        // Actualizar el panel con las reglas
        _ifpanel.setPreferredSize(tam);
        _ifpanel.setSize(tam);
        
        this.updateUI();
    }
    
    private void add_antecedent(Component antecedent) {
        Component ult = _ifpanel.getComponent(_ifpanel.getComponents().length - 1);
        int w = ult.getX() + ult.getWidth() + 6; // 6: el espacio entre componentes
        int h = 0;
        int lon = _ifpanel.getHeight();
        int aux_lon;
        
        // Añadir un JLabel con el texto AND entre reglas
        if(_num_rules != 0){
            javax.swing.JLabel andText = new javax.swing.JLabel();
            andText.setText("AND");
            andText.setSize(andText.getPreferredSize());
            //if((float) (_num_rules + 1) % (_num_row + 1) == 0){ // Cambio de linea
            //    w = 30;
            //    h = lon + 6; 
            //}
            andText.setLocation(w,h+11);
            w += andText.getWidth() + 6; // 16: la altura del componente JLabel
                                         // 6: el espacio entre componentes
            _ifpanel.add(andText);
        }
        
        // Añadir el antecedente
        antecedent.setSize(antecedent.getPreferredSize());
        antecedent.setLocation(w,h);
        w += antecedent.getWidth() + 6;
        _ifpanel.add(antecedent);
        
        // Aplicar cambios en los paneles
        aux_lon = antecedent.getHeight() + antecedent.getY();
        if(aux_lon > lon) lon = aux_lon;
        if(_ifpanel.getWidth() > w) w = _ifpanel.getWidth();
        update_panel(new java.awt.Dimension(w,lon));
        
        _num_rules++;
    }
    
    public void add_categoric_antecedent(String variable, String[] terms) {
        ConditionCategoric antecedent = new ConditionCategoric();
        
        antecedent.setVariable(variable);
        
        for(String term : terms) {
            antecedent.addLabel(term);
        }
        
        add_antecedent(antecedent);
    }
    
    public void add_fuzzy_antecedent(String variable, double[][][] series) {
        ConditionFuzzyLogic antecedent = new ConditionFuzzyLogic();
        
        antecedent.setVariable(variable);
        
        for(double[][] serie : series) {
            antecedent.new_serie();
            for(double[] point : serie) {
                antecedent.add(point[0],point[1]);
            }
        }
        
        antecedent.createGraph();
        
        add_antecedent(antecedent);
    }
    
    public void consequent(String variable, String term) {
        _consequent.setVariable(variable);
        _consequent.addLabel(term);
        _consequent.setSize(_consequent.getPreferredSize());
        java.awt.Dimension tam = new java.awt.Dimension(46 + _consequent.getWidth(),_consequent.getHeight());
        _thenpanel.setPreferredSize(tam);
        _thenpanel.setSize(tam);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _num = new javax.swing.JLabel();
        _ifpanel = new javax.swing.JPanel();
        _IF = new javax.swing.JLabel();
        _thenpanel = new javax.swing.JPanel();
        _THEN = new javax.swing.JLabel();

        _num.setText("RULE X: (WEIGHT = Y)");

        _IF.setText("IF");

        javax.swing.GroupLayout _ifpanelLayout = new javax.swing.GroupLayout(_ifpanel);
        _ifpanel.setLayout(_ifpanelLayout);
        _ifpanelLayout.setHorizontalGroup(
            _ifpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_ifpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(_IF)
                .addContainerGap(197, Short.MAX_VALUE))
        );
        _ifpanelLayout.setVerticalGroup(
            _ifpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_ifpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(_IF)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        _THEN.setText("THEN");

        javax.swing.GroupLayout _thenpanelLayout = new javax.swing.GroupLayout(_thenpanel);
        _thenpanel.setLayout(_thenpanelLayout);
        _thenpanelLayout.setHorizontalGroup(
            _thenpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_thenpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(_THEN)
                .addContainerGap(181, Short.MAX_VALUE))
        );
        _thenpanelLayout.setVerticalGroup(
            _thenpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_thenpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(_THEN)
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_num)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_ifpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_thenpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(_num)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_ifpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_thenpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private int _num_row;
    private int _num_rules;
    private float _weight;
    private ConditionCategoric _consequent;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel _IF;
    private javax.swing.JLabel _THEN;
    private javax.swing.JPanel _ifpanel;
    private javax.swing.JLabel _num;
    private javax.swing.JPanel _thenpanel;
    // End of variables declaration//GEN-END:variables
}
