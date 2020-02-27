package NSLVOrd;

import java.util.Scanner;
import java.io.*;
import java.util.Locale;
import java.util.Random;

/**
 * @file PopulationClass.java
 * @brief define the population. It will be used in Genetic Algorithm
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement population 
 */
public class PopulationClass {

  private int numIndividuals;           // numero de individuos de la población
  private IndividualClass[] individuals;  // cada uno de los individuos de la población

  private int binaryBlocs;   // indica el número de bloques de elementos binarios (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private int integerBlocs;  // indica el número de bloques de elementos enteros (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private int realBlocs;     // indica el número de bloques de elementos reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  
  private double[] probMutBin;  // indica la probabilidad de mutación para cada bloque de binarios (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probMutBinValue;  // indica la probabilidad de mutación para cada bloque de binarios (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probCruBin;  // indica la probabilidad de cruce para cada bloque de binarios (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probMutInt;  // indica la probabilidad de mutación para cada bloque de enteros (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probMutIntValue;  // indica la probabilidad de mutación para cada bloque de enteros (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probCruInt;  // indica la probabilidad de cruce para cada bloque de enteros (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probMutReal; // indica la probabilidad de mutación para cada bloque de reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probMutRealValue; // indica la probabilidad de mutación para cada bloque de reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] probCruReal; // indica la probabilidad de cruce para cada bloque de reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  
  private double[] probInitBin; // indica las probabilidades para inicializar las poblaciones binarias
  private double[] probInitInt; // indica las probabilidades para inicializar las poblaciones enteras
  private double[] probInitReal; // indica las probabilidades para inicializar las poblaciones reales

  private int maxIterGenetico;  // indica el número máximo de iteraciones del genético sin producirse cambios
  
  private double[][] adaptCons; // matriz con las adaptaciones de todos los ejemplos con todos los consecuentes
  private double[][] adaptNoConsPond; // matriz con las adaptaciones de todos los ejemplos con todos los consecuentes
    
  /** Default constructor */
  public PopulationClass () { 
      numIndividuals= -1;
      individuals= null;
      
      binaryBlocs= -1;
      integerBlocs= -1;
      realBlocs= -1;
           
      probMutBin= null;
      probMutBinValue= null;
      probCruBin= null;
      probMutInt= null;
      probMutIntValue= null;
      probCruInt= null;
      probMutReal= null;
      probMutRealValue= null;
      probCruReal= null;      
      
      probInitBin= null;
      probInitInt= null;
      probInitReal= null;
      
      maxIterGenetico= -1;
      
  };
  
  /**
   * constructor
   * @param parametersKeel vector of parameters
   * @param E set of examples
   */
  public PopulationClass (String[] parametersKeel, ExampleSetProcess E) { 

    // definición de la población del algoritmo genético que nos proporcionará las reglas
    int numIndividuals= Integer.parseInt(parametersKeel[0]);
    int maxIterGenetico= Integer.parseInt(parametersKeel[1]);
    int numFitness= 7;

    int binaryBlocs, integerBlocs, realBlocs;
    int[] sizeBinaryBlocs, sizeIntegerBlocs, sizeRealBlocs, integerRange;
    double[] realInfRange, realSupRange;
    double[] probCruBin, probMutBin, probCruInt, probMutInt, probCruReal, probMutReal;
    double[] probMutBinValue, probMutIntValue, probMutRealValue;
    double[] probInitBin, probInitInt, probInitReal;
    double[][] adaptCons, adaptNoConsPond;

    if (numIndividuals < 0){
        
      int maxNumLabels= E.getProblemDefinition().numLinguisticTermOfConsequent();
//      for (int i=0; i < E.numVariables; i++){
//        if (maxNumLabels < E.getProblemDefinition().getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum()){
//          maxNumLabels = E.getProblemDefinition().getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum();
//        }
//      }
      numIndividuals = 20 * maxNumLabels + 2;
//      numIndividuals = 2 * E.getNumExamples();
    }
    
    int numLabels= E.getProblemDefinition().numLinguisticTermOfAntecedentsVariables();
    binaryBlocs= 3;
    sizeBinaryBlocs= new int[binaryBlocs];
    probInitBin= new double[binaryBlocs];
    probCruBin= new double[binaryBlocs];
    probMutBin= new double[binaryBlocs];
    probMutBinValue= new double[binaryBlocs];
    
    sizeBinaryBlocs[2]= sizeBinaryBlocs[1]= sizeBinaryBlocs[0]= numLabels;
    probInitBin[0]= Double.parseDouble(parametersKeel[2]);
    probCruBin[0]= Double.parseDouble(parametersKeel[3]);
    probMutBin[0]= Double.parseDouble(parametersKeel[4]);
    probMutBinValue[0]= Double.parseDouble(parametersKeel[5]);

    int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
    integerBlocs= 1;
    sizeIntegerBlocs= new int[integerBlocs];
    integerRange= new int[integerBlocs];
    probInitInt= new double[integerBlocs];
    probCruInt= new double[integerBlocs];
    probMutInt= new double[integerBlocs];
    probMutIntValue= new double[integerBlocs];

    sizeIntegerBlocs[0]= 1;
    integerRange[0]= numClases;                
    probInitInt[0]= Double.parseDouble(parametersKeel[6]);
    probCruInt[0]= Double.parseDouble(parametersKeel[7]);
    probMutInt[0]= Double.parseDouble(parametersKeel[8]);
    probMutIntValue[0]= Double.parseDouble(parametersKeel[9]);

    int numVariables= E.getProblemDefinition().numAntecedentVariables();
    int numExamples= E.getNumExamples();
//    realBlocs= E.getProblemDefinition().numLinguisticTermOfConsequent() + 3;
    realBlocs= (numClases) + 2;
    sizeRealBlocs= new int[realBlocs];
    realInfRange= new double[realBlocs];
    realSupRange= new double[realBlocs];
    probInitReal= new double[realBlocs];
    probCruReal= new double[realBlocs];
    probMutReal= new double[realBlocs];
    probMutRealValue= new double[realBlocs];

    int i=0;
    sizeRealBlocs[i]= numVariables+1;
    // medidas de informacion
    realInfRange[i]= 0;
    realSupRange[i]= 1;
    probInitReal[i]= Double.parseDouble(parametersKeel[10]);
    probCruReal[i]= Double.parseDouble(parametersKeel[11]);
    probMutReal[i]= Double.parseDouble(parametersKeel[12]);
    probMutRealValue[i]= Double.parseDouble(parametersKeel[13]);
    i++;
    // adaptacion antecedentes
    sizeRealBlocs[i]= numExamples+1;                                
    realInfRange[i]= -1;
    realSupRange[i]= 1;
    probInitReal[i]= 0;
    probCruReal[i]= 0;
    probMutReal[i]= 0;
    probMutRealValue[i]= 0;
    i++;
    // fitness + n+eTrainParaPeso + n-eTrainParaPeso + numEjemplosPositivosTrain + numEjemplosNegativosTrain + n+eTrainParaCalcCovered
    for (int j=0; j < numClases; i++,j++){
      sizeRealBlocs[i]= numFitness + 5;
      realInfRange[i]= -1;
      realSupRange[i]= 1;
      probInitReal[i]= 0;
      probCruReal[i]= 0;
      probMutReal[i]= 0;
      probMutRealValue[i]= 0;
    }

    adaptCons= new double[numClases][numExamples+1];
    adaptNoConsPond= new double[numClases][numExamples+1];
    
    init (numIndividuals, maxIterGenetico, binaryBlocs, integerBlocs, 
         realBlocs, sizeBinaryBlocs, sizeIntegerBlocs, sizeRealBlocs,
         integerRange, realInfRange, realSupRange, numFitness,
         probMutBin, probMutBinValue, probCruBin, probMutInt, probMutIntValue, 
         probCruInt, probMutReal, probMutRealValue, probCruReal, 
         probInitBin, probInitInt, probInitReal,adaptCons, adaptNoConsPond);
      
    }  
  
  /**
   * construtor
   * @param numIndividuals number of individuals
   * @param maxIterGenetico max number of iterations allowed in genetic algorithm
   * @param binaryBlocs number of binary blocs (subpopulations)
   * @param integerBlocs number of integet blocs (subpopulations)
   * @param realBlocs number of real blocs (subpopulations)
   * @param sizeBinaryBlocs vector of size of each binary bloc
   * @param sizeIntegerBlocs vector of size of each integer bloc
   * @param sizeRealBlocs vector of size of each real bloc
   * @param integerRange vector of range of each integer bloc
   * @param realInfRange vector of inferior range of each real bloc
   * @param realSupRange vector of superior range of each real bloc
   * @param numFitness number of fitness of individual
   * @param probMutBin mutation probability for binary population
   * @param probMutBinValue mutation probability for elements of binary population
   * @param probCruBin crossover probability for binary population
   * @param probMutInt mutation probability for integer population
   * @param probMutIntValue mutation probability for elements of integer population
   * @param probCruInt crossover probability for integer population
   * @param probMutReal mutation probability for real population
   * @param probMutRealValue mutation probability for elements of real population
   * @param probCruReal crossover probability for real population
   * @param probInitBin probability for init the binary population
   * @param probInitInt probability for init the integer population
   * @param probInitReal probability for init the real population
   */
  public void init (int numIndividuals, int maxIterGenetico, int binaryBlocs, int integerBlocs, 
          int realBlocs, int[] sizeBinaryBlocs, int[] sizeIntegerBlocs, int[] sizeRealBlocs,
          int[] integerRange, double[] realInfRange, double[] realSupRange, int numFitness, 
          double[] probMutBin, double[] probMutBinValue, double[] probCruBin, 
          double[] probMutInt, double[] probMutIntValue, double[] probCruInt,
          double[] probMutReal, double[] probMutRealValue, double[] probCruReal, 
          double[] probInitBin, double[] probInitInt,
          double[] probInitReal, double[][] adaptCons, double[][] adaptNoConsPond) { 

      this.numIndividuals= numIndividuals;
      this.maxIterGenetico= maxIterGenetico;
      
      this.individuals= new IndividualClass[numIndividuals];

      for (int i=0; i < numIndividuals; i++)
          this.individuals[i]= new IndividualClass(binaryBlocs, integerBlocs, realBlocs,
                  sizeBinaryBlocs, sizeIntegerBlocs, sizeRealBlocs, integerRange, 
                  realInfRange, realSupRange, numFitness);
      
      this.binaryBlocs= binaryBlocs;
      this.integerBlocs= integerBlocs;
      this.realBlocs= realBlocs;
      
      this.probMutBin= probMutBin;
      this.probMutBinValue= probMutBinValue;
      this.probCruBin= probCruBin;
      this.probMutInt= probMutInt;
      this.probMutIntValue= probMutIntValue;
      this.probCruInt= probCruInt;
      this.probMutReal= probMutReal;
      this.probMutRealValue= probMutRealValue;
      this.probCruReal= probCruReal;

      this.probInitBin= probInitBin;
      this.probInitInt= probInitInt;
      this.probInitReal= probInitReal;
      
      this.adaptCons= adaptCons;
      this.adaptNoConsPond= adaptNoConsPond;
      
  };
  
