package NSLVOrdJava;

import static NSLVOrdJava.NSLVOrdJava.fileResultDebug;
import java.util.Random;

/**
 * @file GeneticAlgorithmClass.java
 * @brief define the genetic algorithm to generate new rules
 * clase que se encargará de la ejecución del algoritmo genético para la 
 * generación de nuevas reglas. Para ello necesita el conjunto de ejemplos, 
 * la población y el conjunto de reglas
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implements the necesary elements for genetic algorithm
 */
public class GeneticAlgorithmClass {

  private PopulationClass P;
  // variables "globales" necesarias para la obtención de las nuevas reglas
  private int irrelevantVariables; //número de variables irrelevantes (aquellas que intervienen todas las etiquetas->se pueden eliminar de la regla)
  private int relevantVariables; //número de variables relevantes (aquellas que NO intervienen todas las etiquetas)
  private int stableVariables; //aquellas variables relevantes que tienen asignación comprensible (sólo hay una secuencia de 1 continua)
    
  /** Default constructor
   * @param P Population
   */
  public GeneticAlgorithmClass (String[] parametersKeel, ExampleSetProcess E) { 
      
      this.P = new PopulationClass(parametersKeel, E);
      this.irrelevantVariables= -1;
      this.relevantVariables= -1;
      this.stableVariables= -1;
  };
  
  /** Used for copy constructor
   * @param orig 
   */
    protected GeneticAlgorithmClass(GeneticAlgorithmClass orig){
      this.P= orig.P.copy();
      this.irrelevantVariables= orig.irrelevantVariables;
      this.relevantVariables= orig.relevantVariables;
      this.stableVariables= orig.stableVariables;

/* PUEDE HACER FALTA MÁS ADELANTE....
      int numRules= orig.R.getNumRules();
      if (numRules != 0){
        this.originalWeight= new double[numRules];
        System.arraycopy(orig.originalWeight, 0, this.originalWeight, 0, orig.originalWeight.length);
        this.basicThis= new int[numRules];
        System.arraycopy(orig.basicThis, 0, this.basicThis, 0, orig.basicThis.length);
        this.mistakeThis= new int[numRules];
        System.arraycopy(orig.mistakeThis, 0, this.mistakeThis, 0, orig.mistakeThis.length);
        this.basicFor= new int[numRules];
        System.arraycopy(orig.basicFor, 0, this.basicFor, 0, orig.basicFor.length);
        this.removableRulesList= new int[numRules];
        System.arraycopy(orig.removableRulesList, 0, this.removableRulesList, 0, orig.removableRulesList.length);
        
      }
*/
    }
    
    /** copy constructor
     * @return 
     */
    public GeneticAlgorithmClass copy(){
      return new GeneticAlgorithmClass(this);
    }
    
  /**
   * Set the value of P
   * @param newVar the new value of P
   */
  public void setP ( PopulationClass newVar ) {
    P = newVar;
  }

  /**
   * Get the value of P
   * @return the value of P
   */
  public PopulationClass getP ( ) {
    return P;
  }

/* ......................................................................... */

  
  /**
   * Select subjects in random way from examples to generate the individuals of population
   * (
   * Calcula la mejor etiqueta para cada sujeto y cada variable (si hay 
   * ejemplos para la clase que corresponde con el índice del sujeto)
   * Hay una relación entre el sujeto "i" y la clase "i%numClases" de forma
   * que si no hay ejemplos de la clase "c" los sujetos de la clase "i%numClases=c" 
   * tendrán todas las etiquetas de todas las variables a "-1"
   * )
   * @param randomNum 
   * @return matrix of [individuals][variables] wich contains the best labels of each variable for each individuals
   * @use selectRamdomIndiv
   */
  public int[][] selectSubjects(Random randomNum, ExampleSetProcess E){
      
      int numIndividuals= P.getNumIndividuals();
      int numVariablesAntecedentes= E.getProblemDefinition().numAntecedentVariables();
      int indexConsec= E.getProblemDefinition().consequentIndex();
      int numClasses= E.getProblemDefinition().numLinguisticTermOfConsequent();      
      int numExamples= E.getNumExamples();
      int[][] subjects=  new int[numIndividuals][numVariablesAntecedentes];
      int[] numExamNotCoveredXClass= E.getNumExamNotCoveredXClass();
      int actualSubject;
      
      for (int i=0; i < numIndividuals; i++){
          if (numExamNotCoveredXClass[i%numClasses] == 0){    //no hay ejemplos de la clase "i%numClasses"
              for (int j=0; j < numVariablesAntecedentes; j++){
                  subjects[i][j]= -1;
              }
          }
          else{ // hay ejemplos de la clase "i%numClasses" -> coger uno aleatorio que no esté cubierto y tenga adaptación
              actualSubject= Util.selectRamdomElementIndex(numExamples, -1, randomNum);
              int covered= E.getCovered(actualSubject);
              double adaptExVarClass= E.getAdaptExVarLab(actualSubject,indexConsec,i%numClasses);
              while (covered == 1 || adaptExVarClass == 0){ 
                // está cubierto o no tiene adaptación con la clase del consecuente
                actualSubject= (actualSubject + 1) % numExamples;                  
                covered= E.getCovered(actualSubject);
                adaptExVarClass= E.getAdaptExVarLab(actualSubject,indexConsec,i%numClasses);
              }
              for (int j=0; j < numVariablesAntecedentes; j++){
                  subjects[i][j]= E.getBetterLabel(actualSubject,j);
              }
          }
      }      
    return subjects;
  }


    /**
     * Initialize the population 
     * @param randomNum
     * @use selectSubjects
     */
    public void initPopulation(Random randomNum, ExampleSetProcess E, 
                                double[][] costMatrix){
    int[][] subjects;

    // seleccionar la mejor etiqueta para la creación de los individuos
    subjects= this.selectSubjects(randomNum, E); 
    // inicializar la población
    P.initPopulation(subjects, E, randomNum, costMatrix);
    
    // "eliminar" la parte de variable (la medida de información) y quedarte sólo con la de valor
//    Util.modifyValueLevelPopulationUsingInformationMeasures(P, E);
    
  }
  
  /**
   * Calc the relevant, irrelevant and stable variables of one individuals of population
   * Store the results in properties of object of the individual
   * (
   * cálculo de las variables Relevantes de un individuo de la población
   * Almacena el cálculo en las variables "globales" del objeto ya que se
   * utilizarán en los siguientes métodos
   * )
   * @param populationIndex index of population to consider
   * @return number of relevant variables
   */
  public int calcRelevantVariables(int populationIndex, ExampleSetProcess E){
    int numLabels, start;
    
    
    int numVar= E.getProblemDefinition().numAntecedentVariables();
    this.irrelevantVariables= numVar - P.getIndividuals(populationIndex).getRelevantVariables();
    this.stableVariables= P.getIndividuals(populationIndex).getStableVariables(E.getProblemDefinition());    
    this.relevantVariables= numVar - this.irrelevantVariables;
    
    return this.relevantVariables;
  }
    
  /**
   * calc weight of individual for fitness
   * modify the reals subpopulation with adaptations of examples to antecedents, consecuents
   * (
   * calcula los valores necesarios para el cálculo de fitness
   * Valores que se guardarán en la parte real del individuo
   * Valores como por ejemplo: 
   * - adaptaciones de los ejemplos al antecedente
   * - adaptaciones de los ejemplos al consecuente
   * - adaptaciones de los ejemplos al NO-Consecuente
   * - valores para lambda+rules y lambda-rules
   * - 
   * )
   * @param populationIndex index of population to consider
   */
  public void calcWeightIndividualForFitness(int populationIndex ,ExampleSetProcess E){

    int numExamples= E.getNumExamples();
    int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
    double[] adaptConsClase= new double[numClases];
    double adaptAnt=0;
    double[] adaptCons= new double[numClases];
    double[] adaptNoConsDist= new double[numClases];
    double[] nMasEpond= new double[numClases];
    double[] nMenosEpond= new double[numClases];
    double[] posExamples= new double[numClases];
    double[] negExamples= new double[numClases];
    double nAdaptAnt=0;
    double weight=0;
    
//DebugClass.printPopulationElement("",P,1,0,E,populationIndex);

    for (int j=0; j < numClases; j++){
      nMasEpond[j]= 0;
      nMenosEpond[j]=0;      
      posExamples[j]=0;
      negExamples[j]=0;
    }

    for (int e=0; e < numExamples; e++){     
//DebugClass.printExampleElement("../results.txt0",E,1,1,e);
      adaptAnt= E.calcAdaptAntNormalized(e,P.getIndividuals(populationIndex));
      
      for (int j=0; j < numClases; j++){
        adaptConsClase[j]= P.getAdaptCons(j,e);
        adaptNoConsDist[j]= P.getAdaptNoConsPond(j,e);
        nMasEpond[j]+= (adaptAnt*adaptConsClase[j]);      
        nMenosEpond[j]+= (adaptAnt*adaptNoConsDist[j]);        
        posExamples[j]+= adaptAnt*adaptConsClase[j];
        negExamples[j]+= adaptAnt*(1.0 - adaptConsClase[j]);        
      }
    
      // cálculo del número de ejemplos que describen el concepto B
      if (adaptAnt > 0){
        nAdaptAnt++;
      }

      P.getIndividuals(populationIndex).setRealMatrix(1,e,adaptAnt);      
    }//for (int e=0; e < numExamples; e++){
    
    P.getIndividuals(populationIndex).setRealMatrix(1,numExamples,nAdaptAnt);      
    
    for (int j=0; j < numClases; j++){
//      weight= (nMasEpond[j] + 1)/ (double) (nMasEpond[j] + nMenosEpond[j] + 1);
      if ((nMasEpond[j] + nMenosEpond[j]) == 0){
        weight= 0;
      }
      else{
        weight= (nMasEpond[j])/ (double) (nMasEpond[j] + nMenosEpond[j]);
      }

      P.getIndividuals(populationIndex).setRealMatrix(2+j,4,weight);
      P.getIndividuals(populationIndex).setRealMatrix(2+j,7,nMasEpond[j]);  
      P.getIndividuals(populationIndex).setRealMatrix(2+j,8,nMenosEpond[j]);  
      P.getIndividuals(populationIndex).setRealMatrix(2+j,9,posExamples[j]);  
      P.getIndividuals(populationIndex).setRealMatrix(2+j,10,negExamples[j]);  

    }

//DebugClass.printPopulationElement("../results.txt0",P,0,1,E,populationIndex);
  }
  
  // devuelve el valor máximo del vector sin tener en cuenta el índice j
  public double[] maxValue(double[] vector, int index){
    
    int longitud= vector.length;
    double[] resultado= new double[2];
    double max= - Double.MAX_VALUE;
    int maxIndex= -1;
    
    for (int i=0; i < longitud; i++){
      if (i != index && vector[i] > max){
        max= vector[i];
        maxIndex= i;
      }
    }
    
    resultado[0]= maxIndex;
    resultado[1]= max;
    return resultado;
  }
  
