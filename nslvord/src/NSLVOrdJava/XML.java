/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSLVOrdJava;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import jfml.compatibility.ExportPMML;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.AntecedentType;
import jfml.rule.ClauseType;
import jfml.rule.ConsequentType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.MamdaniRuleBaseType;
import jfml.term.FuzzyTerm;
import jfml.term.FuzzyTermType;

public class XML {
    static private FuzzyInferenceSystem _f;
    static private FuzzyProblemClass _fuzzyProblem; 
    static private RuleSetClass _R;
    static private String _dir;
    static private String _name;
    
    public XML(FuzzyProblemClass fuzzyProblem, RuleSetClass R){
        _f = new FuzzyInferenceSystem();
        _fuzzyProblem = fuzzyProblem;
        _R = R;
    }
    
    public void CreateFileXML(String dir, String name){
        // KNOWLEDGE BASE
        KnowledgeBase();
        
        // RULE BASE
        RuleBase();
        
        // WRITTING INTO AN XML FILE
        _dir = dir;
        _name = name;
        WriteFile();
    }
    
    private void KnowledgeBase(){
        KnowledgeBaseType kb = new KnowledgeBaseType();
	_f.setKnowledgeBase(kb);

        // FUZZY VARIABLE
        int linguisticVarNum= _fuzzyProblem.getFuzzyLinguisticVariableNum();
        int linguisticTermNum;
        FuzzyLinguisticVariableClass auxLinguisticVar;
        FuzzyLinguisticTermClass auxLinguisticTerm;
        for (int i= 0; i < linguisticVarNum-1; i++){
            auxLinguisticVar = _fuzzyProblem.getFuzzyLinguisticVariableList(i);
            FuzzyVariableType s = new FuzzyVariableType(auxLinguisticVar.getName(), (float)auxLinguisticVar.getInfRange(), (float)auxLinguisticVar.getSupRange());

            // FUZZY TERM
            linguisticTermNum= auxLinguisticVar.getFuzzyLinguisticTermNum();
            for (int j=0; j < linguisticTermNum; j++){
                auxLinguisticTerm= auxLinguisticVar.getFuzzyLinguisticTermList(j);
                FuzzyTermType st = new FuzzyTermType(auxLinguisticTerm.getName(), FuzzyTermType.TYPE_trapezoidShape,
				(new float[] {(float)auxLinguisticTerm.getA(), (float)auxLinguisticTerm.getB(), (float)auxLinguisticTerm.getC(), (float)auxLinguisticTerm.getD()}));
		s.addFuzzyTerm(st);
            }
            
            kb.addVariable(s);
        }
        
        // OUTPUT CLASS
        auxLinguisticVar = _fuzzyProblem.getFuzzyLinguisticVariableList(linguisticVarNum-1);
        FuzzyVariableType s = new FuzzyVariableType(auxLinguisticVar.getName(), (float)auxLinguisticVar.getInfRange(), (float)auxLinguisticVar.getSupRange());
	s.setType("output");

        // FUZZY TERM OUTPUT CLASS
        linguisticTermNum= auxLinguisticVar.getFuzzyLinguisticTermNum();
        for (int j=0; j < linguisticTermNum; j++){
            auxLinguisticTerm= auxLinguisticVar.getFuzzyLinguisticTermList(j);
            FuzzyTermType st = new FuzzyTermType(auxLinguisticTerm.getName(), FuzzyTermType.TYPE_singletonShape,
                    (new float[] {(float)auxLinguisticTerm.getA()}));
            s.addFuzzyTerm(st);
        }
        
        kb.addVariable(s);
    }
    
    private ConsequentType getConsequent(KnowledgeBaseType kb, int rule){
        int conseqIndex = _fuzzyProblem.consequentIndex();
        ConsequentType con = new ConsequentType();
            
        KnowledgeBaseVariable a = kb.getVariable(_fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getName());
        FuzzyTerm b = (FuzzyTerm) a.getTerm(_fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getFuzzyLinguisticTermList(_R.getRules(rule).getIntegerMatrix(0,0)).getName());
                       
        con.addThenClause(a, b);
        
        return con;
    }
    
