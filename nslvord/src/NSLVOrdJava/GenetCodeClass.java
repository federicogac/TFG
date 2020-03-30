package NSLVOrdJava;

import java.io.Serializable;

/**
 * @file GenetCodeClass.java
 * @brief define the genet code of individuals of population (will be the rule)
 * fichero para la definición del código genético de los individuos de
 * la población para el algoritmo genético. Coincidirá con la codificación de 
 * una regla del conjunto de reglas que se obtendrá del genético
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement the genet code of population
 */
public class GenetCodeClass implements Serializable {

  private int binaryBlocs;      // indica el número de bloques de elementos binarios (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private int[] sizeBinaryBlocs; // indica el tamaño de cada uno de los bloques de elementos binarios (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private int[][] binaryMatrix; // matriz con los valores de todos los elementos binarios
  private int integerBlocs;     // indica el número de bloques de elementos enteros (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private int[] sizeIntegerBlocs;// indica el tamaño de cada uno de los bloques de elementos enteros (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private int[][] integerMatrix;// matriz con los valores de todos los elementos enteros
  private int realBlocs;     // indica el número de bloques de elementos reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private int[] sizeRealBlocs;// indica el tamaño de cada uno del os bloques de elementos reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[][] realMatrix;// matriz con los valores de todos los elementos reales
  
  private int[] integerRange;   // indica el rango de valores que puede tomar cada bloque de enteros (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] realInfRange;// indica el rango inferior que puede tomar cada bloque de reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  private double[] realSupRange;// indica el rango superior que puede tomar cada bloque de reales (SE ASIGNA EL VALOR EN EL CONSTRUCTOR)
  
  /** Default constructor */
  public GenetCodeClass () { 
      binaryBlocs= -1;
      sizeBinaryBlocs= null;
      binaryMatrix= null;
      integerBlocs= -1;
      sizeIntegerBlocs= null;
      integerMatrix= null;
      realBlocs= -1;
      sizeRealBlocs= null;
      realMatrix= null;
      
      integerRange= null;
      realInfRange= null;
      realSupRange= null;              
  };
    
  /**
   * constructor
   * @param binaryBlocs number of binary blocs (subpopulations)
   * @param integerBlocs number of integet blocs (subpopulations)
   * @param realBlocs number of real blocs (subpopulations)
   * @param sizeBinaryBlocs vector of size of each binary bloc
   * @param sizeIntegerBlocs vector of size of each integer bloc
   * @param sizeRealBlocs vector of size of each real bloc
   * @param integerRange vector of range of each integer bloc
   * @param realInfRange vector of inferior range of each real bloc
   * @param realSupRange vector of superior range of each real bloc
   */
  public GenetCodeClass(int binaryBlocs, int integerBlocs, int realBlocs, 
          int[] sizeBinaryBlocs, int[] sizeIntegerBlocs, int[] sizeRealBlocs,
          int[] integerRange, double[] realInfRange, double[] realSupRange){
      this.binaryBlocs= binaryBlocs;
      this.sizeBinaryBlocs= sizeBinaryBlocs;
      this.binaryMatrix= new int[binaryBlocs][];
      for (int i= 0; i < binaryBlocs; i++){
          this.binaryMatrix[i]= new int[sizeBinaryBlocs[i]];
      }

      this.integerBlocs= integerBlocs;
      this.sizeIntegerBlocs= sizeIntegerBlocs;
      this.integerMatrix= new int[integerBlocs][];
      for (int i= 0; i < integerBlocs; i++){
          this.integerMatrix[i]= new int[sizeIntegerBlocs[i]];
      }
             
      this.realBlocs= realBlocs;
      this.sizeRealBlocs= sizeRealBlocs;
      this.realMatrix= new double[realBlocs][];
      for (int i= 0; i < realBlocs; i++){
          this.realMatrix[i]= new double[sizeRealBlocs[i]];
      }
      
      this.integerRange= integerRange;
      this.realInfRange= realInfRange;
      this.realSupRange= realSupRange;
  }
 
