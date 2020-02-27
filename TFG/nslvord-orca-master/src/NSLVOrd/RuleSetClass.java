package NSLVOrd;

import static NSLVOrd.NSLVOrd.fileResultDebug;
import java.io.Serializable;

/**
 * @file RuleSetClass.java
 * @brief define the set of rules
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date junio 2019
 * @note Implement the set of rules
 */
public class RuleSetClass implements Serializable{

  private int numRules;                         // número de reglas que tiene el conjunto de reglas
  private GenetCodeClass[] rules;               // codificación de cada regla que coincide con la codificación de la regla del algoritmo genético
  public double[][] confusion;
  public double CCR;
  public double SM;
  public double TPR;
  public double TNR;
  public double FPR;
  public double Kappa;
  public double AUC;
  public double MSE;
  public double RMSE;
  public double RMAE;
  public double OMAE;
  public double OMAENormalizado;
  public double MMAE;
  public double mMAE;
  public double AMAE;
  public double Spearman;
  public double Kendall;
  public double OC;
  public double beta;
  public double metric;
  public double metricMedia;
  public double Precision;
  
    // parámetros de ponderación de la característica ordinal o nominal de la función fitness
    // alpha * CCR y (1-alpha) * MAE
    public double alphaMetric=0.5;
  
  
  
  /** Default constructor */
  public RuleSetClass () {
      numRules= 0;
      rules= null;
      confusion= null;
      alphaMetric= 0.5;
  };
  
  /** constructor *
   * @param problemDefinition 
   */
  public RuleSetClass(double alphaPar){
      numRules= 0;
      rules= null;
      beta=0.25;
      confusion= null;
      alphaMetric= alphaPar;
  }
  
  /** Used for copy constructor
   * @param orig 
   */
    protected RuleSetClass(RuleSetClass orig){
      this.numRules= orig.numRules;
      this.rules= new GenetCodeClass[this.numRules];
      for (int i=0; i < this.numRules; i++){
        this.rules[i]= orig.rules[i].copy();
      }
      if (orig.confusion != null){
        int filas= orig.confusion.length;
        int columnas= orig.confusion[0].length;
        this.confusion= new double[filas][columnas];
        for (int i=0; i < filas; i++){
          for (int j=0; j < columnas; j++){
            this.confusion[i][j]= orig.confusion[i][j];
          }
        }
      }
      this.CCR= orig.CCR;
      this.SM= orig.SM;
      this.TPR= orig.TPR;
      this.TNR= orig.TNR;
      this.Kappa= orig.Kappa;
      this.AUC= orig.AUC;
      this.MSE= orig.MSE;
      this.RMSE= orig.RMSE;
      this.RMAE= orig.RMAE;
      this.OMAE= orig.OMAE;
      this.OMAENormalizado= orig.OMAENormalizado;
      this.MMAE= orig.MMAE;
      this.mMAE= orig.mMAE;
      this.AMAE= orig.AMAE;
      this.Spearman= orig.Spearman;
      this.Kendall= orig.Kendall;
      this.OC= orig.OC;
      this.beta= orig.beta;
      this.metric= orig.metric;
      this.metricMedia= orig.metricMedia;
      this.alphaMetric= orig.alphaMetric;
      this.Precision= orig.Precision;     
    }
    
    /** copy constructor
     * @return 
     */
    public RuleSetClass copy(){
      return new RuleSetClass(this);
    }