  /**
   * calc fitness of individual (use the adaptations and weight stored in the individual)
   * (
   * calcula el fitness con los resultados de las funciones anteriores
   * que están almacenados en las matrices de reales de los individuos
   * )
   * @param populationIndex 
   */
  public void calcFitness(int populationIndex, ExampleSetProcess E, RuleSetClass R){
    int numFitness= P.getIndividuals(populationIndex).getNumFitness();
    int numExamples= E.getNumExamples();
    int varCons= E.getProblemDefinition().consequentIndex();    
    int numClases= E.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermNum();
    double[] fitness= new double[numFitness];
    double[] lambdaPos= new double[numExamples]; // labmdaPos == bestAdaptBR; 
    double[] lambdaNeg= new double[numExamples]; // lambdaNeg == bestAdaptBR cuando class(e) != B
    int[] indexLambdaPos= new int[numExamples];
    int[] indexLambdaNeg= new int[numExamples];
    double[] negWeight= new double[numExamples];
    double[] posWeight= new double[numExamples];
    double adaptAnt=0;
    double[] adaptConsClase= new double[numClases];
    double[] adaptNoCons= new double[numClases];
    double[] adaptNoConsDist= new double[numClases];
    double[] peso= new double[numClases];
    double adaptBWeight=0, adaptNoBWeight=0;
    double complet=0, consist=0, consXcomp=0, weight=0, simplic=0, comprens=0, criterioOrdinal=0;
    double[] nMasE= new double[numClases];
    double[] nMenosE= new double[numClases];
    int numVar, numIrrVar, numStabVar, cubierto;
    int classR=0, classEx=0, clasePeorRegla=0, claseMejorRegla=0;
    double[] nBi= new double[numClases];
    double difMetrica=0;
    
    double[] aciertos= new double[numClases];
    double[] fallosPonderados= new double[numClases];
    double CCR=0, OMAE=0, OMAENormalizado=0, Metrica=0;
    int[] posExamples= new int[numClases];
    int[] negExamples= new int[numClases];    
    
    for (int j=0; j < numClases; j++){
      nMasE[j]= 0;
      nMenosE[j]= 0;
      aciertos[j]= 0;
      fallosPonderados[j]=0;
      posExamples[j]=0;
      negExamples[j]=0;      
    }
    
    this.calcRelevantVariables(populationIndex, E);
    numIrrVar= this.irrelevantVariables;
    numStabVar= this.stableVariables;
    numVar= this.irrelevantVariables + this.relevantVariables;
    
    classR= P.getIndividuals(populationIndex).getIntegerMatrix(0,0);
    
    if (R.getNumRules() == 0){
      for (int e=0; e < numExamples; e++){
        adaptAnt= P.getIndividuals(populationIndex).getRealMatrix(1,e);

        for (int j=0; j < numClases; j++){
          adaptConsClase[j]= P.getAdaptCons(j,e);
          adaptNoConsDist[j]= P.getAdaptNoConsPond(j,e);
          adaptNoCons[j]= 1.0 - adaptConsClase[j];
          peso[j]= P.getIndividuals(populationIndex).getRealMatrix(2+j,4);
          nBi[j]= P.getAdaptCons(j,numExamples);

          nMasE[j]+= adaptAnt * adaptConsClase[j] * peso[j];
          nMenosE[j]+= adaptAnt * (adaptNoConsDist[j]) * peso[j];
//          nMenosE[j]+= adaptAnt * (adaptNoCons[j]) * peso[j];          
          aciertos[j]+= adaptConsClase[j];
          fallosPonderados[j]+= adaptNoConsDist[j];
          posExamples[j]+= adaptConsClase[j];
          negExamples[j]+= 1.0 - adaptConsClase[j];          
        }
      }
    }
    else{//if (R.getNumRules() == 0){
      for (int e=0; e < numExamples; e++){

        adaptAnt= P.getIndividuals(populationIndex).getRealMatrix(1,e);
        cubierto= E.getCovered(e);
        classEx= E.getBetterLabel(e, varCons);

        lambdaPos[e]= E.getLambdaPos(e);
        lambdaNeg[e]= E.getLambdaNeg(e);
        indexLambdaPos[e]= E.getIndexLambdaPos(e);
        indexLambdaNeg[e]= E.getIndexLambdaNeg(e);
        posWeight[e]= E.getPosWeight(e);
        negWeight[e]= E.getNegWeight(e);

        for (int j=0; j < numClases; j++){
          adaptConsClase[j]= P.getAdaptCons(j,e);
          adaptNoConsDist[j]= P.getAdaptNoConsPond(j,e);
          adaptNoCons[j]= 1.0 - adaptConsClase[j];
          peso[j]= P.getIndividuals(populationIndex).getRealMatrix(2+j,4);
          nBi[j]= P.getAdaptCons(j, numExamples);

          adaptBWeight= adaptAnt * adaptConsClase[j] * peso[j];
          adaptNoBWeight= adaptAnt * (adaptNoCons[j]) * peso[j];
          weight= peso[j];
          if (cubierto == 0){ // no estaba cubierto
            if (((adaptBWeight > lambdaNeg[e]) || 
               ((adaptBWeight == lambdaNeg[e]) && (weight > negWeight[e])))){ // con esta regla se cubre positivamente
  //            ((adaptClaseWeight[classR] > lambdaNeg[e]) ||
  //             ((adaptClaseWeight[classR] == lambdaNeg[e]) && (peso > negWeight[e])))){

              nMasE[j]+= adaptBWeight;
              aciertos[j]++;
              //si antes no estaba cubierto, lo cubría la regla por defecto, por lo que hay que modificar los fallosPonderados para el OMAE
              if (indexLambdaNeg[e] == -1){
                clasePeorRegla= Util.classDefaultRule;
              }
              else{
                clasePeorRegla= R.getRules(indexLambdaNeg[e]).getIntegerMatrix(0,0);
              }
              fallosPonderados[j]-= Math.abs(clasePeorRegla - j);
              posExamples[j]++;              
            }
//            else if (adaptAnt > 0 && weight > negWeight[e]){// con esta regla se modifica su clasificación
            else if ((adaptNoBWeight > lambdaNeg[e]) || 
                    ((adaptNoBWeight == lambdaNeg[e]) && (weight > negWeight[e]))){ // con esta regla se modifica su clasificación
              nMenosE[j]+= adaptAnt * (adaptNoConsDist[j]) * peso[j];
//              nMenosE[j]+= adaptNoBWeight;
              if (indexLambdaNeg[e] == -1){
                clasePeorRegla= Util.classDefaultRule;
              }
              else{
                clasePeorRegla= R.getRules(indexLambdaNeg[e]).getIntegerMatrix(0,0);
              }
              fallosPonderados[j]-= Math.abs(clasePeorRegla - classEx);            
              fallosPonderados[j]+= Math.abs(classEx - j);
              negExamples[j]++;
            }
          }
          else{ // cubierto == 1 // estaba cubierto
            if ((adaptNoBWeight > lambdaPos[e]) ||
               ((adaptNoBWeight == lambdaPos[e]) && (weight > posWeight[e]))){ // con esta regla lo cubre negativamente (lo desclasifica)
  //            ((adaptClaseWeight[classEx] > lambdaPos[e]) ||
  //             ((adaptClaseWeight[classEx] == lambdaPos[e]) && (peso > posWeight[e])))){

              nMenosE[j]+= adaptAnt * (adaptNoConsDist[j]) * peso[j];
//              nMenosE[j]+= adaptNoBWeight;
              claseMejorRegla= R.getRules(indexLambdaPos[e]).getIntegerMatrix(0,0);
              fallosPonderados[j]+= Math.abs(claseMejorRegla - j);
              aciertos[j]--;
              negExamples[j]++;              
            }
          }          
        }//for (int j=0; j < numClases; j++){
      }//for (int e=0; e < numExamples; e++){      
    } // else //if (R.getNumRules() == 0){
        
    simplic= numIrrVar / (double) numVar;
    comprens= (numStabVar + 1) / (double) (numVar+1); // p=numVar : SE VA A CONSIDERAR QUE TODOS LOS DOMINIOS SON ORDENADOS

    for (int j=0; j < numClases; j++){
      if (nMasE[j] == 0){
  //      consist= -1000; // para controlar cuando no hay nMasE
        consist= 0; // para controlar cuando no hay nMasE
  //        consist= -numExamples;
      }
      else{
        consist= (nMasE[j] - nMenosE[j]) / (double) nMasE[j];
      }
      if (nBi[j] == 0){
        complet= -numExamples;
        consXcomp= -numExamples;          
      }
      else{
        complet= (nMasE[j]) / (double) nBi[j];
        consXcomp= (nMasE[j] - nMenosE[j]) / (double) nBi[j];
      }
      // si la consXcomp es negativa -> hay mas ejemplos que desclasifica que ejemplos que clasifica, pero tenemos que ir buscando
      // que tenga el menor error en la "desclasificacion" para lo que los ponemos todos al mismo valor y así se
      // mira la siguiente componente (myMetric)
//      if (consXcomp < 0){
//        consXcomp= -numExamples;  
//      }
//      if (nMasE == 0){
//        consXcomp= -numExamples;
//      }
//
//  // esto es para ponerlo como fitness 2    
//      if (consXcomp <= 0){
//          consXcomp= -numExamples;
//      }

      aciertos[j]/= (double) numExamples;
      fallosPonderados[j]/= (double) numExamples;
      
      CCR= R.CCR + aciertos[j];
      OMAE= fallosPonderados[j] + R.OMAE;
      OMAENormalizado= OMAE / ((double) numClases-1);
      Metrica = (CCR + (1-OMAENormalizado))/2.0;
      
      double CCRaux= (nMasE[j] / (double) numExamples);
      double OMAEaux= (1 - (nMenosE[j] / ((double)numExamples * (numClases - 1)) ));
      
      criterioOrdinal= (R.alphaMetric * CCRaux) + ((1-R.alphaMetric) * OMAEaux);
      double criterioOrdinalAux= (R.alphaMetric * (nMasE[j] / (double) numExamples)) + 
              ((1-R.alphaMetric) * (1 - (nMenosE[j] / ((double)numExamples * (numClases - 1)) )));
//      System.out.println("J: " + j + "; CCR: " + CCR + "; CCRaux: " + CCRaux + "; OMAENormalizado: " + OMAENormalizado + 
//              "; OMAEaux: " + OMAEaux);
//      System.out.println("   metrica: " + Metrica + 
//              "; criterioOrdinal: " + criterioOrdinal + "; criterioOrdinalAux: " +
//              criterioOrdinalAux + "\n");
              
//// para comprobación
//RuleSetClass aux= this.getR().copy();
//ExampleSetProcess auxE= this.getE().copy();
//IndividualClass auxI= P.getIndividuals(populationIndex).copy();
//auxI.setIntegerMatrix(0,0,j);
//aux.addRule(auxI, 0, auxE);
//Util.calcMetrics(aux, auxE);
//
//if (Math.abs(aux.CCR-CCR) > 0.0001 || Math.abs(aux.OMAE - OMAE) > 0.0001){
//String auxString=("\n\n\nj:" + j + " - CCR: " + CCR + "; OMAE: " + OMAE +" :: ");
//auxString+=(" - aux.CCR: " + (aux.CCR) + "; aux.OMAE: " + (aux.OMAE) +"\n");
//auxString+=(" - aciertos: " + (aciertos[j]*numExamples) + "; fallosPonderados: " + (fallosPonderados[j]*numExamples) +"\n");
//System.out.print(auxString);
//DebugClass.writeResFile(fileResultDebug+0, auxString);
//DebugClass.printResumeRuleSet(fileResultDebug+0 ,R,1,1,E);
//DebugClass.printPopulationElementFitness(DebugClass.fileResultDebug+0,P,1,1,E,0);
//DebugClass.printResumeExampleSet(DebugClass.fileResultDebug+0, E, 1, 1, 0, E.getNumExamples());
//DebugClass.printMetrics(DebugClass.fileResultDebug+0, R, 1, 1);
//DebugClass.printResumeRuleSet(fileResultDebug+0 ,aux,1,1,E);
//DebugClass.printMetrics(DebugClass.fileResultDebug+0, aux, 1, 1);
//auxE.calcCovered(aux);
//DebugClass.printMetrics(DebugClass.fileResultDebug+0, aux, 1, 1);
//DebugClass.printResumeExampleSet(DebugClass.fileResultDebug+0, auxE, 1, 1, 0, auxE.getNumExamples());
//DebugClass.printPopulationElement(DebugClass.fileResultDebug+0,P,1,1,E,populationIndex);
//System.out.println(j);
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,auxString);
//DebugClass.sendMail=1;
//DebugClass.cuerpoMail+= "\n" + auxString;            
////      for (int e=0; e < numExamples; e++){
////
////        adaptAnt= P.getIndividuals(populationIndex).getRealMatrix(1,e);
////        cubierto= E.getCovered(e);
////        classEx= E.getBetterLabel(e, varCons);
////
////          adaptConsClase[j]= P.getIndividuals(populationIndex).getRealMatrix(j+2,e);
////          adaptNoConsDist[j]= P.getIndividuals(populationIndex).getRealMatrix((2*numClases)+j+2,e);
////          adaptNoCons[j]= 1.0 - adaptConsClase[j];
////          peso[j]= P.getIndividuals(populationIndex).getRealMatrix((3*numClases)+2+j,4);
////          nBi[j]= P.getIndividuals(populationIndex).getRealMatrix(j+2, numExamples);
////
////          adaptBWeight= adaptAnt * adaptConsClase[j] * peso[j];
////          adaptNoBWeight= adaptAnt * (adaptNoCons[j]) * peso[j];
////          weight= peso[j];
////          if (cubierto == 0){ // no estaba cubierto
////            if (((adaptBWeight > lambdaNeg[e]) || 
////               ((adaptBWeight == lambdaNeg[e]) && (weight > negWeight[e])))){ // con esta regla se cubre positivamente
////  //            ((adaptClaseWeight[classR] > lambdaNeg[e]) ||
////  //             ((adaptClaseWeight[classR] == lambdaNeg[e]) && (peso > negWeight[e])))){
////
////              nMasE[j]+= adaptBWeight;
////              aciertos[j]++;
////              //si antes no estaba cubierto, lo cubría la regla por defecto, por lo que hay que modificar los fallosPonderados para el OMAE
////              clasePeorRegla= R.getRules(indexLambdaNeg[e]).getIntegerMatrix(0,0);
////              fallosPonderados[j]-= Math.abs(clasePeorRegla - j);
////            }
//////            else if (adaptAnt > 0 && weight > negWeight[e]){// con esta regla se modifica su clasificación
////            else if ((adaptNoBWeight > lambdaNeg[e]) || 
////                    ((adaptNoBWeight == lambdaNeg[e]) && (weight > negWeight[e]))){ // con esta regla se modifica su clasificación
////              nMenosE[j]+= adaptAnt * (adaptNoConsDist[j]) * peso[j];
//////              nMenosE[j]+= adaptNoBWeight;
////              clasePeorRegla= R.getRules(indexLambdaNeg[e]).getIntegerMatrix(0,0);
////              fallosPonderados[j]-= Math.abs(clasePeorRegla - classEx);            
////              fallosPonderados[j]+= Math.abs(classEx - j);
////            }
////          }
////          else{ // cubierto == 1 // estaba cubierto
////            if ((adaptNoBWeight > lambdaPos[e]) ||
////               ((adaptNoBWeight == lambdaPos[e]) && (weight > posWeight[e]))){ // con esta regla lo cubre negativamente (lo desclasifica)
////  //            ((adaptClaseWeight[classEx] > lambdaPos[e]) ||
////  //             ((adaptClaseWeight[classEx] == lambdaPos[e]) && (peso > posWeight[e])))){
////
////              nMenosE[j]+= adaptAnt * (adaptNoConsDist[j]) * peso[j];
//////              nMenosE[j]+= adaptNoBWeight;
////              claseMejorRegla= R.getRules(indexLambdaPos[e]).getIntegerMatrix(0,0);
////              fallosPonderados[j]+= Math.abs(claseMejorRegla - j);
////              aciertos[j]--;
////            }
////          }          
////      }//for (int e=0; e < numExamples; e++){      
//}
//// FIN - para comprobación

//Metrica=R.metricMedia;      
//      P.getIndividuals(populationIndex).setRealMatrix(2+j,0,Metrica - R.metricMedia);
      P.getIndividuals(populationIndex).setRealMatrix(2+j,0,criterioOrdinal);
      P.getIndividuals(populationIndex).setRealMatrix(2+j,1,consXcomp);      
      P.getIndividuals(populationIndex).setRealMatrix(2+j,2,simplic);      
      P.getIndividuals(populationIndex).setRealMatrix(2+j,3,comprens);      
      P.getIndividuals(populationIndex).setRealMatrix(2+j,5,complet);      
      P.getIndividuals(populationIndex).setRealMatrix(2+j,6,consist);  
      P.getIndividuals(populationIndex).setRealMatrix(2+j,11,posExamples[j]);  

//      P.getIndividuals(populationIndex).setRealMatrix(2+j,0,Metrica - R.metricMedia);
//      P.getIndividuals(populationIndex).setRealMatrix(2+j,1,consXcomp);      
//      P.getIndividuals(populationIndex).setRealMatrix(2+j,3,simplic);      
//      P.getIndividuals(populationIndex).setRealMatrix(2+j,2,comprens);      
//      P.getIndividuals(populationIndex).setRealMatrix(2+j,5,complet);      
//      P.getIndividuals(populationIndex).setRealMatrix(2+j,6,consist);  

    }//for (int j=0; j < numClases; j++){