  /** Used for copy constructor
   * @param orig 
   */
    protected GenetCodeClass(GenetCodeClass orig){
      this.binaryBlocs= orig.binaryBlocs;
      this.sizeBinaryBlocs= new int[this.binaryBlocs];
      System.arraycopy(orig.sizeBinaryBlocs, 0, this.sizeBinaryBlocs, 0, orig.sizeBinaryBlocs.length);      
      this.binaryMatrix= new int[this.binaryBlocs][];
      for (int i= 0; i < this.binaryBlocs; i++){
        this.binaryMatrix[i]= new int[this.sizeBinaryBlocs[i]];
        System.arraycopy(orig.binaryMatrix[i], 0, this.binaryMatrix[i], 0, orig.binaryMatrix[i].length);      
      }

      this.integerBlocs= orig.integerBlocs;
      this.sizeIntegerBlocs= new int[this.integerBlocs];
      System.arraycopy(orig.sizeIntegerBlocs, 0, this.sizeIntegerBlocs, 0, orig.sizeIntegerBlocs.length);      
      this.integerMatrix= new int[this.integerBlocs][];
      for (int i= 0; i < this.integerBlocs; i++){
        this.integerMatrix[i]= new int[this.sizeIntegerBlocs[i]];
        System.arraycopy(orig.integerMatrix[i], 0, this.integerMatrix[i], 0, orig.integerMatrix[i].length);      
      }

      this.realBlocs= orig.realBlocs;
      this.sizeRealBlocs= new int[this.realBlocs];
      System.arraycopy(orig.sizeRealBlocs, 0, this.sizeRealBlocs, 0, orig.sizeRealBlocs.length);      
      this.realMatrix= new double[this.realBlocs][];
      for (int i= 0; i < this.realBlocs; i++){
        this.realMatrix[i]= new double[this.sizeRealBlocs[i]];
        System.arraycopy(orig.realMatrix[i], 0, this.realMatrix[i], 0, orig.realMatrix[i].length);      
      }
      
      this.integerRange= orig.integerRange;
      this.realInfRange= orig.realInfRange;
      this.realSupRange= orig.realSupRange;
      
    }
    