  /**
   * Update the actual object (set of rules) with the "orig"
   * Recalculate the fitness of new set of rules
   * (
   * Se actualiza el objeto actual (this) con el que se le pasa por argumento
   * )
   * @param orig 
   */
  public void updateObject(RuleSetClass orig){
      this.numRules= orig.numRules;
      this.rules= new GenetCodeClass[this.numRules];
      for (int i=0; i < this.numRules; i++){
        this.rules[i]= orig.rules[i].copy();
      }
      int filas= orig.confusion.length;
      int columnas= orig.confusion[0].length;
      this.confusion= new double[filas][columnas];
      for (int i=0; i < filas; i++){
        for (int j=0; j < columnas; j++){
          this.confusion[i][j]= orig.confusion[i][j];
        }
      }
      this.CCR= orig.CCR;
      this.SM= orig.SM;
      this.TPR= orig.TPR;
      this.TNR= orig.TNR;
      this.Kappa= orig.Kappa;
      this.AUC= orig.AUC;
      this.MSE= orig.MSE;
      this.RMSE= orig.RMSE;
      this.RMAE= orig.RMAE;
      this.OMAE= orig.OMAE;
      this.OMAENormalizado= orig.OMAENormalizado;
      this.MMAE= orig.MMAE;
      this.mMAE= orig.mMAE;
      this.AMAE= orig.AMAE;
      this.Spearman= orig.Spearman;
      this.Kendall= orig.Kendall;
      this.OC= orig.OC;
      this.beta= orig.beta;
      this.metric= orig.metric;
      this.metricMedia= orig.metricMedia;
      this.alphaMetric= orig.alphaMetric;
      this.Precision= orig.Precision;
  }

  /**
   * Set the value of numRules
   * @param newVar the new value of numRules
   */
  public void setNumRules ( int newVar ) {
    numRules = newVar;
  }

  /**
   * Get the value of numRules
   * @return the value of numRules
   */
  public int getNumRules ( ) {
    return numRules;
  }

  /**
   * Set the value of rules
   * @param newVar the new value of rules
   */
  public void setRules ( GenetCodeClass[] newVar ) {
    rules = newVar;
  }

  /**
   * Get the value of rules
   * @return the value of rules
   */
  public GenetCodeClass[] getRules ( ) {
    return rules;
  }