    int auxClassR= P.getBetterClassWithFitness(populationIndex, E);
    if (auxClassR != classR){
//System.out.println("classR: " + classR + "; betterClass: " + auxClassR);
//DebugClass.printPopulationElementFitness(fileResultDebug+0,P,1,1,E,populationIndex);
      classR= auxClassR;
      P.getIndividuals(populationIndex).setIntegerMatrix(0,0,classR);      
//P.getBetterClassWithFitness(populationIndex, E);
    }

    
    // volver a asignar al individuo la clase original ... OJO, más tarde se le pondrá la clase mejor...
    P.getIndividuals(populationIndex).setIntegerMatrix(0,0, classR);
    difMetrica= P.getIndividuals(populationIndex).getRealMatrix(2+classR,0);
    consXcomp= P.getIndividuals(populationIndex).getRealMatrix(2+classR,1);
    simplic= P.getIndividuals(populationIndex).getRealMatrix(2+classR,2);
    comprens= P.getIndividuals(populationIndex).getRealMatrix(2+classR,3);
    weight= P.getIndividuals(populationIndex).getRealMatrix(2+classR,4);
    complet= P.getIndividuals(populationIndex).getRealMatrix(2+classR,5);
    consist= P.getIndividuals(populationIndex).getRealMatrix(2+classR,6);

//    difMetrica= P.getIndividuals(populationIndex).getRealMatrix(2+classR,0);
//    consXcomp= P.getIndividuals(populationIndex).getRealMatrix(2+classR,1);
//    simplic= P.getIndividuals(populationIndex).getRealMatrix(2+classR,3);
//    comprens= P.getIndividuals(populationIndex).getRealMatrix(2+classR,2);
//    weight= P.getIndividuals(populationIndex).getRealMatrix(2+classR,4);
//    complet= P.getIndividuals(populationIndex).getRealMatrix(2+classR,5);
//    consist= P.getIndividuals(populationIndex).getRealMatrix(2+classR,6);
    
    int[] numExNotCoveredXClass= E.getNumExamNotCoveredXClass();
    if (numExNotCoveredXClass[classR] > 0 
        && (R.getNumRules() == 0 || 
            R.getNumRules() > 0 && difMetrica >= 0)){

      fitness[0]= difMetrica;
      fitness[1]= consXcomp; 
      fitness[2]= simplic;
      fitness[3]= comprens;
      fitness[4]= weight;
      fitness[5]= complet;
      fitness[6]= consist;

//      fitness[0]= difMetrica;
//      fitness[1]= consXcomp; 
//      fitness[3]= simplic;
//      fitness[2]= comprens;
//      fitness[4]= weight;
//      fitness[5]= complet;
//      fitness[6]= consist;
    }
    else{
      fitness[0]= fitness[1]= fitness[2]= fitness[3]= fitness[4]= fitness[5]= fitness[6]= -2*numExamples;      
    }

