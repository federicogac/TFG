package NSLVOrd;

import java.util.Random;

import java.io.*;
import java.util.ArrayList;


/**
 * @file Util.java
 * @brief file for utilities used in other classes 
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement utilities for other classes
 */
public class Util {

  // variables para tiempos de ejecución
  public static int numIterGenetic;  // numero de iteraciones del genético (número de veces que llama a findNewRule)
  public static ArrayList numIterGeneticIn; // vector con número de iteraciones de cada llamada a findNewRule
  public static int numIndividuals; // número de individuos, aunque esté duplicado se pone aquí por comodidad
  public static int numAtrib; // numero de atributos, igual que antes por comodidad
  public static ArrayList numLabels; // vector con el numero de etiqueta por cada atributo
  public static double timeGenetic; // tiempo de ejecución del genético completo
  public static long timeInitGenetic;
  public static ArrayList timeGeneticIn; // vector con el tiempo de ejecución de cada iteracion del genetico
  public static long timeInitGeneticIn;
  public static ArrayList timeCalcFitness; // vector con el tiempo de calculo del fitness en cada iteracion
  public static long timeInitCalcFitness;
  public static ArrayList timeCalcAdapt; // vector con el tiempo de cálculo de las adaptaciones en cada iteracion
  public static long timeInitCalcAdapt;
  public static int classDefaultRule=-1; // -1 -> se ha añadido la regla por defecto, else -> indica el índice de la clase que se obtiene al calcular la regla por defecto
  public static GenetCodeClass DefaultRule=null;

    /**
     * get two points for croosover 
     * @param max max number to consider
     * @param randomNum random number
     * @return vector of two points in [0, max)
     */
    public static int[] crossPoints(int max, Random randomNum){
    int p1= (int) (randomNum.nextDouble()*max);
    int p2= (int) (randomNum.nextDouble()*max);
    int[] cp= new int[2];
    
    if (p1 < p2){
      cp[0]= p1;
      cp[1]= p2;
    }
    else{
      cp[1]= p1;
      cp[0]= p2;
    }
    
    return cp;
    
  }
  
  /**
   * get 1 with probability "p"
   * (
   * Devuelve 1 con probabilidad p y 0 con probabilidad 1-p
   * )
   * @param p probability for return 1
   * @param randomNum random number
   * @return 1 with probability "p", 0 with probability "1-p"
   */
    public static int Probability(double p, Random randomNum){
        double aleat= randomNum.nextDouble();

        if (aleat < p)
            return 1;
        else
            return 0;
    }

  /**
   * get a number in [0,numElements) distint of noValidElement
   * (
   * selecciona un numero de forma aleatoria dentro de 0 a numElements distinto de elemNoValid
   * )
   * @param numElements  max number of elements to chose
   * @param noValidElement index of element no valid to return
   * @param randomNum seed to random number
   * @return index of random element choosen
   */
  public static int selectRamdomElementIndex(int numElements, int noValidElement, Random randomNum){
      int aleat= (int) (randomNum.nextDouble() * numElements);
      while (aleat == noValidElement){
        aleat= (int) (randomNum.nextDouble() * numElements);
      }
      return aleat;
  }

  /**
   * get number of "1" in labels
   * (
   * devuelve el número de unos del String labels
   * )
   * @param labels string with "1" and "0"
   * @return number of ones of labels
   */
  public static int numUnos(int[] labels){
    int numUnos=0;
    
    for (int i=0; i < labels.length; i++){
      if (labels[i] == 1){
        numUnos++;
      }
    }
    return numUnos;
  }
  
  /**
   * get submatrix of "matrix" with count elements from init
   * (
   * devuelve la submatrix de matrix de inicio y tantos elementos como indica count
   * )
   * @param matrix vector of values to return
   * @param init index of values to return
   * @param count number of values to return
   * @return submatrix of matrix with count elements from init 
   */
  public static int[] submatrix(int[] matrix, int init, int count){
    
    int[] sub= new int[count];
    int j= init;
    for (int i= 0; i < count; j++, i++){
      sub[i]= matrix[j];
    }
    return sub;
  }
  
