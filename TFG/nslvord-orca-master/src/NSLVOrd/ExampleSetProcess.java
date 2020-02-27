package NSLVOrd;

import java.util.Scanner;
import java.io.*;
import java.util.Locale;
import java.util.Random;

import keel.Dataset.*;

/**
 * @file ExampleSetProcess.java
 * @brief Process the set of examples to learn
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement the process the set of examples to learn
 */
public class ExampleSetProcess extends ExampleSetClass implements Serializable{
    private FuzzyProblemClass problemDefinition; // definición del problema para los dominios y cálculos de las medidas de información
    private int[] covered;          // matriz[numExamples] que indica si el ejemplo ha sido cubierto para el aprendizaje
    private int[] indexRuleCovered; // matriz[numExamples] que indica el índice de la regla que lo cubre
    private double[] lambdaPos;     // lamatriz[numExamples] 
    private double[] lambdaNeg;
    private double[] negWeight;
    private double[] posWeight;
    private int[] indexLambdaPos;
    private int[] indexLambdaNeg;
    
    private double[][] informationMeasures; //matriz de medidas de información para agilizar los cálculos (nº de variables antecedentes x (clases consecuente+1))
    private int indMayLabels;           // valor máximo de etiquetas de todas las variables para la reserva de la matriz tridimensional de adaptaciones 
    private double[][][] adaptExVarLab; // matriz de adaptaciones (función de pertenencia) del ejemplo "e" considerando la variable "v" a la etiqueta "l"

    /** Default constructor
     * @param problemDefinition 
     */
    public ExampleSetProcess (FuzzyProblemClass problemDefinition) { 
        super();
        this.problemDefinition= problemDefinition;
        covered=null;
        indexRuleCovered= null;
        lambdaPos= null;
        lambdaNeg= null;
        negWeight= null;
        posWeight= null;
        indexLambdaPos= null;
        indexLambdaNeg= null;
    };

    /** Used for copy constructor
     * @param orig 
     */
    protected ExampleSetProcess(ExampleSetProcess orig){
        super(orig);
        int numExamples= orig.getNumExamples();
        this.problemDefinition= orig.problemDefinition.copy();
        this.indMayLabels= orig.indMayLabels;
        
        if (numExamples != 0){
          this.covered= new int[numExamples];
          System.arraycopy(orig.covered, 0, this.covered, 0, orig.covered.length);
          this.indexRuleCovered= new int[numExamples];
          System.arraycopy(orig.indexRuleCovered, 0, this.indexRuleCovered, 0, orig.indexRuleCovered.length);
          this.lambdaPos= new double[numExamples];
          System.arraycopy(orig.lambdaPos, 0, this.lambdaPos, 0, orig.lambdaPos.length);
          this.lambdaNeg= new double[numExamples];
          System.arraycopy(orig.lambdaNeg, 0, this.lambdaNeg, 0, orig.lambdaNeg.length);
          this.posWeight= new double[numExamples];
          System.arraycopy(orig.posWeight, 0, this.posWeight, 0, orig.posWeight.length);
          this.negWeight= new double[numExamples];
          System.arraycopy(orig.negWeight, 0, this.negWeight, 0, orig.negWeight.length);
          this.indexLambdaPos= new int[numExamples];
          System.arraycopy(orig.indexLambdaPos, 0, this.indexLambdaPos, 0, orig.indexLambdaPos.length);
          this.indexLambdaNeg= new int[numExamples];
          System.arraycopy(orig.indexLambdaNeg, 0, this.indexLambdaNeg, 0, orig.indexLambdaNeg.length);

          if (orig.informationMeasures != null){
            int numVariablesAntecedentes= this.problemDefinition.numAntecedentVariables();
            int numClasesMas1= this.problemDefinition.numLinguisticTermOfConsequent()+1;
            this.informationMeasures= new double[numVariablesAntecedentes][numClasesMas1];
            for (int i=0; i < numVariablesAntecedentes; i++){
              System.arraycopy(orig.informationMeasures[i], 0, this.informationMeasures[i], 0, orig.informationMeasures[i].length);
            }
          }

          if (orig.adaptExVarLab != null){
            int numVariables= this.getNumVariables();
            this.adaptExVarLab= new double[numExamples][numVariables][this.indMayLabels];

            for (int i=0; i < numExamples; i++){
              for (int j=0; j < numVariables; j++){
                System.arraycopy(orig.adaptExVarLab[i][j], 0, this.adaptExVarLab[i][j], 0, orig.adaptExVarLab[i][j].length);
              }
            }
          }
        }//if (numExamples != 0){
    }
    
    /** copy constructor
     * @return 
     */
    public ExampleSetProcess copy(){
      return new ExampleSetProcess(this);
    }

    /** Default constructor
     * @param problemDefinition 
     */
    public ExampleSetProcess (FuzzyProblemClass problemDefinition, InstanceSet iSet) { 
        super();
        this.problemDefinition= problemDefinition;
        
        int numExamples= iSet.getNumInstances();
        int numVariables= iSet.getAttributeDefinitions().getNumAttributes();
        
        this.setNumExamples(numExamples);
        this.setNumVariables(numVariables);
        this.setData(new double[numExamples][numVariables]);
        this.covered= new int[numExamples];
        this.indexRuleCovered= new int[numExamples];
        this.lambdaPos= new double[numExamples];
        this.lambdaNeg= new double[numExamples];
        this.posWeight= new double[numExamples];
        this.negWeight= new double[numExamples];
        this.indexLambdaPos= new int[numExamples];
        this.indexLambdaNeg= new int[numExamples];
        this.setNumPartitions(1);
        this.setPartition(new int[numExamples]);
        for (int i=0; i < numExamples; i++){
            double[] valAntec= iSet.getInstance(i).getAllInputValues();
            double[] valCons= iSet.getInstance(i).getAllOutputValues(); // ahora mismo consideramos sólo 1 consecuente
            for (int j=0; j < numVariables-1; j++){ // guardar los antecedentes
              this.setData(i, j, valAntec[j]);
            }
            this.setData(i,numVariables-1,valCons[0]); // guardar el consecuente
            this.covered[i]= 0;
            this.setPartition(i,0);
        }
    };
    
    
    /** AddExampleFile
     * Structure of DataFile
     * --------
     * numberOfExamples
     * numberOfVariables (consequent included)
     * data space separated. New line indicates new example
     * 
     * @param DataFile File with domain definition
     * @param partition number of partition of this DataFile
     * @return -1 if error, 1 otherwise
     * @deprecated Used to add file of examples in format NSLV original
     */
    public int AddExampleFile(String DataFile, int partition){
        Scanner scanFile= null;
        int numExamplesOrig, numVariablesOrig, numExamplesNew, numVariablesNew;
        double val;
        
        numExamplesOrig= this.getNumExamples();
        numVariablesOrig= this.getNumVariables();
                
        try{
            scanFile= new Scanner(new FileReader(DataFile));
            scanFile.useLocale(Locale.ENGLISH);   // configura el formato de números
            numExamplesNew= scanFile.nextInt();
            numVariablesNew= scanFile.nextInt();            
     
            if (numVariablesOrig != numVariablesNew && numVariablesOrig != 0){ // los datos no son coherentes
                return -1;
            }
            if (numExamplesOrig == 0){    // aún no está inicializado el conjunto de ejemplos
                this.setNumExamples(numExamplesNew);
                this.setNumVariables(numVariablesNew);
                this.setData(new double[numExamplesNew][numVariablesNew]);
                this.covered= new int[numExamplesNew];
                this.indexRuleCovered= new int[numExamples];
                this.lambdaPos= new double[numExamples];
                this.lambdaNeg= new double[numExamples];
                this.posWeight= new double[numExamples];
                this.negWeight= new double[numExamples];
                this.indexLambdaPos= new int[numExamples];
                this.indexLambdaNeg= new int[numExamples];
                this.setNumPartitions(1);
                this.setPartition(new int[numExamplesNew]);
                for (int i=0; i < numExamplesNew; i++){
                    int indexJ=0;
                    for (int j=0; j < numVariablesNew; j++){
                       val= scanFile.nextDouble();
                       if (j == problemDefinition.getConsequentIndexOriginal()){
                           this.setData(i,numVariablesNew-1,val);                  
                       }
                       else{
                           this.setData(i, indexJ, val);
                           indexJ++;
                       }
                    }
                    this.covered[i]= 0;
                    this.setPartition(i,partition);
                }
                
            }
            else{   // conjunto de ejemplos inicializado -> redimensionar
                int[] auxInt;
                int[] auxIntOrig;
                double[] auxDouble;
                double[][] auxData= new double[numExamplesOrig+numExamplesNew][numVariablesNew];
                double[][] auxDataOrig= this.getData();
                for (int i=0; i < numExamplesOrig; i++){
                    System.arraycopy(auxDataOrig[i], 0, auxData[i], 0, auxDataOrig[i].length);
                }
                this.setData(auxData);
                auxInt= new int[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.covered, 0, auxInt, 0, this.covered.length);
                this.covered= auxInt;
                auxInt= new int[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.indexRuleCovered, 0, auxInt, 0, this.indexRuleCovered.length);
                this.indexRuleCovered= auxInt;
                auxDouble= new double[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.lambdaPos, 0, auxDouble, 0, this.lambdaPos.length);
                this.lambdaPos= auxDouble;
                auxDouble= new double[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.lambdaNeg, 0, auxDouble, 0, this.lambdaNeg.length);
                this.lambdaNeg= auxDouble;
                auxDouble= new double[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.posWeight, 0, auxDouble, 0, this.posWeight.length);
                this.posWeight= auxDouble;
                auxDouble= new double[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.negWeight, 0, auxDouble, 0, this.negWeight.length);
                this.negWeight= auxDouble;
                auxInt= new int[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.indexLambdaPos, 0, auxInt, 0, this.indexLambdaPos.length);
                this.indexLambdaPos= auxInt;
                auxInt= new int[numExamplesOrig+numExamplesNew];
                System.arraycopy(this.indexLambdaNeg, 0, auxInt, 0, this.indexLambdaNeg.length);
                this.indexLambdaNeg= auxInt;
                auxInt= new int[numExamplesOrig+numExamplesNew];
                auxIntOrig= this.getPartition();
                System.arraycopy(auxIntOrig, 0, auxInt, 0, auxIntOrig.length);
                this.setPartition(auxInt);

                this.setNumExamples(numExamplesOrig+numExamplesNew);

                for (int i=numExamplesOrig; i < numExamplesOrig+numExamplesNew; i++){                
                    int indexJ=0;
                    for (int j=0; j < numVariablesNew; j++){
                       val= scanFile.nextDouble();
                       if (j == problemDefinition.consequentIndex()){
                           this.setData(i,numVariablesNew-1,val);                  
                       }
                       else{
                           this.setData(i, indexJ, val);
                           indexJ++;
                       }
                    }
                    this.covered[i]= 0;
                    this.indexRuleCovered[i]= -1;
                    this.setPartition(i,partition);
                }
            }            
            scanFile.close();            
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }finally{
            try{                   
                if( scanFile != null){  
                    scanFile.close();    
                }                 
            }catch (Exception e2){
                e2.printStackTrace();
                System.exit(-1);
            }
        }
        return 1;
    }
    