  /** Used for copy constructor
   * @param orig 
   */
    protected PopulationClass(PopulationClass orig){
      this.numIndividuals= orig.numIndividuals;
      
      this.individuals= new IndividualClass[this.numIndividuals];

      for (int i=0; i < this.numIndividuals; i++)
          this.individuals[i]= orig.individuals[i].copy();
      
      this.binaryBlocs= orig.binaryBlocs;
      this.integerBlocs= orig.integerBlocs;
      this.realBlocs= orig.realBlocs;
      
      this.probMutBin= orig.probMutBin;
      this.probMutBinValue= orig.probMutBinValue;
      this.probCruBin= orig.probCruBin;
      this.probMutInt= orig.probMutInt;
      this.probMutIntValue= orig.probMutIntValue;
      this.probCruInt= orig.probCruInt;
      this.probMutReal= orig.probMutReal;
      this.probMutRealValue= orig.probMutRealValue;
      this.probCruReal= orig.probCruReal;

      this.probInitBin= orig.probInitBin;
      this.probInitInt= orig.probInitInt;
      this.probInitReal= orig.probInitReal;
      
      this.maxIterGenetico= orig.maxIterGenetico;
      
      this.adaptCons= orig.adaptCons;
      this.adaptNoConsPond= orig.adaptNoConsPond;

    }
    
    /** copy constructor
     * @return 
     */
    public PopulationClass copy(){
      return new PopulationClass(this);
    }

//    /** Constructor
//     * Estructura del fichero de parámetros del algoritmo genético (*.genetic)
//     * 
//     * numIndividuals
//     * maxIterGenetico
//     * numFitness
//     * binaryBlocs (poblaciones binarias)
//     * sizeBinaryBlocs[0] probCruBin[0]	probMutBin[0]
//     * sizeBinaryBlocs[1] probCruBin[1]	probMutBin[1]
//     * ... (tantas como indique binaryBlocs)
//     * integerBlocs (poblaciones enteras)
//     * sizeIntegerBlocs[0] integerRange[0] probCruInt[0] probMutInt[0] 
//     * sizeIntegerBlocs[1] integerRange[1] probCruInt[1] probMutInt[1] 
//     * ... (tantas como indique integerBlocs)
//     * realBlocs (poblaciones reales)
//     * sizeRealBlocs[0] realInfRange[0] realSupRange[0] probCruInt[0] probMutInt[0] 
//     * sizeRealBlocs[1] realInfRange[1] realSupRange[1] probCruInt[1] probMutInt[1] 
//     * ... (tantas como indique realBlocs)
//     * 
//     * --- Explicación de las variables ---              
//     * 
//     * binaryBlocs (poblaciones binarias
//     *  sizeBinaryBlocs[0] (tamaño población binaria 0) 
//     *    probCruBin[0] (probabilidad de cruce de la subpoblación binaria 0)
//     *    probMutBin[0] (probabilidad de mutación de la subpoblación binaria 0)
//     *  sizeBinaryBlocs[1] (tamaño población binaria 1)
//     *    probCruBin[1] (probabilidad de cruce de la subpoblación binaria 1)
//     *    probMutBin[1] (probabilidad de mutación de la subpoblación binaria 1)
//     *   ... (tantas como indique binaryBlocs)
//     * integerBlocs (poblaciones enteras)
//     *   sizeIntegerBlocs[0] (tamaño población entera 0)
//     *     integerRange[0] (rango de posibles valores enteros de la subpoblación entera 0)
//     *     probCruInt[0] (probabilidad de cruce de la subpoblación entera 0)
//     *     probMutInt[0] (probabilidad de mutación de la subpoblación entera 0)
//     *   sizeIntegerBlocs[1] (tamaño población entera 1)
//     *     integerRange[1] (rango de posibles valores enteros de la subpoblación entera 1)
//     *     probCruInt[1] (probabilidad de cruce de la subpoblación entera 1)
//     *     probMutInt[1] (probabilidad de mutación de la subpoblación entera 1)
//     *   ... (tantas como indique integerBlocs)
//     * realBlocs (poblaciones reales)
//     *   sizeRealBlocs[0] (tamaño población real 0)
//     *     realInfRange[0] (rango inferior de posibles valores reales de la subpoblación real 0)
//     *     realSupRange[0] (rango superior de posibles valores reales de la subpoblación real 0)
//     *     probCruInt[0] (probabilidad de cruce de la subpoblación entera 0)
//     *     probMutInt[0] (probabilidad de mutación de la subpoblación entera 0)
//     *   sizeRealBlocs[1] (tamaño población real 1)
//     *     realInfRange[1] (rango inferior de posibles valores reales de la subpoblación real 1)
//     *     realSupRange[1] (rango superior de posibles valores reales de la subpoblación real 1)
//     *     probCruInt[1] (probabilidad de cruce de la subpoblación entera 1)
//     *     probMutInt[1] (probabilidad de mutación de la subpoblación entera 1)
//     *   ... (tantas como indique realBlocs)
//     * 
//     * @param PopulationFile File with domain definition
//     * @return cadena con los cambios producidos según el fichero de configuración y el conjunto de ejemplos
//     * @deprecated not used in this version
//    */
//    public String init(String PopulationFile, ExampleSetProcess E){
//        Scanner scanFile= null;
//        int numIndividuals, numFitness, maxIterGenetico;
//        int binaryBlocs, integerBlocs, realBlocs;
//        int[] sizeBinaryBlocs, sizeIntegerBlocs, sizeRealBlocs, integerRange;
//        double[] realInfRange, realSupRange;
//        double[] probCruBin, probMutBin, probCruInt, probMutInt, probCruReal, probMutReal;
//        double[] probMutBinValue, probMutIntValue, probMutRealValue;
//        double[] probInitBin, probInitInt, probInitReal;
//        String result="";
//
//        try{
//            scanFile= new Scanner(new FileReader(PopulationFile));
//            scanFile.useLocale(Locale.ENGLISH);   // configura el formato de números
//            numIndividuals= scanFile.nextInt();
//            if (numIndividuals < 0){
//              numIndividuals = 20 * E.getProblemDefinition().numLinguisticTermOfConsequent() + 2;
//            }
//            maxIterGenetico= scanFile.nextInt();            
//            numFitness= scanFile.nextInt();
//
//            binaryBlocs= scanFile.nextInt();
//            sizeBinaryBlocs= new int[binaryBlocs];
//            probInitBin= new double[binaryBlocs];
//            probCruBin= new double[binaryBlocs];
//            probMutBin= new double[binaryBlocs];
//            probMutBinValue= new double[binaryBlocs];
//                        
//            for (int i=0; i < binaryBlocs; i++){
//                sizeBinaryBlocs[i]= scanFile.nextInt();
//                probInitBin[i]= scanFile.nextDouble();
//                probCruBin[i]= scanFile.nextDouble();
//                probMutBin[i]= scanFile.nextDouble();
//                probMutBinValue[i]= scanFile.nextDouble();
//            }
//            
//            integerBlocs= scanFile.nextInt();
//            sizeIntegerBlocs= new int[integerBlocs];
//            integerRange= new int[integerBlocs];
//            probInitInt= new double[integerBlocs];
//            probCruInt= new double[integerBlocs];
//            probMutInt= new double[integerBlocs];
//            probMutIntValue= new double[integerBlocs];
//                        
//            for (int i=0; i < integerBlocs; i++){
//                sizeIntegerBlocs[i]= scanFile.nextInt();
//                integerRange[i]= scanFile.nextInt();
//                probInitInt[i]= scanFile.nextDouble();
//                probCruInt[i]= scanFile.nextDouble();
//                probMutInt[i]= scanFile.nextDouble();
//                probMutIntValue[i]= scanFile.nextDouble();
//            }
//                
//            realBlocs= scanFile.nextInt();
//            sizeRealBlocs= new int[realBlocs];
//            realInfRange= new double[realBlocs];
//            realSupRange= new double[realBlocs];
//            probInitReal= new double[realBlocs];
//            probCruReal= new double[realBlocs];
//            probMutReal= new double[realBlocs];
//            probMutRealValue= new double[realBlocs];
//                        
//            for (int i=0; i < realBlocs; i++){
//                sizeRealBlocs[i]= scanFile.nextInt();
//                realInfRange[i]= scanFile.nextDouble();
//                realSupRange[i]= scanFile.nextDouble();
//                probInitReal[i]= scanFile.nextDouble();
//                probCruReal[i]= scanFile.nextDouble();
//                probMutReal[i]= scanFile.nextDouble();
//                probMutRealValue[i]= scanFile.nextDouble();
//            }            
//
//            scanFile.close();            
//        
//
//            // Una vez finalizado el procesamiento del fichero modificamos los valores 
//            // que se toman del conjunto de ejemplos
//            // etiquetas del antecedente
//            int numLabels= E.getProblemDefinition().numLinguisticTermOfAntecedentsVariables();
//            if (numLabels != sizeBinaryBlocs[0]){
//                result+= "numLabels != sizeBinaryBlocs[0] (" + numLabels + " != " + sizeBinaryBlocs[0] + ")\n";
//                sizeBinaryBlocs[0]= numLabels;
//            }
//            
//            // rango de enteros para la clase
//            int numClasses= E.getProblemDefinition().numLinguisticTermOfConsequent();
//            if (numClasses != integerRange[0]){
//                result+= "numClasses != integerRange[0] (" + numClasses + " != " + integerRange[0] + ") \n";
//                integerRange[0]= numClasses;                
//            }
//                
//            // medidas de informacion
//            int numVariables= E.getProblemDefinition().numAntecedentVariables();
//            if (numVariables+1 != sizeRealBlocs[0]){
//                result+= "numVariables+1 != sizeRealBlocs[0] (" + (numVariables+1) + " != " + sizeRealBlocs[0] + ")\n";
//                sizeRealBlocs[0]= numVariables+1;
//            }
//
//            
//            // numero de ejemplos
//            int numExamples= E.getNumExamples();
//            if (numExamples+1 != sizeRealBlocs[1]){
//                result+= "numExamples+1 != sizeRealBlocs[1] (" + (numExamples+1) + " != " + sizeRealBlocs[1] + ")\n";
//                sizeRealBlocs[1]= numExamples+1;                                
//            }
//            if (numExamples+1 != sizeRealBlocs[2]){
//                result+= "numExamples+1 != sizeRealBlocs[2] (" + (numExamples+1) + " != " + sizeRealBlocs[2] + ")\n";
//                sizeRealBlocs[2]= numExamples+1;                                
//            }
//            if (numExamples+1 != sizeRealBlocs[3]){
//                result+= "numExamples+1 != sizeRealBlocs[3] (" + (numExamples+1) + " != " + sizeRealBlocs[3] + ")\n";
//                sizeRealBlocs[3]= numExamples+1;                                
//            }
//            
//            
//
//
//            init (numIndividuals, maxIterGenetico, binaryBlocs, integerBlocs, 
//                 realBlocs, sizeBinaryBlocs, sizeIntegerBlocs, sizeRealBlocs,
//                 integerRange, realInfRange, realSupRange, numFitness,
//                 probMutBin, probMutBinValue, probCruBin, probMutInt, probMutIntValue, 
//                 probCruInt, probMutReal, probMutRealValue, probCruReal, 
//                 probInitBin, probInitInt, probInitReal);
//
//        
//        
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            System.exit(-1);
//        }finally{
//            try{                   
//                if( scanFile != null){  
//                    scanFile.close();    
//                }                 
//            }catch (Exception e2){
//                e2.printStackTrace();
//                System.exit(-1);
//            }
//        }                
//        return result;                       
//    }

    
  /**
   * Set the value of numIndividuals
   * @param newVar the new value of numIndividuals
   */
  public void setNumIndividuals ( int newVar ) {
    numIndividuals = newVar;
  }