    private AntecedentType[] getAntecedent(KnowledgeBaseType kb, int rule){
        double infMeasureClass, actInfMeasure;
        int comb,numVariables,valueLabel,numLabels,tamBloc,start,conseqIndex,index;
        ArrayList validTerm;
        AntecedentType[] ant;
        ArrayList aux;
        
        // Obtener las variables y terminos que van en la regla
        conseqIndex = _fuzzyProblem.consequentIndex();
        tamBloc= _R.getRules(rule).getSizeRealBlocs(0);
        numVariables = _fuzzyProblem.getFuzzyLinguisticVariableNum();
        infMeasureClass= _R.getRules(rule).getRealMatrix(0, tamBloc-1);
        start = 0;
        validTerm = new ArrayList();
        comb = 1;
        for (int j=0; j < numVariables-1; j++){
            numLabels = _fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
            actInfMeasure= _R.getRules(rule).getRealMatrix(0, j);
            aux = new ArrayList();
            if ((_R.getRules(rule).binaryMatrix0AllToOne(start,numLabels) != 1) && // si todas las etiquetas están a 1 --> irrelevante
               (j != conseqIndex && actInfMeasure >= infMeasureClass)){// la medida de información de la variable es >= que la de la clase
                for (int k=0; k < numLabels; k++){
                    valueLabel= _R.getRules(rule).getBinaryMatrix(0,start+k);
                    if (valueLabel == 1){
                        aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getName());
                    }
                }
                if(!aux.isEmpty()){
                    comb *= aux.size();
                    aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getName());
                    validTerm.add(aux);
                }
            }
            start= start+numLabels;
        }
        
        // Hacer las combinaciones de los terminos entre las variables
        ant = new AntecedentType[comb];
        for(int i = 0; i < comb; i++){
            ant[i] = new AntecedentType();
            start = comb;
            index = i;
            for(int j = 0; j < validTerm.size(); j++){
                aux = (ArrayList) validTerm.get(j);
                start = start/(aux.size()-1);
                KnowledgeBaseVariable a = kb.getVariable((String) aux.get(aux.size()-1));
                FuzzyTerm b = (FuzzyTerm) a.getTerm((String) aux.get(index/start));
                ant[i].addClause(new ClauseType(a,b));
                index = index - start * (index/start); 
            }
        }
        
        return ant;
    }
    
    private void RuleBase(){
        MamdaniRuleBaseType rb = new MamdaniRuleBaseType("");
        KnowledgeBaseType kb = _f.getKnowledgeBase();
        int numRules= _R.getNumRules();
        int classR;
        int rule = 0;
            
        // RULES
        for (int i = 0; i < numRules; i++){
            classR= _R.getRules(i).getIntegerMatrix(0,0);
            
            // CONSEQUENT
            ConsequentType con = getConsequent(kb,i);
            
            // ANTECEDENT
            AntecedentType[] combAnt = getAntecedent(kb,i);
            
            // ADD RULES
            for (AntecedentType ant : combAnt) {
                FuzzyRuleType r = new FuzzyRuleType("R" + rule++, "and", "MIN", (float)_R.getRules(i).getRealMatrix(2+classR,4));
                r.setAntecedent(ant);
                r.setConsequent(con);
                rb.addRule(r);
            }
        }
        
        _f.addRuleBase(rb);
    }
    
    private void WriteFile(){
	File dirXMLFiles = new File(_dir);
	if (!dirXMLFiles.exists())
            dirXMLFiles.mkdir();
		
	File XMLFile = new File(_dir + "/" + _name + ".xml");
	JFML.writeFSTtoXML(_f, XMLFile);
    }
    
    public void ExportPMML(){
        ExportPMML _PMML = new ExportPMML();
        _PMML.exportFuzzySystem(_f, _dir + "/" + _name + "PMML.xml");
    }

    public String[][][][] export_rules(){
        String[][][][] export = new String[2][][][];
        
        // KnowledgeBase
        //export[0] = Export_KnowledgeBase();
        
        // RuleBase
        //export[1] = Export_RuleBase();
        
        return export;
    }
    
    public String[] Export_KnowledgeBase(){
        ArrayList export_aux = new ArrayList();
        
        // FUZZY PROBLEM
        export_aux.add(String.valueOf(_fuzzyProblem.getConsequentIndexOriginal()));
        export_aux.add(String.valueOf(_fuzzyProblem.getShift()));
        export_aux.add(String.valueOf(_fuzzyProblem.getDirection()));
        export_aux.add(String.valueOf(_fuzzyProblem.getHomogeneousLabel()));
        export_aux.add(String.valueOf(_fuzzyProblem.getFuzzyLinguisticVariableNum()));
        
        // FUZZY VARIABLE
        for (FuzzyLinguisticVariableClass auxLinguisticVar : _fuzzyProblem.getFuzzyLinguisticVariableList()){
            export_aux.add(auxLinguisticVar.getName());
            export_aux.add(String.valueOf(auxLinguisticVar.getUnit()));
            export_aux.add(String.valueOf(auxLinguisticVar.getNumTermAutomatic()));
            export_aux.add(String.valueOf(auxLinguisticVar.getVariableType()));
            export_aux.add(String.valueOf(auxLinguisticVar.getInfRange()));
            export_aux.add(String.valueOf(auxLinguisticVar.getSupRange()));
            export_aux.add(String.valueOf(auxLinguisticVar.getInfRangeIsInf()));
            export_aux.add(String.valueOf(auxLinguisticVar.getSupRangeIsInf()));
            export_aux.add(String.valueOf(auxLinguisticVar.getFuzzyLinguisticTermNum()));
            
            // FUZZY TERM
            for (FuzzyLinguisticTermClass auxLinguisticTerm : auxLinguisticVar.getFuzzyLinguisticTermList()){
                export_aux.add(auxLinguisticTerm.getName());
                export_aux.add(String.valueOf(auxLinguisticTerm.getA()));
                export_aux.add(String.valueOf(auxLinguisticTerm.getB()));
                export_aux.add(String.valueOf(auxLinguisticTerm.getC()));
                export_aux.add(String.valueOf(auxLinguisticTerm.getD()));
                export_aux.add(String.valueOf(auxLinguisticTerm.getAbInf()));
                export_aux.add(String.valueOf(auxLinguisticTerm.getCdInf()));
            }
        }
        
        // To String vector
        String[] export = new String[export_aux.size()];
        for(int i = 0; i < export_aux.size(); i++){
            export[i] = (String) export_aux.get(i);
        }
        
        return export;
    }
    
    public String[][][] Export_KnowledgeBase_ant(){
        // FUZZY PROBLEM
        int linguisticVarNum= _fuzzyProblem.getFuzzyLinguisticVariableNum();
        String[][][] export = new String[linguisticVarNum+1][][];
        export[0] = new String[1][5];
        export[0][0][0] = String.valueOf(linguisticVarNum);
        export[0][0][1] = String.valueOf(_fuzzyProblem.getConsequentIndexOriginal());
        export[0][0][2] = String.valueOf(_fuzzyProblem.getShift());
        export[0][0][3] = String.valueOf(_fuzzyProblem.getDirection());
        export[0][0][4] = String.valueOf(_fuzzyProblem.getHomogeneousLabel());
                
        
        // FUZZY VARIABLE
        int linguisticTermNum;
        FuzzyLinguisticVariableClass auxLinguisticVar;
        FuzzyLinguisticTermClass auxLinguisticTerm;
        for (int i= 1; i <= linguisticVarNum; i++){
            auxLinguisticVar = _fuzzyProblem.getFuzzyLinguisticVariableList(i-1);
            linguisticTermNum= auxLinguisticVar.getFuzzyLinguisticTermNum();
            export[i] = new String[linguisticTermNum + 1][];
            export[i][0] = new String[9];
            export[i][0][0] = auxLinguisticVar.getName();
            export[i][0][1] = String.valueOf(auxLinguisticVar.getInfRange());
            export[i][0][2] = String.valueOf(auxLinguisticVar.getSupRange());
            export[i][0][3] = String.valueOf(auxLinguisticVar.getInfRangeIsInf());
            export[i][0][4] = String.valueOf(auxLinguisticVar.getSupRangeIsInf());
            export[i][0][5] = String.valueOf(auxLinguisticVar.getUnit());
            export[i][0][6] = String.valueOf(auxLinguisticVar.getNumTermAutomatic());
            export[i][0][7] = String.valueOf(auxLinguisticVar.getVariableType());
            export[i][0][8] = String.valueOf(auxLinguisticVar.getFuzzyLinguisticTermNum());
                    
            // FUZZY TERM
            for (int j=0; j < linguisticTermNum; j++){
                export[i][j+1] = new String[7];
                auxLinguisticTerm= auxLinguisticVar.getFuzzyLinguisticTermList(j);
                export[i][j+1][0] = auxLinguisticTerm.getName();
                export[i][j+1][1] = String.valueOf(auxLinguisticTerm.getA()); 
                export[i][j+1][2] = String.valueOf(auxLinguisticTerm.getB()); 
                export[i][j+1][3] = String.valueOf(auxLinguisticTerm.getC()); 
                export[i][j+1][4] = String.valueOf(auxLinguisticTerm.getD()); 
                export[i][j+1][5] = String.valueOf(auxLinguisticTerm.getAbInf());
                export[i][j+1][6] = String.valueOf(auxLinguisticTerm.getCdInf()); 
            }
        }
        
        return export;
    }
    
    public String[] Export_RuleBase(){
        ArrayList export_aux = new ArrayList();
        
        // RULE SET
        export_aux.add(String.valueOf(_R.getNumRules()));
        export_aux.add(String.valueOf(_R.CCR));
        export_aux.add(String.valueOf(_R.SM));
        export_aux.add(String.valueOf(_R.TPR));
        export_aux.add(String.valueOf(_R.TNR));
        export_aux.add(String.valueOf(_R.FPR));
        export_aux.add(String.valueOf(_R.Kappa));
        export_aux.add(String.valueOf(_R.AUC));
        export_aux.add(String.valueOf(_R.MSE));
        export_aux.add(String.valueOf(_R.RMSE));
        export_aux.add(String.valueOf(_R.RMAE));
        export_aux.add(String.valueOf(_R.OMAE));
        export_aux.add(String.valueOf(_R.OMAENormalizado));
        export_aux.add(String.valueOf(_R.MMAE));
        export_aux.add(String.valueOf(_R.mMAE));
        export_aux.add(String.valueOf(_R.AMAE));
        export_aux.add(String.valueOf(_R.Spearman));
        export_aux.add(String.valueOf(_R.Kendall));
        export_aux.add(String.valueOf(_R.OC));
        export_aux.add(String.valueOf(_R.beta));
        export_aux.add(String.valueOf(_R.metric));
        export_aux.add(String.valueOf(_R.metricMedia));
        export_aux.add(String.valueOf(_R.Precision));
        export_aux.add(String.valueOf(_R.alphaMetric));
        export_aux.add(String.valueOf(_R.confusion.length));
        for (double[] confusion : _R.confusion) {
            export_aux.add(String.valueOf(confusion.length));
            for (double val : confusion) {
                export_aux.add(String.valueOf(val));
            }
        }
        
        // RULE
        for(GenetCodeClass auxGenetCode : _R.getRules()){
            // Binary elements
            export_aux.add(String.valueOf(auxGenetCode.getBinaryBlocs()));
            for(int i = 0; i < auxGenetCode.getBinaryBlocs(); i++){
                export_aux.add(String.valueOf(auxGenetCode.getSizeBinaryBlocs(i)));
                for(int j = 0; j < auxGenetCode.getSizeBinaryBlocs(i); j++){
                    export_aux.add(String.valueOf(auxGenetCode.getBinaryMatrix(i,j)));
                }
            }
            
            // Integer elements
            export_aux.add(String.valueOf(auxGenetCode.getIntegerBlocs()));
            for(int i = 0; i < auxGenetCode.getIntegerBlocs(); i++){
                export_aux.add(String.valueOf(auxGenetCode.getSizeIntegerBlocs(i)));
                for(int j = 0; j < auxGenetCode.getSizeIntegerBlocs(i); j++){
                    export_aux.add(String.valueOf(auxGenetCode.getIntegerMatrix(i,j)));
                }
            }
            export_aux.add(String.valueOf(auxGenetCode.getIntegerRange().length));
            for(int i : auxGenetCode.getIntegerRange()){
                export_aux.add(String.valueOf(i));
            }
            
            // Real elements
            export_aux.add(String.valueOf(auxGenetCode.getRealBlocs()));
            for(int i = 0; i < auxGenetCode.getRealBlocs(); i++){
                export_aux.add(String.valueOf(auxGenetCode.getSizeRealBlocs(i)));
                for(int j = 0; j < auxGenetCode.getSizeRealBlocs(i); j++){
                    export_aux.add(String.valueOf(auxGenetCode.getRealMatrix(i,j)));
                }
            }
            export_aux.add(String.valueOf(auxGenetCode.getRealInfRange().length));
            for(double i : auxGenetCode.getRealInfRange()){
                export_aux.add(String.valueOf(i));
            }
            export_aux.add(String.valueOf(auxGenetCode.getRealSupRange().length));
            for(double i : auxGenetCode.getRealSupRange()){
                export_aux.add(String.valueOf(i));
            }
        }
        
        // To String vector
        String[] export = new String[export_aux.size()];
        for(int i = 0; i < export_aux.size(); i++){
            export[i] = (String) export_aux.get(i);
        }
        
        return export;
    }
    
    public String[][][][] Export_RuleBase_ant(){
        // RULE SET
        int num_rules = _R.getNumRules();
        String[][][][] export = new String[num_rules+1][][][];
        export[0] = new String[2][][];
        // Datas
        export[0][0] = new String[1][24];
        export[0][0][0][0] = String.valueOf(num_rules);
        export[0][0][0][1] = String.valueOf(_R.CCR);
        export[0][0][0][2] = String.valueOf(_R.SM);
        export[0][0][0][3] = String.valueOf(_R.TPR);
        export[0][0][0][4] = String.valueOf(_R.TNR);
        export[0][0][0][5] = String.valueOf(_R.FPR);
        export[0][0][0][6] = String.valueOf(_R.Kappa);
        export[0][0][0][7] = String.valueOf(_R.AUC);
        export[0][0][0][8] = String.valueOf(_R.MSE);
        export[0][0][0][9] = String.valueOf(_R.RMSE);
        export[0][0][0][10] = String.valueOf(_R.RMAE);
        export[0][0][0][11] = String.valueOf(_R.OMAE);
        export[0][0][0][12] = String.valueOf(_R.OMAENormalizado);
        export[0][0][0][13] = String.valueOf(_R.MMAE);
        export[0][0][0][14] = String.valueOf(_R.mMAE);
        export[0][0][0][15] = String.valueOf(_R.AMAE);
        export[0][0][0][16] = String.valueOf(_R.Spearman);
        export[0][0][0][17] = String.valueOf(_R.Kendall);
        export[0][0][0][18] = String.valueOf(_R.OC);
        export[0][0][0][19] = String.valueOf(_R.beta);
        export[0][0][0][20] = String.valueOf(_R.metric);
        export[0][0][0][21] = String.valueOf(_R.metricMedia);
        export[0][0][0][22] = String.valueOf(_R.Precision);
        export[0][0][0][23] = String.valueOf(_R.alphaMetric);
        // Confusion matrix
        export[0][1] = new String[_R.confusion.length][];
        for(int i = 0; i < _R.confusion.length; i++){
            export[0][1][i] = new String[_R.confusion[i].length];
            for(int j = 0; j < _R.confusion[i].length; j++){
                export[0][1][i][j] = String.valueOf(_R.confusion[i][j]);
            }
        }
        
        // RULE
        for(int i = 1; i <= num_rules; i++){
            int row,col;
            int[] range;
            double[] rangeR;
            GenetCodeClass auxGenetCode = _R.getRules(i-1);
            export[i] = new String[4][][];
            export[i][0] = new String[7][];
            export[i][0][0] = new String[3];
            
            // Binary elements
            row = auxGenetCode.getBinaryBlocs();
            export[i][0][0][0] = String.valueOf(row);
            export[i][0][1] = new String[row];
            export[i][1] = new String[row][];
            for(int j = 0; j < row; j++){
                col = auxGenetCode.getSizeBinaryBlocs(j);
                export[i][0][1][j] = String.valueOf(col);
                export[i][1][j] = new String[col];
                for(int k = 0; k < col; k++){
                    export[i][1][j][k] = String.valueOf(auxGenetCode.getBinaryMatrix(j,k));
                }
            }
            
            // Integer elements
            row = auxGenetCode.getIntegerBlocs();
            export[i][0][0][1] = String.valueOf(row);
            export[i][0][2] = new String[row];
            export[i][2] = new String[row][];
            for(int j = 0; j < row; j++){
                col = auxGenetCode.getSizeIntegerBlocs(j);
                export[i][0][2][j] = String.valueOf(col);
                export[i][2][j] = new String[col];
                for(int k = 0; k < col; k++){
                    export[i][2][j][k] = String.valueOf(auxGenetCode.getIntegerMatrix(j,k));
                }
            }
            range = auxGenetCode.getIntegerRange();
            export[i][0][4] = new String[range.length];
            for(int j = 0; j < range.length; j++){
                export[i][0][4][j] = String.valueOf(range[j]);
            }
            
            // Real elements
            row = auxGenetCode.getRealBlocs();
            export[i][0][0][2] = String.valueOf(row);
            export[i][0][3] = new String[row];
            export[i][3] = new String[row][];
            for(int j = 0; j < row; j++){
                col = auxGenetCode.getSizeRealBlocs(j);
                export[i][0][3][j] = String.valueOf(col);
                export[i][3][j] = new String[col];
                for(int k = 0; k < col; k++){
                    export[i][3][j][k] = String.valueOf(auxGenetCode.getRealMatrix(j,k));
                }
            }
            rangeR = auxGenetCode.getRealInfRange();
            export[i][0][5] = new String[rangeR.length];
            for(int j = 0; j < rangeR.length; j++){
                export[i][0][5][j] = String.valueOf(rangeR[j]);
            }
            rangeR = auxGenetCode.getRealSupRange();
            export[i][0][6] = new String[rangeR.length];
            for(int j = 0; j < rangeR.length; j++){
                export[i][0][6][j] = String.valueOf(rangeR[j]);
            }
        }
        
        return export;
    }
   
    public String[] Export_Rules(){
        ArrayList export_aux = new ArrayList();
        
        // RULES
        int numRules= _R.getNumRules();
        export_aux.add(String.valueOf(numRules));
        for (int i = 0; i < numRules; i++){
            int classR= _R.getRules(i).getIntegerMatrix(0,0);
            
            // DATA RULE
            export_aux.add("R" + i);
            export_aux.add(String.valueOf(_R.getRules(i).getRealMatrix(2+classR,4)));
            
            // ANTECEDENT
            ArrayList ant = Export_Antecedents(i);
            export_aux.add(String.valueOf(ant.size() + 2));
            for (Object ant1 : ant) {
                export_aux.add((String) ant1);
            }
            
            // CONSEQUENT
            int conseqIndex = _fuzzyProblem.consequentIndex();
            export_aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getName());
            export_aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getFuzzyLinguisticTermList(_R.getRules(i).getIntegerMatrix(0,0)).getName());            
        }
        
        // To String vector
        String[] export = new String[export_aux.size()];
        for(int i = 0; i < export_aux.size(); i++){
            export[i] = (String) export_aux.get(i);
        }
        
        return export;
    }
    
    public String[][][] Export_Rules_ant(){
        // RULES
        int classR;
        int numRules= _R.getNumRules();
        ArrayList rules,aux; 
        rules = new ArrayList();
        for (int i = 0; i < numRules; i++){
            // ANTECEDENT
            aux = Export_Antecedents(i);
            
            // CONSEQUENT
            classR= _R.getRules(i).getIntegerMatrix(0,0);
            int conseqIndex = _fuzzyProblem.consequentIndex();
            String[] con = new String[2];
            con[0] = _fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getName();
            con[1] = _fuzzyProblem.getFuzzyLinguisticVariableList(conseqIndex).getFuzzyLinguisticTermList(_R.getRules(i).getIntegerMatrix(0,0)).getName();
            
            // ADD RULES
            for (Object ant : aux){
                rules.add((String[][]) ant);
                rules.add(con);
                rules.add(String.valueOf(_R.getRules(i).getRealMatrix(2+classR,4)));
            }
        }
        
        // Export rules
        String[][][] export = new String[rules.size()/3][][];
        for(int j = 0; j <rules.size(); j = j + 3){
            int i = j;
            
            // Get datas
            String[][] ant = (String[][]) rules.get(i);
            String[] con = (String[]) rules.get(i+1);
            String weight = (String) rules.get(i+2);
            export[i/3] = new String[ant.length + 2][2];
            
            // Datas rule
            export[i/3][0][0] = "R" + i/3;
            export[i/3][0][1] = weight;
            
            // Antecedents
            System.arraycopy(ant,0,export[i/3],1,ant.length);
            
            // Consequent
            export[i/3][ant.length + 1] = con;
        }
        
        return export;
    }
    
    public ArrayList Export_Antecedents(int rule){
        ArrayList validTerm = new ArrayList();
        int numVariables = _fuzzyProblem.getFuzzyLinguisticVariableNum();
        int start = 0;
        int tamBloc = _R.getRules(rule).getSizeRealBlocs(0);
        int conseqIndex = _fuzzyProblem.consequentIndex();
        int numT = 0;
        double infMeasureClass = _R.getRules(rule).getRealMatrix(0, tamBloc-1);
        for (int j=0; j < numVariables-1; j++){
            ArrayList aux = new ArrayList();
            int numLabels = _fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
            double actInfMeasure = _R.getRules(rule).getRealMatrix(0, j);
            if ((_R.getRules(rule).binaryMatrix0AllToOne(start,numLabels) != 1) && // si todas las etiquetas están a 1 --> irrelevante
               (j != conseqIndex && actInfMeasure >= infMeasureClass)){// la medida de información de la variable es >= que la de la clase
                for (int k=0; k < numLabels; k++){
                    int valueLabel = _R.getRules(rule).getBinaryMatrix(0,start+k);
                    if (valueLabel == 1){
                        aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getName());
                        aux.add(String.valueOf(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getA()));
                        aux.add(String.valueOf(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getB()));
                        aux.add(String.valueOf(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getC()));
                        aux.add(String.valueOf(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getD()));
                        aux.add(String.valueOf(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getAbInf()));
                        aux.add(String.valueOf(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getCdInf()));
                    }
                }
                if(!aux.isEmpty()){
                    aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getName());
                    validTerm.add(aux);
                    numT++;
                }
            }
            start= start+numLabels;
        }
        
        ArrayList ant = new ArrayList();
        ant.add(String.valueOf(numT+1));
        for (Object next : validTerm) {
            ArrayList aux = (ArrayList) next;
            ant.add((String) aux.get(aux.size()-1));
            int numTerm = (aux.size() - 1) / 7;
            ant.add(String.valueOf(numTerm));
            for(int i = 0; i < numTerm; i++){
                int num = i * 7;
                ant.add((String) aux.get(num));
                ant.add((String) aux.get(num + 1));
                ant.add((String) aux.get(num + 2));
                ant.add((String) aux.get(num + 3));
                ant.add((String) aux.get(num + 4));
                ant.add((String) aux.get(num + 5));
                ant.add((String) aux.get(num + 6));
            }
        }
        
        return ant;
    }
    
    public ArrayList Export_Antecedents_ant(int rule){
        double infMeasureClass, actInfMeasure;
        int comb,numVariables,valueLabel,numLabels,tamBloc,start,conseqIndex,index;
        ArrayList validTerm,ant;
        ArrayList aux;
        
        // Obtener las variables y terminos que van en la regla
        conseqIndex = _fuzzyProblem.consequentIndex();
        tamBloc= _R.getRules(rule).getSizeRealBlocs(0);
        numVariables = _fuzzyProblem.getFuzzyLinguisticVariableNum();
        infMeasureClass= _R.getRules(rule).getRealMatrix(0, tamBloc-1);
        start = 0;
        validTerm = new ArrayList();
        comb = 1;
        for (int j=0; j < numVariables-1; j++){
            numLabels = _fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
            actInfMeasure= _R.getRules(rule).getRealMatrix(0, j);
            aux = new ArrayList();
            if ((_R.getRules(rule).binaryMatrix0AllToOne(start,numLabels) != 1) && // si todas las etiquetas están a 1 --> irrelevante
               (j != conseqIndex && actInfMeasure >= infMeasureClass)){// la medida de información de la variable es >= que la de la clase
                for (int k=0; k < numLabels; k++){
                    valueLabel= _R.getRules(rule).getBinaryMatrix(0,start+k);
                    if (valueLabel == 1){
                        aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).getName());
                    }
                }
                if(!aux.isEmpty()){
                    comb *= aux.size();
                    aux.add(_fuzzyProblem.getFuzzyLinguisticVariableList(j).getName());
                    validTerm.add(aux);
                }
            }
            start= start+numLabels;
        }
        
        // Hacer las combinaciones de los terminos entre las variables
        ant = new ArrayList();
        for(int i = 0; i < comb; i++){
            start = comb;
            index = i;
            String[][] rule_ant = new String[validTerm.size()][2];
            for(int j = 0; j < validTerm.size(); j++){
                aux = (ArrayList) validTerm.get(j);
                start = start/(aux.size()-1);
                rule_ant[j][0] = (String) aux.get(aux.size()-1);
                rule_ant[j][1] = (String) aux.get(index/start);
                index = index - start * (index/start); 
            }
            ant.add(rule_ant);
        }
        
        return ant;
    }
}
