package NSLVOrd;

import java.util.Scanner;
import java.io.*;
import java.util.Locale;
import java.util.Random;

/**
 * @file ExampleSetClass.java
 * @brief set of examples to learn
 * @author Juan Carlos Gámez (original de Raúl Pérez)
 * @version 1
 * @date diciembre 2015
 * @note Implement the management of set of examples to learn
 */
public class ExampleSetClass implements Serializable{
    
    protected int numExamples;        // número de ejemplos para el aprendizaje
    protected int numVariables;       // número de variables (conjuntos difusos) que forman el problema
    protected double[][] data;        // matriz[numExamples][numVariables] con los datos
    protected int numPartitions;       // indica el número de particiones del conjunto de ejemplos
    protected int[] partition;        // matriz[numExamples] que indica la partición a la que pertenece el ejemplo

    /** Default constructor */
    public ExampleSetClass () { 
        numExamples= 0;
        numVariables=0;
        data=null;
        numPartitions= 0;
        partition= null;
    };
    
    /** Used for copy constructor
     * @param orig 
     */
    protected ExampleSetClass(ExampleSetClass orig){
      this.numExamples= orig.numExamples;
      this.numVariables= orig.numVariables;
      this.numPartitions= orig.numPartitions;
      
      if (this.numExamples != 0){
        this.data= new double[this.numExamples][this.numVariables];
        this.partition= new int[this.numExamples];

        System.arraycopy(orig.partition, 0, this.partition, 0, orig.partition.length);
        for (int i=0 ; i < this.numExamples; i++){
          System.arraycopy(orig.data[i], 0, this.data[i], 0, orig.data[i].length);
        }
      }
    }
    
    /** copy constructor
     * @return 
     */
    public ExampleSetClass copy(){
      return new ExampleSetClass(this);
    }    

    /**
    * Set the value of numExamples
    * @param newVar the new value of numExamples
    */
    public void setNumExamples ( int newVar ) {
        numExamples = newVar;
    }

    /**
    * Get the value of numExamples
    * @return the value of numExamples
    */
    public int getNumExamples ( ) {
        return numExamples;
    }

    /**
    * Set the value of numVariables
    * @param newVar the new value of numVariables
    */
    public void setNumVariables ( int newVar ) {
        numVariables = newVar;
    }

    /**
    * Get the value of numVariables
    * @return the value of numVariables
    */
    public int getNumVariables ( ) {
        return numVariables;
    }

    /**
    * Set the value of data
    * @param newVar the new value of data
    */
    public void setData ( double[][] newVar ) {
        data = newVar;
    }

    /**
    * Get the value of data
    * @return the value of data
    */
    public double[][] getData ( ) {
        return data;
    }

    /**
    * Set the value of data
    * @param row possition of new value
    * @param col possition of new value
    * @param newVar the new value of data
    * @return -1 if no valid position, row otherwise
    */
    public int setData (int row, int col, double newVar ) {
//        if (row >= 0 && row < numExamples 
//            && col >= 0 && col < numVariables){            
                data[row][col] = newVar;
                return row;
//        }
//        else{
//            return -1;
//        }        
    }

    /**
    * Get the value of data
    * @param row possition of new value
    * @param col possition of new value
    * @return the value of data, -1 if no valid possition
    */
    public double getData (int row, int col){
//        if (row >= 0 && row < numExamples 
//            && col >= 0 && col < numVariables){            
                return data[row][col];
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Set the value of numPartitions
    * @param newVar the new value of numPartitions
    */
    public void setNumPartitions ( int newVar ) {
        numPartitions = newVar;
    }

    /**
    * Get the value of numPartitions
    * @return the value of numPartitions
    */
    public int getNumPartitions ( ) {
        return numPartitions;
    }

    /**
    * Set the value of partition
    * @param newVar the new value of partition
    */
    public void setPartition ( int[] newVar ) {
        partition = newVar;
    }

    /**
    * Get the value of partition
    * @return the value of partition
    */
    public int[] getPartition ( ) {
        return partition;
    }

    /**
    * Set the value of partition
    * @param row position of new value
    * @param newVar the new value of partition
    * @return -1 if no valid position, row otherwise
    */
    public int setPartition (int row, int newVar ) {
//        if (row >= 0 && row < numExamples){
            partition[row] = newVar;
            return row;
//        }
//        else{
//            return -1;
//        }
    }

    /**
    * Get the value of partition
    * @param row position of new value
    * @return the value of partition, -1 if no valid position
    */
    public int getPartition (int row) {
//        if (row >= 0 && row < numExamples){
            return partition[row];
//        }
//        else{
//            return -1;
//        }
    }
    

    /* ......................................................................... */

    /**
    * Get the matrix of values of example
    * @param row possition of new value
    * @return the matrix of value of data, null if no valid possition
    */
    public double[] getExample (int row){
//        if (row >= 0 && row < numExamples){            
                return data[row];
//        }
//        else{
//            return null;
//        }
    }    
}