  /**
   * Set the value of rules
   * @param row position of new value
   * @param newVar the new value of rules
   * @return -1 if no valid position, row otherwise
   */
  public int setRules (int row, GenetCodeClass newVar ) {
//      if (row >= 0 && row < numRules){
          rules[row]= newVar;
          return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of rules
   * @param row position 
   * @return the value of rules; null if no valid position
   */
  public GenetCodeClass getRules (int row ) {
//      if (row >= 0 && row < numRules){
          return rules[row];
//      }
//      else{
//          return null;
//      }
  }

/* ......................................................................... */

  /**
   * Check if the individual "indiv" (rule) exists in the set of rules
   * (
   * Comprueba si el individuo (código genético) está en el conjunto de reglas
   * )
   * @param indiv individual that has the genet code to look for in the set of rules
   * @return 1 -> if the indiv exists in the set of rules, 0 -> if NO EXISTS in the set of rules
   */
  public int exist(IndividualClass indiv){

      int igual;
      for (int i=0; i < numRules ; i++){
        igual= 1;
        
//String auxString= DebugClass.printGenetCode(indiv);
//System.out.println(auxString);
//auxString= DebugClass.printGenetCode(this.getRules(i));
//System.out.println(auxString);

/*          
        if (indiv.getWeight() != this.getWeight(i)){
            igual=0;
        }
*/        
        for (int j=0; j < this.getRules(i).getBinaryBlocs() && igual == 1; j++){
            for (int k=0; k < this.getRules(i).getSizeBinaryBlocs(j) && igual == 1; k++){
                if (Math.abs(this.getRules(i).getBinaryMatrix(j, k) - indiv.getBinaryMatrix(j, k)) > 0){
                    igual=0;
                }
            }
        }
        for (int j=0; j < this.getRules(i).getIntegerBlocs() && igual == 1; j++){
            for (int k=0; k < this.getRules(i).getSizeIntegerBlocs(j) && igual == 1; k++){
                if (Math.abs(this.getRules(i).getIntegerMatrix(j, k) - indiv.getIntegerMatrix(j, k)) > 0){
                    igual=0;
                }
            }
        }
        for (int j=0; j < this.getRules(i).getRealBlocs() && igual == 1; j++){
            for (int k=0; k < this.getRules(i).getSizeRealBlocs(j) && igual == 1; k++){
                if (Math.abs(this.getRules(i).getRealMatrix(j, k) - indiv.getRealMatrix(j, k)) > 0.01){
                    igual=0;
                }
            }
        }
        
        if (igual==1){
            return 1;
        }
                
      }      
      
      return 0;
  }
  
  /**
   * add a rule to the set of rules
   * @param rule to add
   * @param weight associated to rule
   */
  public void addRule(GenetCodeClass rule, double weight, ExampleSetProcess E){
    GenetCodeClass[] newRules= new GenetCodeClass[numRules+1];
    double[] newWeight= new double[numRules+1];
    
   for (int i=0; i < this.numRules; i++){
      newRules[i]= this.rules[i].copy();
//      newWeight[i]= this.weight[i];
    }
    newRules[this.numRules]= rule;
    newWeight[numRules]= weight;
    this.numRules++;    
    
    this.rules= newRules;
//    this.weight= newWeight;
    
//    // calcular las métricas para modificar la base de reglas
//    String resultado= Util.calcMetrics(this, E);
//    
//    if (!resultado.equals("")){
//      System.out.println("Ha habido un error en el cálculo de las métricas");
//      System.out.println(resultado);
//      String aux="RuleSetClass.addRule \n\n"+"ERROR: calcMetrics"+ resultado;
//      DebugClass.sendMail=1;
//      DebugClass.cuerpoMail+= "\n" + aux;
//      DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//      DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
//      System.exit(-1);
//    }
    
  }
  
  /**
   * remove the rule with index "index" of set of rules
   * @param index index of rule to remove
   * @param E set of examples
   */
  public void removeRule(int index, ExampleSetProcess E, FuzzyProblemClass problem){
    GenetCodeClass[] newRules= new GenetCodeClass[numRules-1];
    double[] newWeight= new double[numRules-1];
    int newI=0;
    for (int i=0; i < this.numRules; i++){
      if (i != index){
        newRules[newI]= this.rules[i].copy();
//        newWeight[newI]= this.weight[i];
        newI++;
      }
    }    
    this.rules= newRules;
//    this.weight= newWeight;
    this.numRules--;
    
    // calcular las métricas para modificar la base de reglas
    String resultado= Util.calcMetrics(this, E, problem);
    
    if (!resultado.equals("")){
      System.out.println("Ha habido un error en el cálculo de las métricas");
      System.out.println(resultado);
      String aux="RuleSetClass.removeRule \n\n"+"ERROR: calcMetrics"+ resultado;
      DebugClass.sendMail=1;
      DebugClass.cuerpoMail+= "\n" + aux;
      DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//      DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
      System.exit(-1);
    }
    
    
  }
  

  /**
   * remove last rule to the set of rules
   * @param E set of rules
   * @use removeRule
   */
  public void removeLastRule(ExampleSetProcess E, FuzzyProblemClass problem){
    
    this.removeRule(this.numRules-1,E, problem);
  }
    
  /**
   * calculate the average of variables in set of rules
   * @return average of variables in set of rules
   */
  public double calcVarXRule(){
    double cont=0;
    
    for (int r=0; r < this.numRules; r++){
      cont+= this.getRules(r).getRelevantVariables();
    }
    if (cont== 0 || this.numRules == 0)
      return 0;
    
    return cont/this.numRules;
  }  
  
  /**
   * calculate number of rules of each class
   * @return vector with number of rules of each class
   */
  public int[] calcRulesXClass(int numClasses){
    int[] rulesXClass= new int[numClasses];
    
    for (int c=0; c < numClasses; c++){
      rulesXClass[c]= 0;
    }
        
    for (int r=0; r < this.numRules; r++){
      rulesXClass[this.getRules(r).getIntegerMatrix(0, 0)]++;
    }
    
    return rulesXClass;
  }  

  
  /**
   * Get de inference of example e with the set of rules
   * @param E set of examples
   * @param e index of actual example
   * @return index of fired rule
   */
  public int inference(ExampleSetProcess E, int e){ 
  
    int indexRule;
    double adaptAntR, pesoR, adaptBRWeight, maxAdaptBRWeight, pesoMaxAdapt;
    int varCons= E.getProblemDefinition().consequentIndex();    
    int numClases= E.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermNum();

    indexRule= -1;
//    indexRule= 0;
    pesoMaxAdapt= 0;
    maxAdaptBRWeight= 0;
     for (int r=0; r < numRules; r++){
      adaptAntR= E.calcAdaptAntNormalized(e, this.getRules(r));
      int classR= this.getRules(r).getIntegerMatrix(0,0);
      pesoR= this.getRules(r).getRealMatrix(2+classR,4);

      adaptBRWeight= adaptAntR*pesoR;
      
      if (adaptBRWeight > 0){
        if (adaptBRWeight > maxAdaptBRWeight){
          indexRule= r;
          maxAdaptBRWeight= adaptBRWeight;
          pesoMaxAdapt= pesoR;
        }
        else if (adaptBRWeight == maxAdaptBRWeight){
          if (pesoR > pesoMaxAdapt){
            indexRule = r;
            pesoMaxAdapt= pesoR;
          }
        }
      }              
    }//for (int r=0; r < R.getNumRules(); r++){      
        
    return indexRule;
  }
  
 
  /**
   * chequea el conjunto de reglas y comprueba si al eliminar una regla
   * mejora la métrica y si es así la elimina
   * @param E 
   * @return 1 -> si ha eliminado reglas
   */
  public int removeRulesForImproveMetric(ExampleSetProcess E, GeneticAlgorithmClass GA, FuzzyProblemClass problem){
    // probar a quitar reglas y ver si mejora la precisión --> 
    RuleSetClass auxRule;
    double originalMetric= this.metricMedia;
    double newMetric = 0;
    int eliminadoReglas= 0;
    
    
    int r=1;
    while ( r < this.getNumRules() -1){
      auxRule= this.copy();
      auxRule.removeRule(r,E, problem);
      newMetric= auxRule.metricMedia;
      
      if (newMetric > originalMetric){ // se puede borrar la regla "r"
String aux= "\n Regla eliminada: "+ r + " de " + this.getNumRules() + "\n\n";
System.out.println(aux);
//DebugClass.printMetrics(fileResultDebug+0, this, 1, 1);
//DebugClass.printMetrics(fileResultDebug+0, auxRule, 1, 1);
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
DebugClass.sendMail=1;
DebugClass.cuerpoMail+= "\n" + aux;

        originalMetric= newMetric;
        this.updateObject(auxRule);
        r= 1;        
        eliminadoReglas= 1;
      }
      else{
        r++;
      }                
    }
        
    return eliminadoReglas;
  }  

  public int removeRulesForImproveMetricTFG(ExampleSetProcess E, GeneticAlgorithmClass GA, FuzzyProblemClass problem){
    // probar a quitar reglas y ver si mejora la precisión --> 
    RuleSetClass auxRule;
    double originalMetric= this.metricMedia;
    double newMetric = 0;
    int eliminadoReglas= 0;
    
    
    int r=1;
    while ( r < this.getNumRules() -1){
      auxRule= this.copy();
      auxRule.removeRule(r,E, problem);
      newMetric= auxRule.metricMedia;
      
      if (newMetric > originalMetric){ // se puede borrar la regla "r"
        originalMetric= newMetric;
        this.updateObject(auxRule);
        r= 1;        
        eliminadoReglas= 1;
      }
      else{
        r++;
      }                
    }
        
    return eliminadoReglas;
  }  

}