    /**
     * Generate partitions randomly
     * @param partitionNums number of partitions of ExampleSetClass
     * @param randomNum Random object for get random number
     * @return -1 if partitionNums < 1 ; 1 otherwise
     * @deprecated used to create partitions in random way in NSLV original
     */
    public int generatePartition(int partitionNums, Random randomNum){
        if (partitionNums < 1){
            return -1;
        }
        
        int numExXPart[] = new int[partitionNums];
        for (int i=0; i< partitionNums; i++){
            numExXPart[i]= 0;
        }
        
        this.setNumPartitions(partitionNums);
        if (partitionNums == 1){
            for (int i=0; i < this.getNumExamples(); i++){
                this.setPartition(i, 0);
            }
        }
        else{
            for (int i=0; i < this.getNumExamples(); i++){
                int aux= (int) (randomNum.nextDouble() * partitionNums);                
                this.setPartition(i, aux);
                numExXPart[aux]++;
            }
// esto sólo se tiene en cuenta para los ejemplos pequeños -> se puede quitar para ejemplos grandes            
            // comprobar que todas las particiones tienen ejemplos
            for (int i= 0; i < partitionNums; i++){
                if (numExXPart[i] == 0){ // no hay ejemplos en esa partición -> asignar
                    int j=(i+1) % partitionNums;
                    while (numExXPart[i] == 0){
                        if (numExXPart[j] >= 2){
                            numExXPart[j]--;
                            numExXPart[i]++;
                            for (int k=0; k < this.getNumExamples(); k++){
                                if (this.getPartition(k) == j){
                                    this.setPartition(k,i);
                                    k= this.getNumExamples()+1;
                                }
                            }                            
                            i=0; // reiniciar para comprobar de nuevo todas las particiones
                            j=i;
                        }//if (numExXPart[j] >= 2){
                        j=(j+1) % partitionNums;
                    }//while (numExXPart[i] == 0){
                }//if (numExXPart[i] == 0){
            }//for (int i= 0; i < partitionNums; i++){
// FIN - esto sólo se tiene en cuenta para los ejemplos pequeños -> se puede quitar para ejemplos grandes            
        }
        return 1;
    }
    

  /**
   * Set the value of problemDefinition
   * @param newVar the new value of problemDefinition
   */
  public void setProblemDefinition ( FuzzyProblemClass newVar ) {
    problemDefinition = newVar;
  }

  /**
   * Get the value of problemDefinition
   * @return the value of problemDefinition
   */
  public FuzzyProblemClass getProblemDefinition ( ) {
    return problemDefinition;
  }

    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setCovered ( int[] newVar ) {
        covered = newVar;
    }

    /**
    * Get the value of covered
    * @return the value of covered
    */
    public int[] getCovered ( ) {
        return covered;
    }