  /**
   * Get the value of numIndividuals
   * @return the value of numIndividuals
   */
  public int getNumIndividuals ( ) {
    return numIndividuals;
  }

  /**
   * Set the value of individuals
   * @param newVar the new value of individuals
   */
  public void setIndividuals ( IndividualClass[] newVar ) {
    individuals = newVar;
  }

  /**
   * Get the value of individuals
   * @return the value of individuals
   */
  public IndividualClass[] getIndividuals ( ) {
    return individuals;
  }

  /**
   * Set the value of individuals
   * @param row position of new value
   * @param newVar the new value of individuals
   * @return -1 if no valid position, row otherwise
   */
  public int setIndividuals ( int row, IndividualClass newVar ) {
//    if (row >= 0 && row < numIndividuals){
        individuals[row] = newVar;
        return row;
//    }
//    else{
//        return -1;
//    }
  }

  /**
   * Get the value of individuals
   * @param row position of new value
   * @return the value of individuals; -1 if no valid possition
   */
  public IndividualClass getIndividuals (int row ) {
//    if (row >= 0 && row < numIndividuals){
        return individuals[row];
//    }
//    else{
//        return null;
//    }
  }
    
  /**
   * Set the value of binaryBlocs
   * @return  
   */
/*  public static void setBinaryBlocs ( int newVar ) {
    binaryBlocs = newVar;
  }

  /**
   * Get the value of binaryBlocs
   * @return the value of binaryBlocs
   */
  public int getBinaryBlocs ( ) {
    return binaryBlocs;
  }

  /**
   * Set the value of integerBlocs
   * @return  
   */
/*  public static void setIntegerBlocs ( int newVar ) {
    integerBlocs = newVar;
  }

  /**
   * Get the value of integerBlocs
   * @return the value of integerBlocs
   */
  public int getIntegerBlocs ( ) {
    return integerBlocs;
  }

  /**
   * Set the value of realBlocs
   * @return  
   */
/*  public static void setRealBlocs ( int newVar ) {
    realBlocs = newVar;
  }

  /**
   * Get the value of realBlocs
   * @return the value of realBlocs
   */
  public int getRealBlocs ( ) {
    return realBlocs;
  }

  
  
  /**
   * Set the value of probMutBin
   * @return  
   */
/*  public static void setProbMutBin ( double[] newVar ) {
    probMutBin = newVar;
  }

  /**
   * Get the value of probMutBin
   * @return the value of probMutBin
   */
  public double[] getProbMutBin ( ) {
    return probMutBin;
  }

  /**
   * Set the value of probMutBin
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setProbMutBin ( int row, double newVar ) {
    if (row >= 0 && row < IndividualClass.getBinaryBlocs()){ 
        probMutBin[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of probMutBin
   * @param row position of new value
   * @return the value of probMutBin; -1 if no valid possition
   */
  public double getProbMutBin (int row ) {
//    if (row >= 0 && row < binaryBlocs){
        return probMutBin[row];
//    }
//    else{
//        return -1;
//    }
  }

  /**
   * Get the value of probMutBin
   * @return the value of probMutBin
   */
  public double[] getProbMutBinValue ( ) {
    return probMutBinValue;
  }

  /**
   * Get the value of probMutBin
   * @param row position of new value
   * @return the value of probMutBin; -1 if no valid possition
   */
  public double getProbMutBinValue (int row ) {
//    if (row >= 0 && row < binaryBlocs){
        return probMutBinValue[row];
//    }
//    else{
//        return -1;
//    }
  }
  
  
  /**
   * Set the value of probCruBin
   * @return  
   */
/*  public static void setProbCruBin ( double[] newVar ) {
    probCruBin = newVar;
  }

  /**
   * Get the value of probCruBin
   * @return the value of probCruBin
   */
  public double[] getProbCruBin ( ) {
    return probCruBin;
  }

  /**
   * Set the value of probCruBin
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setProbCruBin ( int row, double newVar ) {
    if (row >= 0 && row < IndividualClass.getBinaryBlocs()){ 
        probCruBin[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of probCruBin
   * @param row position of new value
   * @return the value of probCruBin; -1 if no valid possition
   */
  public double getProbCruBin (int row ) {
//    if (row >= 0 && row < binaryBlocs){
        return probCruBin[row];
//    }
//    else{
//        return -1;
//    }
  }


  
  /**
   * Set the value of probMutInt
   * @return 
   */
/*  public static void setProbMutInt ( double[] newVar ) {
    probMutInt = newVar;
  }

  /**
   * Get the value of probMutInt
   * @return the value of probMutInt
   */
  public double[] getProbMutInt ( ) {
    return probMutInt;
  }

  /**
   * Set the value of probMutInt
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setProbMutInt ( int row, double newVar ) {
    if (row >= 0 && row < IndividualClass.getIntegerBlocs()){
        probMutInt[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of probMutInt
   * @param row position of new value
   * @return the value of probMutInt; -1 if no valid possition
   */
  public double getProbMutInt (int row ) {
//    if (row >= 0 && row < integerBlocs){
        return probMutInt[row];
//    }
//    else{
//        return -1;
//    }
  }
  
  /**
   * Get the value of probMutInt
   * @return the value of probMutInt
   */
  public double[] getProbMutIntValue ( ) {
    return probMutIntValue;
  }

  /**
   * Get the value of probMutInt
   * @param row position of new value
   * @return the value of probMutInt; -1 if no valid possition
   */
  public double getProbMutIntValue (int row ) {
//    if (row >= 0 && row < integerBlocs){
        return probMutIntValue[row];
//    }
//    else{
//        return -1;
//    }
  }
  
  /**
   * Set the value of probCruInt
   * @return  
   */
/*  public static void setProbCruInt ( double[] newVar ) {
    probCruInt = newVar;
  }

  /**
   * Get the value of probCruInt
   * @return the value of probCruInt
   */
  public double[] getProbCruInt ( ) {
    return probCruInt;
  }

  /**
   * Set the value of probCruInt
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setProbCruInt ( int row, double newVar ) {
    if (row >= 0 && row < IndividualClass.getIntegerBlocs()){
        probCruInt[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of probCruInt
   * @param row position of new value
   * @return the value of probCruInt; -1 if no valid possition
   */
  public double getProbCruInt (int row ) {
//    if (row >= 0 && row < integerBlocs){
        return probCruInt[row];
//    }
//    else{
//        return -1;
//    }
  }

  /**
   * Set the value of probMutReal
   * @return 
   */
/*  public static void setProbMutReal ( double[] newVar ) {
    probMutReal = newVar;
  }

  /**
   * Get the value of probMutReal
   * @return the value of probMutReal
   */
  public double[] getProbMutReal ( ) {
    return probMutReal;
  }

  /**
   * Set the value of probMutReal
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setProbMutReal ( int row, double newVar ) {
    if (row >= 0 && row < IndividualClass.getRealBlocs()){
        probMutReal[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of probMutReal
   * @param row position of new value
   * @return the value of probMutReal; -1 if no valid possition
   */
  public double getProbMutReal (int row ) {
//    if (row >= 0 && row < realBlocs){
        return probMutReal[row];
//    }
//    else{
//        return -1;
//    }
  }

  /**
   * Get the value of probMutReal
   * @return the value of probMutReal
   */
  public double[] getProbMutRealValue ( ) {
    return probMutRealValue;
  }
  
  /**
   * Get the value of probMutReal
   * @param row position of new value
   * @return the value of probMutReal; -1 if no valid possition
   */
  public double getProbMutRealValue (int row ) {
//    if (row >= 0 && row < realBlocs){
        return probMutRealValue[row];
//    }
//    else{
//        return -1;
//    }
  }
  
    /**
   * Set the value of probCruReal
   * @return  
   */
/*  public static void setProbCruReal ( double[] newVar ) {
    probCruReal = newVar;
  }

  /**
   * Get the value of probCruReal
   * @return the value of probCruReal
   */
  public double[] getProbCruReal ( ) {
    return probCruReal;
  }

  /**
   * Set the value of probCruReal
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setProbCruReal ( int row, double newVar ) {
    if (row >= 0 && row < IndividualClass.getRealBlocs()){
        probCruReal[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of probCruReal
   * @param row position of new value
   * @return the value of probCruReal; -1 if no valid possition
   */
  public double getProbCruReal (int row ) {
//    if (row >= 0 && row < realBlocs){
        return probCruReal[row];
//    }
//    else{
//        return -1;
//    }
  }
  
  /**
   * Get the value of probInitBin
   * @param row position of new value
   * @return the value of probCruReal; -1 if no valid possition
   */
  public double getProbInitBin (int row ) {
//    if (row >= 0 && row < binaryBlocs){
        return probInitBin[row];
//    }
//    else{
//        return -1;
//    }
  }

    /**
   * Get the value of probInitInt
   * @param row position of new value
   * @return the value of probCruReal; -1 if no valid possition
   */
  public double getProbInitInt (int row ) {
//    if (row >= 0 && row < integerBlocs){
        return probInitInt[row];
//    }
//    else{
//        return -1;
//    }
  }

    /**
   * Get the value of probInitReal
   * @param row position of new value
   * @return the value of probCruReal; -1 if no valid possition
   */
  public double getProbInitReal (int row ) {
//    if (row >= 0 && row < realBlocs){
        return probInitReal[row];
//    }
//    else{
//        return -1;
//    }
  }

  /**
   * Set the value of maxIterGenetico
   * @param newVar the new value of maxIterGenetico
   */
  public void setMaxIterGenetico ( int newVar ) {
    maxIterGenetico = newVar;
  }