    /** copy constructor
     * @return 
     */
    public GenetCodeClass copy(){
      return new GenetCodeClass(this);
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
   * Set the value of sizeBinaryBlocs
   * @return 
   */
/*  public static void setSizeBinaryBlocs ( int[] newVar ) {
    sizeBinaryBlocs = newVar;
  }

  /**
   * Get the value of sizeBinaryBlocs
   * @return the value of sizeBinaryBlocs
   */
  public int[] getSizeBinaryBlocs ( ) {
    return sizeBinaryBlocs;
  }

  /**
   * Set the value of sizeBinaryBlocs
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setSizeBinaryBlocs ( int row, int newVar ) {
    if (row >= 0 && row < binaryBlocs){
        sizeBinaryBlocs[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of sizeBinaryBlocs
   * @param row position of new value
   * @return the value of sizeBinaryBlocs; -1 if no valid possition
   */
  public int getSizeBinaryBlocs (int row ) {
//    if (row >= 0 && row < binaryBlocs){
        return sizeBinaryBlocs[row];
//    }
//    else{
//        return -1;
//    }
  }

  /**
   * Set the value of binaryMatrix
   * @param newVar the new value of binaryMatrix
   */
  public void setBinaryMatrix ( int[][] newVar ) {
    binaryMatrix = newVar;
  }

  /**
   * Get the value of binaryMatrix
   * @return the value of binaryMatrix
   */
  public int[][] getBinaryMatrix ( ) {
    return binaryMatrix;
  }

  /**
   * Set the value of binaryMatrix
   * @param row possition of new value
   * @param col possition of new value
   * @param newVar the new value of binaryMatrix
   * @return -1 if no valid position, row otherwise
   */
  public int setBinaryMatrix ( int row, int col, int newVar ) {
//      if (row >= 0 && row < binaryBlocs &&
//          col >= 0 && col < sizeBinaryBlocs[row]){
//        
          binaryMatrix[row][col] = newVar;
          return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of binaryMatrix
   * @param row possition of new value
   * @param col possition of new value
   * @return the value of binaryMatrix, -1 if no valid position
   */
  public int getBinaryMatrix (int row, int col ) {
//      if (row >= 0 && row < binaryBlocs &&
//          col >= 0 && col < sizeBinaryBlocs[row]){
//
          return binaryMatrix[row][col];
//      }
//      else{
//          return -1;
//      }
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
   * Set the value of sizeIntegerBlocs
   * @return  
   */
/*  public static void setSizeIntegerBlocs ( int[] newVar ) {
    sizeIntegerBlocs = newVar;
  }

  /**
   * Get the value of sizeIntegerBlocs
   * @return the value of sizeIntegerBlocs
   */
  public int[] getSizeIntegerBlocs ( ) {
    return sizeIntegerBlocs;
  }

  /**
   * Set the value of sizeIntegerBlocs
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setSizeIntegerBlocs ( int row, int newVar ) {
    if (row >= 0 && row < integerBlocs){
        sizeIntegerBlocs[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of sizeIntegerBlocs
   * @param row position of new value
   * @return the value of sizeIntegerBlocs; -1 if no valid possition
   */
  public int getSizeIntegerBlocs (int row ) {
//    if (row >= 0 && row < integerBlocs){
        return sizeIntegerBlocs[row];
//    }
//    else{
//        return -1;
//    }
  }
 
  /**
   * Set the value of integerMatrix
   * @param newVar the new value of integerMatrix
   */
  public void setIntegerMatrix ( int[][] newVar ) {
    integerMatrix = newVar;
  }

  /**
   * Get the value of integerMatrix
   * @return the value of integerMatrix
   */
  public int[][] getIntegerMatrix ( ) {
    return integerMatrix;
  }

  /**
   * Set the value of IntegerMatrix
   * @param row possition of new value
   * @param col possition of new value
   * @param newVar the new value of IntegerMatrix
   * @return -1 if no valid position, row otherwise
   */
  public int setIntegerMatrix ( int row, int col, int newVar ) {
//      if (row >= 0 && row < integerBlocs &&
//          col >= 0 && col < sizeIntegerBlocs[row]){
//        
          integerMatrix[row][col] = newVar;
          return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of IntegerMatrix
   * @param row possition of new value
   * @param col possition of new value
   * @return the value of IntegerMatrix, -1 if no valid position
   */
  public int getIntegerMatrix (int row, int col ) {
//      if (row >= 0 && row < integerBlocs &&
//          col >= 0 && col < sizeIntegerBlocs[row]){
//
          return integerMatrix[row][col];
//      }
//      else{
//          return -1;
//      }
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
   * Set the value of sizeRealBlocs
   * @return  
   */
/*  public static void setSizeRealBlocs ( int[] newVar ) {
    sizeRealBlocs = newVar;
  }

  /**
   * Get the value of sizeRealBlocs
   * @return the value of sizeRealBlocs
   */
  public int[] getSizeRealBlocs ( ) {
    return sizeRealBlocs;
  }

  /**
   * Set the value of sizeRealBlocs
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setSizeRealBlocs ( int row, int newVar ) {
    if (row >= 0 && row < realBlocs){
        sizeRealBlocs[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of sizeRealBlocs
   * @param row position of new value
   * @return the value of sizeRealBlocs; -1 if no valid possition
   */
  public int getSizeRealBlocs (int row ) {
//    if (row >= 0 && row < realBlocs){
        return sizeRealBlocs[row];
//    }
//    else{
//        return -1;
//    }
  }
  
  /**
   * Set the value of realMatrix
   * @param newVar the new value of realMatrix
   */
  public void setRealMatrix ( double[][] newVar ) {
    realMatrix = newVar;
  }

  /**
   * Get the value of realMatrix
   * @return the value of realMatrix
   */
  public double[][] getRealMatrix ( ) {
    return realMatrix;
  }

  /**
   * Set the value of RealMatrix
   * @param row possition of new value
   * @param col possition of new value
   * @param newVar the new value of RealMatrix
   * @return -1 if no valid position, row otherwise
   */
  public int setRealMatrix ( int row, int col, double newVar ) {
//      if (row >= 0 && row < realBlocs &&
//          col >= 0 && col < sizeRealBlocs[row]){
//        
          realMatrix[row][col] = newVar;
          return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of RealMatrix
   * @param row possition of new value
   * @param col possition of new value
   * @return the value of RealMatrix, -1 if no valid position
   */
  public double getRealMatrix (int row, int col ) {
//      if (row >= 0 && row < realBlocs &&
//          col >= 0 && col < sizeRealBlocs[row]){
//
          return realMatrix[row][col];
//      }
//      else{
//          return -1;
//      }
  }
  
  /**
   * Set the value of integerRange
   * @return 
   */
/*  public static void setIntegerRange ( int[] newVar ) {
    integerRange = newVar;
  }

  /**
   * Get the value of integerRange
   * @return the value of integerRange
   */
  public int[] getIntegerRange ( ) {
    return integerRange;
  }

  /**
   * Set the value of integerRange
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setIntegerRange ( int row, int newVar ) {
    if (row >= 0 && row < integerBlocs){
        integerRange[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of integerRange
   * @param row position of new value
   * @return the value of integerRange; -1 if no valid possition
   */
  public int getIntegerRange (int row ) {
//    if (row >= 0 && row < integerBlocs){
        return integerRange[row];
//    }
//    else{
//        return -1;
//    }
  }
  
  /**
   * Set the value of RealInfRange
   * @return  
   */
/*  public static void setRealInfRange ( double[] newVar ) {
    realInfRange = newVar;
  }

  /**
   * Get the value of RealInfRange
   * @return the value of RealInfRange
   */
  public double[] getRealInfRange ( ) {
    return realInfRange;
  }

  /**
   * Set the value of RealInfRange
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setRealInfRange ( int row, double newVar ) {
    if (row >= 0 && row < realBlocs){
        realInfRange[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of RealInfRange
   * @param row position of new value
   * @return the value of RealInfRange; -1 if no valid possition
   */
  public double getRealInfRange (int row ) {
//    if (row >= 0 && row < realBlocs){
        return realInfRange[row];
//    }
//    else{
//        return -1;
//    }
  }

  
    /**
   * Set the value of RealSupRange
   * @return 
   */
/*  public static void setRealSupRange ( double[] newVar ) {
    realSupRange = newVar;
  }

  /**
   * Get the value of RealSupRange
   * @return the value of RealSupRange
   */
  public double[] getRealSupRange ( ) {
    return realSupRange;
  }

  /**
   * Set the value of RealSupRange
   * @param row position of new value
   * @return -1 if no valid position, row otherwise
   */
/*  public static int setRealSupRange ( int row, double newVar ) {
    if (row >= 0 && row < realBlocs){
        realSupRange[row] = newVar;
        return row;
    }
    else{
        return -1;
    }
  }

  /**
   * Get the value of RealSupRange
   * @param row position of new value
   * @return the value of RealSupRange; -1 if no valid possition
   */
  public double getRealSupRange (int row ) {
//    if (row >= 0 && row < realBlocs){
        return realSupRange[row];
//    }
//    else{
//        return -1;
//    }
  }
 
/* ......................................................................... */

  /**
   * Get the value of submatrix BinaryMatrix
   * @param row possition of new value
   * @return the value of BinaryMatrix, null if no valid position
   */
  public int[] getBinarySubMatrix (int row) {
//      if (row >= 0 && row < binaryBlocs){
          return binaryMatrix[row];
//      }
//      else{
//          return null;
//      }
  }

  /**
   * Set the value of submatrix BinaryMatrix
   * @param row possition of new value
   * @param newVar new value matrix
   * @return -1 if error
   */
  public int setBinarySubMatrix (int row, int[] newVar) {
//      if (row >= 0 && row < binaryBlocs){
        binaryMatrix[row]= newVar;
        return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of submatrix IntegerMatrix
   * @param row possition of new value
   * @return the value of IntegerMatrix, null if no valid position
   */
  public int[] getIntegerSubMatrix (int row) {
//      if (row >= 0 && row < integerBlocs){
          return integerMatrix[row];
//      }
//      else{
//          return null;
//      }
  }

  /**
   * Set the value of submatrix IntegerMatrix
   * @param row possition of new value
   * @param newVar new value matrix
   * @return -1 if error
   */
  public int setIntegerSubMatrix (int row, int[] newVar) {
//      if (row >= 0 && row < integerBlocs){
        integerMatrix[row]= newVar;
        return row;
//      }
//      else{
//          return -1;
//      }
  }

  /**
   * Get the value of submatrix RealMatrix
   * @param row possition of new value
   * @return the value of RealMatrix, null if no valid position
   */
  public double[] getRealSubMatrix (int row) {
//      if (row >= 0 && row < realBlocs){
          return realMatrix[row];
//      }
//      else{
//          return null;
//      }
  }

  /**
   * Set the value of submatrix RealMatrix
   * @param row possition of new value
   * @param newVar new value matrix
   * @return -1 if error
   */
  public int setRealSubMatrix (int row, double[] newVar) {
//      if (row >= 0 && row < realBlocs){
        realMatrix[row]= newVar;
        return row;
//      }
//      else{
//          return -1;
//      }
  }

        
  /**
   * check value 0 for all elements of binary subpopulation 0 between start to start+size
   * (
   * chequea si todos los valores de la subpoblación 0 (variables) son 0 comenzando en 
   * "start" y contando "tam" elementos
   * )
   * @param start position of begin to check
   * @param size number of position to check
   * @return 1 if all value of binaryMatrix[0][start..star+size] == 0, 0 otherwise
   */
  public int binaryMatrix0AllToZero(int start,int size){
    int index= start;
    for (int i= 0; i < size; i++){
      if (this.getBinaryMatrix(0,index) == 1){
        return 0;
      }
      index++;
    }
    return 1;
  }
  /**
   * check value 1 for all elements of binary subpopulation 0 between start to start+size
   * (
   * chequea si todos los valores de la subpoblación 0 (variables) son 1 comenzando en 
   * "start" y contando "tam" elementos
   * )
   * @param start position of begin to check
   * @param size number of position to check
   * @return 1 if all value of binaryMatrix[0][start..star+size] == 1, 0 otherwise
   */
  public int binaryMatrix0AllToOne(int start,int size){
    int index= start;
    for (int i= 0; i < size; i++){
      if (this.getBinaryMatrix(0,index) == 0){
        return 0;
      }
      index++;
    }
    return 1;
  }
  
  /**
   * check if there are a unique secuence of "1" between start to start+size
   * (
   * chequea si hay 1 y solo 1 secuencia de "1" comenzando en
   * "start" y contando "tam" elementos
   * )
   * @param start position of begin to check
   * @param size number of position to check
   * @return 1 if all value of binaryMatrix[0][start..star+tam] == 1, 0 otherwise
   */
  public int binaryMatrix0ComprensibleZone(int start,int size){
    int index= start;
    int act_unos, sec_unos, valor;

    sec_unos=0;
    valor= this.getBinaryMatrix(0,index);
    if (valor == 1){
      act_unos=1;
      sec_unos=1;
    }  
    else{
      act_unos=0;
    }
    index++;
    for (int i=1; i < size; i++){
      valor= this.getBinaryMatrix(0,index);
      if (act_unos == 0 && valor == 1){
        act_unos=1;
        sec_unos++;
      }
      else{
        if (act_unos == 1 && valor == 0){
          act_unos=0;
        }        
      }
      index++;
    }
    if (sec_unos == 1)
      return 1;
    else
      return 0;  
  }
  
  /**
   * calc the variables that are relevant 
   * (information measure of var > information measure of class 
   * in subpoblación real)
   * @return number of relevant variables
   */
  public int getRelevantVariables(){
    int tamBloc= this.getSizeRealBlocs(0);
    double infMeasureClass= this.getRealMatrix(0, tamBloc-1);
    int relevantVariables=0;
    
    for (int i=0; i < tamBloc-1; i++){
      double actInfMeasure= this.getRealMatrix(0, i);
      if (actInfMeasure >= infMeasureClass){ // la medida de información de la variable es >= que la de la clase
        relevantVariables++;
      }
    }    
    return relevantVariables;
  }

  /**
   * cálc the variables that are relevants y comprensibles (stables) of the genetcode (rule)
   * @param problem 
   * @return number of stable variables of the rule
   */
  public int getStableVariables(FuzzyProblemClass problem){
    int tamBloc= this.getSizeRealBlocs(0);
    double infMeasureClass= this.getRealMatrix(0, tamBloc-1);
    int numLabels, start;
    
    int stableVariables=0;
    start=0;
    for (int i=0; i < tamBloc-1; i++){
      numLabels= problem.getFuzzyLinguisticVariableList(i).getFuzzyLinguisticTermNum();
      double actInfMeasure= this.getRealMatrix(0, i);
      if (actInfMeasure >= infMeasureClass && // la medida de información de la variable es menor que la de la clase
          this.binaryMatrix0ComprensibleZone(start,numLabels) == 1){
            stableVariables++;
      }
      start=start+numLabels;
    }
    return stableVariables;
  }
 
  
  /**
   * Get matriz with 1's between 0's and number of 0's between 1's in binary population
   * @param start position of begin to check
   * @param size number of position to check
   * @return [0] -> positions of 0's between 1's ; [1] -> position of 1's between 0's (the last position is the sumarize)
   */
  public int[][] getNumGaps(int start,int size){
    int values[]= this.getBinarySubMatrix(0);    
    int resultado[][]= new int[2][size+1];
    
    for (int i=0; i < size+1; i++){
      resultado[0][i]= 0;
      resultado[1][i]= 0;      
    }
    
    int index= start+1;
    for (int i=1; i < size-1; i++){
      if (values[index] == 1 && values[index-1] == 0 && values[index+1] == 0){ 
        resultado[0][i]= 1;
        resultado[0][size]++;
      }
      if (values[index] == 0 && values[index-1] == 1 && values[index+1] == 1){ 
        resultado[1][i]= 1;
        resultado[1][size]++;
      }    
      index++;
    }
    return resultado;
  }

  /**
   * modify the binary population with newValue regarding to gaps and strGaps
   * @param start position of begin to check
   * @param size number of position to check
   * @param gaps vector with "1" in gaps to change
   * @param strGaps string that incates if change or not in each position to change
   */
  public void modGapsStr(int start,int size, int[] gaps, String strGaps){
    int values[]= this.getBinarySubMatrix(0);    
    
    int index= start+1;
    int indexStr=0;
    int val=0;
    for (int i=1; i < size-1; i++){
      if (gaps[i] == 1){
        String aux=strGaps.substring(indexStr, indexStr+1);
        val= Integer.parseInt(strGaps.substring(indexStr, indexStr+1));
        if (val == 1){
          values[index]= Math.abs(1 - values[index]);          
        }
        indexStr++;
      }
      index++;
    }
  }

  /**
   * return number of 1's of binary subpopulation 0 between start to start+size
   * (
   * chequea si todos los valores de la subpoblación 0 (variables) son 1 comenzando en 
   * "start" y contando "tam" elementos
   * )
   * @param start position of begin to check
   * @param size number of position to check
   * @return number of 1's if all value of binaryMatrix[0][start..star+size] == 1, 0 otherwise
   */
  public int calcNumberOnes(int start,int size){
    int values[]= this.getBinarySubMatrix(0);    
    int resultado=0;
    int index= start;
    for (int i=0; i < size; i++){
      resultado+= values[index];
      index++;
    }
    return resultado;
  }
  
  /**
   * return number of 0's of binary subpopulation 0 between start to start+size
   * (
   * chequea si todos los valores de la subpoblación 0 (variables) son 1 comenzando en 
   * "start" y contando "tam" elementos
   * )
   * @param start position of begin to check
   * @param size number of position to check
   * @return number of 1's if all value of binaryMatrix[0][start..star+size] == 1, 0 otherwise
   */
  public int calcNumberZeros(int start,int size){
    int values[]= this.getBinarySubMatrix(0);    
    int resultado=0;
    int index= start;
    for (int i=0; i < size; i++){
      resultado+= Math.abs(1 - values[index]);
      index++;
    }
    return resultado;
  }
  
}