    /**
    * Set the value of covered
    * @param row possition of new value
    * @param newVar the new value of covered
    * @return -1 if no valid position, row otherwise
    */
    public int setCovered (int row, int newVar ) {
//        if (row >= 0 && row < this.getNumExamples()){
            covered[row] = newVar;
            return row;
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Get the value of covered
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public int getCovered (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return covered[row];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Get the value of indexRuleCovered
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public int getIndexRuleCovered (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return indexRuleCovered[row];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setIndexRuleCovered ( int[] newVar ) {
        indexRuleCovered = newVar;
    }

    /**
    * Set the value of covered
    * @param row possition of new value
    * @param newVar the new value of covered
    * @return -1 if no valid position, row otherwise
    */
    public int setIndexRuleCovered (int row, int newVar ) {
//        if (row >= 0 && row < this.getNumExamples()){
            indexRuleCovered[row] = newVar;
            return row;
//        }
//        else{
//            return -1;
//        }
    }
    
    /**
    * Get the value of lambdaPos
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public double getLambdaPos (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return lambdaPos[row];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setLambdaPos ( double[] newVar ) {
        lambdaPos = newVar;
    }
    
    /**
    * Get the value of lambdaNeg
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public double getLambdaNeg (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return lambdaNeg[row];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setLambdaNeg ( double[] newVar ) {
        lambdaNeg = newVar;
    }

    /**
    * Get the value of posWeight
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public double getPosWeight (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return posWeight[row];
//        }
//        else{
//            return -1;
//        }
    }
    
    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setPosWeight (double[] newVar ) {
        posWeight = newVar;
    }

    /**
    * Get the value ofp osWeight
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public double getNegWeight (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return negWeight[row];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setNegWeight (double[] newVar ) {
        negWeight = newVar;
    }

    /**
    * Get the value of indexLambdaPos
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public int getIndexLambdaPos (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return indexLambdaPos[row];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setIndexLambdaPos (int[] newVar ) {
        indexLambdaPos = newVar;
    }

    /**
    * Get the value of indexLambdaNeg
    * @param row position of new value
    * @return the value of covered, -1 if no valid position
    */
    public int getIndexLambdaNeg (int row) {
//        if (row >= 0 && row < this.getNumExamples()){
            return indexLambdaNeg[row];
//        }
//        else{
//            return -1;
//        }
    }
    
    /**
    * Set the value of covered
    * @param newVar the new value of covered
    */
    public void setIndexLambdaNeg ( int[] newVar ) {
        indexLambdaNeg = newVar;
    }

    /**
    * Set the value of information measures
    * @param newVar the new value of information measures
    */
    public void setInformationMeasures ( double[][] newVar ) {
        informationMeasures = newVar;
    }

    /**
    * Get the value of information measures
    * @return the value of information measures
    */
    public double[][] getInformationMeasures ( ) {
        return informationMeasures;
    }

    /**
    * Set the value of information measures
    * @param row position of new value
    * @param col position of new value
    * @param newVar the new value of information measures
    * @return -1 if no valid position, row otherwise
    */
    public int setInformationMeasures (int row, int col, double newVar ) {
//        if (informationMeasures.length == 0)
//            return -1;
//        else if (row >= 0 && row < problemDefinition.getFuzzyLinguisticVariableNum() &&
//            col >= 0 && col < problemDefinition.numLinguisticTermOfConsequent() + 1){
            informationMeasures[row][col] = newVar;
            return row;
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Get the value of informationMeasures
    * @param row position of new value
    * @param col position of new value
    * @return the value of partition, -1 if no valid position
    */
    public double getInformationMeasures (int row, int col) {
//        if (informationMeasures.length == 0)
//            return -1;
//        else if (row >= 0 && row < problemDefinition.getFuzzyLinguisticVariableNum() &&
//            col >= 0 && col < problemDefinition.numLinguisticTermOfConsequent() + 1){
            
            return informationMeasures[row][col];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Set the value of indMayLabels
    * @param newVar the new value of indMayLabels
    */
    public void setIndMayLabels ( int newVar ) {
        indMayLabels = newVar;
    }

    /**
    * Get the value of indMayLabels
    * @return the value of indMayLabels
    */
    public int getIndMayLabels ( ) {
        return indMayLabels;
    }

    /**
    * Set the value of adaptExVarLab
    * @param newVar the new value of adaptExVarLab
    */
    public void setAdaptExVarLab ( double[][][] newVar ) {
        adaptExVarLab = newVar;
    }

    /**
    * Get the value of adaptExVarLab
    * @return the value of adaptExVarLab
    */
    public double[][][] getAdaptExVarLab ( ) {
        return adaptExVarLab;
    }

    /**
    * Set the value of adaptExVarLab
    * @param ex position of new value
    * @param var position of new value
    * @param lab position of new value
    * @param newVar the new value of adaptExVarLab
    * @return -1 if no valid position, ex otherwise
    */
    public int setAdaptExVarLab (int ex, int var, int lab, double newVar ) {
//        if (adaptExVarLab.length ==0)
//            return -1;
//        else if (ex >= 0 && ex < this.getNumExamples() &&
//            var >= 0 && var < this.getNumVariables() &&
//            lab >= 0 && lab < this.getIndMayLabels() ){
            adaptExVarLab[ex][var][lab] = newVar;
            return ex;
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Get the value of adaptExVarLab
    * @param ex position of new value
    * @param var position of new value
    * @param lab position of new value
    * @return the value of partition, -1 if no valid position
    */
    public double getAdaptExVarLab (int ex, int var, int lab) {
//        if (adaptExVarLab.length ==0)
//            return -1;
//        else  if (ex >= 0 && ex < this.getNumExamples() &&
//            var >= 0 && var < this.getNumVariables() &&
//            lab >= 0 && lab < this.getIndMayLabels() ){

            return adaptExVarLab[ex][var][lab];
            
//        }
//        else{
//            return -1;
//        }
    }    
    
    /* ......................................................................... */

    /**
     * Set the ExampleSet with E for training (eliminate the partition "part")
     * @param E ExampleSet original
     * @param part partition not considered
     * @return part if no error; -1 otherwise
     * @deprecated used to set the training set in NSLV original
     */
    public int setTrainingSet(ExampleSetProcess E, int part){
        if (part < 0 || part > E.getNumPartitions()){
            return -1;
        }
        
        int numero=0;
        for (int i=0 ; i < E.getNumExamples(); i++){
            if (E.getPartition(i) != part){
                numero++;
            }
        }
        
        if (numero == 0){
            return -1;
        }

        this.setNumExamples(numero);
        this.setNumVariables(E.getNumVariables());
        covered= new int[this.getNumExamples()];
        indexRuleCovered= new int[this.getNumExamples()];
        lambdaPos= new double[this.getNumExamples()];
        lambdaNeg= new double[this.getNumExamples()];
        posWeight= new double[this.getNumExamples()];
        negWeight= new double[this.getNumExamples()];
        indexLambdaPos= new int[this.getNumExamples()];
        indexLambdaNeg= new int[this.getNumExamples()];
        this.setNumPartitions(E.getNumPartitions()-1);
        this.setPartition(new int[this.getNumExamples()]);
        this.setData(new double[this.getNumExamples()][this.getNumVariables()]);

        if (E.adaptExVarLab != null){
          this.indMayLabels= E.indMayLabels;
          this.adaptExVarLab= new double[numero][numVariables][this.indMayLabels];
        }
        
        
        int iSet=0;
        for (int i=0 ; i < E.getNumExamples(); i++){
            if (E.getPartition(i) != part){
                for (int j=0; j < this.getNumVariables(); j++){
                   this.setData(iSet,j, E.getData(i, j));
                }
                covered[iSet]= 0;
                indexRuleCovered[iSet]= -1;
                lambdaPos[iSet]= -1;
                lambdaNeg[iSet]= -1;
                posWeight[iSet]= -1;
                negWeight[iSet]= -1;
                indexLambdaPos[iSet]= -1;
                indexLambdaNeg[iSet]= -1;
                this.setPartition(iSet,E.getPartition(i));
                if (E.adaptExVarLab != null){
                    for (int k=0; k < numVariables; k++){
                      System.arraycopy(E.adaptExVarLab[i][k], 0, this.adaptExVarLab[iSet][k], 0, E.adaptExVarLab[i][k].length);
                    }
                }
                iSet++;
            }
        }
        return part;
    }
    
    /**
     * Set the ExampleSet with E for test (only the partition "part")
     * @param E ExampleSet original
     * @param part partition not considered
     * @return part if no error; -1 otherwise
     * @deprecated used to set the test set in NSLV original
     */
    public int setTestSet(ExampleSetProcess E, int part){
        if (part < 0 || part > E.getNumPartitions()){
            return -1;
        }
        
        int numero=0;
        for (int i=0 ; i < E.getNumExamples(); i++){
            if (E.getPartition(i) == part){
                numero++;
            }
        }
        
        if (numero == 0){
            return -1;
        }
        
        this.setNumExamples(numero);
        this.setNumVariables(E.getNumVariables());
        covered= new int[this.getNumExamples()];
        indexRuleCovered= new int[this.getNumExamples()];        
        lambdaPos= new double[this.getNumExamples()];
        lambdaNeg= new double[this.getNumExamples()];
        posWeight= new double[this.getNumExamples()];
        negWeight= new double[this.getNumExamples()];
        indexLambdaPos= new int[this.getNumExamples()];
        indexLambdaNeg= new int[this.getNumExamples()];
        this.setNumPartitions(1);
        this.setPartition(new int[this.getNumExamples()]);
        this.setData(new double[this.getNumExamples()][this.getNumVariables()]);

        if (E.adaptExVarLab != null){
          this.indMayLabels= E.indMayLabels;
          this.adaptExVarLab= new double[numero][numVariables][this.indMayLabels];
        }

        int iSet=0;
        for (int i=0 ; i < E.getNumExamples(); i++){
            if (E.getPartition(i) == part){
                for (int j=0; j < this.getNumVariables(); j++){
                   this.setData(iSet,j, E.getData(i, j));
                }
                covered[iSet]= 0;
                indexRuleCovered[iSet]= -1;
                lambdaPos[iSet]=-1;
                lambdaNeg[iSet]=-1;
                posWeight[iSet]=-1;
                negWeight[iSet]=-1;
                indexLambdaPos[iSet]=-1;
                indexLambdaNeg[iSet]=-1;                
                this.setPartition(iSet,E.getPartition(i));
                if (E.adaptExVarLab != null){
                    for (int k=0; k < numVariables; k++){
                      System.arraycopy(E.adaptExVarLab[i][k], 0, this.adaptExVarLab[iSet][k], 0, E.adaptExVarLab[i][k].length);
                    }
                }
                iSet++;
            }
            
        }
        return part;
    }
    
    /**
     * Calculate of simple probability of a variable
     * (
     * Calcula la probabilidad simple de la variable con índice variableIndex respecto al número de casos posibles numEtiquegasVariable
     * prob[etiquetaVariable]=(nº casos favorables, media de las adaptaciones de las etiquetas)
     *                        / (nº casos posibles, número de ejemplos no cubiertos)
     * )
     * @param variableIndex index of variable to consider
     * @return simple probability of variable
     */
    private double[] calcProbSimpleVar(int variableIndex){
        int numConsideredExamples;               
        double[] prob, aux;
        double sum=0, value;
        int numLabels= this.problemDefinition.getFuzzyLinguisticVariableList(variableIndex).getFuzzyLinguisticTermNum();
        
        prob= new double[numLabels];  // probabilidad simple final
        aux= new double[numLabels];

        numConsideredExamples=0;
        // inicializar las probabilidades de los ejemplos
        for (int i= 0; i < numLabels; i++){
            prob[i]=0;
        }
        
        // calcular la media de las adaptaciones como casos favorables
        for (int i=0; i < this.getNumExamples(); i++){
            if (this.getCovered(i) != 1){   // no está cubierto el ejemplo
                sum= 0;
                value= this.getData(i,variableIndex);
                for (int j= 0; j < numLabels; j++){
                    aux[j]= this.problemDefinition.getFuzzyLinguisticVariableList(variableIndex).normalizedAdaptation(value, j);
                    sum= sum + aux[j];
                }
                if (sum > 0){ // ha habido algún valor por la función de pertenencia -> actualizar prob
                    for (int j= 0; j < numLabels; j++){
                        prob[j]= prob[j] + (aux[j]/sum);
                    }
                    numConsideredExamples++;
                }
            }
        }
        
        if (numConsideredExamples > 0){ // actualizar la prob con el numero de ejemplos considerados
            for (int j= 0; j < numLabels; j++){
                prob[j]= prob[j] / numConsideredExamples;
            }
        }
        
        return prob;
    }
    
    /**
     * Calculate the likelihood of two variables
     * (
     * Calcula la probabilidad conjunta de 2 variables (producto cartesiano)
     * )
     * @param varInd1 index of first variable
     * @param varInd2 index of second variable
     * @return conjunt probability of variable1 over variable2
     */    
    private double[][] calcProbConj(int varInd1, int varInd2){
        int numConsideredExamples;               
        double[][] prob, aux;
        double sum=0, value1, value2;
        int numLabels1, numLabels2;
        double membership1, membership2;

        numLabels1= this.problemDefinition.getFuzzyLinguisticVariableList(varInd1).getFuzzyLinguisticTermNum();
        numLabels2= this.problemDefinition.getFuzzyLinguisticVariableList(varInd2).getFuzzyLinguisticTermNum();
        
        prob= new double[numLabels1][numLabels2];  // probabilidad final
        aux= new double[numLabels1][numLabels2];

        numConsideredExamples=0;
        // inicializar las probabilidades de los ejemplos
        for (int i= 0; i < numLabels1; i++){
            for (int j= 0; j < numLabels2; j++){
                prob[i][j]=0;                
            }
        }
        
        // calcular la media de las adaptaciones como casos favorables
        for (int i=0; i < this.getNumExamples(); i++){
            if (this.getCovered(i) != 1){   // no está cubierto el ejemplo
                sum= 0;
                value1= this.getData(i,varInd1);
                value2= this.getData(i,varInd2);
                for (int j= 0; j < numLabels1; j++){
                    for (int k=0; k < numLabels2; k++){
                        membership1= this.problemDefinition.getFuzzyLinguisticVariableList(varInd1).normalizedAdaptation(value1, j);
                        membership2= this.problemDefinition.getFuzzyLinguisticVariableList(varInd2).normalizedAdaptation(value2, k);
                        aux[j][k]= membership1 * membership2;
                        sum= sum + aux[j][k];
                    }
                }
                if (sum > 0){ // ha habido algún valor por la función de pertenencia -> actualizar prob
                    for (int j= 0; j < numLabels1; j++){
                        for (int k=0; k < numLabels2; k++){
                            prob[j][k]= prob[j][k] + (aux[j][k]/sum);
                        }                        
                    }
                    numConsideredExamples++;
                }
            }
        }
        
        if (numConsideredExamples > 0){ // actualizar la prob con el numero de ejemplos considerados
            for (int j= 0; j < numLabels1; j++){
                for (int k= 0; k < numLabels2; k++){
                    prob[j][k]= prob[j][k] / numConsideredExamples;
                }
                
            }
        }
        
        return prob;        
    }

    /**
     * Calculate of information measures based in probabilities (likelihood)
     * Use Shannon entropy and Kullback-Leibler divergence
     * (
     * Calcula la matriz de medidas de información para agilizar los cálculos
     * Es una matriz de [numVariablesActivasAntecedentes][numClases+1]
     * En la columna numClases se encuentra la medida general sobre todas las clases
     * En la columna i se encuentra la medida para la clase i
     * Usa la entropía de Shannón y la curvas de Kullback-Leibler (divergencia de)
     * )
     * @use calcProbSimpleVar()
     * @use calcProbConj2Var(var1, var2)
     */
    protected void calcInformationMeasures(){
        double[] px, py;
        double[][] pxy;
        double[][] I;
        double I1=0; //I1=divergencia de Kullback-Leibler para todas las combinaciones de "x"(variables antecedentes) e "y"(variables consecuentes)
        double H1=0; //H1=entropía de Shannon para todas las combinaciones de "x"(variables antecedentes) e "y"(variables consecuentes)
        double I2=0; //I2=divergencia de Kullback-Leibler para el cálculo de cada "x"(variables antecedentes) y todas las "y"(variables consecuentes)
        double H2=0; //H2=entropía de Shannon para el cálculo de cada "x"(variables antecedentes) y todas las "y"(variables consecuentes)
        double aux=0;
        int indexConseq;
        int numLabelsVar, numLabelsConseq;
        
        // reservar memoria para la matriz
        int numVariablesAntecedentes, numClasesMas1;
        numVariablesAntecedentes= problemDefinition.numAntecedentVariables();
        numClasesMas1= problemDefinition.numLinguisticTermOfConsequent()+1;
        this.informationMeasures= new double[numVariablesAntecedentes][numClasesMas1];

        // realizar el cálculo
        numLabelsConseq= this.problemDefinition.numLinguisticTermOfConsequent();
        
        indexConseq= this.problemDefinition.consequentIndex();
        py= calcProbSimpleVar(indexConseq);
        
        int j=0;
        for (int i=0; i < this.getNumVariables(); i++){
            if (this.problemDefinition.getFuzzyLinguisticVariableList(i).getVariableType() == 0){
                
                px= calcProbSimpleVar(i);
                pxy= calcProbConj(i,indexConseq);
                I1= 0;
                H1= 0;
                for (int k= 0; k < numLabelsConseq; k++){
                    I2= 0;
                    H2= 0;
                    numLabelsVar= this.problemDefinition.getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum();
                    for (int q= 0; q < numLabelsVar; q++){
                        if (pxy[q][k]==0){
                            aux=0;
                        }
                        else{// divergencia de Kullback-Leibler
                            aux=pxy[q][k] * Math.log((px[q]*py[k])/pxy[q][k]);                            
                        }
                        I2=I2-aux;  // divergencia de Kullback-Leibler
                        I1=I1-aux;

                        if (pxy[q][k]==0){
                            aux=0;
                        }
                        else{// entropía de Shannon
                            aux=pxy[q][k] * Math.log(pxy[q][k]);
                        }

                        H2=H2-aux;  // entropía de Shannon
                        H1=H1-aux;
                    }

                    if  (H2==0){
                        this.informationMeasures[j][k]=0;
                    }
                    else{
                        this.informationMeasures[j][k]=(I2/H2);
                    }                    
                }// for (int k= 0; k < numLabelsConseq; k++){
                
                if (H1==0){
                    this.informationMeasures[j][numLabelsConseq]=0;
                }
                else{
                    this.informationMeasures[j][numLabelsConseq]=(I1/H1);                
                }
                j++;
            }//if (this.problemDefinition.getFuzzyLinguisticVariableList(i).getConsequent()== 0){
        }//for (int i=0; i < this.numVariables; i++){
    }//public void calcInformationMeasures(){
    

    /**
     * Calculate matrix of adaptations of all examples, all variables and all labels for optimization
     * (
     * Calcula la matriz de adaptaciones adaptExVarLab para agilizar los cálculos
     * Es una matriz de [numExamples][numVariables][numMaxLabels] que indica 
     * la adaptación del ejemplo "e" con la variable "v" a la etiqueta "l"
     * )
     * @return "" no error; string with error (if error)
     */
    protected String calcAdaptExVarLab(){
        String result = "";
        double valAdapt, valExample;
        int numValNoCero=0;

        // reservar memoria para la matriz
        // cálculo del máximo de etiquetas que tienen las variables para la reserva de la matriz de adaptaciones
        this.indMayLabels= this.problemDefinition.getFuzzyLinguisticVariableList(0).getFuzzyLinguisticTermNum();
        this.setNumVariables(this.problemDefinition.getFuzzyLinguisticVariableNum());
        int index;
        for (int i= 1; i < this.getNumVariables(); i++){
            index= this.problemDefinition.getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum();
            if (index > indMayLabels){
                this.indMayLabels= index;
            }            
        }

//          //Este trozo es para ver la memoria que consume la matriz para las adaptaciones
//          String auxString= "Matriz de :" + this.getNumExamples() +" * " + 
//                  this.getNumVariables() + " * " + this.indMayLabels + " = " +
//                  this.getNumExamples()*this.getNumVariables()*this.indMayLabels + 
//                  " considerando que un double tiene 64bits, en total hay " +
//                  this.getNumExamples()*this.getNumVariables()*this.indMayLabels*64 +
//                  " bits reservados en memoria (" + 
//                  this.getNumExamples()*this.getNumVariables()*this.indMayLabels*64/(1024) +
//                  " Kb, " +
//                  this.getNumExamples()*this.getNumVariables()*this.indMayLabels*64/(1024*1024) +
//                  " Mb, " +
//                  this.getNumExamples()*this.getNumVariables()*this.indMayLabels*64/(1024*1024*1024) +
//                  " Gb)";
//          Runtime runtime = Runtime.getRuntime();
//          DebugClass.printMemoryUsage(auxString+"\n ANTES DE COGER MEMORIA ",1,1);
//          // Run the garbage collector
//          runtime.gc();
//          DebugClass.printMemoryUsage("\n Después de gc",1,1);
//          //FIN - Este trozo es para ver la memoria que consume la matriz para las adaptaciones
            
        adaptExVarLab= new double[this.getNumExamples()][this.getNumVariables()][this.indMayLabels];

//          //Este trozo es para ver la memoria que consume la matriz para las adaptaciones
//          DebugClass.printMemoryUsage("\n DESPUÉS DE COGER MEMORIA ",1,1);
//          // Run the garbage collector
//          runtime.gc();
//          DebugClass.printMemoryUsage("\n Después de gc",1,1);
//          //FIN - Este trozo es para ver la memoria que consume la matriz para las adaptaciones
            
        // calculo de la matriz
        for (int i=0; i < this.getNumExamples(); i++){
            for (int j=0; j < this.getNumVariables(); j++){
                numValNoCero= 0;                
                valExample= this.getData(i, j);
                int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();                
                for (int k=0; k < numLabels; k++){
                    valAdapt= this.getProblemDefinition().getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).adaptation(valExample);
                    this.setAdaptExVarLab(i,j,k,valAdapt);
                    if (valAdapt != 0){
                        numValNoCero++;
                    }                    
                }
                for (int k= numLabels; k < this.indMayLabels; k++){
                    this.setAdaptExVarLab(i,j,k,-1);
                     
                }
                if (numValNoCero == 0){
                    result = "\n\n\t EXAMPLE " + i + " with variable " + j + " and value " + valExample + " WITHOUT ADAPTATION\n\n";
                    String aux="ExampleSetProcess.calcAdaptExVarLab \n\n\n"+result;
                    DebugClass.sendMail=1;
                    DebugClass.cuerpoMail+= "\n" + aux;
                    DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug);
//                    DebugClass.sendMail("nslv execution",aux,DebugClass.fileResultDebug+0);
                    System.exit(-1);
                }
            }
        }        
        return result;
    }
    
    protected String calcAdaptExVarLabTFG(){
        String result = "";
        double valAdapt, valExample;
        int numValNoCero=0;

        // reservar memoria para la matriz
        // cálculo del máximo de etiquetas que tienen las variables para la reserva de la matriz de adaptaciones
        this.indMayLabels= this.problemDefinition.getFuzzyLinguisticVariableList(0).getFuzzyLinguisticTermNum();
        this.setNumVariables(this.problemDefinition.getFuzzyLinguisticVariableNum());
        int index;
        for (int i= 1; i < this.getNumVariables(); i++){
            index= this.problemDefinition.getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum();
            if (index > indMayLabels){
                this.indMayLabels= index;
            }            
        }

        adaptExVarLab= new double[this.getNumExamples()][this.getNumVariables()][this.indMayLabels];
            
        // calculo de la matriz
        for (int i=0; i < this.getNumExamples(); i++){
            for (int j=0; j < this.getNumVariables(); j++){
                numValNoCero= 0;                
                valExample= this.getData(i, j);
                int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();                
                for (int k=0; k < numLabels; k++){
                    valAdapt= this.getProblemDefinition().getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermList(k).adaptation(valExample);
                    this.setAdaptExVarLab(i,j,k,valAdapt);
                    if (valAdapt != 0){
                        numValNoCero++;
                    }                    
                }
                for (int k= numLabels; k < this.indMayLabels; k++){
                    this.setAdaptExVarLab(i,j,k,-1);
                     
                }
                if (numValNoCero == 0){
                    result = "ERROR";
                }
            }
        }        
        return result;
    }
    
    /**
     * Get the index of label with better adaptation of example to variable
     * (
     * Devuelve la etiqueta con mayor adaptación a la variable en ese ejemplo
     * )
     * @param example to consider the value
     * @param variable to consider the labels
     * @return index of label that get better adaptation to value, (if tie (empate) return the first)
     */
    public int getBetterLabel(int example, int variable){
        int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(variable).getFuzzyLinguisticTermNum();
        int label=0;
        double max= this.getAdaptExVarLab(example,variable,0);
        double aux;

        for (int i=1; i < numLabels; i++){
            aux=this.getAdaptExVarLab(example,variable, i); 
            if ( aux > max ){
                max= aux;
                label= i;
            }
        }
        
        return label;
    }
    
    /**
     * get the index of the two labels with better adaptation of example to variable
     * (
     * Devuelve las 2 mejores etiquetas con mayor adaptación a la variable en ese ejemplo
     * )
     * @param example to consider the value
     * @param variable to consider the labels
     * @return matrix of index of label that get better adaptation to value, 
     * if tie (empate) return the first
     * if mayor adaptation is 0 -> return -1
     * @deprecated no used yet (will be used in future)
     */
    public int[] getBetterTwoLabels(int example, int variable){
        int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(variable).getFuzzyLinguisticTermNum();
        int[] label= new int[2]; // para almacenar las dos mejores etiquetas
        double max1=0, max2=0;
        double aux;

        label[0]= label[1]= -1;
        
        for (int i=0; i < numLabels; i++){
            aux=this.getAdaptExVarLab(example,variable, i); 
            if ( aux > max1 ){
              max2= max1;
              label[1]= label[0];
              max1= aux;
              label[0]= i;
            }
            else if (aux > max2){
              max2= aux;
              label[1]= i;
            }
        }
        return label;
    }    
    
    /**
     * Get a vector with the number of examples NOT COVERED YET of each class
     * (
     * Devuelve un vector con el numéro de ejemplos (no cubiertos hasta ahora) 
     * de cada una de las clases
     * Un ejemplo se considera de la clase "i" si sobre la i-ésima clase tiene 
     * el mayor valor de adaptación (función de pertenencia)
     * En caso de empate, se considera que el ejemplo pertenece a la clase de
     * menor índice "i"
     * )
     * @return vector with number of examples (not covered) of each class
     * @use getBetterLabel
     */
    public int[] getNumExamNotCoveredXClass(){
        int numClases= this.getProblemDefinition().numLinguisticTermOfConsequent();
        int numExamples= this.getNumExamples();
        int varCons= this.getProblemDefinition().consequentIndex();
        int[] result= new int[numClases];
        int label;
        
        for (int i=0; i < numClases; i++){
            result[i]= 0;
        }
        
        for (int i=0; i < numExamples; i++){
            if (this.getCovered(i) != 1){ // ejemplo no cubierto
                label= this.getBetterLabel(i,varCons);
                result[label]++;
            }
        }
        return result;  
    }
    
    /**
     * Get a vector with the number of examples of each label of a determined variable
     * (
     * Devuelve un vector con el numéro de ejemplos que pertenecen a cada etiqueta
     * indicado un índice de la variable.
     * )
     * @param varIndex index of variable to consider
     * @return vector with number of examples of each label of a determined variable
     * @use getBetterLabel
     */
    public int[] getNumExamXLabel(int varIndex){
        int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(varIndex).getFuzzyLinguisticTermNum();
        int numExamples= this.getNumExamples();
        int[] result= new int[numLabels];
        int maxIndex= -1;
        double max=0, maxAux;
        
        for (int i=0; i < numLabels; i++){
            result[i]= 0;
        }

        for (int i=0; i < numExamples; i++){
          max= this.getAdaptExVarLab(i, varIndex, 0);
          maxIndex=0;
          for (int j=1; j < numLabels; j++){
            maxAux= this.getAdaptExVarLab(i, varIndex, j);
            if (maxAux >= 0.5){
              max= maxAux;
              maxIndex= j;
              j= numLabels;
            }
          }
          result[maxIndex]++;
        }
        return result;  
    }

  /**
   * calculate the normalized adaptation of all antecedents variables of individual (rule) to the example 
   * (
   * calcula la adaptación NORMALIZADA del ejemplo "e" al antecedente de la regla
   * )
   * @param e example used to calculate its adaptation to the individual (rule)
   * @param indiv individual (rule) used in the adaptation
   * @return máximal (normalized) adaptation of all antecedents variables to the example
   */
  public double calcAdaptAntNormalized(int e, GenetCodeClass indiv){

    int numVariablesAntecedentes= problemDefinition.numAntecedentVariables();
    int numLabels, indexLabel;
    /* las líneas serían equivalentes a las siguientes (que era lo original de Raúl)
    int tamBloc= indiv.getSizeRealBlocs(0);
    double infMeasureClass= indiv.getRealMatrix(0, tamBloc-1); */
    int numVariables= this.getProblemDefinition().getFuzzyLinguisticVariableNum();
    double infMeasureClass= indiv.getRealMatrix(0, numVariables-1);
    double adaptVar=1, adaptLabel=0, adaptActualLabel, maxAdaptLabel;
    // adaptVar guardará el mínimo de las adaptaciones de las variables (considerando todas las etiquetas)
    // adapLabel guardará el máximo de las adaptaciones de las etiquetas a la variable que se está considerando
    double actInfMeasure;
  
    indexLabel=0;
    for (int v=0; v < numVariablesAntecedentes && adaptVar > 0; v++){
      numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
      actInfMeasure= indiv.getRealMatrix(0,v);
      if (actInfMeasure >= infMeasureClass){ // tiene sentido considerar la variable
        adaptLabel=0;
        maxAdaptLabel=0;
        for (int l=0; l < numLabels && adaptLabel < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
          if (maxAdaptLabel < this.getAdaptExVarLab(e,v,l)){
            maxAdaptLabel= this.getAdaptExVarLab(e,v,l);
          }
          int considLabel= indiv.getBinaryMatrix(0, indexLabel+l);
          if (considLabel == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
            // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
            // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
            adaptActualLabel= this.getAdaptExVarLab(e,v,l);
            if (adaptLabel == 0){ // no tiene adaptación con una etiqueta anterior
              adaptLabel= adaptActualLabel;
            }
            else if (adaptActualLabel > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
              adaptLabel= 1;
            }            
          }
        }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
        if (adaptLabel < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
          adaptLabel= adaptLabel / maxAdaptLabel;
        }
        if (adaptLabel < adaptVar){ // coger el mínimo de las adaptaciones como adapt del antecedente
          adaptVar= adaptLabel;
        }        
      }//if (actInfMeasure >= infMeasureClass){ // tiene sentido considerar la variable
      indexLabel= indexLabel + numLabels;
    } //for (int i=0; i < numVariablesAntecedentes; i++){
    return adaptVar;
  }

  /**
   * calculate the normalized adaptation of all antecedents variables of individual (rule) to the example 
   * with umbral
   * The calculate stops when the adaptation is lower or equal to umbral
   * Is used in the calculate with neighbours because the adaptation can't be lower that umbral
   * @param e example used to calculate its adaptation to the individual (rule)
   * @param indiv individual (rule) used in the adaptation
   * @param umbral min umbral to stop the calculate 
   * @return máximal (normalized) adaptation of all antecedents variables to the example
   */
  public double calcAdaptAntNormalizedUmbral(int e, GenetCodeClass indiv, double umbral){

    int numVariablesAntecedentes= problemDefinition.numAntecedentVariables();
    int numLabels, indexLabel;
    /* las líneas serían equivalentes a las siguientes (que era lo original de Raúl)
    int tamBloc= indiv.getSizeRealBlocs(0);
    double infMeasureClass= indiv.getRealMatrix(0, tamBloc-1); */
    int numVariables= this.getProblemDefinition().getFuzzyLinguisticVariableNum();
    double infMeasureClass= indiv.getRealMatrix(0, numVariables-1);
    double adaptVar=1, adaptLabel=0, adaptActualLabel, maxAdaptLabel;
    // adaptVar guardará el mínimo de las adaptaciones de las variables (considerando todas las etiquetas)
    // adapLabel guardará el máximo de las adaptaciones de las etiquetas a la variable que se está considerando
    double actInfMeasure;
  
    indexLabel=0;
    for (int v=0; v < numVariablesAntecedentes && adaptVar > umbral; v++){
      numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
      actInfMeasure= indiv.getRealMatrix(0,v);
      if (actInfMeasure >= infMeasureClass){ // tiene sentido considerar la variable
        adaptLabel=0;
        maxAdaptLabel=0;
        for (int l=0; l < numLabels && adaptLabel < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
          if (maxAdaptLabel < this.getAdaptExVarLab(e,v,l)){
            maxAdaptLabel= this.getAdaptExVarLab(e,v,l);
          }
          int considLabel= indiv.getBinaryMatrix(0, indexLabel+l);
          if (considLabel == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
            // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
            // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
            adaptActualLabel= this.getAdaptExVarLab(e,v,l);
            if (adaptLabel == 0){ // no tiene adaptación con una etiqueta anterior
              adaptLabel= adaptActualLabel;
            }
            else if (adaptActualLabel > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
              adaptLabel= 1;
            }            
          }
        }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
        if (adaptLabel < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
          adaptLabel= adaptLabel / maxAdaptLabel;
        }
        if (adaptLabel < adaptVar){ // coger el mínimo de las adaptaciones como adapt del antecedente
          adaptVar= adaptLabel;
        }        
      }//if (actInfMeasure >= infMeasureClass){ // tiene sentido considerar la variable
      indexLabel= indexLabel + numLabels;
    } //for (int i=0; i < numVariablesAntecedentes; i++){
    return adaptVar;
  }

  public double calcAdaptVar(int e, GenetCodeClass indiv, int indexVar, int indexLabel){
      // calcular la adaptación de la variable modificada en el individuo original
      double adaptVar=0, maxAdaptVar=0, adaptActualVar=0;
      int considVar=0;
      int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(indexVar).getFuzzyLinguisticTermNum();
      
      for (int l=0; l < numLabels && adaptVar < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
        if (maxAdaptVar < this.getAdaptExVarLab(e,indexVar,l)){
          maxAdaptVar= this.getAdaptExVarLab(e,indexVar,l);
        }
        considVar= indiv.getBinaryMatrix(0, indexLabel+l);
        if (considVar == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
          // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
          // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
          adaptActualVar= this.getAdaptExVarLab(e,indexVar,l);
          if (adaptVar == 0){ // no tiene adaptación con una etiqueta anterior
            adaptVar= adaptActualVar;
          }
          else if (adaptActualVar > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
            adaptVar= 1;
          }            
        }
      }// for (int l=0; l < numVars && adaptVar < 1; l++){
      if (adaptVar < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
          adaptVar= adaptVar / maxAdaptVar;
      }
      return adaptVar;
      // FIN - calcular la adaptación de la variable modificada en el individuo original
  }


  /**
   * calculate the normalized adaptation of all antecedents variables of
   * individual Final (rule) to the example e using the adaptation of all 
   * antecedents variables of individual Original (rule) to the example
   * (
   * calcula la adaptación NORMALIZADA del ejemplo "e" al antecedente de la regla
   * pero considerando la adaptación del individuo vecino de distancia de hamming 1
   * )
   * @param e example used to calculate its adaptation to the individual (rule)
   * @param indivOrig individual Original (rule) with adaptation calculated
   * @param indivFin individual Final (rule) which adaptation we are going to calculate
   * @return máximal (normalized) adaptation of all antecedents variables to the example
   */
  public double calcAdaptAntXNeighbour(int e, GenetCodeClass indivOrig, GenetCodeClass indivFin){

    double adaptAntOrig= indivOrig.getRealMatrix(1, e);
    // de esta forma se indicará cuál es la variable que ha cambiado respecto al vecino anterior
    int varModificada= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,0);
    int indexModificado= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,1);
    int ele1Mask= indivOrig.getBinaryMatrix(1, indexModificado);
    int ele2Mask= indivFin.getBinaryMatrix(1, indexModificado);

    double adaptVarOrig=0, adaptVarFin=0;
    
    if (ele1Mask == 1 && ele2Mask == 1){ // en el indivOrig NO y en el indivFin NO interviene la variable modificada -> de devuelve la adapt original
      return adaptAntOrig;
    }
    else if (ele1Mask == 1 && ele2Mask == 0){ // en el indivOrig NO interviene y en el indivFin SI -> realizar los cálculos de fin y devolver en funcion de lo obtenido
      adaptVarFin= calcAdaptVar(e, indivFin, varModificada, indexModificado);
      if (adaptVarFin <= adaptAntOrig){ // esta adaptacion es menor que la de todas las variables anteriores -> se devuelve esta adaptacion
          // min ... adaptAntOrig..=.. adaptLabelOrig.... max      (adaptAntOrig <= adaptLabelOrig) (siempre)
          // min ... adaptLabelFin..=..adaptAntOrig... max
          // conclusion:
          // min ... adaptLabelFin..=..adaptAntOrig..=..adaptLabelOrig ... max
          // esta nueva variable minimiza la adapt al antece 
        return adaptVarFin;
      }// el else no hace falta ya que la nueva adaptación es peor
      else{
        return adaptAntOrig;
      }
    }
    else if (ele1Mask == 0 && ele2Mask == 1){ // en el indivOrig SI y en el indivFin NO -> hay que ver si era el condicionante de la adaptacion o no ... y devolver en función
      adaptVarOrig= calcAdaptVar(e, indivOrig, varModificada, indexModificado);      
      if (adaptVarOrig <= adaptAntOrig){ // en la adapt del Antecedente intervenía esta variable -> hay que volver a calcular la adaptacion completa
        double aux= calcAdaptAntNormalizedUmbral(e, indivFin, adaptAntOrig);
        return aux;
      }// el else no hace falta ya que antes no intervenía esta variable en la adaptacion del antecedente
      else{
        return adaptAntOrig;
      }
    }
    else{// (ele1Mask == 0 && ele2Mask == 0) // en el indivOrig SI y en el indivFin SI -> realizar los cálculos y devolver en funcion ...
      adaptVarFin= calcAdaptVar(e, indivFin, varModificada, indexModificado);
      adaptVarOrig= calcAdaptVar(e, indivOrig, varModificada, indexModificado);      
      if (adaptVarFin <= adaptAntOrig){ // esta adaptacion es menor que la de todas las variables anteriores -> se devuelve esta adaptacion
        return adaptVarFin;
      }
      else if (adaptVarOrig <= adaptAntOrig) { // la nueva adapt no es la menor, pero si en la adapt del antecedente intervenía esta variable -> hay que volver a calcular la adaptacion completa
        double aux= calcAdaptAntNormalizedUmbral(e, indivFin, adaptAntOrig);
        return aux;        
      } // el else no hace falta ya que la nueva adaptacion es peor y en la adapt del antecedente no intervenia esta variable
      else{
        return adaptAntOrig;
      }      
    }
        
    
//ESTA ES OTRA VERSIÓN, PERO NO VA MUY BIEN    
//    int numVariables= this.getProblemDefinition().getFuzzyLinguisticVariableNum();
//    double adaptAntOrig= indivOrig.getRealMatrix(1, e);
//    // de esta forma se indicará cuál es la variable que ha cambiado respecto al vecino anterior
//    int varModificada= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,0);
//    int indexModificado= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,1);
//    double actInfMeasureBegin= indivFin.getRealMatrix(indivFin.getRealBlocs()-1,2); // medida de la variable en el individuo origen
//    double infMeasureClassBegin= indivFin.getRealMatrix(indivFin.getRealBlocs()-1,3); // medida de la clase en el individuo origen
//    double actInfMeasureEnd= indivFin.getRealMatrix(indivFin.getRealBlocs()-1,4); // medida de la variable en el individuo destino
//    double infMeasureClassEnd= indivFin.getRealMatrix(indivFin.getRealBlocs()-1,5); // medida de la clase en el individuo destino
//
//    int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(varModificada).getFuzzyLinguisticTermNum();
//    double adaptVarOrig=0, adaptLabelOrig=0, adaptVarFin=0, adaptLabelFin=0;
//    double adaptActualLabelOrig=0, adaptActualLabelFin=0, maxAdaptLabel=0; 
//    int considLabelOrig=0, considLabelFin=0;
//
//    
//    if (actInfMeasureEnd < infMeasureClassEnd){ // no interviene -> se devuelve lo mismo que antes
//      return adaptAntOrig;
//    }
//    else{
//      if (actInfMeasureBegin < infMeasureClassBegin){ // ahora si interviene, pero antes no intervenía
//        // calcular la adaptación de la variable modificada en el individuo original
//        adaptLabelFin=0;
//        maxAdaptLabel=0;
//        for (int l=0; l < numLabels && adaptLabelFin < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
//          if (maxAdaptLabel < this.getAdaptExVarLab(e,varModificada,l)){
//            maxAdaptLabel= this.getAdaptExVarLab(e,varModificada,l);
//          }
//          considLabelFin= indivFin.getBinaryMatrix(0, indexModificado+l);
//          if (considLabelFin == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
//            // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
//            // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
//            adaptActualLabelFin= this.getAdaptExVarLab(e,varModificada,l);
//            if (adaptLabelFin == 0){ // no tiene adaptación con una etiqueta anterior
//              adaptLabelFin= adaptActualLabelFin;
//            }
//            else if (adaptActualLabelFin > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
//              adaptLabelFin= 1;
//            }            
//          }
//        }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
//        if (adaptLabelFin < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
//            adaptLabelFin= adaptLabelFin / maxAdaptLabel;
//        }
//        // FIN - calcular la adaptación de la variable modificada en el individuo original
//        
//        // realizar las comprobaciones para ver si tengo que volver a calcular la adaptacion
//        if (adaptLabelFin <= adaptAntOrig){ // esta nueva variable minimiza la adapt al antece 
//          // min ... adaptAntOrig..=.. adaptLabelOrig.... max      (adaptAntOrig <= adaptLabelOrig) (siempre)
//          // min ... adaptLabelFin..=..adaptAntOrig... max
//          // conclusion:
//          // min ... adaptLabelFin..=..adaptAntOrig..=..adaptLabelOrig ... max
//          // esta nueva variable minimiza la adapt al antece 
//          return adaptLabelFin;
//        }
//        else{ // ahora no minimiza pero antes sí, por lo que hay que ver si hay otra variable que minimice la adapt 
//          // -> volver a calcularla pero en cuanto consigamos una adapt == adaptAntOrig salimos
//          double aux= calcAdaptAntNormalizedUmbral(e, indivFin, adaptAntOrig);
//    //DebugClass.writeResFile("../results.txt0", "se calcula con calcAdaptAntXVecinosUmbral: " + adaptAntOrig);
//    //System.out.println("se calcula con calcAdaptAntXVecinosUmbral: " + adaptAntOrig);
//          return aux;
//        }
//      }//if (actInfMeasureBegin < infMeasureClassBegin){ // ahora si interviene, pero antes no intervenía
//      else{ // ahora si interviene y antes también intervenía
//        // calcular la adaptación de la variable modificada en el individuo original y el final
//        adaptLabelOrig=0;
//        adaptLabelFin=0;
//        maxAdaptLabel=0;
//        for (int l=0; l < numLabels && adaptLabelOrig < 1 && adaptLabelFin < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
//          if (maxAdaptLabel < this.getAdaptExVarLab(e,varModificada,l)){
//            maxAdaptLabel= this.getAdaptExVarLab(e,varModificada,l);
//          }
//          considLabelOrig= indivOrig.getBinaryMatrix(0, indexModificado+l);
//          if (considLabelOrig == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
//            // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
//            // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
//            adaptActualLabelOrig= this.getAdaptExVarLab(e,varModificada,l);
//            if (adaptLabelOrig == 0){ // no tiene adaptación con una etiqueta anterior
//              adaptLabelOrig= adaptActualLabelOrig;
//            }
//            else if (adaptActualLabelOrig > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
//              adaptLabelOrig= 1;
//            }            
//          } 
//          considLabelFin= indivFin.getBinaryMatrix(0, indexModificado+l);
//          if (considLabelFin == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
//            // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
//            // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
//            adaptActualLabelFin= this.getAdaptExVarLab(e,varModificada,l);
//            if (adaptLabelFin == 0){ // no tiene adaptación con una etiqueta anterior
//              adaptLabelFin= adaptActualLabelFin;
//            }
//            else if (adaptActualLabelFin > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
//              adaptLabelFin= 1;
//            }            
//          }
//        }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
//        if (adaptLabelOrig < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
//          adaptLabelOrig= adaptLabelOrig / maxAdaptLabel;
//        }
//        if (adaptLabelFin < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
//            adaptLabelFin= adaptLabelFin / maxAdaptLabel;
//        }
//        // FIN - calcular la adaptación de la variable modificada en el individuo original y el final
//
//        // realizar las comprobaciones para ver si tengo que volver a calcular la adaptacion
//        if (adaptLabelFin <= adaptAntOrig){ // esta nueva variable minimiza la adapt al antece 
//          // min ... adaptAntOrig..=.. adaptLabelOrig.... max      (adaptAntOrig <= adaptLabelOrig) (siempre)
//          // min ... adaptLabelFin..=..adaptAntOrig... max
//          // conclusion:
//          // min ... adaptLabelFin..=..adaptAntOrig..=..adaptLabelOrig ... max
//          // esta nueva variable minimiza la adapt al antece 
//          return adaptLabelFin;
//        }
//        else if (adaptAntOrig < adaptLabelOrig){ // antes no minimizaba la adapt (y ahora tampoco porque viene de lo anterior)
//          return adaptAntOrig;      
//        }
//        else{ // ahora no minimiza pero antes sí, por lo que hay que ver si hay otra variable que minimice la adapt 
//          // -> volver a calcularla pero en cuanto consigamos una adapt == adaptAntOrig salimos
//          double aux= calcAdaptAntNormalizedUmbral(e, indivFin, adaptAntOrig);
//    //DebugClass.writeResFile("../results.txt0", "se calcula con calcAdaptAntXVecinosUmbral: " + adaptAntOrig);
//    //System.out.println("se calcula con calcAdaptAntXVecinosUmbral: " + adaptAntOrig);
//          return aux;
//        }
//        
//      }
//    }
// FIN - ESTA ES OTRA VERSIÓN, PERO NO VA MUY BIEN        
    
// ESTA VERSION ES PARA TENER EN CUENTA LAS MEDIDAS DE ADAPTACION (PERO HAY QUE VOLVER A REPENSARLO)    
//    int numVariables= this.getProblemDefinition().getFuzzyLinguisticVariableNum();
//    double adaptAntOrig= indivOrig.getRealMatrix(1, e);
//    // de esta forma se indicará cuál es la variable que ha cambiado respecto al vecino anterior
//    int varModificada= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,0);
//    int indexModificado= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,1);
//
//    double infMeasureClassOrig= indivOrig.getRealMatrix(0, numVariables-1);
//    double infMeasureClassFin= indivFin.getRealMatrix(0, numVariables-1);
//    double actInfMeasureOrig= indivOrig.getRealMatrix(0,varModificada);
//    double actInfMeasureFin= indivFin.getRealMatrix(0,varModificada);
//
//    int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(varModificada).getFuzzyLinguisticTermNum();
//    double adaptVarOrig=0, adaptLabelOrig=0, adaptVarFin=0, adaptLabelFin=0;
//    double adaptActualLabelOrig=0, adaptActualLabelFin=0, maxAdaptLabelOrig=0, maxAdaptLabelFin=0; 
//
//    // si esta variable intervenía en la adaptación original se calcula la adaptación de esa variable
//    if (actInfMeasureOrig >= infMeasureClassOrig){ // tiene sentido considerar la variable
//      // calcular la adaptación de la variable modificada en el individuo original
//      adaptLabelOrig=0;
//      maxAdaptLabelOrig=0;
//      for (int l=0; l < numLabels && adaptLabelOrig < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
//        if (maxAdaptLabelOrig < this.getAdaptExVarLab(e,varModificada,l)){
//          maxAdaptLabelOrig= this.getAdaptExVarLab(e,varModificada,l);
//        }
//        int considLabel= indivOrig.getBinaryMatrix(0, indexModificado+l);
//        if (considLabel == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
//          // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
//          // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
//          adaptActualLabelOrig= this.getAdaptExVarLab(e,varModificada,l);
//          if (adaptLabelOrig == 0){ // no tiene adaptación con una etiqueta anterior
//            adaptLabelOrig= adaptActualLabelOrig;
//          }
//          else if (adaptActualLabelOrig > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
//            adaptLabelOrig= 1;
//          }            
//        } 
//      }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
//      if (adaptLabelOrig < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
//        adaptLabelOrig= adaptLabelOrig / maxAdaptLabelOrig;
//      }
//    }//if (actInfMeasureOrig >= infMeasureClassOrig){ // tiene sentido considerar la variable
//    else{
//      adaptLabelOrig=1; //para que no se considere
//    }
//    if (actInfMeasureFin >= infMeasureClassFin){ // tiene sentido considerar la variable
//      // calcular la adaptación de la variable modificada en el individuo final
//      adaptLabelFin=0;
//      maxAdaptLabelFin=0;
//      // hay que recorrer las etiquetas mientras que la adaptación de la etiqueta (adaptLabel)
//      // de la variable que estamos considerando sea menor que la adaptación 
//      // del antecedente completo de la regla de la que es vecino 
//      // ya que al final se toma el mínimo de las adaptaciones de las etiquetas
//      // y si esta variable consigue una adaptación mayor no se tendrá en cuenta
//      for (int l=0; l < numLabels && adaptLabelFin < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
//        if (maxAdaptLabelFin < this.getAdaptExVarLab(e,varModificada,l)){
//          maxAdaptLabelFin= this.getAdaptExVarLab(e,varModificada,l);
//        }
//        int considLabel= indivFin.getBinaryMatrix(0, indexModificado+l);
//        if (considLabel == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
//          // un ejemplo sólo puede adaptarse con 2 etiquetas -> si se ha adaptado con 1
//          // la adapt es != 0 -> si la adapt es != 0 --> la adaptNormalizada es 1
//          adaptActualLabelFin= this.getAdaptExVarLab(e,varModificada,l);
//          if (adaptLabelFin == 0){ // no tiene adaptación con una etiqueta anterior
//            adaptLabelFin= adaptActualLabelFin;
//          }
//          else if (adaptActualLabelFin > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
//            adaptLabelFin= 1;
//          }            
//        }
//      }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
//      if (adaptLabelFin < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
//          adaptLabelFin= adaptLabelFin / maxAdaptLabelFin;
//      }
//    }//if (actInfMeasureFin >= infMeasureClassFin && actInfMeasureOrig >= infMeasureClassOrig){ // tiene sentido considerar la variable
//    else{
//      adaptLabelFin=1; // para que no se considere
//    }
//    
////    if (adaptLabelFin == -1){  
////      // esta nueva variable no es considerada en la adaptación, se devuelve la anterior
////      return adaptAntOrig;
////    }
////      else if (adaptLabelFin <= adaptAntOrig){ // esta nueva variable minimiza la adapt al antece 
//    if (adaptLabelFin <= adaptAntOrig){ // esta nueva variable minimiza la adapt al antece 
//      // min ... adaptAntOrig..=.. adaptLabelOrig.... max      (adaptAntOrig <= adaptLabelOrig) (siempre)
//      // min ... adaptLabelFin..=..adaptAntOrig... max
//      // conclusion:
//      // min ... adaptLabelFin..=..adaptAntOrig..=..adaptLabelOrig ... max
//      // esta nueva variable minimiza la adapt al antece 
//      return adaptLabelFin;
//    }
//    else if (adaptAntOrig < adaptLabelOrig){ // antes no minimizaba la adapt (y ahora tampoco porque viene de lo anterior)
//      return adaptAntOrig;      
//    }
//    else{ // ahora no minimiza pero antes sí, por lo que hay que ver si hay otra variable que minimice la adapt 
//      // -> volver a calcularla pero en cuanto consigamos una adapt == adaptAntOrig salimos
//      double aux= calcAdaptAntNormalizedUmbral(e, indivFin, adaptAntOrig);
////DebugClass.writeResFile("../results.txt0", "se calcula con calcAdaptAntXVecinosUmbral: " + adaptAntOrig);
////System.out.println("se calcula con calcAdaptAntXVecinosUmbral: " + adaptAntOrig);
//      return aux;
//    }
// FIN - ESTA VERSION ES PARA TENER EN CUENTA LAS MEDIDAS DE ADAPTACION (PERO HAY QUE VOLVER A REPENSARLO)    
      
  }

  // POR HACER Y UTILIZAR... TAMBIÉN HABRÁ QUE PONER LOS COMENTARIOS DE LA FUNCIÓN PARA JAVADOC
  public double calcAdaptAntCruceMedidas(int e, GenetCodeClass indivOrig, GenetCodeClass indivFin,
          int indexInic, int indexFin){//, GenetCodeClass indivPopulation){

    int numVariables= this.getProblemDefinition().getFuzzyLinguisticVariableNum();
    int numVariablesAntecedentes= this.getProblemDefinition().numAntecedentVariables();
    double adaptAntOrig= indivOrig.getRealMatrix(1, e);
    // de esta forma se indicará cuál es la variable que ha cambiado respecto al vecino anterior
    int varModificada= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,0);
    int indexModificado= (int) indivFin.getRealMatrix(indivFin.getRealBlocs()-1,1);

    double infMeasureClassOrig= indivOrig.getRealMatrix(0, numVariables-1);
    double infMeasureClassFin= indivFin.getRealMatrix(0, numVariables-1);
    double actInfMeasureOrig;
    double actInfMeasureFin;

    int numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(varModificada).getFuzzyLinguisticTermNum();
    double adaptVarOrig=0, adaptLabelOrig=0, adaptVarFin=0, adaptLabelFin=0;
    double adaptActualLabelOrig=0, adaptActualLabelFin=0, maxAdaptLabelOrig=0, maxAdaptLabelFin=0; 

    double adaptVar=1, adaptLabel=0, adaptActualLabel, maxAdaptLabel;
    int k=0;
    
    // se recorren las variables entre indexInic e indexFin y se ve si se tiene que volver a calcular o no
    for (int v=indexInic; v < indexFin && adaptVar > 0; v++){
      numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
      actInfMeasureOrig= indivOrig.getRealMatrix(0,v);
      actInfMeasureFin= indivFin.getRealMatrix(0,v);

      if (actInfMeasureFin >= infMeasureClassFin){ // ahora se tiene que considerar
        // calcular la adaptación de la variable modificada en el individuo final
        adaptLabelFin=0;
        maxAdaptLabelFin=0;
        for (int l=0; l < numLabels && adaptLabelFin < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
          if (maxAdaptLabelFin < this.getAdaptExVarLab(e,v,l)){
            maxAdaptLabelFin= this.getAdaptExVarLab(e,v,l);
          }
          int considLabel= indivFin.getBinaryMatrix(0, indexModificado+l);
          if (considLabel == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
            adaptActualLabelFin= this.getAdaptExVarLab(e,varModificada,l);
            if (adaptLabelFin == 0){ // no tiene adaptación con una etiqueta anterior
              adaptLabelFin= adaptActualLabelFin;
            }
            else if (adaptActualLabelFin > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
              adaptLabelFin= 1;
            }            
          }
        }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
        if (adaptLabelFin < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
          adaptLabelFin= adaptLabelFin / maxAdaptLabelFin;
        }        
      }//if (actInfMeasureFin >= infMeasureClassFin){ // ahora se tiene que considerar
      
      if (actInfMeasureOrig >= infMeasureClassOrig){ // antes se consideraba
        // calcular la adaptación de la variable modificada en el individuo original
        adaptLabelOrig=0;
        maxAdaptLabelOrig=0;
        for (int l=0; l < numLabels && adaptLabelOrig < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
          if (maxAdaptLabelOrig < this.getAdaptExVarLab(e,varModificada,l)){
            maxAdaptLabelOrig= this.getAdaptExVarLab(e,varModificada,l);
          }
          int considLabel= indivOrig.getBinaryMatrix(0, indexModificado+l);
          if (considLabel == 1){  // el individuo tiene el valor de esa etiqueta en su codificación binaria a 1
            adaptActualLabelOrig= this.getAdaptExVarLab(e,varModificada,l);
            if (adaptLabelOrig == 0){ // no tiene adaptación con una etiqueta anterior
              adaptLabelOrig= adaptActualLabelOrig;
            }
            else if (adaptActualLabelOrig > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
              adaptLabelOrig= 1;
            }            
          }
        }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
        if (adaptLabelOrig < 1){ // hay que normalizar la adaptación ya que tiene adaptación sólo con una etiqueta
          adaptLabelOrig= adaptLabelOrig / maxAdaptLabelOrig;
        }        

///        
        if (adaptLabel < adaptVar){ // coger el mínimo de las adaptaciones como adapt del antecedente
          adaptVar= adaptLabel;
        }        
///      
        
      
      }//if (actInfMeasureOrig >= infMeasureClassOrig){ // antes se consideraba
    }//for (int v=indexInic; v < indexFin && adaptVar > 0; v++){
      
    return adaptVar;
  }

  /**
   * calculate the normalized adaptation of all labels of consequent 
   * that aren't the class of individual (rule) to the example 
   * (
   * calcula la MAXIMA adaptación del ejemplo "e" al NO consecuente de la regla
   * )
   * @param e example used to calculate its adaptation to the individual (rule)
   * @param indiv individual (rule) used in the adaptation
   * @return máximal (normalized) adaptation of all labels of consequent NO-CLASS to the example
   * @deprecated not used in ordinal classification version
   */
  public double calcAdaptNoConsNormalized(int e, GenetCodeClass indiv){

    int varCons= this.getProblemDefinition().consequentIndex();
    int numLabels, indexLabel;
    double adaptLabel=0, adaptActualLabel; 
    int claseIndiv= indiv.getIntegerMatrix(0, 0);

    numLabels= this.getProblemDefinition().getFuzzyLinguisticVariableList(varCons).getFuzzyLinguisticTermNum();
    
    for (int l=0; l < numLabels && adaptLabel < 1; l++){ //recorrer la codificación binaria de las etiquetas que considera la regla para esta variable
      if (claseIndiv != l){
        adaptActualLabel= this.getAdaptExVarLab(e,varCons, l);
        if (adaptLabel == 0){ // no tiene adaptación con una etiqueta anterior
          adaptLabel= adaptActualLabel;
        }
        else if (adaptActualLabel > 0){ // tiene adapt con una etiq anterior y ahora con esta -> adaptNormalizada=1
          adaptLabel= 1;
        }
      }//if (claseIndiv != l){
    }// for (int l=0; l < numLabels && adaptLabel < 1; l++){
    
    return adaptLabel;
  }
  
  /**
   * calculate the normalized adaptation of label of consequent that is 
   * the class of individual (rule) to the example 
   * (
   * calcula la MAXIMA adaptación del ejemplo "e" al consecuente de la regla
   * )
   * @param e example used to calculate its adaptation to the individual (rule)
   * @param indiv individual (rule) used in the adaptation
   * @return máximal (normalized) adaptation of label (CLASS) to the example
   * @deprecated not used in ordinal classification version
   */
  public double calcAdaptConsNormalizada(int e, GenetCodeClass indiv){

    int varCons= this.getProblemDefinition().consequentIndex();
    int claseIndiv= indiv.getIntegerMatrix(0, 0);

    return this.getAdaptExVarLab(e,varCons,claseIndiv);
  }    

  /** 
   * Calculate the covered examples with the new rule 
   * (
   * calcula si un ejemplo está cubierto o no por el conjunto de reglas aprendidas
   * modifica el atributo covered
   * )
   * @param e 
   * @return elementos sin cubrir
   */
  public int calcCovered(RuleSetClass R, PopulationClass P, FuzzyProblemClass problem){

    int numExamples= this.getNumExamples();    
    int numRules= R.getNumRules();
    int good=0, bad=0;
    int[] covered= new int[numExamples];
    int[] indexRuleCovered= new int[numExamples];

    double[] lambdaPos= new double[numExamples];
    double[] lambdaNeg= new double[numExamples];
    int[] indexLambdaPos= new int[numExamples];
    int[] indexLambdaNeg= new int[numExamples];
    double[] negWeight=new double[numExamples];
    double[] posWeight=new double[numExamples];
    double[] values= new double[6];

    // para cálculos de métricas
    double CCR, OMAE, OMAENormalizado;
    int indexRule= -1;
    int classR, classE;
//    double valueR, valueE;
//    double A,B,C,D,inf,sup;
    
    // leemos los datos y los metemos en la matriz de resultados
    int numClases= problem.numLinguisticTermOfConsequent();
    int varCons= problem.consequentIndex();
    int numEjemplos= this.getNumExamples();
//    double [][] results= new double[numEjemplos][2];
//    double[][] clases= new double[numEjemplos][2];

    double[][] confusion= new double[numClases+1][numClases+1];// matriz de confusion (con las filas y columnas de las sumas)
    for (int j= 0; j < numClases+1; j++){
        for (int k= 0; k < numClases+1; k++){
            confusion[j][k]= 0;
        }
    }

    for (int e= 0; e < numExamples; e++){    
      values= Util.calcLambda(this, R, e,P);
      lambdaPos[e]= values[0];
      lambdaNeg[e]= values[1];
      indexLambdaPos[e]= (int) values[2];
      indexLambdaNeg[e]= (int) values[3];
      posWeight[e]= values[4];
      negWeight[e]= values[5];
      
      if (lambdaPos[e] > 0 && 
          ((lambdaNeg[e] < lambdaPos[e]) ||
          ((lambdaNeg[e] == lambdaPos[e]) && (negWeight[e] < posWeight[e])) ||
          ((lambdaNeg[e] == lambdaPos[e]) && (negWeight[e] == posWeight[e]) && (indexLambdaNeg[e] > indexLambdaPos[e])))){

        covered[e]=1;
        indexRuleCovered[e]= (int) indexLambdaPos[e];
        indexRule= indexRuleCovered[e];
        good++;
      }
      else{
        covered[e]=0;
        indexRuleCovered[e]= -1;
        indexRule= (int) indexLambdaNeg[e];
        bad++;
      }
      
      // ahora el cálculo de las métricas
//      valueE= this.getData(e, varCons);
//      results[e][0]= valueE;
      classE= this.getBetterLabel(e, varCons);      
      
//// comprobación      
//      int auxIndexRule= R.inference(this,e);      
//if (auxIndexRule != indexRule){
//String aux="\n ERROR: indexRule: " + indexRule + "; auxIndexRule: " + auxIndexRule;
//System.out.println(aux);
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//DebugClass.sendMail=1;
//DebugClass.cuerpoMail+= "\n" + aux;            
//}      
////aquí se comprueba si mejor coger el indexRule de los lambda calculados anteriormente...
//// FIN - comprobación

/// Para poder ejecutar sin regla por defecto hay que comprobar que el indexRule <> -1. 
/// Si es -1 es que no se ha disparado regla, por lo que se pone la clase que queramos dejar por defecto
/// Lo mejor es dejar la que se ha calculado inicialmente como clase por defecto ->
/// Para ello se modifica la funcion DefaultRule y se añade una variable que es la clase de la regla por defecto
      if (indexRule == -1){
        classR= Util.classDefaultRule;
      }
      else{
        classR= R.getRules(indexRule).getIntegerMatrix(0,0);
      }
      

      confusion[classE][classR]++; // n_i_j
      confusion[classE][numClases]++; // n_i*
      confusion[numClases][classR]++; //n*_j 
      confusion[numClases][numClases]++; //N
      
      
            
    }//for (int e=0; e < numExamples; e++){    
    
    // comprobación matriz confusión
    int sumaFila=0, sumaColumna=0;
    for (int j=0; j < numClases; j++){
        sumaFila+= confusion[j][numClases];
        sumaColumna+= confusion[numClases][j];            
    }
    if (sumaFila != sumaColumna || sumaFila != confusion[numClases][numClases] || sumaFila != numEjemplos){
      String aux="\n ERROR: confusion matrix sumRow: " + sumaFila +", sumCol: "
                + sumaColumna + ", N: " + confusion[numClases][numClases]
                + ", Examples: " + numEjemplos;
      System.out.println(aux);
//      DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
      DebugClass.sendMail=1;
      DebugClass.cuerpoMail+= "\n" + aux;
    }
    // FIN - comprobación matriz confusión

    
    //comenzamos con las métricas
    //métricas clasificacion
    // CCR
    CCR=0;
    for (int j=0; j < numClases; j++){
        CCR+= confusion[j][j];            
    }
    CCR= CCR / confusion[numClases][numClases];
    
    //métricas clasificacion ordinal
    // MAEOrd, AMAE, MMAE
    double[] MAEOrd= new double[numClases];
    double MAETotal=0;
    for (int j=0; j < numClases; j++){
        MAEOrd[j]=0;
    }
    for (int j= 0; j < numClases; j++){
        for (int k= 0; k < numClases; k++){
            MAEOrd[j]+= Math.abs(j-k)*confusion[j][k];
        }
        if (confusion[j][numClases] == 0){
            MAEOrd[j]= 0;
        }
        else{
            MAEOrd[j]= MAEOrd[j] / confusion[j][numClases];
        }
        MAETotal+= confusion[j][numClases] * MAEOrd[j];
    }
    MAETotal= MAETotal / confusion[numClases][numClases];
    OMAE= MAETotal;
    OMAENormalizado= OMAE / ((double) numClases-1);

    // pasar las métricas a la matriz
    R.CCR= CCR;
    R.OMAE= OMAE;
    R.OMAENormalizado= OMAENormalizado;

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

//// comprobación
//if ( Math.abs(CCR - R.CCR) > 0.0001 || Math.abs(OMAE - R.OMAE) > 0.0001){
//String aux="\n ERROR: CCR: " + CCR + " R.CCR: " + R.CCR + "; OMAE: " + OMAE + " R.OMAE: " +R.OMAE ;
//System.out.println(aux);
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//DebugClass.sendMail=1;
//DebugClass.cuerpoMail+= "\n" + aux;
//}
//for (int j= 0; j < numClases+1; j++){
//    for (int k= 0; k < numClases+1; k++){
//        if (Math.abs( R.confusion[j][k]- confusion[j][k]) > 0.0001){
//String aux="\n ERROR: j: " + j + ", k: " + k + " - confusion: " + confusion[j][k] + " R.confusion: " + R.confusion[j][k];
//System.out.println(aux);
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//DebugClass.sendMail=1;
//DebugClass.cuerpoMail+= "\n" + aux;          
//        }
//    }
//}
//// FIN - comprobación
    
    // guardar los valoresde lambda's
    this.setLambdaPos(lambdaPos);
    this.setLambdaNeg(lambdaNeg);
    this.setPosWeight(posWeight);
    this.setNegWeight(negWeight);
    this.setIndexLambdaPos(indexLambdaPos);
    this.setIndexLambdaNeg(indexLambdaNeg);
    this.setCovered(covered);
    this.setIndexRuleCovered(indexRuleCovered);
    
    return good;    
    
  }
  
  public int calcCoveredTFG(RuleSetClass R, PopulationClass P, FuzzyProblemClass problem){

    int numExamples= this.getNumExamples();    
    int numRules= R.getNumRules();
    int good=0, bad=0;
    int[] covered= new int[numExamples];
    int[] indexRuleCovered= new int[numExamples];

    double[] lambdaPos= new double[numExamples];
    double[] lambdaNeg= new double[numExamples];
    int[] indexLambdaPos= new int[numExamples];
    int[] indexLambdaNeg= new int[numExamples];
    double[] negWeight=new double[numExamples];
    double[] posWeight=new double[numExamples];
    double[] values= new double[6];

    // para cálculos de métricas
    double CCR, OMAE, OMAENormalizado;
    int indexRule= -1;
    int classR, classE;
    
    // leemos los datos y los metemos en la matriz de resultados
    int numClases= problem.numLinguisticTermOfConsequent();
    int varCons= problem.consequentIndex();
    int numEjemplos= this.getNumExamples();
    double[][] confusion= new double[numClases+1][numClases+1];// matriz de confusion (con las filas y columnas de las sumas)
    for (int j= 0; j < numClases+1; j++){
        for (int k= 0; k < numClases+1; k++){
            confusion[j][k]= 0;
        }
    }

    for (int e= 0; e < numExamples; e++){    
      values= Util.calcLambda(this, R, e,P);
      lambdaPos[e]= values[0];
      lambdaNeg[e]= values[1];
      indexLambdaPos[e]= (int) values[2];
      indexLambdaNeg[e]= (int) values[3];
      posWeight[e]= values[4];
      negWeight[e]= values[5];
      
      if (lambdaPos[e] > 0 && 
          ((lambdaNeg[e] < lambdaPos[e]) ||
          ((lambdaNeg[e] == lambdaPos[e]) && (negWeight[e] < posWeight[e])) ||
          ((lambdaNeg[e] == lambdaPos[e]) && (negWeight[e] == posWeight[e]) && (indexLambdaNeg[e] > indexLambdaPos[e])))){

        covered[e]=1;
        indexRuleCovered[e]= (int) indexLambdaPos[e];
        indexRule= indexRuleCovered[e];
        good++;
      }
      else{
        covered[e]=0;
        indexRuleCovered[e]= -1;
        indexRule= (int) indexLambdaNeg[e];
        bad++;
      }
      
      // ahora el cálculo de las métricas
      classE= this.getBetterLabel(e, varCons);      

/// Para poder ejecutar sin regla por defecto hay que comprobar que el indexRule <> -1. 
/// Si es -1 es que no se ha disparado regla, por lo que se pone la clase que queramos dejar por defecto
/// Lo mejor es dejar la que se ha calculado inicialmente como clase por defecto ->
/// Para ello se modifica la funcion DefaultRule y se añade una variable que es la clase de la regla por defecto
      if (indexRule == -1){
        classR= Util.classDefaultRule;
      }
      else{
        classR= R.getRules(indexRule).getIntegerMatrix(0,0);
      }
      

      confusion[classE][classR]++; // n_i_j
      confusion[classE][numClases]++; // n_i*
      confusion[numClases][classR]++; //n*_j 
      confusion[numClases][numClases]++; //N
      
      
            
    }//for (int e=0; e < numExamples; e++){    
    
    // comprobación matriz confusión
    int sumaFila=0, sumaColumna=0;
    for (int j=0; j < numClases; j++){
        sumaFila+= confusion[j][numClases];
        sumaColumna+= confusion[numClases][j];            
    }
    if (sumaFila != sumaColumna || sumaFila != confusion[numClases][numClases] || sumaFila != numEjemplos){
        
    }
    // FIN - comprobación matriz confusión

    
    //comenzamos con las métricas
    //métricas clasificacion
    // CCR
    CCR=0;
    for (int j=0; j < numClases; j++){
        CCR+= confusion[j][j];            
    }
    CCR= CCR / confusion[numClases][numClases];
    
    //métricas clasificacion ordinal
    // MAEOrd, AMAE, MMAE
    double[] MAEOrd= new double[numClases];
    double MAETotal=0;
    for (int j=0; j < numClases; j++){
        MAEOrd[j]=0;
    }
    for (int j= 0; j < numClases; j++){
        for (int k= 0; k < numClases; k++){
            MAEOrd[j]+= Math.abs(j-k)*confusion[j][k];
        }
        if (confusion[j][numClases] == 0){
            MAEOrd[j]= 0;
        }
        else{
            MAEOrd[j]= MAEOrd[j] / confusion[j][numClases];
        }
        MAETotal+= confusion[j][numClases] * MAEOrd[j];
    }
    MAETotal= MAETotal / confusion[numClases][numClases];
    OMAE= MAETotal;
    OMAENormalizado= OMAE / ((double) numClases-1);

    // pasar las métricas a la matriz
    R.CCR= CCR;
    R.OMAE= OMAE;
    R.OMAENormalizado= OMAENormalizado;

// Para el cálculo de la métrica ponderada    -    Metrica = (CCR + (1-OMAENormalizado))/2.0;
    R.metricMedia= (CCR + (1-OMAENormalizado)) / 2.0;
    
    R.metric= R.metricMedia;
    
    R.confusion= new double[numClases+1][numClases+1];// matriz de confusion (con las filas y columnas de las sumas)
    for (int j= 0; j < numClases+1; j++){
        for (int k= 0; k < numClases+1; k++){
            R.confusion[j][k]= confusion[j][k];
        }
    }
    
    // guardar los valoresde lambda's
    this.setLambdaPos(lambdaPos);
    this.setLambdaNeg(lambdaNeg);
    this.setPosWeight(posWeight);
    this.setNegWeight(negWeight);
    this.setIndexLambdaPos(indexLambdaPos);
    this.setIndexLambdaNeg(indexLambdaNeg);
    this.setCovered(covered);
    this.setIndexRuleCovered(indexRuleCovered);
    
    return good;    
    
  }

}