  /**
   * Get the value of maxIterGenetico
   * @return the value of maxIterGenetico
   */
  public int getMaxIterGenetico ( ) {
    return maxIterGenetico;
  }

  /**
   * Set the value of adaptCons
   * @param newVar the new value of adaptCons
   */
  public void setAdaptCons ( double[][] newVar ) {
    adaptCons = newVar;
  }

  /**
   * Get the value of adaptCons
   * @return the value of adaptCons
   */
  public double[][] getAdaptCons ( ) {
    return adaptCons;
  }

  /**
   * Set the value of adaptCons
   * @param newVar the new value of adaptCons
   */
  public int setAdaptCons ( int row, int col, double newVar ) {
//      if (row >= 0 && row < adaptCons.length &&
//          col >= 0 && col < adaptCons[0].length){
          adaptCons[row][col] = newVar;
          return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of adaptCons
   * @return the value of adaptCons
   */
  public double getAdaptCons (int row, int col ) {
//      if (row >= 0 && row < adaptCons.length &&
//          col >= 0 && col < adaptCons[0].length){

          return adaptCons[row][col];
//      }
//      else{
//          return -1;
//      }
  }
  
  /**
   * Set the value of adaptNoConsPond
   * @param newVar the new value of adaptNoConsPond
   */
  public void setAdaptNoConsPond ( double[][] newVar ) {
    adaptNoConsPond = newVar;
  }

  /**
   * Get the value of adaptNoConsPond
   * @return the value of adaptNoConsPond
   */
  public double[][] getAdaptNoConsPond ( ) {
    return adaptNoConsPond;
  }

  /**
   * Set the value of adaptNoConsPond
   * @param newVar the new value of adaptNoConsPond
   */
  public int setAdaptNoConsPond ( int row, int col, double newVar ) {
//      if (row >= 0 && row < adaptNoConsPond.length &&
//          col >= 0 && col < adaptNoConsPond[0].length){
        
          adaptNoConsPond[row][col] = newVar;
          return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of adaptNoConsPond
   * @return the value of adaptNoConsPond
   */
  public double getAdaptNoConsPond (int row, int col ) {
//      if (row >= 0 && row < adaptNoConsPond.length &&
//          col >= 0 && col < adaptNoConsPond[0].length){

          return adaptNoConsPond[row][col];
//      }
//      else{
//          return -1;
//      }
  }
  
  
/* ......................................................................... */
    
  /**
   * Initialize the binary subpopulation of index 0.
   * (
   * inicializar el primer bloque de elementos binarios (variables antecedentes)
   * está formado por tantos bloques como variables antecedentes hay
   * el tamaño de cada bloque es el número de etiquetas de la variable
   * tam individuo= número de etiquetas de las variables antecedentes, considerando todas las variables
   * .@param subjects matriz de sujetos con la mejor etiqueta de cada individuo para la inicialización de la población
   * .@param E conjunto de ejemplos para la población, para obtener el numero de antecedentes y demás
   * .@param randomNum semilla para el número aleatorio
   * .@param prob probabilidad para la inicialización de las poblaciones
   * )
   * @param subjects subjects matrix with the better label to init
   * @param E set of examples
   * @param randomNum random number
   * @param prob probability to consider
   */
    public void initPopulationBin0(int[][] subjects, ExampleSetProcess E, Random randomNum){
  
        int numIndividuals= this.getNumIndividuals();
        int numVariablesAntecedentes= E.getProblemDefinition().numAntecedentVariables();
        int numTermLingAntec=0;
        int value=0,k=0;

        for (int i=0; i < numIndividuals; i++){
          k=0;
          for (int v=0; v < numVariablesAntecedentes; v++){
            numTermLingAntec= E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
            if (subjects[i][v] == -1){    // no hay ejemplos de esa clase -> inicialización aleatoria
                for (int l=0; l < numTermLingAntec; l++){
                    value= Util.Probability(probInitBin[0], randomNum);
                    this.getIndividuals(i).setBinaryMatrix(0, (k+l), value);
                }
                // checkear si todas las etiquetas son 0 -> si es así no tiene sentido -> ponemos alguna a 1 de forma aleatoria
                if (this.getIndividuals(i).binaryMatrix0AllToZero(k, numTermLingAntec) == 1){
                    double aleat= randomNum.nextDouble();
                    aleat= aleat * numTermLingAntec;
                    this.getIndividuals(i).setBinaryMatrix(0, (k+(int)aleat), 1);
                }            
            }
            else{ // (subjects[i][j] != -1) hay ejemplos de esa clase -> inicialización conforme a la mejor etiqueta
                for (int l=0; l < numTermLingAntec; l++){
                    this.getIndividuals(i).setBinaryMatrix(0,(k+l),0);                        
                }
                this.getIndividuals(i).setBinaryMatrix(0, ((k)+subjects[i][v]),1);
            }
            k= k + numTermLingAntec;          
          }
        }
        
        
//        for (int i=0 ; i < numIndividuals; i++){
//            for (int j=0; j < numVariablesAntecedentes; j++){
//                numTermLingAntec= E.getProblemDefinition().getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
//                if (subjects[i][j] == -1){    // no hay ejemplos de esa clase -> inicialización aleatoria
//                    for (int k=0; k < numTermLingAntec; k++){
//                        value= Util.Probability(probInitBin[0], randomNum);
//                        this.getIndividuals(i).setBinaryMatrix(0, ((j*numTermLingAntec)+k), value);
//                    }
//                }
//                else{ // (subjects[i][j] != -1) hay ejemplos de esa clase -> inicialización conforme a la mejor etiqueta
//                    for (int k=0; k < numTermLingAntec; k++){
//                        this.getIndividuals(i).setBinaryMatrix(0,((j*numTermLingAntec)+k),0);                        
//                    }
//                    this.getIndividuals(i).setBinaryMatrix(0, ((j*numTermLingAntec)+subjects[i][j]),1);
//                }
//                
//                // checkear si todas las etiquetas son 0 -> si es así no tiene sentido -> ponemos la 1ª a 1
//                numLabels= problem.getFuzzyLinguisticVariableList(j).getFuzzyLinguisticTermNum();
//                if ((R.getRules(i).binaryMatrix0AllToOne(start,numLabels) != 1) && // si todas las etiquetas están a 1 --> irrelevante
//                
//                
//                
//                
//            }
//        }
    }
  
   
  /**
  /**
   * Initialize the integer subpopulation of index 0.
   * (
   * inicializar el primer bloque de elementos enteros (consecuente)
   * hay dos valores:
   * valor 0 -> el valor dentro del rango, indica la etiqueta consecuente con mayor adaptación
   * valor 1 -> el valor dentro del rango, indica la etiqueta consecuente con 2º mayor adaptación
   * Representa la zona de intersección entre dos etiquetas difusas y tiene mayor adaptación con la 1ª
   * )
   * @param randomNum random number
   */
    public void initPopulationInt0(Random randomNum){
  
        int numIndividuals= this.getNumIndividuals();
        int range, value, value2;
//        double aleat= randomNum.nextDouble();

        for (int i=0 ; i < numIndividuals; i++){
            range= this.getIndividuals(i).getIntegerRange(0);
            value= i%range;
            this.getIndividuals(i).setIntegerMatrix(0,0,value);
//            if (value==0){
//                value2= Util.Probability(probInitInt[0],randomNum);
//                this.getIndividuals(i).setIntegerMatrix(0,1, value2);
//            }
//            else if(value == (range - 1)){
//                value2= Util.Probability(probInitInt[0],randomNum);
//                if (value2 == 1){
//                    this.getIndividuals(i).setIntegerMatrix(0,1, range-2);
//                }
//                else{
//                    this.getIndividuals(i).setIntegerMatrix(0,1, range-1);
//                }
//            }
//            else{
//                value2= Util.Probability(probInitInt[0]*2/3.0, randomNum);
//                if (value2 == 1){
//                    this.getIndividuals(i).setIntegerMatrix(0,1, value+1);                    
//                }
//                else{
//                    value2= Util.Probability(probInitInt[0], randomNum);
//                    if (value2 == 1){
//                        this.getIndividuals(i).setIntegerMatrix(0,1, value-1);                                            
//                    }
//                    else{
//                        this.getIndividuals(i).setIntegerMatrix(0,1, value);                                                                    
//                    }
//                }
//            }            
        }
    }
            
  /**
   * Initialize the real subpopulation of index 0.
   * (
   * inicializar el primer bloque de elementos reales (adaptacion de las 
   * variables y del consecuente)
   * Se utilizan las medidas de información 
   * 
   * está formado por tantos bloques como variables antecedentes hay
   * el tamaño de cada bloque es el número de etiquetas de la variable
   * tam individuo= número de etiquetas de las variables antecedentes, 
   * considerando todas las variables 
   * )
   * @param E set of examples
   * @param randomNum random number
   */
    public void initPopulationReal0(ExampleSetProcess E, Random randomNum){
  
        int numIndividuals= this.getNumIndividuals();
        int numClasses= E.getProblemDefinition().numLinguisticTermOfConsequent();   
        double[][] inforMeasure= E.getInformationMeasures();
        double[] max= new double[numClasses];
        double[] min= new double[numClasses];
        int tamBloc= this.getIndividuals(0).getSizeRealBlocs(0);
        double aleat;

        for (int i=0; i < numClasses; i++){
            max[i]= inforMeasure[0][i];
            min[i]= inforMeasure[0][i];
            for (int j=1; j < tamBloc - 1; j++){
                if (inforMeasure[j][i] > max[i]){
                    max[i]= inforMeasure[j][i];
                }
                else if (inforMeasure[j][i] < min[i]){
                    min[i]= inforMeasure[j][i];
                }
            }            
        }
        
        for (int i=0 ; i < numIndividuals; i++){
            for (int j=0 ; j < tamBloc - 1; j++){
                if (min[i%numClasses] == max[i%numClasses]){
                    aleat= randomNum.nextDouble();
                    this.getIndividuals(i).setRealMatrix(0, j, aleat);
                }
                else{
                    aleat= inforMeasure[j][(i%numClasses)];
                    this.getIndividuals(i).setRealMatrix(0, j, aleat);
                }
            }
            aleat= ((max[i%numClasses] - min[i%numClasses])*randomNum.nextDouble()) + min[i%numClasses] ;
            this.getIndividuals(i).setRealMatrix(0, (tamBloc - 1), aleat);
        }
    }
        
  /**
   * calc adaptations of examples to consequents
   * modify the reals subpopulation with adaptations of examples to consecuents
   * @param E set of examples
   */
  public void calcAdaptCons(ExampleSetProcess E, double[][] costMatrix){
    int numExamples= E.getNumExamples();
    int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
    int varCons= E.getProblemDefinition().consequentIndex();
    double[] nBi= new double[numClases];
    double[] nBNoiPond= new double[numClases];
    int classE;
    
    // comprobar que estamos en clasificación ordinal y la matriz de coste es correcta
    if (costMatrix != null && costMatrix.length != numClases){
        costMatrix= null;
    }
    
    for (int j=0; j < numClases; j++){
      nBi[j]=0;
      nBNoiPond[j]=0;
    }

    for (int e=0; e < numExamples; e++){     
// Esta parte sería para clasificación
      classE= E.getBetterLabel(e, varCons);            
      for (int j=0; j < numClases; j++){
        if (j == classE){
          this.adaptCons[j][e]= 1;
          nBi[j]++;
        }
        else{
          this.adaptCons[j][e]= 0;
        }
        if (costMatrix == null){ // no hya matriz de costes -> se toma distancia 1
            this.adaptNoConsPond[j][e]= (1 - this.adaptCons[j][e]) * Math.abs(j-classE);            
            nBNoiPond[j]+= Math.abs(j-classE);
        }
        else{ // hay matriz de costes -> se toma esos valores de costes
            this.adaptNoConsPond[j][e]= (1 - this.adaptCons[j][e]) * costMatrix[j][classE];            
            nBNoiPond[j]+= costMatrix[j][classE];
        }
        
// FIN - Esta parte sería para clasificación
        
//// Esta parte sería para regresión        
//        this.adaptCons[j][e]= E.getAdaptExVarLab(e, varCons, j);
//        this.adaptNoConsPond[j][e]= (1 - this.adaptCons[j][e]) * Math.abs(j-classE);
//        
//        if (j == classE){
//          nBi[j]++;
//        }
//        else{
//          nBNoiPond[j]+= Math.abs(j-classE);;
//        }
//// FIN - Esta parte sería para regresión        
      }//for (int j=0; j < numClases; j++){
    }//for (int e=0; e < numExamples; e++){
    
    for (int j=0; j < numClases; j++){
      this.adaptCons[j][numExamples]= nBi[j];
      this.adaptNoConsPond[j][numExamples]= nBNoiPond[j];
    }
  }
  
  /**
   * Initialize the population
   * (
   * Inicializar la población con los individuos en función de los ejemplos de entrenamiento
   * )
   * @param subjects subjects matrix with the better label to init
   * @param E set of examples
   * @param randomNum random number
   * @param prob probability to consider
   */
  public void initPopulation(int[][] subjects, ExampleSetProcess E, Random randomNum, double[][] costMatrix){

      int numIndividuals= this.getNumIndividuals();
      int numFitness= this.getIndividuals(0).getNumFitness();

      initPopulationBin0(subjects,E, randomNum);     // inicializar el primer bloque de binarios (variables antecedentes)
      
      initPopulationInt0(randomNum);
      
      initPopulationReal0(E,randomNum);
      
      //inicializar valores que no cambian como las adaptaciones de los ejemplos a cada uno de los consecuentes
      calcAdaptCons(E, costMatrix);
      for (int i=0; i < numIndividuals; i++){
          this.getIndividuals(i).setModified(1);
          for (int j= 0; j < numFitness; j++){
              this.getIndividuals(i).setFitness(j,0);
          }
      }
  }
 
  /**
   * swapping the individuals i and j
   * @param i index of individuals
   * @param j index of individuals
   */
  public void swap(int i, int j){
    
    IndividualClass aux;
    
    aux= this.getIndividuals(i);
    this.setIndividuals(i,this.getIndividuals(j));
    this.setIndividuals(j, aux);
  }
  
  /**
   * aply the sort algorithm. First the bigger fitness
   */
  public void sort(){
    int numIndividuals= this.getNumIndividuals();
    int numFitness= this.getIndividuals(0).getNumFitness();
    
    double[] fitnessJ, fitnessJ1;
    
    int k;
    for (int i=0; i<numIndividuals-1; i++){
      for (int j=numIndividuals-1; j>i; j--){
        k=0;
        fitnessJ= this.getIndividuals(j).getFitness();
        fitnessJ1= this.getIndividuals(j-1).getFitness();
        while (k<numFitness && fitnessJ[k] == fitnessJ1[k]){
          k++;
        }
        if (k<numFitness && fitnessJ[k] > fitnessJ1[k]){
          swap(j,j-1);          
        }
      }  
    }    
  }

/* ··· mutaciones genéricas para cada elementos binarios, enteros y reales ···*/
  /**
   * Compute the stationary uniform mutation of binary subpopulation of the last two individuals of population
   * (
   * realiza la mutación uniforme estacionaria de la parte entera de los dos últimos individuos
   * )
   * @param index index of subpoblación to mutate
   * @param randomNum random number
   */
  public void stationaryUniformMutationBin(int index, Random randomNum){
    int numIndividuals= this.getNumIndividuals();
    double prob= this.getProbMutBinValue(index);
    
    for (int i=numIndividuals-2; i < numIndividuals; i++){
      this.getIndividuals(i).stationaryUniformMutationBin(prob,index,randomNum);
    }
  }
  
  /**
   * Compute the stationary uniform mutation of integer subpopulation of the last two individuals of population
   * (
   * realiza la mutación uniforme estacionaria de la parte entera de los dos últimos individuos
   * )
   * @param index index of subpoblación to mutate
   * @param randomNum random number
   */
  public void stationaryUniformMutationInt(int index, Random randomNum){
    int numIndividuals= this.getNumIndividuals();
    double prob= this.getProbMutIntValue(index);
    
    for (int i=numIndividuals-2; i < numIndividuals; i++){
      this.getIndividuals(i).stationaryUniformMutationInt(prob, index, randomNum);
    }
  }

  /**
   * Compute the stationary uniform mutation of real subpopulation of the last two individuals of population
   * (
   * realiza la mutación uniforme estacionaria de la parte real de los dos últimos individuos
   * )
   * @param index index of subpoblación to mutate
   * @param randomNum random number
   */
  public void stationaryUniformMutationReal(int index, Random randomNum){
    int numIndividuals= this.getNumIndividuals();
    double prob= this.getProbMutRealValue(index);
    
    for (int i=numIndividuals-2; i < numIndividuals; i++){
      // realiza la mutación de la parte de medidas de información
      this.getIndividuals(i).stationaryUniformMutationReal(prob, index,randomNum);
    }
  }
/* ··· FIN - mutaciones genéricas para cada elementos binarios, enteros y reales ···*/
  
//  /**
//   * Compute the stationary uniform mutation of real subpopulation of the last two individuals of population
//   * for calculate with neighbour
//   * @param randomNum random number
//   * @param E set of examples
//   * @return matrix with index of values modified
//   * // resultadoMutacion[0]=[modificacionPenultimoBin,modificacionPenultimoInt, modificacionPenultimoReal, check1Bin]
//   * // resultadoMutacion[1]=[modificacionUltimoBin,modificacionUltimoInt, modificacionUltimoReal, check2Bin]
//   */
//  public int[][] stationaryUniformMutationForNeighbour(Random randomNum, ExampleSetProcess E){
//    int[][] resultado= new int[2][4]; // guarda si ha habido una mutación en bin, int y/o real de la variable
//    int anyModified= 0;
//    int[] resultadoAux;
//    resultado[0][0]= resultado[0][1]= resultado[0][2]= resultado[0][3]=0;
//    resultado[1][0]= resultado[1][1]= resultado[1][2]= resultado[1][3]= 0;
//    int k=0;
//    
//    
//    while (anyModified == 0){
//      if (Util.Probability(this.getProbMutBin(0),randomNum) == 1){ // realizar la mutación
//        k=0;
//        for (int i=numIndividuals-2; i < numIndividuals; i++){
//          resultadoAux= this.getIndividuals(i).stationaryUniformMutationBin(this.getProbMutBinValue(0),0,randomNum);
//          for (int kk=0; kk < resultadoAux.length; kk++){
//            if (resultadoAux[kk] == 1){
//              resultado[k][0]=1;
//              kk= resultadoAux.length;
//            }
//          }//for (int k=0; k < resultadoBin.length; k++){
//          k++;
//        }//for (int i=numIndividuals-2; i < numIndividuals; i++){
//        anyModified= 1;
//
//        // comprobar que no tengamos variables con todo 0
//        resultado[0][3]= Util.checkAndChangeBinaryMatrix0NoAllZero(this.getIndividuals(numIndividuals-2), E, randomNum);
//        resultado[0][3]= Util.checkAndChangeBinaryMatrix0NoAllZero(this.getIndividuals(numIndividuals-1), E, randomNum);
//      }
//      if (Util.Probability(this.getProbMutInt(0),randomNum) == 1){ // realizar la mutación
//        k=0;
//        for (int i=numIndividuals-2; i < numIndividuals; i++){
//          resultadoAux= this.getIndividuals(i).stationaryUniformMutationInt(this.getProbMutIntValue(0),0,randomNum);
//          for (int kk=0; kk < resultadoAux.length; kk++){
//            if (resultadoAux[kk] == 1){
//              resultado[k][1]=1;
//              kk= resultadoAux.length;
//            }
//          }//for (int k=0; k < resultadoBin.length; k++){
//          k++;
//        }//for (int i=numIndividuals-2; i < numIndividuals; i++){
//        anyModified= 1;
//      }
//      if (Util.Probability(this.getProbMutReal(0),randomNum) == 1){ // realizar la mutación
//        k=0;
//        for (int i=numIndividuals-2; i < numIndividuals; i++){
//          resultadoAux= this.getIndividuals(i).stationaryUniformMutationReal(this.getProbMutRealValue(0),0,randomNum);
//          for (int kk=0; kk < resultadoAux.length; kk++){
//            if (resultadoAux[kk] == 1){
//              resultado[k][2]=1;
//              kk= resultadoAux.length;
//            }
//          }//for (int k=0; k < resultadoBin.length; k++){
//          k++;
//        }//for (int i=numIndividuals-2; i < numIndividuals; i++){
//        anyModified= 1;
//      }      
//    }
//    
//    return resultado;
//  }

  /**
   * Compute the stationary uniform mutation of the last two individuals of population
   * @param randomNum random number
   */
  public void stationaryUniformMutation(Random randomNum){
    int anyModified= 0;
    
    while (anyModified == 0){
      if (Util.Probability(this.getProbMutBin(0),randomNum) == 1){ // realizar la mutación
        stationaryUniformMutationBin(0,randomNum);
        anyModified= 1;
      }
      if (Util.Probability(this.getProbMutInt(0),randomNum) == 1){ // realizar la mutación
        stationaryUniformMutationInt(0,randomNum);
        anyModified= 1;
      }
      if (Util.Probability(this.getProbMutReal(0),randomNum) == 1){ // realizar la mutación
        stationaryUniformMutationReal(0,randomNum);
        anyModified= 1;
      }      
    }
  }

/* ··· cruces genéricos para cada elementos binarios, enteros y reales ···*/
  /**
   * compute the stationary two points croosver in binary subpopulation of the last two individuals of population
   * @param index index of subpopulation to mutate
   * @param randomNum random number 
   * @param indiv1Index index of individual 1 to do the crossover
   * @param indiv2Index index of individual 2 to do the crossover
   */
  public int[] stationary2PointsCrossoverBin(int index, Random randomNum, 
          int indiv1Index, int indiv2Index){
    int numIndividuals= this.getNumIndividuals();
    int tamBloc= this.getIndividuals(0).getSizeBinaryBlocs(index);
    int[] crossPoints= new int[2];
    int[][] indiv1Matrix, indiv2Matrix;
    int aux;
    
    crossPoints= Util.crossPoints(tamBloc, randomNum);
    
    indiv1Matrix= this.getIndividuals(indiv1Index).getBinaryMatrix();
    indiv2Matrix= this.getIndividuals(indiv2Index).getBinaryMatrix();
    for (int i=crossPoints[0]; i < crossPoints[1]; i++){
      aux= indiv1Matrix[index][i];
      indiv1Matrix[index][i]= indiv2Matrix[index][i];
      indiv2Matrix[index][i]= aux;
    }
// no hace falta ya que se trabaja directamente con los punteros a las matrices
//    this.getIndividuals(indiv1Index).setBinaryMatrix(indiv1Matrix);
//    this.getIndividuals(indiv2Index).setBinaryMatrix(indiv2Matrix);
   
    this.getIndividuals(indiv1Index).setModified(1);
    this.getIndividuals(indiv2Index).setModified(1);
    
    return crossPoints;
  }
  /**
   * compute the stationary two points croosver in integer subpopulation of the last two individuals of population
   * @param index index of subpopulation to mutate
   * @param randomNum random number 
   * @param indiv1Index index of individual 1 to do the crossover
   * @param indiv2Index index of individual 2 to do the crossover
   */
  public int[] stationary2PointsCrossoverInt(int index, Random randomNum, 
          int indiv1Index, int indiv2Index){
    int numIndividuals= this.getNumIndividuals();
    double prob= this.getProbCruInt(index);
    int tamBloc= this.getIndividuals(0).getSizeIntegerBlocs(index);
    int[] crossPoints= new int[2];
    int[][] indiv1Matrix, indiv2Matrix;
    int aux;
    
    crossPoints= Util.crossPoints(tamBloc, randomNum);
    
    indiv1Matrix= this.getIndividuals(indiv1Index).getIntegerMatrix();
    indiv2Matrix= this.getIndividuals(indiv2Index).getIntegerMatrix();
    for (int i=crossPoints[0]; i < crossPoints[1]; i++){
      aux= indiv1Matrix[index][i];
      indiv1Matrix[index][i]= indiv2Matrix[index][i];
      indiv2Matrix[index][i]= aux;
    }
// no hace falta ya que se trabaja directamente con los punteros a las matrices
//    this.getIndividuals(indiv1Index).setIntegerMatrix(indiv1Matrix);
//    this.getIndividuals(indiv2Index).setIntegerMatrix(indiv2Matrix);
        
   
    this.getIndividuals(indiv1Index).setModified(1);
    this.getIndividuals(indiv2Index).setModified(1);
    
    return crossPoints;
  }
  /**
   * compute the stationary two points croosver in real subpopulation of the last two individuals of population
   * @param index index of subpopulation to mutate
   * @param randomNum random number 
   * @param indiv1Index index of individual 1 to do the crossover
   * @param indiv2Index index of individual 2 to do the crossover
   */
  public int[] stationary2PointsCrossoverReal(int index, Random randomNum, 
          int indiv1Index, int indiv2Index){
    int numIndividuals= this.getNumIndividuals();
    double prob= this.getProbCruReal(index);
    int tamBloc= this.getIndividuals(0).getSizeRealBlocs(index);
    int[] crossPoints= new int[2];
    double[][] indiv1Matrix, indiv2Matrix;
    double aux;
    
    crossPoints= Util.crossPoints(tamBloc, randomNum);
    
    indiv1Matrix= this.getIndividuals(indiv1Index).getRealMatrix();
    indiv2Matrix= this.getIndividuals(indiv2Index).getRealMatrix();
    for (int i=crossPoints[0]; i < crossPoints[1]; i++){
      aux= indiv1Matrix[index][i];
      indiv1Matrix[index][i]= indiv2Matrix[index][i];
      indiv2Matrix[index][i]= aux;
    }
// no hace falta ya que se trabaja directamente con los punteros a las matrices
//    this.getIndividuals(indiv1Index).setRealMatrix(indiv1Matrix);
//    this.getIndividuals(indiv2Index).setRealMatrix(indiv2Matrix);
        
   
    this.getIndividuals(indiv1Index).setModified(1);
    this.getIndividuals(indiv2Index).setModified(1);
    
    return crossPoints;
  }
/* ··· FIN - mutaciones genéricas para cada elementos binarios, enteros y reales ···*/
  
//  /**
//   * compute the stationary two points croosver in binary subpopulation of the last two individuals of population
//   * for calculate with neighbour
//   * @param randomNum random number 
//   * @param E set of examples
//   * @return vector with index of values modified
//   * // resultadoCruce=[indiv1Index,indiv2Index,cross1Bin,cross2Bin,cross1Real,cross2Real,check1Binary, check2Binary]
//   */
//  public int[] stationaryLogicalCrossoverForNeighbour(double prob, Random randomNum, 
//          ExampleSetProcess E){
//    int numIndividuals= this.getNumIndividuals();
//    int indiv1Index, indiv2Index;    
//    int anyModified= 0;
//    int[] resultado= new int[8];
//    int[] resultadoBin= new int[2];
//    int[] resultadoReal= new int[2];
//    resultadoBin[0]= resultadoBin[1]= resultadoReal[0]= resultadoReal[1]= -1;    
//
//    if (Util.Probability(1-prob,randomNum) == 1){
//      indiv1Index= Util.selectRamdomElementIndex(numIndividuals-2,-1, randomNum);
//      indiv2Index= Util.selectRamdomElementIndex(numIndividuals-2, indiv1Index, randomNum);
//    }
//    else{
//      indiv1Index=0;
//      indiv2Index= Util.selectRamdomElementIndex(numIndividuals-2,indiv1Index, randomNum);
//    }
//    
//    this.setIndividuals(numIndividuals-2, this.getIndividuals(indiv1Index).copy());
//    this.setIndividuals(numIndividuals-1, this.getIndividuals(indiv2Index).copy());
//
//    while (anyModified == 0){
//      if (Util.Probability(this.getProbCruBin(0),randomNum) == 1){ // realizar el cruce
//        resultadoBin= stationary2PointsCrossoverBin(0,randomNum, numIndividuals-2, numIndividuals-1);
//        anyModified= 1;
//      }
//      if (Util.Probability(this.getProbCruReal(0),randomNum) == 1){ // realizar el cruce
//        resultadoReal= stationary2PointsCrossoverReal(0,randomNum, numIndividuals-2, numIndividuals-1);
//        anyModified= 1;
//      }    
//    }
//    
//    resultado[0]= indiv1Index;
//    resultado[1]= indiv2Index;
//    resultado[2]= resultadoBin[0];
//    resultado[3]= resultadoBin[1];
//    resultado[4]= resultadoReal[0];
//    resultado[5]= resultadoReal[1];
//    resultado[6]= Util.checkAndChangeBinaryMatrix0NoAllZero(this.getIndividuals(numIndividuals-2), E, randomNum);
//    resultado[7]= Util.checkAndChangeBinaryMatrix0NoAllZero(this.getIndividuals(numIndividuals-1), E, randomNum);
//
//    
//    return resultado;
//  }

  /**
   * compute the stationary logical croosver in binary and real subpopulation of the last two individuals of population
   * @param prob probability to consider
   * @param randomNum random number 
   */
  public void stationaryLogicalCrossover(double prob, Random randomNum){
    int numIndividuals= this.getNumIndividuals();
    int indiv1Index, indiv2Index;    
    int anyModified= 0;

    if (Util.Probability(1-prob,randomNum) == 1){
      indiv1Index= Util.selectRamdomElementIndex(numIndividuals-2,-1, randomNum);
      indiv2Index= Util.selectRamdomElementIndex(numIndividuals-2, indiv1Index, randomNum);
    }
    else{
      indiv1Index=0;
      indiv2Index= Util.selectRamdomElementIndex(numIndividuals-2,indiv1Index, randomNum);
    }
    
    this.setIndividuals(numIndividuals-2, this.getIndividuals(indiv1Index).copy());
    this.setIndividuals(numIndividuals-1, this.getIndividuals(indiv2Index).copy());

    while (anyModified == 0){
      if (Util.Probability(this.getProbCruBin(0),randomNum) == 1){ // realizar el cruce
        stationary2PointsCrossoverBin(0,randomNum, numIndividuals-2, numIndividuals-1);
        anyModified= 1;
      }
      if (Util.Probability(this.getProbCruReal(0),randomNum) == 1){ // realizar el cruce
        stationary2PointsCrossoverReal(0,randomNum, numIndividuals-2, numIndividuals-1);
        anyModified= 1;
      }
    
    }
  }
  
  /**
   * aply the sort algorithm over two last individuals and the rest of population
   * First the bigger fitness
   */
  public void sortExcept2Last(){
    int numIndividuals= this.getNumIndividuals();
    int numFitness= this.getIndividuals(0).getNumFitness();
    
    double[] fitnessJ, fitnessJ1;
    
    int k;
    int cambio=1;
    for (int i=0; cambio==1 && i<numIndividuals-3; i++){
      cambio= 0;
      for (int j=numIndividuals-3; j>i; j--){
        k=0;
        fitnessJ= this.getIndividuals(j).getFitness();
        fitnessJ1= this.getIndividuals(j-1).getFitness();
        while (k<numFitness && fitnessJ[k] == fitnessJ1[k]){
          k++;
        }
        if (k<numFitness && fitnessJ[k] > fitnessJ1[k]){
          swap(j,j-1);
          cambio=1;
        }
      }  
    }    
  }

  /**
   * Aply sort the population for learning (only over last two individuals)
   * 
   * @param E set of examples
   */
  public void sort4L(ExampleSetProcess E){

    int numIndividuals= this.getNumIndividuals();
    int numClasses= E.getProblemDefinition().numLinguisticTermOfConsequent();
    int i,k;
    int[] pobMinXClass= new int[numClasses];
    int numExamples= E.getNumExamples();
    int[] numExNotCoveredXClass= E.getNumExamNotCoveredXClass();
    int[][] numIndXClass= new int[numClasses][numIndividuals+1];
    int numMedIndXClass= ((numIndividuals-2) / numClasses) - 1;
    int numMinIndXClass= (int) (0.5*numMedIndXClass);
    
    // ordenar los dos últimos elementos (el resto debe estar ordenados)
    for (i= (numIndividuals-2); i < numIndividuals; i++){
      k=i;
      while (k > 0 && 
             Util.Mayor(this.getIndividuals(k).getFitness(),
                        this.getIndividuals(k-1).getFitness()) == 1){
        swap(k,k-1);
        k--;
      }
    }

    // calculo del numero de individuos por clase
    for (i=0; i < numIndividuals-2; i++){
      int clas= this.getIndividuals(i).getIntegerMatrix(0, 0);
      numIndXClass[clas][0]++;
      numIndXClass[clas][numIndXClass[clas][0]]= i;
    }
    
// check si no hay individuos de la clase
for (i=0; i < numClasses; i++){
if (numIndXClass[i][0] == 0){
String aux= "\nNo hay individuos de la clase "+ i + "\n";
//DebugClass.writeResFile(DebugClass.fileResultDebug+0,aux);
//DebugClass.sendMail=1;
//DebugClass.cuerpoMail+= "\n" + aux;
}
}
// FIN - check si no hay individuos de la clase

    // calculo del tamaño mínimo de las poblaciones
    for (i=0; i< numClasses; i++){
        pobMinXClass[i]= numMinIndXClass;
    }
    
    // poner al final los dos últimos individuos que van a ser reemplazados
    // para ello se tiene en cuenta la población mínima de individuos por clase
    int last2used= 0;
    int clasI, clasJ;
    for (i= numIndividuals-2; i < numIndividuals; i++){
      clasI= this.getIndividuals(i).getIntegerMatrix(0,0);
      if (numIndXClass[clasI][0] < pobMinXClass[clasI]){ 
        // no hay bastantes individuos de esa clase -> subir el individuo para que no se pierda la clase
        last2used= 1;
        int j=numIndividuals-3;
        while (j > 0 && 
               numIndXClass[this.getIndividuals(j).getIntegerMatrix(0,0)][0] <= pobMinXClass[this.getIndividuals(j).getIntegerMatrix(0,0)]){
          j--;
        }
        clasJ= this.getIndividuals(j).getIntegerMatrix(0,0);
        numIndXClass[clasI][numIndXClass[clasI][0]+1]= numIndXClass[clasJ][numIndXClass[clasJ][0]];
        numIndXClass[clasI][0]++;
        numIndXClass[clasJ][0]--;
        swap(i,j);
      }         
    }
    
    if (last2used == 1){
      sortExcept2Last();
    }    

// Esta parte es antigua y realizaba demasiadas comprobaciones. Lo sustituyo por lo anterior    
//    int numIndividuals= this.getNumIndividuals();
//    int numClasses= E.getProblemDefinition().numLinguisticTermOfConsequent();
//    int i,k;
//    int[] pobMinXClass= new int[numClasses];
//    int numExamples= E.getNumExamples();
//    int[] numExNotCoveredXClass= E.getNumExamNotCoveredXClass();
//    int[][] numIndXClass= new int[numClasses][numIndividuals+1];
//    int numMedIndXClass= ((numIndividuals-2) / numClasses) - 1;
//    int numMinIndXClass= (int) (0.5*numMedIndXClass);
//    
//    // ordenar los dos últimos elementos (el resto debe estar ordenados)
//    for (i= (numIndividuals-2); i < numIndividuals; i++){
//      k=i;
//      while (k > 0 && 
//             Util.Mayor(this.getIndividuals(k).getFitness(),
//                        this.getIndividuals(k-1).getFitness()) == 1){
//        swap(k,k-1);
//        k--;
//      }
//    }
//
//    // calculo del numero de individuos por clase
//    for (i=0; i < numIndividuals-2; i++){
//      int clas= this.getIndividuals(i).getIntegerMatrix(0, 0);
//      numIndXClass[clas][0]++;
//      numIndXClass[clas][numIndXClass[clas][0]]= i;
//    }
//    
//    // cálculo del número mínimo de individuos que tiene que haber de una clase 
//    // para el posterior cálculo de la población mínima de cada clase
//// ESTO HAY QUE QUITARLO -> ES SOLO PARA LA PRUEBA CON POBLACIÓN DE 6 INDIVIDUOS    
//    if (numIndividuals < 10){ // hay muy pocos individuos -> sólo se utiliza para pruebas
//        numMinIndXClass= 1;    
//    }
//    else if (numMinIndXClass > numMedIndXClass || numMinIndXClass < 0){
//        numMinIndXClass= 0;
//    }
//    
//// PARA PROBARLO CON LA REGRESIÓN -> AL AUMENTAR EL NÚMERO DE ETIQUETAS PUEDE QUE NO HAYA INDIVIDUOS PARA TODAS ?????
////    numMinIndXClass=0;    
//
//    // calculo del tamaño mínimo de las poblaciones
//    for (i=0; i< numClasses; i++){
//      if ((numExNotCoveredXClass[i] / numExamples) < 0.05){ // hay menos de un 5% de ejemplos de esa clase
//        pobMinXClass[i]= numMinIndXClass;
//      }
//      else{
//        int index1= numIndXClass[i][1]; // indice del mejor individuo de la clase i
//        // numIndXClass[i][0] -> índice del último valor introducido en el vector de numIndXClass[i]
//        int index2= numIndXClass[i][numIndXClass[i][0]]; // indice del último individuo considerado de la clase i
//        double fitnessInd1= this.getIndividuals(index1).getFitness(0);
//        double fitnessInd2= this.getIndividuals(index2).getFitness(0);
//        
//        if (numIndXClass[i][0] > 0 && Math.abs(fitnessInd1-fitnessInd2) < 0.1){ //hay muy poca diferencia de fitness entre el mejor y el peor individuo
//          pobMinXClass[i]= numMinIndXClass;
//        }
//        else{
//          pobMinXClass[i]= numMedIndXClass;
//        }
//      }
//    }
//    
//    // poner al final los dos últimos individuos que van a ser reemplazados
//    // para ello se tiene en cuenta la población mínima de individuos por clase
//    int last2used= 0;
//    int clasI, clasJ;
//    for (i= numIndividuals-2; i < numIndividuals; i++){
//      clasI= this.getIndividuals(i).getIntegerMatrix(0,0);
//      if (numIndXClass[clasI][0] < pobMinXClass[clasI]){ 
//        // no hay bastantes individuos de esa clase -> subir el individuo para que no se pierda la clase
//        last2used= 1;
//        int j=numIndividuals-3;
//        while (j > 0 && 
//               numIndXClass[this.getIndividuals(j).getIntegerMatrix(0,0)][0] <= pobMinXClass[this.getIndividuals(j).getIntegerMatrix(0,0)]){
//          j--;
//        }
//        clasJ= this.getIndividuals(j).getIntegerMatrix(0,0);
//        numIndXClass[clasI][numIndXClass[clasI][0]+1]= numIndXClass[clasJ][numIndXClass[clasJ][0]];
//        numIndXClass[clasI][0]++;
//        numIndXClass[clasJ][0]--;
//        swap(i,j);
//      }         
//    }
//    
//    if (last2used == 1){
//      sortExcept2Last();
//    }    
  }
 
    /**
     * check vector index 0 to verify exists any value distint of zero in population . 
     * @param E set of examples
     * @param randomNum random number
     * @note If all values are 0, change any of them in aleatory way
     */
    public void checkAndChangeBinaryMatrix0NoAllZero(int i, ExampleSetProcess E,Random randomNum){
  
        int numIndividuals= this.getNumIndividuals();
        int numVariablesAntecedentes= E.getProblemDefinition().numAntecedentVariables();
        int numTermLingAntec=0;
        int value=0,k=0;

//        for (int i=0; i < numIndividuals; i++){
          k=0;
//DebugClass.printPopulationElement("",this,1,0,E,i);
          for (int v=0; v < numVariablesAntecedentes; v++){
            numTermLingAntec= E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
            
            // checkear si todas las etiquetas son 0 -> si es así no tiene sentido -> ponemos a 1 (de forma aleatoria)
            if (this.getIndividuals(i).binaryMatrix0AllToZero(k, numTermLingAntec) == 1){
                double aleat= randomNum.nextDouble();
                aleat= aleat * numTermLingAntec;

                this.getIndividuals(i).setBinaryMatrix(0, (k+(int)aleat), 1);
//DebugClass.printPopulationElement("",this,1,0,E,i);
            }            
            k= k + numTermLingAntec;
          }
//        }
    }
  
  
  
//  /**
//   * Calculate the Hamming distance between the individuals with index i and j
//   * @param i index of individual
//   * @param j index of individual
//   * @return Hamming distance
//   * @note if inforMeasure are distint return max Hamming distance
//   */
//  public int calcDistHamming(ExampleSetProcess E, int i,int j){
//    int numBinaryElements= this.getIndividuals(i).getSizeBinaryBlocs(0);
//    int dist=0;
//    int[] ele1, ele2;
//    double[] inforMeasure1, inforMeasure2;
//    int k=0, numLabels=0, numVar=0;
//    
//    ele1= this.getIndividuals(i).getBinarySubMatrix(0).clone();
//    inforMeasure1= this.getIndividuals(i).getRealSubMatrix(0).clone();
//    ele2= this.getIndividuals(j).getBinarySubMatrix(0).clone();
//    inforMeasure2= this.getIndividuals(j).getRealSubMatrix(0);
//    numVar= E.getProblemDefinition().numAntecedentVariables();
//    
//    // Si las medidas de información no coinciden 
//    // -> conforme se van realizando las mutaciones puede dar algo incorrecto
//    // POR AHORA SI NO COINCIDEN NO LO UTILIZAMOS... 
//    // OTRA FORMA ES NO TENER EN CUENTA LAS MEDIDAS DE INFORMACIÓN -- POR HACER... POR AQUIII
//    for (int v=0; v < numVar+1; v++){
//      if (inforMeasure1[v] != inforMeasure2[v]){
//       return this.getIndividuals(i).getBinarySubMatrix(0).length; // devuelve el máximo de la distancia
//      }        
//    }    
//
//    // actualizar las matrices binarias con la codificación del individuo en 
//    // función de la medida de información
//    for (int v=0; v < numVar; v++){
//      numLabels=  E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
//      if (inforMeasure1[v] < inforMeasure1[numVar]){
//        for (int l=0; l < numLabels; l++){
//          ele1[k+l]= 1;
//        }
//      }
//      if (inforMeasure2[v] < inforMeasure2[numVar]){
//        for (int l=0; l < numLabels; l++){
//          ele2[k+l]= 1;
//        }
//      }
//      k= k+numLabels;      
//    }
//
//    // realizar la comparación para la distancia de hamming
//    for (k=0; k < numBinaryElements; k++){
//      if (ele1[k] != ele2[k]){
//        dist++;
//      }
//    }
//    return dist;
//  }

//  /**
//   * Calculate the Hamming distance between the individuals with index i and j
//   * Independently of information measures
//   * @param i index of individual
//   * @param j index of individual
//   * @return Hamming distance
//   * @note if inforMeasure are distint return max Hamming distance
//   */
//  public int calcDistHammingInforMeasureConsidered(ExampleSetProcess E, int i,int j){
//    int numBinaryElements= this.getIndividuals(i).getSizeBinaryBlocs(0);
//    int dist=0;
//    int[] ele1, ele2;
//    double[] inforMeasure1, inforMeasure2;
//    int k=0, numLabels=0, numVar=0;
//    
//    ele1= this.getIndividuals(i).getBinarySubMatrix(0).clone();
//    inforMeasure1= this.getIndividuals(i).getRealSubMatrix(0).clone();
//    ele2= this.getIndividuals(j).getBinarySubMatrix(0).clone();
//    inforMeasure2= this.getIndividuals(j).getRealSubMatrix(0);
//    numVar= E.getProblemDefinition().numAntecedentVariables();
//    
//    // actualizar las matrices binarias con la codificación del individuo en 
//    // función de la medida de información
//    for (int v=0; v < numVar; v++){
//      numLabels=  E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
//      if (inforMeasure1[v] < inforMeasure1[numVar]){
//        for (int l=0; l < numLabels; l++){
//          ele1[k+l]= 1;
//        }
//      }
//      if (inforMeasure2[v] < inforMeasure2[numVar]){
//        for (int l=0; l < numLabels; l++){
//          ele2[k+l]= 1;
//        }
//      }
//      k= k+numLabels;      
//    }
//
//    // realizar la comparación para la distancia de hamming
//    for (k=0; k < numBinaryElements; k++){
//      if (ele1[k] != ele2[k]){
//        dist++;
//      }
//    }
//    return dist;
//  }
 
  /**
   * calc Hamming distance between "i" and "j" individual in value level
   * (no consider information measure)
   * @param i index of individual
   * @param j index of individual
   * @return 
   */
//  public int calcDistHammingInValueLevel(int i, int j, ExampleSetProcess E){
//
//    int numBinaryElements= this.getIndividuals(i).getSizeBinaryBlocs(0);
//    int dist=0;
////    int[] ele1, ele2, ele1Mask, ele2Mask;
//    int[] ele1OR, ele2OR;
//    
////    ele1= this.getIndividuals(i).getBinarySubMatrix(0);
////    ele2= this.getIndividuals(j).getBinarySubMatrix(0);        
////    ele1Mask= this.getIndividuals(i).getBinarySubMatrix(1);
////    ele2Mask= this.getIndividuals(j).getBinarySubMatrix(1);        
//    ele1OR= this.getIndividuals(i).getBinarySubMatrix(2);
//    ele2OR= this.getIndividuals(j).getBinarySubMatrix(2);
//    
//    // realizar la comparación para la distancia de hamming
//    for (int k=0; k < numBinaryElements; k++){
////      ele1OR[k]= Math.max(ele1[k], ele1Mask[k]);
////      ele2OR[k]= Math.max(ele2[k], ele2Mask[k]);
//      if (ele1OR[k] != ele2OR[k]){
//        dist++;
//      }
//    }
//    return dist;
//
//    
////    int numVar= E.getProblemDefinition().numAntecedentVariables();
////    int dist=0;
////    int[] ele1, ele2;
////    int k=0, numLabels=0;
////    double infMeasureClassI= this.getIndividuals(i).getRealMatrix(0, numVar);
////    double infMeasureClassJ= this.getIndividuals(j).getRealMatrix(0, numVar);
////    double actInfMeasureI, actInfMeasureJ;
////    
////    ele1= this.getIndividuals(i).getBinarySubMatrix(0);
////    ele2= this.getIndividuals(j).getBinarySubMatrix(0);        
////    
////    // realizar la comparación para la distancia de hamming
////    k=0;
////    for (int v=0; v < numVar; v++){
////      numLabels=  E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
////      actInfMeasureI= this.getIndividuals(i).getRealMatrix(0, v);
////      actInfMeasureJ= this.getIndividuals(j).getRealMatrix(0, v);
////      if (actInfMeasureI >= infMeasureClassI || actInfMeasureJ >= infMeasureClassJ){
////        for (int l=0; l < numLabels; l++){
////          if (ele1[k+l] != ele2[k+l]){
////            dist++;
////          }
////        }      
////      }
////      k= k+numLabels;
////    }
////
////    return dist;
//    
////    int numBinaryElements= this.getIndividuals(i).getSizeBinaryBlocs(0);
////    int dist=0;
////    int[] ele1, ele2;
////    
////    ele1= this.getIndividuals(i).getBinarySubMatrix(1);
////    ele2= this.getIndividuals(j).getBinarySubMatrix(1);        
////    
////    // realizar la comparación para la distancia de hamming
////    for (int k=0; k < numBinaryElements; k++){
////      if (ele1[k] != ele2[k]){
////        dist++;
////      }
////    }
////    return dist;
//  }
  
 
  /**
   * calc minimal Hamming distance between individual i and the before one
   * consider the value level only with the temporal Pvalue (population in value level)
   * (
   * Calcula la distancia de hamming entre el individuo i y todos los 
   * anteriores de la misma clase, devolviendo el índice del menor
   * )
   * @param i index of individual to consider
   * @return [0]:index of individual with minimal distance (-1 if no there are individual); [1]: minimal distance
   * @note use calcDistHamming which consider individuals with same information measures only
   */
//  public int[] calcMinDistHammingInValueLevel(int i, ExampleSetProcess E){
//    
//    int distMin=this.getIndividuals(i).getBinarySubMatrix(0).length;
//    int indDistMin= -1;
//    int dist=0;
////    int classOfi= P.getIndividuals(i).getIntegerMatrix(0,0);
////    int classOfj;
//    int[] resultado= new int[2];
//   
//    for (int j=0; j < i; j++){
////      classOfj= P.getIndividuals(j).getIntegerMatrix(0,0);
////      if (classOfi == classOfj){
////        dist= P.calcDistHamming(E,i,j);
//        dist= calcDistHammingInValueLevel(i,j, E);
//        if (dist < distMin){
//          distMin= dist;
//          indDistMin= j;
//        }
////      }
//    }
//    resultado[0]= indDistMin;
//    resultado[1]= distMin;
//    return resultado;
//  }
    
  /**
   * modify the value level of individual in function of information measures
   * set 1 value if information measure > informatio measure class in binary level 1
   * @param indiv individual to consider
   * @param E set of examples
   * @return number of relevant variables
   */
  public int modifyValueLevelIndivUsingInformationMeasures(int i, ExampleSetProcess E){

    int[] bin, binValues;
    double[] inforMeasure;
    int k=0, numLabels=0;
    int numVar= E.getProblemDefinition().numAntecedentVariables();
    int relevantVariables=0;

    bin= this.getIndividuals(i).getBinarySubMatrix(0);
    binValues= this.getIndividuals(i).getBinarySubMatrix(1);
    inforMeasure= this.getIndividuals(i).getRealSubMatrix(0);

    // actualizar las matrices binarias con la codificación del individuo en 
    // función de la medida de información
    for (int v=0; v < numVar; v++){
      numLabels=  E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
      if (inforMeasure[v] >= inforMeasure[numVar]){
        relevantVariables++;
        for (int l=0; l < numLabels; l++){
          binValues[k+l]= 1;
        }
      }
      else{
        for (int l=0; l < numLabels; l++){
          binValues[k+l]= 0;
        }
      }
      k= k+numLabels;      
    }

    return relevantVariables;
//    int[] bin, binValues, binORValues;
//    double[] inforMeasure;
//    int k=0, numLabels=0;
//    int numVar= E.getProblemDefinition().numAntecedentVariables();
//
//    bin= this.getIndividuals(i).getBinarySubMatrix(0);
//    binValues= this.getIndividuals(i).getBinarySubMatrix(1);
//    binORValues= this.getIndividuals(i).getBinarySubMatrix(2);
//    inforMeasure= this.getIndividuals(i).getRealSubMatrix(0);
//
//    // actualizar las matrices binarias con la codificación del individuo en 
//    // función de la medida de información
//    for (int v=0; v < numVar; v++){
//      numLabels=  E.getProblemDefinition().getFuzzyLinguisticVariableList(v).getFuzzyLinguisticTermNum();
//      if (inforMeasure[v] < inforMeasure[numVar]){
//        for (int l=0; l < numLabels; l++){
//          binValues[k+l]= 1;
//          binORValues[k+l]= Math.max(bin[k+l],binValues[k+l]);
//        }
//      }
//      else{
//        for (int l=0; l < numLabels; l++){
//          binValues[k+l]= 0;
//          binORValues[k+l]= Math.max(bin[k+l],binValues[k+l]);
//        }
//      }
//      k= k+numLabels;      
//    }
  }
  
  /**
   * modify the value level of population in function of information measures
   * @param E set of examples
   */
  public void modifyValueLevelPopulationUsingInformationMeasures(ExampleSetProcess E){

    int numIndividuals= this.getNumIndividuals();

    for (int i= 0; i < numIndividuals; i++){
      modifyValueLevelIndivUsingInformationMeasures(i,E);
    }    
  }
  
  
  /**
   * get the better class in function of the fitness
   * @param i individual to consider
   * @return index of better class
   */
  public int getBetterClassWithFitness(int i,ExampleSetProcess E){
    double max=0;
    int maxIndex=0;
    
    int numFitness= this.getIndividuals(0).getNumFitness();
    int numClases= E.getProblemDefinition().numLinguisticTermOfConsequent();
    
    double[][] fitness= new double[numClases][numFitness];
    double[] checkClase= new double[numClases];        
    int numCheckClase=numClases;
    
    for (int c=0; c < numClases; c++){
      fitness[c]= this.getIndividuals(i).getRealSubMatrix(2+c);
      checkClase[c]= 1;
    }
    for (int f=0; f < numFitness && numCheckClase > 1; f++){
      max= -(E.getNumExamples());
      maxIndex=-1;
      for (int c=0; c < numClases; c++){
        if (checkClase[c] == 1){
          if (fitness[c][f] > max){
            if (maxIndex != -1){
              checkClase[maxIndex]=0;
              numCheckClase--;
            }
            max= fitness[c][f];
            maxIndex= c;
          }
          else if (fitness[c][f] < max){  // si es == max hay que seguir considerándolo
            checkClase[c]= 0;
            numCheckClase--;
          }            
        }//if (checkClase[c] == 1){
      }//for (int c=0; c < numClases; c++){
    }//for (int f=0; f < numFitness && numCheckClase > 1; f++){

    if (maxIndex == -1){
      return (int) (numClases / 2);
    }
    else{
      return maxIndex;      
    }
  }
  
}
    
    