    P.getIndividuals(populationIndex).setFitness(fitness);    
  }  

  
  /**
   * Calc neighbours of distance 1 that we have to consider form indBegin to indEnd
   * They are considered neighbour in spite of the information measure are differents
   * (
   * obtener los individuos vecinos de distancia 1 por los que tenemos que pasar
   * para llegar del individuo indMinHamming al populationIndex
   * )
   * @param indBegin indMinHamming
   * @param indEnd populationIndex
   * @param distance
   * @return vector of individuals to consider in the path from indBegin to indEnd
   */
  public IndividualClass[] calcNeighbours(int indBegin, int indEnd, 
          int distance, ExampleSetProcess E){
    IndividualClass[] vecinos= new IndividualClass[distance+1];
    int indVec=0;
    
    int numBinaryElements= P.getIndividuals(indBegin).getSizeBinaryBlocs(0);
    int[] ele1, ele2;
    double[] inforMeasure1, inforMeasure2;
    int k=0, numLabels=0, numVar=0;
    
    ele1= P.getIndividuals(indBegin).getBinarySubMatrix(0).clone();
    inforMeasure1= P.getIndividuals(indBegin).getRealSubMatrix(0).clone();
    ele2= P.getIndividuals(indEnd).getBinarySubMatrix(0).clone();    
    inforMeasure2= P.getIndividuals(indEnd).getRealSubMatrix(0);
    numVar= E.getProblemDefinition().numAntecedentVariables();

//    // Si las medidas de información no coinciden 
//    // -> conforme se van realizando las mutaciones puede dar algo incorrecto
//    // POR AHORA SI NO COINCIDEN NO LO UTILIZAMOS... 
//    // OTRA FORMA ES NO TENER EN CUENTA LAS MEDIDAS DE INFORMACIÓN -- POR HACER... POR AQUIII
//    for (int v=0; v < numVar+1; v++){
//      if (inforMeasure1[v] != inforMeasure2[v]){
//       return null; 
//      }        
//    }    
    
    // actualizar las matrices binarias con la codificación del individuo en 
    // función de la medida de información
    for (int v=0; v < numVar; v++){
      numLabels=  E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
      if (inforMeasure1[v] < inforMeasure1[numVar]){
        for (int l=0; l < numLabels; l++){
          ele1[k+l]= 1;
        }
      }
      if (inforMeasure2[v] < inforMeasure2[numVar]){
        for (int l=0; l < numLabels; l++){
          ele2[k+l]= 1;
        }
      }
      k= k+numLabels;      
    }

    // realizar la comparación para la distance de hamming y se guardan los vecinos
    vecinos[indVec]= P.getIndividuals(indBegin).copy();
//    // se guarda el umbral menor para que posteriormente se tenga en cuenta el mayor número de variables
//    if (inforMeasure1[numVar] > inforMeasure2[numVar]){
//      vecinos[indVec].setRealMatrix(0,numVar,inforMeasure2[numVar]);
//    }
//    else{
//      vecinos[indVec].setRealMatrix(0,numVar,inforMeasure1[numVar]);
//    }
    indVec++;
    k=0;
    for (int v=0; v < numVar; v++){
      numLabels=  E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
      // primero cambiamos los 0 por 1 -> para que no tengamos una variable como 0000, sino que tengamos variables como 1111
      for (int l=0; l < numLabels; l++){
        if (ele1[k+l] != ele2[k+l] && ele1[k+l] == 0){
//          if ((inforMeasure1[v] >= inforMeasure1[numVar] && inforMeasure2[v] < inforMeasure2[numVar])  // v interviene en indiv1 y no en indiv2
//            || (inforMeasure1[v] < inforMeasure1[numVar] && inforMeasure2[v] >= inforMeasure2[numVar])){ // v interviene en indiv2 y no en indiv1
//            // la variable ha cambiado pero en un individuo se tiene en cuenta y en otro no -> no se puede hacer el cálculo por vecinos
//            return null;
//          }
          vecinos[indVec]= vecinos[indVec-1].copy();
          vecinos[indVec].setBinaryMatrix(0, k+l, ele2[k+l]);
          // de esta forma se indicará cuál el índice a partir del que se tiene que mirar
          // que corresponde con la variable que ha cambiado respecto al vecino anterior
          vecinos[indVec].setRealMatrix(vecinos[indVec].getRealBlocs()-1,0, v); // variable modificada
          vecinos[indVec].setRealMatrix(vecinos[indVec].getRealBlocs()-1,1, k); // indice donde comienza la variable modificada
          indVec++;
        }
      }
      // ahora cambiamos los 1 por 0
      for (int l=0; l < numLabels; l++){
        if (ele1[k+l] != ele2[k+l] && ele1[k+l] == 1){
//          if ((inforMeasure1[v] >= inforMeasure1[numVar] && inforMeasure2[v] < inforMeasure2[numVar])  // v interviene en indiv1 y no en indiv2
//            || (inforMeasure1[v] < inforMeasure1[numVar] && inforMeasure2[v] >= inforMeasure2[numVar])){ // v interviene en indiv2 y no en indiv1
//            // la variable ha cambiado pero en un individuo se tiene en cuenta y en otro no -> no se puede hacer el cálculo por vecinos
//            return null;
//          }
          vecinos[indVec]= vecinos[indVec-1].copy();
          vecinos[indVec].setBinaryMatrix(0, k+l, ele2[k+l]);
          // de esta forma se indicará cuál el índice a partir del que se tiene que mirar
          // que corresponde con la variable que ha cambiado respecto al vecino anterior
          vecinos[indVec].setRealMatrix(vecinos[indVec].getRealBlocs()-1,0, v); // variable modificada
          vecinos[indVec].setRealMatrix(vecinos[indVec].getRealBlocs()-1,1, k); // indice donde comienza la variable modificada
          indVec++;
        }
      }
      k= k+numLabels;            
    }
    
    return vecinos;
  }
  
  /**
   * Calcula los valores fitness para cada individuo de la población.
   * Para ello calcula las variables irrelevantes de los individuos de la población,
   * calcula los pesos originales de las reglas
   * y actualiza las variables que indican si la regla "i" es válida con la nueva ejecucion del AG
   * para finalmente actualizar los valores fitness de cada individuo de la población
   * @param reduceRules
   * @param discriminated  
   */
  public void calcFitnessPopulationUseIndividualBefore(ExampleSetProcess E, RuleSetClass R){
    int numIndividuals= P.getNumIndividuals();
    int numExamples= E.getNumExamples();
    int numFitness= P.getIndividuals(0).getNumFitness();
    double[] fitness= new double[numFitness];
    int[] numExamNotCoveredXClass= E.getNumExamNotCoveredXClass();
    int classOfIndiv;
    String auxString="";
    
    double auxDouble;
    
    int[] distHamming; // 0-> índice y 1-> distancia
    
    // prepara los fitness para el caso de que no sea necesario realizar los cálculos 
    for (int i=0; i < numFitness; i++){
      fitness[i]= -2*numExamples;
    }
        
    for (int i=0; i < numIndividuals; i++){ 
      if (P.getIndividuals(i).getModified() == 1){ //si el individuo se ha modificado -> recalcular los fitness
//        classOfIndiv= P.getIndividuals(i).getIntegerMatrix(0,0);
//        if (numExamNotCoveredXClass[classOfIndiv] > 0){ // hay ejemplos de la clase del individuo -> sigo con los cálculos
//          int relevantVariables= calcRelevantVariables(i);
//if (relevantVariables == 0){
//  System.out.println(i);
//}          
//          if (relevantVariables > 0){ // hay variables relevantes -> sigo con los cálculos
            
            // restaurar las etiquetas binarias y modificar en el indice 1 para considerar solo nivel de valor
//            P.getIndividuals(i).setBinarySubMatrix(1, P.getIndividuals(i).getBinarySubMatrix(0).clone());
//            P.modifyValueLevelIndivUsingInformationMeasures(i,E);
            
            // calcular la distancia de hamming entre este individuo y todos 
            // los anteriores con el mismo consecuente, en lo que a 
            // subpoblación binaria (antecedentes) se refiere
//            distHamming= P.calcMinDistHammingInValueLevel(i,E);  // 0-> índice y 1-> distancia
//distHamming[0]=-1;            
//            if (distHamming[0] == -1){
              Util.timeInitCalcAdapt= System.currentTimeMillis();              
              calcWeightIndividualForFitness(i,E);
              auxDouble= Double.parseDouble(Util.timeCalcAdapt.get(Util.timeCalcAdapt.size()-1).toString());
              Util.timeCalcAdapt.set(Util.timeCalcAdapt.size()-1, auxDouble + (System.currentTimeMillis() - Util.timeInitCalcAdapt));              

              Util.timeInitCalcFitness= System.currentTimeMillis();              
              calcFitness(i,E,R); 
              auxDouble= Double.parseDouble(Util.timeCalcFitness.get(Util.timeCalcFitness.size()-1).toString());
              Util.timeCalcFitness.set(Util.timeCalcFitness.size()-1, auxDouble + (System.currentTimeMillis() - Util.timeInitCalcAdapt));              
//            }
//            else{
//              IndividualClass[] vecinos;
//              vecinos= Util.calcNeighboursInValueLevel(P.getIndividuals(distHamming[0]), P.getIndividuals(i),
//                      distHamming[1],E);
////              for (int v=0; v < vecinos.length; v++){
////                Util.modifyValueLevelIndivUsingInformationMeasures(vecinos[v],E);                
////              }
//              Util.calcAdaptAntWeightIndivForBinaryNeighbourInitial(P.getIndividuals(distHamming[0]), vecinos[0],E);
//              for (int v=1; v < vecinos.length; v++){
//                Util.calcAdaptAntWeightIndivForBinaryNeighbour(vecinos[v-1], vecinos[v],E);
//              }
//
//              // aquí habría que calcular los fitness y quedarnos con el mejor......
//              for (int v=0; v < vecinos.length; v++){
//                Util.calcFitnessNeighbour(E, vecinos[v], R);
////DebugClass.printMetrics("../results.txt0", R, 1, 1);
//              }
              
//              // al llevar las medidas de informacion al nivel de valor puede ser que el binario haya cambiado
//              // la parte binaria del último vecino debe ser la del final
//              vecinos[vecinos.length-1].setBinarySubMatrix(0, P.getIndividuals(i).getBinarySubMatrix(0).clone());
// check que está bien hecho
//for (int v=0; v < vecinos.length; v++){
//  Util.modifyValueLevelIndivUsingInformationMeasures(vecinos[v],E);                
//}
//calcWeightIndividualForFitness(i);
//calcFitness(i);  
//if (Util.equalIndiv(P.getIndividuals(i), vecinos[vecinos.length-1]) == 0){
//  System.out.println("Los Fitness calculados por vecinos y apelo SON DISTINTOS");
//  DebugClass.printPopulationElement("../results.txt0", P, 0, 1, E, distHamming[0]);
//  DebugClass.printPopulationElement("../results.txt0", P, 0, 1, E, i);
//  for (int kk=0; kk < vecinos.length; kk++){
//    DebugClass.printNeighbourElement("../results.txt0",vecinos,0,1,E,kk);
//  }  
//  String aux="Los Fitness calculados por vecinos y apelo SON DISTINTOS";
//  DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//  DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
//  System.exit(-1);      
//}
// FIN - check que está bien hecho

              // ordenar los vecinos y quedarte con el mejor
//              Util.sort(vecinos);
//
////              P.setIndividuals(i, vecinos[vecinos.length-1].copy());   
//              P.setIndividuals(i, vecinos[0].copy());                          
//
//            }// else - if (distHamming[0] == -1){
//          } // if (relevantVariables > 0){
//          else{//if (relevantVariables > 0){ // no hay variables relevantes -> la regla no tiene sentido
//            P.getIndividuals(i).setFitness(fitness.clone());  
//          }
//        } //if (numExamXClass[classOfIndiv] > 0){ 
//        else{ // no hay ejemplos para de la clase del individuo -> no puedo comprobar la clasificación de la regla
//          P.getIndividuals(i).setFitness(fitness.clone());
//        }
        P.getIndividuals(i).setModified(0);
      }//if (P.getIndividuals(i).getModified() == 1){
    }//for (int i=0; i < numIndividuals; i++){
  }

  /**
   * calculate fitness of population
   * @param cuda 0: don't use cuda lenguage, 1: use cuda lenguage with nvidia peripherals
   */
  public void calcFitnessPopulation(int cuda, ExampleSetProcess E, RuleSetClass R){
    int numIndividuals= P.getNumIndividuals();
    int numExamples= E.getNumExamples();
    int numFitness= P.getIndividuals(0).getNumFitness();
    double[] fitness= new double[numFitness];
    int[] numExamNotCoveredXClass= E.getNumExamNotCoveredXClass();
    int classOfIndiv;
    String auxString="";
    
    int[] distHamming; // 0-> índice y 1-> distancia
    
    // prepara los fitness para el caso de que no sea necesario realizar los cálculos 
    for (int i=0; i < numFitness; i++){
      fitness[i]= -2*numExamples;
    }
    
    for (int i=0; i < numIndividuals; i++){ 
      if (P.getIndividuals(i).getModified() == 1){ //si el individuo se ha modificado -> recalcular los fitness
        classOfIndiv= P.getIndividuals(i).getIntegerMatrix(0,0);
        if (numExamNotCoveredXClass[classOfIndiv] > 0){ // hay ejemplos de la clase del individuo -> sigo con los cálculos
          int relevantVariables= calcRelevantVariables(i,E);
          if (relevantVariables > 0){ // hay variables relevantes -> sigo con los cálculos
            
              calcWeightIndividualForFitness(i,E);
              calcFitness(i,E,R); 
          } // if (relevantVariables > 0){
          else{//if (relevantVariables > 0){ // no hay variables relevantes -> la regla no tiene sentido
            P.getIndividuals(i).setFitness(fitness.clone());  
          }
        } //if (numExamXClass[classOfIndiv] > 0){ 
        else{ // no hay ejemplos para de la clase del individuo -> no puedo comprobar la clasificación de la regla
          P.getIndividuals(i).setFitness(fitness.clone());
        }
        P.getIndividuals(i).setModified(0);
      }//if (P.getIndividuals(i).getModified() == 1){
    }//for (int i=0; i < numIndividuals; i++){
  }

  /**
   * Check if fitness of individuals are equal or not
   * @param indiv1 
   * @param indiv2
   * @return 1 -> fitness have been modified; 0 -> otherwise
   */
  public int fitnessDifferent(IndividualClass indiv1, IndividualClass indiv2){
    int numFitness= indiv1.getNumFitness();
    double[] fitness1= indiv1.getFitness();
    double[] fitness2= indiv2.getFitness();
    
    for (int i= 0; i < numFitness; i++){
      if (fitness1[i] != fitness2[i]){
        return 1;
      }
    }
    return 0;
  }

  /**
   * look for a new rule using Genetic Algorithm
   * (
   * Calcula una nueva regla para el conjunto de ejemplos que pasa por argumento
   * La nueva regla la guarda en el vector de reglas y actualiza su estado
   * ·· Realiza la ejecución del algoritmo genético que obtiene nuevas reglas (antiguo GA)
   * )
   * @param randomNum semilla para el número aleatorio
   * @param cuda 0: don't use cuda lenguage, 1: use cuda lenguage with nvidia peripherals
   * @return 1 if found new rules, 0 otherwise
   */
    public int findNewRule(Random randomNum, int cuda, ExampleSetProcess E, RuleSetClass R){
      
      int numRules= R.getNumRules();
      int numIndividuals= P.getNumIndividuals();
      int noChangeIter=0, maxIter= this.getP().getMaxIterGenetico();
      int elementIndex;
      IndividualClass bestIndividuals;
      int[] resultadoCruce;
      int[][] resultadoMutacion;
      
      double weight;
      
      Double auxDouble;
      int auxInt;
      
String auxString="";

      // inicialmente todos están modificados para que todos sean considerados en la búsqueda 
      for (int i=0; i < numIndividuals; i++){ 
          P.getIndividuals(i).setModified(1);
      }            

      Util.timeCalcAdapt.add((double)0);
      Util.timeCalcFitness.add((double)0);
      Util.timeGeneticIn.add((double)0);
      Util.numIterGeneticIn.add((int)0);
      Util.timeInitGeneticIn= System.currentTimeMillis();
      calcFitnessPopulationUseIndividualBefore(E,R);
      auxInt= Integer.parseInt(Util.numIterGeneticIn.get(Util.numIterGeneticIn.size()-1).toString());
      Util.numIterGeneticIn.set(Util.numIterGeneticIn.size()-1, auxInt+1);
      auxDouble= Double.parseDouble(Util.timeGeneticIn.get(Util.timeGeneticIn.size()-1).toString());
      Util.timeGeneticIn.set(Util.timeGeneticIn.size()-1, auxDouble + (System.currentTimeMillis() - Util.timeInitGeneticIn));

      P.sort();
      bestIndividuals= P.getIndividuals(0);      
      
      while (noChangeIter <= maxIter){
        if ((numRules > 1) &&
           ((P.getIndividuals(0).getFitness(0) <= 0 && Util.Probability(0.75,randomNum) == 1) || 
            (Util.Probability(0.01,randomNum) == 1))){
           
          // modificar (sustituir) los dos últimos individuos de la población
          // para a partir de la mutación realizar la inserción de la regla
          elementIndex= Util.selectRamdomElementIndex(numRules, -1, randomNum);
          for (int i=numIndividuals-2; i < numIndividuals; i++){ 
            //se insertan en la población las reglas de forma aleatoria en los dos últimos individuos
            GenetCodeClass aux= new GenetCodeClass(); // se crea un nuevo código genetico para tener una copia y no un puntero
            aux= R.getRules(elementIndex).copy();
            P.getIndividuals(i).setBinaryMatrix(aux.getBinaryMatrix());
            P.getIndividuals(i).setIntegerMatrix(aux.getIntegerMatrix());
            P.getIndividuals(i).setRealMatrix(aux.getRealMatrix());
            // buscamos de forma aleatoria una nueva regla para insertar en la población
            elementIndex= Util.selectRamdomElementIndex(numRules, elementIndex, randomNum);
          }
        }
        else{//if ((numRules > 1) &&
          P.stationaryLogicalCrossover((noChangeIter/(double)maxIter), randomNum);
//          resultadoCruce= P.stationaryLogicalCrossoverForNeighbour((noChangeIter/(double)maxIter), randomNum, E);
          // resultadoCruce=[indiv1Index,indiv2Index,cross1Bin,cross2Bin,cross1Real,cross2Real,check1Binary, check2Binary]
//          calcFitnessNeighbourCrossover(resultadoCruce);
          
        }//else{//if ((numRules > 1) &&
        P.stationaryUniformMutation(randomNum);   
//        IndividualClass penultimo= P.getIndividuals(numIndividuals-2).copy();
//        IndividualClass ultimo= P.getIndividuals(numIndividuals-1).copy();
//        resultadoMutacion= P.stationaryUniformMutationForNeighbour(randomNum,E);           
//        // resultadoMutacion[0]=[modificacionPenultimoBin,modificacionPenultimoInt, modificacionPenultimoReal, check1Bin]
//        // resultadoMutacion[1]=[modificacionUltimoBin,modificacionUltimoInt, modificacionUltimoReal, check2Bin]
//        calcFitnessNeighbourMut(resultadoMutacion, penultimo, ultimo);
        
        // comprobar que no hay variables 000
        P.checkAndChangeBinaryMatrix0NoAllZero(numIndividuals-2,E, randomNum);
        P.checkAndChangeBinaryMatrix0NoAllZero(numIndividuals-1,E, randomNum);

        Util.timeInitGeneticIn= System.currentTimeMillis();
        calcFitnessPopulationUseIndividualBefore(E,R);
        auxInt= Integer.parseInt(Util.numIterGeneticIn.get(Util.numIterGeneticIn.size()-1).toString());
        Util.numIterGeneticIn.set(Util.numIterGeneticIn.size()-1, auxInt+1);
        auxDouble= Double.parseDouble(Util.timeGeneticIn.get(Util.timeGeneticIn.size()-1).toString());
        Util.timeGeneticIn.set(Util.timeGeneticIn.size()-1, auxDouble + (System.currentTimeMillis() - Util.timeInitGeneticIn));

        P.sort4L(E);
        
        int fitnessModified= fitnessDifferent(P.getIndividuals(0), bestIndividuals);
        if (fitnessModified == 0){
          noChangeIter++;
          System.out.print(".");
          System.out.flush();
        }
        else{
          auxString="\nIter: " + noChangeIter;
//          System.out.println(auxString);
          System.out.flush();
          //DebugClass.writeResFile(auxString);
//          DebugClass.printFitnessIndividualPopulation("",0,this.P,1,0,this.E);                                
//        DebugClass.printResumePopulation(fileResultDebug+0,P,1,1,E,1);          
          noChangeIter= 0;
          bestIndividuals= P.getIndividuals(0);   
        }
        
      }//while (noChangeIter <= maxIter){

      auxString="\nRule Obtained in iter: " + noChangeIter;
      System.out.println(auxString);
      System.out.flush();
//      DebugClass.printPopulationElementFitness(fileResultDebug+0,P,1,0,E,0);
//      DebugClass.printFitnessIndividualPopulation("",0,this.P,1,0,this.E);                                

      
//Esta parte es para probar a generalizar las variables, uniendo las etiquetas que podemos      
      if (generalizeVariables(numIndividuals,E,R) == 1){ //se ha mejorado
        String aux= "\nHA GENERALIZADO LA REGLA APRENDIDA\n";
//        DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//        DebugClass.printResumePopulation(fileResultDebug+0,P,1,1,E,1);          
        System.out.println(aux);
      }
      
      
      
// estos casos hay que comprobarlos...      
      // hay que comprobar qué hacer si no hay variables relevantes
      // no hay que comprobarlo, si no hay variables relevantes -> metrica y consXcomp == 0
//      int relevantVariables= P.modifyValueLevelIndivUsingInformationMeasures(0, E);
//      if (relevantVariables == 0){ // no hay variables irrelevantes --> "para él solo" ya que no hay mejora de metrica ni consXcomp
//        String aux= "\nNo hay variables relevantes en la regla "+R.getNumRules()+" \n" +
//                " difMetrica: " + P.getIndividuals(0).getFitness(0) +
//                "; consXcomp: " + P.getIndividuals(0).getFitness(1) + "\n";
//        DebugClass.sendMail=1;
//        DebugClass.cuerpoMail+= "\n" + aux;
//        DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//      }
      // si existe es porque no ha conseguido encontrar nada mejor
//      int exist= R.exist(P.getIndividuals(0));
//      if (exist == 1){ // si la variable ya existe --> "para él solo" ya que no hay mejora de metrica ni consXcomp
//        String aux= "\nLa regla ya existe en la base de reglas "+R.getNumRules()+" \n";
//        DebugClass.sendMail=1;
//        DebugClass.cuerpoMail+= "\n" + aux;
////        DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//      }
// FIN - estos casos hay que comprobarlos...      
      // comprobar para añadir nueva regla          
      double consXcomp= P.getIndividuals(0).getFitness(1);
      double difMetrica= P.getIndividuals(0).getFitness(0);
      
      if (consXcomp > 0 || difMetrica > 0){ // si aporta algo la regla lo comprobamos

        // nueva comprobación para intentar que no haya sobreaprendizaje y además
        // - tarde menos en terminar
        // - tenga menos reglas que cubren muy pocos ejemplos
        // un ejemplo claro de su comportamiento lo vamos a ver en "marketing-0" que 
        // aprende 1295 reglas, hay 9744 ejemplos y ahy reglas que cubren menos de 2 ejemplos,
        // a partir de la regla 120 cubren unos 4-8 ejemplos 
        //(se pone como límite el 0.1% de ejemplos cubiertos
        
        int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
        int numExamples= E.getNumExamples();
        int classR= P.getIndividuals(0).getIntegerMatrix(0,0);
        int exampCovered= (int) P.getIndividuals(0).getRealMatrix(2+classR,11); //valor n+eCalculadoParaCovered
        if (exampCovered > (int) (0.1 * (numExamples/ 100.0))){

          weight= P.getIndividuals(0).getRealMatrix(2+classR, 4);
          R.addRule(P.getIndividuals(0).getGenetCodeObject(), weight, E);
          
          // VISUALIZAR LA REGLA AÑADIDA
//          DebugClass.printResumePopulation("", P, 1, 0, E, 1);
          
          return 1;
        }
        else{
          String aux= "\nThis rule "+ R.getNumRules() + " cover less than 0.1% ("+exampCovered+") of examples ("+numExamples+") \n";
          DebugClass.sendMail=1;
          DebugClass.cuerpoMail+= "\n" + aux;
//          DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
          System.out.println(aux);
//          DebugClass.printFitnessIndividualPopulation(DebugClass.fileResultDebug+0,0,this.P,1,1,this.E);                                
          
          return 0;
        }
        }
      else{ //sino, seguimos buscando...(no se cuanto más...) o paramos... como mejor veamos...
//            return 1;
// hay que ver si modificamos algún parámetro del genético y lo dejamos ejecutar algo más....        
        return 0;
      }      
    }

    public int findNewRuleTFG(Random randomNum, int cuda, ExampleSetProcess E, RuleSetClass R){
      
      int numRules= R.getNumRules();
      int numIndividuals= P.getNumIndividuals();
      int noChangeIter=0, maxIter= this.getP().getMaxIterGenetico();
      int elementIndex;
      IndividualClass bestIndividuals;
      int[] resultadoCruce;
      int[][] resultadoMutacion;
      
      double weight;
      
      Double auxDouble;
      int auxInt;
      
      // inicialmente todos están modificados para que todos sean considerados en la búsqueda 
      for (int i=0; i < numIndividuals; i++){ 
          P.getIndividuals(i).setModified(1);
      }            

      Util.timeCalcAdapt.add((double)0);
      Util.timeCalcFitness.add((double)0);
      Util.timeGeneticIn.add((double)0);
      Util.numIterGeneticIn.add((int)0);
      Util.timeInitGeneticIn= System.currentTimeMillis();
      calcFitnessPopulationUseIndividualBefore(E,R);
      auxInt= Integer.parseInt(Util.numIterGeneticIn.get(Util.numIterGeneticIn.size()-1).toString());
      Util.numIterGeneticIn.set(Util.numIterGeneticIn.size()-1, auxInt+1);
      auxDouble= Double.parseDouble(Util.timeGeneticIn.get(Util.timeGeneticIn.size()-1).toString());
      Util.timeGeneticIn.set(Util.timeGeneticIn.size()-1, auxDouble + (System.currentTimeMillis() - Util.timeInitGeneticIn));

      P.sort();
      bestIndividuals= P.getIndividuals(0);      
      
      while (noChangeIter <= maxIter){
        if ((numRules > 1) &&
           ((P.getIndividuals(0).getFitness(0) <= 0 && Util.Probability(0.75,randomNum) == 1) || 
            (Util.Probability(0.01,randomNum) == 1))){
           
          // modificar (sustituir) los dos últimos individuos de la población
          // para a partir de la mutación realizar la inserción de la regla
          elementIndex= Util.selectRamdomElementIndex(numRules, -1, randomNum);
          for (int i=numIndividuals-2; i < numIndividuals; i++){ 
            //se insertan en la población las reglas de forma aleatoria en los dos últimos individuos
            GenetCodeClass aux= new GenetCodeClass(); // se crea un nuevo código genetico para tener una copia y no un puntero
            aux= R.getRules(elementIndex).copy();
            P.getIndividuals(i).setBinaryMatrix(aux.getBinaryMatrix());
            P.getIndividuals(i).setIntegerMatrix(aux.getIntegerMatrix());
            P.getIndividuals(i).setRealMatrix(aux.getRealMatrix());
            // buscamos de forma aleatoria una nueva regla para insertar en la población
            elementIndex= Util.selectRamdomElementIndex(numRules, elementIndex, randomNum);
          }
        }
        else{//if ((numRules > 1) &&
          P.stationaryLogicalCrossover((noChangeIter/(double)maxIter), randomNum);
//          resultadoCruce= P.stationaryLogicalCrossoverForNeighbour((noChangeIter/(double)maxIter), randomNum, E);
          // resultadoCruce=[indiv1Index,indiv2Index,cross1Bin,cross2Bin,cross1Real,cross2Real,check1Binary, check2Binary]
//          calcFitnessNeighbourCrossover(resultadoCruce);
          
        }//else{//if ((numRules > 1) &&
        P.stationaryUniformMutation(randomNum);   
//        IndividualClass penultimo= P.getIndividuals(numIndividuals-2).copy();
//        IndividualClass ultimo= P.getIndividuals(numIndividuals-1).copy();
//        resultadoMutacion= P.stationaryUniformMutationForNeighbour(randomNum,E);           
//        // resultadoMutacion[0]=[modificacionPenultimoBin,modificacionPenultimoInt, modificacionPenultimoReal, check1Bin]
//        // resultadoMutacion[1]=[modificacionUltimoBin,modificacionUltimoInt, modificacionUltimoReal, check2Bin]
//        calcFitnessNeighbourMut(resultadoMutacion, penultimo, ultimo);
        
        // comprobar que no hay variables 000
        P.checkAndChangeBinaryMatrix0NoAllZero(numIndividuals-2,E, randomNum);
        P.checkAndChangeBinaryMatrix0NoAllZero(numIndividuals-1,E, randomNum);

        Util.timeInitGeneticIn= System.currentTimeMillis();
        calcFitnessPopulationUseIndividualBefore(E,R);
        auxInt= Integer.parseInt(Util.numIterGeneticIn.get(Util.numIterGeneticIn.size()-1).toString());
        Util.numIterGeneticIn.set(Util.numIterGeneticIn.size()-1, auxInt+1);
        auxDouble= Double.parseDouble(Util.timeGeneticIn.get(Util.timeGeneticIn.size()-1).toString());
        Util.timeGeneticIn.set(Util.timeGeneticIn.size()-1, auxDouble + (System.currentTimeMillis() - Util.timeInitGeneticIn));

        P.sort4L(E);
        
        int fitnessModified= fitnessDifferent(P.getIndividuals(0), bestIndividuals);
        if (fitnessModified == 0){
          noChangeIter++;
        }
        else{          
          noChangeIter= 0;
          bestIndividuals= P.getIndividuals(0);   
        }
        
      }//while (noChangeIter <= maxIter){

//      DebugClass.printPopulationElementFitness(fileResultDebug+0,P,1,0,E,0);
//      DebugClass.printFitnessIndividualPopulation("",0,this.P,1,0,this.E);                                

      
//Esta parte es para probar a generalizar las variables, uniendo las etiquetas que podemos      
      if (generalizeVariables(numIndividuals,E,R) == 1){ //se ha mejorado
        String aux= "\nHA GENERALIZADO LA REGLA APRENDIDA\n";
      }
      
      // comprobar para añadir nueva regla          
      double consXcomp= P.getIndividuals(0).getFitness(1);
      double difMetrica= P.getIndividuals(0).getFitness(0);
      
      if (consXcomp > 0 || difMetrica > 0){ // si aporta algo la regla lo comprobamos
  
        int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
        int numExamples= E.getNumExamples();
        int classR= P.getIndividuals(0).getIntegerMatrix(0,0);
        int exampCovered= (int) P.getIndividuals(0).getRealMatrix(2+classR,11); //valor n+eCalculadoParaCovered
        if (exampCovered > (int) (0.1 * (numExamples/ 100.0))){

          weight= P.getIndividuals(0).getRealMatrix(2+classR, 4);
          R.addRule(P.getIndividuals(0).getGenetCodeObject(), weight, E);
          
          
          return 1;
        }
        else{
          return 0;
        }
        }
      else{ //sino, seguimos buscando...(no se cuanto más...) o paramos... como mejor veamos... 
        return 0;
      }      
    }

    /*
    If addRule == 1 -> establece la regla por defecto y devuelve 1. En otro caso 
    devuelve el índice de la clase que tendría la regla por defecto
    
    */
  public int setDefaultRule(int addRule, ExampleSetProcess E, RuleSetClass R){
    IndividualClass defaultRule= P.getIndividuals(0).copy();
    int numVar= E.getProblemDefinition().numAntecedentVariables();
    int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
    int numEjemplos= E.getNumExamples();
    int numFitness= P.getIndividuals(0).getNumFitness();
    double[] fitness= new double[numFitness];
    
    int[][] binary= P.getIndividuals(0).getBinaryMatrix();
    
    // poner todas las etiquetas de todas las variables antecedentes a 1
    for (int i=0; i < binary.length; i++){
      for (int j=0; j < binary[i].length; j++){
        binary[i][j]= 1; 
      }
    }
    
//    // poner el consecuente al valor medio (clase media)
//    P.getIndividuals(0).setIntegerMatrix(0,0,(int) numClases / 2);
    
    // poner el umbral a 0 para que tenga en cuenta todas las variables
    P.getIndividuals(0).setRealMatrix(0, numVar, 0);
    
    calcWeightIndividualForFitness(0,E);
    calcFitness(0,E,R); 
    int classR= P.getBetterClassWithFitness(0, E);
// Para modificar la clase de la regla por defecto se añade: classR=índice de clase a poner como defecto (0,1,2,3,...)    
     P.getIndividuals(0).setIntegerMatrix(0,0,classR);

    // en vez de calcular el peso de la regla, como se ejecuta cuando no hay otra, se le pone el peor
    // peso que podría tener (n+=0; n-=numEjemplos * (adaptAnt==1)*(adaptNoCons==1)*distanciaMax) 
    // FUNCIONA MEJOR CALCULAR EL FITNESS Y DESPUÉS PONER EL PEOR PESO QUE PONER EL PESO Y DESPUÉS CALCULAR EL FITNESS
    double peso= 1 / (double) ((numEjemplos*(numClases-1)) + 1);
    for (int c=0; c < numClases; c++){
      P.getIndividuals(0).setRealMatrix(2+c,4,peso);      
    }
    fitness[0]= P.getIndividuals(0).getRealMatrix(2+classR,0);
    fitness[1]= P.getIndividuals(0).getRealMatrix(2+classR,1);
    fitness[2]= P.getIndividuals(0).getRealMatrix(2+classR,2);
    fitness[3]= P.getIndividuals(0).getRealMatrix(2+classR,3);
    fitness[4]= P.getIndividuals(0).getRealMatrix(2+classR,4);
    fitness[5]= P.getIndividuals(0).getRealMatrix(2+classR,5);
    fitness[6]= P.getIndividuals(0).getRealMatrix(2+classR,6);
    P.getIndividuals(0).setFitness(fitness);        


    
    // se pone el individuo 1 como el 0 pero calculando el fitness con el peso "real"
    P.setIndividuals(1,P.getIndividuals(0).copy());
//    calcWeightIndividualForFitness(1);
//    double peso= 1 / (double) (numEjemplos + 1);
    for (int c = 0; c < numClases; c++) {
      P.getIndividuals(1).setRealMatrix(2 + c, 4, peso);
    }
    calcFitness(1,E,R); 
    
    if (addRule == 1){
        R.addRule(P.getIndividuals(0).getGenetCodeObject(), peso, E);
        return -1;
    }
    //    P.setIndividuals(0,defaultRule);
    Util.DefaultRule= P.getIndividuals(0);
    return P.getIndividuals(0).getIntegerMatrix(0,0);                
  }
    
 /**
  * try to generalize variables removing 1's or 0's isolates
  * @return 0 -> no improvement, 1 -> improvement
  */
  public int generalizeVariables(int numIndividuals, ExampleSetProcess E, RuleSetClass R){
      IndividualClass auxInd= P.getIndividuals(0).copy();
      IndividualClass auxIndModif;
      int resultado=0;

      int digits=0, chances=0, start=0, numLabels=0, numVarAnt=0;
      String strGaps="", myFormat="%"+digits+"s";
      int[][] gaps;
      
      int numFitness= auxInd.getNumFitness();
      double[] fitnessOrig, fitnessNew;
      int igual=0, mejor=0;
      int numOnesOrig=0, numOnesNew=0, numZerosOrig=0, numZerosNew=0;
      // devuelve el número de 0's o 1's que están aislados (solo hay 1 de un tipo entre el resto de otro tipo)
//DebugClass.printIndividual(fileResultDebug+0,auxInd,1,1,E);
      numVarAnt= E.getProblemDefinition().numAntecedentVariables();
      numLabels=0;
      start=0;
      for (int i=0; i < numVarAnt; i++){
        numLabels= E.getProblemDefinition().getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum();
        gaps= auxInd.getNumGaps(start, numLabels); 
//System.out.println("gaps:");
//for (int j=0; j < gaps[0].length-1; j++){
//  System.out.print(gaps[0][j]);
//}
//System.out.println(" "+gaps[0][gaps[0].length-1]);
//for (int j=0; j < gaps[1].length-1; j++){
//  System.out.print(gaps[1][j]);
//}
//System.out.println(" "+gaps[1][gaps[1].length-1]);
        
        // modificar el individuo y probar las métricas (quitando los 0's) -> lo hace más genérico
        digits= gaps[0][numLabels];
        chances= (int) Math.pow(2, digits);
        myFormat="%"+digits+"s";
        if (chances > 1){
          for (int j=1; j < chances; j++){
            strGaps=String.format(myFormat, Integer.toString(j,2)).replace(" ", "0");
//System.out.println(strGaps);
            
            auxIndModif= auxInd.copy();                        
            auxIndModif.modGapsStr(start, numLabels, gaps[0], strGaps);
//DebugClass.printIndividual(fileResultDebug+0,auxInd,1,1,E);
            P.setIndividuals(numIndividuals-1, auxIndModif);
            this.calcWeightIndividualForFitness(numIndividuals-1,E);
            this.calcFitness(numIndividuals-1,E,R);            
            numOnesOrig= auxInd.calcNumberOnes(start, numLabels);
            numOnesNew= auxIndModif.calcNumberOnes(start, numLabels);
//DebugClass.printIndividual(fileResultDebug+0,auxIndModif,1,1,E);            

            fitnessOrig= auxInd.getFitness();
            fitnessNew= auxIndModif.getFitness();
            igual=1;
            mejor=0;
            for (int k= 0; k < numFitness && igual==1 && mejor==0; k++){
              if (fitnessNew[k] > fitnessOrig[k]){
                mejor=1;
              }
              else if (fitnessNew[k] < fitnessOrig[k]){
                igual=0;
              }
            }            
            if (mejor == 1 || (igual == 1 && numOnesNew > numOnesOrig)){
              auxInd= auxIndModif.copy();
              P.setIndividuals(0,auxInd);
              resultado=1;
            }
          }
        }

        // modificar el individuo y probar las métricas (quitando los 1's) -> lo hace más restrictivo
        digits= gaps[1][numLabels];
        chances= (int) Math.pow(2, digits);
        myFormat="%"+digits+"s";
        if (chances > 1){
          for (int j=1; j < chances; j++){
            strGaps=String.format(myFormat, Integer.toString(j,2)).replace(" ", "0");
//System.out.println(strGaps);
            
            auxIndModif= auxInd.copy();                        
            auxIndModif.modGapsStr(start, numLabels, gaps[1], strGaps);
//DebugClass.printIndividual(fileResultDebug+0,auxInd,1,1,E);
            P.setIndividuals(numIndividuals-1, auxIndModif);
            this.calcWeightIndividualForFitness(numIndividuals-1,E);
            this.calcFitness(numIndividuals-1,E,R);            
            numOnesOrig= auxInd.calcNumberOnes(start, numLabels);
            numOnesNew= auxIndModif.calcNumberOnes(start, numLabels);
//DebugClass.printIndividual(fileResultDebug+0,auxIndModif,1,1,E);

            fitnessOrig= auxInd.getFitness();
            fitnessNew= auxIndModif.getFitness();
            igual=1;
            mejor=0;
            for (int k= 0; k < numFitness && igual==1 && mejor==0; k++){
              if (fitnessNew[k] > fitnessOrig[k]){
                mejor=1;
              }
              else if (fitnessNew[k] < fitnessOrig[k]){
                igual=0;
              }
            }            
            if (mejor == 1 || (igual == 1 && numOnesNew > numOnesOrig)){
              auxInd= auxIndModif.copy();
              P.setIndividuals(0,auxInd);
              resultado=1;
            }
          }
        }
        start=start+numLabels;
      }
      return resultado;
  }  
  
  /**
   * Calculate the necessary (adaptation of antecedents) for get the 
   * fitness of the 2 last individuals when they are results of crossover
   * @param resultadoCruce vector=[indiv1Index,indiv2Index,cross1Bin,cross2Bin,cross1Real,cross2Real,check1Binary, check2Binary]
   */