  /**
   * compare the elements of fitness1 and fitness2 and return 1 when find 
   * a element of fitness1 bigger that the element of fitness2. Begin with index 0
   * (
   * Devuelve 1 si fitness1 es mayor que fitness2 (teniendo más prioridad los
   * índices más bajos.. 0 - n)
   * )
   * @param fitness1 vector of fitness
   * @param fitness2 vector of fitness
   * @return 1 if fitness1 > fitness2; 0: otherwise
   */
  public static int Mayor(double[] fitness1, double[] fitness2){
    int i=0;
    int n= fitness1.length;
    
    while (i < n && fitness1[i] == fitness2[i]){
      i++;
    }
    if (i==n)
      return 0;
    if (fitness1[i] > fitness2[i]){
      return 1;
    }
    else{
      return 0;
    }
  }

  /**
   * get matrix with lambda values for example e and set of rules R
   * @param E set of examples
   * @param R set of rules
   * @param e index of example
   * @return vector with [lambdaPos,lambdaNeg,indexLambdaPos,indexLambdaNeg,posWeight,negWeight]
   */
  public static double[] calcLambda(ExampleSetProcess E, RuleSetClass R, int e, PopulationClass P){  

    int varCons= E.getProblemDefinition().consequentIndex();    
    int numRules= R.getNumRules();
    double lambdaPos, lambdaNeg, negWeight, posWeight, indexLambdaPos, indexLambdaNeg;
    double adaptAntR, adaptConsR, adaptNoConsR, pesoR;
//    double adaptBR, adaptBRWeight;
    double values[]= new double[6];
    int classR, classE;
    
    int numClases= E.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermNum();
//    double[] adaptClase= new double[numClases];
//    double[] nBi= new double[numClases];
    
    lambdaPos= lambdaNeg= negWeight= posWeight= 0;
    indexLambdaPos= indexLambdaNeg= -1;
    classE= E.getBetterLabel(e, varCons);
    for (int r=0; r < numRules; r++){
      classR= R.getRules(r).getIntegerMatrix(0, 0);

      adaptAntR= R.getRules(r).getRealMatrix(1,e);
      pesoR= R.getRules(r).getRealMatrix(2+classR,4);

      adaptConsR= P.getAdaptCons(classR,e);
      adaptNoConsR= 1.0 - adaptConsR;

      if (classE == classR){
        if ((adaptAntR*adaptConsR*pesoR) > lambdaPos){
          lambdaPos= adaptAntR*adaptConsR*pesoR;
          indexLambdaPos= r;
          posWeight= pesoR;
        }
      }
      else{
        if ((adaptAntR*adaptNoConsR*pesoR) > 0){
          if ((adaptAntR*adaptNoConsR*pesoR) > lambdaNeg){
            lambdaNeg= adaptAntR*adaptNoConsR*pesoR;
            indexLambdaNeg= r;
            negWeight= pesoR;
          }
          else if (((adaptAntR*adaptNoConsR*pesoR) == lambdaNeg) && pesoR > negWeight){
            lambdaNeg= adaptAntR*adaptNoConsR*pesoR;
            indexLambdaNeg= r;
            negWeight= pesoR;            
          }
        }
      }      
    }//for (int r=0; r < R.getNumRules(); r++){      
    
    values[0]= lambdaPos;
    values[1]= lambdaNeg;
    values[2]= indexLambdaPos;
    values[3]= indexLambdaNeg;
    values[4]= posWeight;
    values[5]= negWeight;
    
    return values;
    
  }

//  /**
//   * Similar to calcLambda but in this case the adaptation of example is calculated
//   * @param E
//   * @param R
//   * @param e
//   * @return 
//   */
//  public static double[] calcLambdaForInference(ExampleSetProcess E, RuleSetClass R, int e){  
//
//    int varCons= E.getProblemDefinition().consequentIndex();    
//    int numRules= R.getNumRules();
//    double lambdaPos, lambdaNeg, negWeight, posWeight, indexLambdaPos, indexLambdaNeg;
//    double adaptAntR, adaptConsR, pesoR, adaptBR, adaptBRWeight;
//    double values[]= new double[6];
//    int classR, classE;
//    int numClases= E.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermNum();
//    
//    
//    lambdaPos= lambdaNeg= negWeight= posWeight= 0;
//    indexLambdaPos= indexLambdaNeg= -1;
//    classE= E.getBetterLabel(e, varCons);
//    for (int r=0; r < numRules; r++){
//      classR= R.getRules(r).getIntegerMatrix(0, 0);
//            
//      adaptAntR= E.calcAdaptAntNormalized(e, R.getRules(r));
////      adaptConsR= E.calcAdaptConsNormalizada(e, R.getRules(r));
//      pesoR= R.getRules(r).getRealMatrix(numClases+2,4);
//
////      adaptBR= adaptAntR*adaptConsR;
//      adaptBR= adaptAntR;
//      adaptBRWeight= adaptBR*pesoR;
//      
//      if (classE == classR){
//        if (adaptBRWeight > lambdaPos){
//          lambdaPos= adaptBRWeight;
//          indexLambdaPos= r;
//          posWeight= pesoR;
//        }
//      }
//      else{
//        if (adaptBRWeight > lambdaNeg){
//          lambdaNeg= adaptBRWeight;
//          indexLambdaNeg= r;
//          negWeight= pesoR;
//        }
//      }
//    }//for (int r=0; r < R.getNumRules(); r++){      
//    
//    values[0]= lambdaPos;
//    values[1]= lambdaNeg;
//    values[2]= indexLambdaPos;
//    values[3]= indexLambdaNeg;
//    values[4]= posWeight;
//    values[5]= negWeight;
//    
//    return values;
//    
//  }
  
