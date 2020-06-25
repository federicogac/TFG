package NSLVOrdJava;

import java.util.ArrayList;
import jfml.FuzzyInferenceSystem;

public class RuleSystem {
    static private FuzzyInferenceSystem _f;
    static private FuzzyProblemClass _fuzzyProblem; 
    static private RuleSetClass _R;
    //static private String _dir;
    //static private String _name;
    
    public RuleSystem(FuzzyProblemClass fuzzyProblem, RuleSetClass R){
        _f = new FuzzyInferenceSystem();
        _fuzzyProblem = fuzzyProblem;
        _R = R;
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
    
    /*
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
    */
}