//  public void calcFitnessNeighbourCrossover(int[] resultadoCruce){
//    // resultadoCruce=[indiv1Index,indiv2Index,cross1Bin,cross2Bin,cross1Real,cross2Real,check1Binary, check2Binary]
//    int distPenultimoCruce=0, distUltimoCruce=0;
//    IndividualClass[] vecinosPenultimoCruce, vecinosUltimoCruce;
//    int numIndividuals= P.getNumIndividuals();
//        
//    distPenultimoCruce= Util.calcDistHammingInforMeasureConsidered(E,P.getIndividuals(numIndividuals-2), 
//            P.getIndividuals(resultadoCruce[0]));
//    distUltimoCruce= Util.calcDistHammingInforMeasureConsidered(E,P.getIndividuals(numIndividuals-1), 
//            P.getIndividuals(resultadoCruce[1]));
//    vecinosPenultimoCruce= Util.calcVecinos(P.getIndividuals(numIndividuals-2).copy(), 
//            P.getIndividuals(resultadoCruce[0]).copy(), distPenultimoCruce, E);
//    vecinosUltimoCruce= Util.calcVecinos(P.getIndividuals(numIndividuals-1).copy(), 
//            P.getIndividuals(resultadoCruce[1]).copy(), distUltimoCruce, E);
//
////          if (distPenultimoCruce != 0 || Util.equalIndiv(P.getIndividuals(numIndividuals-2),vecinosPenultimoCruce[0]) == 0){
//        Util.calcAdaptAntWeightIndivForInit(P.getIndividuals(numIndividuals-2).copy(), vecinosPenultimoCruce[0],E);
////            if (resultadoCruce[4] != -1 || resultadoCruce[5] != -1){ // ha habido cruce de medidas de informacion 
////              Util.calcAdaptAntWeightIndivForChangeMeasures(P.getIndividuals(numIndividuals-2), vecinosPenultimoCruce[0],E);
////            }//if (resultadoCruce[4] != -1 || resultadoCruce[5] != -1){ // ha habido cruce de medidas de informacion 
////            else{ // No ha habido cruce de medidas de información -> se calcula el primer vecino por valores binarios
////              Util.calcAdaptAntWeightIndivForBinaryNeighbour(P.getIndividuals(numIndividuals-2), vecinosPenultimoCruce[0],E);
////            }// else //if (resultadoCruce[4] != -1 || resultadoCruce[5] != -1){ // ha habido cruce de medidas de informacion 
//
//      // calcular las adaptaciones de los otros vecinos
//      for (int v=1; v < vecinosPenultimoCruce.length; v++){
//        Util.calcAdaptAntWeightIndivForBinaryNeighbour(vecinosPenultimoCruce[v-1], vecinosPenultimoCruce[v],E);
//      }
//
//      // aquí habría que calcular los fitness y quedarnos con el mejor......
//      for (int v=0; v < vecinosPenultimoCruce.length; v++){
//        Util.calcFitnessNeighbour(E, vecinosPenultimoCruce[v], R);
//      }
////// check que está bien hecho 
////System.out.println("vecinosPenultimoCruce: " +vecinosPenultimoCruce.length);
////DebugClass.writeResFile("../results.txt0","vecinosPenultimoCruce: " +vecinosPenultimoCruce.length);
////for (int kk= 0; kk < vecinosPenultimoCruce.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosPenultimoCruce,1,1,E,kk);  
////}
////// FIN - check que está bien hecho               
//              // ordenar los vecinosPenultimoCruce y quedarte con el mejor
//              Util.sort(vecinosPenultimoCruce);
////// check que está bien hecho 
////System.out.println("vecinosPenultimoCruce: " +vecinosPenultimoCruce.length);
////DebugClass.writeResFile("../results.txt0","vecinosPenultimoCruce: " +vecinosPenultimoCruce.length);
////for (int kk= 0; kk < vecinosPenultimoCruce.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosPenultimoCruce,1,1,E,kk);  
////}
////// FIN - check que está bien hecho               
//
////      P.setIndividuals(numIndividuals-2, vecinosPenultimoCruce[vecinosPenultimoCruce.length-1].copy());            
//      P.setIndividuals(numIndividuals-2, vecinosPenultimoCruce[0].copy());            
////// check para comprobar que hay fitness distintos
////if (Util.equalIndiv(vecinosPenultimoCruce[0], vecinosPenultimoCruce[vecinosPenultimoCruce.length-1]) == 0){
////  System.out.println("VECINOS (penultimo cruce) CON FITNESS DISTINTOS");
////}
////// FIN - check para comprobar que hay fitness distintos
//      
//      
//      
//      
////          }//if (distPenultimoCruce != 0){
////          if (distUltimoCruce != 0 || Util.equalIndiv(P.getIndividuals(numIndividuals-1),vecinosUltimoCruce[0]) == 0){
//        Util.calcAdaptAntWeightIndivForInit(P.getIndividuals(numIndividuals-1).copy(), vecinosUltimoCruce[0], E);
////            if (resultadoCruce[4] != -1 || resultadoCruce[5] != -1){ // ha habido cruce de medidas de informacion 
////              Util.calcAdaptAntWeightIndivForChangeMeasures(P.getIndividuals(numIndividuals-1), vecinosUltimoCruce[0], E);
////            }//if (resultadoCruce[4] != -1 || resultadoCruce[5] != -1){ // ha habido cruce de medidas de informacion 
////            else{ // No ha habido cruce de medidas de información -> se calcula el primer vecino por valores binarios
////              Util.calcAdaptAntWeightIndivForBinaryNeighbour(P.getIndividuals(numIndividuals-1), vecinosUltimoCruce[0],E);
////            }// else //if (resultadoCruce[4] != -1 || resultadoCruce[5] != -1){ // ha habido cruce de medidas de informacion 
//
//      // calcular las adaptaciones de los otros vecinos
//      for (int v=1; v < vecinosUltimoCruce.length; v++){
//        Util.calcAdaptAntWeightIndivForBinaryNeighbour(vecinosUltimoCruce[v-1], vecinosUltimoCruce[v],E);
//      }
//
//      // aquí habría que calcular los fitness y quedarnos con el mejor......
//      for (int v=0; v < vecinosUltimoCruce.length; v++){
//        Util.calcFitnessNeighbour(E, vecinosUltimoCruce[v], R);
//      }            
//      
////// check que está bien hecho 
////System.out.println("vecinosUltimoCruce: " +vecinosUltimoCruce.length);
////DebugClass.writeResFile("../results.txt0","vecinosUltimoCruce: " +vecinosUltimoCruce.length);
////for (int kk= 0; kk < vecinosUltimoCruce.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosUltimoCruce,1,1,E,kk);  
////}
////// FIN - check que está bien hecho               
//              // ordenar los vecinosUltimoCruce y quedarte con el mejor
//              Util.sort(vecinosUltimoCruce);
////// check que está bien hecho 
////System.out.println("vecinosUltimoCruce: " +vecinosUltimoCruce.length);
////DebugClass.writeResFile("../results.txt0","vecinosUltimoCruce: " +vecinosUltimoCruce.length);
////for (int kk= 0; kk < vecinosUltimoCruce.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosUltimoCruce,1,1,E,kk);  
////}
////// FIN - check que está bien hecho               
//      
////      P.setIndividuals(numIndividuals-1, vecinosUltimoCruce[vecinosUltimoCruce.length-1].copy());
//      P.setIndividuals(numIndividuals-1, vecinosUltimoCruce[0].copy());
////// check para comprobar que hay fitness distintos
////if (Util.equalIndiv(vecinosUltimoCruce[0], vecinosUltimoCruce[vecinosUltimoCruce.length-1]) == 0){
////  System.out.println("VECINOS (ultimo cruce) CON FITNESS DISTINTOS");
////}
//// FIN - check para comprobar que hay fitness distintos
//      
//      
////          }//if (distUltimoCruce != 0){   
//
//  }

  /**
   * Calculate the necessary (adaptation of antecedents) for get the 
   * fitness of the 2 last individuals when they are results of mutation
   * @param resultadoMutacion matrix (see notes)
   * @param nextToLast individuals in population
   * @param last individuals in population
   * @note resultadoMutacion[0]=[modificacionPenlastBin,modificacionPenlastInt, modificacionPenlastReal, check1Bin]
   * @note resultadoMutacion[1]=[modificacionUltimoBin,modificacionUltimoInt, modificacionUltimoReal, check2Bin]
   */