  /**
   * get central value of label of variable
   * @param label label to consider
   * @param var variable to consider
   * @param E set of examples
   * @return value
   */
  public static double getCentralValue(int label, int var, ExampleSetProcess E){
    
    double limInf, limSup;

    limInf= E.getProblemDefinition().getFuzzyLinguisticVariableList(var).getFuzzyLinguisticTermList(label).getB();     
    limSup= E.getProblemDefinition().getFuzzyLinguisticVariableList(var).getFuzzyLinguisticTermList(label).getC();     
        
    return (limSup + limInf) / (double) 2;    
  }


    /**
     * get the mininal value of cells r-1,c ; r,c-1; r-1,c-1 of W matrix
     * Used to calculate the OC_beta metric
     * @param W matrix with Wrc values
     * @param r row index of element to consider
     * @param c col index fo element to consider
     * @return min of cells r-1,c ; r,c-1; r-1,c-1 of W matrix
     * @note used in calcMetrics
     */
    public static double getMinWrc(double[][] W,int r,int c){

        double min= 1.1;
        
        if (r > 0 && c > 0 && (W[r-1][c-1] < min)){
            min= W[r-1][c-1];
        }
        if (r > 0 && (W[r-1][c] < min)){
            min= W[r-1][c];
        }
        if (c > 0 && (W[r][c-1] < min)){
            min= W[r][c-1];
        }
        
        return min;
    
    }
  
