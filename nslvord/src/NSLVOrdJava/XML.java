/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSLVOrdJava;

import java.io.File;
import java.util.ArrayList;
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
}
