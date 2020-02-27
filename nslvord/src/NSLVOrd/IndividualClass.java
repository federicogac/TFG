package NSLVOrd;

import java.util.Random;

/**
 * @file IndividuoClass.java
 * @brief define the individuals or population. Extends of GenetCodeClass. 
 * Se utilizará en el algoritmo genético
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement the individual of population
 */
public class IndividualClass extends GenetCodeClass{

  private int numFitness;    // número de fitness a tener en cuenta (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] fitness;  // valoración del individuo
  private int modified;                 // indica si el individuo ha sido modificado o no
  
  
  
  /** Default constructor */
  public IndividualClass () { 
      super();
      numFitness= -1;      
      fitness= null;
  };
  
  /** constructor *
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
   */
  public IndividualClass(int binaryBlocs, int integerBlocs, int realBlocs,
          int[] sizeBinaryBlocs, int[] sizeIntegerBlocs, int[] sizeRealBlocs,
          int[] integerRange, double[] realInfRange, double[] realSupRange, 
          int numFitness){

      super(binaryBlocs, integerBlocs, realBlocs, sizeBinaryBlocs, sizeIntegerBlocs,
              sizeRealBlocs, integerRange, realInfRange, realSupRange);
      
      this.numFitness= numFitness;
      fitness= new double[numFitness];
      this.modified= 1;
  }
  
  /** Used for copy constructor
   * @param orig 
   */
    protected IndividualClass(IndividualClass orig){
      super(orig);
      this.numFitness= orig.numFitness;
      this.modified= orig.modified;
      this.fitness= new double[this.numFitness];
      
      System.arraycopy(orig.fitness, 0, this.fitness, 0, orig.fitness.length);
    }
    
    /** copy constructor
     * @return 
     */
    public IndividualClass copy(){
      return new IndividualClass(this);
    }

    /**
     * get the genet code (inherit) of this individual
     * @return genet code of this individual
     */
    public GenetCodeClass getGenetCodeObject(){
        GenetCodeClass aux= new GenetCodeClass(this);
        
        return aux;
    }
    
  /**
   * Set the value of numFitness
   * @return  
   */
/*  public static void setNumFitness ( int newVar ) {
    numFitness = newVar;
  }

  /**
   * Get the value of fitness
   * @return the value of fitness
   */
  public int getNumFitness ( ) {
    return numFitness;
  }
  
  /**
   * Set the value of fitness
   * @param newVar the new value of fitness
   */
  public void setFitness ( double[] newVar ) {
    fitness = newVar;
    
//    // asignar también el fitness a la matriz de reales
//    for (int i=0; i < newVar.length; i++){
//      this.setRealMatrix(4,i,newVar[i]);
//    }
  }

  /**
   * Get the value of fitness
   * @return the value of fitness
   */
  public double[] getFitness ( ) {
    return fitness;
  }

  /**
   * Set the value of fitness
   * @param row position of new value
   * @param newVar the new value of fitness
   * @return -1 if no valid position, row otherwise
   */
  public int setFitness (int row, double newVar ) {
//        if (row >= 0 && row < numFitness){
            fitness[row] = newVar;
            return row;
//        }
//        else{
//            return -1;
//        }
  }

  /**
   * Get the value of fitness
   * @param row position of new value
   * @return the value of modified; -1 if no valid possition
   */
  public double getFitness (int row ) {
//    if (row >= 0 && row < numFitness){
        return fitness[row];
//    }
//    else{
//        return -1;
//    }
  }  
 
  /**
   * Set the value of modified
   * @param newVar the new value of modified
   */
  public void setModified ( int newVar ) {
    modified = newVar;
  }

  /**
   * Get the value of modified
   * @return the value of modified
   */
  public int getModified ( ) {
    return modified;
  }

  /* ......................................................................... */
  
  /**
   * Compute the stationary uniform mutation of binary subpopulation of individual
   * @param prob probability of the mutation index
   * @param index index of subpoblación to mutate
   * @param randomNum random number
   */
  public int[] stationaryUniformMutationBin(double prob, int index, Random randomNum){
    int tamBloc= this.getSizeBinaryBlocs(index);
    int actualValue, value;
    int[] resultado= new int[tamBloc];
    
    for (int i=0; i < tamBloc-1; i++){
      resultado[i]=0;
      if (Util.Probability(prob,randomNum) == 1){
        resultado[i]=1;
        actualValue= this.getBinaryMatrix(index, i);
        if (actualValue == 1){
          value= 0;
        }
        else{
          value= 1;
        }
        this.setBinaryMatrix(index, i, value);
      }
    }
    this.modified= 1;
    return resultado;
  }  

  /**
   * Compute the stationary uniform mutation of integer subpopulation of individual
   * @param prob probability of the mutation index
   * @param index index of subpoblación to mutate
   * @param randomNum random number
   */
  public int[] stationaryUniformMutationInt(double prob, int index, Random randomNum){
    int rangoIndex= this.getIntegerRange(index);
    int tamBloc= this.getSizeIntegerBlocs(index);
    int actualValue, value, aleat;
    int[] resultado= new int[tamBloc];

    for (int i=0; i < tamBloc; i++){
      resultado[i]=0;
      if (Util.Probability(prob,randomNum) == 1){
        resultado[i]=1;
        aleat= (int) (randomNum.nextDouble()*rangoIndex);
        actualValue= this.getIntegerMatrix(index, i);
        while (aleat == actualValue){
          aleat= (int) (randomNum.nextDouble()*rangoIndex);
        }
        value= aleat;
        this.setIntegerMatrix(index, i, value);
      }      
    }
    this.modified= 1;
    return resultado;
  }   

  /**
   * Compute the stationary uniform mutation of real subpopulation of individual
   * @param prob probability of the mutation index
   * @param index index of subpoblación to mutate
   * @param randomNum random number
   */
  public int[] stationaryUniformMutationReal(double prob, int index, Random randomNum){
    int tamBloc= this.getSizeRealBlocs(index);
    double infMeasureClass= this.getRealMatrix(index, tamBloc-1);
    int[] resultado= new int[tamBloc];
        
    for (int i=0; i < tamBloc-1; i++){
      resultado[i]=0;
      if (Util.Probability(prob,randomNum) == 1){
        resultado[i]=1;
        double actInfMeasure= this.getRealMatrix(index, i);
        if (actInfMeasure < infMeasureClass){
          actInfMeasure= infMeasureClass+0.01;
        }
        else{
          actInfMeasure= infMeasureClass-0.01;
        }
        if (actInfMeasure > 1){
          actInfMeasure= 1;
        }
        else if (actInfMeasure < 0){
          actInfMeasure= 0;
        }
        this.setRealMatrix(index, i, actInfMeasure);
      }
    }
    this.modified= 1;
    return resultado;
  }  


}
