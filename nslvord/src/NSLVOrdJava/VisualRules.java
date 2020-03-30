/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSLVOrdJava;

import Rule.Rule;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

/**
 *
 * @author kete1
 */
public class VisualRules extends javax.swing.JFrame {

    static private FuzzyProblemClass _fuzzyProblem; 
    static private RuleSetClass _R;
    
    /**
     * Creates new form VisualRules
     */
    public VisualRules(FuzzyProblemClass fuzzyProblem, RuleSetClass R) {
        initComponents();
        _fuzzyProblem = fuzzyProblem;
        _R = R;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        _actual_num_row = new javax.swing.JTextField();
        _OK = new javax.swing.JButton();
        _info = new javax.swing.JTextPane();
        _cont = new javax.swing.JScrollPane();
        _lista = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(860, 642));

        jLabel1.setText("Num of variable per row:");

        _actual_num_row.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        _actual_num_row.setMaximumSize(new java.awt.Dimension(45, 23));
        _actual_num_row.setMinimumSize(new java.awt.Dimension(45, 23));
        _actual_num_row.setPreferredSize(new java.awt.Dimension(45, 23));
        _actual_num_row.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                _actual_num_rowKeyTyped(evt);
            }
        });

        _OK.setText("OK");
        _OK.setFocusPainted(false);
        _OK.setMaximumSize(new java.awt.Dimension(45, 23));
        _OK.setMinimumSize(new java.awt.Dimension(45, 23));
        _OK.setPreferredSize(new java.awt.Dimension(45, 23));
        _OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _OKActionPerformed(evt);
            }
        });

        _info.setText("Zoom an area of graph: click left and mark the area to right.\n" +
            "See all graph: click left and move to left.");
        _info.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        _info.setEnabled(false);
        _info.setOpaque(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(_actual_num_row, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(_OK, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(_info))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(_actual_num_row, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(_OK, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(jPanel1Layout.createSequentialGroup()
                .add(_info, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout _listaLayout = new org.jdesktop.layout.GroupLayout(_lista);
        _lista.setLayout(_listaLayout);
        _listaLayout.setHorizontalGroup(
            _listaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 858, Short.MAX_VALUE)
        );
        _listaLayout.setVerticalGroup(
            _listaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 568, Short.MAX_VALUE)
        );

        _cont.setViewportView(_lista);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(_cont)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(_cont))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void _actual_num_rowKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__actual_num_rowKeyTyped
        // TODO add your handling code here:
        char text = evt.getKeyChar();
        if(!Character.isDigit(text) || 
           text == KeyEvent.VK_BACKSPACE ||
           text == KeyEvent.VK_DELETE)
                    evt.consume();
    }//GEN-LAST:event__actual_num_rowKeyTyped

    private void _OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__OKActionPerformed
        // TODO add your handling code here:
        String text = _actual_num_row.getText();
        if(text.length() < 1 || text.length() > 2) return;
        
        int num = Integer.parseInt(text);
        if(num < 1 || num > 99) return;
        
        change_num_rules_in_row(num);
    }//GEN-LAST:event__OKActionPerformed
    
    public void SeeRules(String name){
        this.setTitle(name);
        CreateRules();
    }
    
    private void CreateRules(){
        int numRules= _R.getNumRules();
        String consequent_variable;
        String consequent_term;
        int conseqIndex,classR;
        int num_rule = 0;
        int h = 6,w = _lista.getWidth();
        
        // RULES
        for(int i = 0; i < numRules; i++) {
            classR= _R.getRules(i).getIntegerMatrix(0,0);
            
            // ANTECEDENT
            Rule rule = getAntecedent(i);
            
            // CONSEQUENT
            conseqIndex = _fuzzyProblem.consequentIndex();
            consequent_variable = _fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getName();
            consequent_term = _fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getFuzzyLinguisticTermList(_R.getRules(i).getIntegerMatrix(0,0)).getName();
            rule.consequent(consequent_variable,consequent_term);
            
            // ADD RULE
            rule.weight((float) _R.getRules(i).getRealMatrix(2+classR,4));
            rule.number(num_rule);
            rule.setLocation(6,h);
            rule.setSize(rule.getPreferredSize());
            //rule.setBackground(Color.red);
            h += rule.getHeight() + 6;
            if(w < rule.getWidth() + 6) w = rule.getWidth() + 6;
            num_rule++;
            _lista.add(rule);
        }
        
        _lista.setPreferredSize(new java.awt.Dimension(w,h));
        
        int num = 5;
        change_num_rules_in_row(num);
        _actual_num_row.setText("" + num);
    }
    
    private void change_num_rules_in_row(int num){
        int h = 6;
        for(Component comp : _lista.getComponents()){
            Rule rule = (Rule) comp;
            rule.regroup_components(num);
            rule.setSize(rule.getPreferredSize());
            rule.setLocation(6,h);
            h += rule.getHeight() + 6;
        }
        
        _lista.setPreferredSize(new java.awt.Dimension(_lista.getPreferredSize().width,h));
    }
    
    private static Rule getAntecedent(int num) {
        Rule rule = new Rule();
        double infMeasureClass, actInfMeasure;
        int numVariables,valueLabel,numLabels,tamBloc,start,conseqIndex,index;
        int type_variable;
        
        // Obtener las variables y terminos que van en la regla
        conseqIndex = _fuzzyProblem.consequentIndex();
        tamBloc= _R.getRules(num).getSizeRealBlocs(0);
        numVariables = _fuzzyProblem.getFuzzyLinguisticVariableNum();
        infMeasureClass= _R.getRules(num).getRealMatrix(0, tamBloc-1);
        start = 0;
        for (int j=0; j < numVariables-1; j++){
            numLabels = _fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
            actInfMeasure= _R.getRules(num).getRealMatrix(0, j);
            ArrayList aux = new ArrayList();
            type_variable = 0;
            if ((_R.getRules(num).binaryMatrix0AllToOne(start,numLabels) != 1) && // si todas las etiquetas están a 1 --> irrelevante
               (j != conseqIndex && actInfMeasure >= infMeasureClass)){// la medida de información de la variable es >= que la de la clase
                FuzzyLinguisticVariableClass c = _fuzzyProblem.getFuzzyLinguisticVariableList(j);
                for (int k=0; k < numLabels; k++){
                    valueLabel= _R.getRules(num).getBinaryMatrix(0,start+k);
                    if (valueLabel == 1){
                        FuzzyLinguisticTermClass b = c.getFuzzyLinguisticTermList(k);
                        if((float)b.getA() == (float)b.getB() && (float)b.getA() == (float)b.getC() && (float)b.getA() == (float)b.getD()){
                            type_variable = 1;
                        }else{
                            type_variable = 2;
                        }
                        aux.add(b);
                    }
                }
                if(type_variable == 1){
                    String[] terms = new String[aux.size()];
                    for(int i = 0; i < terms.length; i++){
                        FuzzyLinguisticTermClass term = (FuzzyLinguisticTermClass) aux.get(i);
                        terms[i] = term.getName();
                    }
                    rule.add_categoric_antecedent(c.getName(),terms);
                }else if(type_variable == 2){
                    double[][][] series = new double[aux.size()][4][2];
                    for(int i = 0; i < series.length; i++){
                        FuzzyLinguisticTermClass serie = (FuzzyLinguisticTermClass) aux.get(i);
                        series[i][0][0] = serie.getA();
                        series[i][0][1] = c.getInfRangeIsInf();
                        series[i][1][0] = serie.getB();
                        series[i][1][1] = 1;
                        series[i][2][0] = serie.getC();
                        series[i][2][1] = 1;
                        series[i][3][0] = serie.getD();
                        series[i][3][1] = c.getSupRangeIsInf();
                    }
                    rule.add_fuzzy_antecedent(c.getName(),series);
                }
            }
            start= start+numLabels;
        }
        return rule;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _OK;
    private javax.swing.JTextField _actual_num_row;
    private javax.swing.JScrollPane _cont;
    private javax.swing.JTextPane _info;
    private javax.swing.JPanel _lista;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