//  public void calcFitnessNeighbourMut(int[][] resultadoMutacion, IndividualClass nextToLast,
//          IndividualClass last){
//    // resultadoMutacion[0]=[modificacionPenlastBin,modificacionPenlastInt, modificacionPenlastReal, check1Bin]
//    // resultadoMutacion[1]=[modificacionUltimoBin,modificacionUltimoInt, modificacionUltimoReal, check2Bin]
//  
//    int distPenlastMut=0, distUltimoMut=0;
//    IndividualClass[] vecinosPenlastMut, vecinosUltimoMut;
//    int numIndividuals= P.getNumIndividuals();
//
//    distPenlastMut= Util.calcDistHammingInforMeasureConsidered(E,nextToLast, P.getIndividuals(numIndividuals-2));
//    distUltimoMut= Util.calcDistHammingInforMeasureConsidered(E,last, P.getIndividuals(numIndividuals-1));
//    vecinosPenlastMut= Util.calcVecinos(nextToLast, P.getIndividuals(numIndividuals-2).copy(), distPenlastMut,E);
//    vecinosUltimoMut= Util.calcVecinos(last, P.getIndividuals(numIndividuals-1).copy(), distUltimoMut,E);
//
////        if (distPenlastMut != 0 || Util.equalIndiv(P.getIndividuals(numIndividuals-2),vecinosPenlastMut[0]) == 0){
//      if (resultadoMutacion[0][0] == 1 || resultadoMutacion[0][2] == 1 || resultadoMutacion[0][3] == 1){ 
////              Util.calcAdaptAntWeightIndivForChangeMeasures(nextToLast, vecinosPenlastMut[0],E);
//          Util.calcAdaptAntWeightIndivForInit(nextToLast, vecinosPenlastMut[0],E);
//      }
//      for (int v=1; v < vecinosPenlastMut.length; v++){
//        Util.calcAdaptAntWeightIndivForBinaryNeighbour(vecinosPenlastMut[v-1], vecinosPenlastMut[v],E);
//      }          
//      // aquí habría que calcular los fitness y quedarnos con el mejor......
//      for (int v=0; v < vecinosPenlastMut.length; v++){
//        Util.calcFitnessNeighbour(E, vecinosPenlastMut[v], R);
//      }
////// check que está bien hecho 
////System.out.println("vecinosPenlastMut: " +vecinosPenlastMut.length);
////DebugClass.writeResFile("../results.txt0","vecinosPenlastMut: " +vecinosPenlastMut.length);
////for (int kk= 0; kk < vecinosPenlastMut.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosPenlastMut,1,1,E,kk);  
////}
////// FIN - check que está bien hecho               
//              // ordenar los vecinosPenlastMut y quedarte con el mejor
//              Util.sort(vecinosPenlastMut);
////// check que está bien hecho 
////System.out.println("vecinosPenlastMut: " +vecinosPenlastMut.length);
////DebugClass.writeResFile("../results.txt0","vecinosPenlastMut: " +vecinosPenlastMut.length);
////for (int kk= 0; kk < vecinosPenlastMut.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosPenlastMut,1,1,E,kk);  
////}
////// FIN - check que está bien hecho               
//      
////      P.setIndividuals(numIndividuals-2, vecinosPenlastMut[vecinosPenlastMut.length-1].copy());
//      P.setIndividuals(numIndividuals-2, vecinosPenlastMut[0].copy());
////// check para comprobar que hay fitness distintos
////if (Util.equalIndiv(vecinosPenlastMut[0], vecinosPenlastMut[vecinosPenlastMut.length-1]) == 0){
////  System.out.println("VECINOS (nextToLast mut) CON FITNESS DISTINTOS");
////}
////// FIN - check para comprobar que hay fitness distintos
//
//      
//      
////        }//if (distPenlastMut != 0){
//
////        if (distUltimoMut != 0 || Util.equalIndiv(P.getIndividuals(numIndividuals-1),vecinosUltimoMut[0]) == 0){
//      if (resultadoMutacion[1][0] == 1 || resultadoMutacion[1][2] == 1 || resultadoMutacion[1][3] == 1){ 
////              Util.calcAdaptAntWeightIndivForChangeMeasures(last, vecinosUltimoMut[0], E);
//          Util.calcAdaptAntWeightIndivForInit(last, vecinosUltimoMut[0], E);
//      }
//      if (resultadoMutacion[1][0] == 1 || resultadoMutacion[1][2] == 1){ // se ha modificado la parte binaria o las medidas
//        for (int v=1; v < vecinosUltimoMut.length; v++){
//          Util.calcAdaptAntWeightIndivForBinaryNeighbour(vecinosUltimoMut[v-1], vecinosUltimoMut[v],E);
//        }          
//      }
//      // aquí habría que calcular los fitness y quedarnos con el mejor......
//      for (int v=0; v < vecinosUltimoMut.length; v++){
//        Util.calcFitnessNeighbour(E, vecinosUltimoMut[v], R);
//      }
////// check que está bien hecho 
////System.out.println("vecinosUltimoMut: " +vecinosUltimoMut.length);
////DebugClass.writeResFile("../results.txt0","vecinosUltimoMut: " +vecinosUltimoMut.length);
////for (int kk= 0; kk < vecinosUltimoMut.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosUltimoMut,1,1,E,kk);  
////}
////// FIN - check que está bien hecho               
//              // ordenar los vecinosUltimoMut y quedarte con el mejor
//              Util.sort(vecinosUltimoMut);
////// check que está bien hecho 
////System.out.println("vecinosUltimoMut: " +vecinosUltimoMut.length);
////DebugClass.writeResFile("../results.txt0","vecinosUltimoMut: " +vecinosUltimoMut.length);
////for (int kk= 0; kk < vecinosUltimoMut.length; kk++){
////System.out.println("Vecino: " + kk);  
////DebugClass.writeResFile("../results.txt0", "Vecino: " + kk );
////DebugClass.printNeighbourElement("../results.txt0",vecinosUltimoMut,1,1,E,kk);  
////}
////// FIN - check que está bien hecho                     
//      
////      P.setIndividuals(numIndividuals-1, vecinosUltimoMut[vecinosUltimoMut.length-1].copy());          
//      P.setIndividuals(numIndividuals-1, vecinosUltimoMut[0].copy());          
////// check para comprobar que hay fitness distintos
////if (Util.equalIndiv(vecinosUltimoMut[0], vecinosUltimoMut[vecinosUltimoMut.length-1]) == 0){
////  System.out.println("VECINOS (last mut) CON FITNESS DISTINTOS");
////}
////// FIN - check para comprobar que hay fitness distintos
//      
//      
////        }//if (distUltimoMut != 0){
// 
//      
////// check calculos fitness bien
////calcFitnessPopulation(0);
////int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
////double[] fitVec1= vecinosPenlastMut[vecinosPenlastMut.length-1].getRealSubMatrix(numClases+2);
////double[] fitVec2= vecinosUltimoMut[vecinosUltimoMut.length-1].getRealSubMatrix(numClases+2);
////double[] fitInd1= P.getIndividuals(numIndividuals-2).getRealSubMatrix(numClases+2);
////double[] fitInd2= P.getIndividuals(numIndividuals-1).getRealSubMatrix(numClases+2);
////for (int kk=0; kk < 7; kk++){
////  if ( fitVec1[kk] != fitInd1[kk] || fitVec2[kk] != fitInd2[kk]){
////DebugClass.printPopulationElement("../results.txt0",P,1,1,E,numIndividuals-2);
////DebugClass.printNeighbourElement("../results.txt0",vecinosPenlastMut,1,1,E,vecinosPenlastMut.length-1);
////DebugClass.printPopulationElement("../results.txt0",P,1,1,E,numIndividuals-1);
////DebugClass.printNeighbourElement("../results.txt0",vecinosUltimoMut,1,1,E,vecinosUltimoMut.length-1);
////System.exit(-1);
////  }
////}        
////// FIN - check calculos fitness bien
//      
//      
//      
//  }
}