  /**
   * calculate metrics (and confussion matrix) and store in the properties of "R"
   * CCR, SM, TPR, TNR, FPR, Kappa, AUC, MSE, RMSE, RMAE, OMAE, MMAE, mMAE, AMAE, Spearman, Kendall, OC;
   * @param R set of rules 
   * @param E set of examples
   * @return "" no error; errorString otherwise
   * @note modify the metrics and confussion matrix in "R"
   */
  public static String calcMetrics(RuleSetClass R, ExampleSetProcess E, FuzzyProblemClass problem){
   
    String resultado="";
    
    double CCR, SM, TPRMedia, TNRMedia, FPRMedia, Kappa, AUCMedia, PrecisionMedia;
    double MSE, RMSE, RMAE;
    double OMAE, OMAENormalizado, MMAE, mMAE, AMAE, Spearman, Kendall, OC, beta;
    int indexRule= -1;
    int classR, classE;
    double valueR, valueE;
    double A,B,C,D,inf,sup;
    beta= R.beta;
    
    // leemos los datos y los metemos en la matriz de resultados
    int numClases= problem.numLinguisticTermOfConsequent();
    int varCons= problem.consequentIndex();
    int numEjemplos= E.getNumExamples();
    double [][] results= new double[numEjemplos][2];
    double[][] clases= new double[numEjemplos][2];

    double[][] confusion= new double[numClases+1][numClases+1];// matriz de confusion (con las filas y columnas de las sumas)
    for (int j= 0; j < numClases+1; j++){
        for (int k= 0; k < numClases+1; k++){
            confusion[j][k]= 0;
        }
    }

    for (int i= 0; i < numEjemplos; i++){
      valueE= E.getData(i, varCons);
      results[i][0]= valueE;
      classE= E.getBetterLabel(i, varCons);      
      indexRule= R.inference(E,i);      
      if (indexRule != -1){ // hay adaptación del ejemplo con alguna regla
        classR= R.getRules(indexRule).getIntegerMatrix(0,0);
      }
      else{
        classR= Util.classDefaultRule;
//String aux= "\n No ha resuelto nada la inferencia - indexRule="+ indexRule + "\n";
////DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//DebugClass.sendMail=1;
//DebugClass.cuerpoMail+= "\n" + aux;
//        classR= numClases / 2;
//        inf= R.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getInfRange();
//        sup= R.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getSupRange();
//        A= B= R.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getInfRange();
//        C= D= R.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getSupRange();
//        valueR= (sup + inf) / 2.0;
//        results[i][1]= valueR;
      }      

      inf= problem.getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(classR).getA();
      sup= problem.getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(classR).getD();
      A= problem.getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(classR).getA();
      B= problem.getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(classR).getB();
      C= problem.getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(classR).getC();
      D= problem.getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermList(classR).getD();
      valueR= (C + B) / 2.0;
      results[i][1]= valueR;

      confusion[classE][classR]++; // n_i_j
      confusion[classE][numClases]++; // n_i*
      confusion[numClases][classR]++; //n*_j 
      confusion[numClases][numClases]++; //N
    }//for (int i= 0; i < numEjemplos; i++){
    
//    DebugClass.printConfussionMatrix("",confusion,1,0);
    // comprobación matriz confusión
    int sumaFila=0, sumaColumna=0;
    for (int j=0; j < numClases; j++){
        sumaFila+= confusion[j][numClases];
        sumaColumna+= confusion[numClases][j];            
    }
    if (sumaFila != sumaColumna || sumaFila != confusion[numClases][numClases] || sumaFila != numEjemplos){
        resultado= "ERROR: confusion matrix sumRow: " + sumaFila +", sumCol: " 
                + sumaColumna + ", N: " + confusion[numClases][numClases]
                + ", Examples: " + numEjemplos;
        return resultado;
    }

    //comenzamos con las métricas
    //métricas clasificacion
    // CCR
    CCR=0;
    for (int j=0; j < numClases; j++){
        CCR+= confusion[j][j];            
    }
    CCR= CCR / confusion[numClases][numClases];

    // PRECISION
    double[] Precision= new double[numClases];
    PrecisionMedia=0;
    double PrecisionMin=1;
    int PrecisionMinIndex= 0;
    for (int j=0; j < numClases; j++){
//            if (confusion[j][j] != 0 && confusion[j][numClases] == 0 ){
//                TPR[j]= -0;
//            }
        if (confusion[j][numClases] == 0 ){
            Precision[j]= 0;
        }
        else{
            Precision[j]= confusion[j][j] / confusion[numClases][j];
        }
        PrecisionMedia+= Precision[j];
        if (Precision[j] < PrecisionMin){
            PrecisionMin= Precision[j];
            PrecisionMinIndex= j;
        }
    }        
    PrecisionMedia= PrecisionMedia / (double) numClases;

    // TPR, Smin, MM (RECALL)
    double[] TPR= new double[numClases];
    TPRMedia=0;
    double TPRMin=1;
    int TPRMinIndex= 0;
    for (int j=0; j < numClases; j++){
//            if (confusion[j][j] != 0 && confusion[j][numClases] == 0 ){
//                TPR[j]= -0;
//            }
        if (confusion[j][numClases] == 0 ){
            TPR[j]= 0;
        }
        else{
            TPR[j]= confusion[j][j] / confusion[j][numClases];
        }
        TPRMedia+= TPR[j];
        if (TPR[j] < TPRMin){
            TPRMin= TPR[j];
            TPRMinIndex= j;
        }
    }        
    TPRMedia= TPRMedia / (double) numClases;
    SM= TPRMin;

    // Sp, FPR
    double[] TNR= new double[numClases];
    double[] FPR= new double[numClases];
    TNRMedia=0;
    FPRMedia=0;
    for (int j=0; j < numClases; j++){
        TNR[j]= 0;
    }
    for (int j=0; j < numClases; j++){
        for (int k=0; k < numClases; k++){
            for (int l=0; l < numClases; l++){
                if (j != k && j != l){
                    TNR[j]+= confusion[k][l];
                }
            }
        }
        if ((confusion[numClases][numClases] - confusion[j][numClases]) == 0){
            TNR[j]= 0;
        }
        else{
            TNR[j]= TNR[j] / (confusion[numClases][numClases] - confusion[j][numClases]);                                
        }
    }
    for (int j=0; j < numClases; j++){
        TNRMedia+= TNR[j];
        FPR[j]= 1 - TNR[j];
    }
    TNRMedia= TNRMedia / (double) numClases;
    FPRMedia= 1 - TNRMedia;        

    // Kappa Cohen
    Kappa=0;
    double P0=0, Pe=0;
    for (int j=0; j < numClases; j++){
        Pe+= confusion[j][numClases]*confusion[numClases][j];
    }
    Pe= Pe / (confusion[numClases][numClases]*confusion[numClases][numClases]);
    P0= CCR;
    if ((double) 1 - Pe == 0){
        Kappa= 0;
    }
    else{
        Kappa= (P0 - Pe) / ((double) 1 - Pe);            
    }

    //AUC
    double[] AUC= new double[numClases];
    AUCMedia=0;
    for (int j=0; j < numClases; j++){
        AUC[j]= (1 + TPR[j] - FPR[j]) / 2.0;
        AUCMedia+= AUC[j];
    }
    AUCMedia= AUCMedia / (double) numClases;

    //métricas regresion
    // MSE, RMSE, MAE
    MSE=0;
    RMSE=0;
    RMAE=0;
    for (int j=0; j < numEjemplos; j++){
        MSE+= (results[j][0] - results[j][1]) * (results[j][0] - results[j][1]);
        RMAE+= Math.abs(results[j][0] - results[j][1]);
    }
    MSE= MSE / (double) numEjemplos;
    RMSE= Math.sqrt(MSE);
    RMAE= RMAE / (double) numEjemplos;

    //métricas clasificacion ordinal
    // MAEOrd, AMAE, MMAE
    double[] MAEOrd= new double[numClases];
    double MAETotal=0;
    AMAE=0;
    mMAE= confusion[numClases][numClases];
    MMAE=0;
    int MMAEIndex= 0, mMAEIndex=0;
    for (int j=0; j < numClases; j++){
        MAEOrd[j]=0;
    }
    for (int j= 0; j < numClases; j++){
        for (int k= 0; k < numClases; k++){
            MAEOrd[j]+= Math.abs(j-k)*confusion[j][k];
        }
//            if (MAEOrd[j] != 0 && confusion[j][numClases] == 0){
//                MAEOrd[j]= -0;
//            }
        if (confusion[j][numClases] == 0){
            MAEOrd[j]= 0;
        }
        else{
            MAEOrd[j]= MAEOrd[j] / confusion[j][numClases];
        }
        MAETotal+= confusion[j][numClases] * MAEOrd[j];
        AMAE+= MAEOrd[j];
        if (MMAE < MAEOrd[j]){
            MMAE= MAEOrd[j];
            MMAEIndex= j;
        }
        if (mMAE > MAEOrd[j]){
            mMAE= MAEOrd[j];
            mMAEIndex= j;
        }
    }
    MAETotal= MAETotal / confusion[numClases][numClases];
    OMAE= MAETotal;
    OMAENormalizado= OMAE / ((double) numClases-1);
    AMAE= AMAE / numClases;

    //Rs
    // calculos con matriz de confusion (numClases)
    Spearman=0;
    double ORealMedia=0, OPredMedia=0;
    double numerador= 0, denominador1=0, denominador2=0;
    int indNoCero=0; // para que los índices de las clases no comiencen con 0 y se pueda hacer los cálculos

    for (int j= 0; j < numClases; j++){
        ORealMedia+= (j+indNoCero)*confusion[j][numClases];
        OPredMedia+= (j+indNoCero)*confusion[numClases][j];
    }
    ORealMedia= ORealMedia / confusion[numClases][numClases];        
    OPredMedia= OPredMedia / confusion[numClases][numClases];        

    for (int j= 0; j < numClases; j++){
        for (int k=0; k < numClases; k++){
            numerador+= (confusion[j][k])*((j+indNoCero) - ORealMedia)*((k+indNoCero) - OPredMedia);
        }
        denominador1+= confusion[j][numClases] * (((j+indNoCero) - ORealMedia)*((j+indNoCero) - ORealMedia));
        denominador2+= confusion[numClases][j] * (((j+indNoCero) - OPredMedia)*((j+indNoCero) - OPredMedia));
    }            
    denominador1= Math.sqrt(denominador1);
    denominador2= Math.sqrt(denominador2);        
    if (denominador1 * denominador2 == 0){
        Spearman= 0;
    }
    else{
        Spearman= numerador / (denominador1 * denominador2);            
    }

    //Taub
    // calculos con matriz de confusion (numClases)
    numerador= denominador1= denominador2= 0;
    Kendall=0;
    for (int j1= 0; j1 < numClases; j1++){
        for (int j2= 0; j2 < numClases; j2++){
            double parcial=0;
            for (int k1= 0; k1 < numClases; k1++){
                for (int k2= 0; k2 < numClases; k2++){ // no se utiliza la fila y columna del que estoy considerando
                    if (k1 < j1){           // estoy en la parte superior al que estoy considerando (j)
                        if (k2 < j2){       // estoy en la parte izda al que estoy considerando (j)
                            parcial= parcial + confusion[k1][k2];                                
                        }
                        else if (k2 > j2){  // estoy en la parte derecha
                            parcial= parcial - confusion[k1][k2];
                        }
                    }
                    else if (k1 > j1){      // estoy en la parte inferior al que estoy considerando (j)
                        if (k2 < j2){       // estoy en la parte izda al que estoy considerando (j)
                            parcial= parcial - confusion[k1][k2];                                
                        }
                        else if (k2 > j2){  // estoy en la parte derecha
                            parcial= parcial + confusion[k1][k2];
                        }
                    }
                }//for (int k2= 0; k2 < numClases; k2++){ // no se utiliza la fila y columna del que estoy considerando
            }//for (int k1= 0; k1 < numClases; k1++){
            numerador+= confusion[j1][j2] * parcial;
        }//for (int j2= 0; j2 < numClases; j2++){
        denominador1+= confusion[numClases][j1] * (confusion[numClases][numClases] - confusion[numClases][j1]);
        denominador2+= confusion[j1][numClases] * (confusion[numClases][numClases] - confusion[j1][numClases]);
    }//for (int j1= 0; j1 < numClases; j1++){

    if (denominador1 * denominador2 == 0){
        Kendall= 0;
    }
    else{
        Kendall= numerador / Math.sqrt(denominador1 * denominador2);            
    }

    //OC
    double[][] nrc_absnrc= new double[numClases+1][numClases+1];
    double[][] W= new double[numClases][numClases];
    double[][] w= new double[numClases][numClases];
    OC= -1;
    double sumaParcial=0, sumaTotal=0;
    beta= beta / (confusion[numClases][numClases]*(numClases-1));

    for (int j= 0; j < numClases; j++){
        sumaParcial=0;
        for (int k= 0; k < numClases; k++){
            nrc_absnrc[j][k]= confusion[j][k] * Math.abs(j-k);
            sumaParcial+= nrc_absnrc[j][k];
        }
        nrc_absnrc[j][numClases]= sumaParcial;
        sumaTotal+= sumaParcial;
    }
    nrc_absnrc[numClases][numClases]= sumaTotal;

    for (int j= 0; j < numClases; j++){
        for (int k= 0; k < numClases; k++){
            w[j][k]= - ((confusion[j][k]) / 
                       (confusion[numClases][numClases] + nrc_absnrc[numClases][numClases]) )
                     + beta*nrc_absnrc[j][k];                        
        }
    }

    W[0][0]= 1 + w[0][0];
    for (int j=1; j < numClases; j++){
        W[j][0]= w[j][0] + getMinWrc(W,j,0);
    }
    for (int j=1; j < numClases; j++){
        W[0][j]= w[0][j] + getMinWrc(W,0,j);
    }
    for (int j= 1; j < numClases; j++){
        for (int k= 1; k < numClases; k++){
            W[j][k]= w[j][k] + getMinWrc(W,j,k);
        }
    }

    OC= W[numClases-1][numClases-1];
    
    // pasar las métricas a la matriz
    R.CCR= CCR;
    R.SM= SM;
    R.TPR= TPRMedia;
    R.TNR= TNRMedia;
    R.FPR= FPRMedia;
    R.Kappa= Kappa;
    R.AUC= AUCMedia;
    R.MSE= MSE;
    R.RMSE= RMSE;
    R.RMAE= RMAE;
    R.OMAE= OMAE;
    R.OMAENormalizado= OMAENormalizado;
    R.MMAE= MMAE;
    R.mMAE= mMAE;
    R.AMAE= AMAE;
    R.Spearman= Spearman;
    R.Kendall= Kendall;
    R.OC= OC;
    R.Precision= PrecisionMedia;

    // para nosotros la métrica a utilizar va a ser esta y estará en el intervalo 
    // [0,1] siendo mejor cuanto más cercano esté de 1 (igual que CCR)
//    R.metric= ((numClases - 1) - (1-CCR) * OMAE) / (numClases - 1) ;
//    R.metric= 1 - ((1-CCR) * (OMAE / (numClases - 1))) ;
// Para el cálculo de la métrica ponderada    -    Metrica = (CCR + (1-OMAENormalizado))/2.0;
    R.metricMedia= (CCR + (1-OMAENormalizado)) / 2.0;
    R.metric= R.metricMedia;
    
    R.confusion= new double[numClases+1][numClases+1];// matriz de confusion (con las filas y columnas de las sumas)
    for (int j= 0; j < numClases+1; j++){
        for (int k= 0; k < numClases+1; k++){
            R.confusion[j][k]= confusion[j][k];
        }
    }
    
    return resultado;
  }
  
// FUNCIONES PARA CUDA
      /**
     * The extension of the given file name is replaced with "ptx".
     * If the file with the resulting name does not exist, it is
     * compiled from the given file using NVCC. The name of the
     * PTX file is returned.
     *
     * @param cuFileName The name of the .CU file
     * @return The name of the PTX file
     * @throws IOException If an I/O error occurs
     * @deprecated not used in this version
     */
    public static String preparePtxFile(String cuFileName) throws IOException
    {
        int endIndex = cuFileName.lastIndexOf('.');
        if (endIndex == -1)
        {
            endIndex = cuFileName.length()-1;
        }
        String ptxFileName = cuFileName.substring(0, endIndex+1)+"ptx";
        File ptxFile = new File(ptxFileName);
        if (ptxFile.exists())
        {
            return ptxFileName;
        }

        File cuFile = new File(cuFileName);
        if (!cuFile.exists())
        {
            throw new IOException("Input file not found: "+cuFileName);
        }
        String modelString = "-m"+System.getProperty("sun.arch.data.model") + "-arch=sm_11";
        String command =
            "nvcc " + modelString + " -ptx "+
            cuFile.getPath()+" -o "+ptxFileName;

        System.out.println("Executing\n"+command);
        Process process = Runtime.getRuntime().exec(command);

        String errorMessage =
            new String(toByteArray(process.getErrorStream()));
        String outputMessage =
            new String(toByteArray(process.getInputStream()));
        int exitValue = 0;
        try
        {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IOException(
                "Interrupted while waiting for nvcc output", e);
        }

        if (exitValue != 0)
        {
            System.out.println("nvcc process exitValue "+exitValue);
            System.out.println("errorMessage:\n"+errorMessage);
            System.out.println("outputMessage:\n"+outputMessage);
            throw new IOException(
                "Could not create .ptx file: "+errorMessage);
        }

        System.out.println("Finished creating PTX file");
        return ptxFileName;
    }

    /**
     * Fully reads the given InputStream and returns it as a byte array
     *
     * @param inputStream The input stream to read
     * @return The byte array containing the data from the input stream
     * @throws IOException If an I/O error occurs
     * @deprecated not used in this version
     */
    public static byte[] toByteArray(InputStream inputStream)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }
// FIN - FUNCIONES PARA CUDA

  
  
  
  
  
  
  

  public static void initStatisticalData(PopulationClass P, FuzzyProblemClass prob){    
    numIterGenetic=0;
    numIterGeneticIn= new ArrayList();
    numIndividuals= P.getNumIndividuals();
    numAtrib= prob.getFuzzyLinguisticVariableNum();
    numLabels= new ArrayList();
    for (int i=0; i < numAtrib; i++){
      numLabels.add((int) prob.getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum());      
    }
    timeGenetic=0;
    timeGeneticIn= new ArrayList();
    timeCalcFitness= new ArrayList();
    timeCalcAdapt= new ArrayList();    
  } 
    
}
